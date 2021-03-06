package com.hosco.nextcrm.callcenter.ui.contact

import android.view.View
import androidx.annotation.NonNull
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.model.request.PhoneInfoRequest
import com.hosco.nextcrm.callcenter.model.response.ContactResponse
import com.hosco.nextcrm.callcenter.ui.contact.adapter.ContactAdapter
import com.hosco.nextcrm.callcenter.ui.contact.adapter.itemClickListener
import com.hosco.nextcrm.callcenter.ui.dialpad.DialpadViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_customer.*
import org.linphone.utils.DialogUtils

class CustomerFragment : BaseFragment() {

    private lateinit var viewModel: ContactViewModel

    var dialpadViewModel: DialpadViewModel = DialpadViewModel()

    var adapter: ContactAdapter = ContactAdapter()

    companion object {
        fun newInstance(): CustomerFragment {
            return CustomerFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_customer
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        dialpadViewModel = ViewModelProviders.of(this).get(DialpadViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        setUpRCV()
        viewModel.onCreate()

        swipeCustomer.setOnRefreshListener {
            viewModel.refreshListCustomer()
        }

        viewModel.getCustomerData()

        viewModel.dataCustomerResponse().observe(this, Observer {
            if (it != null) {
                AllContactList.getInstance().addCustomerToListContact(it)
                if (viewModel.getCurrenPageCustomer()!! > 1)
                    adapter.addMore(it as ArrayList<ContactResponse>)
                else adapter.setData(it as ArrayList<ContactResponse>)
            }
        })
        viewModel.dataPhoneInfoResponse().observe(this, Observer {
            if (it != null) {
                dialpadViewModel.startCall(rcvCustomer, it.phone)
            }
        })

        viewModel.isShowLoadingPhoneInfo.observe(this, Observer {
            if (it == true)
                com.hosco.nextcrm.callcenter.common.DialogUtils.showCrmLoadingDialog(
                    context,
                    context?.resources?.getString(R.string.txt_loading_get_info_phone_number)
                )
            else
                com.hosco.nextcrm.callcenter.common.DialogUtils.dismissCrm()
        })

        PushDownAnim.setPushDownAnimTo(imgSearchCustomer).setOnClickListener {
            if (!edSearchCustomer.text.trim().isEmpty()) {
                viewModel.setStrSearchCustomer(edSearchCustomer.text.toString().trim())
                viewModel.refreshListCustomer()
            }
        }

        edSearchCustomer.doAfterTextChanged {
            var strSearch = edSearchCustomer.text.toString().trim()
            imgClearSearchCustomer.visibility = if (strSearch.isEmpty()) View.GONE else View.VISIBLE
            adapter.getFilter()?.filter(strSearch)
        }

        PushDownAnim.setPushDownAnimTo(imgClearSearchCustomer).setOnClickListener {
            edSearchCustomer.clearFocus()
            edSearchCustomer.text.clear()
            viewModel.setStrSearchCustomer(edSearchCustomer.text.toString().trim())
            viewModel.refreshListCustomer()
        }
    }

    fun setUpRCV() {
        if (viewModel != null && rcvCustomer != null) {
            adapter = ContactAdapter()
            adapter.setItemClick(object : itemClickListener {
                override fun onClick(contactResponse: ContactResponse) {
                    contactResponse.let {
                        if (it.mobile != null && !it.mobile.isEmpty()) {
                            viewModel.getPhoneInfo(
                                rcvCustomer,
                                PhoneInfoRequest(contactId = it.mobile, mobile = "")
                            )
                        }
                    }
                }

            })
            val layoutManager = LinearLayoutManager(context)
            rcvCustomer.layoutManager = layoutManager
            rcvCustomer.adapter = adapter

            rcvCustomer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) { //1 for down
                        viewModel.loadMoreCustomer()
                    }
                }
            })
        }
    }

    override fun showLoadingDialog() {
        swipeCustomer?.isRefreshing = true
    }

    override fun hideLoadingDialog() {
        swipeCustomer?.isRefreshing = false
    }

    override fun showLoadingMore() {
        loadmoreCustomer.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun hideLoadingMore() {
        loadmoreCustomer.let {
            it.visibility = View.GONE
        }
    }

    override fun showNoData(isNoData: Boolean) {
        if (layoutNodataCustomer != null) {
            if (isNoData) {
                layoutNodataCustomer.visibility = View.VISIBLE
            } else {
                layoutNodataCustomer.visibility = View.GONE
            }
        }
    }


}