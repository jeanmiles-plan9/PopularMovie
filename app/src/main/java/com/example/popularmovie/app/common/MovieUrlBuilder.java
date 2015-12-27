package com.example.popularmovie.app.common;

import android.net.Uri;
import android.util.Log;

import com.example.popularmovie.app.BuildConfig;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/21/15.
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
    private final static String MOVIEDB_QUERY_SORT_BY_POPULARITY_DESC = "popularity.desc";
    private final static String MOVIEDB_QUERY_SORT_BY_VOTE_AVERAGE_DESC = "vote_average.desc";
    private final static String MOVIEDB_QUERY_API_KEY = BuildConfig.THE_MOVIEDB_API_KEY;

    private final static String MOVIEDB_API_KEY = "api_key";
    private final static String MOVIEDB_SORT_BY = "sort_by";


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
