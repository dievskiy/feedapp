/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.feedapp.app.R
import com.feedapp.app.data.api.models.usdafoodsearch.FoodApiModel
import com.feedapp.app.data.interfaces.SearchMealsResult
import com.feedapp.app.data.models.ConnectionMode
import com.feedapp.app.ui.viewholders.MealApiViewHolder
import com.feedapp.app.util.getValidLetter


@SuppressLint("SetTextI18n")
class MealsApiRecyclerAdapter(
    val context: Context,
    private val searchMealsResult: SearchMealsResult
) : ListAdapter<FoodApiModel, MealApiViewHolder>(DIFF_CALLBACK) {

    var colorList: MutableList<Int> = arrayListOf()

    companion object {
        private const val fdcId = "fdcId"
        private const val title = "title"

        val defaultColor = Color.rgb(253, 245, 230)
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FoodApiModel>() {
            override fun areItemsTheSame(oldItem: FoodApiModel, newItem: FoodApiModel): Boolean {
                return oldItem.fdcId == newItem.fdcId && oldItem.description == newItem.description &&
                        oldItem.additionalDescriptions == newItem.additionalDescriptions
            }

            override fun areContentsTheSame(oldItem: FoodApiModel, newItem: FoodApiModel): Boolean {
                return false
            }

        }
    }

    override fun onBindViewHolder(holder: MealApiViewHolder, position: Int) {
        val food = getItem(position)
        holder.textTitle.text = food.description
        holder.mainLayout.setOnClickListener {
            if (searchMealsResult.isConnected()) {
                startDetailedActivity(food)
            }
        }
        // set Color to image
        try {
            holder.image.setColorFilter(colorList[position])
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            holder.image.setColorFilter(defaultColor)
        }
        // set Letter into imageView
        val letter = food.description?.get(0).toString().getValidLetter()
        holder.imageLetter.text = letter
    }

    private fun startDetailedActivity(food: FoodApiModel) {
        searchMealsResult.startDetailedActivity(ConnectionMode.ONLINE, null, food)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MealApiViewHolder {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_meal_api, parent, false)
        return MealApiViewHolder(holder)
    }


}