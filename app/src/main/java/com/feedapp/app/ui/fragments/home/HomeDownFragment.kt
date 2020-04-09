/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.ViewPagerPageListenerCallback
import com.feedapp.app.data.models.ArrowDirection
import com.feedapp.app.databinding.FragmentHomeDownMenuBinding
import com.feedapp.app.ui.adapters.DayPagerAdapter
import com.feedapp.app.ui.listeners.HomeOnPageChangeListener
import com.feedapp.app.ui.viewclasses.ViewPagerTransformer
import com.feedapp.app.util.ANIMATION_DATE_SWITCHER_DURATION
import com.feedapp.app.util.DAY_FRAGMENTS_START_POSITION
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.HomeViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class HomeDownFragment @Inject constructor() : DaggerFragment(),
    ViewPagerPageListenerCallback {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    val fragments: ArrayList<DayFragment> = arrayListOf()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeDownMenuBinding

    // scroll state for viewpager
    override var scrollState = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(HomeViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home_down_menu, container, false)
        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = requireActivity()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setObservers()
        setupAdapter()
        setViews()


    }


    private fun setObservers() {
        viewModel.currentDay.observe(viewLifecycleOwner, Observer {

            binding.fragmentHomeDownDateSwitcher.apply {
                when (viewModel.currentPosition.value) {
                    DAY_FRAGMENTS_START_POSITION -> {
                        setText(getString(R.string.today))
                    }
                    DAY_FRAGMENTS_START_POSITION + 1 -> {
                        setText(getString(R.string.tomorrow))
                    }
                    DAY_FRAGMENTS_START_POSITION - 1 -> {
                        setText(getString(R.string.yesterday))
                    }
                    else -> {
                        setText(viewModel.getDateText())
                    }
                }
            }

        })

        viewModel.isResettingDateOrSwiping.observe(viewLifecycleOwner, Observer {
            binding.homeDownMenuViewpager.isEnabled = !it

        })


    }

    private fun arrowSwipe(arrowDirection: ArrowDirection) {
        val direction = arrowDirection.code
        val currentItem = binding.homeDownMenuViewpager.currentItem
        if (currentItem != 0 && currentItem != 30 && scrollState == 0) {
            viewModel.isResettingDateOrSwiping.value = true
            binding.homeDownMenuViewpager.apply {
                if (this.currentItem != 0) {
                    setCurrentItem(this.currentItem + direction, true)
                }
            }
        }
    }


    private fun setViews() {

        // define animation for textSwitcher for date
        binding.fragmentHomeDownDateSwitcher.setFactory {
            TextView(
                ContextThemeWrapper(
                    activity,
                    R.style.TextInDateSwitcher
                ), null, 0
            )
        }

        val inAnim = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        val outAnim = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)

        inAnim.duration = ANIMATION_DATE_SWITCHER_DURATION
        outAnim.duration = ANIMATION_DATE_SWITCHER_DURATION

        binding.fragmentHomeDownDateSwitcher.inAnimation = inAnim
        binding.fragmentHomeDownDateSwitcher.outAnimation = outAnim

        setUpListeners()

    }

    private fun setUpListeners() {
        // set Listeners for arrows
        binding.fragmentHomeDownArrowLeft.setOnClickListener {
            arrowSwipe(ArrowDirection.LEFT)

        }
        binding.fragmentHomeDownArrowRight.setOnClickListener {
            arrowSwipe(ArrowDirection.RIGHT)
        }

        // set listener for reset day button
        binding.fragmentHomeDownDateButton.setOnClickListener {
            // return if clicking while being on the initial page or viewpager is scrolling
            if (viewModel.currentPosition.value == (DAY_FRAGMENTS_START_POSITION)
                || scrollState != 0
            ) return@setOnClickListener
            viewModel.isResettingDateOrSwiping.value = true
            binding.homeDownMenuViewpager.isEnabled = false
            try {
                viewModel.resetDate()
                binding.homeDownMenuViewpager.currentItem = DAY_FRAGMENTS_START_POSITION
                context?.toast(getString(R.string.toast_initial_day))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }


    private fun setupAdapter() {
        binding.fragmentHomeNsv.isNestedScrollingEnabled = false
        for (i in 0..31) fragments.add(DayFragment())
        val pagerAdapter = DayPagerAdapter(fragments, childFragmentManager)
        binding.homeDownMenuViewpager.apply {
            adapter = pagerAdapter
            setPageTransformer(false, ViewPagerTransformer())
            addOnPageChangeListener(HomeOnPageChangeListener(this@HomeDownFragment))
            offscreenPageLimit = 1
        }

    }

    override fun onResume() {
        super.onResume()

        if (binding.homeDownMenuViewpager.currentItem != viewModel.currentPosition.value) {
            viewModel.currentPosition.value?.let {
                binding.homeDownMenuViewpager.setCurrentItem(
                    it, false
                )
            } ?: viewModel.resetDate()
        }
    }


    override fun updateDayAndDate(position: Int) {
        viewModel.updateDayAndDate(position)
    }

    override var isResettingDateOrSwiping: Boolean
        get() = viewModel.isResettingDateOrSwiping.value ?: false
        set(value) {
            viewModel.isResettingDateOrSwiping.value = value
        }


}