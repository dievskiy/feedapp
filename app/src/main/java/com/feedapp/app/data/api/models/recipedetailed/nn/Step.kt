package com.feedapp.app.data.api.models.recipedetailed.nn

data class Step(
    val equipment: List<Equipment> = listOf(),
    val ingredients: List<Ingredient> = listOf(),
    val length: Length = Length(),
    val number: Int = 0,
    val step: String = ""
)