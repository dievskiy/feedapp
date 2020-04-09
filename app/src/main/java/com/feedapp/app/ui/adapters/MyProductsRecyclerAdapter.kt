package com.feedapp.app.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.feedapp.app.R
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.ui.viewholders.MyProductsRecyclerViewHolder
import com.feedapp.app.util.getValidLetter

@SuppressLint("SetTextI18n")
class MyProductsRecyclerAdapter : ListAdapter<FoodProduct, MyProductsRecyclerViewHolder>(DIFF_CALLBACK) {

    companion object{
        val defaultColor = Color.rgb(253, 245, 230)
        val DIFF_CALLBACK = object :DiffUtil.ItemCallback<FoodProduct>(){
            override fun areItemsTheSame(oldItem: FoodProduct, newItem: FoodProduct): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodProduct, newItem: FoodProduct): Boolean {
                return oldItem.name == newItem.name && oldItem.energy == newItem.energy
                        && oldItem.protein == newItem.protein && oldItem.fat == newItem.fat
                        && oldItem.carbs == newItem.carbs
            }

        }
    }

    override fun onBindViewHolder(holder: MyProductsRecyclerViewHolder, position: Int) {
        val product = getItem(position)

        holder.textName.text = product.name
        holder.textCalories.text = product.calories()
            .toInt().toString().plus("\u0020kcal")

        // set Color to image
        holder.image.setColorFilter(defaultColor)

        // set Letter
        val letter = product.name?.get(0).toString().getValidLetter()
        holder.imageLetter.text = letter

    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyProductsRecyclerViewHolder {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_my_meals, parent, false)
        return MyProductsRecyclerViewHolder(holder)
    }



}