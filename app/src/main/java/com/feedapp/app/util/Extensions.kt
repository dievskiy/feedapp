/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.feedapp.app.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showDialog(
    view: View?,
    title: String,
    message: String?,
    positiveText: Int,
    negativeText: Int,
    okListener: DialogInterface.OnClickListener,
    cancelListener: DialogInterface.OnClickListener?
) {
    AlertDialog.Builder(this)
        .setView(view)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText, okListener)
        .setNegativeButton(negativeText, cancelListener)
        .show()
}

fun String.getValidLetter(): String {
    if (this.isEmpty()) return "K"
    val letter = this.get(0).toString()
    val regex = "[^a-zA-Z]".toRegex()
    val newLetter = letter.toUpperCase(Locale.getDefault()).replace(regex, "K")
    if (newLetter == "Q") return "O"
    if (newLetter == "W") return "V"
    return newLetter
}


fun caloriesToEnergy(calories: Float): Float {
    return calories / ENERGY_TO_CALORIES_MULTIPLICATOR
}

fun String.containsNumbers(): Boolean {
    // check whether there is number(s) in string
    var contains = false
    this.forEach { if (it.isDigit()) contains = true }
    return contains
}

fun String.toFloatOrZero(): Float {
    // convert it to float if possible or return 0
    return if (this.containsNumbers()) this.toFloatOrNull() ?: 0f
    else 0f
}

fun View.hideKeyboard(context: Context) {
    try {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.hideKeyboard() {
    try {
        val view = this.findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.dpToPx(dp: Float): Int = context.dpToPx(dp)
fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    if (layoutParams is T) block(layoutParams as T)
}

fun Context.isConnected() = run {
    val conManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val ni = conManager.activeNetworkInfo
    ni != null && ni.isConnected
}

fun View.setMargins(
    leftMarginDp: Int? = null,
    topMarginDp: Int? = null,
    rightMarginDp: Int? = null,
    bottomMarginDp: Int? = null
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        leftMarginDp?.run { params.leftMargin = this.dpToPx(context) }
        topMarginDp?.run { params.topMargin = this.dpToPx(context) }
        rightMarginDp?.run { params.rightMargin = this.dpToPx(context) }
        bottomMarginDp?.run { params.bottomMargin = this.dpToPx(context) }
        requestLayout()
    }
}


/*
 * converts dp to pixels
 */
fun Int.dpToPx(context: Context?): Int {
    if (context == null) return this
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), metrics).toInt()
}

// round float to decimals
fun Float.round(decimals: Int): Float {
    return BigDecimal(this.toDouble()).setScale(decimals, RoundingMode.HALF_EVEN).toFloat()
}


