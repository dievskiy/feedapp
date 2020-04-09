package com.feedapp.app.data.databases.daos

import androidx.room.*
import com.feedapp.app.data.models.user.User
import com.feedapp.app.data.models.MeasureType
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.RecentProduct


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

    @Query("select * from recentProduct order by recentId desc limit :limit ")
    fun getRecentProducts(limit: Int = 5): List<RecentProduct>

    @Query("select measureType from user where uid == 0")
    fun getMeasure(): MeasureType?

}