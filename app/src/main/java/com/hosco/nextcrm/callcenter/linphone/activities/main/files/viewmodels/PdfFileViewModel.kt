package org.linphone.activities.main.files.viewmodels

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.core.Content
import org.linphone.core.tools.Log

class PdfFileViewModelFactory(private val content: Content) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PdfFileViewModel(content) as T
    }
}

class PdfFileViewModel(content: Content) : FileViewerViewModel(content) {
    val operationInProgress = MutableLiveData<Boolean>()

    private val pdfRenderer: PdfRenderer

    init {
        operationInProgress.value = false

        val input = ParcelFileDescriptor.open(File(filePath), ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(input)
        Log.i("[PDF Viewer] ${pdfRenderer.pageCount} pages in file $filePath")
    }

    override fun onCleared() {
        pdfRenderer.close()
        super.onCleared()
    }

    fun getPagesCount(): Int {
        return pdfRenderer.pageCount
    }

    fun loadPdfPageInto(index: Int, view: ImageView) {
        try {
            operationInProgress.value = true

            val page: PdfRenderer.Page = pdfRenderer.openPage(index)
            val width = if (coreContext.screenWidth <= coreContext.screenHeight) coreContext.screenWidth else coreContext.screenHeight
            val bm = Bitmap.createBitmap(
                width.toInt(),
                (width / page.width * page.height).toInt(),
                Bitmap.Config.ARGB_8888
            )
            page.render(bm, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            view.setImageBitmap(bm)

            operationInProgress.value = false
        } catch (e: Exception) {
            Log.e("[PDF Viewer] Exception: $e")
            operationInProgress.value = false
        }
    }
}
