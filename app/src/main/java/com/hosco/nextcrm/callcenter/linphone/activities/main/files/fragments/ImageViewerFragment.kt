package org.linphone.activities.main.files.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FileImageViewerFragmentBinding
import org.linphone.activities.main.files.viewmodels.ImageFileViewModel
import org.linphone.activities.main.files.viewmodels.ImageFileViewModelFactory
import org.linphone.core.tools.Log

class ImageViewerFragment : GenericViewerFragment<FileImageViewerFragmentBinding>() {
    private lateinit var viewModel: ImageFileViewModel

    override fun getLayoutId(): Int = R.layout.file_image_viewer_fragment

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val content = sharedViewModel.contentToOpen.value
        if (content == null) {
            Log.e("[Image Viewer] Content is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            ImageFileViewModelFactory(content)
        )[ImageFileViewModel::class.java]
        binding.viewModel = viewModel
    }
}
