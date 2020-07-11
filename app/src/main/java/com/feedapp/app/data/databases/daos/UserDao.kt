/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.databases.daos

import androidx.room.*
import com.feedapp.app.data.models.MeasureType
import com.feedapp.app.data.models.user.User

@Dao
interface UserDao {

    @Query("select caloriesNeeded from user where uid == 0")
    fun getCalories(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Update
    fun update(user: User)

    @Query("delete from user")
    fun deleteAllUsers()

    @Query("select * from user where uid == 0")
    fun getUser(): User?


    @Query("select measureType from user where uid == 0")
    fun getMeasure(): MeasureType?



}