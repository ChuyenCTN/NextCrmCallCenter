package org.linphone.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.core.tools.Log

abstract class GenericFragment<T : ViewDataBinding> : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    protected var useMaterialSharedAxisXForwardAnimation = true

    protected val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goBack()
        }
    }

    abstract fun getLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return _binding!!.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onStart() {
        super.onStart()

        if (useMaterialSharedAxisXForwardAnimation && corePreferences.enableAnimations) {
            enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            postponeEnterTransition()
            binding.root.doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun goBack() {
        try {
            if (!findNavController().popBackStack()) {
                if (!findNavController().navigateUp()) {
                    onBackPressedCallback.isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        } catch (ise: IllegalStateException) {
            Log.e("[Generic Fragment] Can't go back: $ise")
            onBackPressedCallback.isEnabled = false
            requireActivity().onBackPressed()
        }
    }
}
