package com.example.popularmovie.app.common;

import com.example.popularmovie.app.data.MovieContract;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 2/3/16.
 */
public class MovieConstant {

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    public static final int COL_MOVIE_OVERVIEW = 5;
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_POPULARITY = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 3;
    public static final int COL_MOVIE_POSTER = 4;
    public static final int COL_MOVIE_RATING = 6;
    public static final int COL_MOVIE_RUNTIME = 7;
    public static final int COL_MOVIE_FAVORITE = 8;
    public static final int COL_REVIEW_AUTHOR = 11;
    public static final int COL_REVIEW_CONTENT = 12;
    public static final int COL_VIDEO_KEY = 15;
    public static final int COL_VIDEO_NAME = 16;
    public static final int COL_VIDEO_SITE = 17;
    public static final int COL_VIDEO_TYPE = 19;

    //SELECT m.id, popularity, title, releaseDate, poster, overview, rating, runtime,
    // r.id, r.movieId, author, review,
    // v.id, v.movieId, key, name, site, size, type FROM movie AS m
    // INNER JOIN review AS r ON m.id = r.movieId
    // INNER JOIN video AS v ON m.id = v.movieId WHERE (m.id = ? )
    private static final String MOVIE_ALIAS = "m";
    private static final String REVIEW_ALIAS = "r";
    private static final String VIDEO_ALIAS = "v";

    public static final String[] DETAIL_COLUMNS = {
            MOVIE_ALIAS + "." + MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RUNTIME,
            MovieContract.MovieEntry.COLUMN_FAVORITE,

            REVIEW_ALIAS + "." + MovieContract.ReviewEntry.COLUMN_ID,
            REVIEW_ALIAS + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW,

            VIDEO_ALIAS + "." + MovieContract.VideoEntry.COLUMN_ID,
            VIDEO_ALIAS + "." + MovieContract.VideoEntry.COLUMN_MOVIE_ID,
            MovieContract.VideoEntry.COLUMN_KEY,
            MovieContract.VideoEntry.COLUMN_NAME,
            MovieContract.VideoEntry.COLUMN_SITE,
            MovieContract.VideoEntry.COLUMN_SIZE,
            MovieContract.VideoEntry.COLUMN_TYPE
    };

}
