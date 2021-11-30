package org.linphone.activities.main.settings.viewmodels

import androidx.lifecycle.MutableLiveData
import java.lang.NumberFormatException
import org.linphone.activities.main.settings.SettingListenerStub

class NetworkSettingsViewModel : GenericSettingsViewModel() {
    val wifiOnlyListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            core.enableWifiOnly(newValue)
        }
    }
    val wifiOnly = MutableLiveData<Boolean>()

    val allowIpv6Listener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            core.enableIpv6(newValue)
        }
    }
    val allowIpv6 = MutableLiveData<Boolean>()

    val randomPortsListener = object : SettingListenerStub() {
        override fun onBoolValueChanged(newValue: Boolean) {
            val port = if (newValue) -1 else 5060
            setTransportPort(port)
            sipPort.value = port
        }
    }
    val randomPorts = MutableLiveData<Boolean>()

    val sipPortListener = object : SettingListenerStub() {
        override fun onTextValueChanged(newValue: String) {
            try {
                val port = newValue.toInt()
                setTransportPort(port)
            } catch (nfe: NumberFormatException) {
            }
        }
    }
    val sipPort = MutableLiveData<Int>()

    init {
        wifiOnly.value = core.wifiOnlyEnabled()
        allowIpv6.value = core.ipv6Enabled()
        randomPorts.value = getTransportPort() == -1
        sipPort.value = getTransportPort()
    }

    private fun setTransportPort(port: Int) {
        val transports = core.transports
        transports.udpPort = port
        transports.tcpPort = port
        transports.tlsPort = -1
        core.transports = transports
    }

    private fun getTransportPort(): Int {
        val transports = core.transports
        if (transports.udpPort > 0) return transports.udpPort
        return transports.tcpPort
    }
}
