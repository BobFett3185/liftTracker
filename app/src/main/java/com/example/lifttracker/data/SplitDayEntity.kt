package com.example.lifttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "split_days")
data class SplitDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val orderIndex: Int
)
