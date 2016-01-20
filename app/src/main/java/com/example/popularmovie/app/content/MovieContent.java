package com.example.popularmovie.app.content;

import android.util.Log;

import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.MovieUrlBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing movie content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class MovieContent {

    public static String LOG_TAG = MovieContent.class.getSimpleName();

    public static final List<MovieItem> ITEMS = new ArrayList<MovieItem>();
    /**
     * A map of movie items, by ID.
     */
    public static final Map<String, MovieItem> ITEM_MAP = new HashMap<String, MovieItem>();

    /*
     * Latest page result retrieved
     */
    public static int LATEST_PAGE_RESULT = 1;

    /*
     * Set sort order of movies - default is popular
     */
    private static MovieSortOrder movieOrder = MovieSortOrder.POPULAR;

    // names of JSON objects
    final static String MOVIE_PAGE = "page";
    final static String MOVIE_RESULTS = "results";
    final static String MOVIE_POSTER_PATH = "poster_path";
    final static String MOVIE_OVERVIEW = "overview";
    final static String MOVIE_RELEASE_DATE = "release_date";
    final static String MOVIE_ID = "id";
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
     * This method create MovieItems from JsonObject and set the sort order.  If the sort order has changed then the arraylist is
     * cleared.
     */
    public static void createMovieItems(JSONObject movieJsonObject, MovieSortOrder sortOrder) {
        try {
            // if different sort order is return from request then clear array before loading movieitems in
            if (movieOrder != sortOrder) {
                clearMovies();
            }
            // parse out the Json response into MovieItems
            JSONArray movieArray = movieJsonObject.getJSONArray(MOVIE_RESULTS);
            LATEST_PAGE_RESULT = movieJsonObject.getInt(MOVIE_PAGE);
            for (int index = 0; index < movieArray.length(); index++) {
                MovieItem movieItem = new MovieItem();
                JSONObject jsonMovie = movieArray.getJSONObject(index);
                movieItem.id = jsonMovie.getString(MOVIE_ID);
                movieItem.title = jsonMovie.getString(MOVIE_TITLE);
                movieItem.overview = jsonMovie.getString(MOVIE_OVERVIEW);
                movieItem.releaseDate = jsonMovie.getString(MOVIE_RELEASE_DATE);
                movieItem.rating = jsonMovie.getDouble(MOVIE_VOTE_AVERAGE);
                movieItem.setPosterUrl(jsonMovie.getString(MOVIE_POSTER_PATH));
                ITEMS.add(movieItem);
                ITEM_MAP.put(movieItem.id, movieItem);
                Log.d(LOG_TAG, "movie id is " + movieItem.id + " movie title " + movieItem.title);
            }
            setMovieSortOrder(sortOrder);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /*
     * This method clears the arraylist
     */
    public static void clearMovies() {
        ITEM_MAP.clear();
    }

    /*
     * This method sets the movie sort order
     */
    public static void setMovieSortOrder(MovieSortOrder sortOrder) {
        movieOrder = sortOrder;
    }

    /*
     * This method gets the movie sort order
     */
    public static MovieSortOrder getMovieSortOrder() {
        return movieOrder;
    }

    public static void createMovieReviews(JSONObject response) {
        try {
            MovieItem item = ITEM_MAP.get(response.getString(MOVIE_ID));
            if (item != null) {
                item.runtime = response.getInt(MOVIE_RUNTIME);
                JSONObject reviews = response.getJSONObject(MOVIE_REVIEWS);
                JSONArray results = reviews.getJSONArray(MOVIE_RESULTS);
                for (int index = 0; index < results.length(); index++) {
                    JSONObject review = results.getJSONObject(index);
                    item.addReview(review.getString(MOVIE_AUTHOR), review.getString(MOVIE_CONTENT));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    public static void createMovieVideos(JSONObject response) {
        try {
            MovieItem item = ITEM_MAP.get(response.getString(MOVIE_ID));
            if (item != null) {
                item.runtime = response.getInt(MOVIE_RUNTIME);
                JSONArray movieArray = response.getJSONObject(MOVIE_VIDEOS).getJSONArray(MOVIE_RESULTS);
                for (int index = 0; index < movieArray.length(); index++) {
                    JSONObject jsonMovie = movieArray.getJSONObject(index);
                    item.addTrailer(jsonMovie.getString(MOVIE_KEY), jsonMovie.getString(MOVIE_SITE));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    /**
     * A movie item representing a piece of content.
     */
    public static class MovieItem {
        final private static String POSTER_SIZE = "w185";
        final private static String BACKDROP_SIZE = "w92";

        public String id;
        public String title;
        public double rating;
        public String releaseDate;
        public String overview;
        public int runtime;  /* in minutes */
        public List<String> trailers;
        public List<Review> reviews;
        public String poster;
        private String posterUrl;


        public MovieItem() {
        }

        public String getReleaseYear() {
            String year = null;
            if (releaseDate != null) {
                String[] tokens = releaseDate.split("-");
                year = tokens.length > 0 ? tokens[0] : "";
            }
            return year;
        }

        public String getPosterUrl() {
            return posterUrl;
        }


        public void setPosterUrl(String path) {
            posterUrl = MovieUrlBuilder.createImageUrl(path, POSTER_SIZE);
        }

        public void addTrailer(String key, String site) {
            if (trailers == null) {
                trailers = new ArrayList<String>();
            }
            trailers.add("https://www." + site + ".com/watch?v=" + key);
        }

        public void addReview(String author, String content) {
            if (reviews == null) {
                reviews = new ArrayList<Review>();
            }
            Review review = new Review(author, content);
            reviews.add(review);
        }
    }
}
