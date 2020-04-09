package com.feedapp.app.ui.adapters

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feedapp.app.R
import com.feedapp.app.data.api.models.recipedetailed.nn.IngredientX
import com.feedapp.app.data.models.MeasureType
import com.feedapp.app.ui.viewholders.RecipeIngredientViewHolder
import com.feedapp.app.util.StringUtils


class RecipeIngredientAdapter(
    val application: Application,
    private val ingredient: ArrayList<IngredientX>,
    var servings: Int,
    var measureType: MeasureType = MeasureType.METRIC
) :
    RecyclerView.Adapter<RecipeIngredientViewHolder>() {

    private val stringUtils = StringUtils()

    override fun onBindViewHolder(holder: RecipeIngredientViewHolder, position: Int) {
        try {
            val ingredient = ingredient[position]
            holder.textTitle.text = ingredient.name

            // if user use metric system preference
            // convert amount and unit accordingly
            if (measureType == MeasureType.METRIC) ingredient.checkAmountConversion()

            // get total amount
            ingredient.amount *= servings

            holder.textAmount.text = application.getString(R.string.recipes_detailed_amount)
                .format(
                    ingredient.getRoundedAmount(),
                    stringUtils.checkTeaspoonWriting(ingredient.unit)
                )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeIngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_recipes_ingredient, parent, false)
        return RecipeIngredientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ingredient.size
    }

    fun updateList(newList: List<IngredientX>) {
        ingredient.clear()
        ingredient.addAll(newList)
        notifyDataSetChanged()
    }

}





