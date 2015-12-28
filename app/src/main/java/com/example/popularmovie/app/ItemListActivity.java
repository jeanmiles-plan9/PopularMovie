package com.example.popularmovie.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.volley.MovieRequest;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ItemListActivity.class.getSimpleName();
    private static final String MAIN_TITLE = "Pop Movies";
    private MovieRequest movieRequest;
    private static final String GRID_STATE = "gridState";
    private int grid_scroll_position;
    private RecyclerView recyclerView;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;

    /*
     * RecyclerView and Adapter
     */
    private SimpleGridRecyclerViewAdapter simpleGridRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(MAIN_TITLE);
        }

        final View view = findViewById(R.id.grid_list);
        assert view != null;
        recyclerView = (RecyclerView) view;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(new GridLayoutScrollListener(gridLayoutManager, getApplicationContext()) {
            @Override
            public void loadMore(int page) {
                Log.d(LOG_TAG, " page to fetch is " + page);
                fetchMoviesFor(MovieContent.getMovieSortOrder(), page);
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }

        setupRecyclerView(recyclerView);
        /*
         *  This checks to see if there is any contents in movie, if so don't call API again
         *  only time movie gets called again is app startup
         */
        movieRequest = new MovieRequest();
        if (MovieContent.ITEMS.isEmpty()) {
            fetchMoviesFor(MovieContent.getMovieSortOrder(), 1);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            if (MovieContent.getMovieSortOrder() == MovieSortOrder.RATING) {
                fetchMoviesFor(MovieSortOrder.POPULAR, 1);
                Log.d(LOG_TAG, "called request for most popular selected");
            }
            Log.d(LOG_TAG, "most popular selected");
            return true;
        } else if (id == R.id.action_rated) {
            if (MovieContent.getMovieSortOrder() == MovieSortOrder.POPULAR) {
                fetchMoviesFor(MovieSortOrder.RATING, 1);
                Log.d(LOG_TAG, "called request for most highest rated");
            }
            Log.d(LOG_TAG, "most highest rated");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the gridlayout current scroll position
        if (recyclerView.getLayoutManager() != null && recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            grid_scroll_position = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            savedInstanceState.putInt(GRID_STATE, grid_scroll_position);
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * This method is to restore recyclerView grid to position it was when onSaveInstanceState was called.
     * NOTE: This is current not called even though the onSaveInstanceState is called and executed.
     * I will be looking into fixing this in Stage2.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        grid_scroll_position = savedInstanceState.getInt(GRID_STATE);
        recyclerView.smoothScrollToPosition(grid_scroll_position);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleGridRecyclerViewAdapter = new SimpleGridRecyclerViewAdapter(this, MovieContent.ITEMS, twoPane);
        recyclerView.setAdapter(simpleGridRecyclerViewAdapter);
    }

    private void fetchMoviesFor(MovieSortOrder sortOrder, int page) {
        movieRequest = new MovieRequest();

        if (sortOrder == MovieSortOrder.POPULAR) {
            movieRequest.fetchMoviesInMostPopularOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
        } else if (sortOrder == MovieSortOrder.RATING) {
            movieRequest.fetchMoviesInHighestRatingOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
        }
    }


}
