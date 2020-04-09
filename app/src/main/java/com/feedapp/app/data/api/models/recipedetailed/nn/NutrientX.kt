package com.feedapp.app.data.api.models.recipedetailed.nn

data class NutrientX constructor(
    val amount: Float = 0f,
    val percentOfDailyNeeds: Float = 0f,
    val title: String = "",
    val unit: String = ""
)