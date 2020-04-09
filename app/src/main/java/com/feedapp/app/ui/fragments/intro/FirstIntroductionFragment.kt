/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.data.models.user.UserGoal
import com.feedapp.app.viewModels.IntroductionViewModel
import com.github.paolorotolo.appintro.ISlidePolicy
import kotlinx.android.synthetic.main.fragment_intro_first.*

@Suppress("DEPRECATION")
class FirstIntroductionFragment : Fragment(), ISlidePolicy {

    override fun isPolicyRespected(): Boolean {
        return false
    }

    override fun onUserIllegallyRequestedNextPage() {
        return
    }

    companion object {
        fun newInstance() =
            FirstIntroductionFragment()
    }

    private lateinit var introViewModel: IntroductionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container!!.setBackgroundColor(resources.getColor(R.color.white))
        activity?.let {
            introViewModel = ViewModelProvider(it).get(IntroductionViewModel::class.java)
        }

        return inflater.inflate(R.layout.fragment_intro_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {
        intro_eat_healthier.setOnClickListener {
            introViewModel.chooseGoal(UserGoal.EAT_HEALTHIER)
        }
        intro_lose_weight.setOnClickListener {
            introViewModel.chooseGoal(UserGoal.LOSE)

        }
        intro_gain_weight.setOnClickListener {
            introViewModel.chooseGoal(UserGoal.GAIN)

        }
        activity?.let { act ->
            introViewModel.progressBar.observe(act, Observer { event ->
                event.getContentIfNotHandled()?.let {
                        if (event.peekContent())
                            activity_intro_progressBar.visibility = View.VISIBLE
                        else activity_intro_progressBar.visibility = View.GONE
                    }
            })
        }


    }


}
