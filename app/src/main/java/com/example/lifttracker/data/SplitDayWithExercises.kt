package com.example.lifttracker.data

import androidx.room.Embedded
import androidx.room.Relation

data class SplitDayWithExercises(
    @Embedded val splitDay: SplitDayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "splitDayId"
    )
    val exercises: List<SplitExerciseEntity>
)
