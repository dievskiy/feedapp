package com.feedapp.app.data.api.models.recipesearch

data class RecipeResult(
    var id: Int = 0,
    var image: String? = "",
    var imageUrls: List<String> = listOf(),
    var readyInMinutes: Int = 0,
    var servings: Int = 0,
    var title: String = ""
)