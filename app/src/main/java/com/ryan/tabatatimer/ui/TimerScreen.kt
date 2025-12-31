package com.ryan.tabatatimer.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryan.tabatatimer.model.TimerConfig
import com.ryan.tabatatimer.model.TimerPhase
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.ui.theme.*
import com.ryan.tabatatimer.viewmodel.TimerViewModel

@Composable
fun TimerScreen(
    workout: Workout,
    onBack: () -> Unit,
    viewModel: TimerViewModel = viewModel()
) {
    LaunchedEffect(workout) {
        viewModel.updateConfig(
            TimerConfig(
                prepareTimeSeconds = workout.warmupSeconds,
                workTimeSeconds = workout.workDurationSeconds,
                restTimeSeconds = workout.restDurationSeconds,
                totalRounds = workout.rounds
            )
        )
    }
    val uiState by viewModel.state.collectAsState()
    val config by viewModel.config.collectAsState()

    // Determine colors/text based on phase
    val (backgroundColor, phaseName, nextPhaseName) = when (uiState.phase) {
        TimerPhase.PREPARE -> Triple(WarmupColor, "WARM UP", "WORK")
        TimerPhase.WORK -> Triple(WorkColor, "WORK", "REST")
        TimerPhase.REST -> Triple(RestColor, "REST", "WORK")
        TimerPhase.FINISHED -> Triple(FinishedColor, "FINISHED", "DONE")
    }

    TabataTimerTheme {
        // Full screen background color transition
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar / Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${uiState.currentRound}/${uiState.totalRounds}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                // Main Timer Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 32.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = phaseName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = backgroundColor, // Match phase color
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.timeRemainingSeconds.toString(),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 120.sp),
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // Control Buttons (Pause/Resume)
                        // Only show controls if not finished
                        if (uiState.phase != TimerPhase.FINISHED) {
                            FilledTonalIconButton(
                                onClick = { viewModel.toggleTimer() },
                                modifier = Modifier.size(64.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = backgroundColor.copy(alpha = 0.1f),
                                    contentColor = backgroundColor
                                )
                            ) {
                                Icon(
                                    imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (uiState.isRunning) "Pause" else "Resume",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                // Up Next Card (Blue)
                if (uiState.phase != TimerPhase.FINISHED) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = UpNextColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "UP NEXT",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = nextPhaseName,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Show duration of next phase if predictable
                            // Simple logic: if WORK -> REST (use rest config), if REST -> WORK (use work config)
                            val nextDuration = when (uiState.phase) {
                                TimerPhase.WORK -> config.restTimeSeconds
                                TimerPhase.REST -> config.workTimeSeconds
                                TimerPhase.PREPARE -> config.workTimeSeconds
                                else -> 0
                            }
                            Text(
                                text = "${nextDuration}s",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = WorkColor)
                    ) {
                        Text("DONE", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
