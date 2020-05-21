/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.databases.daos

import androidx.room.*
import com.feedapp.app.data.models.MeasureType
import com.feedapp.app.data.models.RecentProduct
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

    @Query("select proteinsNeeded from user where uid == 0")
    fun getProteins(): Int?

    @Query("select carbsNeeded from user where uid == 0")
    fun getCarbs(): Int?

    @Query("select fatsNeeded from user where uid == 0")
    fun getFats(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentProducts(recentProduct: RecentProduct)

    /**
     * return 10 recent products
     */
    @Query("select * from recentProducts order by recentId desc limit 10")
    fun getRecentProducts(): List<RecentProduct>?

    @Query("select measureType from user where uid == 0")
    fun getMeasure(): MeasureType?

    @Query("select count(rowid) from recentProducts")
    fun getNumRecent(): Int

    @Query("delete from recentProducts where recentId == 0")
    fun deleteFirstRecent()

}