package org.linphone.activities.main.files.viewmodels

import androidx.lifecycle.ViewModel
import org.linphone.core.Content
import org.linphone.core.tools.Log
import org.linphone.utils.FileUtils

open class FileViewerViewModel(val content: Content) : ViewModel() {
    val filePath: String
    private val deleteAfterUse: Boolean = content.isFileEncrypted

    init {
        filePath = if (deleteAfterUse) content.plainFilePath else content.filePath.orEmpty()
    }

    override fun onCleared() {
        if (deleteAfterUse) {
            Log.i("[File Viewer] Deleting temporary plain file: $filePath")
            FileUtils.deleteFile(filePath)
        }

        super.onCleared()
    }
}
