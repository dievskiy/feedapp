/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.feedapp.app.R
import com.feedapp.app.data.api.models.recipesearch.RecipeSearchModel
import com.feedapp.app.data.models.connection.ConnectionLiveData
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.data.repositories.RecipeSearchRepository
import com.feedapp.app.databinding.FragmentRecipesBinding
import com.feedapp.app.ui.activities.DetailedRecipeActivity
import com.feedapp.app.ui.activities.HomeActivity
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_ID
import com.feedapp.app.ui.activities.SearchActivity.Companion.INTENT_EXTRAS_TITLE
import com.feedapp.app.ui.adapters.OnSearchLimit
import com.feedapp.app.ui.adapters.RecipesRecyclerAdapter
import com.feedapp.app.ui.fragments.recipesbox.CardItem
import com.feedapp.app.ui.fragments.recipesbox.CardPagerAdapter
import com.feedapp.app.ui.viewclasses.ClassicStaggeredItemDecoration
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.RecipeSearchViewModel
import com.mancj.materialsearchbar.MaterialSearchBar
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class RecipesFragment : DaggerFragment(), OnSearchLimit {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var recipeSearchRepository: RecipeSearchRepository
    lateinit var viewModel: RecipeSearchViewModel


    private lateinit var resultAdapter: RecipesRecyclerAdapter

    lateinit var binding: FragmentRecipesBinding
    private val cardNumber = 4


    private val cardAdapterB = CardPagerAdapter()
    private val cardAdapterL = CardPagerAdapter()
    private val cardAdapterS = CardPagerAdapter()
    private val cardAdapterD = CardPagerAdapter()

    companion object{
        const val INTENT_EXTRAS_IMAGE_URI = "imageUri"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.run {
            ViewModelProvider(this, modelFactory)[RecipeSearchViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recipes, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = requireActivity()

        // setup vh_recipes_card
        resultAdapter = RecipesRecyclerAdapter({ id, imageUri, title ->
            run {
                val intent = Intent(context, DetailedRecipeActivity::class.java)
                intent.putExtra(INTENT_EXTRAS_ID, id)
                intent.putExtra(INTENT_EXTRAS_IMAGE_URI, imageUri)
                intent.putExtra(INTENT_EXTRAS_TITLE, title)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                activity?.startActivity(intent)
            }
        }, requestManager, this)
        binding.fragmentRecipesSResultRv.run {
            addItemDecoration(ClassicStaggeredItemDecoration(12))
            layoutManager = StaggeredGridLayoutManager(2, 1)
            adapter = resultAdapter

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setObservers()

        try {
            // set up listener for receiving query from SearchActivity
            findNavController().addOnDestinationChangedListener { _, _, arguments ->
                val query = arguments?.getString(HomeActivity.EXTRAS_RECIPES_QUERY)
                query?.let {
                    search(it)
                }
            }

        } catch (e: Exception) {
        }

    }


    private fun setUpView() {
        scrollActivityToTop()
        setViewPagers()
        binding.fragmentRecipesSearchBar.apply {
            setPlaceHolderColor(Color.BLACK)
            setCardViewElevation(0)
            setOnSearchActionListener(searchActionListener)
        }
    }

    private fun scrollActivityToTop() =
        try {
            requireActivity().findViewById<NestedScrollView>(R.id.activity_main_nsv)
                .smoothScrollTo(0, 0)
        } catch (e: java.lang.Exception) {
        }


    private val searchActionListener = object : MaterialSearchBar.OnSearchActionListener {

        override fun onButtonClicked(buttonCode: Int) {}

        override fun onSearchStateChanged(enabled: Boolean) {}

        override fun onSearchConfirmed(text: CharSequence?) {
            if (text == null || text.isEmpty()) return
            if (text.toString().length < 3 || text.toString().contains(".*\\d.*".toRegex())) {
                activity?.toast(context!!.getString(R.string.error_recipes_query))
                return
            }
            activity?.hideKeyboard()
            binding.fragmentRecipesSearchBar.clearFocus()
            search(text.toString())
        }

    }

    private fun search(query: String) {
        if (viewModel.isConnected()) {
            if (!viewModel.areRecipesLimitReached()) {
                viewModel.searchRecipe(query)
            } else searchLimitReached()
        } else {
            activity?.toast(requireActivity().getString(R.string.error_toast_connection))
        }
    }

    private fun searchLimitReached() {
        AlertDialog.Builder(requireActivity())
            .setTitle(getString(R.string.dialog_search_limit_title))
            .setMessage(getString(R.string.dialog_search_limit))
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.cancel, null)
            .show()

    }


    private fun setViewPagers() {
        for (i in 0 until cardNumber) {
            val a = CardItem("", "")
            cardAdapterB.addCardItem(a)
            cardAdapterL.addCardItem(a)
            cardAdapterS.addCardItem(a)
            cardAdapterD.addCardItem(a)
        }

        binding.fragmentRecipesBreakfastViewpager.apply {
            adapter = cardAdapterB
            offscreenPageLimit = 4
        }
        binding.fragmentRecipesLunchViewpager.apply {
            adapter = cardAdapterL
            offscreenPageLimit = 4
        }
        binding.fragmentRecipesSnackViewpager.apply {
            adapter = cardAdapterS
            offscreenPageLimit = 4
        }
        binding.fragmentRecipesDinnerViewpager.apply {
            adapter = cardAdapterD
            offscreenPageLimit = 4
        }
    }


    private fun getCardImageListener(mealType: MealType): RequestListener<Drawable>? {
        return object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                when (mealType) {
                    MealType.BREAKFAST -> binding.fragmentRecipesBreakfastShimmer.hideShimmer()
                    MealType.LUNCH -> binding.fragmentRecipesLunchShimmer.hideShimmer()
                    MealType.SNACK -> binding.fragmentRecipesSnackShimmer.hideShimmer()
                    MealType.DINNER -> binding.fragmentRecipesDinnerShimmer.hideShimmer()
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                when (mealType) {
                    MealType.BREAKFAST -> binding.fragmentRecipesBreakfastShimmer.hideShimmer()
                    MealType.LUNCH -> binding.fragmentRecipesLunchShimmer.hideShimmer()
                    MealType.SNACK -> binding.fragmentRecipesSnackShimmer.hideShimmer()
                    MealType.DINNER -> binding.fragmentRecipesDinnerShimmer.hideShimmer()
                }
                return false
            }
        }
    }

    private fun setUpCard(
        i: Int,
        card: CardView?,
        model: RecipeSearchModel,
        mealType: MealType
    ) {
        try {
            val image = card?.findViewById<ImageView>(R.id.recipes_card_image)
            val titleView = card?.findViewById<TextView>(R.id.titleTextView)
            if (card == null || image == null || titleView == null || model.results.getOrNull(i)
                == null
            ) return

            val title = recipeSearchRepository.checkTitle(model.results[i].title)
            var fullImageUri: String? = null
            if (!model.results[i].image.isNullOrEmpty()) {
                fullImageUri = model.baseUri?.plus(model.results[i].image)
                fullImageUri?.let {
                    requestManager.load(fullImageUri)
                        .listener(getCardImageListener(mealType))
                        .into(image)
                }
            }
            card.findViewById<RelativeLayout>(R.id.recipes_card_rl)
                ?.setOnClickListener { _ ->
                    val intent = Intent(activity, DetailedRecipeActivity::class.java)
                    val id = model.results[i].id
                    intent.putExtra(INTENT_EXTRAS_IMAGE_URI, fullImageUri)
                    intent.putExtra(INTENT_EXTRAS_ID, id)
                    intent.putExtra(INTENT_EXTRAS_TITLE, title)
                    startActivity(intent)
                }

            titleView.text = title

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setObservers() {
        viewModel.run {
            searchRecipes.observe(viewLifecycleOwner, Observer {
                resultAdapter.submitList(it.results)
            })

            val connectionLiveData = context?.applicationContext?.let { ConnectionLiveData(it) }
            connectionLiveData?.observe(viewLifecycleOwner, Observer {
                it?.let {
                    if (it.isConnected) {
                        isConnected.value = true
                        if (binding.fragmentRecipesVpRl.visibility == View.VISIBLE) {
                            loadDefaultRecipes()
                        }
                    } else {
                        isConnected.postValue(false)
                    }
                }
            })

            recipesBreakfast.observe(viewLifecycleOwner, Observer {
                for (i in 0 until cardAdapterB.count) {
                    setUpCard(i, cardAdapterB.getCardViewAt(i), it, MealType.BREAKFAST)
                }

            })

            recipesLunch.observe(viewLifecycleOwner, Observer {
                for (i in 0 until cardAdapterL.count) {
                    setUpCard(i, cardAdapterL.getCardViewAt(i), it, MealType.LUNCH)
                }

            })

            recipesSnack.observe(viewLifecycleOwner, Observer {
                for (i in 0 until cardAdapterS.count) {
                    setUpCard(i, cardAdapterS.getCardViewAt(i), it, MealType.SNACK)
                }

            })

            recipesDinner.observe(viewLifecycleOwner, Observer {
                for (i in 0 until cardAdapterD.count) {
                    setUpCard(i, cardAdapterD.getCardViewAt(i), it, MealType.DINNER)
                }
            })

        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.updateIntoleranceAndDiet()
    }

    override fun ifLimitReached(): Boolean = viewModel.areRecipesLimitReached()


    override fun limitReached() {
        searchLimitReached()
    }

}