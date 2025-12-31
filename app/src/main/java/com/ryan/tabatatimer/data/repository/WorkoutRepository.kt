package com.ryan.tabatatimer.data.repository

import com.ryan.tabatatimer.data.local.WorkoutDao
import com.ryan.tabatatimer.model.Workout
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()

    suspend fun getWorkoutById(id: Long): Workout? {
        return workoutDao.getWorkoutById(id)
    }

    suspend fun insertWorkout(workout: Workout) {
        workoutDao.insertWorkout(workout)
    }

    suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout)
    }

    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }
}
