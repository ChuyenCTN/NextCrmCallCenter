package com.hosco.nextcrm.callcenter.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.DialogUtils
import androidx.core.content.ContextCompat


abstract class BaseActivity : AppCompatActivity() {
    private var isBackPressed: Boolean = false
    protected val TAG = BaseActivity::class.java.simpleName


    var x = 10;
    var y = 15
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.anim_right_to_left_enter, R.anim.anim_right_to_left_leave)
        setContentView(getRootLayoutId())
        setupView(savedInstanceState)

        val window: Window = getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(getColor(R.color.colorPressButton))
    }

    abstract fun getRootLayoutId(): Int

    abstract fun setupViewModel()

    abstract fun setupView(savedInstanceState: Bundle?)

    fun setTitleToolbar(title: String? = "") {
//        tv_title.text = title
//        ln_left.setOnClickListener {
//            onBackPressed()
//        }
    }

    fun goToActivity(c: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, c)
        bundle?.let {
            intent.putExtra(Const.KEY_EXTRA_DATA, bundle)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun extraData(getData: (Bundle) -> Unit) {
        var bundle = intent.getBundleExtra(Const.KEY_EXTRA_DATA)
        bundle?.let { getData.invoke(it) }
    }

    fun replaceFragment(viewID: Int, fragment: Fragment?, tag: String? = null) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.add(viewID, fragment!!)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    fun setObserveLive(viewModel: BaseViewModel) {
        viewModel.eventLoading.observe(this, androidx.lifecycle.Observer {
            if (it.getContentIfNotHandled() != null) {
                if (it.peekContent()) {
                    showLoadingDialog()
                } else {
                    hideLoadingDialog()
                }
            }
        })
        viewModel.eventFailure.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    showFailure(it.peekContent())
                }
            }
        })
        viewModel.eventErrorMessage.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (it.getContentIfNotHandled() != null) {
                    showError(it.peekContent())
                }
            }
        })
    }

    open fun showError(message: String) {
        Log.e(TAG, "showQuestError: " + message)
    }

    open fun showLoadingDialog() {
        DialogUtils.showLoadingDialog(this)
    }

    open fun hideLoadingDialog() {
        DialogUtils.dismiss()
    }

    private fun showFailure(throwable: Throwable) {
        if (throwable.message != null) {
            Log.i(TAG, "showQuestFailure: " + throwable.message)
        }
    }

    fun widthItem(number: Int): Int {
        return Resources.getSystem().displayMetrics.widthPixels / number
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        if (!isTaskRoot) {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.anim_left_to_right_enter,
                R.anim.anim_left_to_right_leave
            )
            return
        }
        if (isBackPressed) {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.anim_left_to_right_enter,
                R.anim.anim_left_to_right_leave
            )
        } else {
            Toast.makeText(this, R.string.back_again_to_exit, Toast.LENGTH_SHORT).show()
            isBackPressed = true
            Handler().postDelayed({ isBackPressed = false }, 2000)
        }
    }
}