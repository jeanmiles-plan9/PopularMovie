package com.example.popularmovie.app.content;

import com.example.popularmovie.app.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 */
public class MovieContent {

    /**
     * An array of movie items.
     */
    public static final List<MovieItem> ITEMS = new ArrayList<MovieItem>();

    /**
     * A map of movie items, by ID.
     */
    public static final Map<String, MovieItem> ITEM_MAP = new HashMap<String, MovieItem>();

    public static int LATEST_PAGE_RESULT = 1;


    private static void addItem(MovieItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.movie.id, item);
    }

    public static void createMovieItems(List<Movie> movies) {
        for (Movie movie: movies) {
            MovieItem movieItem = new MovieItem(movie);
            ITEMS.add(movieItem);
            ITEM_MAP.put(movie.id,movieItem);
        }
    }


    /**
     * A movie item representing a piece of content.
     */
    public static class MovieItem {
        public Movie movie;

        public MovieItem(Movie movie) {
            this.movie = movie;
        }

        @Override
        public String toString() {
            return movie.overview;
        }
    }
}
