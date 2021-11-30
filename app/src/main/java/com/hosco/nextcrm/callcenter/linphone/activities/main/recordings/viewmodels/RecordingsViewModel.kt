package org.linphone.activities.main.recordings.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media.AudioFocusRequestCompat
import kotlin.collections.ArrayList
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.main.recordings.data.RecordingData
import org.linphone.core.tools.Log
import org.linphone.utils.AppUtils
import org.linphone.utils.FileUtils

class RecordingsViewModel : ViewModel() {
    val recordingsList = MutableLiveData<ArrayList<RecordingData>>()

    val isVideoVisible = MutableLiveData<Boolean>()

    private var recordingPlayingAudioFocusRequest: AudioFocusRequestCompat? = null

    private val recordingListener = object : RecordingData.RecordingListener {
        override fun onPlayingStarted(videoAvailable: Boolean) {
            if (recordingPlayingAudioFocusRequest == null) {
                recordingPlayingAudioFocusRequest = AppUtils.acquireAudioFocusForVoiceRecordingOrPlayback(
                    coreContext.context
                )
            }

            isVideoVisible.value = videoAvailable
        }

        override fun onPlayingEnded() {
            val request = recordingPlayingAudioFocusRequest
            if (request != null) {
                AppUtils.releaseAudioFocusForVoiceRecordingOrPlayback(coreContext.context, request)
                recordingPlayingAudioFocusRequest = null
            }

            isVideoVisible.value = false
        }
    }

    init {
        getRecordings()
        isVideoVisible.value = false
    }

    override fun onCleared() {
        recordingsList.value.orEmpty().forEach(RecordingData::destroy)

        val request = recordingPlayingAudioFocusRequest
        if (request != null) {
            AppUtils.releaseAudioFocusForVoiceRecordingOrPlayback(coreContext.context, request)
            recordingPlayingAudioFocusRequest = null
        }

        super.onCleared()
    }

    fun deleteRecordings(list: ArrayList<RecordingData>) {
        for (recording in list) {
            // Hide video when removing a recording being played with video.
            if (recording.isPlaying.value == true && recording.isVideoAvailable()) {
                isVideoVisible.value = false
            }

            Log.i("[Recordings] Deleting recording ${recording.path}")
            FileUtils.deleteFile(recording.path)
        }

        getRecordings()
    }

    private fun getRecordings() {
        recordingsList.value.orEmpty().forEach(RecordingData::destroy)
        val list = arrayListOf<RecordingData>()

        for (f in FileUtils.getFileStorageDir().listFiles().orEmpty()) {
            Log.i("[Recordings] Found file ${f.path}")
            if (RecordingData.RECORD_PATTERN.matcher(f.path).matches()) {
                list.add(
                    RecordingData(
                        f.path,
                        recordingListener
                    )
                )
                Log.i("[Recordings] Found record ${f.path}")
            }
        }

        list.sort()
        recordingsList.value = list
    }
}
