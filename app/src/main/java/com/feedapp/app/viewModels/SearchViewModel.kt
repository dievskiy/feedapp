/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedapp.app.data.api.models.usdafoodsearch.SearchMealsResultModel
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.models.RecentProduct
import com.feedapp.app.data.repositories.SearchFoodRepository
import com.feedapp.app.data.repositories.UserRepository
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchFoodRepository,
    userRepository: UserRepository
) : ViewModel() {


    val isConnected: LiveData<Boolean> = searchRepository.isConnected

    // used to avoid multiple toast in one session
    val canShowNoInternetToast = MutableLiveData<Boolean>(true)

    val recentProducts = userRepository.recentProducts

    // true if user has searched at least once
    val hasSearched: LiveData<Boolean> = searchRepository.hasSearched

    val mealsOnline: LiveData<SearchMealsResultModel> = searchRepository.mealsOnline

    val mealsOffline: LiveData<List<FoodProduct>> = searchRepository.mealsOffline

    val isSearching: LiveData<Boolean> = searchRepository.isSearching

    fun searchByQuery(query: String) = searchRepository.searchByQuery(query)

    fun observeRecipe(): MediatorLiveData<SearchMealsResultModel> {
        return searchRepository.recipeMediator
    }

    fun generateColors(size: Int?): List<Int> = searchRepository.generateColors(size)

    /**
     * return 5 last recent products
     */
    fun getRecentSublist(): List<RecentProduct> = recentProducts.value ?: listOf()

    fun setHasSearched(b: Boolean) = searchRepository.hasSearched.postValue(b)

    fun setConnected(b: Boolean) = searchRepository.isConnected.postValue(b)

    fun setMealsOnline(it: SearchMealsResultModel?) {
        searchRepository.mealsOnline.postValue(it)
    }

}