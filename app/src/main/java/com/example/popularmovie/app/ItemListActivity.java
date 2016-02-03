package com.example.popularmovie.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.example.popularmovie.app.data.MovieContract;
import com.example.popularmovie.app.volley.MovieRequest;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ItemListActivity.class.getSimpleName();
    private static final String MAIN_TITLE = "Pop Movies";
    private static final String POSITION_STATE = "selected_position";
    private static final int MOVIE_LOADER = 0;
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_POSTER
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_POPULARITY = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_RELEASE_DATE = 3;
    static final int COL_MOVIE_POSTER = 4;
    static final int COL_MOVIE_OVERVIEW = 5;
    static final int COL_MOVIE_RATING = 6;
    static final int COL_MOVIE_RUNTIME = 7;

    private MovieRequest movieRequest;
    private int grid_scroll_position = RecyclerView.NO_POSITION;
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
                Log.d(LOG_TAG, " next page to fetch is " + page);
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
        fetchMoviesFor(MovieSortOrder.POPULAR, MovieContent.LATEST_PAGE_RESULT_POPULAR);

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_STATE)) {
            grid_scroll_position = savedInstanceState.getInt(POSITION_STATE);
        }

        callbacks = this;
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(MOVIE_LOADER, null, callbacks);
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
        Log.d(LOG_TAG, "before change sortOrder " + MovieContent.getMovieSortOrder());
        if (id == R.id.action_popular) {
            if (MovieContent.getMovieSortOrder() == MovieSortOrder.RATING) {
                MovieContent.setMovieSortOrder(MovieSortOrder.POPULAR);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER,null,callbacks);
            }
            Log.d(LOG_TAG, "most popular selected");
            return true;
        } else if (id == R.id.action_rated) {
            if (MovieContent.getMovieSortOrder() == MovieSortOrder.POPULAR) {
                MovieContent.setMovieSortOrder(MovieSortOrder.RATING);
                fetchMoviesFor(MovieSortOrder.RATING, MovieContent.LATEST_PAGE_RESULT_RATING);
                getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, callbacks);
            }
            Log.d(LOG_TAG, "most highest rated selected");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save the gridlayout current scroll position
        if (simpleGridRecyclerViewAdapter != null) {
            grid_scroll_position = simpleGridRecyclerViewAdapter.imagePosition;
            savedInstanceState.putInt(POSITION_STATE, grid_scroll_position);
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

        grid_scroll_position = savedInstanceState.getInt(POSITION_STATE);
        recyclerView.smoothScrollToPosition(grid_scroll_position);
    }


    // LoaderManager overrides
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        if (MovieContent.getMovieSortOrder() == MovieSortOrder.RATING) {
            sortOrder = MovieContract.MovieEntry.COLUMN_RATING + " DESC";
        }
        Log.d(LOG_TAG, "Cursor loader sortorder is " + sortOrder);
        Uri moviesUri = MovieContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(this,
                moviesUri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case MOVIE_LOADER: {
                simpleGridRecyclerViewAdapter.swapCursor(data);
                break;
            }
        }
        if (grid_scroll_position != RecyclerView.NO_POSITION) {
            recyclerView.smoothScrollToPosition(grid_scroll_position);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleGridRecyclerViewAdapter.swapCursor(null);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleGridRecyclerViewAdapter = new SimpleGridRecyclerViewAdapter(this, twoPane);
        recyclerView.setAdapter(simpleGridRecyclerViewAdapter);
    }

    private void fetchMoviesFor(MovieSortOrder sortOrder, int page) {
        // if page is 0 then this is first request for Popular or Rating json data  the rest of pages are
        if (page != 1) {
            page = page > 0 ? page : 1;

            Log.d(LOG_TAG, "Movie request sort order is " + sortOrder);
            if (sortOrder == MovieSortOrder.POPULAR) {
                movieRequest.fetchMoviesInMostPopularOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
            } else if (sortOrder == MovieSortOrder.RATING) {
                movieRequest.fetchMoviesInHighestRatingOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
            }
        }
    }


}
