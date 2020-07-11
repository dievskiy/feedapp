/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.R
import com.feedapp.app.data.api.interfaces.RecipeApiSearch
import com.feedapp.app.data.api.models.recipedetailed.nn.RecipeDetailedResponse
import com.feedapp.app.data.api.models.recipesearch.RecipeSearchModel
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.util.StringUtils
import com.feedapp.app.util.TAG
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RecipeSearchRepository
@Inject internal constructor
    (
    val application: Application,
    private val recipeApiSearchResult: RecipeApiSearch
) {

    val isRecipesEmpty = MutableLiveData(false)

    private val stringUtils = StringUtils()

    val searchRecipes = MutableLiveData<RecipeSearchModel>()
    val recipesBreakfast = MutableLiveData<RecipeSearchModel>()
    val recipesLunch = MutableLiveData<RecipeSearchModel>()
    val recipesSnack = MutableLiveData<RecipeSearchModel>()
    val recipesDinner = MutableLiveData<RecipeSearchModel>()
    val isSearching = MutableLiveData(false)

    fun observeRecipesDetailed(): MediatorLiveData<RecipeDetailedResponse> {
        return recipeDetailedMediator
    }


    private val recipeDetailedMediator = MediatorLiveData<RecipeDetailedResponse>()
    val recipeDetailed = MutableLiveData<RecipeDetailedResponse>()

    var intolerance: List<String>? = null
    var diet: List<String>? = null

    private fun setDataToRecipe(mealType: MealType, response: Response<RecipeSearchModel?>) {
        when (mealType) {
            MealType.BREAKFAST -> {
                recipesBreakfast.postValue(response.body())
            }
            MealType.LUNCH -> {
                recipesLunch.postValue(response.body())
            }
            MealType.SNACK -> {
                recipesSnack.postValue(response.body())
            }
            MealType.DINNER -> {
                recipesDinner.postValue(response.body())
            }
        }
    }

    fun searchRecipe(query: String) {
        try {
            isSearching.postValue(true)
            val call = searchRecipes(query, intolerance, diet) ?: return

            call.enqueue(object : Callback<RecipeSearchModel?> {
                override fun onResponse(
                    call: Call<RecipeSearchModel?>,
                    response: Response<RecipeSearchModel?>
                ) {
                    isSearching.postValue(false)
                    if (response.code() == 200) {
                        searchRecipes.postValue(response.body())
                        if (response.body() != null) {
                            if (response.body()!!.results.isNullOrEmpty()) {
                                isRecipesEmpty.postValue(true)
                            } else {
                                isRecipesEmpty.postValue(false)
                            }
                        }
                    } else {
                        throw Exception()
                    }
                }

                override fun onFailure(call: Call<RecipeSearchModel?>, t: Throwable) {
                    t.printStackTrace()
                    isSearching.postValue(false)
                }
            })

        } catch (e: Exception) {
            Log.e(TAG, "Failed accessing API...")
            e.printStackTrace()
            isSearching.postValue(false)
        }
    }

    fun searchDetails(id: Int) {
        try {
            val source = searchDetailedInfo(id = id)?.let {
                LiveDataReactiveStreams.fromPublisher(it.subscribeOn(Schedulers.io())
                    .doOnComplete {
                        isSearching.postValue(false)
                    })
            }
            source?.let { searchModel ->
                recipeDetailedMediator.addSource(searchModel) {
                    recipeDetailedMediator.postValue(it)
                    recipeDetailedMediator.removeSource(source)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed accessing Recipes API...")
            e.printStackTrace()
        }
    }

    private fun searchVP(
        query: String,
        intolerance: List<String>? = null,
        diet: List<String>? = null
    ): Call<RecipeSearchModel?>? {
        val intoleranceReady = if (!intolerance.isNullOrEmpty()) intolerance.toString().replace(
            "(])|(\\[)".toRegex(),
            ""
        ) else ""
        val dietReady = if (!diet.isNullOrEmpty()) diet.toString().replace(
            "(])|(\\[)".toRegex(),
            ""
        ) else ""

        val offset = if (intoleranceReady.isNotEmpty() || dietReady.isNotEmpty()) 0 else 30

        return recipeApiSearchResult.getRecipesVP(
            query = query,
            intolerance = intoleranceReady,
            diet = dietReady,
            offset = offset
        )
    }

    private fun searchRecipes(
        query: String,
        intolerance: List<String>? = null,
        diet: List<String>? = null
    ): Call<RecipeSearchModel?>? {
        val intoleranceReady = if (!intolerance.isNullOrEmpty()) intolerance.toString().replace(
            "(])|(\\[)".toRegex(),
            ""
        ) else ""
        val dietReady = if (!diet.isNullOrEmpty()) diet.toString().replace(
            "(])|(\\[)".toRegex(),
            ""
        ) else ""

        return recipeApiSearchResult.getRecipesSearch(
            query = query,
            intolerance = intoleranceReady,
            diet = dietReady
        )
    }

    private fun searchDetailedInfo(id: Int): Flowable<RecipeDetailedResponse?>? {
        return recipeApiSearchResult.getRecipesDetails(id = id)
    }

    fun checkTitle(title: String): String {
        return stringUtils.getCorrectRecipeTitle(title)
    }

    fun checkCredits(credits: String?, sourceName: String?, sourceUrl: String?): String {
        return if (credits.isNullOrEmpty() && sourceName != null) {
            application.getString(R.string.credits).format(sourceName)
        } else if (!sourceName.isNullOrEmpty() && credits != null) {
            application.getString(R.string.credits).format(credits)
        } else {
            val site = stringUtils.getSiteFromUrl(sourceUrl.toString())
            application.getString(R.string.credits).format(site)
        }

    }

    fun startSearching() {
        isSearching.postValue(true)
    }

    fun updateIntoleranceAndDiet(intolerance: List<String>?, diet: List<String>?) {
        this.intolerance = intolerance
        this.diet = diet
    }

    fun searchRecipeWithType(query: String, mealType: MealType) {
        try {
            val call = searchVP(query, intolerance, diet) ?: return

            call.enqueue(object : Callback<RecipeSearchModel?> {
                override fun onResponse(
                    call: Call<RecipeSearchModel?>,
                    response: Response<RecipeSearchModel?>
                ) {

                    if (response.code() == 200) {
                        setDataToRecipe(mealType, response)
                    } else {
                        Log.i(TAG, "Fail request data. Response code = ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RecipeSearchModel?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Failed accessing API...")
            e.printStackTrace()
        }

    }


}
