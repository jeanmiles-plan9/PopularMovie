package com.example.popularmovie.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.popularmovie.app.data.MovieContract.*;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/10/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movieDB.db";


    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE  " +
                        ReviewEntry.TABLE_NAME +
                        " (" +
                        ReviewEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                        ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                        ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL,  " +
                        " PRIMARY KEY (" + ReviewEntry.COLUMN_ID + ")," +
                        "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID +
                        ") references movie (" + MovieEntry.COLUMN_ID +
                        ") on update cascade on delete cascade);";

        Log.d(LOG_TAG, SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_VIDEO_TABLE =
                "CREATE TABLE  " +
                        VideoEntry.TABLE_NAME +
                         " (" +
                        VideoEntry.COLUMN_ID + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_KEY + " INTEGER NOT NULL, " +
                        VideoEntry.COLUMN_NAME + " TEXT NOT NULL,  " +
                        VideoEntry.COLUMN_SITE + " TEXT NOT NULL,  " +
                        VideoEntry.COLUMN_SIZE + " TEXT NOT NULL,  " +
                        VideoEntry.COLUMN_TYPE + " TEXT NOT NULL,  " +
                        " PRIMARY KEY (" + VideoEntry.COLUMN_ID + ")," +
                        "FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID +
                        ") references movie (" + MovieEntry.COLUMN_ID +
                        ") on update cascade on delete cascade);";

        Log.d(LOG_TAG, SQL_CREATE_VIDEO_TABLE);


        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE  " +
                        MovieEntry.TABLE_NAME +
                        " (" +
                        MovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                        MovieEntry.COLUMN_RUNTIME + " INTEGER NOT NULL " +
                        ");";
        Log.d(LOG_TAG, SQL_CREATE_MOVIE_TABLE);

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            Log.w(LOG_TAG, " Old database version is : "+oldVersion
                    + " New database version is:"+newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
