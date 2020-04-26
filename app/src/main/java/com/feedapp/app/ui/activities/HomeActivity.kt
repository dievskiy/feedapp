/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.BottomNavigationValuesUpdate
import com.feedapp.app.data.models.FragmentNavigationType
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.databinding.ActivityHomeBinding
import com.feedapp.app.ui.fragments.home.HomeFragment.Companion.REQUEST_CODE_STATISTICS
import com.feedapp.app.ui.listeners.BottomNavigationItemListener
import com.feedapp.app.ui.viewclasses.TargetViewFactory
import com.feedapp.app.util.intentDate
import com.feedapp.app.util.intentMealType
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.HomeViewModel
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import javax.inject.Inject


class HomeActivity : ClassicActivity(), BottomNavigationValuesUpdate {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(HomeViewModel::class.java)
    }
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private var doubleBackToExitPressedOnce = false

    companion object {
        const val REQUEST_CODE_ADD_MEAL = 100
        const val RESULT_CODE_SEARCH_IN_RECIPES = 101
        const val RESULT_CODE_UPDATE_DAY = 102
        const val EXTRAS_UPDATE_DAY = "updateDay"
        const val EXTRAS_RECIPES_QUERY = "recipesName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        if (!viewModel.introShowed()) {
            startIntroduction()
        } else {
            // set binding
            binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
            binding.viewmodel = viewModel
            binding.lifecycleOwner = this

            setViewModelObservers()
            setViewListeners()
            setAddMealFab()
            checkHomeUiGuide()
        }

    }

    /**
     * Check if introduction screen for Add button showed
     */
    private fun checkHomeUiGuide() {

        if (!viewModel.isHomeGuideShowed()) {
            TapTargetView.showFor(this,
                TargetViewFactory(this, findViewById(R.id.fab_add_meal)).generateTargetView(),
                object : TapTargetView.Listener() {
                    override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                        view?.isFocusable = false
                        super.onTargetDismissed(view, userInitiated)
                        viewModel.saveHomeUiGuideShowed()
                    }

                    override fun onTargetClick(view: TapTargetView) {
                        // prevent bugs with nestedscrollview's focusability
                        view.isFocusable = false
                        super.onTargetClick(view)
                        viewModel.saveHomeUiGuideShowed()
                    }
                })
        }
    }


    private fun startIntroduction() {
        val introIntent = Intent(this, IntroductionActivity::class.java)
        introIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(introIntent)
    }


    // set navigation View and controller
    private fun setNavView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        val navListener = BottomNavigationItemListener(navController, this)
        navView.apply {
            setupWithNavController(navController)
            setOnNavigationItemSelectedListener(navListener)
            setOnNavigationItemReselectedListener { return@setOnNavigationItemReselectedListener }
        }

    }

    private fun setViewListeners() {
        setNavView()
        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddCustomProductActivity::class.java)
            startActivity(intent)
        }
        binding.activityMainNsv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (binding.fabAddMeal.visibility == View.VISIBLE && binding.fabAddMeal.isOpen)
                binding.fabAddMeal.close()
        }
    }


    private fun setAddMealFab() {
        val labelBgColor = Color.parseColor("#888888")
        val labelColor = Color.WHITE

        val breakfastItem =
            SpeedDialActionItem.Builder(R.id.fab_meals_breakfast, R.drawable.apple)
                .setLabel(R.string.breakfast)
                .setLabelClickable(false)
                .setLabelColor(labelColor)
                .setLabelBackgroundColor(labelBgColor)
                .setFabImageTintColor(Color.WHITE)
                .create()

        val lunchItem =
            SpeedDialActionItem.Builder(R.id.fab_meals_lunch, R.drawable.baguette)
                .setLabel(getString(R.string.lunch))
                .setLabelClickable(false)
                .setLabelBackgroundColor(labelBgColor)
                .setLabelColor(labelColor)
                .setFabImageTintColor(Color.WHITE)
                .create()

        val snackItem =
            SpeedDialActionItem.Builder(R.id.fab_meals_snack, R.drawable.croissant)
                .setLabel(getString(R.string.snack))
                .setLabelClickable(false)
                .setLabelBackgroundColor(labelBgColor)
                .setLabelColor(labelColor)
                .setFabImageTintColor(Color.WHITE)
                .create()

        val dinnerItem =
            SpeedDialActionItem.Builder(R.id.fab_meals_dinner, R.drawable.fish)
                .setLabel(getString(R.string.dinner))
                .setLabelClickable(false)
                .setLabelBackgroundColor(labelBgColor)
                .setLabelColor(labelColor)
                .setFabImageTintColor(Color.WHITE)
                .create()
        try {
            binding.fabAddMeal.apply {
                mainFabClosedIconColor = Color.WHITE
                addActionItem(breakfastItem)
                addActionItem(lunchItem)
                addActionItem(snackItem)
                addActionItem(dinnerItem)
                setOnActionSelectedListener(fabListener)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onBackPressed() {
        if (binding.fabAddMeal.visibility == View.VISIBLE && binding.fabAddMeal.isOpen) {
            binding.fabAddMeal.close()
        }
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        toast(getString(R.string.exit))
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 1500)
    }


    private fun setViewModelObservers() {
        viewModel.dayData.observe(this, Observer {})
        viewModel.userData.observe(this, Observer {})

        viewModel.currentBottomPosition.observe(this, Observer {
            when (it) {
                FragmentNavigationType.PRODUCTS -> {
                    binding.fabAddMeal.apply {
                        close(true)
                        hide()
                    }
                    binding.fabAddProduct.show()

                }
                FragmentNavigationType.HOME -> {
                    binding.fabAddMeal.show()
                    binding.fabAddProduct.hide()
                }
                FragmentNavigationType.RECIPES -> {
                    binding.fabAddMeal.apply {
                        close(true)
                        hide()
                    }
                    binding.fabAddProduct.hide()
                }
                FragmentNavigationType.SETTINGS -> {
                    binding.fabAddMeal.apply {
                        close(true)
                        hide()
                    }
                    binding.fabAddProduct.hide()
                }
                else -> {
                }
            }
        })
    }


    /**
     * Listener for Add Product Fab
     */
    private val fabListener = SpeedDialView.OnActionSelectedListener { actionItem ->
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra(intentDate, viewModel.currentDay.value?.date)
        binding.fabAddMeal.close()
        when (actionItem.id) {
            R.id.fab_meals_breakfast -> {
                intent.putExtra(intentMealType, MealType.BREAKFAST.code)
            }
            R.id.fab_meals_lunch -> {
                intent.putExtra(intentMealType, MealType.LUNCH.code)
            }
            R.id.fab_meals_snack -> {
                intent.putExtra(intentMealType, MealType.SNACK.code)
            }
            R.id.fab_meals_dinner -> {
                intent.putExtra(intentMealType, MealType.DINNER.code)
            }
        }

        startActivityForResult(intent, REQUEST_CODE_ADD_MEAL)

        false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // if user has added products to the day or deleted product in statistics activity ->
            // update day in VM
            REQUEST_CODE_ADD_MEAL, REQUEST_CODE_STATISTICS -> {
                when (resultCode) {
                    RESULT_CODE_UPDATE_DAY -> {
                        viewModel.updateDayAndDate()
                    }
                    RESULT_CODE_SEARCH_IN_RECIPES -> {
                        // if user has added a product and then clicked "search in recipes", update day
                        if (data?.getBooleanExtra(EXTRAS_UPDATE_DAY, false) == true) {
                            viewModel.updateDayAndDate()
                        }

                        // navigate to recipes and search by query
                        val query = data?.getStringExtra(EXTRAS_RECIPES_QUERY)
                        query?.let {
                            val bundle = Bundle()
                            bundle.putString(EXTRAS_RECIPES_QUERY, it)
                            navController.navigate(R.id.navigation_recipes, bundle)
                            binding.fabAddMeal.hide()
                        }
                    }
                }

            }
        }
    }


    override fun updateBottomPosition(type: FragmentNavigationType) {
        viewModel.currentBottomPosition.postValue(type)
    }

}
