package com.example.popularmovie.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/10/16.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.popularmovie.app";
    public static final Uri BASE_CONTENT_URL = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_VIDEO = "video";


    public static final class ReviewEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URL.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_REVIEW;

        public static final String CONTENT_ITEM_BASE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_REVIEW;


        public static final String TABLE_NAME = "review";
        // foreign key to Movie table
        public static final String COLUMN_MOVIE_ID = "movieId";
//        // this is review setting string that will be sent to theMovieDB as the movie review query
//        public static final String COLUMN_REVIEW_SETTING = "review_setting";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_REVIEW = "review";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewUriWithMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URL.buildUpon().appendPath(PATH_VIDEO).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_VIDEO;

        public static final String CONTENT_ITEM_BASE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_VIDEO;

        public static final String TABLE_NAME = "video";
        // foreign key to Movie table
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MOVIE_ID = "movieId";
//        // this is review setting string that will be sent to theMovieDB as the movie videos query
//        public static final String COLUMN_VIDEO_SETTING = "video_setting";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildVideoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildVideoUriWithMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URL.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_MOVIE;

        public static final String CONTENT_ITEM_BASE_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/"
                + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_POSTER = "poster";

        public static Uri buildMovieUri (long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieReviewVideo(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).appendPath(PATH_REVIEW).appendPath(PATH_VIDEO).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }



}
