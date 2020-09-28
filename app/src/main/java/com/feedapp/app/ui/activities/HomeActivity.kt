/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.BasicOperationCallback
import com.feedapp.app.data.models.FragmentNavigationType
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.data.models.localdb.LocalDBSAvailable
import com.feedapp.app.data.models.localdb.LocalDBUris
import com.feedapp.app.data.models.prefs.SharedPrefsHelper
import com.feedapp.app.databinding.ActivityHomeBinding
import com.feedapp.app.ui.fragments.home.HomeFragment.Companion.REQUEST_CODE_STATISTICS
import com.feedapp.app.ui.listeners.BottomNavigationItemListener
import com.feedapp.app.ui.viewclasses.TargetViewFactory
import com.feedapp.app.util.showDialog
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.HomeViewModel
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import javax.inject.Inject


class HomeActivity : ClassicActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @Inject
    lateinit var spHelper: SharedPrefsHelper

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
        const val INTENT_EXTRAS_DATE = "Day"
        const val INTENT_EXTRAS_MEAL_TYPE = "MealType"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
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
            checkLocalDatabase()
        }

    }

    /**
     * Checks if database for current locale is available to download and downloads it
     */
    private fun checkLocalDatabase() {
        var code = getCountry()
        val isAvailable =
            LocalDBSAvailable.values().asList()
                .find { code.contains(it.toString(), ignoreCase = true) }
        if (isAvailable != null) {
            code = isAvailable.toString()

            val cacheDirPath = this@HomeActivity.cacheDir?.toString() ?: return
            val filePath = LocalDBUris.getDBPath(cacheDirPath, code)
            val file = File(filePath)

            // if file doesn't exist, ask user to download db from Internet
            if (!file.exists() && spHelper.shouldAskDownloadDB()) {
                showDownloadDialog(
                    DialogInterface.OnClickListener { _, _ ->
                        firebaseAnalytics.logEvent("database_download"){
                            param("isDownloading", "true")
                        }
                        viewModel.downloadLocalDB(filePath, code, object : BasicOperationCallback {
                            override fun onSuccess() {
                                if (spHelper.saveLocalDBDownloaded(code))
                                    CoroutineScope(Main).launch {
                                        toast(getString(R.string.db_local_download_success))
                                    }
                            }

                            override fun onFailure() {
                                CoroutineScope(Main).launch {
                                    toast(getString(R.string.db_local_download_failure))
                                }
                            }

                        })
                    },
                    code,
                    code
                )
            }
        }
    }

    private fun getCountry(): String = Locale.getDefault().toLanguageTag()

    /**
     * show dialog to user if he wants to download database on his local language
     */
    private fun showDownloadDialog(
        listener: DialogInterface.OnClickListener,
        countryDisplayName: String,
        code: String
    ) {
        val view = layoutInflater.inflate(R.layout.dialog_download_db, null)
        val dontAskAgain = view.findViewById<CheckBox>(R.id.checkBox_dont_ask)

        showDialog(
            view,
            getString(R.string.dialog_download_local_db_title),
            getString(R.string.dialog_download_local_db_message, countryDisplayName, code),
            R.string.ok,
            R.string.cancel,
            listener,
            DialogInterface.OnClickListener { _, _ ->
                if (dontAskAgain.isChecked) spHelper.saveDontAskDownloadDB()
            }
        )

        firebaseAnalytics.logEvent("dialog"){
            param("downloadDatabase", "showed")
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
        introIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(introIntent)
    }


    // set navigation View and controller
    private fun setNavView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        val navListener =
            BottomNavigationItemListener(navController) { t -> updateBottomPosition(t) }
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
                        close(false)
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
                        close(false)
                        hide()
                    }
                    binding.fabAddProduct.hide()
                }
                FragmentNavigationType.SETTINGS -> {
                    binding.fabAddMeal.apply {
                        close(false)
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
        intent.putExtra(INTENT_EXTRAS_DATE, viewModel.currentDay.value?.date)
        val code = when (actionItem.id) {
            R.id.fab_meals_breakfast -> MealType.BREAKFAST.code
            R.id.fab_meals_lunch -> MealType.LUNCH.code
            R.id.fab_meals_snack -> MealType.SNACK.code
            R.id.fab_meals_dinner -> MealType.DINNER.code
            else -> MealType.BREAKFAST.code
        }

        intent.putExtra(INTENT_EXTRAS_MEAL_TYPE, code)

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


    private fun updateBottomPosition(type: FragmentNavigationType) {
        viewModel.currentBottomPosition.postValue(type)
    }

}
