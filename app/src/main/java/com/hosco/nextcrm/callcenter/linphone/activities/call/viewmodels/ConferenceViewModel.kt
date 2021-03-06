package org.linphone.activities.call.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.call.data.ConferenceParticipantData
import org.linphone.core.*
import org.linphone.core.tools.Log

class ConferenceViewModel : ViewModel() {
    val isConferencePaused = MutableLiveData<Boolean>()

    val isMeConferenceFocus = MutableLiveData<Boolean>()

    val conferenceAddress = MutableLiveData<Address>()

    val conferenceParticipants = MutableLiveData<List<ConferenceParticipantData>>()

    val isInConference = MutableLiveData<Boolean>()

    private val conferenceListener = object : ConferenceListenerStub() {
        override fun onParticipantAdded(conference: Conference, participant: Participant) {
            if (conference.isMe(participant.address)) {
                Log.i("[Conference VM] Entered conference")
                isConferencePaused.value = false
            } else {
                Log.i("[Conference VM] Participant added")
                updateParticipantsList(conference)
            }
        }

        override fun onParticipantRemoved(conference: Conference, participant: Participant) {
            if (conference.isMe(participant.address)) {
                Log.i("[Conference VM] Left conference")
                isConferencePaused.value = true
            } else {
                Log.i("[Conference VM] Participant removed")
                updateParticipantsList(conference)
            }
        }

        override fun onParticipantAdminStatusChanged(
            conference: Conference,
            participant: Participant
        ) {
            Log.i("[Conference VM] Participant admin status changed")
            updateParticipantsList(conference)
        }
    }

    private val listener = object : CoreListenerStub() {
        override fun onConferenceStateChanged(
            core: Core,
            conference: Conference,
            state: Conference.State
        ) {
            Log.i("[Conference VM] Conference state changed: $state")
            isConferencePaused.value = !conference.isIn

            if (state == Conference.State.Instantiated) {
                conference.addListener(conferenceListener)
            } else if (state == Conference.State.Created) {
                updateParticipantsList(conference)
                isMeConferenceFocus.value = conference.me.isFocus
                conferenceAddress.value = conference.conferenceAddress
            } else if (state == Conference.State.Terminated || state == Conference.State.TerminationFailed) {
                isInConference.value = false
                conference.removeListener(conferenceListener)
                conferenceParticipants.value = arrayListOf()
            }
        }
    }

    init {
        coreContext.core.addListener(listener)

        isConferencePaused.value = coreContext.core.conference?.isIn != true
        isMeConferenceFocus.value = false
        conferenceParticipants.value = arrayListOf()
        isInConference.value = false

        val conference = coreContext.core.conference
        if (conference != null) {
            conference.addListener(conferenceListener)
            isMeConferenceFocus.value = conference.me.isFocus
            updateParticipantsList(conference)
        }
    }

    override fun onCleared() {
        coreContext.core.removeListener(listener)

        super.onCleared()
    }

    fun pauseConference() {
        val defaultProxyConfig = coreContext.core.defaultProxyConfig
        val localAddress = defaultProxyConfig?.identityAddress
        val participants = arrayOf<Address>()
        val remoteConference = coreContext.core.searchConference(null, localAddress, conferenceAddress.value, participants)
        val localConference = coreContext.core.searchConference(null, conferenceAddress.value, conferenceAddress.value, participants)
        val conference = remoteConference ?: localConference

        if (conference != null) {
            Log.i("[Conference VM] Leaving conference with address ${conferenceAddress.value?.asStringUriOnly()} temporarily")
            conference.leave()
        } else {
            Log.w("[Conference VM] Unable to find conference with address ${conferenceAddress.value?.asStringUriOnly()}")
        }
    }

    fun resumeConference() {
        val defaultProxyConfig = coreContext.core.defaultProxyConfig
        val localAddress = defaultProxyConfig?.identityAddress
        val participants = arrayOf<Address>()
        val remoteConference = coreContext.core.searchConference(null, localAddress, conferenceAddress.value, participants)
        val localConference = coreContext.core.searchConference(null, conferenceAddress.value, conferenceAddress.value, participants)
        val conference = remoteConference ?: localConference

        if (conference != null) {
            Log.i("[Conference VM] Entering again conference with address ${conferenceAddress.value?.asStringUriOnly()}")
            conference.enter()
        } else {
            Log.w("[Conference VM] Unable to find conference with address ${conferenceAddress.value?.asStringUriOnly()}")
        }
    }

    private fun updateParticipantsList(conference: Conference) {
        val participants = arrayListOf<ConferenceParticipantData>()
        for (participant in conference.participantList) {
            Log.i("[Conference VM] Participant found: ${participant.address.asStringUriOnly()}")
            val viewModel = ConferenceParticipantData(conference, participant)
            participants.add(viewModel)
        }
        conferenceParticipants.value = participants
        isInConference.value = participants.isNotEmpty()
    }
}
