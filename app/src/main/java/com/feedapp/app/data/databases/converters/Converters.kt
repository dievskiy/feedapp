/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.databases.converters

import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import androidx.room.TypeConverter
import com.feedapp.app.data.models.*
import com.feedapp.app.data.models.day.Day
import com.feedapp.app.data.models.day.DayDate
import com.feedapp.app.data.models.day.Meal
import com.feedapp.app.data.models.day.MealType
import com.feedapp.app.util.TAG
import com.google.gson.Gson
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.util.*

object Converters {
    // HashSet<String>
    @JvmStatic
    @TypeConverter
    fun fromStringSet(strings: HashSet<String?>?): String? {
        if (strings == null) {
            return null
        }
        val result = StringWriter()
        val json = JsonWriter(result)
        try {
            json.beginArray()
            for (s in strings) {
                json.value(s)
            }
            json.endArray()
            json.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception creating JSON", e)
        }
        return result.toString()
    }

    @JvmStatic
    @TypeConverter
    fun toStringSet(strings: String?): HashSet<String>? {
        if (strings == null) {
            return null
        }
        val reader = StringReader(strings)
        val json = JsonReader(reader)
        val result: HashSet<String> = HashSet()
        try {
            json.beginArray()
            while (json.hasNext()) {
                result.add(json.nextString())
            }
            json.endArray()
        } catch (e: IOException) {
            Log.e(TAG, "Exception parsing JSON", e)
        }
        return result
    }


    // List<String>
    @TypeConverter
    @JvmStatic
    fun StringlistToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    @JvmStatic
    fun StringjsonToList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()

    // MealList
    @TypeConverter
    @JvmStatic

    fun MealListToJson(value: List<Meal>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToMealList(value: String): List<Meal>? {
        val objects = Gson().fromJson(value, Array<Meal>::class.java) as Array<Meal>
        val list = objects.toList()
        return list
    }

    @TypeConverter
    @JvmStatic

    fun toOrdinalMealType(type: MealType): Int = type.ordinal

    @TypeConverter
    @JvmStatic

    fun toEnumFromMealType(ordinal: Int): MealType =
        MealType.values().first { it.ordinal == ordinal }

    // MonthEnum
    @TypeConverter
    @JvmStatic

    fun toOrdinalMonthEnum(type: MonthEnum): Int = type.ordinal

    @TypeConverter
    @JvmStatic

    fun toEnumFromMonthEnum(ordinal: Int): MonthEnum =
        MonthEnum.values().first { it.ordinal == ordinal }

    // measure system
    @TypeConverter
    @JvmStatic

    fun toOrdinalMeasure(type: MeasureType): Int = type.ordinal

    @TypeConverter
    @JvmStatic

    fun toEnumFromMeasure(ordinal: Int): MeasureType =
        MeasureType.values().first { it.ordinal == ordinal }


    // FoodProduct  list
    @TypeConverter
    @JvmStatic

    fun FoodProductListToJson(value: List<FoodProduct>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToFoodProductList(value: String): List<FoodProduct>? {
        val objects = Gson().fromJson(value, Array<FoodProduct>::class.java) as Array<FoodProduct>
        return objects.toList()
    }

    // Product  list
    @TypeConverter
    @JvmStatic

    fun ProductListToJson(value: ArrayList<Product>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToProductList(value: String): ArrayList<Product>? {
        val objects = Gson().fromJson(value, Array<Product>::class.java) as Array<Product>
        return objects.toMutableList() as ArrayList<Product>
    }


    @TypeConverter
    @JvmStatic

    fun RecentProductListToJson(value: ArrayList<RecentProduct>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToRecentProduct(value: String): ArrayList<RecentProduct>? {
        val objects =
            Gson().fromJson(value, Array<RecentProduct>::class.java) as Array<RecentProduct>
        return objects.toMutableList() as ArrayList<RecentProduct>
    }

    // Day Converter
    @TypeConverter
    @JvmStatic

    fun DayListToJson(value: List<Day>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToDayList(value: String): List<Day>? {
        val objects = Gson().fromJson(value, Array<Day>::class.java) as Array<Day>
        return objects.toList()
    }

    // Date converters
    @TypeConverter
    @JvmStatic

    fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter
    @JvmStatic

    fun fromDate(date: Date): Long {
        return date.time;
    }

    // DayDate Converter
    @TypeConverter
    @JvmStatic

    fun DayDateToJson(value: DayDate?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic

    fun jsonToDayDate(value: String): DayDate {
        return Gson().fromJson(value, DayDate::class.java) as DayDate
    }


}
