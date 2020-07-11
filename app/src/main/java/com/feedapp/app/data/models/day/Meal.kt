/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models.day

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.feedapp.app.data.databases.converters.Converters
import com.feedapp.app.data.models.Product


/**
 *
 * meal means breakfast, lunch, snack or dinner. It's collection of [Product] with type
 */

@Entity(tableName = "meals")
@TypeConverters(Converters::class)
data class Meal constructor(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val products: ArrayList<Product>,
    val mealType: MealType
) {

    constructor() : this(id = 0, products = arrayListOf<Product>(), mealType = MealType.BREAKFAST)

    override fun toString(): String {
        return "id of the meal = $id | mealType = $mealType"
    }


    fun deleteProduct(product: Product) {
        try {
            products.remove(product)
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    /**
     * Get total calories from all products from meal
     */
    fun getTotalCalories(): Float {
        var total = 0F
        products.forEach { total += it.consumedCalories }
        return total
    }

    // Get total proteins from all products from meal
    fun getTotalProteins(): Float {
        var total = 0F
        products.forEach { total += it.consumedProtein ?: 0f }
        return total
    }

    // Get total fats from all products from meal
    fun getTotalFats(): Float {
        var total = 0F
        products.forEach { total += it.consumedFat ?: 0f }
        return total
    }

    // Get total carbs from all products from meal
    fun getTotalCarbs(): Float {
        var total = 0F
        products.forEach { total += it.consumedCarbs ?: 0f }
        return total
    }

    fun getTotalSugar(): Float {
        var total = 0F
        products.forEach { total += it.consumedSugar ?: 0f }
        return total
    }
}

