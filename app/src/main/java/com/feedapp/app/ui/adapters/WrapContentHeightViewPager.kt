package com.feedapp.app.ui.adapters

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class WrapContentHeightViewPager : ViewPager {
    /**
     * Constructor
     *
     * @param context the context
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec2 = heightMeasureSpec
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
        // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
        // super has to be called in the beginning so the child views can be initialized.
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            var height = 0
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
                val h = child.measuredHeight
                if (h > height) height = h
            }
            heightMeasureSpec2 = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        // super has to be called again so the new specs are treated as exact measurements
        super.onMeasure(widthMeasureSpec, heightMeasureSpec2)
    }

}