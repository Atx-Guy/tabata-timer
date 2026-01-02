package com.ryan.tabatatimer.service

import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        
        // Add some dummy media items for testing
        val mediaItem1 = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3")
        val mediaItem2 = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/jazz_in_paris.mp3")
        player.addMediaItem(mediaItem1)
        player.addMediaItem(mediaItem2)
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_ALL

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
