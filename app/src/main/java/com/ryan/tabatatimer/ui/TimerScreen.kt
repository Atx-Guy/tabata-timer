package com.ryan.tabatatimer.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.view.WindowManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import com.ryan.tabatatimer.model.TimerPhase
import com.ryan.tabatatimer.model.TimerState
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.service.TimerService
import com.ryan.tabatatimer.ui.theme.FinishedColor
import com.ryan.tabatatimer.ui.theme.RestColor
import com.ryan.tabatatimer.ui.theme.WarmupColor
import com.ryan.tabatatimer.ui.theme.WorkColor
import com.ryan.tabatatimer.ui.theme.DarkBackground

@Composable
fun TimerScreen(
    workoutJson: String,
    onNavigateBack: () -> Unit
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
    
    // Service Binding
    var timerService by remember { mutableStateOf<TimerService?>(null) }
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.LocalBinder
                timerService = binder.getService()
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        context.startForegroundService(intent) 
        
        onDispose {
            context.unbindService(connection)
        }
    }
    
    LaunchedEffect(timerService) {
        if (timerService != null) {
            val state = timerService!!.timerState.value
            if (!state.isRunning && state.phase != TimerPhase.FINISHED) {
                timerService!!.startWorkout(workout)
            }
        }
    }

    val timerState by timerService?.timerState?.collectAsState() ?: remember { mutableStateOf(TimerState()) }

    // FLUID BACKGROUND COLOR LOGIC
    val targetColor = when (timerState.phase) {
        TimerPhase.PREPARE -> WarmupColor
        TimerPhase.WORK -> WorkColor
        TimerPhase.REST -> RestColor
        TimerPhase.FINISHED -> FinishedColor
    }

    // Animate color change over 500ms
    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "BackgroundColor"
    )

    // Contrast logic: If background is dark (Finished), use white text. 
    // If background is bright (Work/Rest), use dark text or white depending on exact shade.
    // Given WorkColor (Lime) and RestColor (Orange), Black text is often better, or White with shadow.
    // Let's stick to White for High Contrast purely, or Black if colors are very bright.
    // WorkColor(0xFF76FF03) is very bright -> Black text.
    // RestColor(0xFFFF3D00) is med/dark -> White text.
    // WarmupColor(0xFFFFAB00) is bright -> Black text.
    // FinishedColor(0xFF9E9E9E) is med -> White text.
    
    val contentColor = when(timerState.phase) {
        TimerPhase.WORK, TimerPhase.PREPARE -> Color.Black
        else -> Color.White
    }

    val phaseText = when (timerState.phase) {
        TimerPhase.PREPARE -> "WARM UP"
        TimerPhase.WORK -> "WORK"
        TimerPhase.REST -> "REST"
        TimerPhase.FINISHED -> "FINISHED"
    }

    val isFinished = timerState.phase == TimerPhase.FINISHED

    Scaffold(
        containerColor = animatedBackgroundColor, 
        contentColor = contentColor,
        floatingActionButton = {
            if (!isFinished) {
                FloatingActionButton(
                    onClick = { 
                        if (timerState.isRunning) timerService?.pauseTimer() else timerService?.resumeTimer()
                    },
                    containerColor = Color.White.copy(alpha = 0.9f),
                    contentColor = Color.Black
                ) {
                    Icon(
                        if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (timerState.isRunning) "Pause" else "Resume"
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    timerService?.stopTimer()
                    onNavigateBack()
                }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit",
                        tint = contentColor.copy(alpha = 0.8f)
                    )
                }
                if (!isFinished) {
                    Text(
                        text = "ROUND ${timerState.currentRound}/${timerState.totalRounds}",
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }

            // Central Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = phaseText,
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black),
                    color = contentColor,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (!isFinished) {
                    Text(
                        text = "${timerState.timeRemainingSeconds}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 140.sp, 
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor
                    )
                } else {
                     Icon(
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Done",
                        modifier = Modifier.size(120.dp),
                        tint = contentColor
                     )
                }
            }

            // Footer
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                 if (isFinished) {
                    Button(
                        onClick = {
                            timerService?.stopTimer()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, 
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Exit Workout")
                    }
                }
            }
        }
    }
}
