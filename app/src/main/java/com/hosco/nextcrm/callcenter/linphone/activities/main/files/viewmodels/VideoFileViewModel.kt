package org.linphone.activities.main.files.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.linphone.core.Content

class VideoFileViewModelFactory(private val content: Content) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VideoFileViewModel(content) as T
    }
}

class VideoFileViewModel(content: Content) : FileViewerViewModel(content)
