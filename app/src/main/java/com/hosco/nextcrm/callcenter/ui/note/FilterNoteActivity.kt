package com.hosco.nextcrm.callcenter.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.extensions.FilterTicket
import com.hosco.nextcrm.callcenter.model.KeyEventBus
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.PrioritySpinnerAdapter
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.StateSpinnerAdapter
import com.hosco.nextcrm.callcenter.ui.addnote.adapter.TypeSpinnerAdapter
import com.hosco.nextcrm.callcenter.ui.searchcontact.SearchContactActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_filter_note.*
import org.greenrobot.eventbus.EventBus

class FilterNoteActivity : BaseActivity() {

    var viewModel: NoteViewModel = NoteViewModel()
    var state: String = ""
    var type: String = ""
    var priority: String = ""
    var contactResponse: ContactResponse? = null

    var filterTicket = FilterTicket.getInstance()

    override fun getRootLayoutId(): Int {
        return R.layout.activity_filter_note
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupView(savedInstanceState: Bundle?) {

        viewModel.getInputData()

        contactResponse = filterTicket.contactResponse

        viewModel.dataState.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                state = it[0].code
                val adapter = StateSpinnerAdapter(it)
                spnStatusFilterTicket.setAdapter(adapter)
                if (filterTicket.getState() != null && !filterTicket.getState().isEmpty()) {
                    for (i in 0..it.size - 1) {
                        if (it[i].code.equals(filterTicket.getState())) {
                            spnStatusFilterTicket.selectedIndex = i
                            state = it[i].code
                        }
                    }
                }else {
                    spnStatusFilterTicket.selectedIndex = 0
                }
                spnStatusFilterTicket.setOnItemSelectedListener { view, position, id, item ->
                    state = item.toString()
                }
            }
        })

        viewModel.dataPriority.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                priority = it[0].name
                val adapter = PrioritySpinnerAdapter(it)
                spnPriorityFilterTicket.setAdapter(adapter)
                if (filterTicket.getPriority() != null && !filterTicket.getPriority().isEmpty()) {
                    for (i in 0..it.size - 1) {
                        if (it[i].name.equals(filterTicket.getPriority())) {
                            spnPriorityFilterTicket.selectedIndex = i
                            priority = it[i].name
                        }
                    }
                } else {
                    spnPriorityFilterTicket.selectedIndex = 0
                }
                spnPriorityFilterTicket.setOnItemSelectedListener { view, position, id, item ->
                    priority = item.toString()
                }
            }
        })

        viewModel.datatype.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                type = it[0].id.toString()
                val adapter = TypeSpinnerAdapter(it)
                spnTypeFilterTicket.setAdapter(adapter)
                if (filterTicket.getType() != null && !filterTicket.getType().isEmpty()) {
                    for (i in 0..it.size - 1) {
                        if (it[i].id.toString().equals(filterTicket.getType())) {
                            spnTypeFilterTicket.selectedIndex = i
                            type = it[i].id.toString()
                        }
                    }
                } else {
                    spnTypeFilterTicket.selectedIndex = 0
                }
                spnTypeFilterTicket.setOnItemSelectedListener { view, position, id, item ->
                    type = id.toString()
                }
            }
        })

        contactResponse.let {
            it?.name.let {
                tvContactFilterTicket.text = contactResponse?.name
            }
        }

        PushDownAnim.setPushDownAnimTo(imgBackFilterTicket).setOnClickListener {
            onBackPressed()
        }

        PushDownAnim.setPushDownAnimTo(layoutContactFilterTicket).setOnClickListener {
            startActivityForResult(Intent(this, SearchContactActivity::class.java), 11)
        }

        PushDownAnim.setPushDownAnimTo(btnRefreshFilterNote).setOnClickListener {
            contactResponse = null
            filterTicket.refreshInput()
            viewModel.getInputData()
            tvContactFilterTicket.text = ""
        }

        PushDownAnim.setPushDownAnimTo(imgDonefilterTicket).setOnClickListener {
            filterTicket.state = state
            filterTicket.priority = priority
            filterTicket.type = type
            EventBus.getDefault().post(KeyEventBus(Const.KEY_RELOAD_LIST_NOTE))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(Const.DATA_CONTACT_RESULT)) {
                val json = data.getStringExtra(Const.DATA_CONTACT_RESULT)
                this.contactResponse = Gson().fromJson(json, ContactResponse::class.java)
                filterTicket.contactResponse = contactResponse
                tvContactFilterTicket.text = contactResponse?.name
            }
        }
    }

}