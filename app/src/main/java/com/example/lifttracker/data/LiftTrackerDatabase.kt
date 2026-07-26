package com.example.lifttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WorkoutEntity::class, ExerciseEntryEntity::class, SetEntryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LiftTrackerDatabase : RoomDatabase() {
    abstract fun liftTrackerDao(): LiftTrackerDao

    companion object {
        @Volatile
        private var Instance: LiftTrackerDatabase? = null

        fun getDatabase(context: Context): LiftTrackerDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LiftTrackerDatabase::class.java,
                    "lifttracker.db"
                ).build().also { Instance = it }
            }
        }
    }
}
