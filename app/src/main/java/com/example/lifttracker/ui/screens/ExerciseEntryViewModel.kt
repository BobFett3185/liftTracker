package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.ExerciseWithSets
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExerciseEntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    private val _exercise = MutableStateFlow<ExerciseWithSets?>(null)
    val exercise = _exercise.asStateFlow()

    private var loadedExerciseId: Long? = null

    fun loadExercise(exerciseId: Long) {
        if (loadedExerciseId == exerciseId) return
        loadedExerciseId = exerciseId
        viewModelScope.launch {
            repository.getExerciseById(exerciseId).collectLatest { exercise ->
                _exercise.value = exercise
            }
        }
    }

    fun addSet(exerciseId: Long, reps: Int, weight: Double) {
        viewModelScope.launch {
            repository.addSet(exerciseId, reps, weight)
        }
    }

    fun renameExercise(exerciseId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.renameExercise(exerciseId, name.trim())
        }
    }
}
