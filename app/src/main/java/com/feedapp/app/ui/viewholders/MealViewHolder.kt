/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R

class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textTitle: TextView = itemView.findViewById(R.id.meal_api_title)
    val mainLayout: RelativeLayout = itemView.findViewById(R.id.meal_api_main)
    val image: ImageView = itemView.findViewById(R.id.meal_api_image)
    val imageLetter: TextView = itemView.findViewById(R.id.meal_api_image_letter)

}
