package com.feedapp.app.data.api.models.usdafooddetailed

import com.google.gson.annotations.SerializedName

data class FoodNutrient(
    @SerializedName("amount")
    val amount: Float?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("nutrient")
    val nutrient: Nutrient?,
    val nutrientAnalysisDetails: List<NutrientAnalysisDetail>?,
    val type: String?
)