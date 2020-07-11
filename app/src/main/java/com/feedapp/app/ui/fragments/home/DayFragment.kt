/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedapp.app.R
import com.feedapp.app.databinding.FragmentDayBinding
import com.feedapp.app.ui.adapters.DayRecyclerAdapter
import com.feedapp.app.ui.viewclasses.DayRecyclerViewItemDecoration
import com.feedapp.app.ui.viewclasses.WaterContainer
import com.feedapp.app.ui.viewclasses.WaterModifier
import com.feedapp.app.util.VIEW_DAY_RECYCLER_VIEW_SPACE_HEIGHT
import com.feedapp.app.viewModels.HomeViewModel
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject


class DayFragment : DaggerFragment() {

    @Inject
    lateinit var dayRecyclerAdapter: DayRecyclerAdapter

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentDayBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_day, container, false)
        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root

    }


    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManagerToSet = LinearLayoutManager(activity)
        layoutManagerToSet.initialPrefetchItemCount = 4

        binding.fragmentDayRecycler.apply {
            layoutManager = layoutManagerToSet
            addItemDecoration(DayRecyclerViewItemDecoration(VIEW_DAY_RECYCLER_VIEW_SPACE_HEIGHT))
            setHasFixedSize(true)
            adapter = dayRecyclerAdapter
        }

        try {
            // setup water view
            (binding.waterContainer as WaterContainer).waterModifier = object : WaterModifier {
                override fun setWater(newWater: Int) {
                    viewModel.setWater(newWater)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setObservers()

    }

    @InternalCoroutinesApi
    private fun setObservers() {
        viewModel.currentDay.observe(viewLifecycleOwner, Observer {
            kotlinx.coroutines.internal.synchronized(viewModel.isResettingDateOrSwiping.value == false) {
                dayRecyclerAdapter.updateList(it.meals)
                (binding.waterContainer as WaterContainer).fillWaterGlassViews(it.waterNum)
            }
        })
    }


}