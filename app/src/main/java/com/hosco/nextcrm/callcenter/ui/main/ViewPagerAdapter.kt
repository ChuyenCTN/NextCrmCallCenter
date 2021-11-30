package com.hosco.nextcrm.callcenter.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPagerAdapter(manager: FragmentManager) :
    FragmentPagerAdapter(manager) {
    private var mFragmentList: ArrayList<Fragment> = ArrayList()
    private var mTitle: ArrayList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    fun addTab(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mTitle.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitle[position]
    }

}
