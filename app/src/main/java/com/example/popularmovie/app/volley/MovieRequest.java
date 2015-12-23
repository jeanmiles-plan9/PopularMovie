package com.example.popularmovie.app.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovie.app.SimpleGridRecyclerViewAdapter;
import com.example.popularmovie.app.common.RequestUtility;
import com.example.popularmovie.app.content.MovieContent;

import org.json.JSONObject;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/20/15.
 */
public class MovieRequest {

    private static final String LOG_TAG = MovieRequest.class.getSimpleName();

    public void fetchPopularMovies(Context context, int page, final SimpleGridRecyclerViewAdapter movieAdapter) {

         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RequestUtility.createUriFetchPopularMovies(page), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    MovieContent.createMovieItems(response);
                    movieAdapter.notifyDataSetChanged();
                    Log.d(LOG_TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG,"network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}