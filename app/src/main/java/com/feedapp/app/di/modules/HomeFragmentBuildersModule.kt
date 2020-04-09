/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.di.modules

import com.feedapp.app.ui.fragments.home.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomeFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeUpFragment(): HomeUpFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeDownFragment(): HomeDownFragment

    @ContributesAndroidInjector
    abstract fun contributeDayFragment(): DayFragment

    @ContributesAndroidInjector
    abstract fun contributeMyMealsFragment(): MyMealsFragment

    @ContributesAndroidInjector()
    abstract fun contributeRecipeSearchFragment(): RecipesFragment

    @ContributesAndroidInjector()
    abstract fun contributeSettingsFragment(): Settings

}