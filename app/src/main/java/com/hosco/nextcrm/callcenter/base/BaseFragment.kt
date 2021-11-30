package com.hosco.nextcrm.callcenter.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.hosco.nextcrm.callcenter.common.DialogUtils

abstract class BaseFragment : Fragment() {
    protected val TAG = BaseFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getRootLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI(view)
    }

    abstract fun getRootLayoutId(): Int

    abstract fun setupViewModel()

    abstract fun setupUI(view: View)

    private lateinit var mProgressDialog: Dialog

    open fun showLoadingDialog() {
        if (isVisible) {
            DialogUtils.showLoadingDialog(activity)
        }
    }

    open fun showLoadingMore() {

    }

    open fun hideLoadingMore() {

    }

    fun setTitleToolbar(title: String? = "") {
//        ln_left.visibility = View.INVISIBLE
//        tv_title.text = title
    }

    open fun hideLoadingDialog() {
        DialogUtils.dismiss()
    }

    fun setObserveLive(viewModel: BaseViewModel) {
        viewModel.eventLoadingMore.observe(this, Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    if (it.peekContent()) {
                        showLoadingMore()
                    } else {
                        hideLoadingMore()
                    }
                }
            }
        })
        viewModel.eventLoading.observe(this, Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    if (it.peekContent()) {
                        showLoadingDialog()
                    } else {
                        hideLoadingDialog()
                    }
                }
            }
        })
        viewModel.eventFailure.observe(this, Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    showFailure(it.peekContent())
                }
            }
        })

        viewModel.eventNoData.observe(this, Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    if (it.peekContent())
                        showNoData(true)
                    else showNoData(false)
                }
            }
        })

    }

    open fun showFailure(throwable: Throwable) {
        if (throwable.message != null) {
            Log.i(TAG, "showQuestFailure: " + throwable.message)
        }
    }

    open fun showNoData(isNoData: Boolean) {
        Log.i(TAG, "showNoData")
    }

    fun goToActivity(c: Class<*>, bundle: Bundle? = null) {
        (activity as? BaseActivity)?.goToActivity(c, bundle)
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}