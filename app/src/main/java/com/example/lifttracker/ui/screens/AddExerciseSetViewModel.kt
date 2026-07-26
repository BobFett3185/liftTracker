package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import kotlinx.coroutines.launch

class AddExerciseSetViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    fun saveExerciseSet(
        workoutId: Long,
        exerciseName: String,
        reps: Int,
        weight: Double,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addExerciseWithSet(workoutId, exerciseName, reps, weight)
            onSaved()
        }
    }
}
