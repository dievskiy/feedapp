package com.feedapp.app.data.api.models.usdafoodsearch

import com.google.gson.annotations.SerializedName

class SearchMealsResultModel(
    val currentPage: Int? = null,
    val foodSearchCriteria: FoodSearchCriteria? = null,
    @SerializedName("foods")
    val foodApiModels: ArrayList<FoodApiModel>? = null,
    val totalHits: Int? = null,
    val totalPages: Int? = null
)