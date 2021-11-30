package org.linphone.activities.assistant

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantActivityBinding
import org.linphone.activities.GenericActivity
import org.linphone.activities.SnackBarActivity
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel

class AssistantActivity : GenericActivity(), SnackBarActivity {
    private lateinit var binding: AssistantActivityBinding
    private lateinit var sharedViewModel: SharedAssistantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.assistant_activity)

        sharedViewModel = ViewModelProvider(this).get(SharedAssistantViewModel::class.java)
    }

    override fun showSnackBar(resourceId: Int) {
        Snackbar.make(binding.coordinator, resourceId, Snackbar.LENGTH_LONG).show()
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(binding.coordinator, message, Snackbar.LENGTH_LONG).show()
    }
}
