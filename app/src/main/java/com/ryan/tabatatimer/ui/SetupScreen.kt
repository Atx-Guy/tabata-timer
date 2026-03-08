package com.ryan.tabatatimer.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.util.SoundManager
import com.ryan.tabatatimer.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    onStartTimer: (Workout) -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    soundManager: SoundManager
) {
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()
    val showPaywall by viewModel.showPaywall.collectAsState()
    val isProUser by viewModel.isProUser.collectAsState()

    // Editor State
    val editorName by viewModel.editorName.collectAsState()
    val editorWork by viewModel.editorWork.collectAsState()
    val editorRest by viewModel.editorRest.collectAsState()
    val editorRounds by viewModel.editorRounds.collectAsState()
    val editorWarmup by viewModel.editorWarmup.collectAsState()
    val editorWorkoutId by viewModel.editorWorkoutId.collectAsState()

    val isAudioEnabled by soundManager.isAudioEnabled.collectAsState()

    val scrollState = rememberScrollState()


    var isOrganizeMode by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(editorWorkoutId) {
        if (editorWorkoutId != null) {
            showSheet = true
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isOrganizeMode) {
                FloatingActionButton(
                    onClick = {
                        viewModel.clearEditor()
                        showSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Workout")
                }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tabata Timer") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = { isOrganizeMode = !isOrganizeMode },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = if (isOrganizeMode) "DONE" else "ORGANIZE",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState) // Make the whole screen scrollable
        ) {
            // QUICK START HERO CARD
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "READY TO SWEAT?",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quick Start",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Standard Tabata • 20/10 x 8",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            onStartTimer(
                                Workout(
                                    name = "Quick Start",
                                    workDurationSeconds = 20,
                                    restDurationSeconds = 10,
                                    rounds = 8,
                                    warmupSeconds = 5,
                                    id = 0 // Transient ID
                                )
                            )
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                         modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START NOW")
                    }
                }
            }

            // SAVED LIST SECTION
            Text(
                text = "Saved Workouts",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (savedWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved workouts.")
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 80.dp) // Space for bottom bar
                ) {
                    savedWorkouts.forEach { workout ->
                        WorkoutCard(
                            workout = workout,
                            isOrganizeMode = isOrganizeMode,
                            onPlay = { onStartTimer(workout) },
                            onDelete = { viewModel.deleteWorkout(workout) },
                            onEdit = {
                                viewModel.onEditRequest(workout)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Bottom Sheet Editor
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                viewModel.clearEditor()
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = if (editorWorkoutId != null) "Edit Workout" else "Setup New Workout",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                WorkoutEditor(
                    name = editorName,
                    onNameChange = { viewModel.updateEditorState(name = it) },
                    work = editorWork,
                    onWorkChange = { viewModel.updateEditorState(work = it) },
                    rest = editorRest,
                    onRestChange = { viewModel.updateEditorState(rest = it) },
                    rounds = editorRounds,
                    onRoundsChange = { viewModel.updateEditorState(rounds = it) },
                    warmup = editorWarmup,
                    onWarmupChange = { viewModel.updateEditorState(warmup = it) },
                    isAudioEnabled = isAudioEnabled,
                    onAudioEnabledChange = { soundManager.setAudioEnabled(it) },
                    onSave = {
                        viewModel.saveOrUpdateWorkout()
                        showSheet = false
                    },
                    onClear = {
                        viewModel.clearEditor()
                        showSheet = false
                    },
                    isEditingConfig = editorWorkoutId != null
                )
            }
        }
    }


    if (showPaywall) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.dismissPaywall() },
            icon = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Unlock Unlimited Workouts") },
            text = { Text("Free users can save 1 workout. Upgrade to Tabata Pro for unlimited timers, smart audio, and more.") },
            confirmButton = {
                Button(onClick = { 
                    viewModel.upgradeToPro()
                    viewModel.dismissPaywall()
                }) {
                    Text("UPGRADE NOW")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissPaywall() }) {
                    Text("Maybe Later")
                }
            }
        )
    }
}

@Composable
fun WorkoutCard(
    workout: Workout,
    isOrganizeMode: Boolean,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface, // DarkSurface
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary // Deep Crimson title
                )
                Text(
                    text = "${workout.rounds} rounds • ${workout.workDurationSeconds}s/${workout.restDurationSeconds}s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions — in organize mode show only Delete; otherwise show Edit + Play
            Crossfade(targetState = isOrganizeMode, label = "ActionButtons") { organizing ->
                if (organizing) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(
                                Icons.Default.Edit,
                                "Edit",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        FilledTonalIconButton(
                            onClick = onPlay,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, "Start")
                        }
                    }
                }
            }
        }
    }
}
