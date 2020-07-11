/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home


import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.feedapp.app.R
import com.feedapp.app.databinding.FragmentHomeUpMenuBinding
import com.feedapp.app.viewModels.HomeViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class HomeUpFragment @Inject constructor() : DaggerFragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: HomeViewModel
    private val ANIMATION_DURATION = 300L
    private lateinit var binding: FragmentHomeUpMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_up_menu, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setObservers()

        return binding.root
    }


    private fun setObservers() {
        // when parameter changes - animate progress bar
        viewModel.userLeftValues.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer

            val caloriesProgress = it.calories.third
            val fatsProgress = it.fatsLeft.third
            val proteinsProgress = it.proteinsLeft.third
            val carbsProgress = it.carbsLeft.third

            ObjectAnimator.ofInt(binding.fragmentHomeUpPBar, "progress", caloriesProgress)
                .setDuration(ANIMATION_DURATION)
                .start()

            ObjectAnimator.ofInt(binding.upMenuProteinsBar, "progress", proteinsProgress)
                .setDuration(ANIMATION_DURATION)
                .start()

            ObjectAnimator.ofInt(binding.upMenuCarbsBar, "progress", carbsProgress)
                .setDuration(ANIMATION_DURATION)
                .start()

            ObjectAnimator.ofInt(binding.upMenuFatsBar, "progress", fatsProgress)
                .setDuration(ANIMATION_DURATION)
                .start()

        })
    }


}
