package com.feedapp.app.di.modules

import com.feedapp.app.ui.fragments.statistics.StatisticsDayFragment
import com.feedapp.app.ui.fragments.statistics.StatisticsMonthFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class StatisticsFragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeMonthFragment(): StatisticsMonthFragment
    @ContributesAndroidInjector
    abstract fun contributeDayFragment(): StatisticsDayFragment


}