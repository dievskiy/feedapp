package com.feedapp.app.data.api.interfaces

import com.feedapp.app.data.api.models.usdafooddetailed.USDAFoodModel
import com.feedapp.app.data.api.models.usdafoodsearch.SearchMealsResultModel
import com.feedapp.app.util.USDA_API_KEY
import io.reactivex.Flowable
import retrofit2.http.*

interface USDAApiServiceFood {

    @GET("fdc/v1/{id}/")
    fun getMealsByQuery(
        @Path("id", encoded = true) id: Int,
        @Query("api_key") api_key:String = USDA_API_KEY
    ): Flowable<USDAFoodModel?>?

    @Headers("Content-Type: application/json")
    @POST("fdc/v1/search/")
    fun getMealsByQuery(
        @Query("api_key") api_key:String = USDA_API_KEY,
        @Body() body: HashMap<String, Any>
    ): Flowable<SearchMealsResultModel?>?

}