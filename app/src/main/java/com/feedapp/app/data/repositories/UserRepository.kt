/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.feedapp.app.data.databases.daos.UserDao
import com.feedapp.app.data.interfaces.BasicOperationCallback
import com.feedapp.app.data.interfaces.UserDeleteCallback
import com.feedapp.app.data.models.BasicNutrientType
import com.feedapp.app.data.models.MeasureType
import com.feedapp.app.data.models.calculations.LeftNutrientCalculator
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.models.user.UserLeftValues
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepository @Inject internal constructor(
    private val userDao: UserDao,
    private val leftCalculator: LeftNutrientCalculator
) {

    private val proteinsType = BasicNutrientType.PROTEINS
    private val fatsType = BasicNutrientType.FATS
    private val carbsType = BasicNutrientType.CARBS
    private val caloriesType = BasicNutrientType.CALORIES

    private val _user: MutableLiveData<User?> =
        liveData(IO) { emit(getUserPrepared()) } as MutableLiveData<User?>

    val user: LiveData<User?> get() = _user

    val userLeftValues: MutableLiveData<UserLeftValues> =
        MutableLiveData(UserLeftValues())


    fun insertUser(user: User) = CoroutineScope(IO).launch {
        userDao.insertUser(user)
    }


    /**
     * Saving user's profile to Firebase
     * Used also for updating user's data
     */
    private fun saveUserToFirebase(user: User) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.let {
            val db = Firebase.firestore
            db.collection("users").document(firebaseUser.uid)
                .collection("profile").document("userProfile").set(user)
        }
    }

    /**
     * Search user from local room db, if its null - download from firebase
     */
    private suspend fun getUserPrepared() = coroutineScope {
        userDao.getUser() ?: downloadUserFromFB()
    }


    fun deleteFirebaseAccount(
        userDeleteCallback: UserDeleteCallback
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                userDeleteCallback.onDeletionSuccess()
            }
        }?.addOnFailureListener {
            if (it is FirebaseAuthRecentLoginRequiredException)
                userDeleteCallback.reauthRequired()
            else userDeleteCallback.onDeletionError()
        }
    }


    fun saveMeasure(metricB: Boolean) {
        _user.value?.let { user ->
            val newUser = user.apply {
                measureType = if (metricB) MeasureType.METRIC else MeasureType.US
            }
            userDao.insertUser(newUser)
            saveUserToFirebase(newUser)
            _user.postValue(newUser)
        }
    }


    fun saveIntolerance(list: List<String>) {
        _user.value?.let { user ->
            val newUser = user.apply {
                intolerance = list
            }
            userDao.insertUser(newUser)
            saveUserToFirebase(newUser)
            _user.postValue(newUser)
        }
    }

    fun saveDiet(list: List<String>) {
        _user.value?.let { user ->
            val newUser = user.apply {
                diet = list
            }
            userDao.insertUser(newUser)
            saveUserToFirebase(newUser)
            _user.postValue(newUser)
        }
    }

    fun deleteAllUsers() = CoroutineScope(IO).launch {
        userDao.deleteAllUsers()
    }

    fun saveUser(user: User) {
        insertUser(user)
        saveUserToFirebase(user)
    }

    private suspend fun downloadUserFromFB() = coroutineScope<User?> {
        try {
            getUserProfileFBReference()?.get()?.await()?.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun getUserProfileFBReference(): DocumentReference? {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        return firebaseUser?.let {
            val db = Firebase.firestore
            db.collection("users").document(firebaseUser.uid)
                .collection("profile").document("userProfile")
        }

    }

    suspend fun ifUserExists(): Boolean = coroutineScope {
        try {
            getUserProfileFBReference()?.get()?.await()?.exists()
        } catch (e: Exception) {
            false
        }
    } ?: false


    fun updateLeftValues(day: Day?) {
        day ?: return
        val leftValues = UserLeftValues()
        updateCalories(day, leftValues)
        updateProteins(day, leftValues)
        updateCarbsLeft(day, leftValues)
        updateFatsLeft(day, leftValues)
        userLeftValues.postValue(leftValues)

    }


    private fun updateCarbsLeft(
        day: Day,
        leftValues: UserLeftValues
    ) {
        user.value?.let {
            val carbsProgress = leftCalculator.calculateProgress(
                it.carbsNeeded,
                day,
                carbsType
            )
            val carbsAmount = leftCalculator.calculateAmount(
                it.carbsNeeded,
                day,
                carbsType
            )
            leftValues.carbsLeft = Triple(carbsAmount.first, carbsAmount.second, carbsProgress)

        }
    }

    private fun updateProteins(
        day: Day,
        leftValues: UserLeftValues
    ) {
        user.value?.let {
            val proteinsProgress = leftCalculator.calculateProgress(
                it.proteinsNeeded,
                day,
                proteinsType
            )
            val proteinsAmount = leftCalculator.calculateAmount(
                it.proteinsNeeded,
                day,
                proteinsType
            )
            leftValues.proteinsLeft =
                Triple(proteinsAmount.first, proteinsAmount.second, proteinsProgress)
        }
    }

    private fun updateFatsLeft(
        day: Day,
        leftValues: UserLeftValues
    ) {
        user.value?.let {
            val fatsProgress = leftCalculator.calculateProgress(
                it.fatsNeeded,
                day,
                fatsType
            )
            val fatsAmount = leftCalculator.calculateAmount(
                it.fatsNeeded,
                day,
                fatsType
            )
            leftValues.fatsLeft = Triple(fatsAmount.first, fatsAmount.second, fatsProgress)

        }
    }

    private fun updateCalories(
        day: Day,
        leftValues: UserLeftValues
    ) {
        user.value?.let {
            val calProgress = leftCalculator.calculateProgress(
                it.caloriesNeeded,
                day,
                caloriesType
            )
            val calAmount = leftCalculator.calculateAmount(
                it.caloriesNeeded,
                day,
                caloriesType
            )
            leftValues.calories = Triple(calAmount.first, calAmount.second, calProgress)
        }

    }

    /**
     * check if string nutrient value is valid
     */
    private fun isNewNutrientValueValid(newValueToSave: String): Boolean {
        for (c in newValueToSave) if (!c.isDigit()) return false
        if (newValueToSave.toInt() !in 1..10000) return false
        return true
    }


    /**
     * Updates nutrient value for user in local and firebase
     */
    suspend fun updateNutrient(
        newValueToSave: String,
        type: BasicNutrientType,
        callback: BasicOperationCallback? = null
    ) = coroutineScope {
        try {

            // check value
            if (!isNewNutrientValueValid(newValueToSave)) throw IllegalArgumentException()

            val newValue = newValueToSave.toInt()

            _user.value?.let {
                when (type) {
                    BasicNutrientType.CALORIES -> {
                        val newUser = it.apply { caloriesNeeded = newValue }
                        _user.postValue(newUser)
                    }
                    BasicNutrientType.PROTEINS -> {
                        val newUser = it.apply { proteinsNeeded = newValue }
                        _user.postValue(newUser)

                    }
                    BasicNutrientType.FATS -> {
                        val newUser = it.apply { fatsNeeded = newValue }
                        _user.postValue(newUser)

                    }
                    BasicNutrientType.CARBS -> {
                        val newUser = it.apply { carbsNeeded = newValue }
                        _user.postValue(newUser)

                    }
                }
                callback?.onSuccess()
                userDao.update(it)
                saveUserToFirebase(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback?.onFailure()
        }
    }
}