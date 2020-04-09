package com.feedapp.app.di.modules

import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.di.other.ViewModelProviderFactory
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(viewModelProviderFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}