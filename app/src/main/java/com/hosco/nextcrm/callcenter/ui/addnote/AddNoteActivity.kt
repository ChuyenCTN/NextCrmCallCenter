package com.hosco.nextcrm.callcenter.ui.addnote

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.DialogUtils
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.PrioritySpinnerAdapter
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.StateSpinnerAdapter
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.TypeSpinnerAdapter
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_add_note.*
import android.app.Activity
import android.content.Context

import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.google.gson.Gson
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.ui.searchcontact.SearchContactActivity
import kotlinx.android.synthetic.main.activity_filter_note.*
import org.greenrobot.eventbus.EventBus


class AddNoteActivity : BaseActivity() {

    var viewModel: AddNoteViewModel = AddNoteViewModel()
    var state: String = ""
    var type: String = ""
    var priority: String = ""
    var contactResponse: ContactResponse? = null

    override fun getRootLayoutId(): Int {
        return R.layout.activity_add_note
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(AddNoteViewModel::class.java)
        setObserveLive(viewModel)
    }

    @SuppressLint("ClickableViewAccessibility")
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

    override fun setupView(savedInstanceState: Bundle?) {

        viewModel.getInputData()


        viewModel.isShowLoading.observe(this, Observer {
            if (it) DialogUtils.showCrmLoadingDialog(this,null)
            else DialogUtils.dismissCrm()
        })

        viewModel.dataAddNoteResponse.observe(this, Observer {
            EventBus.getDefault().post(Gson().toJson(it))
            finish()
        })

        viewModel.dataState.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                state = it[0].code
                val adapter = StateSpinnerAdapter(it)
                spnStateAddNote.setAdapter(adapter)
                spnStateAddNote.setOnItemSelectedListener { view, position, id, item ->
                    state = item.toString()
                }
            }
        })

        viewModel.dataPriority.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                priority = it[0].name
                val adapter = PrioritySpinnerAdapter(it)
                spnPriorityAddNote.setAdapter(adapter)
                spnPriorityAddNote.setOnItemSelectedListener { view, position, id, item ->
                    priority = item.toString()
                }
            }
        })

        viewModel.datatype.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                type = it[0].id.toString()
                val adapter = TypeSpinnerAdapter(it)
                spnTypeAddNote.setAdapter(adapter)
                spnTypeAddNote.setOnItemSelectedListener { view, position, id, item ->
                    type = id.toString()
                }
            }
        })

        PushDownAnim.setPushDownAnimTo(imgBackAddNote).setOnClickListener {
            onBackPressed()
        }

        PushDownAnim.setPushDownAnimTo(edContactInputAddNote).setOnClickListener {
            startActivityForResult(Intent(this, SearchContactActivity::class.java), 11)
        }

        PushDownAnim.setPushDownAnimTo(btnAddNote).setOnClickListener {
            viewModel.setNoteRequest(
                it,
                contact = edContactInputAddNote.text.toString().trim(),
                titleNote = edNoteTitleInputAddNote.text.toString().trim(),
                internalNote = edIntenalNoteInputAddNote.text.toString().trim(),
                state = state, type = type, priority = priority
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(Const.DATA_CONTACT_RESULT)) {
                val json = data.getStringExtra(Const.DATA_CONTACT_RESULT)
                this.contactResponse = Gson().fromJson(json, ContactResponse::class.java)
//                filterTicket.contactResponse = contactResponse
                edContactInputAddNote.text = contactResponse?.name
            }
        }
    }
}