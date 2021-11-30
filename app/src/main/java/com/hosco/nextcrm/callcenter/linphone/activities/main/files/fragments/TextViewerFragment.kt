package org.linphone.activities.main.files.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FileTextViewerFragmentBinding
import org.linphone.activities.main.files.viewmodels.TextFileViewModel
import org.linphone.activities.main.files.viewmodels.TextFileViewModelFactory
import org.linphone.core.tools.Log

class TextViewerFragment : GenericViewerFragment<FileTextViewerFragmentBinding>() {
    private lateinit var viewModel: TextFileViewModel

    override fun getLayoutId(): Int = R.layout.file_text_viewer_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val content = sharedViewModel.contentToOpen.value
        if (content == null) {
            Log.e("[Text Viewer] Content is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            TextFileViewModelFactory(content)
        )[TextFileViewModel::class.java]
        binding.viewModel = viewModel
    }
}
