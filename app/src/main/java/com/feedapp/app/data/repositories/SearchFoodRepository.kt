/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.data.api.interfaces.USDAApiServiceFood
import com.feedapp.app.data.api.models.usdafooddetailed.USDAFoodModel
import com.feedapp.app.data.api.models.usdafoodsearch.SearchMealsResultModel
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.exceptions.NoInternetConnectionException
import com.feedapp.app.data.models.ColorGenerator
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.util.TAG
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchFoodRepository
@Inject constructor(
    private val searchApi: USDAApiServiceFood,
    private val foodProductDao: FoodProductDao
) {

    val foodMediator = MediatorLiveData<USDAFoodModel>()

    val multiplier: LiveData<Double> get() = _multiplier
    private val _multiplier = MutableLiveData(1.0)

    // 0 - grams' position in array of list by default
    val multiplierPosition = MutableLiveData(0)

    val foodInfoOnline = MutableLiveData<USDAFoodModel>()
    val foodInfoOffline = MutableLiveData<FoodProduct>()

    val mealsOnline = MutableLiveData<SearchMealsResultModel>()
    val mealsOffline = MutableLiveData<List<FoodProduct>>()

    val isSearching = MutableLiveData<Boolean>(false)

    val recipeMediator = MediatorLiveData<SearchMealsResultModel>()

    val isConnected = MutableLiveData<Boolean>(true)

    val hasSearched = MutableLiveData<Boolean>(false)

    private fun searchFromOfflineDB(query: String): List<FoodProduct> {
        return foodProductDao.searchByName(query)
    }

    private fun searchByQueryR(query: String)
            : Flowable<SearchMealsResultModel?>? {
        val body = HashMap<String, Any>()
        val searchOptions = HashMap<String, Any>()
        searchOptions["Survey (FNDDS)"] = true
        searchOptions["Foundation"] = true
        searchOptions["Branded"] = false
        body["includeDataTypes"] = searchOptions
        body["generalSearchInput"] = query

        return searchApi.getMealsByQuery(body = body)
    }

    private fun getInfoAboutProductR(id: Int): Flowable<USDAFoodModel?>? {
        return searchApi.getMealsByQuery(id)
    }

    private fun searchOfflineById(id: Int): FoodProduct? {
        return foodProductDao.searchById(id)
    }

    fun searchByQuery(query: String) = CoroutineScope(IO).launch {
        isSearching.postValue(true)
        // if connected to the Internet, search through API, else in offline db
        if (isConnected.value == true) searchOnline(query)
        else searchOffline(query)
    }

    private fun searchOffline(query: String) =
        CoroutineScope(IO).launch {
            // get result from offline DB
            val searchResult = searchFromOfflineDB(query)
            hasSearched.postValue(true)
            mealsOffline.postValue(searchResult)
            isSearching.postValue(false)
        }


    private suspend fun searchOnline(query: String) {
        try {
            val source = LiveDataReactiveStreams.fromPublisher(
                searchByQueryR(query)!!
                    .subscribeOn(Schedulers.io())
                    .doOnComplete {
                        hasSearched.postValue(true)
                        isSearching.postValue(false)
                    }
            )

            withContext(Main) {
                recipeMediator.addSource(source) {
                    recipeMediator.postValue(it)
                    recipeMediator.removeSource(source)
                }
            }
        } catch (e: NoInternetConnectionException) {
            e.printStackTrace()
        } catch (e: Exception) {
            isSearching.postValue(false)
            Log.e(TAG, "Failed accessing API...")
            searchOffline(query)
            e.printStackTrace()
        }
    }

    fun generateColors(size: Int?): List<Int> {
        if (size == null || size == 0) return mutableListOf()
        return ColorGenerator().generateColor(size)
    }

    /**
     * load Detailed information about product from API
     */
    fun getInfoAboutProduct(id: Int) {
        isSearching.postValue(true)
        if (isConnected.value == true) {
            try {
                val source = LiveDataReactiveStreams.fromPublisher(
                    getInfoAboutProductR(id)!!
                        .doOnError { e -> e.printStackTrace() }
                        .subscribeOn(Schedulers.io())
                        .doOnComplete { isSearching.postValue(false) }
                )
                foodMediator.addSource(source) {
                    foodMediator.postValue(it)
                    foodMediator.removeSource(source)
                }
            } catch (e: NoInternetConnectionException) {
                e.printStackTrace()
            } catch (e: Exception) {
                isSearching.postValue(false)
                Log.e(TAG, "Failed accessing API...")
                e.printStackTrace()
            }
        } else {
            val usdaFoodModel = USDAFoodModel()
            foodInfoOnline.postValue(usdaFoodModel)
        }
    }

    /**
     * search product in offline DB
     */
    fun searchFoodProduct(id: Int) =
        CoroutineScope(IO).launch {
            val product = searchOfflineById(id)
            product?.let {
                foodInfoOffline.postValue(product)
            }
        }

    fun changeMultiplierValue(d: Double) = _multiplier.postValue(d)


}