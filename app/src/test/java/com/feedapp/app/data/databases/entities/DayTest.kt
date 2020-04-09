package com.feedapp.app.data.databases.entities

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.feedapp.app.data.databases.daos.DayDao
import com.feedapp.app.data.databases.dbclasses.UserDatabase
import com.feedapp.app.data.models.*
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.models.day.Meal
import com.feedapp.app.data.models.day.MealType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DayTest {


    @org.junit.Test
    fun createDayTest() {

        val date = DayDate()
        // create Day
        val meals = listOf(
            Meal(
                products = arrayListOf(),
                mealType = MealType.BREAKFAST
            ),
            Meal(
                products = arrayListOf(),
                mealType = MealType.DINNER
            ),
            Meal(
                products = arrayListOf(),
                mealType = MealType.LUNCH
            )
        )
        val day = Day(
            meals = meals,
            date = date,
            dayId = 0
        )
        assertEquals(meals, day.meals)
        assertEquals(date, day.date)
    }



}