package com.feedapp.app.data.repositories

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.models.day.Meal
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.util.getDayDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DayRepositoryTest {


    @Test
    fun testAll() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = com.feedapp.app.data.databases.dbclasses.UserDatabase.invoke(
                InstrumentationRegistry.getInstrumentation().targetContext
            ).getDayDao()
            val repository = DayRepository(dao)
            testInsertAndDeletion(repository)
            testDateChanger(repository)
        }
    }

    private fun testInsertAndDeletion(repository: DayRepository) {
        val date = DayDate()
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
            date = date
        )

        repository.insertDay(day)
        assert(day.date == repository.searchById(1)?.date)
        repository.deleteDay(1)
        assert(0 == repository.getSize())
    }

    private fun testDateChanger(repository: DayRepository) {
        val calendar = Calendar.getInstance()
        val date = Calendar.getInstance().time
        val dayDate = date.getDayDate()

        assert(calendar.get(Calendar.DATE) == dayDate.day.toInt())
        assert(calendar.get(Calendar.MONTH).minus(-1) == dayDate.month.toInt())
        assert(calendar.get(Calendar.YEAR) == dayDate.year.toInt())

        val increasedDay = dayDate.day.toInt() + 1
        val increasedTestDay = repository.getDate(15 + 1) // 15 default
        assert(increasedDay == increasedTestDay.getDayDate().day.toInt())

        val increasedDay2 = dayDate.day.toInt() + 4
        val increasedTestDay2 = repository.getDate(15 + 4) // 15 default
        assert(increasedDay2 == increasedTestDay2.getDayDate().day.toInt())

        val increasedDay3 = dayDate.day.toInt() - 5
        val increasedTestDay3 = repository.getDate(15 - 5) // 15 default
        assert(increasedDay3 == increasedTestDay3.getDayDate().day.toInt())
    }
}