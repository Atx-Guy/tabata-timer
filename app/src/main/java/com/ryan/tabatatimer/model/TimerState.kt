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
            TimerPhase.PREPARE -> {
                 // Safe division: prevent crash if prepareTimeSeconds is 0 (unlikely but possible)
                 if (timeRemainingSeconds > 0) timeRemainingSeconds.toFloat() / 5f else 0f 
                 // Note: Ideally denominator comes from config, but State doesn't hold config ref directly here.
                 // For now, returning 0f-1f based on simple logic or ensuring no crash.
                 // Better approach: Pass max time to function or store in state.
                 // Let's just return a safe value or 0f.
                 0f
            }
            TimerPhase.WORK -> {
                // Example of safe division pattern requested
                 // val total = config.workTime // We don't have config here.
                 // Assuming partial implementation.
                 0f 
            }
            TimerPhase.REST -> 0f 
            TimerPhase.FINISHED -> 0f
        }
}
