package com.ryan.tabatatimer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.ui.theme.DeepCrimson
import com.ryan.tabatatimer.ui.theme.MutedTeal
import com.ryan.tabatatimer.ui.theme.OffWhite
import com.ryan.tabatatimer.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerListScreen(
    onNavigateToTimer: (Workout) -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
) {
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Timer Pro",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Open Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MutedTeal,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Timer")
            }
        },
        bottomBar = {
            Surface(
                color = DeepCrimson,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "ORGANIZE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Monetization Banner
            item {
                MonetizationBanner()
            }

            // Timer List
            if (savedWorkouts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No timers yet. Tap + to create one.", color = OffWhite)
                    }
                }
            } else {
                items(savedWorkouts) { workout ->
                    TimerListItem(
                        workout = workout,
                        onClick = { onNavigateToTimer(workout) },
                        onEdit = { /* TODO: Edit */ }
                    )
                    Divider(color = Color.DarkGray, thickness = 0.5.dp)
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
fun SliderInput(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${value.toInt()}${if (label == "Rounds") "" else "s"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            steps = ((max - min) / (if (label == "Rounds") 1 else 5)).toInt() - 1
        )
    }
}


@Composable
fun MonetizationBanner() {
    Surface(
        color = DeepCrimson,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { /* TODO: Upgrade */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = DeepCrimson
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Upgrade for $6.99",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remove ads & unlock unlimited timers",
                color = OffWhite.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun TimerListItem(
    workout: Workout,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Container
        Surface(
            shape = CircleShape,
            color = DeepCrimson,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.PlayArrow, // Fallback for "Stopwatch"
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = workout.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = String.format(
                    Locale.getDefault(),
                    "%d Rounds / %02d:%02d",
                    workout.rounds,
                    (workout.workDurationSeconds * workout.rounds) / 60,
                    (workout.workDurationSeconds * workout.rounds) % 60
                ), // Rough duration calc
                color = OffWhite,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Edit Action
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.Gray
            )
        }
    }
}
