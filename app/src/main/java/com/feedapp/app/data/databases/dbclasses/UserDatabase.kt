/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.databases.dbclasses

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.feedapp.app.data.databases.converters.Converters
import com.feedapp.app.data.databases.daos.DayDao
import com.feedapp.app.data.databases.daos.RecentDao
import com.feedapp.app.data.databases.daos.UserDao
import com.feedapp.app.data.models.FoodProduct
import com.feedapp.app.data.models.Product
import com.feedapp.app.data.models.RecentProduct
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.day.Meal
import com.feedapp.app.data.models.user.User


@Database(
    entities = [User::class, Meal::class, FoodProduct::class, Product::class, Day::class, RecentProduct::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
    abstract fun getDayDao(): DayDao
    abstract fun getRecentDao(): RecentDao

    companion object {
        @Volatile
        private var instance: UserDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also { instance = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, UserDatabase::class.java, "user.db"
        )
            .build()

    }




}