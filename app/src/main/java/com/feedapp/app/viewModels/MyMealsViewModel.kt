/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.repositories.FoodRepository
import javax.inject.Inject

class MyMealsViewModel @Inject
internal constructor(
    private val foodRepository: FoodRepository,
    private val sp: SharedPreferences

) : ViewModel() {

    init {
        // get custom products from room db
        foodRepository.getCustomProducts()
    }

    val myProducts: LiveData<ArrayList<FoodProduct>> = foodRepository.myProducts

    private val _textNoMeals = MutableLiveData<String>().apply {
        value = "There is no products yet"
    }
    val textNoMeals: LiveData<String> = _textNoMeals
    val isTextNoMealsVisible = MutableLiveData(false)
    val isProgressBarVisible = MutableLiveData(true)


    fun deleteCustomProduct(
        foodProduct: FoodProduct
    ) {
        foodRepository.deleteProduct(foodProduct)
    }

    fun refreshCustomProducts() {
        foodRepository.updateProducts()
    }

    fun isProductsEmpty(): Boolean {
        return myProducts.value != null && myProducts.value!!.isEmpty()
    }

    fun isProductsUiGuideShowed(): Boolean {
        return sp.getBoolean("productsUi", false)
    }

    fun saveProductsUiGuideShowed() {
        sp.edit().putBoolean("productsUi", true).apply()
    }

}