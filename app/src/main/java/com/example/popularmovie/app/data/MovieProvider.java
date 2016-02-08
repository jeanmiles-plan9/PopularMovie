package com.example.popularmovie.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by jeanettetakaoka-miles on 1/27/16.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private MovieDbHelper movieDbHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_REVIEW_VIDEO = 101;
    static final int REVIEW = 200;
    static final int REVIEW_WITH_MOVIEID = 201;
    static final int VIDEO = 300;
    static final int VIDEO_WITH_MOVIEID = 301;
    static final String MOVIE_ALIAS = "m";

    /*
     * This class interacts with indirectly with Activity and Fragment on table  query, insert, update and delete.
     * It also works with LoaderManager on callbacks.
     */
    private static final SQLiteQueryBuilder movieWithReviewVideoQueryBuilder;

    static {
        movieWithReviewVideoQueryBuilder = new SQLiteQueryBuilder();
        //SELECT m.id, popularity, title, releaseDate, poster, overview, rating, runtime, r.id, r.movieId, author, review, v.id, v.movieId, key, name, site, size, type FROM movie AS m INNER JOIN review AS r ON m.id = r.movieId INNER JOIN video AS v ON m.id = v.movieId WHERE (m.id = 1 )
        movieWithReviewVideoQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME +
                " AS " + MOVIE_ALIAS + " LEFT JOIN " + MovieContract.ReviewEntry.TABLE_NAME +
                " AS r ON " + MOVIE_ALIAS + "." + MovieContract.MovieEntry.COLUMN_ID +
                " = r." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
                " LEFT JOIN " + MovieContract.VideoEntry.TABLE_NAME +
                " AS v ON " + MOVIE_ALIAS + "." + MovieContract.MovieEntry.COLUMN_ID +
                " = v." + MovieContract.VideoEntry.COLUMN_MOVIE_ID);
    }


    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#/"
                + MovieContract.PATH_REVIEW + "/" + MovieContract.PATH_VIDEO, MOVIE_WITH_REVIEW_VIDEO);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO, VIDEO);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/*", REVIEW_WITH_MOVIEID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_VIDEO + "/*", VIDEO_WITH_MOVIEID);

        return uriMatcher;
    }

    //movie.id = ?
    private static final String movieIdSelection =
            MOVIE_ALIAS + "." + MovieContract.MovieEntry.COLUMN_ID + " = ? ";

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIE_WITH_REVIEW_VIDEO: {
                cursor = getMovieWithReviewVideo(uri, projection, sortOrder);
                break;
            }
            case MOVIE: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case REVIEW: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case VIDEO: {
                cursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    private Cursor getMovieWithReviewVideo(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};
        String selection = movieIdSelection;


        return movieWithReviewVideoQueryBuilder.query(movieDbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case VIDEO:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case MOVIE_WITH_REVIEW_VIDEO:
                return MovieContract.MovieEntry.CONTENT_ITEM_BASE_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                long id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(id);
                } else {
                    Log.d(LOG_TAG, "Content values that failed " + values.toString());
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case REVIEW: {
                long id = db.insertWithOnConflict(MovieContract.ReviewEntry.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case VIDEO: {
                long id = db.insertWithOnConflict(MovieContract.VideoEntry.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0) {
                    returnUri = MovieContract.VideoEntry.buildVideoUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        if (null == selection) {
            selection = "1";
        }

        switch (match) {
            case MOVIE: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VIDEO: {
                rowsDeleted = db.delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowUpdated;
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            case VIDEO: {
                rowUpdated = db.update(MovieContract.VideoEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (rowUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Log.d(LOG_TAG, "record to be inserted " + value.toString());
                        long id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            }
            case REVIEW_WITH_MOVIEID: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Log.d(LOG_TAG, "record to be inserted " + value.toString());
                        long id = db.insertWithOnConflict(MovieContract.ReviewEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case VIDEO_WITH_MOVIEID: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Log.d(LOG_TAG, "record to be inserted " + value.toString());
                        long id = db.insertWithOnConflict(MovieContract.VideoEntry.TABLE_NAME, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }

    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }
}
