/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.repositories.FoodRepository
import javax.inject.Inject

class MyMealsViewModel @Inject
internal constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {

    init {
        // get custom products from room db
        foodRepository.getCustomProducts()
    }

    val myProducts: LiveData<ArrayList<FoodProduct>> = foodRepository.myProducts

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


}