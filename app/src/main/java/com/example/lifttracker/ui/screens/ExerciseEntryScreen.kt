package com.example.lifttracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.SetEntryEntity

@Composable
fun ExerciseEntryScreen(
    navController: NavController,
    exerciseId: Long,
    viewModel: ExerciseEntryViewModel = viewModel()
) {
    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    val exerciseWithSets by viewModel.exercise.collectAsState()
    val exercise = exerciseWithSets

    Column(
        modifier = Modifier.fillMaxSize().imePadding().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (exercise == null) {
            Text("Loading exercise...")
            return@Column
        }

        var editedName by remember(exercise.exercise.id, exercise.exercise.name) {
            mutableStateOf(exercise.exercise.name)
        }
        val sets = exercise.sets.sortedBy { it.setNumber }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(44.dp)) {
                Text("<", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.exercise.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${sets.size} sets", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Exercise name") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(
                onClick = { viewModel.renameExercise(exercise.exercise.id, editedName) },
                enabled = editedName.isNotBlank() && editedName != exercise.exercise.name
            ) {
                Text("Rename")
            }
        }

        AddSetPanel(
            onSaveSet = { reps, weight ->
                viewModel.addSet(exercise.exercise.id, reps, weight)
            }
        )

        Text("Sets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (sets.isEmpty()) {
            Text("No sets yet.")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(sets, key = { it.id }) { set ->
                SetRow(set = set)
            }
        }
    }
}

@Composable
private fun AddSetPanel(onSaveSet: (Int, Double) -> Unit) {
    var reps by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    val repsValue = reps.toIntOrNull()
    val weightValue = weight.toDoubleOrNull()
    val canSave = repsValue != null && weightValue != null

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        onSaveSet(repsValue, weightValue)
                        reps = ""
                        weight = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave
            ) {
                Text("Add Set")
            }
        }
    }
}

@Composable
private fun SetRow(set: SetEntryEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Set ${set.setNumber}", fontWeight = FontWeight.Bold)
            Text("${set.reps} reps")
            Text("${set.weight} lb")
        }
    }
}
