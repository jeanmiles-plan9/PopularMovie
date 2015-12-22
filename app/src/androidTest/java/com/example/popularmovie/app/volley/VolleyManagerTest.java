package com.example.popularmovie.app.volley;

import android.test.mock.MockContext;

import com.android.volley.RequestQueue;

import junit.framework.TestCase;
import org.Mockito.*;


/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/19/15.
 */

public class VolleyManagerTest extends TestCase {

    MockContext mockContext;


    public void testGetInstance() throws Exception {
        MockContext mockContext = new MockContext();
        when(mockContext.getApplicationContext())
        assertNull(VolleyManager.getInstance(mockContext));
    }

    public void testGetRequestQueue() {
        VolleyManager volleyManager = VolleyManager.getInstance(new MockContext());
        assertTrue(volleyManager.getRequestQueue() instanceof RequestQueue);
    }
}