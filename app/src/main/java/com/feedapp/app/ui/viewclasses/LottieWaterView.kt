/* * Copyright (c) 2020 Ruslan Potekhin */package com.feedapp.app.ui.viewclassesimport android.content.Contextimport android.util.AttributeSetimport com.airbnb.lottie.LottieAnimationViewinterface WaterModifier {    fun setWater(newWater: Int)}class LottieWaterView constructor(context: Context, attrs: AttributeSet? = null) :    LottieAnimationView(context, attrs) {    fun isAnimationOver(): Boolean = progress > 0.98f    fun fillWater() {        if (canFill()) progress = 1f    }    // if the view has not been filled yet    fun canFill(): Boolean = !isAnimationOver() && !isAnimating    fun reset() {        cancelAnimation()        progress = 0f    }    fun fill() {        playAnimation()    }}