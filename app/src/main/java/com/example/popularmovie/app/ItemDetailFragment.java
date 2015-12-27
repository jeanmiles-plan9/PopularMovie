package com.example.popularmovie.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.content.MovieContent;
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

    public static final String ARG_ITEM_POSITION = "item_position";

    /**
     * The movie content this fragment is presenting.
     */
    private MovieContent.MovieItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the movie content specified by the fragment
            // arguments.
            mItem = MovieContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        ImageLoader imageLoader = VolleyManager.getInstance(getActivity().getApplicationContext()).getImageLoader();

        // Show the movie content as text in a TextView.
        if (mItem != null) {
            ((NetworkImageView) rootView.findViewById(R.id.movie_item_image)).setImageUrl(mItem.getPosterUrl(), imageLoader);
            ((TextView) rootView.findViewById(R.id.movie_title)).setText(mItem.title);
            ((TextView) rootView.findViewById(R.id.movie_item_release_year)).setText(mItem.getReleaseYear());
            ((TextView) rootView.findViewById(R.id.movie_item_rating)).setText(mItem.rating + "/10");
            ((TextView) rootView.findViewById(R.id.movie_item_overview)).setText(mItem.overview);
        }

        return rootView;
    }
}
