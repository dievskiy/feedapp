/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.feedapp.app.R
import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.databinding.ActivityDetailedRecipeBinding
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_ID
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_TITLE
import com.feedapp.app.ui.adapters.RecipeIngredientAdapter
import com.feedapp.app.ui.adapters.RecipeStepAdapter
import com.feedapp.app.ui.fragments.home.RecipesFragment.Companion.INTENT_EXTRAS_IMAGE_URI
import com.feedapp.app.ui.viewclasses.DayRecyclerViewItemDecoration
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.DetailedRecipeViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import javax.inject.Inject


class DetailedRecipeActivity @Inject constructor() : ClassicActivity() {

    @Inject
    lateinit var stepAdapter: RecipeStepAdapter

    @Inject
    lateinit var ingAdapter: RecipeIngredientAdapter

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    private val viewModel by lazy {
        ViewModelProvider(this, modelFactory).get(DetailedRecipeViewModel::class.java)

    }

    private lateinit var binding: ActivityDetailedRecipeBinding

    private val startIntent = Intent(Intent.ACTION_VIEW)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        let {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_detailed_recipe)
            binding.viewmodel = viewModel
            binding.lifecycleOwner = this
        }

        setSupportActionBar(binding.detailedRecipesToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setUpView()
    }

    private fun setUpView() {
        val title = intent.getStringExtra(INTENT_EXTRAS_TITLE) ?: "Recipe"
        val id = intent.getIntExtra(INTENT_EXTRAS_ID, 0)
        val imageUri = intent.getStringExtra(INTENT_EXTRAS_IMAGE_URI)

        setUpAppbar(title, imageUri)
        setUpListeners()
        setUpObservers()
        loadRecipeModel(id)
        setUpAdapter()

        binding.detailedRecipeTitle.text = title

    }

    private fun setUpAdapter() {
        val layoutManagerSteps = LinearLayoutManager(this)
        val layoutManagerIngs = LinearLayoutManager(this)

        viewModel.user.observe(this, Observer {
            it?.let {
                ingAdapter.measureType = it.measureType
            }
        })

        binding.detailedRecipeStepRv.apply {
            layoutManager = layoutManagerSteps
            addItemDecoration(DayRecyclerViewItemDecoration(50))
            setHasFixedSize(true)
            adapter = stepAdapter
        }

        binding.detailedRecipeIngRv.apply {
            layoutManager = layoutManagerIngs
            addItemDecoration(DayRecyclerViewItemDecoration(40))
            setHasFixedSize(true)
            adapter = ingAdapter
        }
    }

    private fun setUpObservers() {
        viewModel.observeRecipesDetailed().observe(this, Observer {
            viewModel.recipeDetailed.postValue(it)
        })

        viewModel.recipeDetailed.observe(this, Observer {
            ingAdapter.servings = it.servings
            val credits = viewModel.checkCredits(it.creditsText, it.sourceName, it.sourceUrl)
            binding.detailedRecipeCredit.text = credits
            binding.detailedRecipeTimeText.text =
                getString(R.string.recipe_time).format(it.readyInMinutes)

            // carbs
            val carbsPair = viewModel.getDailyPercentage(BasicNutrientType.CARBS)
            binding.detailedRecipeInfoCarbsBar.progress = carbsPair.first
            binding.detailedRecipeInfoCarbsText.text =
                applicationContext.getString(R.string.recipe_nutrient_percentage)
                    .format(carbsPair.second)

            // Proteins
            val proteinsPair = viewModel.getDailyPercentage(BasicNutrientType.PROTEINS)
            binding.detailedRecipeInfoProteinsBar.progress = proteinsPair.first
            binding.detailedRecipeInfoProteinsText.text =
                applicationContext.getString(R.string.recipe_nutrient_percentage)
                    .format(proteinsPair.second)

            // Fats
            val fatsPair = viewModel.getDailyPercentage(BasicNutrientType.FATS)
            binding.detailedRecipeInfoFatsBar.progress = fatsPair.first
            binding.detailedRecipeInfoFatsText.text =
                applicationContext.getString(R.string.recipe_nutrient_percentage)
                    .format(fatsPair.second)

            // steps
            if (it?.steps?.isNotEmpty() == true) {
                binding.detailedRecipeInsRl.visibility = View.VISIBLE
                it.steps[0].steps.let { steps -> stepAdapter.updateList(steps) }
            } else {
                binding.detailedRecipeInsRl.visibility = View.GONE
            }

            // ingredients
            if (it?.nutrition?.ingredients?.isNotEmpty() == true) {
                binding.detailedRecipeIngNo.visibility = View.GONE
                binding.detailedRecipeIngPerServings.visibility = View.VISIBLE
                it.nutrition.ingredients.let { ings -> ingAdapter.updateList(ings) }
            } else {
                binding.detailedRecipeIngNo.visibility = View.VISIBLE
                binding.detailedRecipeIngPerServings.visibility = View.GONE
            }

            // servings
            binding.detailedRecipeIngPerServings.text = if (it.servings > 1)
                getString(R.string.servings_many).format(it.servings.toString())
            else if (it.servings == 1)
                getString(R.string.servings_one).format(it.servings.toString())
            else ""

            binding.detailedRecipeCreditRl.setOnClickListener { _ ->
                var link = it.sourceUrl
                if (link != null && link.isEmpty()) link = it.spoonacularSourceUrl
                startIntent.data = Uri.parse(link)
                startActivity(startIntent)
            }
        })
    }

    private fun setUpListeners() {
        binding.detailedRecipeFab.setOnClickListener {
            showTrackDialog()
        }
        binding.detailedRecipesToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showTrackDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_track_recipe, null)
        val edtServings = view.findViewById<EditText>(R.id.dialog_recipes_servings)
        val mealTypeDropdown =
            view.findViewById<AutoCompleteTextView>(R.id.dialog_recipes_dropdown)
        mealTypeDropdown.apply {
            setAdapter(
                ArrayAdapter(applicationContext, R.layout.spinner_default, resources.getStringArray(R.array.MealTypeArray))
            )

            setOnClickListener {
                view.hideKeyboard(this@DetailedRecipeActivity)
            }
            setOnItemClickListener { _, _, position, _ ->
                viewModel.mealTypePosition.value = position
            }
            // remove white space in the bottom of dropdown menu
            setDropDownBackgroundResource(R.drawable.white_background)
            setText(viewModel.getDropDownInitialText(), false)
        }
        try {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_track_recipe))
                .setView(view)
                .setPositiveButton(getString(R.string.track)) { _, _ ->
                    val servings = edtServings.text.toString()
                    if (!viewModel.isServingsCorrect(servings)) {
                        toast(getString(R.string.toast_invalid_number))
                        return@setPositiveButton
                    }
                    viewModel.trackRecipe(
                        mealType = viewModel.mealTypePosition.value,
                        servings = servings.toIntOrNull()
                    ).invokeOnCompletion {
                        finish()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun loadRecipeModel(id: Int) {
        if (id == 0) return
        viewModel.startSearching()
        viewModel.searchDetailedInfo(id)
    }


    private fun setUpAppbar(
        title: String,
        imageUri: String?
    ) = try {
        binding.detailedRecipesCollapsingAppbar.addOnOffsetChangedListener(object :
            OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.detailedRecipesCollapsingToolbar.title = title
                    isShow = true
                } else if (isShow) {
                    binding.detailedRecipesCollapsingToolbar.title = " "
                    isShow = false
                }
            }
        })

        binding.detailedRecipesCollapsingToolbar.apply {
            this.title = title
            setCollapsedTitleTextColor(ResourcesCompat.getColor(resources, R.color.black, theme))
            // load image to Collapsing bar
            when {
                imageUri != null -> {
                    requestManager.load(imageUri).into(binding.detailedRecipesCtImage)
                }
            }

        }

    } catch (e: Exception) {
        e.printStackTrace()
    }


}
