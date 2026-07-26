package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    private val _workouts = MutableStateFlow<List<com.example.lifttracker.data.WorkoutEntity>>(emptyList())
    val workouts = _workouts.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getWorkouts().collect { list ->
                _workouts.value = list
            }
        }
    }
}
