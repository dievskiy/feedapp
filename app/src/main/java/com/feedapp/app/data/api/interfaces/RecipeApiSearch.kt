package com.feedapp.app.data.api.interfaces

import com.feedapp.app.data.api.models.recipedetailed.nn.RecipeDetailedResponse
import com.feedapp.app.data.api.models.recipesearch.RecipeSearchModel
import com.feedapp.app.util.SPOONACULAR_API_KEY
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.*

interface RecipeApiSearch {

    @GET("recipes/search")
    @Headers("Content-Type: application/json")
    fun getRecipesVP(
        @Query("apiKey") api_key: String = SPOONACULAR_API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 4,
        @Query("offset") offset: Int = 30,
        @Query("intolerances") intolerance: String = "",
        @Query("diet") diet: String = ""
    ): Call<RecipeSearchModel?>

    @GET("recipes/search")
    @Headers("Content-Type: application/json")
    fun getRecipesSearch(
        @Query("apiKey") api_key: String = SPOONACULAR_API_KEY,
        @Query("query") query: String,
        @Query("number") number: Int = 20,
        @Query("intolerances") intolerance: String = "",
        @Query("diet") diet: String = "",
        @Query("instructionsRequired") instructionsRequired: Boolean = true

    ): Call<RecipeSearchModel?>?


    @GET("recipes/{id}/information/")
    @Headers("Content-Type: application/json")
    fun getRecipesDetails(
        @Path("id") id: Int,
        @Query("apiKey") api_key: String = SPOONACULAR_API_KEY,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): Flowable<RecipeDetailedResponse?>?


}
