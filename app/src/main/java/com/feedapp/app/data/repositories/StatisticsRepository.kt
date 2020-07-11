/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.feedapp.app.data.databases.daos.DayDao
import com.feedapp.app.data.models.MonthEnum
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.StatisticsNutrientType
import com.feedapp.app.data.models.day.Day
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class StatisticsRepository @Inject internal constructor(
    private val dayDao: DayDao,
    val calendar: Calendar
) {

    val barDataSet: MutableLiveData<BarDataSet> = MutableLiveData()
    val pieDataSet: MutableLiveData<PieDataSet> = MutableLiveData()

    val products: MutableLiveData<ArrayList<Product>> = MutableLiveData(arrayListOf())

    val monthPosition = MutableLiveData(calendar.get(Calendar.MONTH))
    private val currentYear = calendar.get(Calendar.YEAR)
    val nutrient = MutableLiveData(StatisticsNutrientType.CALORIES)

    init {
        setNewBarDataset(StatisticsNutrientType.CALORIES, getDefaultMonth())
    }

    private fun getNutrientTotalFromMonth(
        nutrient: StatisticsNutrientType,
        month: Int
    ): HashMap<Int, Int> {
        val monthString = getMonthStringFromInt(month)
        val days = dayDao.getAllDaysInMonth(monthString) as ArrayList<Day>
        val values = hashMapOf<Int, Int>()
        // add empty entries
        for (day in days) {
            val t = day.getTotalNutrient(nutrient).roundToInt()
            values[day.date.day.toIntOrNull() ?: 1] = t
        }
        return values
    }

    private fun getMonthStringFromInt(month: Int): String {
        val monthString = month.toString()
        val monthInt = month + 1
        return if (monthString.length == 1) "0".plus(monthInt) else monthInt.toString()
    }

    fun updateProducts(day: Day) {
        products.postValue(day.getAllProducts())
    }

    private fun getDaysFromCurrentMonth(monthEnum: MonthEnum): Int {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, monthEnum.code, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun setNewBarDataset(nutrientType: StatisticsNutrientType, monthEnum: MonthEnum) =
        CoroutineScope(Dispatchers.IO).launch {
            nutrient.postValue(nutrientType)
            monthPosition.postValue(monthEnum.code)
            barDataSet.postValue(
                BarDataSet(
                    getBarEntries(nutrientType, monthEnum),
                    nutrientType.name
                )
            )
        }

    fun setNewPieDataset(day: Day) {
        val entriesPie = arrayListOf<PieEntry>()
        val proteins = day.getTotalNutrient(StatisticsNutrientType.PROTEINS)
        val fats = day.getTotalNutrient(StatisticsNutrientType.FATS)
        val carbs = day.getTotalNutrient(StatisticsNutrientType.CARBS)
        // return if empty
        if ((proteins + fats + carbs).roundToInt() == 0) return
        entriesPie.add(PieEntry(proteins, "Proteins"))
        entriesPie.add(PieEntry(fats, "Fats"))
        entriesPie.add(PieEntry(carbs, "Carbs"))
        val pieDataSetV = PieDataSet(entriesPie, "")
        pieDataSetV.valueTextSize = 16f
        pieDataSetV.colors = getPieColors()
        pieDataSet.postValue(pieDataSetV)
    }

    private fun getPieColors(): MutableList<Int> {
        return mutableListOf(
            Color.parseColor("#EC6B56"),
            Color.parseColor("#FFC154"),
            Color.parseColor("#47B39C")
        )
    }


    private fun getBarEntries(
        nutrientType: StatisticsNutrientType,
        monthEnum: MonthEnum
    ): MutableList<BarEntry> {
        val arr = arrayListOf<BarEntry>()
        val daysInMonth = getDaysFromCurrentMonth(monthEnum)
        // add empty entries
        // start from 1 to display columns correctly
        for (i in 1 until daysInMonth + 1) {
            arr.add(BarEntry(i.toFloat(), 0f))
        }
        // fill up with real days
        try {
            val filledDays =
                getNutrientTotalFromMonth(nutrientType, monthEnum.code)
            filledDays.forEach {
                arr[it.key - 1] = BarEntry(it.key.toFloat(), it.value.toFloat())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arr
    }


    private fun getDefaultStatisticsNutrientType(): StatisticsNutrientType {
        return StatisticsNutrientType.values()[nutrient.value?.code
            ?: StatisticsNutrientType.CALORIES.code]
    }

    private fun getDefaultMonth(): MonthEnum {
        return MonthEnum.values()[monthPosition.value ?: MonthEnum.JANUARY.code]
    }

    fun deleteProductFromProducts(product: Product) {
        try {
            val p = products.value
            p?.remove(product)
            products.postValue(p)
            if (products.value.isNullOrEmpty())
                pieDataSet.postValue(PieDataSet(listOf(), ""))
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    fun updateBarDataset(nutrient: Int?, month: Int?) {
        val monthInt = month ?: getDefaultMonth().code
        val nutrientInt = nutrient ?: getDefaultStatisticsNutrientType().code
        val nutrientType = StatisticsNutrientType.values()[nutrientInt]
        val monthType = MonthEnum.values()[monthInt]
        setNewBarDataset(nutrientType, monthType)
    }


}