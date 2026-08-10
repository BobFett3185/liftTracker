package com.example.lifttracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        WorkoutEntity::class,
        ExerciseEntryEntity::class,
        SetEntryEntity::class,
        SplitDayEntity::class,
        SplitExerciseEntity::class
    ],
    version = 2,
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
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS split_days (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        orderIndex INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS split_exercises (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        splitDayId INTEGER NOT NULL,
                        name TEXT NOT NULL,
                        orderIndex INTEGER NOT NULL,
                        FOREIGN KEY(splitDayId) REFERENCES split_days(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_split_exercises_splitDayId ON split_exercises(splitDayId)")
            }
        }
    }
}
