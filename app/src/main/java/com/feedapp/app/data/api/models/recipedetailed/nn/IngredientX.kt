/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.api.models.recipedetailed.nn

import com.feedapp.app.util.LB_TO_G_MULTIPLICAND
import com.feedapp.app.util.OZ_TO_G_MULTIPLICAND
import com.feedapp.app.util.round
import kotlin.math.roundToInt

class IngredientX constructor(
    var amount: Float = 0f,
    var name: String = "",
    var nutrients: List<Nutrient> = listOf(),
    var unit: String = ""
) {

    // round to second significant number
    private fun roundRecipeUnitsAmount(amount: Float): Float {
        return if (amount > 50f) {
            val res = amount.toInt()
            return if (res % 10 >= 5) res.div(10).plus(1).times(10).toFloat()
            else res.div(10).times(10).toFloat()
        } else amount
    }

    // used only for displaying nice amount
    fun getRoundedAmount(): String {
        // if amount is like 1.0 or 2.0, we can round it to 1, 2
        amount = roundRecipeUnitsAmount(amount)
        if (amount / amount.toInt().toFloat() == 1f) {
            return amount.roundToInt().toString()
        } else {
            val a = amount.round(1)
            if (a / a.toInt().toFloat() == 1f)
                return a.toInt().toString()
            else return a.toString()
        }
    }

    fun checkAmountConversion() {
        if (unit.contains("oz", true)
            || unit.contains("ounce", true)
            || unit.contains("ounces", true)
        ) {
            unit = "g"
            convertOzToGrams()
        } else if (unit.contains("lbs", true)
            || unit.contains("lb", true)
            || unit.contains("pound", true)
            || unit.contains("pounds", true)
        ) {
            unit = "g"
            convertLbToGrams()
        }
    }

    private fun convertLbToGrams() {
        amount *= LB_TO_G_MULTIPLICAND
    }

    private fun convertOzToGrams() {
        amount *= OZ_TO_G_MULTIPLICAND
    }

}
