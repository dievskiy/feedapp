/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R
import com.feedapp.app.data.models.prefs.SharedPrefsHelper
import com.feedapp.app.databinding.FragmentMyMealsBinding
import com.feedapp.app.ui.adapters.MyProductsRecyclerAdapter
import com.feedapp.app.ui.viewclasses.MealsItemTouchCallback
import com.feedapp.app.util.toast
import com.feedapp.app.viewModels.MyMealsViewModel
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class MyMealsFragment : DaggerFragment() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var myMealsViewModel: MyMealsViewModel

    @Inject
    lateinit var spHelper: SharedPrefsHelper

    lateinit var binding: FragmentMyMealsBinding
    private val productsAdapter = MyProductsRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myMealsViewModel = activity?.run {
            ViewModelProvider(this, modelFactory).get(MyMealsViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_meals, container, false)
        binding.mealsVM = myMealsViewModel
        binding.lifecycleOwner = requireActivity()

        return binding.root
    }

    /*
     * Check if introduction screen for Create FAB showed
     */
    private fun checkProductsUiGuide() {
        try {
            val fab =
                requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.fab_add_product)
            TapTargetView.showFor(requireActivity(),
                TapTarget.forView(
                    fab,
                    getString(R.string.intro_my_meals_title),
                    getString(R.string.intro_my_meals_desc)
                )
                    .outerCircleAlpha(0.96f)
                    .titleTextSize(22)
                    .titleTextColor(R.color.white)
                    .dimColor(R.color.black)
                    .drawShadow(true)
                    .cancelable(true)
                    .tintTarget(true)
                    .transparentTarget(false)
                    .targetRadius(100),
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView) {
                        // prevent bugs with nestedscrollview's focusability
                        view.isFocusable = false
                        super.onTargetClick(view)
                        spHelper.saveProductsUiGuideShowed()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val layoutManagerToSet = LinearLayoutManager(requireParentFragment().context)
        binding.fragmentMyMealsRv.apply {
            layoutManager = layoutManagerToSet
            adapter = productsAdapter
        }
        setObservers()
        setViews()
    }

    // set recycler view swipe callback
    private fun setViews() {
        val swipeHandler = object : MealsItemTouchCallback(requireActivity().applicationContext) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myMealsViewModel.myProducts.value?.get(viewHolder.adapterPosition)?.let {
                    myMealsViewModel.deleteCustomProduct(it)
                    context?.toast(getString(R.string.toast_product_deleted))
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.fragmentMyMealsRv)
    }


    private fun setObservers() {
        if (!spHelper.isProductsUiGuideShowed()) checkProductsUiGuide()

        myMealsViewModel.myProducts.observe(viewLifecycleOwner, Observer { it ->
            CoroutineScope(Main).launch {
                it?.let {
                    if (myMealsViewModel.isProgressBarVisible.value != null &&
                        myMealsViewModel.isProgressBarVisible.value!!
                    ) {
                        delay(300L)
                        myMealsViewModel.isProgressBarVisible.postValue(false)
                    }
                    val list = it.toList()
                    productsAdapter.submitList(list)
                }
                if (myMealsViewModel.isProductsEmpty()) {
                    myMealsViewModel.isTextNoMealsVisible.postValue(true)
                    myMealsViewModel.isProgressBarVisible.postValue(false)
                } else {
                    myMealsViewModel.isTextNoMealsVisible.postValue(false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        myMealsViewModel.refreshCustomProducts()
    }

}