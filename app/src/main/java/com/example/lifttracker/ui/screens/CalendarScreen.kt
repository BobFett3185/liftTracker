package com.example.lifttracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifttracker.data.WorkoutEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(navController: NavController, viewModel: CalendarViewModel = viewModel()) {
    val workouts by viewModel.workouts.collectAsState()
    var workoutToDelete by remember { mutableStateOf<WorkoutEntity?>(null) }
    var monthOffset by remember { mutableIntStateOf(0) }
    var selectedDate by remember { mutableStateOf(todayDateString()) }

    val visibleMonth = remember(monthOffset) {
        Calendar.getInstance().apply {
            add(Calendar.MONTH, monthOffset)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val monthDates = remember(monthOffset) { buildMonthDates(visibleMonth) }
    val workoutDates = workouts.map { it.date }.toSet()
    val selectedWorkouts = workouts.filter { it.date == selectedDate }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("LiftTracker", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { navController.navigate("newWorkout") }, modifier = Modifier.weight(1f)) {
                Text("Start Workout")
            }
            Button(onClick = { navController.navigate("progress") }, modifier = Modifier.weight(1f)) {
                Text("Progress")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { monthOffset -= 1 }) {
                        Text("Previous")
                    }
                    Text(monthTitle(visibleMonth), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { if (monthOffset < 0) monthOffset += 1 }, enabled = monthOffset < 0) {
                        Text("Next")
                    }
                }

                CalendarWeekHeader()
                CalendarMonthGrid(
                    dates = monthDates,
                    visibleMonth = visibleMonth,
                    selectedDate = selectedDate,
                    workoutDates = workoutDates,
                    onDateSelected = { selectedDate = it }
                )
            }
        }

        Text(selectedDate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (selectedWorkouts.isEmpty()) {
            Text("No workouts logged for this day.")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(selectedWorkouts, key = { it.id }) { workout ->
                WorkoutSummaryCard(
                    workout = workout,
                    onOpen = { navController.navigate("workout/${workout.id}") },
                    onDelete = { workoutToDelete = workout }
                )
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

@Composable
private fun CalendarWeekHeader() {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    dates: List<Calendar>,
    visibleMonth: Calendar,
    selectedDate: String,
    workoutDates: Set<String>,
    onDateSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        dates.chunked(7).forEach { week ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    val dateText = dateString(day)
                    val isVisibleMonth = day.get(Calendar.MONTH) == visibleMonth.get(Calendar.MONTH)
                    val isSelected = dateText == selectedDate
                    val hasWorkout = dateText in workoutDates

                    CalendarDayCell(
                        day = day.get(Calendar.DAY_OF_MONTH).toString(),
                        isVisibleMonth = isVisibleMonth,
                        isSelected = isSelected,
                        hasWorkout = hasWorkout,
                        onClick = { onDateSelected(dateText) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: String,
    isVisibleMonth: Boolean,
    isSelected: Boolean,
    hasWorkout: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        isSelected -> MaterialTheme.colorScheme.primary
        hasWorkout -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isVisibleMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(day, color = textColor, style = MaterialTheme.typography.bodyMedium)
            if (hasWorkout) {
                Box(
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                        .size(6.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkoutSummaryCard(workout: WorkoutEntity, onOpen: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(workout.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (workout.notes.isNotBlank()) {
                    Text(workout.notes, style = MaterialTheme.typography.bodyMedium)
                }
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}

private fun buildMonthDates(month: Calendar): List<Calendar> {
    val firstDay = month.clone() as Calendar
    firstDay.set(Calendar.DAY_OF_MONTH, 1)
    val startOffset = firstDay.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    firstDay.add(Calendar.DAY_OF_MONTH, -startOffset)

    return List(42) { index ->
        (firstDay.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, index)
        }
    }
}

private fun monthTitle(month: Calendar): String {
    return SimpleDateFormat("MMMM yyyy", Locale.US).format(month.time)
}

private fun todayDateString(): String {
    return dateString(Calendar.getInstance())
}

private fun dateString(calendar: Calendar): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
}
