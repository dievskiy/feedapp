/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.repositories

import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.feedapp.app.data.models.calculations.LeftNutrientCalculator
import com.feedapp.app.data.models.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
internal class UserRepositoryTest2 {


    @Test
    fun testAll() {
        CoroutineScope(IO).launch {
            val userDao = com.feedapp.app.data.databases.dbclasses.UserDatabase.invoke(
                InstrumentationRegistry.getInstrumentation().context
            ).getUserDao()
            val repository = UserRepository(userDao,
                LeftNutrientCalculator()
            )

            insertUserTest(repository)
            deleteUserTest(repository)
        }

    }


    private suspend fun insertUserTest(userRepository: UserRepository) {
        val user =
            User(uid = 0, caloriesNeeded = 111)
        userRepository.insertUser(user)
        delay(200L)
        val userTest = userRepository.user.value?.caloriesNeeded
        println(userRepository.user.value)
        println(user.caloriesNeeded)
        assert(user.caloriesNeeded == userTest)
    }

    private suspend fun deleteUserTest(repository: UserRepository) {
        val user =
            User(
                uid = 0,
                caloriesNeeded = 2100
            )
        val user2 =
            User(
                uid = 1,
                caloriesNeeded = 411
            )
        val user3 =
            User(
                uid = 2,
                caloriesNeeded = 9491
            )
        repository.insertUser(user)
        repository.insertUser(user2)
        repository.insertUser(user3)
        repository.deleteAllUsers()
        val userAfter = repository.user.value
        Assert.assertEquals(userAfter, null)
    }

}