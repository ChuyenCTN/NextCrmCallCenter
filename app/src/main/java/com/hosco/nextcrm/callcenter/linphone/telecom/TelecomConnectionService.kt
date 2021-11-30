package org.linphone.telecom

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.telecom.*
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.Call
import org.linphone.core.Core
import org.linphone.core.CoreListenerStub
import org.linphone.core.tools.Log

class TelecomConnectionService : ConnectionService() {
    private val connections = arrayListOf<NativeCallWrapper>()

    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            Log.i("[Telecom Connection Service] call [${call.callLog.callId}] state changed: $state")
            when (call.state) {
                Call.State.OutgoingProgress -> {
                    for (connection in connections) {
                        if (connection.callId.isEmpty()) {
                            connection.callId = core.currentCall?.callLog?.callId ?: ""
                        }
                    }
                }
                Call.State.End, Call.State.Released -> onCallEnded(call)
                Call.State.Connected -> onCallConnected(call)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("[Telecom Connection Service] onCreate()")
        coreContext.core.addListener(listener)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i("[Telecom Connection Service] onUnbind()")
        coreContext.core.removeListener(listener)

        return super.onUnbind(intent)
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val accountHandle = request.accountHandle
        val componentName = ComponentName(applicationContext, this.javaClass)

        return if (accountHandle != null && componentName == accountHandle.componentName) {
            Log.i("[Telecom Connection Service] Creating outgoing connection")

            val extras = request.extras
            var callId = extras.getString("Call-ID")
            val displayName = extras.getString("DisplayName")
            if (callId == null) {
                callId = coreContext.core.currentCall?.callLog?.callId ?: ""
            }
            Log.i("[Telecom Connection Service] Outgoing connection is for call [$callId] with display name [$displayName]")

            // Prevents user dialing back from native dialer app history
            if (callId.isEmpty() && displayName.isNullOrEmpty()) {
                Log.e("[Telecom Connection Service] Looks like a call was made from native dialer history, aborting")
                return Connection.createFailedConnection(DisconnectCause(DisconnectCause.OTHER))
            }

            val connection = NativeCallWrapper(callId)
            connection.setDialing()

            val providedHandle = request.address
            connection.setAddress(providedHandle, TelecomManager.PRESENTATION_ALLOWED)
            connection.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED)
            Log.i("[Telecom Connection Service] Address is $providedHandle")

            connections.add(connection)
            connection
        } else {
            Log.e("[Telecom Connection Service] Error: $accountHandle $componentName")
            Connection.createFailedConnection(
                DisconnectCause(
                    DisconnectCause.ERROR,
                    "Invalid inputs: $accountHandle $componentName"
                )
            )
        }
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val accountHandle = request.accountHandle
        val componentName = ComponentName(applicationContext, this.javaClass)

        return if (accountHandle != null && componentName == accountHandle.componentName) {
            Log.i("[Telecom Connection Service] Creating incoming connection")

            val extras = request.extras
            val incomingExtras = extras.getBundle(TelecomManager.EXTRA_INCOMING_CALL_EXTRAS)
            var callId = incomingExtras?.getString("Call-ID")
            val displayName = incomingExtras?.getString("DisplayName")
            if (callId == null) {
                callId = coreContext.core.currentCall?.callLog?.callId ?: ""
            }
            Log.i("[Telecom Connection Service] Incoming connection is for call [$callId] with display name [$displayName]")

            val connection = NativeCallWrapper(callId)
            connection.setRinging()

            val providedHandle =
                incomingExtras?.getParcelable<Uri>(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS)
            connection.setAddress(providedHandle, TelecomManager.PRESENTATION_ALLOWED)
            connection.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED)
            Log.i("[Telecom Connection Service] Address is $providedHandle")

            connections.add(connection)
            connection
        } else {
            Log.e("[Telecom Connection Service] Error: $accountHandle $componentName")
            Connection.createFailedConnection(
                DisconnectCause(
                    DisconnectCause.ERROR,
                    "Invalid inputs: $accountHandle $componentName"
                )
            )
        }
    }

    private fun getConnectionForCallId(callId: String): NativeCallWrapper? {
        return connections.find { connection ->
            connection.callId == callId
        }
    }

    private fun onCallEnded(call: Call) {
        val connection = getConnectionForCallId(call.callLog.callId)
        connection ?: return

        connections.remove(connection)
        connection.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        connection.destroy()
    }

    private fun onCallConnected(call: Call) {
        val connection = getConnectionForCallId(call.callLog.callId)
        connection ?: return

        if (connection.state != Connection.STATE_HOLDING) {
            connection.setActive()
        }
    }
}
