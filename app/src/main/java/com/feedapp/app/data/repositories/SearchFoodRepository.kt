/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.models.FoodProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchFoodRepository
@Inject constructor(
    private val foodProductDao: FoodProductDao
) {

    // 0 - grams' position in array of list by default
    val multiplierPosition = MutableLiveData(0)

    val foodInfo: LiveData<FoodProduct>
        get() = _foodInfo

    private val _foodInfo = MutableLiveData<FoodProduct>()

    val products: LiveData<List<FoodProduct>> get() = _products
    private val _products = MutableLiveData<List<FoodProduct>>()


    fun searchByQuery(query: String) = CoroutineScope(IO).launch {
        search(query.trim())
    }

    private fun search(query: String) {
        // get result from offline DB
        val searchResult = foodProductDao.searchByName(query)
        _products.postValue(searchResult)
    }


    /**
     * search product in offline DB
     */
    fun searchFoodProduct(id: Int) =
        CoroutineScope(IO).launch {
            val product = foodProductDao.searchById(id)
            product?.let {
                _foodInfo.postValue(product)
            }
        }


    /**
     * return list of max 5 suggestions for the query
     */
    suspend fun getSearchSuggestions(q: String): List<String> = coroutineScope {
        val query = q.replace(" ".toRegex(), "")
        var list = foodProductDao.searchBySuggestion(query)
            ?.sortedWith(compareBy { it.length })

        val size = list?.size

        size?.let {
            // if bigger than 5, sublist it
            if (size > 5) {
                list = list?.subList(0, 5)
            }
        }
        list ?: listOf()
    }


}