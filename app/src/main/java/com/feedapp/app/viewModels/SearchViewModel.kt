/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedapp.app.data.models.ColorGenerator
import com.feedapp.app.data.models.localdb.IProduct
import com.feedapp.app.data.models.localdb.LocalFoodDelegate
import com.feedapp.app.data.repositories.RecentDelegate
import com.feedapp.app.data.repositories.SearchFoodRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchFoodRepository,
    recentDelegate: RecentDelegate
) : ViewModel() {

    private var localFoodSearch: LocalFoodDelegate<IProduct>? = null

    val recentProducts = recentDelegate.recentProducts

    // true if user has searched at least once
    private val _hasSearched = MutableLiveData<Boolean>(false)
    val hasSearched: LiveData<Boolean> get() = _hasSearched

    val hasAdded = MutableLiveData(false)

    private val _searchQuery = MutableLiveData<String>()

    val searchQuery: LiveData<String>
        get() = _searchQuery

    val meals: MediatorLiveData<List<IProduct>> = MediatorLiveData()

    init {
        meals.addSource(searchRepository.products) {
            meals.value = it
        }
    }

    val isSearching: LiveData<Boolean> get() = _isSearching
    private val _isSearching = MutableLiveData<Boolean>(false)


    fun searchByQuery(query: String) {
        this._searchQuery.postValue(query)
        _isSearching.postValue(true)

        localFoodSearch?.searchByQuery(query)?.invokeOnCompletion {
            _isSearching.postValue(false)
            _hasSearched.postValue(true)
        }
            ?: searchRepository.searchByQuery(query).invokeOnCompletion {
                _isSearching.postValue(false)
                _hasSearched.postValue(true)
            }

    }

    fun setHasSearched(b: Boolean) {
        _hasSearched.postValue(b)
    }

    suspend fun getSearchSuggestions(query: String): List<String> {
        return localFoodSearch?.getSearchSuggestions(query)
            ?: searchRepository.getSearchSuggestions(query)
    }


    fun generateColors(size: Int?): List<Int> {
        if (size == null || size == 0) return mutableListOf()
        return ColorGenerator().generateColor(size)
    }

    fun <T : IProduct> initLocalFoodDelegate(delegate: LocalFoodDelegate<T>) {
        try {
            localFoodSearch = delegate as LocalFoodDelegate<IProduct>
            localFoodSearch?.let {
                meals.removeSource(searchRepository.products)
                meals.addSource(it.products) { products ->
                    meals.value = products
                }
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    suspend fun getByIdLocal(id: Int) = coroutineScope {
        localFoodSearch?.searchById(id)
    }


}