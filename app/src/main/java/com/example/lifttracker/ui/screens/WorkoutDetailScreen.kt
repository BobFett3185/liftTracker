package com.example.lifttracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.Alignment
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
    var expandedExerciseId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val workout = workoutWithExercises
        if (workout == null) {
            Text("Loading workout...")
            return@Column
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(44.dp)) {
                Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text(
                workout.workout.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(workout.workout.date, style = MaterialTheme.typography.bodyMedium)
        }

        if (workout.workout.notes.isNotBlank()) {
            Text(workout.workout.notes)
        }

        if (workout.exercises.isEmpty()) {
            Text("No exercises logged yet.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(workout.exercises.sortedBy { it.exercise.orderIndex }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    isExpanded = expandedExerciseId == exercise.exercise.id,
                    onToggle = {
                        expandedExerciseId = if (expandedExerciseId == exercise.exercise.id) null else exercise.exercise.id
                    },
                    onAddSet = viewModel::addSet,
                    onRename = viewModel::renameExercise
                )
            }
        }

        Button(
            onClick = { navController.navigate("workout/$workoutId/add") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Exercise")
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseWithSets,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onAddSet: (Long, Int, Double) -> Unit,
    onRename: (Long, String) -> Unit
) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var editedName by remember(exercise.exercise.id, exercise.exercise.name) { mutableStateOf(exercise.exercise.name) }
    val repsValue = reps.toIntOrNull()
    val weightValue = weight.toDoubleOrNull()
    val sets = exercise.sets.sortedBy { it.setNumber }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onToggle),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(exercise.exercise.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${sets.size} sets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(if (isExpanded) "Close" else "Open", style = MaterialTheme.typography.labelLarge)
            }

            if (isExpanded) {
                Divider()
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Exercise name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = { onRename(exercise.exercise.id, editedName) },
                        enabled = editedName.isNotBlank() && editedName != exercise.exercise.name
                    ) {
                        Text("Rename")
                    }
                }

                if (sets.isEmpty()) {
                    Text("No sets yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    sets.forEach { set ->
                        Text("Set ${set.setNumber}: ${set.reps} reps at ${set.weight} lb")
                    }
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
}
