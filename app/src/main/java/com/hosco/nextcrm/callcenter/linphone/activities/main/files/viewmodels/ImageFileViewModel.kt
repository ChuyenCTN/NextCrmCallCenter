package org.linphone.activities.main.files.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.linphone.core.Content

class ImageFileViewModelFactory(private val content: Content) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ImageFileViewModel(content) as T
    }
}

class ImageFileViewModel(content: Content) : FileViewerViewModel(content)
