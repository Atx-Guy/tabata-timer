package com.ryan.tabatatimer.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryan.tabatatimer.model.TimerPhase
import com.ryan.tabatatimer.model.TimerState
import com.ryan.tabatatimer.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    viewModel: TimerViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val config by viewModel.config.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsDialog(
            currentConfig = config,
            onDismiss = { showSettings = false },
            onConfirm = { newConfig ->
                viewModel.updateConfig(newConfig)
                showSettings = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tabata Timer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            TimerVisuals(state = state)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            TimerInfo(state = state)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            TimerControls(
                isRunning = state.isRunning,
                onToggle = { viewModel.toggleTimer() },
                onReset = { viewModel.resetTimer() }
            )
        }
    }
}

@Composable
fun TimerVisuals(state: TimerState) {
    val phaseColor = when(state.phase) {
        TimerPhase.PREPARE -> Color.Yellow
        TimerPhase.WORK -> Color.Green
        TimerPhase.REST -> Color.Red
        TimerPhase.FINISHED -> Color.Blue
    }
    
    val animatedColor by animateColorAsState(targetValue = phaseColor, label = "PhaseColor")
    
    Box(contentAlignment = Alignment.Center) {
        // Background Circle
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier.size(300.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 20.dp
        )
        
        // Progress Circle
        // Note: Simple progress mapping for now. Can be improved with smooth animation.
        val progress = if (state.phase == TimerPhase.FINISHED) 0f else {
            // We would need max time for the phase to calculate accurate progress 0.0 to 1.0
            // For now just showing full circle or some indication
            // To do this properly, we need totalSecondsForPhase in TimerState or Config lookup
            1f 
        }
        
        CircularProgressIndicator(
            progress = progress, // TODO: animate this properly based on actual time/total
            modifier = Modifier.size(300.dp),
            color = animatedColor,
            strokeWidth = 20.dp,
            strokeCap = StrokeCap.Round
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(state.timeRemainingSeconds),
                style = MaterialTheme.typography.displayLarge,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = state.phase.displayName,
                style = MaterialTheme.typography.headlineMedium,
                color = animatedColor
            )
        }
    }
}

@Composable
fun TimerInfo(state: TimerState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ROUND", style = MaterialTheme.typography.labelLarge)
            Text(
                "${state.currentRound}/${state.totalRounds}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TOTAL TIME", style = MaterialTheme.typography.labelLarge)
            Text(
                formatTime(state.totalTimeElapsedSeconds),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun TimerControls(
    isRunning: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onReset,
            modifier = Modifier.size(72.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(32.dp))
        }
        
        Spacer(modifier = Modifier.width(32.dp))
        
        Button(
            onClick = onToggle,
            modifier = Modifier.size(96.dp), // Larger primary button
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Icon(
                if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
