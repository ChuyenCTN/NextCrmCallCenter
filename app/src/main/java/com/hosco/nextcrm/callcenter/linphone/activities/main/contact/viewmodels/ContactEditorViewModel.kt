package org.linphone.activities.main.contact.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.ByteArrayOutputStream
import java.io.IOException
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import org.linphone.activities.main.contact.data.NumberOrAddressEditorData
import org.linphone.contact.*
import org.linphone.core.ChatRoomSecurityLevel
import org.linphone.core.tools.Log
import org.linphone.utils.ImageUtils
import org.linphone.utils.PermissionHelper

class ContactEditorViewModelFactory(private val contact: Contact?) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactEditorViewModel(contact) as T
    }
}

class ContactEditorViewModel(val c: Contact?) : ViewModel(), ContactDataInterface {
    override val contact: MutableLiveData<Contact> = MutableLiveData<Contact>()
    override val displayName: MutableLiveData<String> = MutableLiveData<String>()
    override val securityLevel: MutableLiveData<ChatRoomSecurityLevel> = MutableLiveData<ChatRoomSecurityLevel>()

    val firstName = MutableLiveData<String>()

    val lastName = MutableLiveData<String>()

    val organization = MutableLiveData<String>()

    val displayOrganization = corePreferences.contactOrganizationVisible

    val tempPicturePath = MutableLiveData<String>()
    private var picture: ByteArray? = null

    val numbers = MutableLiveData<ArrayList<NumberOrAddressEditorData>>()

    val addresses = MutableLiveData<ArrayList<NumberOrAddressEditorData>>()

    var syncAccountName: String? = null
    var syncAccountType: String? = null

    init {
        if (c != null) {
            contact.value = c!!
            displayName.value = c.fullName ?: c.firstName + " " + c.lastName
        } else {
            displayName.value = ""
        }
        firstName.value = c?.firstName ?: ""
        lastName.value = c?.lastName ?: ""
        organization.value = c?.organization ?: ""

        updateNumbersAndAddresses()
    }

    fun save(): Contact {
        var contact = c
        var created = false
        if (contact == null) {
            created = true
            contact = if (PermissionHelper.get().hasWriteContactsPermission()) {
                NativeContact(NativeContactEditor.createAndroidContact(syncAccountName, syncAccountType).toString())
            } else {
                Contact()
            }
        }

        if (contact is NativeContact) {
            NativeContactEditor(contact)
                .setFirstAndLastNames(firstName.value.orEmpty(), lastName.value.orEmpty())
                .setOrganization(organization.value.orEmpty())
                .setPhoneNumbers(numbers.value.orEmpty())
                .setSipAddresses(addresses.value.orEmpty())
                .setPicture(picture)
                .commit()
        } else {
            val friend = contact.friend ?: coreContext.core.createFriend()
            friend.edit()
            friend.name = "${firstName.value.orEmpty()} ${lastName.value.orEmpty()}"

            for (address in friend.addresses) {
                friend.removeAddress(address)
            }
            for (address in addresses.value.orEmpty()) {
                val parsed = coreContext.core.interpretUrl(address.newValue.value.orEmpty())
                if (parsed != null) friend.addAddress(parsed)
            }

            for (phone in friend.phoneNumbers) {
                friend.removePhoneNumber(phone)
            }
            for (phone in numbers.value.orEmpty()) {
                val phoneNumber = phone.newValue.value
                if (phoneNumber?.isNotEmpty() == true) {
                    friend.addPhoneNumber(phoneNumber)
                }
            }

            val vCard = friend.vcard
            if (vCard != null) {
                vCard.organization = organization.value
                vCard.familyName = lastName.value
                vCard.givenName = firstName.value
            }
            friend.done()

            if (contact.friend == null) {
                contact.friend = friend
                coreContext.core.defaultFriendList?.addLocalFriend(friend)
            }
        }

        if (created) {
            coreContext.contactsManager.addContact(contact)
        }
        return contact
    }

    fun setPictureFromPath(picturePath: String) {
        var orientation = ExifInterface.ORIENTATION_NORMAL
        var image = BitmapFactory.decodeFile(picturePath)

        try {
            val ei = ExifInterface(picturePath)
            orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            Log.i("[Contact Editor] Exif rotation is $orientation")
        } catch (e: IOException) {
            Log.e("[Contact Editor] Failed to get Exif rotation, exception raised: $e")
        }

        if (image == null) {
            Log.e("[Contact Editor] Couldn't get bitmap from filePath: $picturePath")
            return
        }

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 ->
                image =
                    ImageUtils.rotateImage(image, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 ->
                image =
                    ImageUtils.rotateImage(image, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 ->
                image =
                    ImageUtils.rotateImage(image, 270f)
        }

        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        picture = stream.toByteArray()
        tempPicturePath.value = picturePath
        image.recycle()
        stream.close()
    }

    fun addEmptySipAddress() {
        val list = arrayListOf<NumberOrAddressEditorData>()
        list.addAll(addresses.value.orEmpty())
        list.add(NumberOrAddressEditorData("", true))
        addresses.value = list
    }

    fun addEmptyPhoneNumber() {
        val list = arrayListOf<NumberOrAddressEditorData>()
        list.addAll(numbers.value.orEmpty())
        list.add(NumberOrAddressEditorData("", false))
        numbers.value = list
    }

    private fun updateNumbersAndAddresses() {
        val phoneNumbers = arrayListOf<NumberOrAddressEditorData>()
        for (number in c?.rawPhoneNumbers.orEmpty()) {
            phoneNumbers.add(NumberOrAddressEditorData(number, false))
        }
        if (phoneNumbers.isEmpty()) {
            phoneNumbers.add(NumberOrAddressEditorData("", false))
        }
        numbers.value = phoneNumbers

        val sipAddresses = arrayListOf<NumberOrAddressEditorData>()
        for (address in c?.rawSipAddresses.orEmpty()) {
            sipAddresses.add(NumberOrAddressEditorData(address, true))
        }
        if (sipAddresses.isEmpty()) {
            sipAddresses.add(NumberOrAddressEditorData("", true))
        }
        addresses.value = sipAddresses
    }
}
