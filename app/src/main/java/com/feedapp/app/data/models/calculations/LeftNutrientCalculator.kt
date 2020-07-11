/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models.calculations

import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.data.models.day.Day
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class LeftNutrientCalculator {

    /**
     * calculate left amount
     */
    fun calculateAmount(
        needed: Int,
        day: Day?,
        type: BasicNutrientType
    ): Pair<Int, Boolean> {
        return calculateNutrientAmount(type, needed, day)
    }

    /**
     * calculate progress based on left amount
     */
    fun calculateProgress(needed: Int, day: Day?, type: BasicNutrientType): Int {
        return calculateNutrientProgress(type, needed, day)
    }

    /**
    * calculate difference of current and needed amount
    *
    * @return isOverConsumed is true if amount is negative
    */
    private fun calculateNutrientAmount(
        type: BasicNutrientType,
        needed: Int,
        day: Day?
    ): Pair<Int, Boolean> {
        var left = 0f
        var isOverConsumed = false
        if (day != null) {
            left = (needed - day.getTotalNutrientBasic(type))
            // if left is negative, calories has exceeded the norm, return exceeded value
            if (left < 0) {
                left = left.absoluteValue
                isOverConsumed = true
            }
        }
        return Pair(left.roundToInt(), isOverConsumed)
    }

    /**
    * calculate difference of current and needed amount in percentage
    * if negative, 100 returned
    */
    private fun calculateNutrientProgress(type: BasicNutrientType, needed: Int?, day: Day?): Int {
        var progress = 0f
        if (needed != null && day != null) {
            progress = (day.getTotalNutrientBasic(type) / needed) * 100
            if (progress >= 100) progress = 100f
        }
        return progress.toInt()
    }


}