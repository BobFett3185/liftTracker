package com.example.lifttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.WorkoutEntity

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = viewModel()) {
    val workouts by viewModel.workouts.collectAsState()
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Workout History", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = { navController.navigate("newWorkout") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start New Workout")
        }
        Button(
            onClick = { navController.navigate("progress") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Progress")
        }

        if (workouts.isEmpty()) {
            Text("No workouts yet. Start one when you get to the gym.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items(workouts, key = { it.id }) { workout ->
                Card(
                    onClick = { navController.navigate("workout/${workout.id}") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(workout.title, style = MaterialTheme.typography.titleMedium)
                        Text(workout.date, style = MaterialTheme.typography.bodyMedium)
                        if (workout.notes.isNotEmpty()) Text(workout.notes)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { workoutToDelete = workout }) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    workoutToDelete?.let { workout ->
        AlertDialog(
            onDismissRequest = { workoutToDelete = null },
            title = { Text("Delete workout?") },
            text = { Text("This will remove ${workout.title} and all of its exercises and sets.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteWorkout(workout.id)
                        workoutToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { workoutToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
