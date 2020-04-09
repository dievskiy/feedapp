package com.feedapp.app.data.api.models.usdafooddetailed

class NutrientAnalysisDetail(
    val amount: Double?,
    val labMethodDescription: String?,
    val labMethodLink: String?,
    val labMethodOriginalDescription: String?,
    val labMethodTechnique: String?,
    val nutrientAcquisitionDetails: List<NutrientAcquisitionDetail>?,
    val nutrientId: Int?,
    val subSampleId: Int?
)