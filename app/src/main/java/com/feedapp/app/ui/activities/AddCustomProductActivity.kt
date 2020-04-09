package com.feedapp.app.ui.activities

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.toFloatOrZero
import com.feedapp.app.viewModels.AddCustomProductViewModel
import kotlinx.android.synthetic.main.activity_add_custom_product.*
import javax.inject.Inject

class AddCustomProductActivity @Inject constructor() : ClassicActivity() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private val addViewModel by lazy {
        ViewModelProvider(this, modelFactory).get(AddCustomProductViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_custom_product)
        setStatusBar()
        setFab()
        setEnterListeners()

        setSupportActionBar(activity_add_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        activity_add_toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun setEnterListeners() {
        activity_add_quantity_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_calories_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })

        activity_add_calories_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_title_proteins_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })

        // proteins
        activity_add_title_proteins_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_title_fats_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })
        // carbs
        activity_add_title_fats_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_title_carbs_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })
        // fats
        activity_add_title_carbs_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_title_carbs_edt_text.clearFocus()
                this.hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        // sugar
        activity_add_sugar_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_s_fats_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })
        // s-fats
        activity_add_s_fats_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_u_fats_edt_text.requestFocus()
                return@OnKeyListener true
            }
            false
        })
        // t-fats
        activity_add_t_fats_edt_text.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                activity_add_t_fats_edt_text.clearFocus()
                this.hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

    }

    private fun setFab() {
        try {
            // add white icon to the fab
            activity_add_fab.setOnClickListener(fabListener)
            val myFabSrc = getDrawable(R.drawable.ic_check_24)
            val willBeWhite = myFabSrc?.constantState?.newDrawable()
            willBeWhite?.mutate()?.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            willBeWhite?.let { activity_add_fab.setImageDrawable(it) }
        } catch (e: Exception) {
            activity_add_fab.setOnClickListener(fabListener)
            e.printStackTrace()
        }
    }

    private val fabListener = View.OnClickListener {
        var canSave = true
        val gramsInOnePortion = activity_add_quantity_edt_text.text.toString().toFloatOrZero()
        val caloriesInOnePortion = activity_add_calories_edt_text.text.toString().toFloatOrZero()
        val proteins = activity_add_title_proteins_edt_text.text.toString().toFloatOrZero()
        val fats = activity_add_title_fats_edt_text.text.toString().toFloatOrZero()
        val carbs = activity_add_title_carbs_edt_text.text.toString().toFloatOrZero()
        val calories = activity_add_calories_edt_text.text.toString().toFloatOrZero()
        val grams = activity_add_quantity_edt_text.text.toString().toFloatOrZero()

        // handle calories errors
        if (activity_add_calories_edt_text.text.isEmpty() || calories == 0f) {
            activity_add_calories_edt.isErrorEnabled = true
            activity_add_calories_edt.error = getString(R.string.error_calories)
            canSave = false
        } else {
            activity_add_calories_edt.isErrorEnabled = false
        }
        // handle grams errors
        if (activity_add_quantity_edt_text.text.isEmpty() || grams == 0f) {
            activity_add_quantity_edt.isErrorEnabled = true
            activity_add_quantity_edt.error = getString(R.string.error_grams)
            canSave = false
        } else {
            activity_add_quantity_edt.isErrorEnabled = false
        }

        // handle name errors
        if (activity_add_name_edt.text != null && (activity_add_name_edt.text!!.isEmpty()
                    || activity_add_name_edt.text!!.isBlank())
        ) {
            activity_add_name_edt.error = getString(R.string.error_specify_product_name)
            canSave = false
        }

        // proteins
        if (activity_add_title_proteins_edt_text.text.isEmpty()) {
            activity_add_title_proteins_edt.isErrorEnabled = true
            activity_add_title_proteins_edt.error = getString(R.string.error_specify_proteins)
            canSave = false

        } else if (addViewModel.exceeds(gramsInOnePortion, proteins)) {
            activity_add_title_proteins_edt.isErrorEnabled = true
            activity_add_title_proteins_edt.error =
                "Can't be bigger than ${gramsInOnePortion.toInt()}"
            canSave = false
        } else {
            activity_add_title_proteins_edt.isErrorEnabled = false
        }

        // fats
        if (activity_add_title_fats_edt_text.text.isEmpty()) {
            activity_add_title_fats_edt.isErrorEnabled = true
            activity_add_title_fats_edt.error = getString(R.string.error_specify_fats)
            canSave = false
        } else if (addViewModel.exceeds(gramsInOnePortion, fats)) {
            activity_add_title_fats_edt.isErrorEnabled = true
            activity_add_title_fats_edt.error = "Can't be bigger than ${gramsInOnePortion.toInt()}"
            canSave = false
        } else {
            activity_add_title_fats_edt.isErrorEnabled = false
        }

        // carbs
        if (activity_add_title_carbs_edt_text.text.isEmpty()) {
            activity_add_title_carbs_edt.isErrorEnabled = true
            activity_add_title_carbs_edt.error = getString(R.string.error_specify_carbs)
            canSave = false
        } else if (addViewModel.exceeds(gramsInOnePortion, carbs)) {
            activity_add_title_carbs_edt.isErrorEnabled = true
            activity_add_title_carbs_edt.error = "Can't be bigger than ${gramsInOnePortion.toInt()}"
            canSave = false
        } else {
            activity_add_title_carbs_edt.isErrorEnabled = false
        }

        if (canSave) {
            val hundredMultiplier = addViewModel.getMultiplier(gramsInOnePortion)
            val caloriesInHundredGrams =
                addViewModel.getCalories(caloriesInOnePortion, hundredMultiplier)
            val energy = addViewModel.getEnergy(caloriesInHundredGrams)
            val name = activity_add_name_edt.text.toString()
            val sugar = if (!activity_add_sugar_edt_text.text.isNullOrEmpty())
                activity_add_sugar_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f
            // get nutrients in 100 grams
            val sFats = if (!activity_add_s_fats_edt_text.text.isNullOrEmpty())
                activity_add_s_fats_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f
            val uFats = if (!activity_add_u_fats_edt_text.text.isNullOrEmpty())
                activity_add_u_fats_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f
            val tFats = if (!activity_add_title_fats_edt_text.text.isNullOrEmpty())
                activity_add_title_fats_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f

            val proteinsInHundred = if (!activity_add_title_proteins_edt_text.text.isNullOrEmpty())
                activity_add_title_proteins_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f
            val fatsInHundred = if (!activity_add_title_fats_edt_text.text.isNullOrEmpty())
                activity_add_title_fats_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f
            val carbsInHundred = if (!activity_add_title_carbs_edt_text.text.isNullOrEmpty())
                activity_add_title_carbs_edt_text.text.toString().toFloat() * hundredMultiplier
            else 0f

            // save product based on user inputs
            addViewModel.saveProduct(
                name,
                energy,
                proteinsInHundred,
                fatsInHundred,
                carbsInHundred,
                sugar,
                sFats,
                uFats,
                tFats
            ).invokeOnCompletion {
                finish()
            }
        }
    }
}
