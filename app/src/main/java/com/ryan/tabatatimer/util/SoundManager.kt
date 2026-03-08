package com.ryan.tabatatimer.util

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.ryan.tabatatimer.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SoundManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("tabata_prefs", Context.MODE_PRIVATE)
    private val PREF_KEY_AUDIO_ENABLED = "audio_enabled"

    private val _isAudioEnabled = MutableStateFlow(prefs.getBoolean(PREF_KEY_AUDIO_ENABLED, true))
    val isAudioEnabled: StateFlow<Boolean> = _isAudioEnabled.asStateFlow()

    private var workPlayer: MediaPlayer? = null
    private var restPlayer: MediaPlayer? = null

    init {
        // Pre-load players to minimize latency
        // Note: In a production app, we might want to handle this more lazily or carefully for memory,
        // but for a timer with small assets, this creates the snappiest experience.
        loadPlayers()
    }

    private fun loadPlayers() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
            
        try {
            workPlayer = MediaPlayer.create(context, R.raw.work)?.apply {
                setAudioAttributes(audioAttributes)
            }
            restPlayer = MediaPlayer.create(context, R.raw.rest)?.apply {
                setAudioAttributes(audioAttributes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setAudioEnabled(enabled: Boolean) {
        _isAudioEnabled.value = enabled
        prefs.edit().putBoolean(PREF_KEY_AUDIO_ENABLED, enabled).apply()
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
    private val focusRequest = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        android.media.AudioFocusRequest.Builder(android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
    } else {
        null
    }

    fun requestAudioFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.requestAudioFocus(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                null, 
                android.media.AudioManager.STREAM_MUSIC, 
                android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )
        }
    }

    fun abandonAudioFocus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    fun playWorkSound() {
        if (_isAudioEnabled.value) {
            safePlay(workPlayer)
        }
    }

    fun playRestSound() {
        if (_isAudioEnabled.value) {
            safePlay(restPlayer)
        }
    }

    @Synchronized
    private fun safePlay(player: MediaPlayer?) {
        if (player == null) return
        try {
            requestAudioFocus()
            if (player.isPlaying) {
                // Restart from beginning instead of layering the sound
                player.seekTo(0)
            } else {
                player.start()
                player.setOnCompletionListener {
                    abandonAudioFocus()
                    it.setOnCompletionListener(null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Release old broken instances before reloading to prevent memory leak
            releasePlayers()
            loadPlayers()
        }
    }

    private fun releasePlayers() {
        workPlayer?.release()
        restPlayer?.release()
        workPlayer = null
        restPlayer = null
    }

    fun release() {
        releasePlayers()
    }
}
