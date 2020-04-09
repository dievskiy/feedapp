/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.feedapp.app.data.interfaces.BasicOperationCallback
import com.feedapp.app.data.interfaces.UserDeleteCallback
import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.data.models.BasicNutrientType.*
import com.feedapp.app.data.models.DataResponseStatus
import com.feedapp.app.data.models.Event
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.models.user.UserDeleteOperation
import com.feedapp.app.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingsViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val sp: SharedPreferences
) : ViewModel(), UserDeleteCallback {

    var context: Context? = null

    val status = MutableLiveData(DataResponseStatus.NONE)

    private val spCautionName = "ShouldShowCautionDialog"

    var user: LiveData<User?> = userRepository.user

    private val toastDelete = MutableLiveData<Event<UserDeleteOperation>>()

    private val firebaseReauth = MutableLiveData<Event<Boolean>>()

    val toast: LiveData<Event<UserDeleteOperation>>
        get() = toastDelete


    val reauth: LiveData<Event<Boolean>>
        get() = firebaseReauth


    fun saveNewValue(newValueToSave: String, type: BasicNutrientType) =
        viewModelScope.launch(IO) {
            user.value?.let {
                val callback = object : BasicOperationCallback {
                    override fun onSuccess() {
                        status.postValue(DataResponseStatus.SUCCESS)
                    }

                    override fun onFailure() {
                        status.postValue(DataResponseStatus.FAILED)
                    }
                }
                userRepository.updateNutrient(newValueToSave, type, callback)
            }
        }

    fun saveMeasure(metric: Boolean) = viewModelScope.launch(IO) {
        userRepository.saveMeasure(metric)
        status.postValue(DataResponseStatus.SUCCESS)
    }


    override fun onDeletionSuccess() {
        // if firebase acc deleted, delete also all local data
        try {
            context ?: throw java.lang.Exception()
            (context!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .clearApplicationUserData()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            onDeletionError()
        }
    }

    override fun onDeletionError() {
        toastDeletion(UserDeleteOperation.FAILED)
    }

    override fun reauthRequired() {
        toastDeletion(UserDeleteOperation.REAUTH)
    }

    fun deleteAllData() = viewModelScope.launch(IO) {
        try {
            userRepository.deleteFirebaseAccount(object :
                UserDeleteCallback {
                override fun onDeletionSuccess() {
                    this@SettingsViewModel.onDeletionSuccess()
                }

                override fun onDeletionError() {
                    this@SettingsViewModel.onDeletionError()
                }

                override fun reauthRequired() {
                    this@SettingsViewModel.reauthRequired()
                    firebaseReauth(true)
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
            toastDeletion(UserDeleteOperation.FAILED)
        }
    }

    private fun toastDeletion(itemId: UserDeleteOperation) {
        toastDelete.value = Event(itemId)
    }

    private fun firebaseReauth(itemId: Boolean) {
        firebaseReauth.value = Event(itemId)
    }

    fun saveIntolerance(intoleranceList: List<String>) {
        viewModelScope.launch(IO) {
            userRepository.saveIntolerance(intoleranceList)
            status.postValue(DataResponseStatus.SUCCESS)
        }
    }

    fun saveDiet(dietList: List<String>) {
        viewModelScope.launch(IO) {
            userRepository.saveDiet(dietList)
            status.postValue(DataResponseStatus.SUCCESS)
        }
    }

    fun shouldShowCautionDialog(): Boolean = sp.getBoolean(spCautionName, true)

    fun saveShowedCautionDialog() = sp.edit().putBoolean(spCautionName, false).apply()

    fun getNutrientValue(nutrient: BasicNutrientType): String {
        return when (nutrient) {
            PROTEINS -> user.value?.proteinsNeeded.toString()
            FATS -> user.value?.fatsNeeded.toString()
            CARBS -> user.value?.carbsNeeded.toString()
            CALORIES -> user.value?.caloriesNeeded.toString()
        }
    }


}
