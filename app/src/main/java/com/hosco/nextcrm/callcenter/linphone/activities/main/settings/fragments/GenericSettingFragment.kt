package org.linphone.activities.main.settings.fragments

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import org.linphone.activities.GenericFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel

abstract class GenericSettingFragment<T : ViewDataBinding> : GenericFragment<T>() {
    protected lateinit var sharedViewModel: SharedMainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        useMaterialSharedAxisXForwardAnimation = sharedViewModel.isSlidingPaneSlideable.value == false

        super.onViewCreated(view, savedInstanceState)
    }
}
