package com.hosco.nextcrm.callcenter.ui.contact

import android.view.View
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.model.response.InternalResponse
import com.hosco.nextcrm.callcenter.ui.contact.adapter.InternalAdapter
import com.hosco.nextcrm.callcenter.ui.contact.adapter.ItemClickInternal
import com.hosco.nextcrm.callcenter.ui.dialpad.DialpadViewModel
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_customer.*
import kotlinx.android.synthetic.main.fragment_internal.*

class InternalFragment : BaseFragment() {

    private lateinit var viewModel: ContactViewModel

    var dialpadViewModel: DialpadViewModel = DialpadViewModel()

    var adapter: InternalAdapter = InternalAdapter()

    companion object {
        fun newInstance(): InternalFragment {
            return InternalFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_internal
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        dialpadViewModel = ViewModelProviders.of(this).get(DialpadViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        setUpRCV()
        viewModel.onCreate()

        swipeInternal.setOnRefreshListener {
            viewModel.refreshListInternal()
        }

        viewModel.getInternalData()

        viewModel.dataInternalResponse().observe(this, Observer {
            if (it != null) {
                if (viewModel.getCurrenPageInternal() > 1)
                    adapter.addMore(it as ArrayList<InternalResponse>)
                else adapter.setData(it as ArrayList<InternalResponse>)
                AllContactList.getInstance().addInternalToListContact(it)
            }
        })

        edSearchInternal.doAfterTextChanged {
            var strSearch = edSearchInternal.text.toString().trim()
            imgClearSearchInternal.visibility = if (strSearch.isEmpty()) View.GONE else View.VISIBLE
            adapter.getFilter()?.filter(strSearch)
        }

        PushDownAnim.setPushDownAnimTo(imgClearSearchInternal).setOnClickListener {
            edSearchInternal.clearFocus()
            edSearchInternal.text.clear()
            viewModel.setStrSearchInternal(edSearchInternal.text.toString().trim())
            viewModel.refreshListInternal()
        }
    }

    fun setUpRCV() {
        if (viewModel != null && rcvInternal != null) {
            adapter = InternalAdapter()
            adapter.setInternalListener(object : ItemClickInternal {
                override fun onClick(internalResponse: InternalResponse) {
                    if (!internalResponse.crmExtensionId.isNullOrEmpty()) {
                        dialpadViewModel.startCall(rcvInternal, internalResponse.crmExtensionId)
                    }
                }
            })
            val layoutManager = LinearLayoutManager(context)
            rcvInternal.layoutManager = layoutManager
            rcvInternal.adapter = adapter

            rcvInternal.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) { //1 for down
                        viewModel.loadMoreInternal()
                    }
                }
            })
        }
    }

    override fun showLoadingDialog() {
        swipeInternal?.isRefreshing = true

    }

    override fun hideLoadingDialog() {
        swipeInternal?.isRefreshing = false
    }

    override fun showLoadingMore() {
        loadmoreInternal.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun hideLoadingMore() {
        loadmoreInternal.let {
            it.visibility = View.GONE
        }
    }

    override fun showNoData(isNoData: Boolean) {
        if (layoutNodataInternal != null) {
//            if (isNoData) {
//                layoutNodataInternal.visibility = View.VISIBLE
//            } else {
//                layoutNodataInternal.visibility = View.GONE
//            }
        }
    }
}