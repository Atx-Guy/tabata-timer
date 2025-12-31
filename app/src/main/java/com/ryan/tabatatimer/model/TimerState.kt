package com.ryan.tabatatimer.model

enum class TimerPhase(val displayName: String) {
    PREPARE("Prepare"),
    WORK("Work"),
    REST("Rest"),
    FINISHED("Finished")
}

data class TimerConfig(
    val prepareTimeSeconds: Int = 5,
    val workTimeSeconds: Int = 20,
    val restTimeSeconds: Int = 10,
    val totalRounds: Int = 8
)

data class TimerState(
    val phase: TimerPhase = TimerPhase.PREPARE,
    val timeRemainingSeconds: Int = 5,
    val currentRound: Int = 1,
    val totalRounds: Int = 8,
    val isRunning: Boolean = false,
    val totalTimeElapsedSeconds: Int = 0
) {
    val progress: Float
        get() = when(phase) {
            TimerPhase.PREPARE -> 1f // Or based on prepare config
            TimerPhase.WORK -> 1f // Dynamic calculation required with config
            TimerPhase.REST -> 1f 
            TimerPhase.FINISHED -> 0f
        }
}
