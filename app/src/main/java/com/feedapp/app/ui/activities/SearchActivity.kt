/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.RecentProductResult
import com.feedapp.app.data.interfaces.SearchMealsResult
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.databinding.ActivitySearchBinding
import com.feedapp.app.ui.activities.HomeActivity.Companion.RESULT_CODE_UPDATE_DAY
import com.feedapp.app.ui.adapters.FoodProductRecyclerAdapter
import com.feedapp.app.ui.adapters.RecentProductsRecyclerAdapter
import com.feedapp.app.ui.viewclasses.ClassicItemDecoration
import com.feedapp.app.util.intentDate
import com.feedapp.app.util.intentMealType
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.SearchViewModel
import javax.inject.Inject


class SearchActivity @Inject constructor() : ClassicActivity(), SearchMealsResult,
    RecentProductResult {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(SearchViewModel::class.java)
    }
    private lateinit var binding: ActivitySearchBinding
    private lateinit var recentAdapter: RecentProductsRecyclerAdapter
    private lateinit var offlineAdapter: FoodProductRecyclerAdapter

    private var dateString: DayDate? = null

    private var mealTypeCode: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setSupportActionBar(binding.activitySearchMtoolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setUpView()
        subscribeObservers()
        setBindingListeners()

    }

    private fun setUpView() {
        setAdapters()
        setViews()
        setUpListeners()


    }

    private fun setUpListeners() {
        binding.activitySearchNotFound.setOnClickListener {
            viewModel.searchQuery.value?.run {
                if (this.length < 3) return@setOnClickListener
                val intent = Intent()
                intent.putExtra(HomeActivity.EXTRAS_RECIPES_QUERY, this)
                intent.putExtra(HomeActivity.EXTRAS_UPDATE_DAY, viewModel.hasAdded.value)
                setResult(HomeActivity.RESULT_CODE_SEARCH_IN_RECIPES, intent)
                finish()
            } ?: toast(getString(R.string.no_query))

        }

        binding.activitySearchMtoolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setViews() {
        binding.activitySearchRv.run {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = offlineAdapter
            addItemDecoration(ClassicItemDecoration(context))
        }

        binding.activitySearchRvRecent.run {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = recentAdapter
            addItemDecoration(ClassicItemDecoration(context))
        }
    }

    private fun setAdapters() {
        dateString = intent.extras?.getSerializable(intentDate) as DayDate?
        mealTypeCode = intent.extras?.getInt(intentMealType)
        recentAdapter = RecentProductsRecyclerAdapter(this, this)
        offlineAdapter = FoodProductRecyclerAdapter(this, this)
    }

    private fun setBindingListeners() {
        binding.activitySearchTextCreateLl.setOnClickListener {
            binding.activitySearchTextCreateLl.isClickable = false
            val intent = Intent(this, AddCustomProductActivity::class.java)
            startActivity(intent)
        }
        binding.activitySearchTextChooseLl.setOnClickListener {
            binding.activitySearchTextChooseLl.isClickable = false
            val intent = Intent(this, MyMealsSearchActivity::class.java)
            intent.putExtra(intentDate, dateString)
            intent.putExtra(intentMealType, mealTypeCode)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.activitySearchTextCreateLl.isClickable = true
        binding.activitySearchTextChooseLl.isClickable = true
    }

    override fun onBackPressed() {
        if (viewModel.hasSearched.value == true) {
            viewModel.setHasSearched(false)
        } else finish()
    }


    private fun subscribeObservers() {

        viewModel.recentProducts.observe(this, Observer {
            it?.let { recentAdapter.submitList(it) }
        })

        viewModel.meals.observe(this, Observer {
            offlineAdapter.colorList.addAll(viewModel.generateColors(it?.size))
            offlineAdapter.submitList(it)
            viewModel.hasSearched.value?.let { isSearched ->
                if (it.isNullOrEmpty() && isSearched) {
                    binding.activitySearchRv.overScrollMode = View.OVER_SCROLL_NEVER
                } else {
                    binding.activitySearchRv.overScrollMode = View.OVER_SCROLL_ALWAYS
                }
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search_menu, menu)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView: SearchView? = searchItem?.actionView as SearchView

        searchView?.let {
            it.setSearchableInfo(manager.getSearchableInfo(componentName))

            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query == null || query.length < 3) return true
                    searchByQuery(query)
                    it.clearFocus()
                    it.setQuery("", false)
                    searchItem.collapseActionView()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
        return true
    }

    private fun searchByQuery(q: String) {
        viewModel.searchByQuery(q)
    }

    override fun startDetailedActivity(foodOffline: FoodProduct) {
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra(intentDate, dateString)
        intent.putExtra(intentMealType, mealTypeCode)
        intent.putExtra("id", foodOffline.id)
        intent.putExtra("title", foodOffline.name)
        startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
    }

    override fun startDetailedActivity(recentFdcId: Int, name: String) {
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra(intentDate, dateString)
        intent.putExtra(intentMealType, mealTypeCode)
        intent.putExtra("id", recentFdcId)
        intent.putExtra("title", name)
        startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // pass update code from DetailedActivity to the HomeActivity
            HomeActivity.REQUEST_CODE_ADD_MEAL -> {
                if (resultCode == RESULT_CODE_UPDATE_DAY) {
                    viewModel.hasAdded.postValue(true)
                    setResult(RESULT_CODE_UPDATE_DAY)
                }
            }
        }
    }


}
