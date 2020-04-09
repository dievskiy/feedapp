/* * Copyright (c) 2020 Ruslan Potekhin */package com.feedapp.app.di.modules.viewModelModulesimport androidx.lifecycle.ViewModelimport com.feedapp.app.di.other.ViewModelKeyimport com.feedapp.app.viewModels.HomeViewModelimport com.feedapp.app.viewModels.MyMealsViewModelimport com.feedapp.app.viewModels.RecipeSearchViewModelimport com.feedapp.app.viewModels.SettingsViewModelimport dagger.Bindsimport dagger.Moduleimport dagger.multibindings.IntoMap@Moduleabstract class HomeActivityVMModules {    @Binds    @IntoMap    @ViewModelKey(HomeViewModel::class)    internal abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel    @Binds    @IntoMap    @ViewModelKey(MyMealsViewModel::class)    internal abstract fun bindMyMealsViewModel(myMealsViewModel: MyMealsViewModel): ViewModel    @Binds    @IntoMap    @ViewModelKey(RecipeSearchViewModel::class)    internal abstract fun bindRecipeSearchViewModelModule(recipeSearchViewModel: RecipeSearchViewModel): ViewModel    @Binds    @IntoMap    @ViewModelKey(SettingsViewModel::class)    internal abstract fun bindSettingsVM(settingsViewModel: SettingsViewModel): ViewModel}