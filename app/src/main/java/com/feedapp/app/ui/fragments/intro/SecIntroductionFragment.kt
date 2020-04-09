/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.ui.fragments.intro

import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.feedapp.app.R
import com.feedapp.app.util.hideKeyboard
import com.feedapp.app.util.setMargins
import com.feedapp.app.util.toastLong
import com.feedapp.app.viewModels.IntroductionViewModel
import com.github.paolorotolo.appintro.ISlidePolicy
import kotlinx.android.synthetic.main.fragment_intro_sec.*


class SecIntro : Fragment(), ISlidePolicy {
    private lateinit var introViewModel: IntroductionViewModel

    override fun isPolicyRespected(): Boolean {
        return false
    }

    override fun onUserIllegallyRequestedNextPage() {
        return
    }

    private fun isVisible(view: View?): Boolean {
        if (view == null) {
            return false
        }
        if (!view.isShown) {
            return false
        }
        val actualPosition = Rect()
        view.getGlobalVisibleRect(actualPosition)
        val displayMetrics = DisplayMetrics()
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val screen =
            Rect(0, 0, width, height)
        return actualPosition.intersect(screen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.let {
            introViewModel = ViewModelProvider(it).get(IntroductionViewModel::class.java)
        }
        return inflater.inflate(R.layout.fragment_intro_sec, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isVisible(view.findViewById(R.id.intro_sec_continue))) {
            intro_sec_continue.setMargins(topMarginDp = 30)
        }
        setObservers()
        try {
            setListeners(view)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setObservers() {
        activity?.let {
            introViewModel.valuesInvalid.observe(it, Observer { event ->
                event.getContentIfNotHandled()
                    ?.let { // Only proceed if the event has never been handled
                        if (!event.peekContent()) activity?.toastLong(getString(R.string.incorrect_data))
                    }
            })
        }
    }

    private fun setListeners(view: View) {
        val adapter = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.Exercises)
        )
        val spinnerExc = view.findViewById<AutoCompleteTextView>(R.id.filled_exposed_dropdown)
        spinnerExc.setAdapter(adapter)
        spinnerExc.setOnItemClickListener { _, _, position, _ ->
            introViewModel.setActivityLevel(position)
            introViewModel.setActivityChosen(true)
            activity?.hideKeyboard()
            unblockButton()
        }
        val adapter2 = ArrayAdapter(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            resources.getStringArray(R.array.Sex)
        )
        val spinnerSex = view.findViewById<AutoCompleteTextView>(R.id.intro_sex)
        spinnerSex.setAdapter(adapter2)
        spinnerSex.setOnItemClickListener { _, _, position, _ ->
            activity?.hideKeyboard()
            introViewModel.sexValue(position == 0)
            introViewModel.sexChosenValue(true)
            unblockButton()
        }

        intro_sec_continue.setOnClickListener {
            introViewModel.setAppliedValue(true)

        }

        edt_height.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (edt_height.text.length >= 2) {
                    introViewModel.setHeightValue(edt_height.text.toString())
                    unblockButton()
                }
                if (edt_height.text.length == 3) {
                    activity?.hideKeyboard()
                }
            }
        })
        edt_years.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    introViewModel.setAgeValue(edt_years.text.toString())
                    unblockButton()
                }
            }
        })
        edt_weight.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isNotEmpty()) {
                    introViewModel.setWeightValue(edt_weight.text.toString())
                    unblockButton()
                }
            }
        })
    }

    fun unblockButton() {
        if (introViewModel.canUnblockButton(
                edt_height.text.length,
                edt_weight.text.length,
                edt_years.text.isNotEmpty()
            )
        ) {
            if (introViewModel.areHeightWeightInvalid(
                    edt_height.text.toString(),
                    edt_weight.text.toString()
                )
            ) {
                intro_sec_continue.isEnabled = false
                return
            }
            intro_sec_continue.isEnabled = true
        } else if (intro_sec_continue.isEnabled) {
            intro_sec_continue.isEnabled = false
        }
    }

    companion object {
        fun newInstance() = SecIntro()
    }


}
