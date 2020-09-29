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
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.models.localdb.IProduct
import com.feedapp.app.data.models.localdb.LocalDBUris
import com.feedapp.app.data.models.localdb.LocalInjectorUtils
import com.feedapp.app.data.models.prefs.SharedPrefsHelper
import com.feedapp.app.databinding.ActivitySearchBinding
import com.feedapp.app.ui.activities.HomeActivity.Companion.RESULT_CODE_UPDATE_DAY
import com.feedapp.app.ui.adapters.FoodProductRecyclerAdapter
import com.feedapp.app.ui.adapters.RecentProductsRecyclerAdapter
import com.feedapp.app.ui.viewclasses.ClassicItemDecoration
import com.feedapp.app.ui.viewclasses.SearchActionListener
import com.feedapp.app.ui.viewclasses.SearchSuggestionAdapter
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.SearchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.Serializable
import javax.inject.Inject


class SearchActivity @Inject constructor() : ClassicActivity() {

    @Inject
    lateinit var spHelper: SharedPrefsHelper

    private var localeSearch: String? = null

    companion object {
        const val SP_KEY_LOCALE_SEARCH = "localeSearch"
        const val SP_KEY_LOCALE_ASK = "askToDownload"
        const val INTENT_EXTRAS_TITLE = "title"
        const val INTENT_EXTRAS_ID = "id"
        const val INTENT_EXTRAS_PRODUCT = "product"
    }

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

        // check locale and set needed db
        configureSearchDB()

    }

    /**
     * Checks if db for current locale is needed to be set and does it
     */
    private fun configureSearchDB() {

        spHelper.getLocalSearchPreferred()?.let {
            val cacheDirPath = this.cacheDir?.toString() ?: return
            val filePath = LocalDBUris.getDBPath(cacheDirPath, it)
            val file = File(filePath)

            if (!file.exists()) return

            localeSearch = it
            val repo = LocalInjectorUtils.provideRepository(this, file)
            viewModel.initLocalFoodDelegate(repo)
        }
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

    private suspend fun getSearchSuggestions(s: String): List<String> {
        return viewModel.getSearchSuggestions(s)
    }

    private fun setSearchBar() {

        binding.activitySearchSearchBar.setCustomSuggestionAdapter(
            SearchSuggestionAdapter(
                layoutInflater,
                { q -> searchByQuery(q) },
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
                            val list = getSearchSuggestions(s.toString())
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

        binding.activitySearchSearchBar.setOnSearchActionListener(SearchActionListener { q ->
            searchByQuery(
                q
            )
        })
    }

    private fun setAdapters() {
        dateString = intent.extras?.getSerializable(HomeActivity.INTENT_EXTRAS_DATE) as DayDate?
        mealTypeCode = intent.extras?.getInt(HomeActivity.INTENT_EXTRAS_MEAL_TYPE)
        recentAdapter =
            RecentProductsRecyclerAdapter(this) { id, name -> onRecentItemClicked(id, name) }
        offlineAdapter = FoodProductRecyclerAdapter(this) { p -> startDetailedActivity(p) }
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
            intent.putExtra(HomeActivity.INTENT_EXTRAS_DATE, dateString)
            intent.putExtra(HomeActivity.INTENT_EXTRAS_MEAL_TYPE, mealTypeCode)
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
            it?.let {
                recentAdapter.submitList(it)
            }
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

    private fun searchByQuery(q: String) {
        binding.activitySearchSearchBar.clearFocus()
        binding.activitySearchSearchBar.disableSearch()
        hideKeyboard()
        viewModel.searchByQuery(q)
    }


    private fun startDetailedActivity(food: IProduct) {
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_DATE, dateString)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_MEAL_TYPE, mealTypeCode)
        intent.putExtra(INTENT_EXTRAS_ID, food.id)
        intent.putExtra(INTENT_EXTRAS_TITLE, food.name)

        localeSearch?.let {
            val product = ProductImplFactory().createProductImpl(food)
            intent.putExtra(INTENT_EXTRAS_PRODUCT, product)
        }
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

    private fun onRecentItemClicked(id: Int, name: String) {
        val intent = Intent(this, DetailedFoodActivity::class.java)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_DATE, dateString)
        intent.putExtra(HomeActivity.INTENT_EXTRAS_MEAL_TYPE, mealTypeCode)
        intent.putExtra(INTENT_EXTRAS_ID, id)
        intent.putExtra(INTENT_EXTRAS_TITLE, name)

        localeSearch?.let {
            CoroutineScope(IO).launch {
                val food = viewModel.getByIdLocal(id) ?: return@launch
                val product = ProductImplFactory().createProductImpl(food)

                intent.putExtra(INTENT_EXTRAS_PRODUCT, product)
                startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
            }
        } ?: run {
            startActivityForResult(intent, HomeActivity.REQUEST_CODE_ADD_MEAL)
        }

    }


}

class ProductImpl(
    override val name: String,
    override val proteins: Float,
    override val fats: Float,
    override val carbs: Float,
    override val calories: Float,
    override var id: Int
) : IProduct, Serializable

class ProductImplFactory {
    fun createProductImpl(food: IProduct): ProductImpl {
        return ProductImpl(
            name = food.name,
            proteins = food.proteins,
            fats = food.fats,
            carbs = food.carbs,
            calories = food.calories,
            id = food.id
        )
    }
}