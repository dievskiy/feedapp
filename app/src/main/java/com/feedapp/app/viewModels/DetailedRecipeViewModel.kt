/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.data.models.calculations.RecipesDetailsCalculator
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.repositories.DayRepository
import com.feedapp.app.data.repositories.RecipeSearchRepository
import com.feedapp.app.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailedRecipeViewModel @Inject constructor(
    private val recipeSearchRepository: RecipeSearchRepository,
    userRepository: UserRepository,
    private val dayRepository: DayRepository,
    private val recipesDetailsCalculator: RecipesDetailsCalculator
) :
    ViewModel() {

    val user: LiveData<User?> = userRepository.user

    val mealTypePosition = MutableLiveData(0)


    val isSearching: LiveData<Boolean> = recipeSearchRepository.isSearching

    val recipeDetailed = recipeSearchRepository.recipeDetailed

    init {
        mealTypePosition.postValue(recipesDetailsCalculator.getMealTypeByCurrentTime().code)
    }

    // load details
    fun searchDetailedInfo(id: Int) = recipeSearchRepository.searchDetails(id)

    fun observeRecipesDetailed() = recipeSearchRepository.observeRecipesDetailed()

    fun checkCredits(credits: String?, sourceName: String?, sourceUrl: String?) =
        recipeSearchRepository.checkCredits(credits, sourceName, sourceUrl)


    fun getDailyPercentage(type: BasicNutrientType): Pair<Int, Int> {
        return user.value?.let {
            val amount = recipeDetailed.value?.nutrition?.getAmountByNutrient(type)
            recipesDetailsCalculator.getDailyPercentage(type, it, amount)
        } ?: Pair(0, 0)
    }

    // save recipe as consumed product
    fun trackRecipe(servings: Int?, mealType: Int?) =
        viewModelScope.launch(IO) {
            recipeDetailed.value?.let {
                dayRepository.saveRecipeToDay(
                    it,
                    servings ?: 1,
                    mealType ?: 0
                )
            }
        }


    fun isServingsCorrect(servings: String): Boolean =
        recipesDetailsCalculator.checkServingsAmount(servings)


    fun getDropDownInitialText() =
        recipesDetailsCalculator.getDropDownInitialText(mealTypePosition.value)

    fun startSearching() {
        recipeSearchRepository.startSearching()
    }


}

