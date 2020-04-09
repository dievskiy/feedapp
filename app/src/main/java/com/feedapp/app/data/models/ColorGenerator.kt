/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models

import android.graphics.Color
import kotlin.random.Random

class ColorGenerator {

    companion object {
        const val defaultAlpha = 15
    }

    /**
     * generate random color
     */
    fun getColorWithAlpha(): Int {
        return Color.argb(
            defaultAlpha, Random.nextInt(256),
            Random.nextInt(256), Random.nextInt(256)
        )
    }

    fun generateColor(size: Int): MutableList<Int> {
        val list = arrayListOf<Int>()
        for (i in 0 until size) {
            list.add(colors.random())
        }
        return list
    }

    private val colors = listOf(
        Color.rgb(255, 140, 0),
        Color.rgb(255, 127, 80),
        Color.rgb(255, 160, 122),
        Color.rgb(255, 165, 0),
        Color.rgb(255, 215, 0),
        Color.rgb(218, 165, 32),
        Color.rgb(238, 232, 170),
        Color.rgb(240, 230, 140),
        Color.rgb(154, 205, 50),
        Color.rgb(34, 139, 34),
        Color.rgb(144, 238, 144),
        Color.rgb(224, 255, 255),
        Color.rgb(105, 206, 235)
    )

}