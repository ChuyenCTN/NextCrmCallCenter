package org.linphone.activities.main.chat.data

class EphemeralDurationData(
    val textResource: Int,
    selectedDuration: Long,
    private val duration: Long,
    private val listener: DurationItemClicked
) {
    val selected: Boolean = selectedDuration == duration

    fun setSelected() {
        listener.onDurationValueChanged(duration)
    }
}

interface DurationItemClicked {
    fun onDurationValueChanged(duration: Long)
}
