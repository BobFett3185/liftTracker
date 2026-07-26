package com.example.lifttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.ExerciseProgressPoint

@Composable
fun ProgressScreen(navController: NavController, viewModel: ProgressViewModel = viewModel()) {
    val exerciseNames by viewModel.exerciseNames.collectAsState()
    val selectedExercise by viewModel.selectedExercise.collectAsState()
    val progress by viewModel.progress.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Progress", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }

        if (exerciseNames.isEmpty()) {
            Text("Log a workout with exercises and sets to see progress.")
            return@Column
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
        ) {
            exerciseNames.forEach { name ->
                Button(onClick = { viewModel.selectExercise(name) }) {
                    Text(name)
                }
            }
        }

        Text(selectedExercise, style = MaterialTheme.typography.titleLarge)
        ProgressBars(points = progress)
    }
}

@Composable
private fun ProgressBars(points: List<ExerciseProgressPoint>) {
    if (points.isEmpty()) {
        Text("No sets found for this exercise.")
        return
    }

    val maxWeight = points.maxOf { it.weight }.coerceAtLeast(1.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(16.dp)
        ) {
            points.forEach { point ->
                val barHeight = ((point.weight / maxWeight) * 160).coerceAtLeast(12.0).toFloat().dp
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.width(72.dp)
                ) {
                    Text("${point.weight} lb", style = MaterialTheme.typography.labelMedium)
                    Box(
                        modifier = Modifier
                            .height(168.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .width(34.dp)
                                .height(barHeight)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text("${point.reps} reps", style = MaterialTheme.typography.labelSmall)
                    Text(point.date.take(5), style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                }
            }
        }
    }
}
