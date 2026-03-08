package com.ryan.tabatatimer.viewmodel

import android.content.Context
import android.content.SharedPreferences
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

class SettingsViewModel(
    private val repository: WorkoutRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

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

    private val _isProUser = MutableStateFlow(prefs.getBoolean(PREF_IS_PRO, false))
    val isProUser: StateFlow<Boolean> = _isProUser.asStateFlow()

    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    fun dismissPaywall() { _showPaywall.value = false }

    fun upgradeToPro() {
        _isProUser.value = true
        prefs.edit().putBoolean(PREF_IS_PRO, true).apply()
    }

    fun saveOrUpdateWorkout() {
        val currentId = _editorWorkoutId.value
        val isUpdate = currentId != null
        
        // Entitlement Check:
        // Free users can only have 1 saved workout.
        // Updates to existing workouts are allowed.
        // Creation of new workouts beyond limit is blocked.
        if (!isUpdate && !_isProUser.value && savedWorkouts.value.isNotEmpty()) {
             _showPaywall.value = true
             return
        }

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
        private const val PREF_IS_PRO = "is_pro_user"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TabataApplication)
                val repository = application.container.workoutRepository
                val prefs = application.getSharedPreferences("tabata_prefs", Context.MODE_PRIVATE)
                SettingsViewModel(repository, prefs)
            }
        }
    }
}
