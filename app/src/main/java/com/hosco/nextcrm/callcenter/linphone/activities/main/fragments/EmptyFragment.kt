package org.linphone.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import com.hosco.nextcrm.callcenter.R

class EmptyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        return inflater.inflate(R.layout.empty_fragment, container, false)
    }
}
