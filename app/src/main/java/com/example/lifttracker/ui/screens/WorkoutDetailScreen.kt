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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                    onOpen = { navController.navigate("exercise/${exercise.exercise.id}") }
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
    onOpen: () -> Unit
) {
    val sets = exercise.sets.sortedBy { it.setNumber }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
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
            Text(">", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}
