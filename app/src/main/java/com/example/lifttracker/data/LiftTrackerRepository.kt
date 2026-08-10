package com.example.lifttracker.data

class LiftTrackerRepository(private val dao: LiftTrackerDao) {
    fun getWorkouts() = dao.getWorkouts()

    fun getWorkoutHistory() = dao.getWorkoutHistory()

    fun getWorkoutById(workoutId: Long) = dao.getWorkoutByIdFlow(workoutId)

    fun getExerciseNames() = dao.getExerciseNames()

    fun getProgressForExercise(exerciseName: String) = dao.getProgressForExercise(exerciseName)

    fun getSplitDays() = dao.getSplitDays()

    suspend fun createWorkout(title: String, date: String, notes: String = ""): Long {
        val workoutId = dao.insertWorkout(WorkoutEntity(date = date, title = title, notes = notes))
        return workoutId
    }

    suspend fun createWorkoutFromSplit(splitDay: SplitDayWithExercises, date: String, notes: String = ""): Long {
        val workoutId = createWorkout(splitDay.splitDay.name, date, notes)
        splitDay.exercises.sortedBy { it.orderIndex }.forEach { exercise ->
            dao.insertExercise(
                ExerciseEntryEntity(
                    workoutId = workoutId,
                    name = exercise.name,
                    orderIndex = exercise.orderIndex
                )
            )
        }
        return workoutId
    }

    suspend fun deleteWorkout(workoutId: Long) {
        dao.deleteWorkout(workoutId)
    }

    suspend fun createSplitDay(name: String): Long {
        val orderIndex = dao.getSplitDayCount()
        return dao.insertSplitDay(SplitDayEntity(name = name, orderIndex = orderIndex))
    }

    suspend fun addSplitExercise(splitDayId: Long, name: String): Long {
        val orderIndex = dao.getSplitExerciseCount(splitDayId)
        return dao.insertSplitExercise(
            SplitExerciseEntity(splitDayId = splitDayId, name = name, orderIndex = orderIndex)
        )
    }

    suspend fun renameExercise(exerciseId: Long, name: String) {
        dao.renameExercise(exerciseId, name)
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
