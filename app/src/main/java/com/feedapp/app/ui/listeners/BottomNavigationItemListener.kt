/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.listeners

import android.view.MenuItem
import androidx.navigation.NavController
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.BottomNavigationValuesUpdate
import com.feedapp.app.data.models.FragmentNavigationType
import com.google.android.material.bottomnavigation.BottomNavigationView


class BottomNavigationItemListener(
    private val navController: NavController,
    private val bottomNavigationItemListener: BottomNavigationValuesUpdate
) : BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> {
                navController.navigate(R.id.navigation_home, null)
                bottomNavigationItemListener.updateBottomPosition(FragmentNavigationType.HOME)
                return true
            }
            R.id.navigation_my_products -> {
                navController.navigate(R.id.navigation_my_products, null)
                bottomNavigationItemListener.updateBottomPosition(FragmentNavigationType.PRODUCTS)
                return true
            }
            R.id.navigation_recipes -> {
                navController.navigate(R.id.navigation_recipes, null)
                bottomNavigationItemListener.updateBottomPosition(FragmentNavigationType.RECIPES)
                return true
            }
            R.id.navigation_settings -> {
                navController.navigate(R.id.navigation_settings, null)
                bottomNavigationItemListener.updateBottomPosition(FragmentNavigationType.SETTINGS)
                return true
            }
        }
        return false
    }
}