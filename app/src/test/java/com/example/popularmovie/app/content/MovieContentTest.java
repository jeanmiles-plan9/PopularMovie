package com.example.popularmovie.app.content;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 1/10/16.
 */
public class MovieContentTest extends TestCase {


    public void testItemMap() {
        assertNotNull(MovieContent.ITEM_MAP);
    }


    public void testCreateMovieVideos() throws Exception {
        MovieContent.ITEM_MAP.put("550", new MovieContent.MovieItem());
        JSONObject response = new JSONObject();
        response.put("id","550");
        response.put("runtime",139);

        JSONObject item = new JSONObject();
        item.put("key","SUXWAEX2jlg");
        item.put("site","YouTube");
        item.put("type","Trailer");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(item);

        JSONObject videos = new JSONObject();
        videos.put("results", jsonArray);

        response.put("videos", videos);

        MovieContent.createMovieVideos(response);

        assertNotNull(MovieContent.ITEM_MAP.get("550"));
        String expectedUrl = "https://www.YouTube.com/watch?v=SUXWAEX2jlg";
        assertEquals("expected " + expectedUrl, MovieContent.ITEM_MAP.get("550").trailers.get(0), expectedUrl);
        assertEquals("expected runtime to be 139", MovieContent.ITEM_MAP.get("550").runtime, 139);
    }


    public void testCreateMovieReviews() throws Exception{

        MovieContent.ITEM_MAP.put("49026", new MovieContent.MovieItem());
        JSONObject response = new JSONObject();
        response.put("id","49026");
        response.put("runtime",165);

        JSONObject review = new JSONObject();
        review.put("author", "Travis Bell");
        review.put("content", "movie needs a plot");

        JSONArray results = new JSONArray();
        results.put(review);



        JSONObject reviews = new JSONObject();
        reviews.put("results", results);
        reviews.put("id",1234);

        response.put("reviews", reviews);

        MovieContent.createMovieReviews(response);

        assertNotNull(MovieContent.ITEM_MAP.get("49026"));
        assertEquals("expected to equal author", MovieContent.ITEM_MAP.get("49026").reviews.get(0).author, "Travis Bell");
        assertEquals("expected to equal content", MovieContent.ITEM_MAP.get("49026").reviews.get(0).content, "movie needs a plot");
        assertEquals("expected runtime to be 165", MovieContent.ITEM_MAP.get("49026").runtime, 165);
    }
}