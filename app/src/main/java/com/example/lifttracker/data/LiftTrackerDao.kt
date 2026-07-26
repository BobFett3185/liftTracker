package com.example.lifttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LiftTrackerDao {
    @Insert
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntryEntity): Long

    @Insert
    suspend fun insertSet(set: SetEntryEntity): Long

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getWorkouts(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getWorkoutHistory(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Long): WorkoutWithExercises?

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    fun getWorkoutByIdFlow(workoutId: Long): Flow<WorkoutWithExercises?>

    @Query("SELECT COUNT(*) FROM exercise_entries WHERE workoutId = :workoutId")
    suspend fun getExerciseCount(workoutId: Long): Int

    @Query("SELECT COUNT(*) FROM set_entries WHERE exerciseId = :exerciseId")
    suspend fun getSetCount(exerciseId: Long): Int

    @Query("SELECT DISTINCT name FROM exercise_entries ORDER BY name ASC")
    fun getExerciseNames(): Flow<List<String>>

    @Query(
        """
        SELECT workouts.id AS workoutId, workouts.date AS date, exercise_entries.name AS exerciseName,
               set_entries.weight AS weight, set_entries.reps AS reps
        FROM set_entries
        INNER JOIN exercise_entries ON set_entries.exerciseId = exercise_entries.id
        INNER JOIN workouts ON exercise_entries.workoutId = workouts.id
        WHERE exercise_entries.name = :exerciseName
        ORDER BY workouts.date ASC, set_entries.id ASC
        """
    )
    fun getProgressForExercise(exerciseName: String): Flow<List<ExerciseProgressPoint>>
}
