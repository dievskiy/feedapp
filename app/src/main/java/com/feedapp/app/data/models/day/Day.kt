/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models.day

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.StatisticsNutrientType
import kotlin.math.roundToInt


/**
 * Contains all data about consumed food in specific date
 */

@Entity(tableName = "days")
data class Day constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "dayId")
    var dayId: Int = 0,
    val meals: List<Meal> = listOf(),
    @Embedded
    var date: DayDate,
    var waterNum: Int = 0
) : Cloneable {


    override fun equals(other: Any?): Boolean {
        if (other is Day) {
            return hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return (getTotalCalories().roundToInt().plus(getTotalCarbs().roundToInt())
            .plus(getTotalFats().roundToInt()).plus(waterNum + 121))
            .hashCode() + date.day.hashCode() + date.month.hashCode() + date.year.hashCode()
    }

    constructor() : this(date = DayDate())


    public override fun clone(): Day {
        val clone: Day
        try {
            clone = super.clone() as Day
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
        return clone
    }

    override fun toString(): String {
        return "\n id = $dayId | date = $date MEALS SIZE= ${meals.size}"
    }

    fun removeProduct(productToDelete: Product): Boolean {
        meals.forEach { meal ->
            meal.products.forEach { product ->
                if (productToDelete == product) {
                    meal.deleteProduct(product)
                    return true
                }
            }
        }
        return false
    }

    fun getAllProducts(): ArrayList<Product> {
        val products = arrayListOf<Product>()
        meals.forEach { products.addAll(it.products) }
        return products
    }

    fun getTotalNutrient(nutrientType: StatisticsNutrientType): Float {
        when (nutrientType) {
            StatisticsNutrientType.CALORIES -> {
                return getTotalCalories()
            }
            StatisticsNutrientType.PROTEINS -> {
                return getTotalProteins()
            }
            StatisticsNutrientType.FATS -> {
                return getTotalFats()
            }
            StatisticsNutrientType.CARBS -> {
                return getTotalCarbs()
            }
            StatisticsNutrientType.SUGAR -> {
                return getTotalSugar()
            }
            else -> {
                return 0f
            }
        }
    }

    private fun getTotalSugar(): Float {
        var total = 0f
        this.meals.forEach {
            total += it.getTotalSugar()
        }
        return total
    }

    fun getTotalNutrientBasic(nutrientType: BasicNutrientType): Float {
        when (nutrientType) {
            BasicNutrientType.CALORIES -> {
                return getTotalCalories()
            }
            BasicNutrientType.PROTEINS -> {
                return getTotalProteins()
            }
            BasicNutrientType.FATS -> {
                return getTotalFats()
            }
            BasicNutrientType.CARBS -> {
                return getTotalCarbs()
            }
            else -> {
                return 0f
            }
        }
    }

    private fun getTotalCarbs(): Float {
        var total = 0f
        this.meals.forEach {
            total += it.getTotalCarbs()
        }
        return total
    }

    private fun getTotalFats(): Float {
        var total = 0f
        this.meals.forEach {
            total += it.getTotalFats()
        }
        return total
    }

    private fun getTotalProteins(): Float {
        var total = 0f
        this.meals.forEach {
            total += it.getTotalProteins()
        }
        return total
    }

    private fun getTotalCalories(): Float {
        var total = 0f
        this.meals.forEach {
            total += it.getTotalCalories()
        }
        return total
    }
}


