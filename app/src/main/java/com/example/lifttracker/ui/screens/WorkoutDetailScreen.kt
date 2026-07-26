package com.example.lifttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.ExerciseWithSets

@Composable
fun WorkoutDetailScreen(
    navController: NavController,
    workoutId: Long,
    viewModel: WorkoutDetailViewModel = viewModel()
) {
    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    val workoutWithExercises by viewModel.workout.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        val workout = workoutWithExercises
        if (workout == null) {
            Text("Loading workout...")
            return@Column
        }

        Text(workout.workout.title, style = MaterialTheme.typography.headlineMedium)
        Text(workout.workout.date, style = MaterialTheme.typography.bodyLarge)
        if (workout.workout.notes.isNotBlank()) {
            Text(workout.workout.notes)
        }

        Button(
            onClick = { navController.navigate("workout/$workoutId/add") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Exercise")
        }

        if (workout.exercises.isEmpty()) {
            Text("No exercises logged yet.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            items(workout.exercises.sortedBy { it.exercise.orderIndex }) { exercise ->
                ExerciseCard(exercise = exercise, onAddSet = viewModel::addSet)
            }
        }
    }
}

@Composable
private fun ExerciseCard(exercise: ExerciseWithSets, onAddSet: (Long, Int, Double) -> Unit) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val repsValue = reps.toIntOrNull()
    val weightValue = weight.toDoubleOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(exercise.exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider()
            exercise.sets.sortedBy { it.setNumber }.forEach { set ->
                Text("Set ${set.setNumber}: ${set.reps} reps at ${set.weight} lb")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
            Button(
                onClick = {
                    if (repsValue != null && weightValue != null) {
                        onAddSet(exercise.exercise.id, repsValue, weightValue)
                        reps = ""
                        weight = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = repsValue != null && weightValue != null
            ) {
                Text("Add Set")
            }
        }
    }
}
