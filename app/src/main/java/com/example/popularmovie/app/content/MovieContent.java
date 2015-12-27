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

    /**
     * An array of movie items.
     */
    public static final List<MovieItem> ITEMS = new ArrayList<>();

    /**
     * A map of movie items, by ID.
     */
    public static final Map<String, MovieItem> ITEM_MAP = new HashMap<>();

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
                movieItem.setBackdropUrl(jsonMovie.getString(MOVIE_BACKDROP_PATH));
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
        ITEMS.clear();
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
        private String backdropUrl;
        private String posterUrl;

        public MovieItem() {}

        public String getReleaseYear() {
            String year = null;
            if (releaseDate != null) {
                String[] tokens = releaseDate.split("-");
                year = tokens.length > 0 ? tokens[0] : "";
            }
            return year;
        }

        public String getBackdropUrl() {
            return backdropUrl;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public void setBackdropUrl(String path) {
            backdropUrl = MovieUrlBuilder.createImageUrl(path, BACKDROP_SIZE);
        }

        public void setPosterUrl(String path) {
            posterUrl = MovieUrlBuilder.createImageUrl(path, POSTER_SIZE);
        }
    }
}
