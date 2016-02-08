package com.example.popularmovie.app.content;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.popularmovie.app.common.MovieConstant;
import com.example.popularmovie.app.data.MovieContract;

import java.util.ArrayList;
import java.util.List;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 2/7/16.
 */
public class MovieDetail {


    public int movieId;
    public String posterUrl;
    public String title;
    public String runtime;
    public String rating;
    public String releaseDate;
    public String overview;
    public int favorite;
    public String poster;
    public List<Trailer> trailers;
    public List<Review> reviews;

    public MovieDetail(Cursor data) {
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
        createMovieDetailFromCursor(data);
    }


    public MovieDetail(ContentValues contentValuesMovie, ContentValues[] contentValuesTrailers, ContentValues[] contentValuesReviews) {
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
        createMovieDetailFromContentValuesMovie(contentValuesMovie);
        createMovieDetailFromContentValueTrailers(contentValuesTrailers);
        createMovieDetailFromContentValueReviews(contentValuesReviews);
    }



    private void createMovieDetailFromContentValuesMovie(ContentValues contentValuesMovie) {
        if (contentValuesMovie == null ) {
            return;
        }
        movieId = ((Integer) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_ID)).intValue();
        title = (String) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_TITLE);
        poster = (String) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_POSTER);
        posterUrl = MovieContent.buildPosterUrl(poster);
        runtime = String.valueOf(contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_RUNTIME)) + "min";
        rating = String.valueOf(contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_RATING)) + "/10";
        releaseDate = MovieContent.getReleaseYearFromDate((String) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        overview = ((String) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        favorite = (int) contentValuesMovie.get(MovieContract.MovieEntry.COLUMN_FAVORITE);
    }

    private void createMovieDetailFromContentValueTrailers(ContentValues[] contentValuesTrailers) {
        if (contentValuesTrailers == null) {
            return;
        }
        for (int i = 0; i < contentValuesTrailers.length; i++) {
            Trailer trailer = new Trailer();
            trailer.name = (String) contentValuesTrailers[i].get(MovieContract.VideoEntry.COLUMN_NAME);
            trailer.source = (String) contentValuesTrailers[i].get(MovieContract.VideoEntry.COLUMN_KEY);
            trailers.add(trailer);
        }
    }

    private void createMovieDetailFromContentValueReviews(ContentValues[] contentValuesReviews) {
        if (contentValuesReviews == null) {
            return;
        }
        for (int i = 0; i < contentValuesReviews.length; i++) {
            Review review = new Review();
            review.author = (String) contentValuesReviews[i].get(MovieContract.ReviewEntry.COLUMN_AUTHOR);
            review.content = (String) contentValuesReviews[i].get(MovieContract.ReviewEntry.COLUMN_REVIEW);
            reviews.add(review);
        }
    }


    public void createMovieDetailFromCursor(Cursor data) {
        movieId = data.getInt(MovieConstant.COL_MOVIE_ID);
        title = data.getString(MovieConstant.COL_MOVIE_TITLE);
        poster = data.getString(MovieConstant.COL_MOVIE_POSTER);
        posterUrl = MovieContent.buildPosterUrl(poster);
        runtime = data.getString(MovieConstant.COL_MOVIE_RUNTIME) + "min";
        rating = String.valueOf(data.getDouble(MovieConstant.COL_MOVIE_RATING)) + "/10";
        releaseDate = MovieContent.getReleaseYearFromDate(data.getString(MovieConstant.COL_MOVIE_RELEASE_DATE));
        overview = data.getString(MovieConstant.COL_MOVIE_OVERVIEW);
        favorite = data.getInt(MovieConstant.COL_MOVIE_FAVORITE);


        for (int i = 0; i < data.getCount(); i++, data.moveToNext()) {

            if (data.getString(MovieConstant.COL_VIDEO_KEY) != null) {
                Trailer trailer = new Trailer();
                trailer.name = data.getString(MovieConstant.COL_VIDEO_NAME);
                trailer.source = MovieContent.createTrailerUrl(data.getString(MovieConstant.COL_VIDEO_KEY));
                trailers.add(trailer);
            }

            if (data.getString(MovieConstant.COL_REVIEW_AUTHOR) != null) {
                Review review = new Review();
                review.author = data.getString(MovieConstant.COL_REVIEW_AUTHOR);
                review.content = data.getString(MovieConstant.COL_REVIEW_CONTENT);
                reviews.add(review);
            }
        }
    }


    public class Trailer {

        public String name;
        public String source;
    }

    public class Review {

        public String author;
        public String content;
    }
}
