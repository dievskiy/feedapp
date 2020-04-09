/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedapp.app.data.api.models.recipesearch.RecipeSearchModel
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.data.repositories.RecipeSearchRepository
import com.feedapp.app.data.repositories.UserRepository
import com.feedapp.app.util.USER_RECIPES_SEARCHES_MAX
import com.feedapp.app.util.getDayDate
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class RecipeSearchViewModel @Inject constructor(
    private val calendar: Calendar,
    private val sp: SharedPreferences,
    private val recipeSearchRepository: RecipeSearchRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val isConnected = MutableLiveData(true)
    val isSearching: LiveData<Boolean> = recipeSearchRepository.isSearching

    val hasSearched = MutableLiveData(false)
    val isRecipesEmpty: LiveData<Boolean> = recipeSearchRepository.isRecipesEmpty

    val searchRecipes: LiveData<RecipeSearchModel> = recipeSearchRepository.searchRecipes
    val recipesBreakfast: LiveData<RecipeSearchModel> = recipeSearchRepository.recipesBreakfast
    val recipesLunch: LiveData<RecipeSearchModel> = recipeSearchRepository.recipesLunch
    val recipesSnack: LiveData<RecipeSearchModel> = recipeSearchRepository.recipesSnack
    val recipesDinner: LiveData<RecipeSearchModel> = recipeSearchRepository.recipesDinner


    init {
        updateIntoleranceAndDiet()
    }

    fun updateIntoleranceAndDiet() =
        viewModelScope.launch(IO) {
            val user = userRepository.user.value
            user?.let {
                recipeSearchRepository.updateIntoleranceAndDiet(user.intolerance, user.diet)
            }
        }


    private fun searchRecipeWithType(query: String, mealType: MealType) =
        recipeSearchRepository.searchRecipeWithType(query, mealType)


    /**
     * check if user hasn't searched too much to save API calls
     */
    fun areRecipesLimitReached(): Boolean {
        val date = calendar.time.getDayDate()
        // check if user has searched less than max times
        val searchesInDay = sp.getInt(date.toJson(), 1)
        date.toJson()?.let { incrementSearches(it, searchesInDay) }
        if (searchesInDay <= USER_RECIPES_SEARCHES_MAX) {
            return false
        }
        return true

    }

    private fun incrementSearches(date: String, searchesInDay: Int) {
        sp.edit().putInt(date, searchesInDay + 1).apply()
    }


    fun searchRecipe(query: String) {
        searchStarted()
        recipeSearchRepository.searchRecipe(query)

    }


    fun loadDefaultRecipes() {
        if (isConnected.value == true) {
            viewModelScope.launch(IO) {
                for (type in MealType.values()) searchRecipeWithType(type.toString(), type)
            }
        }
    }

    private fun searchStarted() {
        hasSearched.postValue(true)
    }

    fun isConnected(): Boolean {
       return isConnected.value != null && isConnected.value!!
    }

}

