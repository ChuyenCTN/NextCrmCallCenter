package org.linphone.activities.main.files.viewmodels

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.MediaController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalStateException
import org.linphone.core.Content

class AudioFileViewModelFactory(private val content: Content) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AudioFileViewModel(content) as T
    }
}

class AudioFileViewModel(content: Content) : FileViewerViewModel(content), MediaController.MediaPlayerControl {
    val mediaPlayer = MediaPlayer()

    init {
        mediaPlayer.apply {
            setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    override fun onCleared() {
        mediaPlayer.release()
        super.onCleared()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override fun getCurrentPosition(): Int {
        try {
            return mediaPlayer.currentPosition
        } catch (ise: IllegalStateException) {}
        return 0
    }

    override fun seekTo(pos: Int) {
        mediaPlayer.seekTo(pos)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return mediaPlayer.audioSessionId
    }
}
