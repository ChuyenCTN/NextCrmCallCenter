package com.hosco.nextcrm.callcenter.ui.contact

import android.Manifest
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.model.response.DeviceContact
import com.hosco.nextcrm.callcenter.ui.contact.adapter.DeviceContactAdapter
import com.hosco.nextcrm.callcenter.ui.contact.adapter.ItemClickDevice
import com.hosco.nextcrm.callcenter.ui.dialpad.DialpadViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_customer.*
import kotlinx.android.synthetic.main.fragment_device.*
import kotlinx.android.synthetic.main.fragment_device.tvMessagePermissionDevice
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class DeviceFragment : BaseFragment(), EasyPermissions.PermissionCallbacks {

    private lateinit var viewModel: ContactViewModel
    var dialpadViewModel: DialpadViewModel = DialpadViewModel()

    var adapter: DeviceContactAdapter = DeviceContactAdapter()
    var isPermissions: Boolean = false

    companion object {
        fun newInstance(): DeviceFragment {
            return DeviceFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_device
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        dialpadViewModel = ViewModelProviders.of(this).get(DialpadViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        setUpRCV()
        viewModel.onCreate()

        requestPermission()

        swipeDeviceContact.setOnRefreshListener {
            if (isPermissions)
                viewModel.getDeviceContactList(requireContext())
            else
                requestPermission()
            swipeDeviceContact.isRefreshing = false
        }

        viewModel.dataDeviceResponse().observe(this, Observer {
            if (it != null) {
                adapter.setData(it as ArrayList<DeviceContact>)
                AllContactList.getInstance().addDeviceToListContact(it)

            }
        })

        PushDownAnim.setPushDownAnimTo(tvRequestPermissionDevice).setOnClickListener {
            requestPermission()
        }

        edSearchDevice.doAfterTextChanged {
            var strSearch = edSearchDevice.text.toString().trim()
            imgClearSearchDevice.visibility = if (strSearch.isEmpty()) View.GONE else View.VISIBLE
            adapter.getFilter()?.filter(strSearch)
        }

        PushDownAnim.setPushDownAnimTo(imgClearSearchDevice).setOnClickListener {
            edSearchDevice.clearFocus()
            edSearchDevice.text.clear()
        }
    }

    fun setUpRCV() {
        if (viewModel != null && rcvDeviceContatc != null) {
            adapter = DeviceContactAdapter()
            adapter.setListener(object : ItemClickDevice {
                override fun onClick(deviceContact: DeviceContact) {
                    deviceContact.phone.let {
                        it?.let { it1 -> dialpadViewModel.startCall(rcvDeviceContatc, it1) }
                    }
                }
            })
            val layoutManager = LinearLayoutManager(context)
            rcvDeviceContatc.layoutManager = layoutManager
            rcvDeviceContatc.adapter = adapter
        }
    }

    override fun showLoadingDialog() {
        swipeDeviceContact?.isRefreshing = true
    }

    override fun hideLoadingDialog() {
        swipeDeviceContact?.isRefreshing = false
    }

    fun requestPermission() {
        context?.let {
            val perms = Manifest.permission.READ_CONTACTS
            if (EasyPermissions.hasPermissions(it, perms)) {
                isPermissions = true
                viewModel.getDeviceContactList(requireContext())
                showNoData(false, false)
            } else {
                showNoData(true, true)
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.txt_permission_contact_dialog),
                    1233,
                    perms
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showNoData(false, false)
        isPermissions = true
        viewModel.getDeviceContactList(requireContext())
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("zxcvbnm", "onPermissionsDenied")
        showNoData(true, true)
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    fun showNoData(isShowNoData: Boolean, isShowMessage: Boolean) {
        if (isShowNoData) {
            swipeDeviceContact.visibility = View.GONE
            layoutNodataDevice.let {
                layoutNodataDevice.visibility = View.VISIBLE
                if (isShowMessage == true) {
                    swipeDeviceContact.visibility = View.GONE
                    tvMessagePermissionDevice.visibility = View.VISIBLE
                    tvRequestPermissionDevice.visibility = View.VISIBLE

                } else {
                    swipeDeviceContact.visibility = View.VISIBLE
                    tvMessagePermissionDevice.visibility = View.GONE
                    tvRequestPermissionDevice.visibility = View.GONE
                }
            }
        } else {
            swipeDeviceContact.visibility = View.VISIBLE
            layoutNodataDevice.visibility = View.GONE
        }
    }
}