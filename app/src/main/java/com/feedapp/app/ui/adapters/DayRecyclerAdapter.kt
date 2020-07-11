/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R
import com.feedapp.app.data.models.day.Meal
import com.feedapp.app.ui.viewholders.DayRecyclerViewHolder
import com.feedapp.app.util.TranslationResolver
import kotlin.math.roundToInt


class DayRecyclerAdapter(val meals: ArrayList<Meal>) :
    RecyclerView.Adapter<DayRecyclerViewHolder>() {

    private val titleColor = Color.parseColor("#6E6E6E")
    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onBindViewHolder(holder: DayRecyclerViewHolder, position: Int) {
        try {
            val meal = meals[position]
            val layoutManagerToSet = LinearLayoutManager(holder.rv.context)
            val adapterToSet = ItemDayRecyclerAdapter(meal.products)

            meal.mealType.let {
                holder.textTitle.text =
                    TranslationResolver.getTranslationMealType(it, holder.textTitle.context)
                holder.textTitle.setTextColor(titleColor)
            }
            holder.textTotal.text = holder.textTotal.context.getString(
                R.string.day_item_kcal,
                meal.getTotalCalories().roundToInt()
            )

            holder.rv.apply {
                layoutManager = layoutManagerToSet
                adapter = adapterToSet
                isNestedScrollingEnabled = false
                setRecycledViewPool(viewPool)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.vh_home_day, parent, false)
        return DayRecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return meals.size
    }

    fun updateList(newList: List<Meal>) {
        meals.clear()
        meals.addAll(newList)
        notifyDataSetChanged()
    }

}





