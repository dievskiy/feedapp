/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.viewclasses

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

class ViewPagerTransformer : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.apply {
            scaleX = 1f
            scaleY = 1f
        }
        when {
            position < -1 ->
                page.alpha = 0.1f
            position <= 1 -> {
                page.alpha = max(0f, 1f - abs(position))
            }
            position == 1f ->{
                page.alpha = 1f
            }
            else -> page.alpha = 0.2f
        }
    }
}