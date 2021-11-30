package com.hosco.nextcrm.callcenter.base

abstract class LazyLoadFragment : BaseFragment() {
    var isLoaded = false
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !isLoaded) {
            loadData()
            isLoaded = true
        }
    }

    abstract fun loadData()
}