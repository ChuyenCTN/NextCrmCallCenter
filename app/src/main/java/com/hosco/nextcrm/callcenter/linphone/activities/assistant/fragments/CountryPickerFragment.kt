package org.linphone.activities.assistant.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.AssistantCountryPickerFragmentBinding
import org.linphone.activities.assistant.adapters.CountryPickerAdapter
import org.linphone.core.DialPlan

class CountryPickerFragment(private val listener: CountryPickedListener) : DialogFragment() {
    private var _binding: AssistantCountryPickerFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CountryPickerAdapter

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
        _binding = AssistantCountryPickerFragmentBinding.inflate(inflater, container, false)

        adapter = CountryPickerAdapter()
        binding.countryList.adapter = adapter

        binding.countryList.setOnItemClickListener { _, _, position, _ ->
            if (position >= 0 && position < adapter.count) {
                val dialPlan = adapter.getItem(position)
                listener.onCountryClicked(dialPlan)
            }
            dismiss()
        }

        binding.searchCountry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        binding.setCancelClickListener {
            dismiss()
        }

        return binding.root
    }

    interface CountryPickedListener {
        fun onCountryClicked(dialPlan: DialPlan)
    }
}
