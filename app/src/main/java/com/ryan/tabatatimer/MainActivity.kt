package com.ryan.tabatatimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.ryan.tabatatimer.model.Workout
import com.ryan.tabatatimer.navigation.Screen
import com.ryan.tabatatimer.ui.SetupScreen
import com.ryan.tabatatimer.ui.TimerScreen
import com.ryan.tabatatimer.ui.theme.TabataTimerTheme
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TabataTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            // Handle permission granted or denied if needed
                        }
                    )

                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    val navController = rememberNavController()
                    val appContainer = (application as TabataApplication).container
                    val soundManager = appContainer.soundManager
                    
                    // Release sound resources when activity is destroyed
                    DisposableEffect(Unit) {
                        onDispose {
                            soundManager.release()
                        }
                    }

                    NavHost(navController = navController, startDestination = Screen.Setup.route) {
                        composable(Screen.Setup.route) {
                            SetupScreen(
                                onStartTimer = { workout ->
                                    navController.navigate(Screen.Timer.createRoute(workout))
                                },
                                soundManager = soundManager
                            )
                        }
                        
                        composable(
                            route = Screen.Timer.route,
                            arguments = listOf(navArgument("workoutJson") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val workoutJson = backStackEntry.arguments?.getString("workoutJson")
                            
                            if (workoutJson != null) {
                                TimerScreen(
                                    workoutJson = workoutJson,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
