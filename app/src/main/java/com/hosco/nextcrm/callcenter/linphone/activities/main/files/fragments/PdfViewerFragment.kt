package org.linphone.activities.main.files.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FilePdfViewerFragmentBinding
import org.linphone.activities.main.files.adapters.PdfPagesListAdapter
import org.linphone.activities.main.files.viewmodels.PdfFileViewModel
import org.linphone.activities.main.files.viewmodels.PdfFileViewModelFactory
import org.linphone.core.tools.Log

class PdfViewerFragment : GenericViewerFragment<FilePdfViewerFragmentBinding>() {
    private lateinit var viewModel: PdfFileViewModel
    private lateinit var adapter: PdfPagesListAdapter

    override fun getLayoutId(): Int = R.layout.file_pdf_viewer_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        val content = sharedViewModel.contentToOpen.value
        if (content == null) {
            Log.e("[PDF Viewer] Content is null, aborting!")
            // (activity as MainActivity).showSnackBar(R.string.error)
            findNavController().navigateUp()
            return
        }

        viewModel = ViewModelProvider(
            this,
            PdfFileViewModelFactory(content)
        )[PdfFileViewModel::class.java]
        binding.viewModel = viewModel

        adapter = PdfPagesListAdapter(viewModel)
        binding.pdfViewPager.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}
