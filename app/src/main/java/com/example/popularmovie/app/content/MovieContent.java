package com.example.popularmovie.app.content;

import android.content.ContentValues;
import android.util.Log;

import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.MovieUrlBuilder;
import com.example.popularmovie.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    final static String MOVIE_POPULARITY = "popularity";
    final static String MOVIE_TITLE = "title";
    final static String MOVIE_VOTE_AVERAGE = "vote_average";
    final static String MOVIE_VIDEOS = "videos";
    final static String MOVIE_RUNTIME = "runtime";
    final static String MOVIE_KEY = "key";
    final static String MOVIE_SITE = "site";
    static final String MOVIE_REVIEWS = "reviews";
    static final String MOVIE_AUTHOR = "author";
    static final String MOVIE_CONTENT = "content";
    static final String MOVIE_NAME = "name";
    static final String MOVIE_SIZE = "size";
    static final String MOVIE_TYPE = "type";


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

    public static ContentValues[] insertMovieTable(JSONObject movieJsonObject, MovieSortOrder sortOrder) {
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
                contentValues.put(MovieContract.MovieEntry.COLUMN_ID, Integer.parseInt(movieId));
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

    public static String buildPosterUrl(String path) {
        return MovieUrlBuilder.createImageUrl(path, POSTER_SIZE);
    }

    public static String getReleaseYearFromDate(String releaseDate) {
        String year = null;
        if (releaseDate != null) {
            String[] tokens = releaseDate.split("-");
            year = tokens.length > 0 ? tokens[0] : "";
        }
        return year;
    }

    public static ContentValues[] insertReviewTable(JSONObject response) {
        ContentValues[] contentValues = null;
        try {
            String movieId = (response.getString(MOVIE_ID));
            if (movieId != null && response.has(MOVIE_REVIEWS)) {
                JSONObject reviewObject = response.getJSONObject(MOVIE_REVIEWS);
                JSONArray reviewArray = reviewObject.getJSONArray(MOVIE_RESULTS);
                contentValues = new ContentValues[reviewArray.length()];
                for (int index = 0; index < reviewArray.length(); index++) {
                    JSONObject review = reviewArray.getJSONObject(index);
                    int id = review.getInt(MOVIE_ID);
                    String author = review.getString(MOVIE_AUTHOR);
                    String content = review.getString(MOVIE_CONTENT);

                    ContentValues reviewContentValue = new ContentValues();
                    reviewContentValue.put(MovieContract.ReviewEntry.COLUMN_ID, id);
                    reviewContentValue.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                    reviewContentValue.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
                    reviewContentValue.put(MovieContract.ReviewEntry.COLUMN_REVIEW, content);
                    contentValues[index] = reviewContentValue;
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return contentValues;
    }

    public static ContentValues[] insertVideoTable(JSONObject response) {
        ContentValues[] contentValues = null;
        try {
            String movieId = (response.getString(MOVIE_ID));
            if (movieId != null && response.has(MOVIE_VIDEOS)) {
                JSONObject videoObject = response.getJSONObject(MOVIE_VIDEOS);
                JSONArray videoArray = videoObject.getJSONArray(MOVIE_RESULTS);
                List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
                for (int index = 0; index < videoArray.length(); index++) {
                    JSONObject jsonMovie = videoArray.getJSONObject(index);
                    String id = (response.getString(MOVIE_ID));
                    String key = jsonMovie.getString(MOVIE_KEY);
                    String name = jsonMovie.getString(MOVIE_NAME);
                    String site = jsonMovie.getString(MOVIE_SITE);
                    int size = jsonMovie.getInt(MOVIE_SIZE);
                    String type = jsonMovie.getString(MOVIE_TYPE);

                    // save only the top 5 trailers that is on YouTube site
                    if (type.equals("Trailer") && site.equals("YouTube") && index < 5) {
                        ContentValues videoContentValues = new ContentValues();
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_ID, id);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_KEY, key);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_NAME, name);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_SITE, site);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_SIZE, size);
                        videoContentValues.put(MovieContract.VideoEntry.COLUMN_TYPE, type);
                        contentValuesList.add(videoContentValues);
                    }
                }
                contentValues = contentValuesList.toArray(new ContentValues[contentValuesList.size()]);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return contentValues;
    }

    public static ContentValues updateMovieTable(JSONObject response) {
        ContentValues contentValues = new ContentValues();
        try {
            String movieId = (response.getString(MOVIE_ID));
            if (movieId != null) {
                int runtime = response.getInt(MOVIE_RUNTIME);
                contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, runtime);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return contentValues;
    }

    /*
        * This method updates Movie table from JsonObject and set the sort order.  If the sort order has changed then the arraylist is
        * cleared.
        */
    public static String getPosterUrl(String poster) {
        return MovieUrlBuilder.createImageUrl(poster, POSTER_SIZE);
    }

    public static String createTrailerUrl(String key) {
        return "https://www.YouTube.com/watch?v=" + key;
    }


}
