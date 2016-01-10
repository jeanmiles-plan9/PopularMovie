package com.example.popularmovie.app.common;

import android.net.Uri;
import android.util.Log;

import com.example.popularmovie.app.BuildConfig;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/21/15.
 */
/*
 * This class builds Url for theMovieDB API
 */
public class MovieUrlBuilder {
    private static final String LOG_TAG = MovieUrlBuilder.class.getSimpleName();

    /*
     *  text for building theMovieDB API url
     */
    private final static String MOVIEDB_BASE_URL_HTTPS_SCHEME = "https";
    private final static String MOVIEDB_BASE_URL_HTTP_SCHEME = "http";
    private final static String MOVIEDB_BASE_URL_AUTHORITY = "api.themoviedb.org";
    private final static String MOVIEDB_BASE_URL_IMAGE_AUTHORITY = "image.tmdb.org";
    private final static String MOVIEDB_VERSION = "3";
    private final static String MOVIEDB_IMAGE_PATH_T = "t";
    private final static String MOVIEDB_IMAGE_PATH_P = "p";
    private final static String MOVIEDB_PATH_MOVIE = "movie";
    private final static String MOVIEDB_PATH_DISCOVER = "discover";
    private final static String MOVIEDB_QUERY_PAGE = "page";
    private static final String MOVIEDB_PATH_REVIEWS = "reviews";
    private static final String MOVIEDB_PATH_VIDEOS = "videos" ;

    /* categories keywords */
    private final static String MOVIEDB_SORT_BY = "sort_by";
    private final static String MOVIEDB_QUERY_SORT_BY_POPULARITY_DESC = "popularity.desc";
    private final static String MOVIEDB_QUERY_SORT_BY_VOTE_AVERAGE_DESC = "vote_average.desc";
    private final static String MOVIEDB_QUERY_APPEND_TO_RESPONSE = "append_to_response";

    /* api key */
    private final static String MOVIEDB_QUERY_API_KEY = BuildConfig.THE_MOVIEDB_API_KEY;
    private final static String MOVIEDB_API_KEY = "api_key";


    /*
     * This method creates a Url for fetching most popular movie in descending order by page
     */
    public static String createUrlFetchMostPopularMovies(int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIEDB_BASE_URL_HTTPS_SCHEME);
        builder.authority(MOVIEDB_BASE_URL_AUTHORITY);
        builder.appendPath(MOVIEDB_VERSION);
        builder.appendPath(MOVIEDB_PATH_DISCOVER);
        builder.appendPath(MOVIEDB_PATH_MOVIE);
        builder.appendQueryParameter(MOVIEDB_API_KEY, MOVIEDB_QUERY_API_KEY);
        builder.appendQueryParameter(MOVIEDB_SORT_BY, MOVIEDB_QUERY_SORT_BY_POPULARITY_DESC);
        builder.appendQueryParameter(MOVIEDB_QUERY_PAGE, String.valueOf(page));
        Log.d(LOG_TAG, builder.toString());
        return builder.toString();
    }


    /*
     * This method creates a Url for fetching highest rated movie in descending order by page
     */
    public static String createUriFetchHighestRatedMovies(int page) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIEDB_BASE_URL_HTTPS_SCHEME);
        builder.authority(MOVIEDB_BASE_URL_AUTHORITY);
        builder.appendPath(MOVIEDB_VERSION);
        builder.appendPath(MOVIEDB_PATH_DISCOVER);
        builder.appendPath(MOVIEDB_PATH_MOVIE);
        builder.appendQueryParameter(MOVIEDB_API_KEY, MOVIEDB_QUERY_API_KEY);
        builder.appendQueryParameter(MOVIEDB_SORT_BY, MOVIEDB_QUERY_SORT_BY_VOTE_AVERAGE_DESC);
        builder.appendQueryParameter(MOVIEDB_QUERY_PAGE, String.valueOf(page));
        Log.d(LOG_TAG, builder.toString());
        return builder.toString();
    }

    /*
     * This method creates a Url for fetching trailers for a movie
    */
    public static String createUriMovieDetailsAndTrailers(String movieId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIEDB_BASE_URL_HTTPS_SCHEME);
        builder.authority(MOVIEDB_BASE_URL_AUTHORITY);
        builder.appendPath(MOVIEDB_VERSION);
        builder.appendPath(MOVIEDB_PATH_MOVIE);
        builder.appendPath(movieId);
        builder.appendQueryParameter(MOVIEDB_API_KEY, MOVIEDB_QUERY_API_KEY);
        builder.appendQueryParameter(MOVIEDB_QUERY_APPEND_TO_RESPONSE,MOVIEDB_PATH_VIDEOS);
        Log.d(LOG_TAG, builder.toString());
        return builder.toString();
    }


    /*
     * This method creates a Url for fetching reviews for a movie
    */
    public static String createUriMovieReviews(int page, String movieId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIEDB_BASE_URL_HTTPS_SCHEME);
        builder.authority(MOVIEDB_BASE_URL_AUTHORITY);
        builder.appendPath(MOVIEDB_VERSION);
        builder.appendPath(MOVIEDB_PATH_MOVIE);
        builder.appendPath(movieId);
        builder.appendPath(MOVIEDB_PATH_REVIEWS);
        builder.appendQueryParameter(MOVIEDB_API_KEY, MOVIEDB_QUERY_API_KEY);
        Log.d(LOG_TAG, builder.toString());
        return builder.toString();
    }

    /*
     * This method creates a Url for fetching image for particular size
     */
    public static String createImageUrl(String path, String size) {
        path = path.replaceFirst("\\/", "");
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(MOVIEDB_BASE_URL_HTTP_SCHEME);
        builder.authority(MOVIEDB_BASE_URL_IMAGE_AUTHORITY);
        builder.appendPath(MOVIEDB_IMAGE_PATH_T);
        builder.appendPath(MOVIEDB_IMAGE_PATH_P);
        builder.appendPath(size);
        builder.appendPath(path);
        return builder.toString();
    }
}
