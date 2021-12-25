package com.hosco.nextcrm.callcenter.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.*
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_domain.*


class DomainActivity : BaseActivity() {

    var viewModel: LoginViewModel = LoginViewModel()
    var _domain = ""

//    var mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
//    var mDatabaseReference: DatabaseReference? = null

    override fun getRootLayoutId(): Int {
        return R.layout.activity_domain
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getColor(R.color.colorPressButton))

        PushDownAnim.setPushDownAnimTo(btnContinueDomain).setOnClickListener {
            hideKeyboard()
            checkDomain(it)
        }

        viewModel.dataCustomerResponse().observe(this, Observer {
            val intent = Intent(this, LoginActivity::class.java)
            SharePreferenceUtils.getInstances().saveDomain(_domain)
            intent.putExtra(Const.DATA_DOMAIN, _domain)
            startActivity(intent)
            finish()
        })

        viewModel.isShowLoading.observe(this, {
            if (it) DialogUtils.showCrmLoadingDialog(this,null)
            else DialogUtils.dismissCrm()
        })

        viewModel.errorMessage.observe(this, {
            val contextView = findViewById<View>(R.id.btnLogin)
            if (it != null) {
                DialogUtils.showSnackBar(contextView, it)
            }
        })

//        mDatabaseReference = mFirebaseDatabase.getReference("crmcallcenter")
//        mDatabaseReference!!.child("isSubmit")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    try {
//                        var isSubmit = snapshot.value
//                        if (isSubmit != null && isSubmit == true) {
//                            edDomain.setText("hosco")
//                        }
//                    } catch (e: Exception) {
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    try {
//
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            })
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (parent !is EditText) {
            parent?.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }

        return super.onCreateView(parent, name, context, attrs)
    }

    override fun showError(message: String) {
        DialogUtils.showSnackBar(btnContinueDomain,message)
    }


    fun checkDomain(view: View) {
        if (edDomain.text.toString().isEmpty()) {
            DialogUtils.showSnackBar(view, getString(R.string.txt_error_valid_common))
            return
        } else {
//            _domain = edDomain.text.toString() + tvDomain.text
            _domain = edDomain.text.toString()
            viewModel.checkCustomer(_domain)

        }
    }
}