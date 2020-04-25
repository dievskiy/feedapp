/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.models.ColorGenerator
import com.feedapp.app.data.models.FoodProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFoodRepository
@Inject constructor(
    private val foodProductDao: FoodProductDao
) {

    val multiplier: LiveData<Double> get() = _multiplier

    private val _multiplier = MutableLiveData(1.0)

    // 0 - grams' position in array of list by default
    val multiplierPosition = MutableLiveData(0)

    val foodInfo: LiveData<FoodProduct>
        get() = _foodInfo

    private val _foodInfo = MutableLiveData<FoodProduct>()

    val meals = MutableLiveData<List<FoodProduct>>()

    val isSearching = MutableLiveData<Boolean>(false)

    val isConnected = MutableLiveData<Boolean>(true)

    val hasSearched = MutableLiveData<Boolean>(false)

    private fun searchFromDB(query: String): List<FoodProduct> {
        return foodProductDao.searchByName(query)
    }


    private fun searchById(id: Int): FoodProduct? {
        return foodProductDao.searchById(id)
    }

    fun searchByQuery(query: String) = CoroutineScope(IO).launch {
        isSearching.postValue(true)
        search(query)
    }

    private fun search(query: String) =
        CoroutineScope(IO).launch {
            // get result from offline DB
            val searchResult = searchFromDB(query)
            hasSearched.postValue(true)
            meals.postValue(searchResult)
            isSearching.postValue(false)
        }

    fun generateColors(size: Int?): List<Int> {
        if (size == null || size == 0) return mutableListOf()
        return ColorGenerator().generateColor(size)
    }


    /**
     * search product in offline DB
     */
    fun searchFoodProduct(id: Int) =
        CoroutineScope(IO).launch {
            val product = searchById(id)
            product?.let {
                _foodInfo.postValue(product)
            }
        }

    fun changeMultiplierValue(d: Double) = _multiplier.postValue(d)


}