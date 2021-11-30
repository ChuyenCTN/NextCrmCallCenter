package org.linphone.contact

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ContactAvatarBinding
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.ChatRoomSecurityLevel
import org.linphone.utils.AppUtils

class ContactAvatarView : LinearLayout {
    lateinit var binding: ContactAvatarBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    fun init(context: Context) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.contact_avatar, this, true
        )
    }

    fun setData(data: ContactDataInterface) {
        val contact: Contact? = data.contact.value
        val initials = if (contact != null) {
            AppUtils.getInitials(contact.fullName ?: contact.firstName + " " + contact.lastName)
        } else {
            AppUtils.getInitials(data.displayName.value ?: "")
        }

        binding.initials = initials
        binding.generatedAvatarVisibility = initials.isNotEmpty() && initials != "+"
        binding.groupChatAvatarVisibility = data.showGroupChatAvatar

        binding.imagePath = contact?.getContactThumbnailPictureUri()
        binding.borderVisibility = corePreferences.showBorderOnContactAvatar

        binding.securityIcon = when (data.securityLevel.value) {
            ChatRoomSecurityLevel.Safe -> R.drawable.security_2_indicator
            ChatRoomSecurityLevel.Encrypted -> R.drawable.security_1_indicator
            else -> R.drawable.security_alert_indicator
        }
        binding.securityContentDescription = when (data.securityLevel.value) {
            ChatRoomSecurityLevel.Safe -> R.string.content_description_security_level_safe
            ChatRoomSecurityLevel.Encrypted -> R.string.content_description_security_level_encrypted
            else -> R.string.content_description_security_level_unsafe
        }
    }
}
