package org.linphone.activities.main.chat

import org.linphone.core.Address
import org.linphone.core.ChatRoomSecurityLevel

data class GroupChatRoomMember(
    val address: Address,
    var isAdmin: Boolean = false,
    val securityLevel: ChatRoomSecurityLevel = ChatRoomSecurityLevel.ClearText,
    val hasLimeX3DHCapability: Boolean = false,
    // A participant not yet added to a group can't be set admin at the same time it's added
    val canBeSetAdmin: Boolean = false
)
