/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.di.modules

import com.feedapp.app.di.modules.viewModelModules.*
import com.feedapp.app.ui.activities.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBuilderModule {

    @ContributesAndroidInjector(modules = [AddCustomProductVMModule::class])
    abstract fun contributeAddProductActivity(): AddCustomProductActivity

    @ContributesAndroidInjector(
        modules = [HomeFragmentBuildersModule::class, HomeActivityVMModules::class]
    )
    abstract fun contributeHomeActivity(): HomeActivity

    @ContributesAndroidInjector(modules = [SearchViewModelModule::class, ProductSearchModule::class])
    abstract fun contributeSearchActivity(): SearchActivity

    @ContributesAndroidInjector(modules = [IntroVMModule::class])
    abstract fun contributeIntroActivity(): IntroductionActivity

    @ContributesAndroidInjector(modules = [DetailedViewModelModule::class, ProductSearchModule::class])
    abstract fun contributeDetailedFoodActivity(): DetailedFoodActivity

    @ContributesAndroidInjector(modules = [MyMealsSearchViewModelModule::class])
    abstract fun contributeMyMealsSearchActivity(): MyMealsSearchActivity

    @ContributesAndroidInjector(modules = [DetailedRecipeVMModule::class])
    abstract fun contributeRecipeDetailedActivity(): DetailedRecipeActivity

    @ContributesAndroidInjector(modules = [StatisticsViewModelModule::class, StatisticsFragmentModule::class])
    abstract fun contributeRecipeHistoryActivity(): StatisticsActivity

    @ContributesAndroidInjector()
    abstract fun contributeAboutActivity(): AboutActivity
}
