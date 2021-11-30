package org.linphone.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.ensureCoreExists
import org.linphone.core.tools.Log

class CorePushReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ensureCoreExists(context.applicationContext, true)
        Log.i("[Push Receiver] Push notification received")
    }
}
