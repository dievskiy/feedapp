/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.intro


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feedapp.app.R
import com.feedapp.app.ui.activities.HomeActivity
import com.feedapp.app.viewModels.IntroductionViewModel
import com.github.paolorotolo.appintro.ISlidePolicy
import kotlinx.android.synthetic.main.fragment_intro_third.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ThirdIntroductionFragment : Fragment(), ISlidePolicy {
    private lateinit var introViewModel: IntroductionViewModel

    override fun onUserIllegallyRequestedNextPage() {
        return
    }

    override fun isPolicyRespected(): Boolean {
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            introViewModel = ViewModelProvider(it).get(IntroductionViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_intro_third, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImage()
        setViewListeners()
    }


    private fun setViewListeners() {
        val activityIntent = Intent(requireActivity(), HomeActivity::class.java)
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse("https://sites.google.com/view/feedapp")

        intro_third_continue.setOnClickListener {
            try {
                // prevent starting 2 activities
                it.isClickable = false
                CoroutineScope(IO).launch {
                    introViewModel.saveUser()
                    withContext(Main) {
                        startActivity(activityIntent)
                    }
                }


            } catch (e: Exception) {
                it.isClickable = true
                e.printStackTrace()
            }
        }

        intro_privacy.setOnClickListener {
            startActivity(browserIntent)
        }

    }


    private fun loadImage() {
        Glide.with(this).load(Uri.parse("file:///android_asset/images/intro.jpg"))
            .centerCrop()
            .apply(RequestOptions.circleCropTransform())
            .into(intro_third_image)

    }

    companion object {
        fun newInstance() =
            ThirdIntroductionFragment()
    }
}
