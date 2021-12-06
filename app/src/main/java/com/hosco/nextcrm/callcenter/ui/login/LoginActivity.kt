package com.hosco.nextcrm.callcenter.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.*
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.ui.main.HomeActivity
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity() {

    var viewModel: LoginViewModel = LoginViewModel()
    var _domain = ""

//    var mFirebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
//    var mDatabaseReference: DatabaseReference? = null

    override fun getRootLayoutId(): Int {
        return com.hosco.nextcrm.callcenter.R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        imgShowHidePass.setImageDrawable(getDrawable(com.hosco.nextcrm.callcenter.R.drawable.ic_baseline_visibility_off))
        _domain = SharePreferenceUtils.getInstances().getDomain()
        if (_domain.isNullOrEmpty())
            intent.let {
                intent.getStringExtra(Const.DATA_DOMAIN).let { _domain = it.toString() }
            }

        viewModel.dataLoginResponse().observe(this, {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        })

        viewModel.isShowLoading.observe(this, {
            if (it) DialogUtils.showCrmLoadingDialog(this,null)
            else DialogUtils.dismissCrm()
        })

        viewModel.errorMessage.observe(this, {
            val contextView = findViewById<View>(com.hosco.nextcrm.callcenter.R.id.btnLogin)
            if (it != null) {
                DialogUtils.showSnackBar(contextView, it)
            }
        })

        PushDownAnim.setPushDownAnimTo(imgShowHidePass).setOnClickListener {
            viewModel.setShowHidePass(edPassword, imgShowHidePass)
        }
        PushDownAnim.setPushDownAnimTo(btnLogin).setOnClickListener {
            viewModel.setAuRequest(it, _domain, getUsername(), getPassword())
            hideKeyboard()
        }
        PushDownAnim.setPushDownAnimTo(tvOtherDomain).setOnClickListener {
            startActivity(Intent(this, DomainActivity::class.java))
            SharePreferenceUtils.getInstances().logout()
        }

//        mDatabaseReference = mFirebaseDatabase.getReference("crmcallcenter")
//        mDatabaseReference!!.child("isSubmit")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    try {
//                        var isSubmit = snapshot.value
//                        if (isSubmit != null && isSubmit == true) {
//                            // hard value for submit app
//                            edUsername.setText("admin")
//                            edPassword.setText("gymmaster123")
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

    fun getUsername(): String {
        return edUsername.text.toString().trim()
    }

    fun getPassword(): String {
        return edPassword.text.toString().trim()
    }

    override fun showError(message: String) {
        super.showError(message)
    }

}