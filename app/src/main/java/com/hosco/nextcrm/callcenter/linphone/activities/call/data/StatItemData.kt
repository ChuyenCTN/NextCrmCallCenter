package org.linphone.activities.call.data

import androidx.lifecycle.MutableLiveData
import com.hosco.nextcrm.callcenter.R
import org.linphone.core.AddressFamily
import org.linphone.core.Call
import org.linphone.core.CallStats
import org.linphone.core.StreamType
import java.text.DecimalFormat

enum class StatType(val nameResource: Int) {
    CAPTURE(R.string.call_stats_capture_filter),
    PLAYBACK(R.string.call_stats_player_filter),
    PAYLOAD(R.string.call_stats_codec),
    ENCODER(R.string.call_stats_encoder_name),
    DECODER(R.string.call_stats_decoder_name),
    DOWNLOAD_BW(R.string.call_stats_download),
    UPLOAD_BW(R.string.call_stats_upload),
    ICE(R.string.call_stats_ice),
    IP_FAM(R.string.call_stats_ip),
    SENDER_LOSS(R.string.call_stats_sender_loss_rate),
    RECEIVER_LOSS(R.string.call_stats_receiver_loss_rate),
    JITTER(R.string.call_stats_jitter_buffer),
    SENT_RESOLUTION(R.string.call_stats_video_resolution_sent),
    RECEIVED_RESOLUTION(R.string.call_stats_video_resolution_received),
    SENT_FPS(R.string.call_stats_video_fps_sent),
    RECEIVED_FPS(R.string.call_stats_video_fps_received),
    ESTIMATED_AVAILABLE_DOWNLOAD_BW(R.string.call_stats_estimated_download)
}

class StatItemData(val type: StatType) {
    val value = MutableLiveData<String>()

    fun update(call: Call, stats: CallStats) {
        val payloadType = if (stats.type == StreamType.Audio) call.currentParams.usedAudioPayloadType else call.currentParams.usedVideoPayloadType
        payloadType ?: return
        value.value = when (type) {
            StatType.CAPTURE -> if (stats.type == StreamType.Audio) call.core.captureDevice else call.core.videoDevice
            StatType.PLAYBACK -> if (stats.type == StreamType.Audio) call.core.playbackDevice else call.core.videoDisplayFilter
            StatType.PAYLOAD -> "${payloadType.mimeType}/${payloadType.clockRate / 1000} kHz"
            StatType.ENCODER -> call.core.mediastreamerFactory.getDecoderText(payloadType.mimeType)
            StatType.DECODER -> call.core.mediastreamerFactory.getEncoderText(payloadType.mimeType)
            StatType.DOWNLOAD_BW -> "${stats.downloadBandwidth} kbits/s"
            StatType.UPLOAD_BW -> "${stats.uploadBandwidth} kbits/s"
            StatType.ICE -> stats.iceState.toString()
            StatType.IP_FAM -> if (stats.ipFamilyOfRemote == AddressFamily.Inet6) "IPv6" else "IPv4"
            StatType.SENDER_LOSS -> DecimalFormat("##.##%").format(stats.senderLossRate)
            StatType.RECEIVER_LOSS -> DecimalFormat("##.##%").format(stats.receiverLossRate)
            StatType.JITTER -> DecimalFormat("##.## ms").format(stats.jitterBufferSizeMs)
            StatType.SENT_RESOLUTION -> call.currentParams.sentVideoDefinition?.name
            StatType.RECEIVED_RESOLUTION -> call.currentParams.receivedVideoDefinition?.name
            StatType.SENT_FPS -> "${call.currentParams.sentFramerate}"
            StatType.RECEIVED_FPS -> "${call.currentParams.receivedFramerate}"
            StatType.ESTIMATED_AVAILABLE_DOWNLOAD_BW -> "${stats.estimatedDownloadBandwidth} kbit/s"
        }
    }
}
