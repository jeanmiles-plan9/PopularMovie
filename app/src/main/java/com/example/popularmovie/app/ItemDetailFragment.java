package com.example.popularmovie.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.volley.VolleyManager;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /*
     * if View is in twopane
     */
    public static final String ARG_TWOPANE = "twopane";

    /**
     * The movie content this fragment is presenting.
     */
//    private MovieContent.MovieItem mItem;
    private boolean twoPane;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    private Cursor dataCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the movie content specified by the fragment
            // arguments.
//            mItem = MovieContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            twoPane = getArguments().getBoolean(ARG_TWOPANE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        ImageLoader imageLoader = VolleyManager.getInstance(getActivity().getApplicationContext()).getImageLoader();
        if (twoPane) {
            LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.movie_item_detail);
            linearLayout.getLayoutParams().height = 400;
        }
        Object data = new Object();  // TODO: should be dataCursor
        // Show the movie content as text in a TextView.
        if (data != null) {
            ((NetworkImageView) rootView.findViewById(R.id.movie_item_image)).setImageUrl("", imageLoader);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText("title");
            ((TextView) rootView.findViewById(R.id.movie_item_release_year)).setText("12/12/2001");
            ((TextView) rootView.findViewById(R.id.movie_item_rating)).setText(4.9 + "/10");
            ((TextView) rootView.findViewById(R.id.movie_item_overview)).setText("great movie");
    }

        return rootView;
    }
}
