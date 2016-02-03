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
                ContentValues[] contentValues = MovieContent.updateMovieTable(response, MovieSortOrder.POPULAR);
                int inserted = 0;
                if (contentValues.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                }
                Log.d(LOG_TAG, "Movie table bulk insert completed" + contentValues.length + " Inserted " + inserted);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void fetchMoviesInHighestRatingOrder(final Context context, int page, final SimpleGridRecyclerViewAdapter movieAdapter) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUriFetchHighestRatedMovies(page), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                ContentValues[] contentValues = MovieContent.updateMovieTable(response, MovieSortOrder.RATING);
                int inserted = 0;
                if (contentValues.length > 0) {
                    inserted = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
                }
                Log.d(LOG_TAG, "Movie table bulk insert completed" + contentValues.length + " Inserted " + inserted);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    public void fetchMovieTrailers(Context context, int page, final String movieId) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUriMovieDetailsAndTrailers(movieId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MovieContent.updateReviewTable(response);
                // TODO: 1/10/16  update here to notify to update ui, how ???
                Log.d(LOG_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void fetchMovieReviews(Context context, int page, final String movieId) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MovieUrlBuilder.createUriMovieReviews(page, movieId), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MovieContent.updateVideoTable(response);
                // TODO: 1/10/16  update here to notify to update ui, how ???
                Log.d(LOG_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
