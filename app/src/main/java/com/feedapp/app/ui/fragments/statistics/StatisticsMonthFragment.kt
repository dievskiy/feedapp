/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.statistics

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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
        setUpListeners()
        setUpChart()
        setUpDropdownMenus()
        loadChart()
    }

    private fun setUpListeners() {
        binding.btnSavePdf.setOnClickListener {
            showSavePDFDialog()
        }
    }

    private fun showSavePDFDialog() {
        try {
            val dialogView = layoutInflater.inflate(R.layout.dialog_save_pdf, null)
            val imageView = dialogView.findViewById<ImageView>(R.id.dialog_save_pdf_image)
            Glide.with(requireContext()).load("https://i.imgur.com/damZ7lc.jpg").into(imageView)

            AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.statistics))
                .setPositiveButton(R.string.download) { _, _ ->
                    val openSiteIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://feedapp-85670.appspot.com/"))
                    startActivity(openSiteIntent)
                }
                .setNegativeButton(R.string.cancel, null)
                .setView(dialogView)
                .show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpNutrientMenu() {
        try {
            val nutrients = resources.getStringArray(R.array.StatisticsNutrients)
            binding.nutrientDropdown.apply {
                setAdapter(ArrayAdapter(requireActivity(), R.layout.spinner_default, nutrients))
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.updateBarDataset(nutrientInt = position)
                }
                // remove white space in the bottom of dropdown menu
                setDropDownBackgroundResource(R.drawable.white_background)
                setText(nutrients[viewModel.nutrientPosition.value?.code ?: 0], false)
            }
        } catch (e: RuntimeException) {
        }
    }

    private fun setUpMonthMenu() {
        try {
            val months = resources.getStringArray(R.array.Months)
            binding.monthDropdown.apply {
                setAdapter(
                    ArrayAdapter(requireActivity(), R.layout.spinner_default, months)
                )
                setOnItemClickListener { _, _, position, _ ->
                    viewModel.updateBarDataset(monthInt = position)
                }
                // remove white space in the bottom of dropdown menu
                setDropDownBackgroundResource(R.drawable.white_background)
                setText(months[viewModel.monthPosition.value ?: 0], false)
            }
        } catch (e: RuntimeException) {
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
            if (rounded == 0) return ""
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
