package com.feedapp.app.data.api.models.recipedetailed.nn

import com.google.gson.annotations.SerializedName

data class RecipeDetailedResponse(
    var aggregateLikes: Int = 0,
    @SerializedName("analyzedInstructions")
    var steps: List<AnalyzedInstruction> = listOf(),
    var cheap: Boolean = false,
    var creditsText: String? = "",
    var cuisines: List<String> = listOf(),
    var dairyFree: Boolean = false,
    var diets: List<String> = listOf(),
    var dishTypes: List<String> = listOf(),
    var extendedIngredients: List<ExtendedIngredient> = listOf(),
    var gaps: String = "",
    var glutenFree: Boolean = false,
    var healthScore: Float? = 0f,
    var id: Int = 0,
    var image: String? = "",
    var imageType: String? = "",
    var instructions: String? = "",
    var ketogenic: Boolean = false,
    var lowFodmap: Boolean = false,
    override var nutrition: Nutrition = Nutrition(),
    var occasions: List<String> = listOf(),
    var pricePerServing: Double = 0.0,
    var readyInMinutes: Int = 0,
    var servings: Int = 0,
    var sourceName: String? = "",
    var sourceUrl: String? = "",
    var spoonacularScore: Float = 0f,
    var spoonacularSourceUrl: String = "",
    var sustainable: Boolean = false,
    override var title: String = "",
    var vegan: Boolean = false,
    var vegetarian: Boolean = false,
    var veryHealthy: Boolean = false,
    var veryPopular: Boolean = false,
    var weightWatcherSmartPoints: Float = 0f,
    var whole30: Boolean = false,
    var winePairing: WinePairing = WinePairing()
) : RecipeDetailedResponseI
