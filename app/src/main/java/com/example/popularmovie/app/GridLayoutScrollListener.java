package com.example.popularmovie.app;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.content.MovieContent;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/26/15.
 */
/*
 * This class extends RecyclerView.OnScrollListner so we can override the scroll methods
 */
public class GridLayoutScrollListener extends RecyclerView.OnScrollListener {
    private final static String LOG_TAG = GridLayoutScrollListener.class.getSimpleName();

    private GridLayoutManager gridLayoutManager;
    private Context ctx;

    private int visibleItemCount, totalItemCount, firstVisibleItem, lastVisibleItem;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 4;

    public GridLayoutScrollListener(GridLayoutManager layoutManager, Context context) {
        gridLayoutManager = layoutManager;
        ctx = context;
    }

    /*
     * This method detects RecyclerView scrolling.  If reach near the end of the list,  it will call loadMove().  The
     * loadMove() is Overrided in Activity method to fetch more movie data.
     */
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        firstVisibleItem = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

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
            int page = MovieContent.getMovieSortOrder() == MovieSortOrder.POPULAR ?
                    MovieContent.LATEST_PAGE_RESULT_POPULAR : MovieContent.LATEST_PAGE_RESULT_RATING;

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
