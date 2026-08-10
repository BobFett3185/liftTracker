package com.example.lifttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.lifttracker.ui.screens.AddExerciseSetScreen
import com.example.lifttracker.ui.screens.CalendarScreen
import com.example.lifttracker.ui.screens.NewWorkoutScreen
import com.example.lifttracker.ui.screens.ProgressScreen
import com.example.lifttracker.ui.screens.SplitScreen
import com.example.lifttracker.ui.screens.WorkoutDetailScreen
import com.example.lifttracker.ui.theme.LiftTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiftTrackerApp()
        }
    }
}

@Composable
fun LiftTrackerApp() {
    val navController = rememberNavController()
    LiftTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            NavHost(navController = navController, startDestination = "calendar") {
                composable("calendar") { CalendarScreen(navController) }
                composable("newWorkout") { NewWorkoutScreen(navController) }
                composable("split") { SplitScreen(navController) }
                composable(
                    route = "workout/{workoutId}",
                    arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                    WorkoutDetailScreen(navController, workoutId)
                }
                composable(
                    route = "workout/{workoutId}/add",
                    arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: return@composable
                    AddExerciseSetScreen(navController, workoutId)
                }
                composable("progress") { ProgressScreen(navController) }
            }
        }
    }
}
