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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.SplitDayWithExercises

@Composable
fun SplitScreen(navController: NavController, viewModel: SplitViewModel = viewModel()) {
    val splitDays by viewModel.splitDays.collectAsState()
    var newDayName by remember { mutableStateOf("") }
    val exerciseInputs = remember { mutableStateMapOf<Long, String>() }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Back")
            }
            Text("Split", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = newDayName,
                onValueChange = { newDayName = it },
                label = { Text("Day name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = {
                    viewModel.addSplitDay(newDayName)
                    newDayName = ""
                },
                enabled = newDayName.isNotBlank()
            ) {
                Text("Add")
            }
        }

        if (splitDays.isEmpty()) {
            Text("Add days like Push, Pull, Legs, or Upper.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            items(splitDays, key = { it.splitDay.id }) { splitDay ->
                SplitDayCard(
                    splitDay = splitDay,
                    exerciseText = exerciseInputs[splitDay.splitDay.id].orEmpty(),
                    onExerciseTextChange = { exerciseInputs[splitDay.splitDay.id] = it },
                    onAddExercise = {
                        viewModel.addExercise(splitDay.splitDay.id, exerciseInputs[splitDay.splitDay.id].orEmpty())
                        exerciseInputs[splitDay.splitDay.id] = ""
                    }
                )
            }
        }
    }
}

@Composable
private fun SplitDayCard(
    splitDay: SplitDayWithExercises,
    exerciseText: String,
    onExerciseTextChange: (String) -> Unit,
    onAddExercise: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(splitDay.splitDay.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            val exercises = splitDay.exercises.sortedBy { it.orderIndex }
            if (exercises.isEmpty()) {
                Text("No exercises yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                exercises.forEachIndexed { index, exercise ->
                    Text("${index + 1}. ${exercise.name}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = exerciseText,
                    onValueChange = onExerciseTextChange,
                    label = { Text("Exercise") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(onClick = onAddExercise, enabled = exerciseText.isNotBlank()) {
                    Text("Add")
                }
            }
        }
    }
}
