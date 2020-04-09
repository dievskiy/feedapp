package com.feedapp.app.ui.activities

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.feedapp.app.R
import dagger.android.support.DaggerAppCompatActivity

abstract class ClassicActivity: DaggerAppCompatActivity(){

    fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }else{
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }


}