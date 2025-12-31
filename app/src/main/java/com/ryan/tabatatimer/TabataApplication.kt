package com.ryan.tabatatimer

import android.app.Application
import android.content.Context
import com.ryan.tabatatimer.data.local.AppDatabase
import com.ryan.tabatatimer.data.repository.WorkoutRepository

class TabataApplication : Application() {
    // Manual Dependency Injection Container
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDefaultContainer(this)
    }
}

interface AppContainer {
    val workoutRepository: WorkoutRepository
    val soundManager: com.ryan.tabatatimer.util.SoundManager
}

class AppDefaultContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val workoutRepository: WorkoutRepository by lazy {
        WorkoutRepository(database.workoutDao())
    }

    override val soundManager: com.ryan.tabatatimer.util.SoundManager by lazy {
        com.ryan.tabatatimer.util.SoundManager(context)
    }
}
