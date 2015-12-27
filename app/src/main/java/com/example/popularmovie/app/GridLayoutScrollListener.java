package com.example.popularmovie.app;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.popularmovie.app.content.MovieContent;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/26/15.
 */
public class GridLayoutScrollListener extends RecyclerView.OnScrollListener {
    private final static String LOG_TAG = GridLayoutScrollListener.class.getSimpleName();

    private GridLayoutManager gridLayoutManager;
    private Context ctx;

    private int visibleItemCount, totalItemCount, lastVisibleItem;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 4;

    public GridLayoutScrollListener(GridLayoutManager layoutManager, Context context) {
        gridLayoutManager = layoutManager;
        ctx = context;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        lastVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                Log.d(LOG_TAG, "totalItemCount:" + totalItemCount + " previousTotal:" + previousTotal);
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount < (lastVisibleItem + visibleThreshold))) {
            Log.d(LOG_TAG, "totalItemCount:" + totalItemCount + " visibleItemCount:" + visibleItemCount +
                    " lastVisibleItem:" + lastVisibleItem + " visibleThreshold:" + visibleThreshold +
                    " ");
            int page = MovieContent.LATEST_PAGE_RESULT;

            loadMore(++page);

            loading = true;
        }

    }

    /**
     * This method needs to be @Override by Activity or
     * Fragment class to implement the call to get more data.
     *
     * @param page page of theMovieDB API result requested
     */
    public void loadMore(int page) {
    }

}
