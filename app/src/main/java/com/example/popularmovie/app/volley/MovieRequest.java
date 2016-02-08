package com.example.popularmovie.app.volley;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovie.app.ItemDetailFragment;
import com.example.popularmovie.app.SimpleGridRecyclerViewAdapter;
import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.common.MovieUrlBuilder;
import com.example.popularmovie.app.content.MovieContent;

import org.json.JSONObject;

import java.util.Arrays;

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
                ContentValues[] contentValues = MovieContent.insertMoviesTable(response, MovieSortOrder.POPULAR);
                MovieContent.addAllMovieItems(Arrays.asList(contentValues));
                movieAdapter.notifyDataSetChanged();
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
                ContentValues[] contentValues = MovieContent.insertMoviesTable(response, MovieSortOrder.RATING);
                MovieContent.addAllMovieItems(Arrays.asList(contentValues));
                movieAdapter.notifyDataSetChanged();
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

                ContentValues contentValuesMovie = MovieContent.insertMovieTable(response);
                ContentValues[] contentValuesReview = MovieContent.insertReviewTable(response);
                ContentValues[] contentValuesVideo = MovieContent.insertVideoTable(response);
                notifyUpdateUI(context, contentValuesMovie, contentValuesVideo, contentValuesReview);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Error: Volley Network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void notifyUpdateUI(Context context, ContentValues contentValuesMovie, ContentValues[] contentValuesVideo, ContentValues[] contentValuesReview) {
        Intent intent = new Intent(ItemDetailFragment.VOLLEY_MOVIE_DETAIL_EVENT);
        intent.putExtra(ItemDetailFragment.VOLLEY_MOVIE_DATA, contentValuesMovie);
        intent.putExtra(ItemDetailFragment.VOLLEY_TRAILER_DATA, contentValuesVideo);
        intent.putExtra(ItemDetailFragment.VOLLEY_REVIEW_DATA, contentValuesReview);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
