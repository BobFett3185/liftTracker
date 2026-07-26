package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import kotlinx.coroutines.launch

class NewWorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    fun saveWorkout(title: String, date: String, notes: String = "", onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val workoutId = repository.createWorkout(title, date, notes)
            onSaved(workoutId)
        }
    }
}
