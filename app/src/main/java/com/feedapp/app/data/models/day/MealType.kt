package com.feedapp.app.data.models.day


enum class MealType constructor(var code: Int = 0) {
    BREAKFAST(0),
    LUNCH(1),
    SNACK(2),
    DINNER(3);

    override fun toString(): String {
        return when (this) {
            BREAKFAST -> {
                "Breakfast"
            }
            LUNCH -> {
                "Lunch"
            }
            SNACK -> {
                "Snack"
            }
            DINNER -> {
                "Dinner"
            }
        }
    }
}
