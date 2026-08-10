package com.example.lifttracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "split_exercises",
    foreignKeys = [
        ForeignKey(
            entity = SplitDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["splitDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("splitDayId")]
)
data class SplitExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val splitDayId: Long,
    val name: String,
    val orderIndex: Int
)
