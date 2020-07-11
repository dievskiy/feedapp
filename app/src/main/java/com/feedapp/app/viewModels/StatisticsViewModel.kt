/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedapp.app.data.interfaces.BasicOperationCallback
import com.feedapp.app.data.models.Event
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.StatisticsNutrientType
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.repositories.DayRepository
import com.feedapp.app.data.repositories.StatisticsRepository
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieDataSet
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel is shared by StatisticsActivity, StatisticsMonthFragment and StatisticsDayFragment
 */

@SuppressLint("DefaultLocale")
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val dayRepository: DayRepository
) : ViewModel() {


    val barDataSet: LiveData<BarDataSet> = statisticsRepository.barDataSet
    val pieDataSet: LiveData<PieDataSet> = statisticsRepository.pieDataSet
    val products: LiveData<ArrayList<Product>> = statisticsRepository.products

    val monthPosition: LiveData<Int> = statisticsRepository.monthPosition
    val nutrientPosition: LiveData<StatisticsNutrientType> = statisticsRepository.nutrient

    // check if day data has been changed
    val dataChanged = MutableLiveData(false)

    private fun updateProducts(day: Day) = statisticsRepository.updateProducts(day)


    fun getNewPieData(date: DayDate?) =
        viewModelScope.launch(IO) {
            date ?: return@launch
            val day = dayRepository.getDayByDate(date) ?: return@launch
            statisticsRepository.setNewPieDataset(day)
            updateProducts(day)
        }

    fun updateBarDataset(
        nutrientInt: Int? = null,
        monthInt: Int? = null
    ) = statisticsRepository.updateBarDataset(nutrientInt, monthInt)

    private val _deleteProduct = MutableLiveData<Event<String>>()

    val deleteProduct: LiveData<Event<String>>
        get() = _deleteProduct

    private val deleteCallback = object : BasicOperationCallback {
        override fun onSuccess() {
        }

        override fun onFailure() {
            _deleteProduct.value = Event("")
        }
    }

    fun deleteProduct(date: DayDate, product: Product) =
        viewModelScope.launch(IO) {
            // if product has been deleted, delete it from LiveData
            if (dayRepository.deleteProductFromDay(date, product, deleteCallback)) {
                statisticsRepository.deleteProductFromProducts(product)
            }
        }


    fun getDateToDisplay(dayDate: DayDate): CharSequence {
        return "${dayDate.day}/${dayDate.month}"
    }

}