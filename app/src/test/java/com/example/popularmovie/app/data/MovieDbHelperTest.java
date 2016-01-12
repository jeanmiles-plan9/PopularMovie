package com.example.popularmovie.app.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.junit.Test;

import java.util.HashSet;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/11/16.
 */
public class MovieDbHelperTest extends AndroidTestCase {
    public long insertLocation() {
        return -1L;
    }

    public static final String LOG_TAG = MovieDbHelperTest.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() throws Exception {
        deleteTheDatabase();
    }

    @Test
    public void testOnCreate() throws Exception {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteTheDatabase();

        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());



        
    }

    @Test
    public void testOnUpgrade() throws Exception {

    }
}