package com.example.popularmovie.app.model;

import com.example.popularmovie.app.common.RequestUtility;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/20/15.
 */
public class Movie {

    final private static String POSTER_SIZE = "w500";
    final private static String THUMBNAIL_SIZE = "w92";


    public String id;
    public String title;
    public int rating;
    public String releaseDate;
    public String overview;
    private String backdropUrl;
    private String posterUrl;

    public String getBackdropUrl() {
        return backdropUrl;
    }

    public String getPosterUrl() {
        return posterUrl;
    }


    public Movie() {
    }


    public void setBackdropUrl(String path) {
        backdropUrl = RequestUtility.createImageUrl(path, THUMBNAIL_SIZE);
    }


    public void setPosterUrl(String path) {
        posterUrl = RequestUtility.createImageUrl(path, POSTER_SIZE);
    }
}
