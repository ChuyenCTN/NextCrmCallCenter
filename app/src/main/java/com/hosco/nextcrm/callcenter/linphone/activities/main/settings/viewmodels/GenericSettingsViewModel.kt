package org.linphone.activities.main.settings.viewmodels

import androidx.lifecycle.ViewModel
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences

abstract class GenericSettingsViewModel : ViewModel() {
    protected val prefs = corePreferences
    protected val core = coreContext.core
}
