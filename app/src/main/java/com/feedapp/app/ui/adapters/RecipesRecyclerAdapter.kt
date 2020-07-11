/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.feedapp.app.R
import com.feedapp.app.data.api.models.recipesearch.RecipeResult
import com.feedapp.app.ui.viewholders.RecipeApiViewHolder
import com.feedapp.app.util.StringUtils
import com.feedapp.app.util.spoonacularBaseImageUrl

interface OnSearchLimit {
    fun ifLimitReached(): Boolean
    fun limitReached()
}

class RecipesRecyclerAdapter(
    private val starter: ((Int, String?, String) -> Unit),
    private val requestManager: RequestManager,
    private val onSearchLimit: OnSearchLimit

) : ListAdapter<RecipeResult, RecipeApiViewHolder>(DIFF_CALLBACK) {

    private val stringUtils = StringUtils()

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RecipeResult>() {
            override fun areItemsTheSame(oldItem: RecipeResult, newItem: RecipeResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RecipeResult, newItem: RecipeResult): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }

    override fun onBindViewHolder(holder: RecipeApiViewHolder, position: Int) {
        val result = getItem(position)
        var fullImageUri: String? = null

        holder.run {
            val title = stringUtils.getCorrectRecipeTitle(result.title)
            textTitle.text = title
            val imageLink = result.image
            if (imageLink != null && imageLink.isNotEmpty()) {
                fullImageUri = spoonacularBaseImageUrl.plus(result.image)
                requestManager.load(fullImageUri).into(image)
            } else if (result.imageUrls.isNotEmpty() && result.imageUrls[0].isNotEmpty()) {
                fullImageUri = spoonacularBaseImageUrl.plus(result.imageUrls[0])
                requestManager.load(fullImageUri).into(image)
            }

            mainLayout.setOnClickListener {
                // if limit reached, stop
                if (onSearchLimit.ifLimitReached()) {
                    onSearchLimit.limitReached()
                } else {
                    starter.invoke(result.id, fullImageUri, title)
                }
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeApiViewHolder {
        val holder = LayoutInflater.from(parent.context)
            .inflate(R.layout.vh_recipes_search, parent, false)
        return RecipeApiViewHolder(holder)
    }


}