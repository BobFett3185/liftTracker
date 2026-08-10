package com.example.lifttracker.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifttracker.data.ExerciseProgressPoint
import com.example.lifttracker.data.LiftTrackerDatabase
import com.example.lifttracker.data.LiftTrackerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LiftTrackerRepository(LiftTrackerDatabase.getDatabase(application).liftTrackerDao())

    private val _exerciseNames = MutableStateFlow<List<String>>(emptyList())
    val exerciseNames = _exerciseNames.asStateFlow()

    private val _selectedExercise = MutableStateFlow("")
    val selectedExercise = _selectedExercise.asStateFlow()

    private val _setNumbers = MutableStateFlow<List<Int>>(emptyList())
    val setNumbers = _setNumbers.asStateFlow()

    private val _selectedSetNumber = MutableStateFlow<Int?>(null)
    val selectedSetNumber = _selectedSetNumber.asStateFlow()

    private val _progress = MutableStateFlow<List<ExerciseProgressPoint>>(emptyList())
    val progress = _progress.asStateFlow()

    private var setNumbersJob: Job? = null
    private var progressJob: Job? = null

    init {
        viewModelScope.launch {
            repository.getExerciseNames().collectLatest { names ->
                _exerciseNames.value = names
                if (_selectedExercise.value.isBlank() && names.isNotEmpty()) {
                    selectExercise(names.first())
                }
            }
        }
    }

    fun selectExercise(exerciseName: String) {
        if (exerciseName == _selectedExercise.value && _setNumbers.value.isNotEmpty()) return
        _selectedExercise.value = exerciseName
        _selectedSetNumber.value = null
        _setNumbers.value = emptyList()
        _progress.value = emptyList()

        setNumbersJob?.cancel()
        setNumbersJob = viewModelScope.launch {
            repository.getSetNumbersForExercise(exerciseName).collectLatest { numbers ->
                _setNumbers.value = numbers
                val currentSet = _selectedSetNumber.value
                if ((currentSet == null || currentSet !in numbers) && numbers.isNotEmpty()) {
                    selectSetNumber(numbers.first())
                }
            }
        }
    }

    fun selectSetNumber(setNumber: Int) {
        _selectedSetNumber.value = setNumber
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            repository.getProgressForExerciseSet(_selectedExercise.value, setNumber).collectLatest { points ->
                _progress.value = points
            }
        }
    }
}
