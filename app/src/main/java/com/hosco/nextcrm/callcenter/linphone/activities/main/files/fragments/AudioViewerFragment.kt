package org.linphone.activities.main.files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.MediaController
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FileAudioViewerFragmentBinding
import org.linphone.activities.main.files.viewmodels.AudioFileViewModel
import org.linphone.activities.main.files.viewmodels.AudioFileViewModelFactory
import org.linphone.core.tools.Log

class AudioViewerFragment : GenericViewerFragment<FileAudioViewerFragmentBinding>() {
    private lateinit var viewModel: AudioFileViewModel

    private lateinit var mediaController: MediaController

    override fun getLayoutId(): Int = R.layout.file_audio_viewer_fragment

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val content = sharedViewModel.contentToOpen.value
        if (content == null) {
            Log.e("[Audio Viewer] Content is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            AudioFileViewModelFactory(content)
        )[AudioFileViewModel::class.java]
        binding.viewModel = viewModel

        mediaController = object : MediaController(requireContext()) {
            // This hack is even if media controller is showed with timeout=0
            // Once a control is touched, it will disappear 3 seconds later anyway
            override fun show(timeout: Int) {
                super.show(0)
            }

            // This is to prevent the first back key press to only hide to media controls
            override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
                    goBack()
                    return true
                }
                return super.dispatchKeyEvent(event)
            }
        }
        mediaController.setMediaPlayer(viewModel)

        viewModel.mediaPlayer.setOnPreparedListener {
            mediaController.setAnchorView(binding.anchor)
            // This will make the controls visible forever
            mediaController.show(0)
        }
    }

    override fun onPause() {
        mediaController.hide()
        viewModel.mediaPlayer.pause()

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        mediaController.show(0)
    }
}
