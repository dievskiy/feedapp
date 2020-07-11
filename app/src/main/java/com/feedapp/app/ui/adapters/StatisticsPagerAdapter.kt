/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.feedapp.app.ui.fragments.statistics.StatisticsDayFragment
import com.feedapp.app.ui.fragments.statistics.StatisticsMonthFragment

class StatisticsPagerAdapter(fm: FragmentManager, private val titles: Array<String>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val fragments: ArrayList<Fragment> =
        arrayListOf(
            StatisticsMonthFragment(),
            StatisticsDayFragment()
        )

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int = fragments.size
}