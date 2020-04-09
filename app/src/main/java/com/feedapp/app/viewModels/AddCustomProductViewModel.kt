/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedapp.app.data.repositories.FoodRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddCustomProductViewModel @Inject internal constructor(private val foodRepository: FoodRepository) :
    ViewModel() {


    fun exceeds(gramsInOnePortion: Float, nutrient: Float): Boolean =
        gramsInOnePortion < nutrient && gramsInOnePortion != 0f


    fun getMultiplier(gramsInOnePortion: Float): Float = 100 / gramsInOnePortion


    fun getCalories(caloriesInOnePortion: Float, hundredMultiplier: Float) =
        foodRepository.getCalories(caloriesInOnePortion, hundredMultiplier)

    fun getEnergy(caloriesInHundredGrams: Float) = foodRepository.getEnergy(caloriesInHundredGrams)

    fun saveProduct(
        name: String,
        energy: Float,
        proteinsInHundred: Float,
        fatsInHundred: Float,
        carbsInHundred: Float,
        sugar: Float,
        sFats: Float,
        uFats: Float,
        tFats: Float
    ) = viewModelScope.launch(IO) {
        foodRepository.saveProduct(
            name,
            energy,
            proteinsInHundred,
            fatsInHundred,
            carbsInHundred,
            sugar,
            sFats,
            uFats,
            tFats
        )
    }

}
