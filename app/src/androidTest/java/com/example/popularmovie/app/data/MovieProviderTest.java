package com.example.popularmovie.app.data;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

/**
 * Created by jeanettetakaoka-miles on 1/27/16.
 */
public class MovieProviderTest extends AndroidTestCase {

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

    public void testOnCreate() throws Exception {

    }

    public void testQuery() throws Exception {

    }

    public void testGetType() throws Exception {


    }

    public void testInsert() throws Exception {

    }

    public void testDelete() throws Exception {

    }

    public void testUpdate() throws Exception {

    }
}