package org.linphone.activities.main.sidemenu.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.databinding.SideMenuFragmentBinding
import kotlinx.coroutines.launch
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import org.linphone.activities.*
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.activities.main.settings.SettingListenerStub
import org.linphone.activities.main.sidemenu.viewmodels.SideMenuViewModel
import org.linphone.activities.main.viewmodels.SharedMainViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.Event
import org.linphone.utils.FileUtils
import org.linphone.utils.PermissionHelper
import java.io.File

class SideMenuFragment : GenericFragment<SideMenuFragmentBinding>() {
    private lateinit var viewModel: SideMenuViewModel
    private lateinit var sharedViewModel: SharedMainViewModel
    private var temporaryPicturePath: File? = null

    override fun getLayoutId(): Int = R.layout.side_menu_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this).get(SideMenuViewModel::class.java)
        binding.viewModel = viewModel

        sharedViewModel = requireActivity().run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

        sharedViewModel.accountRemoved.observe(
            viewLifecycleOwner,
            {
                Log.i("[Side Menu] Account removed, update accounts list")
                viewModel.updateAccountsList()
            }
        )

        viewModel.accountsSettingsListener = object : SettingListenerStub() {
            override fun onAccountClicked(identity: String) {
                val args = Bundle()
                args.putString("Identity", identity)
                Log.i("[Side Menu] Navigation to settings for account with identity: $identity")

                sharedViewModel.toggleDrawerEvent.value = Event(true)
                navigateToAccountSettings(identity)
            }
        }

        binding.setSelfPictureClickListener {
            pickFile()
        }

        binding.setAssistantClickListener {
            sharedViewModel.toggleDrawerEvent.value = Event(true)
            startActivity(Intent(context, AssistantActivity::class.java))
        }

        binding.setSettingsClickListener {
            sharedViewModel.toggleDrawerEvent.value = Event(true)
            navigateToSettings()
        }

        binding.setRecordingsClickListener {
            sharedViewModel.toggleDrawerEvent.value = Event(true)
            navigateToRecordings()
        }

        binding.setAboutClickListener {
            sharedViewModel.toggleDrawerEvent.value = Event(true)
            navigateToAbout()
        }

        binding.setQuitClickListener {
            requireActivity().finishAndRemoveTask()
            coreContext.stop()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val contactImageFilePath = FileUtils.getFilePathFromPickerIntent(data, temporaryPicturePath)
                if (contactImageFilePath != null) {
                    viewModel.setPictureFromPath(contactImageFilePath)
                }
            }
        }
    }

    private fun pickFile() {
        val cameraIntents = ArrayList<Intent>()

        // Handles image picking
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"

        if (PermissionHelper.get().hasCameraPermission()) {
            // Allows to capture directly from the camera
            val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val tempFileName = System.currentTimeMillis().toString() + ".jpeg"
            val file = FileUtils.getFileStoragePath(tempFileName)
            temporaryPicturePath = file
            val publicUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getString(R.string.file_provider),
                file
            )
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, publicUri)
            captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            cameraIntents.add(captureIntent)
        }

        val chooserIntent =
            Intent.createChooser(galleryIntent, getString(R.string.chat_message_pick_file_dialog))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(arrayOf<Parcelable>()))

        startActivityForResult(chooserIntent, 0)
    }
}
