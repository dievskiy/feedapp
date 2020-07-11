/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedapp.app.R
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.databinding.ActivityMyMealsSearchBinding
import com.feedapp.app.ui.activities.HomeActivity.Companion.INTENT_EXTRAS_MEAL_TYPE
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_ID
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_TITLE
import com.feedapp.app.ui.adapters.MyProductsSearchRecyclerAdapter
import com.feedapp.app.viewModels.MyMealsSearchViewModel
import javax.inject.Inject

class MyMealsSearchActivity @Inject constructor() : ClassicActivity() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(MyMealsSearchViewModel::class.java)
    }

    private lateinit var binding: ActivityMyMealsSearchBinding
    private val adapterToSet =
        MyProductsSearchRecyclerAdapter { id, title -> startDetailedActivity(id, title) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_meals_search)
        setStatusBar()

        let {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_my_meals_search)
            binding.viewmodel = viewModel
        }
        setSupportActionBar(binding.activityMySearchMtoolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.activityMySearchMtoolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val layoutManagerToSet = LinearLayoutManager(applicationContext)
        binding.activityMymealsSearchRv.run {
            layoutManager = layoutManagerToSet
            adapter = adapterToSet
        }

        setObservers()
        viewModel.updateProducts()

    }

    private fun setObservers() {
        viewModel.myProducts.observe(this, Observer {
            adapterToSet.submitList(it)
            if (it.isEmpty()) {
                binding.activityMySearchNoproducts.visibility = View.VISIBLE
            } else {
                binding.activityMySearchNoproducts.visibility = View.GONE
            }
        })
    }

    fun startDetailedActivity(id: Int, title: String) {
        val mealType: Int = intent?.extras?.getInt(INTENT_EXTRAS_MEAL_TYPE) ?: 0
        val date: DayDate? =
            intent?.extras?.getSerializable(HomeActivity.INTENT_EXTRAS_DATE) as DayDate?
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra(INTENT_EXTRAS_ID, id)
        intent.putExtra(INTENT_EXTRAS_TITLE, title)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_DATE, date)
        intent.putExtra(INTENT_EXTRAS_MEAL_TYPE, mealType)
        intent.putExtra(INTENT_EXTRAS_CUSTOM_PRODUCT, true)
        startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // pass update code from DetailedActivity to the HomeActivity
            HomeActivity.REQUEST_CODE_ADD_MEAL -> {
                if (resultCode == HomeActivity.RESULT_CODE_UPDATE_DAY) {
                    setResult(HomeActivity.RESULT_CODE_UPDATE_DAY)
                }
            }
        }
    }

    companion object {
        const val INTENT_EXTRAS_CUSTOM_PRODUCT = "custom"
    }

}
