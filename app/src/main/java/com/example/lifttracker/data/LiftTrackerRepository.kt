package com.example.lifttracker.data

class LiftTrackerRepository(private val dao: LiftTrackerDao) {
    fun getWorkouts() = dao.getWorkouts()

    fun getWorkoutHistory() = dao.getWorkoutHistory()

    fun getWorkoutById(workoutId: Long) = dao.getWorkoutByIdFlow(workoutId)

    fun getExerciseNames() = dao.getExerciseNames()

    fun getProgressForExercise(exerciseName: String) = dao.getProgressForExercise(exerciseName)

    suspend fun createWorkout(title: String, date: String, notes: String = ""): Long {
        val workoutId = dao.insertWorkout(WorkoutEntity(date = date, title = title, notes = notes))
        return workoutId
    }

    suspend fun deleteWorkout(workoutId: Long) {
        dao.deleteWorkout(workoutId)
    }

    suspend fun addExercise(workoutId: Long, name: String): Long {
        val orderIndex = dao.getExerciseCount(workoutId)
        val exerciseId = dao.insertExercise(
            ExerciseEntryEntity(workoutId = workoutId, name = name, orderIndex = orderIndex)
        )
        return exerciseId
    }

    suspend fun addSet(exerciseId: Long, reps: Int, weight: Double): Long {
        val setNumber = dao.getSetCount(exerciseId) + 1
        return dao.insertSet(SetEntryEntity(exerciseId = exerciseId, setNumber = setNumber, reps = reps, weight = weight))
    }

    suspend fun addExerciseWithSet(workoutId: Long, name: String, reps: Int, weight: Double): Long {
        val exerciseId = addExercise(workoutId, name)
        addSet(exerciseId, reps, weight)
        return exerciseId
    }
}
