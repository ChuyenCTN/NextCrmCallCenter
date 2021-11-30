package com.hosco.nextcrm.callcenter.customview

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class MeEndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener() {
    val TAG = MeEndlessRecyclerViewScrollListener::class.java.simpleName


    private var previousTotal = 8 // The total number of items in the dataset after the last load

    private var visibleThreshold = 2
    private var visibleThresholdInit = 2
    private var loading = true // True if we are still waiting for the last set of data to load.

    //    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    // The minimum amount of items to have below your current scroll position before loading more.
    private var firstVisibleItem = 0
    //    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.

    // The minimum amount of items to have below your current scroll position before loading more.
    private var visibleItemCount = 0
    //    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.

    // The minimum amount of items to have below your current scroll position before loading more.
    private var totalItemCount = 0

    private var current_page = 1

    private var mLinearLayoutManager: LinearLayoutManager? = null

    fun EndlessRecyclerOnScrollListener(
        linearLayoutManager: RecyclerView.LayoutManager?,
        previousTotal: Int
    ) {
        visibleThresholdInit = previousTotal
        this.previousTotal = visibleThresholdInit - 2
        mLinearLayoutManager = linearLayoutManager as LinearLayoutManager?
    }

    fun EndlessRecyclerOnScrollListener(linearLayoutManager: RecyclerView.LayoutManager?) {
//        this.visibleThreshold = visibleThreshold;
        visibleThresholdInit = 20
        previousTotal = visibleThresholdInit - 2
        mLinearLayoutManager = linearLayoutManager as LinearLayoutManager?
    }

    fun reset() {
        loading = true
        previousTotal = visibleThresholdInit - 2
        current_page = 1
        visibleThreshold = 2
    }

    fun setVisibleThreshold(visibleThreshold: Int) {
        this.visibleThreshold = visibleThreshold
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager!!.itemCount
        firstVisibleItem = mLinearLayoutManager!!.findFirstVisibleItemPosition()
        Log.e(
            TAG,
            "" + visibleItemCount + "/" + totalItemCount + "/" + firstVisibleItem + "/" + previousTotal
        );
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        Log.d(TAG, "" + visibleItemCount);
        Log.d(TAG, "" + totalItemCount);
        Log.d(TAG, "" + firstVisibleItem);
        if (!loading && totalItemCount > visibleItemCount && (totalItemCount - visibleItemCount
                    <= firstVisibleItem + visibleThreshold)
        ) {
            // End has been reached

            // Do something
            current_page += 1
            onLoadMore(current_page)
            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)

}