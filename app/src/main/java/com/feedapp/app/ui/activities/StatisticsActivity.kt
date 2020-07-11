/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.feedapp.app.R
import com.feedapp.app.databinding.ActivityStatisticsBinding
import com.feedapp.app.ui.adapters.StatisticsPagerAdapter
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.StatisticsViewModel
import javax.inject.Inject

class StatisticsActivity : ClassicActivity() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(StatisticsViewModel::class.java)
    }
    private lateinit var binding: ActivityStatisticsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setUpView()
        setObservers()
    }

    private fun setObservers() {
        viewModel.deleteProduct.observe(this, Observer { event ->
            event.getContentIfNotHandled()
                ?.let {
                    toast(getString(R.string.error_firebase_save))
                }
        })
    }

    private fun setUpView() {
        setUpAppBar()
        setUpTabLayout()
    }


    private fun setUpTabLayout() {
        val viewPager = findViewById<ViewPager>(R.id.pager)
        viewPager.adapter = StatisticsPagerAdapter(
            supportFragmentManager,
            arrayOf(getString(R.string.month), getString(R.string.day))
        )
        binding.tablayout.setupWithViewPager(viewPager)
    }


    private fun setUpAppBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // update day in the HomeActivity if data has been changed
        if (viewModel.dataChanged.value == true) setResult(HomeActivity.RESULT_CODE_UPDATE_DAY)
        super.onBackPressed()
    }


}
