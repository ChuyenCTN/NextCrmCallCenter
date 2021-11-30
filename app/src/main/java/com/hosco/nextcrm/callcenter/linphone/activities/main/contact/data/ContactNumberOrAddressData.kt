package org.linphone.activities.main.contact.data

import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.Address

class ContactNumberOrAddressData(
    val address: Address?,
    val hasPresence: Boolean,
    val displayedValue: String,
    val isSip: Boolean = true,
    val showSecureChat: Boolean = false,
    val typeLabel: String = "",
    private val listener: ContactNumberOrAddressClickListener
) {
    val showInvite = !hasPresence && !isSip

    val chatAllowed = !corePreferences.disableChat

    fun startCall() {
        address ?: return
        listener.onCall(address)
    }

    fun startChat(secured: Boolean) {
        address ?: return
        listener.onChat(address, secured)
    }

    fun smsInvite() {
        listener.onSmsInvite(displayedValue)
    }
}

interface ContactNumberOrAddressClickListener {
    fun onCall(address: Address)

    fun onChat(address: Address, isSecured: Boolean)

    fun onSmsInvite(number: String)
}
