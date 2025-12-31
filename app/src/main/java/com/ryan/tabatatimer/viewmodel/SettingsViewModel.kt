package com.ryan.tabatatimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ryan.tabatatimer.TabataApplication
import com.ryan.tabatatimer.data.repository.WorkoutRepository
import com.ryan.tabatatimer.model.Workout
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WorkoutRepository) : ViewModel() {
    
    val savedWorkouts: StateFlow<List<Workout>> = repository.allWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveWorkout(name: String, work: Int, rest: Int, rounds: Int, warmup: Int) {
        viewModelScope.launch {
            val workout = Workout(
                name = name,
                workDurationSeconds = work,
                restDurationSeconds = rest,
                rounds = rounds,
                warmupSeconds = warmup
            )
            repository.insertWorkout(workout)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TabataApplication)
                val repository = application.container.workoutRepository
                SettingsViewModel(repository)
            }
        }
    }
}
