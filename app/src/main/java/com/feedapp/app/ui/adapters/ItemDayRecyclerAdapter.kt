package com.feedapp.app.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R
import com.feedapp.app.data.models.Product
import com.feedapp.app.ui.viewholders.DayItemViewHolder
import kotlin.math.roundToInt


class ItemDayRecyclerAdapter(val products: ArrayList<Product>) :
    RecyclerView.Adapter<DayItemViewHolder>() {

    override fun onBindViewHolder(holder: DayItemViewHolder, position: Int) {
        try {
            val product = products[position]
            holder.textTitle.text = product.name
            holder.textCalories.text =
                holder.textCalories.context.getString(
                    R.string.day_item_kcal,
                    product.consumedCalories.roundToInt()
                )
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.vh_day_item, parent, false)
        return DayItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

}
