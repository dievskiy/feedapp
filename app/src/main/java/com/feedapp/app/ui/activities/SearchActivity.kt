/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedapp.app.R
import com.feedapp.app.data.interfaces.RecentProductResult
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.databinding.ActivitySearchBinding
import com.feedapp.app.ui.activities.HomeActivity.Companion.RESULT_CODE_UPDATE_DAY
import com.feedapp.app.ui.adapters.FoodProductRecyclerAdapter
import com.feedapp.app.ui.adapters.RecentProductsRecyclerAdapter
import com.feedapp.app.ui.viewclasses.ClassicItemDecoration
import com.feedapp.app.ui.viewclasses.SearchActionListener
import com.feedapp.app.ui.viewclasses.SearchByQuery
import com.feedapp.app.ui.viewclasses.SearchSuggestionAdapter
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.intentDate
import com.feedapp.app.util.intentMealType
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class SearchActivity @Inject constructor() : ClassicActivity(),
    RecentProductResult, SearchByQuery {

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
    }


    private fun setViews() {
        setSearchBar()
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


    private fun setSearchBar() {

        binding.activitySearchSearchBar.setCustomSuggestionAdapter(
            SearchSuggestionAdapter(
                layoutInflater,
                this,
                resources.getInteger(R.integer.search_view_height_int)
            )
        )

        // set up material search bar for product search
        binding.activitySearchSearchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s ?: return

                // show suggestions from 2 chars
                if (s.length >= 2) {
                    binding.activitySearchSearchBar.apply {
                        CoroutineScope(IO).launch {
                            // update suggestions list when query changed
                            val list = viewModel.getSearchSuggestions(s.toString())
                            withContext(Main) {
                                updateLastSuggestions(list)
                            }
                        }
                    }
                } else {
                    binding.activitySearchSearchBar.clearSuggestions()
                }
            }
        })

        binding.activitySearchSearchBar.setOnSearchActionListener(SearchActionListener(this))
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
            startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
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

    override fun searchByQuery(q: String) {
        binding.activitySearchSearchBar.clearFocus()
        binding.activitySearchSearchBar.disableSearch()
        hideKeyboard()
        viewModel.searchByQuery(q)
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
