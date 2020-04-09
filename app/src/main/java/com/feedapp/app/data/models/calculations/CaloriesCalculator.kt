/*
 * Copyright (c) 2020 Ruslan Potekhin 
 */

package com.feedapp.app.data.models.calculations

import com.feedapp.app.data.models.user.UserActivityLevel
import com.feedapp.app.data.models.user.UserGoal
import kotlin.math.roundToInt

class CaloriesCalculator {

    companion object {
        const val CALORIES_MINIMUM = 1200
        const val LOSE_WEIGHT_MULTIPLIER = 0.85
        const val GAIN_WEIGHT_MULTIPLIER = 1.15
    }

    /**
     * Calculate calories based on users characteristics
     */
    fun calculateCalories(
        areValuesValid: Boolean,
        weight: Int?,
        height: Int?,
        age: Int?,
        activityLevel: UserActivityLevel?,
        sex: Boolean?,
        goal: UserGoal?
    ): Int {
        var calories: Double
        if (areValuesValid &&
            (weight != null && height != null && activityLevel != null && sex != null && goal != null && age != null)
        ) {
            // use formula for calculus
            val bmr: Double = if (sex) {
                10 * weight + 6.25 * height - 5 * age + 5
            } else {
                10 * weight + 6.25 * height - 5 * age - 161
            }
            calories = bmr * activityLevel.value
            when (goal) {
                UserGoal.LOSE -> {
                    calories *= LOSE_WEIGHT_MULTIPLIER
                }
                UserGoal.GAIN -> {
                    calories *= GAIN_WEIGHT_MULTIPLIER
                }
                else -> { }
            }
        } else {
            return CALORIES_MINIMUM
        }
        return calories.roundToInt()
    }

}