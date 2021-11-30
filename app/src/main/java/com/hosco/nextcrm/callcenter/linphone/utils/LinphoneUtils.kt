package org.linphone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager.*
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.*
import org.linphone.core.tools.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import com.hosco.nextcrm.callcenter.R

/**
 * Various utility methods for Linphone SDK
 */
class LinphoneUtils {
    companion object {
        private const val RECORDING_DATE_PATTERN = "dd-MM-yyyy-HH-mm-ss"

        fun getDisplayName(address: Address): String {
            if (address.displayName == null) {
                val account = coreContext.core.accountList.find { account ->
                    account.params.identityAddress?.asStringUriOnly() == address.asStringUriOnly()
                }
                val localDisplayName = account?.params?.identityAddress?.displayName
                if (localDisplayName != null) {
                    return localDisplayName
                }
            }
            return address.displayName ?: address.username ?: ""
        }

        fun getDisplayableAddress(address: Address?): String {
            if (address == null) return "[null]"
            return if (corePreferences.replaceSipUriByUsername) {
                address.username ?: address.asStringUriOnly()
            } else {
                val copy = address.clone()
                copy.clean() // To remove gruu if any
                copy.asStringUriOnly()
            }
        }

        fun getDisplayableAddressCrm(address: Address?): String {
            if (address == null) return ""
            return address.displayName.toString()
        }

        fun isLimeAvailable(): Boolean {
            val core = coreContext.core
            return core.limeX3DhAvailable() && core.limeX3DhEnabled() &&
                    core.limeX3DhServerUrl != null &&
                    core.defaultAccount?.params?.conferenceFactoryUri != null
        }

        fun isGroupChatAvailable(): Boolean {
            val core = coreContext.core
            return core.defaultAccount?.params?.conferenceFactoryUri != null
        }

        fun createOneToOneChatRoom(participant: Address, isSecured: Boolean = false): ChatRoom? {
            val core: Core = coreContext.core
            val defaultAccount = core.defaultAccount

            val params = core.createDefaultChatRoomParams()
            params.enableGroup(false)
            params.backend = ChatRoomBackend.Basic
            if (isSecured) {
                params.subject = AppUtils.getString(R.string.chat_room_dummy_subject)
                params.enableEncryption(true)
                params.backend = ChatRoomBackend.FlexisipChat
            }

            val participants = arrayOf(participant)

            return core.searchChatRoom(params, defaultAccount?.contactAddress, null, participants)
                ?: core.createChatRoom(params, defaultAccount?.contactAddress, participants)
        }

        fun deleteFilesAttachedToEventLog(eventLog: EventLog) {
            if (eventLog.type == EventLog.Type.ConferenceChatMessage) {
                val message = eventLog.chatMessage
                if (message != null) deleteFilesAttachedToChatMessage(message)
            }
        }

        fun deleteFilesAttachedToChatMessage(chatMessage: ChatMessage) {
            for (content in chatMessage.contents) {
                val filePath = content.filePath
                if (filePath != null && filePath.isNotEmpty()) {
                    Log.i("[Linphone Utils] Deleting file $filePath")
                    FileUtils.deleteFile(filePath)
                }
            }
        }

        fun getRecordingFilePathForAddress(address: Address): String {
            val displayName = getDisplayName(address)
            val dateFormat: DateFormat = SimpleDateFormat(
                RECORDING_DATE_PATTERN,
                Locale.getDefault()
            )
            val fileName = "${displayName}_${dateFormat.format(Date())}.mkv"
            return FileUtils.getFileStoragePath(fileName).absolutePath
        }

        fun getRecordingFilePathForConference(): String {
            val dateFormat: DateFormat = SimpleDateFormat(
                RECORDING_DATE_PATTERN,
                Locale.getDefault()
            )
            val fileName = "conference_${dateFormat.format(Date())}.mkv"
            return FileUtils.getFileStoragePath(fileName).absolutePath
        }

        fun getRecordingDateFromFileName(name: String): Date {
            return SimpleDateFormat(RECORDING_DATE_PATTERN, Locale.getDefault()).parse(name)
        }

        @SuppressLint("MissingPermission")
        fun checkIfNetworkHasLowBandwidth(context: Context): Boolean {
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return when (networkInfo.subtype) {
                        NETWORK_TYPE_EDGE, NETWORK_TYPE_GPRS, NETWORK_TYPE_IDEN -> true
                        else -> false
                    }
                }
            }
            // In doubt return false
            return false
        }

        fun isCallLogMissed(callLog: CallLog): Boolean {
            return (
                    callLog.dir == Call.Dir.Incoming &&
                            (
                                    callLog.status == Call.Status.Missed ||
                                            callLog.status == Call.Status.Aborted ||
                                            callLog.status == Call.Status.EarlyAborted
                                    )
                    )
        }

        fun getChatRoomId(localAddress: String, remoteAddress: String): String {
            return "$localAddress~$remoteAddress"
        }
    }
}