package org.linphone.activities.main.contact.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.ContactSyncAccountPickerFragmentBinding
import org.linphone.activities.main.contact.adapters.SyncAccountAdapter
import org.linphone.core.tools.Log

class SyncAccountPickerFragment(private val listener: SyncAccountPickedListener) : DialogFragment() {
    private var _binding: ContactSyncAccountPickerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SyncAccountAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.assistant_country_dialog_style)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ContactSyncAccountPickerFragmentBinding.inflate(inflater, container, false)

        adapter = SyncAccountAdapter()
        binding.accountsList.adapter = adapter

        binding.accountsList.setOnItemClickListener { _, _, position, _ ->
            if (position >= 0 && position < adapter.count) {
                val account = adapter.getItem(position)
                Log.i("[Sync Account Picker] Picked ${account.first} / ${account.second}")
                listener.onSyncAccountClicked(account.first, account.second)
            }
            dismiss()
        }

        binding.setLocalSyncAccountClickListener {
            Log.i("[Sync Account Picker] Picked local account")
            listener.onSyncAccountClicked(null, null)
            dismiss()
        }

        return binding.root
    }

    interface SyncAccountPickedListener {
        fun onSyncAccountClicked(name: String?, type: String?)
    }
}
