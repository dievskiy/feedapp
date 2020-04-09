package com.feedapp.app.ui.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R

class DayRecyclerViewHolder(
    itemView: View
):
    RecyclerView.ViewHolder(itemView){
    val textTitle:TextView = itemView.findViewById(R.id.item_title)
    val textTotal:TextView = itemView.findViewById(R.id.item_total)
    val rv:RecyclerView = itemView.findViewById(R.id.day_item_rv)

}
