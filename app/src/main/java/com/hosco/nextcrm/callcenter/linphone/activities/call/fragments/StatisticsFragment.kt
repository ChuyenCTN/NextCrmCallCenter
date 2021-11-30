package org.linphone.activities.call.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.CallStatisticsFragmentBinding
import org.linphone.activities.GenericFragment
import org.linphone.activities.call.viewmodels.StatisticsListViewModel

class StatisticsFragment : GenericFragment<CallStatisticsFragmentBinding>() {
    private lateinit var viewModel: StatisticsListViewModel

    override fun getLayoutId(): Int = R.layout.call_statistics_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        useMaterialSharedAxisXForwardAnimation = false

        viewModel = ViewModelProvider(this).get(StatisticsListViewModel::class.java)
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback.isEnabled = false
    }
}
