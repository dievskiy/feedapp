package com.feedapp.app.util

import android.annotation.SuppressLint
import com.feedapp.app.data.models.day.DayDate
import java.text.SimpleDateFormat
import java.util.*

private fun getMonthFromDate(date: String): String {
    return date.substring(5..6)
}

private fun getDayFromDate(date: String): String {
    return date.substring(8..9)
}

@SuppressLint("SimpleDateFormat")
fun Date.getDayDate(): DayDate {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = simpleDateFormat.format(this)
    val month = getMonthFromDate(date = date)
    val day = getDayFromDate(date = date)
    return DayDate(month = month, day = day)
}
