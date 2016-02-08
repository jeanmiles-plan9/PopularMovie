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

import com.example.popularmovie.app.common.MovieConstant;
import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.NetworkValidation;
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
    private static final String POPULAR_TITLE = "Most Popular Movies";
    private static final String RATING_TITLE = "Highest Rated Movies";
    private static final String FAVORITE_TITLE = "Favorite Movies";
    private static final String POSITION_STATE = "selected_position";
    private static final String FAVORITE_SELECTION = "favorite";
    private static final int FAVORITE_SELECTION_ARG = 1;
    private static final int MOVIE_LOADER = 0;
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;

    private MovieRequest movieRequest;
    private static int grid_scroll_position = RecyclerView.NO_POSITION;
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
            getSupportActionBar().setTitle(getActionBarTitle());
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
            gridLayoutManager.setSpanCount(3);
            twoPane = true;
        }

        setupRecyclerView(recyclerView);

        // this must happen after the adapter is created in setupRecyclerView.
        movieRequest = new MovieRequest();
        fetchMoviesFor(MovieSortOrder.POPULAR, 1);

        /*
         *  This checks to see if there is any contents in movie, if so don't call API again
         *  only time movie gets called again is app startup
         */

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_STATE)) {
            grid_scroll_position = savedInstanceState.getInt(POSITION_STATE);
        }

        // setup for LoaderManager DB calls
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
            if (MovieContent.getMovieSortOrder() != MovieSortOrder.POPULAR) {
                MovieContent.setMovieSortOrder(MovieSortOrder.POPULAR);
                Log.d(LOG_TAG, "most popular selected");
                getSupportActionBar().setTitle(getActionBarTitle());
                MovieContent.clear();
                MovieContent.LATEST_PAGE_RESULT_POPULAR = 1;
                fetchMoviesFor(MovieSortOrder.POPULAR, MovieContent.LATEST_PAGE_RESULT_POPULAR);
            }
            return true;
        } else if (id == R.id.action_rated) {
            if (MovieContent.getMovieSortOrder() != MovieSortOrder.RATING) {
                MovieContent.setMovieSortOrder(MovieSortOrder.RATING);
                Log.d(LOG_TAG, "most highest rated selected");
                getSupportActionBar().setTitle(getActionBarTitle());
                MovieContent.clear();
                MovieContent.LATEST_PAGE_RESULT_RATING = 1;
                fetchMoviesFor(MovieSortOrder.RATING, MovieContent.LATEST_PAGE_RESULT_RATING);
            }
            return true;
        } else if (id == R.id.action_favorite) {
            if (MovieContent.getMovieSortOrder() != MovieSortOrder.FAVORITE) {
                MovieContent.setMovieSortOrder(MovieSortOrder.FAVORITE);
                getSupportActionBar().setTitle(getActionBarTitle());
                fetchFavoriteMovies();
            }
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

        String selection = null;
        String[] selectionArgs = null;
        if (args != null && args.containsKey(FAVORITE_SELECTION)) {
            selection = MovieContract.MovieEntry.COLUMN_FAVORITE + " = " + args.getInt(FAVORITE_SELECTION);
        }

        Uri moviesUri = MovieContract.MovieEntry.CONTENT_URI;


        return new CursorLoader(this,
                moviesUri,
                MovieConstant.MOVIE_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (MovieContent.getMovieSortOrder() == MovieSortOrder.POPULAR || MovieContent.getMovieSortOrder()
                == MovieSortOrder.RATING) {
            return;
        }

        switch (loader.getId()) {
            case MOVIE_LOADER: {
                simpleGridRecyclerViewAdapter.swapCursor(data);
                break;
            }
        }
        if (grid_scroll_position != RecyclerView.NO_POSITION) {
            Log.d(LOG_TAG, "Loader finished position to scroll to " + grid_scroll_position);
            recyclerView.getLayoutManager().scrollToPosition(grid_scroll_position);
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleGridRecyclerViewAdapter.swapCursor(null);
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleGridRecyclerViewAdapter = new SimpleGridRecyclerViewAdapter(this, twoPane, MovieContent.MOVIE_ITEMS);
        recyclerView.setAdapter(simpleGridRecyclerViewAdapter);
    }

    // retrieve popular or highest rated movies from theMovieDB in JSON format (Volley is used to make the request)
    private void fetchMoviesFor(MovieSortOrder sortOrder, int page) {
        if (NetworkValidation.isNetworkAvailable(this)) {
            Log.d(LOG_TAG, "Movie request sort order is " + sortOrder);
            if (sortOrder == MovieSortOrder.POPULAR) {
                movieRequest.fetchMoviesInMostPopularOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
            } else if (sortOrder == MovieSortOrder.RATING) {
                movieRequest.fetchMoviesInHighestRatingOrder(getApplicationContext(), page, simpleGridRecyclerViewAdapter);
            }
        }
    }

    // retreive favorite movies from Db
    private void fetchFavoriteMovies() {
        if (NetworkValidation.isNetworkAvailable(this)) {
            Bundle args = new Bundle();
            args.putInt(FAVORITE_SELECTION, FAVORITE_SELECTION_ARG);
            getSupportLoaderManager().restartLoader(MOVIE_LOADER, args, callbacks);
        }
    }

    // this determines what the title bar should be.  Based on the Data content (Popular, Rated, Favorite)
    private String getActionBarTitle() {
        String title = "";
        switch (MovieContent.getMovieSortOrder()) {
            case POPULAR: {
                title = POPULAR_TITLE;
                break;
            }
            case RATING: {
                title = RATING_TITLE;
                break;
            }
            case FAVORITE: {
                title = FAVORITE_TITLE;
                break;
            }
        }
        return title;
    }

}
