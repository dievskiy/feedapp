package com.feedapp.app.data.api.models.recipedetailed.nn

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AbstractIngredientXTest {

    @Test
    fun convertOzToGrams() {
        // round to given significant number
        val b = IngredientX()
        b.amount = 763f
        // to round to 700
        assert("760" == b.getRoundedAmount())
    }
}