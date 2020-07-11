/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.data.models.ColorGenerator
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.models.localdb.IProduct
import com.feedapp.app.databinding.ActivityDetailedFoodBinding
import com.feedapp.app.ui.activities.MyMealsSearchActivity.Companion.INTENT_EXTRAS_CUSTOM_PRODUCT
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_ID
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_PRODUCT
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_TITLE
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.toFloatOrZero
import com.feedapp.app.viewModels.DetailedViewModel
import javax.inject.Inject


class DetailedFoodActivity @Inject constructor() : ClassicActivity() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(DetailedViewModel::class.java)
    }

    private lateinit var binding: ActivityDetailedFoodBinding


    private val dropdownMultipliers = listOf(1.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed_food)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this


        val id = intent.extras?.getInt(INTENT_EXTRAS_ID)
        val productIntent = intent.getSerializableExtra(INTENT_EXTRAS_PRODUCT) as IProduct?
        product = productIntent
        val title = intent.extras?.getString(INTENT_EXTRAS_TITLE) ?: getString(R.string.product)

        setUpLayout(title)
        setListeners()
        loadDetailedData(id)

        viewModel.changeMultiplierValue(100.0)
    }

    private fun loadDetailedData(id: Int?) {
        // get data about specific product
        id?.let {
            product?.let {
                viewModel.setLocalProductInfo(product = it)
                viewModel.usingLocal.value = true
            } ?: viewModel.searchFoodProduct(id)
        }
    }


    private fun setUpLayout(title: String) {
        val quantityList = arrayListOf<String>()
        val spinner = findViewById<AutoCompleteTextView>(R.id.detailed_quantity_dropdown)
        val adapter = ArrayAdapter(
            applicationContext, R.layout.spinner_default,
            quantityList
        )

        // set action bar
        setSupportActionBar(binding.activityDetailedToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // set spinner
        quantityList.add(getString(R.string.grams))
        spinner.setAdapter(adapter)
        spinner.setText(spinner.adapter.getItem(0).toString(), false)
        spinner.setOnItemClickListener { _, _, position, _ ->
            viewModel.changeMultiplierValue(dropdownMultipliers[position])
        }
        // remove white space in the bottom of dropdown menu
        spinner.setDropDownBackgroundResource(R.drawable.white_background)

        //set collapsing toolbar
        setUpCollapsingBar(title)

    }

    private fun setListeners() {
        binding.activityDetailedToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val quantityET = findViewById<EditText>(R.id.detailed_quantity_edit)
        quantityET.addTextChangedListener {
            if (it.isNullOrBlank()) {
                return@addTextChangedListener
            }
            it.toString().toDoubleOrNull()?.let { value -> viewModel.changeMultiplierValue(value) }
        }

        // hide keyboard if user presses OK after editing quantity
        quantityET.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                quantityET.clearFocus()
                this.hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        // change multiplier according to dropdown menu
        binding.detailedQuantityDropdown.setOnItemClickListener { _, _, position, _ ->
            hideKeyboard()
        }


        binding.detailedAddProduct.setOnClickListener { view ->
            hideKeyboard()
            val text = quantityET.text
            if (text.isNullOrBlank() || text.toString() == "0" || text.toString()
                    .toFloatOrZero() == 0f
            ) {
                quantityET.error = getString(R.string.error_0)
                return@setOnClickListener
            } else if (!viewModel.isMultiplierValueValid(text.toString())) {
                quantityET.error = getString(R.string.error_too_big)
                return@setOnClickListener
            } else {
                // if everything with numbers is ok, save to db
                view.isClickable = false
                val grams = quantityET.text.toString().toFloatOrNull() ?: 100f
                saveConsumedFoodToDB(grams)
            }

        }
    }

    private var product: IProduct? = null

    private fun saveConsumedFoodToDB(grams: Float) {
        val dateString: DayDate? = intent.extras?.getSerializable(HomeActivity.INTENT_EXTRAS_DATE) as DayDate?
        val mealType: Int? = intent.extras?.getInt(HomeActivity.INTENT_EXTRAS_MEAL_TYPE)
        val custom = intent.extras?.getBoolean(INTENT_EXTRAS_CUSTOM_PRODUCT) ?: false
        (
                if (custom) viewModel.saveWithoutRecent(dateString, mealType, grams, product)
                else viewModel.saveConsumedFoodToDB(
                    dateString,
                    mealType,
                    grams,
                    product
                )
                )
            .invokeOnCompletion {
                setResult(HomeActivity.RESULT_CODE_UPDATE_DAY)
                finish()
            }


    }

    private fun setUpCollapsingBar(title: String) = try {
        binding.activityDetailedCollapsingToolbar.apply {
            this.title = title
            setCollapsedTitleTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
            setExpandedTitleColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.colorText_desc,
                    theme
                )
            )
            setBackgroundColor(ColorGenerator().getColorWithAlpha())
            setExpandedTitleTextAppearance(R.style.CollapsingToolbar_Title)
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

}
