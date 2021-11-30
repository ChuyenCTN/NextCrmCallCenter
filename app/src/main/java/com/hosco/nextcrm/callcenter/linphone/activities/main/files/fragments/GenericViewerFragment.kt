package org.linphone.activities.main.files.fragments

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import org.linphone.activities.main.fragments.SecureFragment
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log

abstract class GenericViewerFragment<T : ViewDataBinding> : SecureFragment<T>() {
    protected lateinit var sharedViewModel: SharedMainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        isSecure = arguments?.getBoolean("Secure") ?: false
    }

    override fun onStart() {
        super.onStart()

        val content = sharedViewModel.contentToOpen.value
        if (content == null) {
            Log.e("[Generic Viewer] Content is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        (childFragmentManager.findFragmentById(R.id.top_bar_fragment) as? TopBarFragment)
            ?.setContent(content)
    }
}
