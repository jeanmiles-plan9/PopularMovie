package com.example.popularmovie.app;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.common.MovieConstant;
import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.NetworkValidation;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.content.MovieDetail;
import com.example.popularmovie.app.data.MovieContract;
import com.example.popularmovie.app.volley.MovieRequest;
import com.example.popularmovie.app.volley.VolleyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = ItemDetailFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final int DETAIL_LOADER = 0;

    public static final String ARG_TWOPANE = "twopane";
    public static final String ARG_DETAIL_URI = "contentUri";
    public static final String ARG_MOVIE_ID = "movieId";
    private static final String FAVORITE_ON = "on";
    private static final String FAVORITE_OFF = "off";
    public static final String ARG_SORTORDER = "sortOrder";
    public static final String VOLLEY_MOVIE_DETAIL_EVENT = "volley_movie_detail_event";
    public static final String VOLLEY_MOVIE_DATA = "volley_movie_data";
    public static final String VOLLEY_TRAILER_DATA = "volley_trailer_data";
    public static final String VOLLEY_REVIEW_DATA = "volley_review_data";
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;
    private boolean twoPane;
    private Uri detailUri;
    private MovieRequest movieRequest;
    private String movieId;
    private MovieSortOrder movieSortOrder;
    private ContentValues contentValuesMovie;
    private ContentValues[] contentValuesTrailers;
    private ContentValues[] contentValuesReviews;
    private MovieDetail movieDetail;

    // UI FIELDS
    private NetworkImageView posterImageView;
    private TextView releaseDateView;
    private TextView titleView;
    private TextView overviewView;
    private TextView ratingView;
    private TextView runtimeView;
    private ImageView favorite;
    private String imageSavePath;

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
            movieId = getArguments().getString(ARG_MOVIE_ID);
            detailUri = getArguments().getParcelable(ARG_DETAIL_URI);
            twoPane = getArguments().getBoolean(ARG_TWOPANE);
            movieSortOrder = (MovieSortOrder) getArguments().get(ARG_SORTORDER);
        } else {
            movieId = getActivity().getIntent().getStringExtra(ARG_MOVIE_ID);
            detailUri = getActivity().getIntent().getData();
            movieSortOrder = (MovieSortOrder) getActivity().getIntent().getSerializableExtra(ARG_SORTORDER);
        }

        // always retrieve movie from theMovieDb if movie selected from list is not a favorite.
        // Not assuming it is in the Database.
        if (movieSortOrder != MovieSortOrder.FAVORITE) {
            fetchReviewsVideosForMovie(movieId);
        }
    }


    private BroadcastReceiver volleyReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // if data exist for this movie then it exist in database do not override, need the favorite value.
            contentValuesMovie = (ContentValues) intent.getParcelableExtra(VOLLEY_MOVIE_DATA);
            if (movieDetail == null || (movieDetail != null &&
                    movieDetail.movieId != ((Integer) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_ID)).intValue())) {
                contentValuesTrailers = (ContentValues[]) intent.getParcelableArrayExtra(VOLLEY_TRAILER_DATA);
                contentValuesReviews = (ContentValues[]) intent.getParcelableArrayExtra(VOLLEY_REVIEW_DATA);
                movieDetail = new MovieDetail(contentValuesMovie, contentValuesTrailers, contentValuesReviews);
                updateView(movieDetail);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);

        posterImageView = (NetworkImageView) rootView.findViewById(R.id.movie_item_image);
        titleView = ((TextView) rootView.findViewById(R.id.movie_title));
        releaseDateView = ((TextView) rootView.findViewById(R.id.movie_item_release_year));
        ratingView = ((TextView) rootView.findViewById(R.id.movie_item_rating));
        overviewView = ((TextView) rootView.findViewById(R.id.movie_item_overview));
        runtimeView = ((TextView) rootView.findViewById(R.id.movie_item_running_time));
        favorite = (ImageView) rootView.findViewById(R.id.movie_item_favorite);
        final String favoriteTag = (String) favorite.getTag();
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add update DB field on movie table as favorite
                // indicator switch the value here (toggle) DB 1 is true  0 is false

                if (movieDetail.favorite == 0) {
                    contentValuesMovie.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
                    Uri uri = getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                            contentValuesMovie);
                    if (contentValuesTrailers != null && contentValuesTrailers.length > 0) {
                        int count = getContext().getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, contentValuesTrailers);
                    }
                    if (contentValuesReviews != null && contentValuesReviews.length > 0) {
                        int count = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValuesReviews);
                    }
                    saveToInternalStorage(((BitmapDrawable) favorite.getDrawable()).getBitmap());
                    Log.d(LOG_TAG, "favorite insert db, insert uri " + uri);
                } else {
                    int count = getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry.COLUMN_ID + "= ?",
                            new String[]{movieId});
                    // movie is deleted from Db so reload UI from MovieDetail object with favorite off
                    movieDetail.favorite = 0;
                    updateView(movieDetail);
                    Log.d(LOG_TAG, "favorite delete db, delete count " + count);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        callbacks = this;
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(volleyReciever,
                new IntentFilter(VOLLEY_MOVIE_DETAIL_EVENT));
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(volleyReciever);
        super.onPause();
    }

    // LoaderManager methods (init and callbacks methods below)
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
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            movieDetail = new MovieDetail(data);
            updateView(movieDetail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    // Volley request to theMovieDB
    private void fetchReviewsVideosForMovie(String movieId) {
        if (NetworkValidation.isNetworkAvailable(getContext())) {
            movieRequest.fetchMovieVideosReviews(getContext(), movieId);
        }
    }

    // Update UI from MovieDetail.  MovieDetail is created either by DB or JSON data.
    private void updateView(MovieDetail movieDetail) {
        ImageLoader imageLoader = VolleyManager.getInstance(getActivity().getApplicationContext()).getImageLoader();

        String favoriteTag = movieDetail.favorite == 0 ? FAVORITE_OFF : FAVORITE_ON;
        if (NetworkValidation.isNetworkAvailable(getContext())) {
            posterImageView.setImageUrl(movieDetail.posterUrl, imageLoader);
        } else {
            if (imageSavePath != null) {
                loadImageFromStorage(imageSavePath);
            }
        }
        titleView.setText(movieDetail.title);
        runtimeView.setText(movieDetail.runtime);
        ratingView.setText(movieDetail.rating);
        releaseDateView.setText(movieDetail.releaseDate);
        overviewView.setText(movieDetail.overview);
        int imageSource = favoriteTag.equals(FAVORITE_OFF) ? R.drawable.star_outline : R.drawable.star_filled;
        favorite.setImageResource(imageSource);
        favorite.setTag(favoriteTag);

        LayoutInflater inflater = (LayoutInflater) this.getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout trailersLayout = (LinearLayout) getActivity().findViewById(R.id.list_trailers_section);
        LinearLayout reviewLayout = (LinearLayout) getActivity().findViewById(R.id.list_review_section);
        if (getActivity().findViewById(R.id.trailer_title) == null) {
            // if trailers is not empty put in a section header
            if (!movieDetail.trailers.isEmpty()) {
                View section = inflater.inflate(R.layout.list_detail_section_item, null);
                TextView title = (TextView) section.findViewById(R.id.list_detail_section_title);
                title.setText(R.string.trailer_header);
                trailersLayout.addView(section);
            }
            for (MovieDetail.Trailer trailer : movieDetail.trailers) {
                if (trailer.source != null) {
                    View v = inflater.inflate(R.layout.list_detail_trailer_item, null);
                    TextView textView = (TextView) v.findViewById(R.id.trailer_title);
                    textView.setText(trailer.name);
                    ImageView imagePlay = (ImageView) v.findViewById(R.id.play_button);
                    String trailerSource = MovieContent.createTrailerUrl(trailer.source);
                    imagePlay.setOnClickListener(new ClickPlayListener(getContext(), trailerSource));
                    trailersLayout.addView(v);
                }

                if (!movieDetail.reviews.isEmpty()) {

                    View section = inflater.inflate(R.layout.list_detail_section_item, null);
                    TextView title = (TextView) section.findViewById(R.id.list_detail_section_title);
                    title.setText(R.string.review_header);
                    reviewLayout.addView(section);
                }
                for (MovieDetail.Review review : movieDetail.reviews) {
                    View v = inflater.inflate(R.layout.list_detail_review_item, null);
                    TextView author = (TextView) v.findViewById(R.id.list_detail_author);
                    author.setText(review.author);
                    TextView content = (TextView) v.findViewById(R.id.list_detail_review);
                    content.setText(review.content);
                    reviewLayout.addView(v);
                }
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "storing bitmap image " + e.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "error closing saving bitmap file " + e.getMessage());
                return null;
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            posterImageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "retrieving stored bitmap image " + e.getMessage());
        }

    }
}
