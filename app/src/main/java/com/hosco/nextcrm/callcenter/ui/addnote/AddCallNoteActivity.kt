package com.hosco.nextcrm.callcenter.ui.addnote

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProviders
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.DialogUtils

class AddCallNoteActivity : BaseActivity() {

    var viewModel: AddNoteViewModel = AddNoteViewModel()

    override fun getRootLayoutId(): Int {
        return R.layout.activity_add_call_note
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddNoteViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        getWindow().setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val phoneNumber: String = intent.getStringExtra(Const.DATA_PHONE).toString()

        DialogUtils.showCallNote(
            this,
            object : DialogUtils.callNoteListener {
                override fun onOK(data: String) {
                    viewModel.addNote(phoneNumber, data)
                    Handler().postDelayed(Runnable { finish() }, 300)
                }

                override fun onCancel() {
                    finish()
                }
            })
    }


}