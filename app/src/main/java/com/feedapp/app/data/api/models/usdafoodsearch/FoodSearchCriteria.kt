package com.feedapp.app.data.api.models.usdafoodsearch

class FoodSearchCriteria(
    val generalSearchInput: String?,
    val includeDataTypes: IncludeDataTypes?,
    val pageNumber: Int?,
    val requireAllWords: Boolean?
)