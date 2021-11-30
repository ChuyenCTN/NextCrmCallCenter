package org.linphone.telecom

import android.graphics.drawable.Icon
import android.os.Bundle
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.telecom.StatusHints
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.Call
import org.linphone.core.tools.Log
import org.linphone.utils.AudioRouteUtils

class NativeCallWrapper(var callId: String) : Connection() {
    init {
        val capabilities = connectionCapabilities or CAPABILITY_MUTE or CAPABILITY_SUPPORT_HOLD or CAPABILITY_HOLD
        connectionCapabilities = capabilities
        audioModeIsVoip = true
        statusHints = StatusHints(
            "",
            Icon.createWithResource(coreContext.context, R.drawable.linphone_logo_tinted),
            Bundle()
        )
    }

    override fun onStateChanged(state: Int) {
        Log.i("[Connection] Telecom state changed [$state] for call with id: $callId")
        super.onStateChanged(state)
    }

    override fun onAnswer(videoState: Int) {
        Log.i("[Connection] Answering telecom call with id: $callId")
        getCall()?.accept()
    }

    override fun onHold() {
        Log.i("[Connection] Pausing telecom call with id: $callId")
        getCall()?.pause()
        setOnHold()
    }

    override fun onUnhold() {
        Log.i("[Connection] Resuming telecom call with id: $callId")
        getCall()?.resume()
        setActive()
    }

    override fun onCallAudioStateChanged(state: CallAudioState) {
        Log.i("[Connection] Audio state changed: $state")

        val call = getCall()
        call?.microphoneMuted = state.isMuted
        when (state.route) {
            CallAudioState.ROUTE_EARPIECE -> AudioRouteUtils.routeAudioToEarpiece(call)
            CallAudioState.ROUTE_SPEAKER -> AudioRouteUtils.routeAudioToSpeaker(call)
            CallAudioState.ROUTE_BLUETOOTH -> AudioRouteUtils.routeAudioToBluetooth(call)
            CallAudioState.ROUTE_WIRED_HEADSET -> AudioRouteUtils.routeAudioToHeadset(call)
        }
    }

    override fun onPlayDtmfTone(c: Char) {
        Log.i("[Connection] Sending DTMF [$c] in telecom call with id: $callId")
        getCall()?.sendDtmf(c)
    }

    override fun onDisconnect() {
        Log.i("[Connection] Terminating telecom call with id: $callId")
        getCall()?.terminate()
    }

    override fun onAbort() {
        Log.i("[Connection] Aborting telecom call with id: $callId")
        getCall()?.terminate()
    }

    override fun onReject() {
        Log.i("[Connection] Rejecting telecom call with id: $callId")
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        getCall()?.terminate()
    }

    private fun getCall(): Call? {
        return coreContext.core.getCallByCallid(callId)
    }
}
