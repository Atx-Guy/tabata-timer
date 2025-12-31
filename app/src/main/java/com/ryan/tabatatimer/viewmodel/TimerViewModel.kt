package com.ryan.tabatatimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryan.tabatatimer.model.TimerConfig
import com.ryan.tabatatimer.model.TimerPhase
import com.ryan.tabatatimer.model.TimerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private val _config = MutableStateFlow(TimerConfig())
    val config: StateFlow<TimerConfig> = _config.asStateFlow()

    private var timerJob: Job? = null

    init {
        resetTimer()
    }

    fun toggleTimer() {
        if (_state.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        if (_state.value.phase == TimerPhase.FINISHED) {
            resetTimer()
        }
        _state.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_state.value.isRunning) {
                delay(1000L) // 1 second tick
                tick()
            }
        }
    }

    private fun pauseTimer() {
        _state.update { it.copy(isRunning = false) }
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerJob?.cancel()
        val currentConfig = _config.value
        _state.value = TimerState(
            phase = TimerPhase.PREPARE,
            timeRemainingSeconds = currentConfig.prepareTimeSeconds,
            currentRound = 1,
            totalRounds = currentConfig.totalRounds,
            isRunning = false,
            totalTimeElapsedSeconds = 0
        )
    }

    private fun tick() {
        val currentState = _state.value
        val currentConfig = _config.value

        if (currentState.timeRemainingSeconds > 1) {
            _state.update { 
                it.copy(
                    timeRemainingSeconds = it.timeRemainingSeconds - 1,
                    totalTimeElapsedSeconds = it.totalTimeElapsedSeconds + 1
                ) 
            }
        } else {
            // Phase transition
            transitionPhase(currentState, currentConfig)
        }
    }

    private fun transitionPhase(currentState: TimerState, config: TimerConfig) {
        when (currentState.phase) {
            TimerPhase.PREPARE -> {
                _state.update {
                    it.copy(
                        phase = TimerPhase.WORK,
                        timeRemainingSeconds = config.workTimeSeconds,
                        totalTimeElapsedSeconds = it.totalTimeElapsedSeconds + 1
                    )
                }
            }
            TimerPhase.WORK -> {
                if (currentState.currentRound < config.totalRounds) {
                    _state.update {
                        it.copy(
                            phase = TimerPhase.REST,
                            timeRemainingSeconds = config.restTimeSeconds,
                            totalTimeElapsedSeconds = it.totalTimeElapsedSeconds + 1
                        )
                    }
                } else {
                    finishTimer()
                }
            }
            TimerPhase.REST -> {
                _state.update {
                    it.copy(
                        phase = TimerPhase.WORK,
                        timeRemainingSeconds = config.workTimeSeconds,
                        currentRound = it.currentRound + 1,
                        totalTimeElapsedSeconds = it.totalTimeElapsedSeconds + 1
                    )
                }
            }
            TimerPhase.FINISHED -> {
                // Should not happen if logic is correct
                pauseTimer()
            }
        }
    }

    private fun finishTimer() {
        _state.update { 
            it.copy(
                phase = TimerPhase.FINISHED, 
                timeRemainingSeconds = 0,
                isRunning = false
            ) 
        }
        timerJob?.cancel()
    }
    
    fun updateConfig(newConfig: TimerConfig) {
        _config.value = newConfig
        resetTimer()
    }
}
