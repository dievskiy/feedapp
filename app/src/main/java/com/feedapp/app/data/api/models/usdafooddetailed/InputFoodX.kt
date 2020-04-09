package com.feedapp.app.data.api.models.usdafooddetailed

class InputFoodX(
    val changes: String?,
    val dataType: String?,
    val description: String?,
    val endDate: String?,
    val fdcId: Int?,
    val foodAttributes: List<Any>?,
    val foodClass: String?,
    val foodCode: String?,
    val publicationDate: String?,
    val startDate: String?,
    val tableAliasName: String?,
    val wweiaFoodCategory: WweiaFoodCategory?
)