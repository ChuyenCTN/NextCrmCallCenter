package org.linphone.activities.main.recordings.fragments

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.RecordingsFragmentBinding
import org.linphone.activities.main.fragments.MasterFragment
import org.linphone.activities.main.recordings.adapters.RecordingsListAdapter
import org.linphone.activities.main.recordings.data.RecordingData
import org.linphone.activities.main.recordings.viewmodels.RecordingsViewModel
import org.linphone.utils.AppUtils
import org.linphone.utils.RecyclerViewHeaderDecoration

class RecordingsFragment : MasterFragment<RecordingsFragmentBinding, RecordingsListAdapter>() {
    private lateinit var viewModel: RecordingsViewModel

    private var videoX: Float = 0f
    private var videoY: Float = 0f

    override fun getLayoutId(): Int = R.layout.recordings_fragment

    override fun onDestroyView() {
        binding.recordingsList.adapter = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(RecordingsViewModel::class.java)
        binding.viewModel = viewModel

        _adapter = RecordingsListAdapter(listSelectionViewModel, viewLifecycleOwner)
        binding.recordingsList.setHasFixedSize(true)
        binding.recordingsList.adapter = adapter

        val layoutManager = LinearLayoutManager(activity)
        binding.recordingsList.layoutManager = layoutManager

        // Divider between items
        binding.recordingsList.addItemDecoration(AppUtils.getDividerDecoration(requireContext(), layoutManager))

        // Displays the first letter header
        val headerItemDecoration = RecyclerViewHeaderDecoration(requireContext(), adapter)
        binding.recordingsList.addItemDecoration(headerItemDecoration)

        viewModel.recordingsList.observe(
            viewLifecycleOwner,
            { recordings ->
                adapter.submitList(recordings)
            }
        )

        binding.setBackClickListener { goBack() }

        binding.setEditClickListener { listSelectionViewModel.isEditionEnabled.value = true }

        binding.setVideoTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    videoX = v.x - event.rawX
                    videoY = v.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> {
                    v.animate()
                        .x(event.rawX + videoX)
                        .y(event.rawY + videoY)
                        .setDuration(0)
                        .start()
                }
                else -> {
                    v.performClick()
                    false
                }
            }
            true
        }

        adapter.setVideoTextureView(binding.recordingVideoSurface)
    }

    override fun deleteItems(indexesOfItemToDelete: ArrayList<Int>) {
        val list = ArrayList<RecordingData>()
        for (index in indexesOfItemToDelete) {
            val recording = adapter.currentList[index]
            list.add(recording)
        }
        viewModel.deleteRecordings(list)
    }
}
