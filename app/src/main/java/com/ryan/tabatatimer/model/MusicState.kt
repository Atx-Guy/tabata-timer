package com.ryan.tabatatimer.model

data class MusicState(
    val title: String = "Not Playing",
    val artist: String = "",
    val isPlaying: Boolean = false,
    val durationMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val buffering: Boolean = false
)
