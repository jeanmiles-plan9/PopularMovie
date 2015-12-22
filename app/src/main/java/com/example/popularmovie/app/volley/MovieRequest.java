package com.example.popularmovie.app.volley;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovie.app.ItemListActivity;
import com.example.popularmovie.app.common.RequestUtility;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/20/15.
 */
public class MovieRequest {

    private static final String LOG_TAG = MovieRequest.class.getSimpleName();

    private List<Movie> movies;

    public void fetchPopularMovies(Context context, int page, final ItemListActivity.SimpleItemRecyclerViewAdapter movieAdapter) {

        movies = new ArrayList<Movie>();

         JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, RequestUtility.createUriFetchPopularMovies(page), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    Log.d(LOG_TAG,response.toString());
                try {
                    parseJsonObject(response, movieAdapter);
                } catch (JSONException e) {
                    Log.d(LOG_TAG, "JSON Exception thrown " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(LOG_TAG,"network error " + error.getMessage());
            }
        });

        VolleyManager.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private void parseJsonObject(JSONObject movieJsonObject, ItemListActivity.SimpleItemRecyclerViewAdapter movieAdapter) throws JSONException{
        // names of JSON objects
        final String MOVIE_PAGE = "page";
        final String MOVIE_RESULTS = "results";
        final String MOVIE_POSTER_PATH = "poster_path";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_BACKDROP_PATH = "backdrop_path";
        final String MOVIE_VOTE_AVERAGE = "vote_average";


        JSONArray movieArray = movieJsonObject.getJSONArray(MOVIE_RESULTS);

        for (int index = 0; index < movieArray.length(); index++) {
            Movie movie = new Movie();
            JSONObject jsonMovie = movieArray.getJSONObject(index);
            movie.id = jsonMovie.getString(MOVIE_ID);
            movie.title = jsonMovie.getString(MOVIE_TITLE);
            movie.overview = jsonMovie.getString(MOVIE_OVERVIEW);
            movie.releaseDate = jsonMovie.getString(MOVIE_RELEASE_DATE);
            movie.rating = jsonMovie.getInt(MOVIE_VOTE_AVERAGE);
            movie.setPosterUrl(jsonMovie.getString(MOVIE_POSTER_PATH));
            movie.setBackdropUrl(jsonMovie.getString(MOVIE_BACKDROP_PATH));
            movies.add(movie);
            Log.d(LOG_TAG, "movie id is " + movie.id + " movie title " + movie.title );
        }
        MovieContent.createMovieItems(movies);
        movieAdapter.notifyDataSetChanged();
    }


}
