/*
 * Copyright (c) 2020 Ruslan Potekhin
 */

package com.feedapp.app.data.models.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.feedapp.app.data.models.MeasureType

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = 0,
    @ColumnInfo(name = "caloriesNeeded")
    var caloriesNeeded: Int = 0,
    @ColumnInfo(name = "proteinsNeeded")
    var proteinsNeeded: Int = 0,
    @ColumnInfo(name = "carbsNeeded")
    var carbsNeeded: Int = 0,
    @ColumnInfo(name = "fatsNeeded")
    var fatsNeeded: Int = 0,
    @ColumnInfo(name = "measureType")
    var measureType: MeasureType = MeasureType.METRIC,
    @ColumnInfo(name = "intolerance")
    var intolerance: List<String>? = listOf(),
    @ColumnInfo(name = "diet")
    var diet: List<String>? = listOf()
)