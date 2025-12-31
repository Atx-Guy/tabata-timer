package com.ryan.tabatatimer.navigation

import com.ryan.tabatatimer.model.Workout
import android.net.Uri
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Timer : Screen("timer/{workoutJson}") {
        fun createRoute(workout: Workout): String {
            val json = Uri.encode(Gson().toJson(workout))
            return "timer/$json"
        }
    }
}
