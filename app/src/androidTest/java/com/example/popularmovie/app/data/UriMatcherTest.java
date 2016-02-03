package com.example.popularmovie.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by jeanettetakaoka-miles on 1/27/16.
 */
public class UriMatcherTest extends AndroidTestCase {

    private static final String TEST_PATH_REVIEW = "review";
    private static final String TEST_PATH_VIDEO = "video";
    private static final String TEST_MOVIE_ID = "157336";

    private static final Uri TEST_MOVIE = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_REVIEW_MOVIES =
            MovieContract.MovieEntry.buildMovieReviewVideo(TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher uriMatcher = MovieProvider.buildUriMatcher();

        assertNotNull(uriMatcher);
        assertEquals("Error: expected Movie Discover Uri", uriMatcher.match(TEST_MOVIE), MovieProvider.MOVIE);
        assertEquals("Error: expected Movie ReviewContent Video Uri", uriMatcher.match(TEST_MOVIE_WITH_REVIEW_MOVIES), MovieProvider.MOVIE_WITH_REVIEW_VIDEO);

    }

}
