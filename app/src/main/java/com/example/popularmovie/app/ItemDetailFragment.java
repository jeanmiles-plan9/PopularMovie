package com.example.popularmovie.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.common.ClickPlayListener;
import com.example.popularmovie.app.common.MovieConstant;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.volley.MovieRequest;
import com.example.popularmovie.app.volley.VolleyManager;

;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final int DETAIL_LOADER = 0;

    public static final String ARG_TWOPANE = "twopane";
    public static final String ARG_DETAIL_URI = "contentUri";
    public static final String ARG_MOVIE_ID = "movieId";
    private boolean twoPane;
    private Uri detailUri;
    private MovieRequest movieRequest;

    // UI FIELDS
    private NetworkImageView posterImageView;
    private TextView releaseDateView;
    private TextView titleView;
    private TextView overviewView;
    private TextView ratingView;
    private TextView runtimeView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieRequest = new MovieRequest();
        if (getArguments().containsKey(ARG_MOVIE_ID)) {
            // Load the movie content specified by the fragment
            // arguments.
            String movieId = getArguments().getString(ARG_MOVIE_ID);
            detailUri = getArguments().getParcelable(ARG_DETAIL_URI);
            twoPane = getArguments().getBoolean(ARG_TWOPANE);
            fetchReviewsVideosForMovie(movieId);
        } else {
            String movieId = getActivity().getIntent().getStringExtra(ARG_MOVIE_ID);
            detailUri = getActivity().getIntent().getData();
            fetchReviewsVideosForMovie(movieId);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
// TODO BUG FIX CARD WRITTEN ON THIS
//        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.movie_item_detail);
//        if (twoPane) {
//            linearLayout.getLayoutParams().height = 400;
//        } null value on params
        posterImageView = (NetworkImageView) rootView.findViewById(R.id.movie_item_image);
        titleView = ((TextView) rootView.findViewById(R.id.movie_title));
        releaseDateView = ((TextView) rootView.findViewById(R.id.movie_item_release_year));
        ratingView = ((TextView) rootView.findViewById(R.id.movie_item_rating));
        overviewView = ((TextView) rootView.findViewById(R.id.movie_item_overview));
        runtimeView = ((TextView) rootView.findViewById(R.id.movie_item_running_time));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != detailUri) {
            return new CursorLoader(
                    getActivity(),
                    detailUri,
                    MovieConstant.DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            ImageLoader imageLoader = VolleyManager.getInstance(getActivity().getApplicationContext()).getImageLoader();

            int movieId = data.getInt(MovieConstant.COL_MOVIE_ID);
            String posterUrl = MovieContent.buildPosterUrl(data.getString(MovieConstant.COL_MOVIE_POSTER));
            posterImageView.setImageUrl(posterUrl, imageLoader);
            titleView.setText(data.getString(MovieConstant.COL_MOVIE_TITLE));
            runtimeView.setText(data.getString(MovieConstant.COL_MOVIE_RUNTIME));
            ratingView.setText(data.getDouble(MovieConstant.COL_MOVIE_RATING) + "/10");
            releaseDateView.setText(
                    MovieContent.getReleaseYearFromDate(data.getString(MovieConstant.COL_MOVIE_RELEASE_DATE)));
            overviewView.setText(data.getString(MovieConstant.COL_MOVIE_OVERVIEW));
        }
        // create views with section headers, trailers and reviews
        data.moveToFirst();

        LayoutInflater inflater = (LayoutInflater) this.getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout trailersLayout = (LinearLayout) getActivity().findViewById(R.id.list_trailers_section);
        LinearLayout reviewLayout = (LinearLayout) getActivity().findViewById(R.id.list_review_section);

        for (int i = 0; i < data.getCount(); i++, data.moveToNext()) {
            if (data.getString(MovieConstant.COL_VIDEO_KEY) != null) {
                View v = inflater.inflate(R.layout.list_detail_trailer_item, null);
                TextView textView = (TextView) v.findViewById(R.id.trailer_title);
                textView.setText(data.getString(MovieConstant.COL_VIDEO_NAME));
                ImageView imagePlay = (ImageView) v.findViewById(R.id.play_button);
                String trailerSource = MovieContent.createTrailerUrl(data.getString(MovieConstant.COL_VIDEO_KEY));
                imagePlay.setOnClickListener(new ClickPlayListener(trailerSource));
                trailersLayout.addView(v);
            }
            // TODO remove commented code just testing layout
            if (data.getString(MovieConstant.COL_MOVIE_ID) != null) {
                View v = inflater.inflate(R.layout.list_detail_review_item, null);
                TextView author = (TextView) v.findViewById(R.id.list_detail_author);
                author.setText("author" + i);//data.getString(MovieConstant.COL_REVIEW_AUTHOR));
                TextView review = (TextView) v.findViewById(R.id.list_detail_review);
                review.setText("content" + i);//data.getString(MovieConstant.COL_REVIEW_CONTENT));
                reviewLayout.addView(v);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void fetchReviewsVideosForMovie(String movieId) {
        movieRequest.fetchMovieVideosReviews(getContext(), movieId);
    }
}
