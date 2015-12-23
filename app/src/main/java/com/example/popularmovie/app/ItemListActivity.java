package com.example.popularmovie.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoPane;
    private SimpleGridRecyclerViewAdapter simpleGridRecyclerViewAdapter;
    private static final String MAIN_TITLE = "Pop Movies";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(MAIN_TITLE);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        final View recyclerView = findViewById(R.id.grid_list);
        assert recyclerView != null;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        ((RecyclerView) recyclerView).setLayoutManager(gridLayoutManager);
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true;
        }

        if (MovieContent.ITEMS.isEmpty()) {
            MovieRequest movieRequest = new MovieRequest();
            movieRequest.fetchPopularMovies(getApplicationContext(), MovieContent.LATEST_PAGE_RESULT, simpleGridRecyclerViewAdapter);
        }
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        simpleGridRecyclerViewAdapter = new SimpleGridRecyclerViewAdapter(this, MovieContent.ITEMS, twoPane);
        recyclerView.setAdapter(simpleGridRecyclerViewAdapter);
    }

}
