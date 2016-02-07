package com.example.popularmovie.app.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.popularmovie.app.data.MovieContract;
import com.example.popularmovie.app.data.MovieDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/19/16.
 */
public class TestUtilities extends AndroidTestCase {

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            boolean isString = true;
            if (entry.getValue() instanceof Double) {
                isString = false;
            }

            if (isString) {
                assertEquals("Value '" + entry.getValue().toString() +
                                "' did not match the expected value '" +
                                expectedValue + "'. " + error,
                        expectedValue,
                        valueCursor.getString(idx));
            } else {
                Double expectedDouble = Double.valueOf(expectedValue);
                assertEquals("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, expectedDouble, valueCursor.getDouble(idx));
            }

        }
    }

    public static ContentValues createMovieValues(long movieRowId) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_ID, movieRowId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 52.881698);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "SuperHero");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "January 19, 2016");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "/D6e8RJf2qUstnfkTslTXNTUAlT.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "movie lacks a plot");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8.5446);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 139);
        return movieValues;
    }

    public static long insertMovieValues(Context mContext) {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        long movieId = 550;
        ContentValues contentValues = createMovieValues(movieId);
        long insertRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);

        assertTrue("Error:  Failure to insert movie values", insertRowId == movieId);

        return insertRowId;
    }

    public static long insertReviewValues(Context mContext) {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        long reviewId = 1234;
        ContentValues contentValues = createReviewValues(550, reviewId);
        long insertRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, contentValues);

        assertTrue("Error:  Failure to insert review values", insertRowId == reviewId);

        return insertRowId;
    }

    public static ContentValues createReviewValues(long movieId, long reviewId) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieContract.ReviewEntry.COLUMN_ID, reviewId);
        contentValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "author");
        contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW, "movie is not worth the price of the ticket");

        return contentValues;
    }

    public static long insertVideoValues(Context mContext) {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        long videoId = 12345;
        String key = "aJJrkyHas78";

        ContentValues contentValues = createVideoValues(550, videoId);
        long insertRowId = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, contentValues);

        assertTrue("Error:  Failure to insert video values", insertRowId == 1);

        return insertRowId;
    }

    public static ContentValues createVideoValues(int movieId, long videoId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
        contentValues.put(MovieContract.VideoEntry.COLUMN_ID, videoId);
        contentValues.put(MovieContract.VideoEntry.COLUMN_KEY, "aJJrkyHas78");
        contentValues.put(MovieContract.VideoEntry.COLUMN_NAME, "movie title");
        contentValues.put(MovieContract.VideoEntry.COLUMN_SITE, "youTube");
        contentValues.put(MovieContract.VideoEntry.COLUMN_SIZE, "350");
        contentValues.put(MovieContract.VideoEntry.COLUMN_TYPE, "trailers");

        return contentValues;
    }

    /*
         Students: The functions we provide inside of TestProvider use this utility class to test
         the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
         CTS tests.

         Note that this only tests that the onChange function is called; it does not test that the
         correct Uri is returned.
      */
    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
