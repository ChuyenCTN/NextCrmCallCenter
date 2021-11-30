package com.hosco.nextcrm.callcenter.ui.dialpad

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.extension.addCharacter
import com.hosco.nextcrm.callcenter.extension.getKeyEvent
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_dialpad_my.*
import org.linphone.activities.main.viewmodels.SharedMainViewModel

class DialpadActivity : BaseActivity() {

    var viewModel: DialpadViewModel = DialpadViewModel()
    private lateinit var sharedViewModel: SharedMainViewModel

    override fun getRootLayoutId(): Int {
        return R.layout.activity_dialpad_my
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(DialpadViewModel::class.java)
        sharedViewModel = run {
            ViewModelProvider(this).get(SharedMainViewModel::class.java)
        }

    }

    override fun setupView(savedInstanceState: Bundle?) {
        eventClickKeyoard()

    }

    fun eventClickKeyoard() {
        PushDownAnim.setPushDownAnimTo(btn0)
            .setOnClickListener {
                dialpadPressed('0', it)
            }
        PushDownAnim.setPushDownAnimTo(btn1)
            .setOnClickListener {
                dialpadPressed('1', it)
            }
        PushDownAnim.setPushDownAnimTo(btn2)
            .setOnClickListener {
                dialpadPressed('2', it)
            }
        PushDownAnim.setPushDownAnimTo(btn3)
            .setOnClickListener {
                dialpadPressed('3', it)
            }
        PushDownAnim.setPushDownAnimTo(btn4)
            .setOnClickListener {
                dialpadPressed('4', it)
            }
        PushDownAnim.setPushDownAnimTo(btn5)
            .setOnClickListener {
                dialpadPressed('5', it)
            }
        PushDownAnim.setPushDownAnimTo(btn6)
            .setOnClickListener {
                dialpadPressed('6', it)
            }
        PushDownAnim.setPushDownAnimTo(btn7)
            .setOnClickListener {
                dialpadPressed('7', it)
            }
        PushDownAnim.setPushDownAnimTo(btn8)
            .setOnClickListener {
                dialpadPressed('8', it)
            }
        PushDownAnim.setPushDownAnimTo(btn9)
            .setOnClickListener {
                dialpadPressed('9', it)
            }
        PushDownAnim.setPushDownAnimTo(btnstar)
            .setOnClickListener {
                dialpadPressed('*', it)
            }
        PushDownAnim.setPushDownAnimTo(btnhash)
            .setOnClickListener {
                dialpadPressed('#', it)
            }
        PushDownAnim.setPushDownAnimTo(btncall)
            .setOnClickListener {
                getPhoneValue()
            }
        PushDownAnim.setPushDownAnimTo(btnBackspase)
            .setOnClickListener {
                clearChar(it)
            }
        PushDownAnim.setPushDownAnimTo(btnDialpad)
            .setOnClickListener {
                onBackPressed()
//                overridePendingTransition(R.anim.anim_slide_out_down, R.anim.anim_slide_in_up)
            }

        btnBackspase.setOnLongClickListener(View.OnLongClickListener {
            try {
                clearInput()
            } catch (e: Exception) {
                return@OnLongClickListener false
            }
            false
        })

        disableKeyboardPopping()
    }

    fun getPhoneValue() {
        if (edValueDialpad.text.toString().trim().isNullOrEmpty())
            return
        viewModel.startCall(edValueDialpad.text.toString().trim())
        edValueDialpad.setText("")
    }

    private fun dialpadPressed(char: Char, view: View?) {
        edValueDialpad.addCharacter(char)
    }

    private fun clearChar(view: View) {
        edValueDialpad.dispatchKeyEvent(edValueDialpad.getKeyEvent(KeyEvent.KEYCODE_DEL))
    }

    private fun clearInput() {
        edValueDialpad.setText("")
    }

    private fun disableKeyboardPopping() {
        edValueDialpad.showSoftInputOnFocus = false
    }


}