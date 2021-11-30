package com.hosco.nextcrm.callcenter.ui.setting

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.hosco.nextcrm.callcenter.BR
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.common.Const
import com.hosco.nextcrm.callcenter.common.extensions.AllContactList
import com.hosco.nextcrm.callcenter.common.extensions.SipHelper
import com.hosco.nextcrm.callcenter.common.extensions.SipHelperCrm
import com.hosco.nextcrm.callcenter.databinding.FragmentSettingBinding
import com.hosco.nextcrm.callcenter.ui.login.DomainActivity
import com.hosco.nextcrm.callcenter.ui.login.LoginActivity
import com.hosco.nextcrm.callcenter.utils.SharePreferenceUtils
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_setting.*


class SettingFragment : BaseFragment() {

    private lateinit var viewModel: SettingViewModel

    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_setting
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        viewModel.fillData()
        viewModel.getVersionApp(requireContext())
        val databinding = FragmentSettingBinding.bind(view)
        databinding.setVariable(BR.settingViewModel, viewModel)
        databinding.executePendingBindings()


        handleOnclick()

    }

    fun handleOnclick() {
        val popup = PopupMenu(requireContext(), tvStateProfile)
        popup.menuInflater.inflate(
            R.menu.state_menu,
            popup.menu
        )
        PushDownAnim.setPushDownAnimTo(imgUpdateProfile)
            .setOnClickListener {

                AllContactList.getInstance().getContact("95864586").let {
                    Log.d("zxcvbnm", it.toString())
                }
            }
        PushDownAnim.setPushDownAnimTo(layoutChangePasswordProfile)
            .setOnClickListener {

            }
        PushDownAnim.setPushDownAnimTo(tvLogoutProfile)
            .setOnClickListener {
                MaterialDialog(requireContext()).show {
                    title(text = context.getString(com.hosco.nextcrm.callcenter.R.string.txt_noti))
                    message(text = activity?.getString(com.hosco.nextcrm.callcenter.R.string.txt_logout_question))
                    positiveButton(com.hosco.nextcrm.callcenter.R.string.txt_ok) { dialog ->
                        SharePreferenceUtils.getInstances().logout()
                        startActivity(Intent(activity, LoginActivity::class.java))
                        activity?.finish()
                    }
                }
            }
        PushDownAnim.setPushDownAnimTo(tvValueNumberPhoneProfile)
            .setOnClickListener {

            }
        PushDownAnim.setPushDownAnimTo(tvStateProfile)
            .setOnClickListener {
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.item_state_available -> {
                            SipHelperCrm.onLogin()
                        }
//                        R.id.item_state_lunch_break -> {
//                            SipHelperCrm.disable(true)
//                        }
                        R.id.item_state_short_break -> {
                            SipHelperCrm.disable(false)
                        }
                        R.id.item_state_not_available -> {
                            SipHelperCrm.delete()
                        }
                    }
                    true
                }
                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popup)
                    mPopup.javaClass
                        .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        .invoke(mPopup, true)
                } catch (e: Exception) {
                    Log.e("Main", "Error showing menu icons.", e)
                } finally {
                    popup.show()
                }
            }
        handleListenerSip(popup)
    }

    fun handleListenerSip(popup: PopupMenu) {
        SipHelperCrm.stateSip.observe(this, Observer {
            Log.d(SettingFragment::class.java.simpleName, it.toString())
            when (it) {
                Const.SIP_PROGRESS -> {
                    tvStateProfile.text =
                        requireActivity().resources.getString(R.string.txt_loading)
                }
                Const.SIP_AVAILABLE -> {
//                    popup?.menu?.getItem(0)?.setCheckable(true)
                    tvStateProfile.text = popup.menu.getItem(0).title
                }
                Const.SIP_DISABLE -> {
                    tvStateProfile.text = popup.menu.getItem(1).title
//                    popup?.menu?.getItem(1)?.setCheckable(true)
                }
                Const.SIP_DELETE -> {
                    tvStateProfile.text = popup.menu.getItem(2).title
//                    popup?.menu?.getItem(2)?.setCheckable(true)
                }
            }

        })
    }


}

