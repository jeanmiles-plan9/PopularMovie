package com.example.popularmovie.app.volley;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/19/15.
 *  * This class is created similar stated in http://developer.android.com/training/volley/requestqueue.html
 */

public class VolleyManager {
    private static VolleyManager volleyManager;
    private static Context ctx;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    // add retry count to 1 for request
    private static final int REQUEST_MAX_RETRIES = 1;


    public static synchronized VolleyManager getInstance(Context context) {
        if (volleyManager == null) {
            volleyManager = new VolleyManager(context);
        }
        return volleyManager;
    }

    private VolleyManager(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(40));
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, REQUEST_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

}
