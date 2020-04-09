/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.api.models.usdafooddetailed

import com.google.gson.annotations.SerializedName

data class USDAFoodModel(
    val changes: String? = null,
    val dataType: String? = null,
    val description: String? = null,
    val endDate: String? = null,
    val fdcId: Int? = null,
    val foodAttributes: List<FoodAttribute>? = null,
    val foodClass: String? = null,
    val foodCode: String? = null,
    val foodComponents: List<Any>? = null,
    @SerializedName("foodNutrients")
    val foodNutrients: List<FoodNutrient>? = null,
    val foodPortions: List<FoodPortion>? = null,
    val inputFoods: List<InputFood>? = null,
    val publicationDate: String? = null,
    val startDate: String? = null,
    val tableAliasName: String? = null,
    val wweiaFoodCategory: WweiaFoodCategoryX? = null
)