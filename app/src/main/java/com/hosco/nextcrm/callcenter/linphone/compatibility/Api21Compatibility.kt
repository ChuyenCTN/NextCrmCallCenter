package org.linphone.compatibility

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Vibrator
import android.provider.MediaStore
import android.provider.Settings
import android.view.WindowManager
import com.hosco.nextcrm.callcenter.R
import org.linphone.core.Content
import org.linphone.core.tools.Log
import org.linphone.utils.AppUtils
import org.linphone.utils.FileUtils
import org.linphone.utils.PermissionHelper

@Suppress("DEPRECATION")
@TargetApi(21)
class Api21Compatibility {
    companion object {
        fun getDeviceName(context: Context): String {
            var name = BluetoothAdapter.getDefaultAdapter().name
            if (name == null) {
                name = Settings.Secure.getString(
                    context.contentResolver,
                    "bluetooth_name"
                )
            }
            if (name == null) {
                name = Build.MANUFACTURER + " " + Build.MODEL
            }
            return name
        }

        @SuppressLint("MissingPermission")
        fun eventVibration(vibrator: Vibrator) {
            val pattern = longArrayOf(0, 100, 100)
            vibrator.vibrate(pattern, -1)
        }

        fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
            return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        suspend fun addImageToMediaStore(context: Context, content: Content): Boolean {
            if (!PermissionHelper.get().hasWriteExternalStorage()) {
                Log.e("[Media Store] Write external storage permission denied")
                return false
            }

            val filePath = content.filePath
            if (filePath == null) {
                Log.e("[Media Store] Content doesn't have a file path!")
                return false
            }

            val appName = AppUtils.getString(R.string.app_name)
            val relativePath = "${Environment.DIRECTORY_PICTURES}/$appName"
            val fileName = content.name
            val mime = "${content.type}/${content.subtype}"
            Log.i("[Media Store] Adding image $filePath to Media Store with name $fileName and MIME $mime, asking to be stored in $relativePath")

            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, mime)
            }
            val collection = MediaStore.Images.Media.getContentUri("external")
            val mediaStoreFilePath = addContentValuesToCollection(context, filePath, collection, values)
            if (mediaStoreFilePath.isNotEmpty()) {
                content.userData = mediaStoreFilePath
                return true
            }
            return false
        }

        suspend fun addVideoToMediaStore(context: Context, content: Content): Boolean {
            if (!PermissionHelper.get().hasWriteExternalStorage()) {
                Log.e("[Media Store] Write external storage permission denied")
                return false
            }

            val filePath = content.filePath
            if (filePath == null) {
                Log.e("[Media Store] Content doesn't have a file path!")
                return false
            }

            val appName = AppUtils.getString(R.string.app_name)
            val relativePath = "${Environment.DIRECTORY_MOVIES}/$appName"
            val fileName = content.name
            val mime = "${content.type}/${content.subtype}"
            Log.i("[Media Store] Adding video $filePath to Media Store with name $fileName and MIME $mime, asking to be stored in $relativePath")

            val values = ContentValues().apply {
                put(MediaStore.Video.Media.TITLE, fileName)
                put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Video.Media.MIME_TYPE, mime)
            }
            val collection = MediaStore.Video.Media.getContentUri("external")
            val mediaStoreFilePath = addContentValuesToCollection(context, filePath, collection, values)
            if (mediaStoreFilePath.isNotEmpty()) {
                content.userData = mediaStoreFilePath
                return true
            }
            return false
        }

        suspend fun addAudioToMediaStore(context: Context, content: Content): Boolean {
            if (!PermissionHelper.get().hasWriteExternalStorage()) {
                Log.e("[Media Store] Write external storage permission denied")
                return false
            }

            val filePath = content.filePath
            if (filePath == null) {
                Log.e("[Media Store] Content doesn't have a file path!")
                return false
            }

            val appName = AppUtils.getString(R.string.app_name)
            val relativePath = "${Environment.DIRECTORY_MUSIC}/$appName"
            val fileName = content.name
            val mime = "${content.type}/${content.subtype}"
            Log.i("[Media Store] Adding audio $filePath to Media Store with name $fileName and MIME $mime, asking to be stored in $relativePath")

            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.TITLE, fileName)
                put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Audio.Media.MIME_TYPE, mime)
            }
            val collection = MediaStore.Audio.Media.getContentUri("external")

            val mediaStoreFilePath = addContentValuesToCollection(context, filePath, collection, values)
            if (mediaStoreFilePath.isNotEmpty()) {
                content.userData = mediaStoreFilePath
                return true
            }
            return false
        }

        private suspend fun addContentValuesToCollection(
            context: Context,
            filePath: String,
            collection: Uri,
            values: ContentValues
        ): String {
            try {
                val fileUri = context.contentResolver.insert(collection, values)
                if (fileUri == null) {
                    Log.e("[Media Store] Failed to get a URI to where store the file, aborting")
                    return ""
                }

                context.contentResolver.openOutputStream(fileUri).use { out ->
                    if (FileUtils.copyFileTo(filePath, out)) {
                        return fileUri.toString()
                    }
                }
            } catch (e: Exception) {
                Log.e("[Media Store] Exception: $e")
            }
            return ""
        }

        fun setShowWhenLocked(activity: Activity, enable: Boolean) {
            if (enable) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            }
        }

        fun setTurnScreenOn(activity: Activity, enable: Boolean) {
            if (enable) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        }

        fun requestDismissKeyguard(activity: Activity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }
}
