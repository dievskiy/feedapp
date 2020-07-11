/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.ui.activities.HomeActivity
import com.feedapp.app.ui.activities.StatisticsActivity
import com.feedapp.app.viewModels.HomeViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import javax.inject.Inject


class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeViewModel

    @Inject
    lateinit var homeUpFragment: HomeUpFragment

    @Inject
    lateinit var downMenuFragment: HomeDownFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInfoListener()
        scrollActivityToTop()
    }

    private fun setInfoListener() {
        fragment_home_info.setOnClickListener {
            startStatisticsActivity()
        }
    }

    private fun startStatisticsActivity() {
        val intent = Intent(activity, StatisticsActivity::class.java)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_DATE, viewModel.currentDay.value?.date)
        activity?.startActivityForResult(
            intent,
            REQUEST_CODE_STATISTICS
        )
    }

    override fun onDestroy() {
        MainScope().cancel()
        super.onDestroy()
    }

    private fun scrollActivityToTop() = try {
        requireActivity().findViewById<NestedScrollView>(R.id.activity_main_nsv)
            .smoothScrollTo(0, 0)
    } catch (e: java.lang.Exception) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!homeUpFragment.isAdded || !downMenuFragment.isAdded) {
            childFragmentManager.beginTransaction()
                .replace(R.id.home_up_menu_container, homeUpFragment)
                .replace(R.id.home_down_menu_container, downMenuFragment)
                .commit()

        }
    }

    companion object {
        const val REQUEST_CODE_STATISTICS = 101
    }


}