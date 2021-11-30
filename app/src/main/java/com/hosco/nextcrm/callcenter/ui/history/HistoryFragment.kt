package com.hosco.nextcrm.callcenter.ui.history

import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.model.response.HistoryResponse
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_note.*

class HistoryFragment : BaseFragment() {

    private lateinit var viewModel: HistoryViewModel

    var adapter: HistoryAdapter = HistoryAdapter()

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_history
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {

        setUpRCV()
        viewModel.onCreate()

        swipeHistory.setOnRefreshListener {
            viewModel.refreshList()
        }

        viewModel.getHistoryData()

        viewModel.showHistoryData().observe(this, Observer {
            if (it != null) {
                if (viewModel.getCurrenPage()!! > 1)
                    adapter.addMore(it as ArrayList<HistoryResponse>)
                else adapter.setData(it as ArrayList<HistoryResponse>)
            }
        })

    }

    fun setUpRCV() {
        if (viewModel != null && rcvHistory != null) {
            if (adapter == null)
                adapter = HistoryAdapter()
            val layoutManager = LinearLayoutManager(context)
            rcvHistory.layoutManager = layoutManager
            rcvHistory.adapter = adapter

            rcvHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) { //1 for down
                        viewModel.addCurrentPage()
                    }
                }
            })
        }
    }

    override fun showLoadingDialog() {
        swipeHistory?.isRefreshing = true

    }

    override fun hideLoadingDialog() {
        swipeHistory?.isRefreshing = false
    }

    override fun showFailure(throwable: Throwable) {
        super.showFailure(throwable)
    }

    override fun showLoadingMore() {
        loadmoreHistory.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun hideLoadingMore() {
        loadmoreHistory.let {
            it.visibility = View.GONE
        }
    }

}