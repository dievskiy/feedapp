package com.feedapp.app.data.api.models.recipedetailed.nn

data class ExtendedIngredient constructor(
    val aisle: String = "",
    val amount: Float = 0f,
    val consitency: String = "",
    val id: Int = 0,
    val image: String = "",
    val measures: Measures = Measures(),
    val meta: List<String> = listOf(),
    val metaInformation: List<String> = listOf(),
    val name: String = "",
    val original: String = "",
    val originalName: String = "",
    val originalString: String = "",
    val unit: String = ""
)