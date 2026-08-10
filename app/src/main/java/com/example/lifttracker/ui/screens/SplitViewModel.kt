package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import com.example.lifttracker.data.SplitDayWithExercises
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    private val _splitDays = MutableStateFlow<List<SplitDayWithExercises>>(emptyList())
    val splitDays = _splitDays.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSplitDays().collect { days ->
                _splitDays.value = days
            }
        }
    }

    fun addSplitDay(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.createSplitDay(name.trim())
        }
    }

    fun addExercise(splitDayId: Long, name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addSplitExercise(splitDayId, name.trim())
        }
    }
}
