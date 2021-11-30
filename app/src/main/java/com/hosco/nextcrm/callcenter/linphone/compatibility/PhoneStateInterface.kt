package org.linphone.compatibility

interface PhoneStateInterface {
    fun destroy()

    fun isInCall(): Boolean
}
