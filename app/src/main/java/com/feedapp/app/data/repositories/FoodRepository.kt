/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.data.databases.daos.FoodProductDao
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.models.FoodProductFBWrapper
import com.feedapp.app.util.ENERGY_TO_CALORIES_MULTIPLICATOR
import com.feedapp.app.util.caloriesToEnergy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject

class FoodRepository @Inject constructor(
    val context: Context,
    private val foodProductDao: FoodProductDao
) {

    private val _myProducts = MutableLiveData<ArrayList<FoodProduct>>()

    val myProducts: LiveData<ArrayList<FoodProduct>>
        get() = _myProducts

    fun searchById(id: Int): FoodProduct? {
        return foodProductDao.searchById(id)
    }

    fun searchByName(name: String): List<FoodProduct> {
        return foodProductDao.searchByName(name)
    }

    fun insertProduct(product: FoodProduct) {
        myProducts.value?.apply {
            add(product)
        }?.also {
            foodProductDao.insertProduct(product)
            uploadProductsToFirebase()
        }
    }


    fun getSize(): Int {
        return foodProductDao.getSize()
    }

    fun deleteProduct(product: FoodProduct) = CoroutineScope(IO).launch {
        myProducts.value?.apply {
            remove(product)
        }?.also {
            _myProducts.postValue(it)
            foodProductDao.deleteProduct(product)
            uploadProductsToFirebase()
        }
    }


    fun getLastIndex(): Int {
        return foodProductDao.getSize()
    }

    fun updateProducts() = CoroutineScope(IO).launch {
        _myProducts.postValue(foodProductDao.getCustomProducts() as ArrayList<FoodProduct>)
    }

    fun getCustomProducts() = CoroutineScope(IO).launch {
        _myProducts.postValue(foodProductDao.getCustomProducts() as ArrayList<FoodProduct>)
        checkFirebaseCustomProducts()
    }

    private fun checkFirebaseCustomProducts() {
        try {
            getProductsDocRef()?.get()?.addOnSuccessListener {
                it?.let {
                    val fsList: FoodProductFBWrapper? =
                        it.toObject(FoodProductFBWrapper::class.java)
                    fsList?.let { wrapper ->
                        // compare to current
                        if (!wrapper.list.equals(_myProducts.value)) {
                            // if no local custom products
                            if (_myProducts.value != null && _myProducts.value!!.isEmpty()) {
                                // upload from fb
                                uploadProductsFromFirebase()
                            } else {
                                uploadProductsToFirebase()
                            }
                        }
                    }

                } ?: uploadProductsToFirebase()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getProductsDocRef(): DocumentReference? {
        val userId = FirebaseAuth.getInstance().uid ?: return null
        return Firebase.firestore.document("/users/$userId/customProducts/products")
    }

    private fun uploadProductsFromFirebase() {
        try {
            _myProducts.value?.let {
                getProductsDocRef()?.get()?.addOnSuccessListener {
                    it?.let { doc ->
                        val products = doc.toObject(FoodProductFBWrapper::class.java)?.list
                            ?: return@addOnSuccessListener
                        insertProducts(products)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun insertProducts(products: List<FoodProduct>) = CoroutineScope(IO).launch {
        foodProductDao.insertProducts(products)
        _myProducts.postValue(products as ArrayList<FoodProduct>)
    }

    private fun uploadProductsToFirebase() {
        val userId = FirebaseAuth.getInstance().uid ?: return
        _myProducts.value?.let {
            getProductsDocRef()?.set(
                FoodProductFBWrapper(it)
            )
        }
    }

    suspend fun saveProduct(
        name: String,
        energy: Float,
        proteinsInHundred: Float,
        fatsInHundred: Float,
        carbsInHundred: Float,
        sugar: Float,
        sFats: Float,
        uFats: Float,
        tFats: Float
    ) = coroutineScope {
        val lastId = getLastIndex()
        // create product
        val product = FoodProduct(
            id = lastId + 1,
            name = name,
            energy = energy,
            proteins = proteinsInHundred,
            fats = fatsInHundred,
            carbs = carbsInHundred,
            sugar = sugar,
            sat_fats_g = sFats,
            mono_fats_g = uFats,
            o_poly_fats_g = tFats,
            calories = energy * ENERGY_TO_CALORIES_MULTIPLICATOR
        )
        insertProduct(product)
    }


    fun getCalories(caloriesInOnePortion: Float, hundredMultiplier: Float): Float {
        return (caloriesInOnePortion * hundredMultiplier)
            .toBigDecimal()
            .setScale(2, RoundingMode.UP).toFloat()
    }

    fun getEnergy(caloriesInHundredGrams: Float): Float {
        return caloriesToEnergy(caloriesInHundredGrams)
    }

}