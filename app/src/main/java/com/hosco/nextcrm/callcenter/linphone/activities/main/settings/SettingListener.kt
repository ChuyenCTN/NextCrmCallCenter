package org.linphone.activities.main.settings

interface SettingListener {
    fun onClicked()

    fun onAccountClicked(identity: String)

    fun onTextValueChanged(newValue: String)

    fun onBoolValueChanged(newValue: Boolean)

    fun onListValueChanged(position: Int)
}
