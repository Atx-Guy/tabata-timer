package com.ryan.tabatatimer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onStartTimer: (Workout) -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tabata Timer") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Saved Workouts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (savedWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved workouts. Create one!")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(savedWorkouts) { workout ->
                        WorkoutCard(
                            workout = workout,
                            onPlay = { onStartTimer(workout) },
                            onDelete = { viewModel.deleteWorkout(workout) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWorkoutDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, work, rest, rounds, warmup ->
                viewModel.saveWorkout(name, work, rest, rounds, warmup)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    onPlay: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workout.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${workout.rounds} rounds • ${workout.workDurationSeconds}s/${workout.restDurationSeconds}s",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete")
                }
                FilledTonalIconButton(onClick = onPlay) {
                    Icon(Icons.Default.PlayArrow, "Start")
                }
            }
        }
    }
}

@Composable
fun AddWorkoutDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int, Int) -> Unit
) {
    var name by remember { mutableStateOf("My Tabata") }
    var work by remember { mutableStateOf(20f) }
    var rest by remember { mutableStateOf(10f) }
    var rounds by remember { mutableStateOf(8f) }
    var warmup by remember { mutableStateOf(5f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Workout") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                SliderInput("Work", work, 5f, 300f) { work = it }
                SliderInput("Rest", rest, 0f, 300f) { rest = it }
                SliderInput("Rounds", rounds, 1f, 50f) { rounds = it }
                SliderInput("Warmup", warmup, 0f, 60f) { warmup = it }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(name, work.toInt(), rest.toInt(), rounds.toInt(), warmup.toInt())
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SliderInput(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}${if (label == "Rounds") "" else "s"}", style = MaterialTheme.typography.bodyMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = ((max - min) / (if(label == "Rounds") 1 else 5)).toInt() - 1 
        )
    }
}
