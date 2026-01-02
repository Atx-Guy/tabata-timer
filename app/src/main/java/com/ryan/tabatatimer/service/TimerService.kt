package com.ryan.tabatatimer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.ryan.tabatatimer.MainActivity
import com.ryan.tabatatimer.R
import com.ryan.tabatatimer.model.TimerPhase
import com.ryan.tabatatimer.model.TimerState
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.util.SoundManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerService : LifecycleService() {

    private val binder = LocalBinder()
    private var wakeLock: PowerManager.WakeLock? = null
    private var soundManager: SoundManager? = null

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var currentWorkout: Workout? = null
    
    // Monotonic Clock Vars
    private var lastTickTime = 0L
    private val TICK_INTERVAL_MS = 100L // Check every 100ms for responsiveness
    private var accumulatedTimeMs = 0L

    // Internal State Helper
    private var currentIntervalIndex = 0
    private var intervals = listOf<Interval>()
    
    data class Interval(val type: Int, val duration: Int, val roundIndex: Int)

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onCreate() {
        super.onCreate()
        soundManager = SoundManager(this)
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // Keep service alive if killed
        return Service.START_STICKY
    }

    fun startWorkout(workout: Workout) {
        currentWorkout = workout
        // Build intervals
        val list = mutableListOf<Interval>()
        if (workout.warmupSeconds > 0) {
            list.add(Interval(0, workout.warmupSeconds, 0))
        }
        for (i in 1..workout.rounds) {
            list.add(Interval(1, workout.workDurationSeconds, i))
            if (i < workout.rounds || workout.restDurationSeconds > 0) {
                 if (i < workout.rounds) {
                     list.add(Interval(2, workout.restDurationSeconds, i))
                 }
            }
        }
        // Finished state
        list.add(Interval(3, 0, workout.rounds))
        intervals = list.toList()

        currentIntervalIndex = 0
        
        _timerState.value = TimerState(
            phase = getTypeFromInt(intervals[0].type),
            timeRemainingSeconds = intervals[0].duration,
            currentRound = intervals[0].roundIndex,
            totalRounds = workout.rounds,
            isRunning = true,
            totalTimeElapsedSeconds = 0
        )

        startForegroundService()
        startTimerLoop()
    }

    fun pauseTimer() {
        _timerState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        soundManager?.abandonAudioFocus()
        updateNotification()
    }

    fun resumeTimer() {
        if (currentWorkout == null) return
        _timerState.update { it.copy(isRunning = true) }
        startTimerLoop()
        updateNotification()
    }

    fun stopTimer() {
        pauseTimer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        
        timerJob = lifecycleScope.launch(Dispatchers.Default) {
             // Monotonic Clock: Define the target time for the NEXT tick
            var nextTickTarget = System.nanoTime() + 1_000_000_000L // +1 Second

            while (_timerState.value.isRunning) {
                val now = System.nanoTime()
                val delayMs = (nextTickTarget - now) / 1_000_000

                if (delayMs > 0) {
                    delay(delayMs)
                }

                // Tick
                tick()
                
                // Advance target by exactly 1s from the PREVIOUS target
                // This prevents drift calculation errors
                nextTickTarget += 1_000_000_000L
            }
        }
    }

    private fun tick() {
        val currentState = _timerState.value
        if (currentState.timeRemainingSeconds > 0) {
            _timerState.update { 
                it.copy(
                    timeRemainingSeconds = it.timeRemainingSeconds - 1,
                    totalTimeElapsedSeconds = it.totalTimeElapsedSeconds + 1
                ) 
            }
            updateNotification()
        } else {
            advanceInterval()
        }
    }

    private fun advanceInterval() {
        if (currentIntervalIndex < intervals.size - 1) {
            currentIntervalIndex++
            val nextInterval = intervals[currentIntervalIndex]
            
            // Audio Cues
            if (nextInterval.type == 1) soundManager?.playWorkSound()
            else if (nextInterval.type == 2) soundManager?.playRestSound()
            else if (nextInterval.type == 3) soundManager?.playRestSound() // Finish sound?
            
            _timerState.update {
                it.copy(
                    phase = getTypeFromInt(nextInterval.type),
                    timeRemainingSeconds = nextInterval.duration,
                    currentRound = nextInterval.roundIndex,
                    totalTimeElapsedSeconds = it.totalTimeElapsedSeconds // Don't reset total
                )
            }
            updateNotification()
        } else {
            // Finished
            _timerState.update { 
                it.copy(
                    phase = TimerPhase.FINISHED, 
                    isRunning = false,
                    timeRemainingSeconds = 0
                ) 
            }
            stopTimer()
        }
    }

    private fun getTypeFromInt(type: Int): TimerPhase {
        return when (type) {
            0 -> TimerPhase.PREPARE
            1 -> TimerPhase.WORK
            2 -> TimerPhase.REST
            3 -> TimerPhase.FINISHED
            else -> TimerPhase.PREPARE
        }
    }

    // --- System & Notification ---

    private fun acquireWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TabataTimer::WakeLock")
        wakeLock?.acquire(120*60*1000L /*2 hours*/)
    }

    private fun startForegroundService() {
        val notification = buildNotification()
        startForeground(1, notification)
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, buildNotification())
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val state = _timerState.value
        val title = "Tabata Timer: ${state.phase}"
        val content = "Time remaining: ${state.timeRemainingSeconds}s (Round ${state.currentRound}/${state.totalRounds})"

        return NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_app_icon) // Use default helper
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "timer_channel",
                "Timer Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.release()
        soundManager?.release()
    }
}
