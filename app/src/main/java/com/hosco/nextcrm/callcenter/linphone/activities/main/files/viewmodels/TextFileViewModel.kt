package org.linphone.activities.main.files.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.io.BufferedReader
import java.io.FileReader
import java.lang.StringBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.linphone.core.Content
import org.linphone.core.tools.Log

class TextFileViewModelFactory(private val content: Content) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TextFileViewModel(content) as T
    }
}

class TextFileViewModel(content: Content) : FileViewerViewModel(content) {
    val operationInProgress = MutableLiveData<Boolean>()

    val text = MutableLiveData<String>()

    init {
        operationInProgress.value = false

        openFile()
    }

    private fun openFile() {
        operationInProgress.value = true

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val br = BufferedReader(FileReader(filePath))
                    var line: String?
                    val textBuilder = StringBuilder()
                    while (br.readLine().also { line = it } != null) {
                        textBuilder.append(line)
                        textBuilder.append('\n')
                    }
                    br.close()

                    text.postValue(textBuilder.toString())
                    operationInProgress.postValue(false)
                } catch (e: Exception) {
                    Log.e("[Text Viewer] Exception: $e")
                    operationInProgress.postValue(false)
                }
            }
        }
    }
}
