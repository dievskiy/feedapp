/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.repositories.SearchFoodRepository
import com.feedapp.app.data.repositories.UserRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchFoodRepository,
    userRepository: UserRepository
) : ViewModel() {

    val recentProducts = userRepository.recentProducts

    // true if user has searched at least once
    val hasSearched: LiveData<Boolean> = searchRepository.hasSearched

    val hasAdded = MutableLiveData(false)

    private val _searchQuery = MutableLiveData<String>()

    val searchQuery: LiveData<String>
        get() = _searchQuery


    val meals: LiveData<List<FoodProduct>> = searchRepository.meals

    val isSearching: LiveData<Boolean> = searchRepository.isSearching

    fun searchByQuery(query: String) {
        this._searchQuery.postValue(query)
        searchRepository.searchByQuery(query)
    }

    fun generateColors(size: Int?): List<Int> = searchRepository.generateColors(size)

    fun setHasSearched(b: Boolean) = searchRepository.hasSearched.postValue(b)


}