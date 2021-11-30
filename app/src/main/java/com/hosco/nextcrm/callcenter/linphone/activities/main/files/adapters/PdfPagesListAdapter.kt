package org.linphone.activities.main.files.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.FilePdfViewerCellBinding
import org.linphone.activities.main.files.viewmodels.PdfFileViewModel

class PdfPagesListAdapter(private val pdfViewModel: PdfFileViewModel) : RecyclerView.Adapter<PdfPagesListAdapter.PdfPageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfPageViewHolder {
        val binding: FilePdfViewerCellBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.file_pdf_viewer_cell, parent, false
        )
        return PdfPageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return pdfViewModel.getPagesCount()
    }

    override fun onBindViewHolder(holder: PdfPageViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class PdfPageViewHolder(private val binding: FilePdfViewerCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(index: Int) {
            with(binding) {
                pdfViewModel.loadPdfPageInto(index, pdfImage)
            }
        }
    }
}
