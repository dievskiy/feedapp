package com.feedapp.app.data.api.models.recipesearch

import com.google.gson.annotations.SerializedName

class RecipeSearchModel(
    var baseUri: String? = "",
//    var expires: Long = 0,
//    var isStale: Boolean = false,
    var number: Int = 0,
//    var offset: Int = 0,
//    var processingTimeMs: Int = 0,
    @SerializedName("results")
    var results: List<RecipeResult> = listOf()
//    var totalResults: Int = 0
)

