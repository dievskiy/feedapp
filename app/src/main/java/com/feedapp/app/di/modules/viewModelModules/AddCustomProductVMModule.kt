package com.feedapp.app.di.modules.viewModelModules

import androidx.lifecycle.ViewModel
import com.feedapp.app.di.other.ViewModelKey
import com.feedapp.app.viewModels.AddCustomProductViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AddCustomProductVMModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddCustomProductViewModel::class)
    internal abstract fun bindAddCustomViewModel(addCustomProductViewModel: AddCustomProductViewModel): ViewModel

}