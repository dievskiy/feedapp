package com.feedapp.app.di.modules.viewModelModules

import androidx.lifecycle.ViewModel
import com.feedapp.app.di.other.ViewModelKey
import com.feedapp.app.viewModels.IntroductionViewModel
import com.feedapp.app.viewModels.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class IntroVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(IntroductionViewModel::class)
    internal abstract fun bindSearchVM(introductionViewModel: IntroductionViewModel): ViewModel

}