package com.feedapp.app.ui.viewclasses

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView


class ClassicItemDecoration(context: Context) :
    DividerItemDecoration(context, VERTICAL) {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
    }

}
