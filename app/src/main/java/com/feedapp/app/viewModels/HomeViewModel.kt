/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import androidx.lifecycle.*
import com.feedapp.app.data.models.FragmentNavigationType
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.prefs.SharedPrefsHelper
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.models.user.UserLeftValues
import com.feedapp.app.data.repositories.DayRepository
import com.feedapp.app.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 * ViewModel is shared by HomeActivity, HomeFragment, HomeDownFragment, HomeUpFragment and
 * DayFragment
 */


class HomeViewModel @Inject internal constructor(
    private val dayRepository: DayRepository,
    private val userRepository: UserRepository,
    private val spHelper: SharedPrefsHelper
) : ViewModel() {

    val isResettingDateOrSwiping = MutableLiveData(false)

    val currentDay: LiveData<Day> = dayRepository.currentDay

    val currentBottomPosition = MutableLiveData(FragmentNavigationType.HOME)

    val currentPosition: LiveData<Int> = dayRepository.currentPosition

    val user: LiveData<User?> = userRepository.user

    val userLeftValues: LiveData<UserLeftValues> = userRepository.userLeftValues

    fun introShowed() = spHelper.isIntroShowed()

    // update left values automatically when user is changed
    val userData = Transformations.map(user) {
        it?.let {
            userRepository.updateLeftValues(currentDay.value)
        }
    }

    val dayData = Transformations.map(currentDay) {
        it?.let {
            userRepository.updateLeftValues(it)
            dayRepository.checkFirebaseVersion(it)
        }
    }


    fun updateDayAndDate(position: Int? = null) {
        isResettingDateOrSwiping?.value?.let {
            if (it) return
        } ?: return
        viewModelScope.launch(IO) {
            dayRepository.updateDay(position)
        }
    }


    fun resetDate() {
        dayRepository.resetDate()
    }

    fun saveHomeUiGuideShowed() =
        spHelper.saveHomeUiGuideShowed()


    fun isHomeGuideShowed(): Boolean = spHelper.isHomeGuideShowed()

    // get date to display in UI
    fun getDateText(): CharSequence =
        "${currentDay.value?.date?.day}.${currentDay.value?.date?.month}"

    fun setWater(i: Int) {
        dayRepository.updateWaterNum(i)
    }


}


