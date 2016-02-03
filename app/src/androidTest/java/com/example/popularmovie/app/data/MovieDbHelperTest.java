package com.example.popularmovie.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.popularmovie.app.utils.TestUtilities;

import junit.framework.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/12/16.
 */
public class MovieDbHelperTest extends AndroidTestCase {
    public static final String LOG_TAG = MovieDbHelperTest.class.getSimpleName();
    public Context ctx;


    void deleteTheDatabase() {
        ctx.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }


    public void setUp() throws Exception {
        super.setUp();
        ctx = mContext;
        deleteTheDatabase();

    }

    public void testOnCreate() throws Exception {
        deleteTheDatabase();

        SQLiteDatabase db = new MovieDbHelper(ctx).getWritableDatabase();
        Assert.assertEquals(true, db.isOpen());

        assertTrue("Error expected table to be created.", tableCreated(db));

        Cursor c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final Set<String>  movieColumnSet = new HashSet<String>();
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_ID);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_POSTER);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_RATING);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnSet.add(MovieContract.MovieEntry.COLUMN_RUNTIME);

        int columnIndex = c.getColumnIndex("name");
        int columnIndexCid = c.getColumnIndex("cid");
        do {
            String columnName = c.getString(columnIndex);
            int columnNameIndex = c.getInt(columnIndexCid);
            Log.d(LOG_TAG, columnName + " at index " + columnNameIndex);
            movieColumnSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required columns names ", movieColumnSet.isEmpty());

        c.close();
        db.close();
    }

    public void testOnUpgrade() throws Exception {
        MovieDbHelper dbHelper = new MovieDbHelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(db, 1, 2);
        assertTrue("Error:  expected table to exist", tableCreated(db));
     }

    public void testInsertSelectUpdateDeleteMovieValues(){
        MovieDbHelper dbHelper = new MovieDbHelper(ctx);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = 550l;
        ContentValues testValues = TestUtilities.createMovieValues(movieRowId);
        long insertRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        assertTrue("Error:  Failure to insert movie values", insertRowId == movieRowId);

        Cursor c = db.rawQuery("SELECT * FROM movie where " + MovieContract.MovieEntry.COLUMN_ID + "=" + movieRowId, null);
        TestUtilities.validateCursor(" on insert row into movie table.", c, testValues);

        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "new Title");
        int rows = db.update(MovieContract.MovieEntry.TABLE_NAME,testValues, MovieContract.MovieEntry.COLUMN_ID+"="+movieRowId, null);

        c = db.rawQuery("SELECT * FROM movie where " + MovieContract.MovieEntry.COLUMN_ID + "=" + movieRowId, null);
        TestUtilities.validateCursor(" on update row into movie table.", c, testValues);

        rows = db.delete(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.COLUMN_ID + "=" + movieRowId, null);
        assertTrue("Error:  Failure to delete movie --> " + rows, rows > 0);

        db.close();
    }


    private boolean tableCreated(SQLiteDatabase db) {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE TYPE='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
            Log.d(LOG_TAG, c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without the movie table",
                tableNameHashSet.isEmpty());
        c.close();
        return true;
    }
}



