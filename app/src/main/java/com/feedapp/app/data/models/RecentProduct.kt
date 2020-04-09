package com.feedapp.app.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentProduct")
data class RecentProduct(
    @PrimaryKey(autoGenerate = true)
    val recentId: Int = 0,
    @Embedded
    val product: Product,
    val recentFdcId: Int
)