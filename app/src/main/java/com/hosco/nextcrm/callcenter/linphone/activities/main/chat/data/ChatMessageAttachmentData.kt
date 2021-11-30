package org.linphone.activities.main.chat.data

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.linphone.utils.FileUtils
import org.linphone.utils.ImageUtils

class ChatMessageAttachmentData(
    val path: String,
    private val deleteCallback: (attachment: ChatMessageAttachmentData) -> Unit
) {
    val fileName: String = FileUtils.getNameFromFilePath(path)
    val isImage: Boolean = FileUtils.isExtensionImage(path)
    val isVideo: Boolean = FileUtils.isExtensionVideo(path)
    val isAudio: Boolean = FileUtils.isExtensionAudio(path)
    val isPdf: Boolean = FileUtils.isExtensionPdf(path)
    val videoPreview = MutableLiveData<Bitmap>()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        if (isVideo) {
            scope.launch {
                withContext(Dispatchers.IO) {
                    videoPreview.postValue(ImageUtils.getVideoPreview(path))
                }
            }
        }
    }

    fun destroy() {
        scope.cancel()
    }

    fun delete() {
        deleteCallback(this)
    }
}
