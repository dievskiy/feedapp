/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.listeners

import android.view.MenuItem
import androidx.navigation.NavController
import com.feedapp.app.R
import com.feedapp.app.data.models.FragmentNavigationType
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavigationItemListener(
    private val navController: NavController,
    private val bottomNavigation: ((FragmentNavigationType) -> Unit)
) : BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                navController.navigate(R.id.navigation_home, null)
                bottomNavigation.invoke(FragmentNavigationType.HOME)
                return true
            }
            R.id.navigation_my_products -> {
                navController.navigate(R.id.navigation_my_products, null)
                bottomNavigation.invoke(FragmentNavigationType.PRODUCTS)
                return true
            }
            R.id.navigation_recipes -> {
                navController.navigate(R.id.navigation_recipes, null)
                bottomNavigation.invoke(FragmentNavigationType.RECIPES)
                return true
            }
            R.id.navigation_settings -> {
                navController.navigate(R.id.navigation_settings, null)
                bottomNavigation.invoke(FragmentNavigationType.SETTINGS)
                return true
            }
        }
        return false
    }
}