package com.hosco.nextcrm.callcenter.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.coreContext
import com.hosco.nextcrm.callcenter.CallCenterApplication.Companion.corePreferences
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseActivity
import com.hosco.nextcrm.callcenter.common.extensions.SipHelper
import com.hosco.nextcrm.callcenter.common.extensions.SipHelperCrm
import com.hosco.nextcrm.callcenter.model.response.NoteResponse
import com.hosco.nextcrm.callcenter.network.remote.auth.ExtentionConfig
import com.hosco.nextcrm.callcenter.ui.contact.ContactFragment
import com.hosco.nextcrm.callcenter.ui.dialpad.DialpadActivity
import com.hosco.nextcrm.callcenter.ui.history.HistoryFragment
import com.hosco.nextcrm.callcenter.ui.note.NoteFragment
import com.hosco.nextcrm.callcenter.ui.setting.SettingFragment
import com.hosco.nextcrm.callcenter.utils.Key
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_setting.*
import org.linphone.core.AccountCreator
import org.linphone.core.ProxyConfig
import org.linphone.core.TransportType
import org.linphone.core.tools.Log
import java.util.*

class HomeActivity : BaseActivity() {
    override fun getRootLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun setupViewModel() {
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    override fun setupView(savedInstanceState: Bundle?) {

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_note -> vpContainer.currentItem = 0
                R.id.nav_contact -> vpContainer.currentItem = 1
                R.id.nav_history -> vpContainer.currentItem = 2
                R.id.nav_setting -> vpContainer.currentItem = 3
            }
            return@setOnNavigationItemSelectedListener true
        }

        vpContainer.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                bottom_navigation.menu.getItem(position).isChecked = true;
                bottom_navigation.menu.getItem(position);
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        setupViewPager(vpContainer)

        PushDownAnim.setPushDownAnimTo(fabDialer)
            .setOnClickListener {
                Intent(
                    applicationContext,
                    DialpadActivity::class.java
                ).apply {
                    startActivity(this)
                }
            }
        createProxyConfig()
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


    override fun showLoadingDialog() {
        super.showLoadingDialog()
        Toast.makeText(this, "loading", Toast.LENGTH_LONG).show()
    }

    override fun hideLoadingDialog() {
        super.hideLoadingDialog()
        Toast.makeText(this, "hide Loading", Toast.LENGTH_LONG).show()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(NoteFragment.newInstance())
        adapter.addFragment(ContactFragment.newInstance())
        adapter.addFragment(HistoryFragment.newInstance())
        adapter.addFragment(SettingFragment.newInstance())
        viewPager.adapter = adapter;
        viewPager.offscreenPageLimit = 4
    }

    fun createProxyConfig() {
        if (SharePreferenceUtils.getInstances().getBoolean(Key.SIP_AVAILABLE) == true)
            SipHelperCrm.onLogin()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action === MotionEvent.ACTION_DOWN) {
            val v: View? = getCurrentFocus()
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}