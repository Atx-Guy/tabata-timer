package com.ryan.tabatatimer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ryan.tabatatimer.TabataApplication
import com.ryan.tabatatimer.data.repository.WorkoutRepository
import com.ryan.tabatatimer.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val savedWorkouts: StateFlow<List<Workout>> = repository.allWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Editor State
    private val _editorName = MutableStateFlow("My Tabata")
    val editorName: StateFlow<String> = _editorName.asStateFlow()

    private val _editorWork = MutableStateFlow(20)
    val editorWork: StateFlow<Int> = _editorWork.asStateFlow()

    private val _editorRest = MutableStateFlow(10)
    val editorRest: StateFlow<Int> = _editorRest.asStateFlow()

    private val _editorRounds = MutableStateFlow(8)
    val editorRounds: StateFlow<Int> = _editorRounds.asStateFlow()

    private val _editorWarmup = MutableStateFlow(5)
    val editorWarmup: StateFlow<Int> = _editorWarmup.asStateFlow()

    private val _editorWorkoutId = MutableStateFlow<Long?>(null)
    val editorWorkoutId: StateFlow<Long?> = _editorWorkoutId.asStateFlow()

    fun updateEditorState(
        name: String? = null,
        work: Int? = null,
        rest: Int? = null,
        rounds: Int? = null,
        warmup: Int? = null
    ) {
        name?.let { _editorName.value = it }
        work?.let { _editorWork.value = it }
        rest?.let { _editorRest.value = it }
        rounds?.let { _editorRounds.value = it }
        warmup?.let { _editorWarmup.value = it }
    }

    fun onEditRequest(workout: Workout) {
        _editorName.value = workout.name
        _editorWork.value = workout.workDurationSeconds
        _editorRest.value = workout.restDurationSeconds
        _editorRounds.value = workout.rounds
        _editorWarmup.value = workout.warmupSeconds
        _editorWorkoutId.value = workout.id
    }

    fun clearEditor() {
        _editorName.value = "My Tabata"
        _editorWork.value = 20
        _editorRest.value = 10
        _editorRounds.value = 8
        _editorWarmup.value = 5
        _editorWorkoutId.value = null
    }

    fun saveOrUpdateWorkout() {
        val currentId = _editorWorkoutId.value
        val workout = Workout(
            id = currentId ?: 0, // 0 triggers auto-increment for Insert
            name = _editorName.value,
            workDurationSeconds = _editorWork.value,
            restDurationSeconds = _editorRest.value,
            rounds = _editorRounds.value,
            warmupSeconds = _editorWarmup.value
        )

        viewModelScope.launch {
            if (currentId != null) {
                repository.updateWorkout(workout)
            } else {
                repository.insertWorkout(workout)
            }
            clearEditor()
        }
    }

    fun saveWorkout(name: String, work: Int, rest: Int, rounds: Int, warmup: Int) {
        val workout = Workout(
            name = name,
            workDurationSeconds = work,
            restDurationSeconds = rest,
            rounds = rounds,
            warmupSeconds = warmup
        )
        viewModelScope.launch {
            repository.insertWorkout(workout)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
            // If the deleted workout was being edited, clear the editor
            if (_editorWorkoutId.value == workout.id) {
                clearEditor()
            }
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
