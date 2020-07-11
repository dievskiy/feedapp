/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.feedapp.app.R
import com.feedapp.app.data.models.RecentProduct
import com.feedapp.app.ui.activities.DetailedFoodActivity
import com.feedapp.app.ui.viewholders.RecentProductsViewHolder
import com.feedapp.app.util.getValidLetter

@SuppressLint("SetTextI18n")
class RecentProductsRecyclerAdapter(
    val context: Context,
    private val searchResult: ((Int, String) -> Unit)
) : ListAdapter<RecentProduct, RecentProductsViewHolder>(DIFF_CALLBACK) {

    val intent = Intent(context, DetailedFoodActivity::class.java)

    companion object {
        val defaultColor = Color.rgb(253, 245, 230)
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecentProduct>() {
            override fun areItemsTheSame(oldItem: RecentProduct, newItem: RecentProduct): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: RecentProduct,
                newItem: RecentProduct
            ): Boolean {
                return oldItem.name == newItem.name
            }

        }
    }


    override fun onBindViewHolder(holder: RecentProductsViewHolder, position: Int) {
        val food = getItem(position)
        food.run {
            holder.textTitle.text = name
            // set Color to image
            holder.image.setColorFilter(defaultColor)
            val letter = name.getOrNull(0).toString().getValidLetter()
            holder.imageLetter.text = letter

        }
        holder.mainLayout.setOnClickListener {
            searchResult.invoke(food.foodProductId, food.name)
        }

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentProductsViewHolder {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_recent_product, parent, false)
        return RecentProductsViewHolder(holder)
    }


}