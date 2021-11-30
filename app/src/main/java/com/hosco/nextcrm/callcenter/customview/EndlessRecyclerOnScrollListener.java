package com.hosco.nextcrm.callcenter.customview;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by tienb on 9/29/2015.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();


//    private int previousTotal = 9; // The total number of items in the dataset after the last load
    private int previousTotal = 8; // The total number of items in the dataset after the last load
    private int visibleThreshold = 2;
    private int visibleThresholdInit = 2;
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
//    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
// The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(RecyclerView.LayoutManager linearLayoutManager, int previousTotal) {
        this.visibleThresholdInit =previousTotal;
        this.previousTotal = visibleThresholdInit-2;
        this.mLinearLayoutManager = (LinearLayoutManager) linearLayoutManager;
    }
    public EndlessRecyclerOnScrollListener(RecyclerView.LayoutManager linearLayoutManager) {
//        this.visibleThreshold = visibleThreshold;
        this.visibleThresholdInit =20;
        this.previousTotal = visibleThresholdInit-2;
        this.mLinearLayoutManager = (LinearLayoutManager) linearLayoutManager;
    }
    public void reset() {
        loading = true;
        this.previousTotal = visibleThresholdInit-2;
        current_page = 1;
        visibleThreshold = 2;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
//        Log.e("Endless onScrolled",visibleItemCount +"/"+totalItemCount+"/"+firstVisibleItem+"/"+previousTotal);

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
//        Log.d("visibleItemCount",visibleItemCount+"");
//        Log.d("totalItemCount",totalItemCount+"");
//        Log.d("firstVisibleItem",firstVisibleItem+"");
        if (!loading &&totalItemCount>visibleItemCount&& (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page += 1;
            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}
