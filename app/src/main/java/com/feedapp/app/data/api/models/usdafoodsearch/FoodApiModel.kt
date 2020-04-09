package com.feedapp.app.data.api.models.usdafoodsearch

import com.google.gson.annotations.SerializedName

// model from response by usda
class FoodApiModel(
    val additionalDescriptions: String? = null,
    val allHighlightFields: String? = null,
    val dataType: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("fdcId")
    val fdcId: Int? = null,
    val foodCode: String? = null,
    val publishedDate: String? = null,
    val score: Double? = null
)