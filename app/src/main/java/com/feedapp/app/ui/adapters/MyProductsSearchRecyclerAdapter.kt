/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.feedapp.app.R
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.ui.viewholders.MyProductsSearchVH
import com.feedapp.app.util.getValidLetter


@SuppressLint("SetTextI18n")
class MyProductsSearchRecyclerAdapter(private val myProductsSearchResult: ((Int, String) -> Unit)) :
    ListAdapter<FoodProduct, MyProductsSearchVH>(DIFF_CALLBACK) {

    companion object {
        // color for image with letter
        val defaultColor = Color.rgb(253, 245, 230)
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FoodProduct>() {
            override fun areItemsTheSame(oldItem: FoodProduct, newItem: FoodProduct): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodProduct, newItem: FoodProduct): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }


    override fun onBindViewHolder(holder: MyProductsSearchVH, position: Int) {
        val food = getItem(position)
        holder.textTitle.text = food.name
        holder.mainLayout.setOnClickListener {
            myProductsSearchResult.invoke(food.id, food.name)
        }

        // set Color to image
        holder.image.setColorFilter(defaultColor)

        // check letter
        val letter = food.name.getOrNull(0).toString().getValidLetter()
        holder.imageLetter.text = letter
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyProductsSearchVH {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_my_meals_search, parent, false)
        return MyProductsSearchVH(holder)
    }


}