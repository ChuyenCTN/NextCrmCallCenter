package org.linphone.contact

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ContactAvatarBigBinding
import org.linphone.utils.AppUtils

class BigContactAvatarView : LinearLayout {
    lateinit var binding: ContactAvatarBigBinding

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
            LayoutInflater.from(context), R.layout.contact_avatar_big, this, true
        )
    }

    fun setViewModel(viewModel: ContactDataInterface?) {
        if (viewModel == null) {
            binding.root.visibility = View.GONE
            return
        }
        binding.root.visibility = View.VISIBLE

        val contact: Contact? = viewModel.contact.value
        val initials = if (contact != null) {
            AppUtils.getInitials(contact.fullName ?: contact.firstName + " " + contact.lastName)
        } else {
            AppUtils.getInitials(viewModel.displayName.value ?: "")
        }

        binding.initials = initials
        binding.generatedAvatarVisibility = initials.isNotEmpty() && initials != "+"
        binding.imagePath = contact?.getContactPictureUri()
        binding.borderVisibility = corePreferences.showBorderOnBigContactAvatar
    }
}
