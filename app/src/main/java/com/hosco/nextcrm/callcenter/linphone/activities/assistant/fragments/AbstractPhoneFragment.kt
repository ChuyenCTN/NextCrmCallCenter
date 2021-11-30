
package org.linphone.activities.assistant.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.databinding.ViewDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hosco.nextcrm.callcenter.R
import org.linphone.activities.GenericFragment
import org.linphone.activities.assistant.viewmodels.AbstractPhoneViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.PermissionHelper
import org.linphone.utils.PhoneNumberUtils

abstract class AbstractPhoneFragment<T : ViewDataBinding> : GenericFragment<T>() {
    abstract val viewModel: AbstractPhoneViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("[Assistant] READ_PHONE_NUMBERS permission granted")
                updateFromDeviceInfo()
            } else {
                Log.w("[Assistant] READ_PHONE_NUMBERS permission denied")
            }
        }
    }

    protected fun checkPermission() {
        if (!resources.getBoolean(R.bool.isTablet)) {
            if (!PermissionHelper.get().hasReadPhoneState()) {
                Log.i("[Assistant] Asking for READ_PHONE_STATE permission")
                requestPermissions(arrayOf(Manifest.permission.READ_PHONE_STATE), 0)
            } else {
                updateFromDeviceInfo()
            }
        }
    }

    private fun updateFromDeviceInfo() {
        val phoneNumber = PhoneNumberUtils.getDevicePhoneNumber(requireContext())
        val dialPlan = PhoneNumberUtils.getDialPlanForCurrentCountry(requireContext())
        viewModel.updateFromPhoneNumberAndOrDialPlan(phoneNumber, dialPlan)
    }

    protected fun showPhoneNumberInfoDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.assistant_phone_number_info_title))
            .setMessage(
                getString(R.string.assistant_phone_number_link_info_content) + "\n" +
                    getString(
                        R.string.assistant_phone_number_link_info_content_already_account
                    )
            )
            .setNegativeButton(getString(R.string.dialog_ok), null)
            .show()
    }
}
