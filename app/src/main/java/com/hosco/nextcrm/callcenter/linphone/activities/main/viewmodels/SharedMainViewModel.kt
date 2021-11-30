package org.linphone.activities.main.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.linphone.activities.main.history.data.GroupedCallLogData
import org.linphone.contact.Contact
import org.linphone.core.*
import org.linphone.utils.Event

class SharedMainViewModel : ViewModel() {
    val toggleDrawerEvent = MutableLiveData<Event<Boolean>>()

    val layoutChangedEvent = MutableLiveData<Event<Boolean>>()
    var isSlidingPaneSlideable = MutableLiveData<Boolean>()
    val closeSlidingPaneEvent = MutableLiveData<Event<Boolean>>()

    /* Call history */

    val selectedCallLogGroup = MutableLiveData<GroupedCallLogData>()

    /* Chat */

    val chatRoomFragmentOpenedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val selectedChatRoom = MutableLiveData<ChatRoom>()
    var destructionPendingChatRoom: ChatRoom? = null

    val selectedGroupChatRoom = MutableLiveData<ChatRoom>()

    val filesToShare = MutableLiveData<ArrayList<String>>()

    val textToShare = MutableLiveData<String>()

    val messageToForwardEvent: MutableLiveData<Event<ChatMessage>> by lazy {
        MutableLiveData<Event<ChatMessage>>()
    }

    val isPendingMessageForward = MutableLiveData<Boolean>()

    val contentToOpen = MutableLiveData<Content>()

    var createEncryptedChatRoom: Boolean = false

    val chatRoomParticipants = MutableLiveData<ArrayList<Address>>()

    var chatRoomSubject: String = ""

    // When using keyboard to share gif or other, see RichContentReceiver & RichEditText classes
    val richContentUri = MutableLiveData<Event<Uri>>()

    /* Contacts */

    val contactFragmentOpenedEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val selectedContact = MutableLiveData<Contact>()

    // For correct animations directions
    val updateContactsAnimationsBasedOnDestination: MutableLiveData<Event<Int>> by lazy {
        MutableLiveData<Event<Int>>()
    }

    /* Accounts */

    val accountRemoved = MutableLiveData<Boolean>()

    /* Call */

    var pendingCallTransfer: Boolean = false

    /* Dialer */

    var dialerUri: String = ""

    // For correct animations directions
    val updateDialerAnimationsBasedOnDestination: MutableLiveData<Event<Int>> by lazy {
        MutableLiveData<Event<Int>>()
    }
}
