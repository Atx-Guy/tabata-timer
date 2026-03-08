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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.currentBackStackEntryAsState

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
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val isOnTimerScreen = navBackStackEntry?.destination?.route?.startsWith("timer/") == true
                    val appContainer = (application as TabataApplication).container
                    val soundManager = appContainer.soundManager
                    
                    // Music Controller
                    val musicController = remember { com.ryan.tabatatimer.data.repository.MusicController(context) }
                    val musicState = musicController.musicState.collectAsState()

                    // Release sound resources when activity is destroyed
                    DisposableEffect(Unit) {
                        onDispose {
                            soundManager.release()
                            musicController.release()
                        }
                    }

                    androidx.compose.material3.Scaffold(
                        bottomBar = {
                            if (musicState.value.isPlaying && isOnTimerScreen) {
                                com.ryan.tabatatimer.ui.components.MiniPlayer(
                                    musicState = musicState.value,
                                    onPlayPause = { musicController.playPause() },
                                    onSkipNext = { musicController.skipNext() },
                                    onSkipPrevious = { musicController.skipPrevious() }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController, 
                            startDestination = Screen.Setup.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
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
}
