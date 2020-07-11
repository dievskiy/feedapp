/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedapp.app.R
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.databinding.FragmentStatisticsDayBinding
import com.feedapp.app.ui.activities.HomeActivity
import com.feedapp.app.ui.adapters.StatisticsDayProductsAdapter
import com.feedapp.app.viewModels.StatisticsViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticsDayFragment : DaggerFragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var binding: FragmentStatisticsDayBinding
    private val productsAdapter =
        StatisticsDayProductsAdapter(arrayListOf()) { product -> deleteProduct(product) }
    var dayDate: DayDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dayDate =
            activity?.intent?.getSerializableExtra(HomeActivity.INTENT_EXTRAS_DATE) as DayDate?
        setUpView()
        setUpObservers()

    }


    private fun setUpView() {
        setUpAdapter()
        setUpPieChartView()
        dayDate?.let {
            setDateText(it)
            viewModel.getNewPieData(it)
        }
        CoroutineScope(Default).launch {
            binding.pieChart.postInvalidate()
        }


    }

    private fun setDateText(it: DayDate) {
        binding.date.text = viewModel.getDateToDisplay(it)
    }

    private fun setUpAdapter() {
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = productsAdapter
        }
    }

    private fun setUpPieChartView() {
        binding.pieChart.apply {
            isDrawHoleEnabled = false
            setDrawEntryLabels(false)
            setUsePercentValues(false)
            description.isEnabled = false
            isRotationEnabled = false
        }
    }

    private fun setUpObservers() {
        viewModel.pieDataSet.observe(viewLifecycleOwner, Observer {
            if (!it.values.isNullOrEmpty()) {
                updatePieChart(it)
            } else {
                resetPieChart()
            }
        })
        viewModel.products.observe(viewLifecycleOwner, Observer {
            productsAdapter.updateList(it)

            if (it.isNullOrEmpty()) {
                binding.noProducts.visibility = View.VISIBLE
            } else {
                binding.noProducts.visibility = View.GONE
            }
        })
    }

    private fun resetPieChart() {
        binding.pieChart.data = null
        binding.pieChart.invalidate()
    }

    private fun updatePieChart(pieData: IPieDataSet) {
        binding.pieChart.data = PieData(pieData)
        binding.pieChart.invalidate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(StatisticsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statistics_day, container, false)
        binding.viewmodel = viewModel
        return binding.root
    }

    fun deleteProduct(product: Product) {
        dayDate?.let {
            viewModel.deleteProduct(it, product = product).invokeOnCompletion {
                onDeleteProduct()
            }
        }

    }

    private fun onDeleteProduct() {
        viewModel.getNewPieData(dayDate)
        viewModel.dataChanged.postValue(true)


    }

}
