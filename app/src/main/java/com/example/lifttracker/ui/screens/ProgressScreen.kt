package com.example.lifttracker.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.ExerciseProgressPoint

@Composable
fun ProgressScreen(navController: NavController, viewModel: ProgressViewModel = viewModel()) {
    val exerciseNames by viewModel.exerciseNames.collectAsState()
    val selectedExercise by viewModel.selectedExercise.collectAsState()
    val setNumbers by viewModel.setNumbers.collectAsState()
    val selectedSetNumber by viewModel.selectedSetNumber.collectAsState()
    val progress by viewModel.progress.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(44.dp)) {
                Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Set Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Track one exercise set over time", style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (exerciseNames.isEmpty()) {
            Text("Log a workout with exercises and sets to see progress.")
            return@Column
        }

        Text("Exercise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        HorizontalChoiceRow(
            items = exerciseNames,
            selectedItem = selectedExercise,
            label = { it },
            onSelect = viewModel::selectExercise
        )

        Text("Set Number", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (setNumbers.isEmpty()) {
            Text("No sets logged for this exercise yet.")
            return@Column
        }
        val activeSetNumber = selectedSetNumber ?: setNumbers.first()
        HorizontalChoiceRow(
            items = setNumbers,
            selectedItem = activeSetNumber,
            label = { "Set $it" },
            onSelect = viewModel::selectSetNumber
        )

        ProgressSummary(
            exerciseName = selectedExercise,
            setNumber = activeSetNumber,
            points = progress
        )

        Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(progress.reversed(), key = { "${it.workoutId}-${it.setNumber}-${it.date}-${it.reps}-${it.weight}" }) { point ->
                ProgressHistoryRow(point = point)
            }
        }
    }
}

@Composable
private fun <T> HorizontalChoiceRow(
    items: List<T>,
    selectedItem: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
    ) {
        items.forEach { item ->
            Button(onClick = { onSelect(item) }) {
                Text(label(item))
            }
        }
    }
}

@Composable
private fun ProgressSummary(
    exerciseName: String,
    setNumber: Int?,
    points: List<ExerciseProgressPoint>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                if (setNumber == null) exerciseName else "$exerciseName - Set $setNumber",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (points.size < 2) {
                Text("Add this set across more workouts to see change over time.")
                return@Column
            }

            val first = points.first()
            val latest = points.last()
            val weightChange = latest.weight - first.weight
            val repsChange = latest.reps - first.reps
            val weightPrefix = if (weightChange >= 0) "+" else ""
            val repsPrefix = if (repsChange >= 0) "+" else ""

            Text(
                "$weightPrefix${formatWeight(weightChange)} lb",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Weight change from first logged set")
            Text("$repsPrefix$repsChange reps over the same span")
            Text("Latest: ${latest.reps} reps at ${formatWeight(latest.weight)} lb")
        }
    }
}

@Composable
private fun ProgressHistoryRow(point: ExerciseProgressPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(point.date, fontWeight = FontWeight.Bold)
            Text("Set ${point.setNumber}")
            Text("${point.reps} x ${formatWeight(point.weight)} lb")
        }
    }
}

private fun formatWeight(weight: Double): String {
    return if (weight % 1.0 == 0.0) {
        weight.toInt().toString()
    } else {
        weight.toString()
    }
}
