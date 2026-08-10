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

    @Insert
    suspend fun insertSplitDay(splitDay: SplitDayEntity): Long

    @Insert
    suspend fun insertSplitExercise(splitExercise: SplitExerciseEntity): Long

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: Long)

    @Query("UPDATE exercise_entries SET name = :name WHERE id = :exerciseId")
    suspend fun renameExercise(exerciseId: Long, name: String)

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

    @Transaction
    @Query("SELECT * FROM exercise_entries WHERE id = :exerciseId")
    fun getExerciseByIdFlow(exerciseId: Long): Flow<ExerciseWithSets?>

    @Query("SELECT COUNT(*) FROM exercise_entries WHERE workoutId = :workoutId")
    suspend fun getExerciseCount(workoutId: Long): Int

    @Query("SELECT COUNT(*) FROM set_entries WHERE exerciseId = :exerciseId")
    suspend fun getSetCount(exerciseId: Long): Int

    @Query("SELECT COUNT(*) FROM split_days")
    suspend fun getSplitDayCount(): Int

    @Query("SELECT COUNT(*) FROM split_exercises WHERE splitDayId = :splitDayId")
    suspend fun getSplitExerciseCount(splitDayId: Long): Int

    @Transaction
    @Query("SELECT * FROM split_days ORDER BY orderIndex ASC, id ASC")
    fun getSplitDays(): Flow<List<SplitDayWithExercises>>

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
