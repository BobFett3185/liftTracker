package com.example.lifttracker.ui.screens

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.SplitDayWithExercises
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewWorkoutScreen(navController: NavController, viewModel: NewWorkoutViewModel = viewModel()) {
    val splitDays by viewModel.splitDays.collectAsState()
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())) }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(44.dp)) {
                Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Text("Choose Workout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Text("Split Days", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (splitDays.isEmpty()) {
            Text("No split days yet. Build your split first, or create a custom workout below.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(splitDays, key = { it.splitDay.id }) { splitDay ->
                SplitWorkoutChoice(
                    splitDay = splitDay,
                    onStart = {
                        viewModel.saveWorkoutFromSplit(splitDay, date.trim(), notes.trim()) { workoutId ->
                            navController.navigate("workout/$workoutId") {
                                popUpTo("calendar")
                            }
                        }
                    }
                )
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Custom workout title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.saveWorkout(title.trim(), date.trim(), notes.trim()) { workoutId ->
                        navController.navigate("workout/$workoutId") {
                            popUpTo("calendar")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text("Create Custom Workout")
        }
    }
}

@Composable
private fun SplitWorkoutChoice(splitDay: SplitDayWithExercises, onStart: () -> Unit) {
    Card(
        onClick = onStart,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(splitDay.splitDay.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "${splitDay.exercises.size} exercises",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val preview = splitDay.exercises.sortedBy { it.orderIndex }.take(4).joinToString(", ") { it.name }
            if (preview.isNotBlank()) {
                Text(preview, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
