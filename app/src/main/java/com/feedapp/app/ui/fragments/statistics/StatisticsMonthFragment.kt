package com.feedapp.app.ui.fragments.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.databinding.FragmentStatisticsMonthBinding
import com.feedapp.app.viewModels.StatisticsViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.math.roundToInt

class StatisticsMonthFragment : DaggerFragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: StatisticsViewModel
    private lateinit var binding: FragmentStatisticsMonthBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel

        setUpView()
        setUpObservers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(StatisticsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_statistics_month, container, false)
        return binding.root
    }


    private fun setUpObservers() {
        viewModel.barDataSet.observe(viewLifecycleOwner, Observer {
            loadChart()
        })
    }

    private fun setUpView() {
        setUpChart()
        setUpDropdownMenus()
        loadChart()
    }

    private fun setUpNutrientMenu() {
        binding.nutrientDropdown.apply {
            setAdapter(
                ArrayAdapter(
                    activity!!,
                    R.layout.spinner_default,
                    viewModel.nutrientArrayList.value ?: arrayListOf()
                )
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.updateBarDataset(nutrientInt = position)
            }
            // remove white space in the bottom of dropdown menu
            setDropDownBackgroundResource(R.drawable.white_background)
            setText(viewModel.getNutrientDropdownInitialText(), false)


        }
    }

    private fun setUpMonthMenu() {
        binding.monthDropdown.apply {
            setAdapter(
                ArrayAdapter(
                    activity!!,
                    R.layout.spinner_default,
                    viewModel.monthArrayList.value ?: arrayListOf()
                )
            )
            setOnItemClickListener { _, _, position, _ ->
                viewModel.updateBarDataset(monthInt = position)
            }
            // remove white space in the bottom of dropdown menu
            setDropDownBackgroundResource(R.drawable.white_background)
            setText(viewModel.getMonthDropdownInitialText(), false)

        }
    }

    private fun setUpDropdownMenus() {
        setUpMonthMenu()
        setUpNutrientMenu()

    }

    override fun onResume() {
        super.onResume()
        // if day products have been edited, reload chart
        if (viewModel.dataChanged.value == true) {
            viewModel.dataChanged.postValue(false)
            viewModel.updateBarDataset()
        }
    }

    private fun loadChart() {
        val barDataSet = viewModel.barDataSet.value ?: return
        val barData = BarData(barDataSet)
        barData.setValueTextSize(11f)
        barData.setValueFormatter(BarValueFormatter)
        binding.chart.apply {
            data = barData
            animateY(800)
        }
    }

    private object BarValueFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val rounded = value.roundToInt()
            if(rounded == 0) return ""
            return rounded.toString()
        }
    }

    private fun setUpChart() =
        binding.chart.apply {
            xAxis.axisMinimum = 0f
            xAxis.labelCount = 10
            axisLeft.axisMinimum = 0f
            axisRight.setDrawLabels(false)
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawLabels(true)
            xAxis.setDrawGridLines(false)
            description.isEnabled = false
            setDrawBorders(false)
            isAutoScaleMinMaxEnabled = true
            setDrawValueAboveBar(true)
            isScaleXEnabled = false
            isScaleYEnabled = false
        }

}
