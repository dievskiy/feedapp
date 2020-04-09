package com.feedapp.app.data.api.models.usdafooddetailed

import com.google.gson.annotations.SerializedName

class Nutrient(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("number")
    val number: String?,
    @SerializedName("rank")
    val rank: Int?,
    @SerializedName("unitName")
    val unitName: String?
)