package org.linphone.activities.main.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal abstract class ChatScrollListener(private val mLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    // The total number of items in the data set after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = mLayoutManager.itemCount
        val firstVisibleItemPosition: Int = mLayoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition: Int = mLayoutManager.findLastVisibleItemPosition()

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                loading = true
            }
        }

        // If it’s still loading, we check to see if the data set count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the mVisibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && firstVisibleItemPosition < mVisibleThreshold && firstVisibleItemPosition > 0 && lastVisibleItemPosition < totalItemCount - mVisibleThreshold) {
            onLoadMore(totalItemCount)
            loading = true
        }
    }

    // Defines the process for actually loading more data based on page
    protected abstract fun onLoadMore(totalItemsCount: Int)

    companion object {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private const val mVisibleThreshold = 5
    }
}
