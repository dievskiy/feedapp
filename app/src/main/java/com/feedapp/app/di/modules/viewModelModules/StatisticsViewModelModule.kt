package com.feedapp.app.di.modules.viewModelModules

import androidx.lifecycle.ViewModel
import com.feedapp.app.di.other.ViewModelKey
import com.feedapp.app.viewModels.StatisticsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class StatisticsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(StatisticsViewModel::class)
    internal abstract fun bindStatisticsViewModelModule(statisticsViewModel: StatisticsViewModel): ViewModel

}