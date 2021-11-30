
package org.linphone.activities.assistant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.linphone.activities.assistant.fragments.CountryPickerFragment
import org.linphone.core.AccountCreator
import org.linphone.core.DialPlan
import org.linphone.core.tools.Log
import org.linphone.utils.PhoneNumberUtils

abstract class AbstractPhoneViewModel(val accountCreator: AccountCreator) :
    ViewModel(),
    CountryPickerFragment.CountryPickedListener {

    val prefix = MutableLiveData<String>()

    val phoneNumber = MutableLiveData<String>()
    val phoneNumberError = MutableLiveData<String>()

    val countryName: LiveData<String> = Transformations.switchMap(prefix) {
        getCountryNameFromPrefix(it)
    }

    init {
        prefix.value = "+"
    }

    override fun onCountryClicked(dialPlan: DialPlan) {
        prefix.value = "+${dialPlan.countryCallingCode}"
    }

    fun isPhoneNumberOk(): Boolean {
        return countryName.value.orEmpty().isNotEmpty() && phoneNumber.value.orEmpty().isNotEmpty() && phoneNumberError.value.orEmpty().isEmpty()
    }

    fun updateFromPhoneNumberAndOrDialPlan(number: String?, dialPlan: DialPlan?) {
        if (dialPlan != null) {
            Log.i("[Assistant] Found prefix from dial plan: ${dialPlan.countryCallingCode}")
            prefix.value = "+${dialPlan.countryCallingCode}"
        }

        if (number != null) {
            Log.i("[Assistant] Found phone number: $number")
            phoneNumber.value = number!!
        }
    }

    private fun getCountryNameFromPrefix(prefix: String?): MutableLiveData<String> {
        val country = MutableLiveData<String>()
        country.value = ""

        if (prefix != null && prefix.isNotEmpty()) {
            val countryCode = if (prefix.first() == '+') prefix.substring(1) else prefix
            val dialPlan = PhoneNumberUtils.getDialPlanFromCountryCallingPrefix(countryCode)
            Log.i("[Assistant] Found dial plan $dialPlan from country code: $countryCode")
            country.value = dialPlan?.country
        }
        return country
    }
}
