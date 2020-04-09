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
import com.feedapp.app.data.interfaces.MyProductsSearchResult
import com.feedapp.app.data.models.ConnectionMode
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.databinding.ActivityMyMealsSearchBinding
import com.feedapp.app.ui.adapters.MyProductsSearchRecyclerAdapter
import com.feedapp.app.util.intentDate
import com.feedapp.app.util.intentMealType
import com.feedapp.app.viewModels.MyMealsSearchViewModel
import javax.inject.Inject

class MyMealsSearchActivity @Inject constructor() : ClassicActivity(), MyProductsSearchResult {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(MyMealsSearchViewModel::class.java)

    }


    private lateinit var binding: ActivityMyMealsSearchBinding
    private val adapterToSet = MyProductsSearchRecyclerAdapter(this)

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

    override fun startDetailedActivity(id: Int, title: String) {
        val mealType: Int = intent?.extras?.getInt(intentMealType) ?: 0
        val date: DayDate? = intent?.extras?.getSerializable(intentDate) as DayDate?
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra("connectionMode", ConnectionMode.OFFLINE)
        intent.putExtra("id", id)
        intent.putExtra("title", title)
        intent.putExtra(intentDate, date)
        intent.putExtra(intentMealType, mealType)
        startActivity(intent)
    }
}
