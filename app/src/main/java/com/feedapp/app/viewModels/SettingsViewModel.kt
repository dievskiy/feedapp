/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.viewModels

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
import com.feedapp.app.data.models.localdb.LocalDBUrls
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.models.user.UserDeleteOperation
import com.feedapp.app.data.repositories.RecentDelegate
import com.feedapp.app.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.inject.Inject

enum class Operation {
    SUCCESS, FAILED
}

class SettingsViewModel @Inject internal constructor(
    private val userRepository: UserRepository,
    private val recentDelegate: RecentDelegate
) : ViewModel() {

    val status = MutableLiveData(DataResponseStatus.NONE)
    var user: LiveData<User?> = userRepository.user

    private val _toastDatabase = MutableLiveData<Event<Operation>>()
    val toastDatabase: LiveData<Event<Operation>> get() = _toastDatabase

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

    fun deleteAllData(listener: UserDeleteCallback) = viewModelScope.launch(IO) {
        try {
            userRepository.deleteFirebaseAccount(object :
                UserDeleteCallback {
                override fun onDeletionSuccess() {
                    listener.onDeletionSuccess()
                }

                override fun onDeletionError() {
                    listener.onDeletionError()
                    toastDeletion(UserDeleteOperation.FAILED)
                }

                override fun reauthRequired() {
                    toastDeletion(UserDeleteOperation.REAUTH)
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

    fun deleteRecentProducts() = viewModelScope.launch(IO) {
        recentDelegate.deleteRecentProducts()
    }

    fun getNutrientValue(nutrient: BasicNutrientType): String {
        return when (nutrient) {
            PROTEINS -> user.value?.proteinsNeeded.toString()
            FATS -> user.value?.fatsNeeded.toString()
            CARBS -> user.value?.carbsNeeded.toString()
            CALORIES -> user.value?.caloriesNeeded.toString()
        }
    }


    fun downloadDatabase(
        filePath: String,
        code: String,
        listener: BasicOperationCallback
    ) = viewModelScope.launch(IO) {
        fun download(link: String, path: String) {
            try {
                URL(link).openStream().use { input ->
                    FileOutputStream(File(path)).use { output ->
                        input.copyTo(output)
                        _toastDatabase.postValue(Event(Operation.SUCCESS))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _toastDatabase.postValue(Event(Operation.FAILED))
                listener.onFailure()
            }
        }

        LocalDBUrls.getURLByCode(code)?.let { download(it, filePath) }
    }

}
