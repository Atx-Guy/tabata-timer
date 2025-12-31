package com.ryan.tabatatimer.ui

import android.net.Uri
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.ui.theme.FinishedColor
import com.ryan.tabatatimer.ui.theme.RestColor
import com.ryan.tabatatimer.ui.theme.UpNextColor
import com.ryan.tabatatimer.ui.theme.WarmupColor
import com.ryan.tabatatimer.ui.theme.WorkColor
import kotlinx.coroutines.delay
import android.app.Activity

import com.ryan.tabatatimer.util.SoundManager

@Composable
fun TimerScreen(
    workoutJson: String,
    onNavigateBack: () -> Unit,
    soundManager: SoundManager
) {
    val workout = remember(workoutJson) {
        try {
            Gson().fromJson(Uri.decode(workoutJson), Workout::class.java)
        } catch (e: Exception) {
            null
        }
    }

    if (workout == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error loading workout")
            Button(onClick = onNavigateBack) { Text("Go Back") }
        }
        return
    }

    // Keep screen on
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    var isPaused by remember { mutableStateOf(false) }
    
    // Convert workout structure into a flat list of intervals for easier processing
    // Type: 0=Warmup, 1=Work, 2=Rest, 3=Finished
    data class Interval(val type: Int, val duration: Int, val roundIndex: Int)
    
    val intervals = remember(workout) {
        val list = mutableListOf<Interval>()
        if (workout.warmupSeconds > 0) {
            list.add(Interval(0, workout.warmupSeconds, 0))
        }
        for (i in 1..workout.rounds) {
            list.add(Interval(1, workout.workDurationSeconds, i))
            if (i < workout.rounds || workout.restDurationSeconds > 0) {
                 // Usually last rest is optional but standard tabata includes it or specific cool down.
                 // For simplicity, we add rest after every work, unless it's the very last one? 
                 // Let's assume standard behavior: Work -> Rest -> Work -> Rest.
                 if (i < workout.rounds) {
                     list.add(Interval(2, workout.restDurationSeconds, i))
                 }
            }
        }
        // Add a "Finished" state
        list.add(Interval(3, 0, workout.rounds)) 
        list
    }

    var currentIntervalIndex by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(intervals.first().duration) }

    LaunchedEffect(key1 = isPaused, key2 = currentIntervalIndex) {
        if (!isPaused && currentIntervalIndex < intervals.size - 1) { // Don't tick if finished
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            // Move to next interval
            if (currentIntervalIndex < intervals.size - 1) {
                currentIntervalIndex++
                timeLeft = intervals[currentIntervalIndex].duration
                
                // Audio Cues
                val newInterval = intervals[currentIntervalIndex]
                if (newInterval.type == 1) { // Work
                    soundManager.playWorkSound()
                } else if (newInterval.type == 2) { // Rest
                    soundManager.playRestSound()
                }
            }
        }
    }

    val currentInterval = intervals[currentIntervalIndex]
    
    // Theme Logic based on State
    val (stateText, stateColor) = when (currentInterval.type) {
        0 -> "WARM UP" to WarmupColor
        1 -> "WORK" to WorkColor
        2 -> "REST" to RestColor
        3 -> "FINISHED" to FinishedColor
        else -> "" to Color.White
    }

    val nextInterval = intervals.getOrNull(currentIntervalIndex + 1)
    val nextText = when (nextInterval?.type) {
        1 -> "Work (${nextInterval.duration}s)"
        2 -> "Rest (${nextInterval.duration}s)"
        3 -> "Finish"
        else -> ""
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Deep Charcoal
        floatingActionButton = {
            if (currentInterval.type != 3) {
                FloatingActionButton(
                    onClick = { isPaused = !isPaused },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(
                        if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause"
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Round Info & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, // Changed to AutoMirrored
                        contentDescription = "Exit",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (currentInterval.type in 1..2) {
                    Text(
                        text = "ROUND ${currentInterval.roundIndex}/${workout.rounds}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Main Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stateText,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = stateColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (currentInterval.type != 3) {
                    Text(
                        text = "$timeLeft",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 120.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = stateColor
                    )
                } else {
                     Icon(
                        imageVector = Icons.Default.Close, // Or a checkmark
                        contentDescription = "Done",
                        modifier = Modifier.size(100.dp),
                        tint = FinishedColor
                     )
                }
            }

            // Up Next
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                if (nextInterval != null && nextInterval.type != 3) {
                    Text(
                        text = "UP NEXT",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = nextText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = UpNextColor
                    )
                } else if (currentInterval.type == 3) {
                    Button(onClick = onNavigateBack) {
                        Text("Exit Workout")
                    }
                }
            }
        }
    }
}
