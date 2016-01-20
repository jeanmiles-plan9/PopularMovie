package com.example.popularmovie.app.volley;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.android.volley.RequestQueue;


/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/19/15.
 */

public class VolleyManagerTest extends ApplicationTestCase<Application> {



    public VolleyManagerTest() {
        super(Application.class);
    }


    public void testGetInstance() throws Exception {
        assertNotNull(VolleyManager.getInstance(getContext()));
    }

    public void testGetRequestQueue() {
        VolleyManager volleyManager = VolleyManager.getInstance(getContext());
        assertTrue(volleyManager.getRequestQueue() instanceof RequestQueue);
    }
}