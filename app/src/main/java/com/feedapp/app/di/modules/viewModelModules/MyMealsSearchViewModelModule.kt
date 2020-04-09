package com.feedapp.app.di.modules.viewModelModules

import androidx.lifecycle.ViewModel
import com.feedapp.app.di.other.ViewModelKey
import com.feedapp.app.viewModels.MyMealsSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MyMealsSearchViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MyMealsSearchViewModel::class)
    internal abstract fun bindSearchVM(myMealsSearchViewModel: MyMealsSearchViewModel): ViewModel

}