package org.linphone.activities.main.dialer.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext

class ConfigFileViewModel : ViewModel() {
    val text = MutableLiveData<String>()

    init {
        text.value = coreContext.core.config.dump()
    }
}
