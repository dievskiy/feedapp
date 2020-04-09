package com.feedapp.app.data.models


enum class MonthEnum(var code: Int = 0) {
    JANUARY(0),
    FEBRUARY(1),
    MARCH(2),
    APRIL(3),
    MAY(4),
    JUNE(5),
    JULE(6),
    AUGUST(7),
    SEPTEMBER(8),
    OCTOBER(9),
    NOVEMBER(10),
    DECEMBER(11);

    override fun toString(): String {
        return when (this) {
            JANUARY -> "January"
            FEBRUARY -> "February"
            MARCH -> "March"
            APRIL -> "April"
            MAY -> "May"
            JUNE -> "June"
            JULE -> "Jule"
            AUGUST -> "August"
            SEPTEMBER -> "September"
            OCTOBER -> "October"
            NOVEMBER -> "November"
            DECEMBER -> "December"
        }
    }
}
