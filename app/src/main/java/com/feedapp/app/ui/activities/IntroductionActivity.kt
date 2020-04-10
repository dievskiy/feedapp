/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.ui.fragments.intro.FirstIntroductionFragment
import com.feedapp.app.ui.fragments.intro.SecIntro
import com.feedapp.app.ui.fragments.intro.ThirdIntroductionFragment
import com.feedapp.app.viewModels.IntroductionViewModel
import com.firebase.ui.auth.AuthUI
import com.github.paolorotolo.appintro.AppIntro
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Suppress("DEPRECATION")
class IntroductionActivity : AppIntro() {

    @Inject
    lateinit var modelFactory: ViewModelProvider.Factory
    private val introViewModel by lazy {
        ViewModelProvider(this, modelFactory).get(IntroductionViewModel::class.java)
    }

    private val RC_SIGN_IN = 103;


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setIntroSettings()
        setObservers()
        setStatusBar()
        logIn()

    }

    private fun logIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false, false)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.icon)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun setStatusBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }
    }

    private fun setIntroSettings() {
        addSlide(FirstIntroductionFragment.newInstance())
        addSlide(SecIntro.newInstance())
        addSlide(ThirdIntroductionFragment.newInstance())

        showSkipButton(false)
        isProgressButtonEnabled = false
        setBarColor(resources.getColor(android.R.color.transparent))
        showSeparator(false)
        setIndicatorColor(
            resources.getColor(R.color.colorPrimary400),
            resources.getColor(R.color.colorPrimary800)
        )
        setFadeAnimation()
    }


    private fun setObservers() {

        introViewModel.goal.observe(this, Observer {
            it?.let {
                pager.goToNextSlide()
            }
        })

        introViewModel.applied.observe(this, Observer {
            if (it) {
                pager.goToNextSlide()

            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    checkUser()
                } else {
                    handleLoginBackButton()
                }
            }
        }
    }

    /**
     * Checking if user exists in firebase database
     * if so, skip introduction and open HomeActivity
     *
     */
    private fun checkUser() {
        // show progress bar
        introViewModel.showProgressBar(true)
        checkIfUserExists().invokeOnCompletion {
            introViewModel.showProgressBar(false)
        }

    }

    private fun checkIfUserExists() =
        CoroutineScope(IO).launch {
            if (introViewModel.ifUserExists()) {
                // if has been already registered, skip introduction
                withContext(Main) { pager.setCurrentItem(3, true) }
            }
        }

    override fun onBackPressed() {
        return
    }

    private fun handleLoginBackButton() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.login))
            .setMessage(getString(R.string.dialog_firebase_login_error))
            .setPositiveButton(R.string.ok) { _, _ ->
                logIn()
            }
            .setCancelable(false)
            .show()
    }

}
