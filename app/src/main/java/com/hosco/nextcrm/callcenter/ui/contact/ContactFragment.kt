package com.hosco.nextcrm.callcenter.ui.contact

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.hosco.nextcrm.callcenter.R
import com.hosco.nextcrm.callcenter.base.BaseFragment
import com.hosco.nextcrm.callcenter.ui.addcontact.AddContactActivity
import com.hosco.nextcrm.callcenter.ui.main.ViewPagerAdapter
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.android.synthetic.main.fragment_contact.*

class ContactFragment : BaseFragment() {

    private lateinit var viewModel: ContactViewModel

    companion object {
        fun newInstance(): ContactFragment {
            return ContactFragment()
        }
    }

    override fun getRootLayoutId(): Int {
        return R.layout.fragment_contact
    }

    override fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        setObserveLive(viewModel)
    }

    override fun setupUI(view: View) {
        setupViewPager(vpContact)
        tabContact.setupWithViewPager(vpContact)
        tabContact.setSelectedTabIndicatorColor(resources.getColor(R.color.white))
        vpContact.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
//                bottom_navigation.menu.getItem(position).isChecked = true;
//                bottom_navigation.menu.getItem(position);
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        PushDownAnim.setPushDownAnimTo(imgAddContact).setOnClickListener {
            startActivity(Intent(requireActivity(), AddContactActivity::class.java))
        }
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = childFragmentManager?.let { ViewPagerAdapter(it) }
        adapter?.addTab(CustomerFragment.newInstance(), getString(R.string.txt_customer))
        adapter?.addTab(InternalFragment.newInstance(), getString(R.string.txt_internal))
        adapter?.addTab(DeviceFragment.newInstance(), getString(R.string.txt_personal))

        viewPager.adapter = adapter;
        viewPager.offscreenPageLimit = 3
    }

}