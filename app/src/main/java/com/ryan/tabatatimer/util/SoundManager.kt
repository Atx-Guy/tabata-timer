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

    private fun safePlay(player: MediaPlayer?) {
        try {
            if (player?.isPlaying == true) {
                player.seekTo(0)
            } else {
                player?.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Try to recover if player died
            loadPlayers()
        }
    }

    fun release() {
        workPlayer?.release()
        restPlayer?.release()
        workPlayer = null
        restPlayer = null
    }
}
