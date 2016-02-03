package com.example.popularmovie.app.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.popularmovie.app.utils.TestUtilities;

/**
 * Created by jeanettetakaoka-miles on 1/27/16.
 */
public class MovieProviderTest extends AndroidTestCase {

    private static final String LOG_TAG = MovieProviderTest.class.getSimpleName();
    private static final int BULK_INSERT_COUNT = 10;

    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] listContentValues = new ContentValues[BULK_INSERT_COUNT];
        for (int i = 0; i < BULK_INSERT_COUNT; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_ID, 550 + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 50.000000 + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "SuperHero" + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "January 19, 2016");
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "/D6e8RJf2qUstnfkTslTXNTUAlT.jpg" + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "movie lacks a plot" + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, 8.5446 + i);
            contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, 139 + i);
            listContentValues[i] = contentValues;
        }
        return listContentValues;
    }

    public void testBulkInsert() {
        ContentValues[] contentValuesList = createBulkInsertMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);


        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesList);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        assertEquals(insertCount, BULK_INSERT_COUNT);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_ID + " ASC");

        assertEquals(cursor.getCount(), BULK_INSERT_COUNT);

        cursor.moveToFirst();

        for (int i = 0; i < BULK_INSERT_COUNT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord(
                    "testBulkInsert. Error validating movie entry " + i,
                    cursor, contentValuesList[i]);
        }
        cursor.close();

    }

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.VideoEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromDB();
    }

    public void testProviderRegistry() {
        PackageManager packageManager = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try {
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);
            assertEquals("Error MovieProvider registered with authority: " + providerInfo +
                    " instead of authority: " + MovieContract.CONTENT_AUTHORITY, providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MovieProvider is not registered at " + mContext.getPackageName(), false);
        }
    }


    public void testQuery() throws Exception {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        // create movie
        long movieRowId = TestUtilities.insertMovieValues(mContext);
        // create reviews
        long reviewRowId = TestUtilities.insertReviewValues(mContext);

        // create videos
        long videoRowId = TestUtilities.insertVideoValues(mContext);

        db.close();

        // create query and run it
        Cursor cursor = mContext.getContentResolver().
                query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);


        ContentValues movieValues = TestUtilities.createMovieValues(550);
        TestUtilities.validateCursor("testMovieQuery", cursor, movieValues);
    }

    public void testGetType() throws Exception {
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.ReviewEntry.CONTENT_URI);
        assertEquals("Error: ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",MovieContract.ReviewEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.VideoEntry.CONTENT_URI);
        assertEquals("Error: VideoEntry CONTENT_URI should return VideoEntry.CONTENT_TYPE",MovieContract.VideoEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieReviewVideo("550"));
        assertEquals("Error:  Movie with Reviews and Videos CONTENT ITEM URI", MovieContract.MovieEntry.CONTENT_ITEM_BASE_TYPE, type);
    }

    public void testInsert() throws Exception {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        // create movie
        ContentValues movieValues = TestUtilities.createMovieValues(550);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("test insert movie record", cursor, movieValues);



        // create reviews
        ContentValues reviewValues = TestUtilities.createReviewValues(550, 1234);
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);
        Uri reviewUri = mContext.getContentResolver().insert(
                MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long reviewRowId = ContentUris.parseId(reviewUri);
        assertTrue(reviewRowId != -1);
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        TestUtilities.validateCursor("test insert ReviewContent records",
                reviewCursor, reviewValues);

        // create videos
        ContentValues videoValues = TestUtilities.createVideoValues(550, 12345);
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.VideoEntry.CONTENT_URI, true, tco);
        Uri videoUri = mContext.getContentResolver().insert(
                MovieContract.VideoEntry.CONTENT_URI, videoValues);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        long videoRowId = ContentUris.parseId(videoUri);

        assertTrue(videoUri != null);
        Cursor videoCursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        TestUtilities.validateCursor("test insert video record",
                videoCursor, videoValues);

        movieValues.putAll(reviewValues);
        movieValues.putAll(videoValues);

        cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieReviewVideo("550"),
                null,
                null,
                null,
                null);
        TestUtilities.validateCursor("test all values on insert",
                cursor, movieValues);

        db.close();
    }

    public void testDelete() throws Exception {
        testInsert();
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        TestUtilities.TestContentObserver videoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.VideoEntry.CONTENT_URI, true, videoObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();
        videoObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
        mContext.getContentResolver().unregisterContentObserver(videoObserver);
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null);
        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null);
        mContext.getContentResolver().delete(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                null);
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertEquals("Error: Records not deleted", 0, cursor.getCount());
        cursor.close();
    }

    public void testUpdateMovie() throws Exception {
        ContentValues contentValues = TestUtilities.createMovieValues(550);

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "new row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(contentValues);
        updatedValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "new title");
        updatedValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "updated overview");

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,null,null,null,null,null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedValues, MovieContract.MovieEntry.COLUMN_ID + "= ?",
                new String[] {"550"});
        assertEquals(1, count);
        tco.waitForNotificationOrFail();
        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_ID + "=" + movieRowId,
                null,
                null);

        TestUtilities.validateCursor("test update movie", cursor, updatedValues);
    }

    public void testUpdateReview() throws Exception {
        ContentValues movieValues = TestUtilities.createMovieValues(550);

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        long movieRowId = ContentUris.parseId(movieUri);

        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "new movie row id: " + movieRowId);

        ContentValues contentValues = TestUtilities.createReviewValues(550, 1234);

        Uri reviewUri = mContext.getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, contentValues);
        long reviewRowId = ContentUris.parseId(reviewUri);

        assertTrue(reviewRowId != -1);
        Log.d(LOG_TAG, "new review row id: " + reviewRowId);

        ContentValues updatedValues = new ContentValues(contentValues);
        updatedValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "new author");
        updatedValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW, "updated review");

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,null,null,null,null,null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieContract.ReviewEntry.CONTENT_URI,
                updatedValues, MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "= ?",
                new String[] {"550"});
        assertEquals(count, 1);
        tco.waitForNotificationOrFail();
        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "=" + movieRowId,
                null,
                null);

        TestUtilities.validateCursor("test update review", cursor, updatedValues);
    }
}