package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import com.example.lifttracker.data.WorkoutWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkoutDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    private val _workout = MutableStateFlow<WorkoutWithExercises?>(null)
    val workout = _workout.asStateFlow()

    private var loadedWorkoutId: Long? = null

    fun loadWorkout(workoutId: Long) {
        if (loadedWorkoutId == workoutId) return
        loadedWorkoutId = workoutId
        viewModelScope.launch {
            repository.getWorkoutById(workoutId).collectLatest { workout ->
                _workout.value = workout
            }
        }
    }

}
