package com.hosco.nextcrm.callcenter.ui.searchcontact

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.ui.contact.adapter.ContactAdapter
import com.hosco.nextcrm.callcenter.ui.contact.adapter.itemClickListener
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_filter_note.*
import kotlinx.android.synthetic.main.activity_search_contact.*


class SearchContactActivity : BaseActivity() {

    var viewModel: SearchContactViewModel = SearchContactViewModel()

    var adapter: ContactAdapter = ContactAdapter()

    override fun getRootLayoutId(): Int =
        com.hosco.nextcrm.callcenter.R.layout.activity_search_contact

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(SearchContactViewModel::class.java)
        setObserveLive(viewModel)
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

    override fun setupView(savedInstanceState: Bundle?) {
        setUpRCV()

        viewModel.onCreate()
        viewModel.getCustomerData()

        viewModel.dataCustomerResponse().observe(this, Observer {
            if (it != null) {
                if (viewModel.getCurrenPage()!! > 1)
                    adapter.addMore(it as ArrayList<ContactResponse>)
                else adapter.setData(it as ArrayList<ContactResponse>)
            }
        })
        viewModel.isShowLoadMore.observe(this, Observer {
            loadmoreSearchContact.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.isShowLoading.observe(this, Observer {
            swipeSearchContact.isRefreshing = if (it) true else false
        })
        viewModel.isNoData.observe(this, Observer {
            tvNoDataSearchContact.visibility = if (it) View.VISIBLE else View.GONE
        })

        edSearchContact.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed) {
                        viewModel.setStrSearchCustomer(edSearchContact.text.toString().trim())
                        viewModel.refreshListCustomer()
                        hideKeyboard()
                        return@OnEditorActionListener true // consume.
                    }
                }
                false
            }
        )

        swipeSearchContact.setOnRefreshListener {
            viewModel.refreshListCustomer()
        }

        PushDownAnim.setPushDownAnimTo(imgBackSearchContact).setOnClickListener {
            onBackPressed()
        }

    }

    fun setUpRCV() {
        if (viewModel != null && rcvSearchContact != null) {
            adapter = ContactAdapter()
            adapter.setItemClick(object : itemClickListener {
                override fun onClick(contactResponse: ContactResponse) {
                    val intent = Intent()
                    intent.putExtra(Const.DATA_CONTACT_RESULT, Gson().toJson(contactResponse))
                    setResult(
                        Activity.RESULT_OK,
                        intent
                    )
                    finish()
                }

            })
            val layoutManager = LinearLayoutManager(this)
            rcvSearchContact.layoutManager = layoutManager
            rcvSearchContact.adapter = adapter

            rcvSearchContact.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) { //1 for down
                        viewModel.loadMoreCustomer()
                    }
                }
            })
        }
    }

}