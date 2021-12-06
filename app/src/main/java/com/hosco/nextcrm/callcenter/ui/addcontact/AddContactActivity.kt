package com.hosco.nextcrm.callcenter.ui.addcontact

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_add_contact.*

class AddContactActivity : BaseActivity() {

    var viewModel: AddContactViewModel = AddContactViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getRootLayoutId(): Int {
        return R.layout.activity_add_contact
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddContactViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupView(savedInstanceState: Bundle?) {

        viewModel.isShowLoading.observe(this, Observer {
            if (it) DialogUtils.showCrmLoadingDialog(this,null)
            else DialogUtils.dismissCrm()
        })

        viewModel.dataAddContact.observe(this, Observer {
            finish()
        })

        PushDownAnim.setPushDownAnimTo(btnAddContact).setOnClickListener {
            var company = edCompanyAddContact.text.toString().trim()
            var name = edNameAddContact.text.toString().trim()
            var extension = edExtensionAddContact.text.toString().trim()
            var numberPhone = edPhoneNumberAddContact.text.toString().trim()
            var email = edEmailAddContact.text.toString().trim()

            viewModel.setContactRequest(it, company, name, extension, numberPhone, email)

        }

        PushDownAnim.setPushDownAnimTo(imgBackAddContact).setOnClickListener {
            onBackPressed()
        }
    }
}