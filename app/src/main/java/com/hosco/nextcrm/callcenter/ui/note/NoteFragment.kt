package com.hosco.nextcrm.callcenter.ui.note

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.extensions.FilterTicket
import com.hosco.nextcrm.callcenter.model.KeyEventBus
import com.hosco.nextcrm.callcenter.model.response.NoteResponse
import com.hosco.nextcrm.callcenter.ui.addnote.AddNoteActivity
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_note.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NoteFragment : BaseFragment() {

    private lateinit var viewModel: NoteViewModel

    var adapter = NoteAdapter()

    lateinit var swipeNote: SwipeRefreshLayout

    lateinit var rcvNote: RecyclerView

    var filterTicket = FilterTicket.getInstance()

    companion object {
        fun newInstance(): NoteFragment {
            return NoteFragment()
        }
    }

    override fun getRootLayoutId() = R.layout.fragment_note

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        swipeNote = view.findViewById<SwipeRefreshLayout>(R.id.swipeNote)
        rcvNote = view.findViewById<RecyclerView>(R.id.rcvNote)
        setUpRCV()
        viewModel.onCreate()

        viewModel.getDataContact()

        viewModel.showNoteData().observe(this, Observer {
            if (it != null) {
                layoutNodataNote.visibility = View.GONE
                if (viewModel.getCurrenPage()!! > 1)
                    adapter.addMore(it as ArrayList<NoteResponse>)
                else {
                    adapter.setData(it as ArrayList<NoteResponse>)
                    if (it.size == 0) layoutNodataNote.visibility = View.VISIBLE
                }
            } else {
                layoutNodataNote.visibility = View.VISIBLE
            }
        }
        )

        swipeNote.setOnRefreshListener {
            viewModel.refreshList()
        }

        PushDownAnim.setPushDownAnimTo(imgAddNote).setOnClickListener {
            val intent = Intent(requireActivity(), AddNoteActivity::class.java)
            startActivity(intent)
        }
        PushDownAnim.setPushDownAnimTo(imgFillter).setOnClickListener {
            val intent = Intent(requireActivity(), FilterNoteActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {

        }
        return true
    }

    fun setUpRCV() {
        if (viewModel != null && rcvNote != null) {
            adapter = NoteAdapter()
            val layoutManager = LinearLayoutManager(context)
            rcvNote.layoutManager = layoutManager
            rcvNote.adapter = adapter

            rcvNote.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
        swipeNote?.isRefreshing = true

    }

    override fun hideLoadingDialog() {
        swipeNote?.isRefreshing = false
    }

    override fun showLoadingMore() {
        loadmoreNote.let {
            it.visibility = View.VISIBLE
        }
    }

    override fun hideLoadingMore() {
        loadmoreNote.let {
            it.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResultReceived(result: String) {
        adapter.let {
            try {
                it.addMore(Gson().fromJson(result, NoteResponse::class.java))
                rcvNote.smoothScrollToPosition(0)
            } catch (e: Exception) {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReloadReceived(result: KeyEventBus) {
        result.let {
            if (it.key.equals(Const.KEY_RELOAD_LIST_NOTE)) {
                viewModel.setParamSearch(
                    if (filterTicket.contactResponse == null) "" else filterTicket.contactResponse.code,
                    if (filterTicket.type.equals("0")) "" else filterTicket.type,
                    if (filterTicket.state.equals("All")) "" else filterTicket.state,
                    if (filterTicket.priority.equals("All")) "" else filterTicket.priority
                )
            }
            viewModel.refreshList()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

}