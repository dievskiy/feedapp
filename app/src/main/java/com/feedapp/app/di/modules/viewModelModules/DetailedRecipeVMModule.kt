package com.feedapp.app.di.modules.viewModelModules

import androidx.lifecycle.ViewModel
import com.feedapp.app.di.other.ViewModelKey
import com.feedapp.app.viewModels.AddCustomProductViewModel
import com.feedapp.app.viewModels.DetailedRecipeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class DetailedRecipeVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(DetailedRecipeViewModel::class)
    internal abstract fun bindDetailedRecipeViewModel(detailedRecipeViewModel: DetailedRecipeViewModel): ViewModel

}