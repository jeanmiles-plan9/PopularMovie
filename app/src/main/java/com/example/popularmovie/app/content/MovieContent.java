package com.example.popularmovie.app.content;

import android.content.ContentValues;
import android.util.Log;

import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.MovieUrlBuilder;
import com.example.popularmovie.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class for providing movie content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class MovieContent {

    public static String LOG_TAG = MovieContent.class.getSimpleName();

//    public static final List<MovieItem> ITEMS = new ArrayList<MovieItem>();
//    /**
//     * A map of movie items, by ID.
//     */
//    public static final Map<String, MovieItem> ITEM_MAP = new HashMap<String, MovieItem>();


    /*
     * Latest page result retrieved
     */
    public static int LATEST_PAGE_RESULT_POPULAR = 0;
    public static int LATEST_PAGE_RESULT_RATING = 0;
    /*
     * Set sort order of movies - default is popular
     */
    private static MovieSortOrder movieOrder = MovieSortOrder.POPULAR;
    final private static String POSTER_SIZE = "w185";
    final private static String BACKDROP_SIZE = "w92";

    // names of JSON objects
    final static String MOVIE_PAGE = "page";
    final static String MOVIE_RESULTS = "results";
    final static String MOVIE_POSTER_PATH = "poster_path";
    final static String MOVIE_OVERVIEW = "overview";
    final static String MOVIE_RELEASE_DATE = "release_date";
    final static String MOVIE_ID = "id";
    private static final String MOVIE_POPULARITY = "popularity";
    final static String MOVIE_TITLE = "title";
    final static String MOVIE_BACKDROP_PATH = "backdrop_path";
    final static String MOVIE_VOTE_AVERAGE = "vote_average";
    final static String MOVIE_VIDEOS = "videos";
    final static String MOVIE_RUNTIME = "runtime";
    final static String MOVIE_KEY = "key";
    final static String MOVIE_SITE = "site";
    static final String MOVIE_REVIEWS = "reviews";
    static final String MOVIE_AUTHOR = "author";
    static final String MOVIE_CONTENT = "content";


    /*
     * This method gets the movie sort order
     */
    public static MovieSortOrder getMovieSortOrder() {
        return movieOrder;
    }

    /*
     * This method sets the movie sort order
     */
    public static void setMovieSortOrder(MovieSortOrder sortOrder) {
        movieOrder = sortOrder;
    }

    public static ContentValues[] updateMovieTable(JSONObject movieJsonObject, MovieSortOrder sortOrder) {
        ContentValues[] contentValuesArray = null;
        try {
            // parse out the Json response into MovieItems
            JSONArray movieArray = movieJsonObject.getJSONArray(MOVIE_RESULTS);
            contentValuesArray = new ContentValues[movieArray.length()];
            // update page result so app can keep track what is the next page to retrieve next

            LATEST_PAGE_RESULT_POPULAR = sortOrder == MovieSortOrder.POPULAR ? movieJsonObject.getInt(MOVIE_PAGE) : LATEST_PAGE_RESULT_POPULAR;
            LATEST_PAGE_RESULT_RATING = sortOrder == MovieSortOrder.RATING ? movieJsonObject.getInt(MOVIE_PAGE) : LATEST_PAGE_RESULT_RATING;

            for (int index = 0; index < movieArray.length(); index++) {
                JSONObject jsonMovie = movieArray.getJSONObject(index);
                String movieId = jsonMovie.getString(MOVIE_ID);
                double popularity = jsonMovie.getDouble(MOVIE_POPULARITY);
                String title = jsonMovie.getString(MOVIE_TITLE);
                String overview = jsonMovie.getString(MOVIE_OVERVIEW);
                String releaseDate = jsonMovie.getString(MOVIE_RELEASE_DATE);
                double rating = jsonMovie.getDouble(MOVIE_VOTE_AVERAGE);
                String poster = jsonMovie.getString(MOVIE_POSTER_PATH);
                Log.d(LOG_TAG, sortOrder + " movie id is " + movieId + " movie title " + title + " poster " + poster);

                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.MovieEntry.COLUMN_ID,Integer.parseInt(movieId));
                contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
                contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, "");
                contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, poster);

                contentValuesArray[index] = contentValues;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return contentValuesArray;
    }

    public static String createPosterUrl(String path) {
        return MovieUrlBuilder.createImageUrl(path, POSTER_SIZE);
    }

    public static void updateReviewTable(JSONObject response) {
        try {
            String movieId = (response.getString(MOVIE_ID));
            if (movieId != null) {
                int runtime = response.getInt(MOVIE_RUNTIME);
                JSONObject reviews = response.getJSONObject(MOVIE_REVIEWS);
                JSONArray results = reviews.getJSONArray(MOVIE_RESULTS);
                for (int index = 0; index < results.length(); index++) {
                    JSONObject review = results.getJSONObject(index);
                    String author = review.getString(MOVIE_AUTHOR);
                    String content= review.getString(MOVIE_CONTENT);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    public static void updateVideoTable(JSONObject response) {
        try {
            String movieId = response.getString(MOVIE_ID);
            if (movieId != null) {
                int runtime = response.getInt(MOVIE_RUNTIME);
                JSONArray movieArray = response.getJSONObject(MOVIE_VIDEOS).getJSONArray(MOVIE_RESULTS);
                for (int index = 0; index < movieArray.length(); index++) {
                    JSONObject jsonMovie = movieArray.getJSONObject(index);
                    String key = jsonMovie.getString(MOVIE_KEY);
                    String site = jsonMovie.getString(MOVIE_SITE);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }
    /*
        * This method updates Movie table from JsonObject and set the sort order.  If the sort order has changed then the arraylist is
        * cleared.
        */
    public static String getPosterUrl(String poster) {
        return MovieUrlBuilder.createImageUrl(poster, POSTER_SIZE);
    }

}
