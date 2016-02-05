package com.example.popularmovie.app.volley;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovie.app.SimpleGridRecyclerViewAdapter;
import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.MovieUrlBuilder;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.data.MovieContract;

import org.json.JSONObject;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/20/15.
 */
public class MovieRequest {

    private static final String LOG_TAG = MovieRequest.class.getSimpleName();

    public void fetchMoviesInMostPopularOrder(final Context context, int page, final SimpleGridRecyclerViewAdapter movieAdapter) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUrlFetchMostPopularMovies(page), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ContentValues[] contentValues = MovieContent.insertMovieTable(response, MovieSortOrder.POPULAR);
                int inserted = 0;
                if (contentValues.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                }
                Log.d(LOG_TAG, "Movie table bulk insert completed" + contentValues.length + " Inserted " + inserted);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error: Volley Network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void fetchMoviesInHighestRatingOrder(final Context context, int page, final SimpleGridRecyclerViewAdapter movieAdapter) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUriFetchHighestRatedMovies(page), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ContentValues[] contentValues = MovieContent.insertMovieTable(response, MovieSortOrder.RATING);
                int inserted = 0;
                if (contentValues.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                }
                Log.d(LOG_TAG, "Movie table bulk insert completed" + contentValues.length + " Inserted " + inserted);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error: Volley Network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void fetchMovieVideosReviews(final Context context, final String movieId) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUriMovieDetailsVideosAndReviews(movieId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(LOG_TAG, response.toString());

                ContentValues contentValuesMovie = MovieContent.updateMovieTable(response);
                int updated = 0;
                if (contentValuesMovie.containsKey(MovieContract.MovieEntry.COLUMN_RUNTIME)) {
                    updated = context.getContentResolver().update(
                            MovieContract.MovieEntry.CONTENT_URI,
                            contentValuesMovie, MovieContract.MovieEntry.COLUMN_ID + "= ?",
                            new String[] {movieId});
                    Log.d(LOG_TAG, "Movie table update completed" + movieId  + " updated " + updated);
                }

                int inserted = 0;
                ContentValues[] contentValuesReview = MovieContent.insertReviewTable(response);
                if (contentValuesReview != null && contentValuesReview.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.buildReviewUriWithMovieId(Long.parseLong(movieId)), contentValuesReview);
                    Log.d(LOG_TAG, "Review table bulk insert completed" + contentValuesReview.length + " Inserted " + inserted);
                }

                ContentValues[] contentValuesVideo = MovieContent.insertVideoTable(response);
                if (contentValuesVideo != null && contentValuesVideo.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.VideoEntry.buildVideoUriWithMovieId(Long.parseLong(movieId)), contentValuesVideo);
                    Log.d(LOG_TAG, "Review table bulk insert completed" + contentValuesVideo.length + " Inserted " + inserted);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error: Volley Network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

}
