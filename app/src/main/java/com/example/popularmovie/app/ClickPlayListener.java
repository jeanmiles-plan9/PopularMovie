package com.example.popularmovie.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 2/6/16.
 */
public class ClickPlayListener implements View.OnClickListener {
    private Context context;
    private String trailerSource;


    /*
     * This class is used to initiate YouTube in Web Browser
     */
    public ClickPlayListener(Context context,String trailerSource) {
        this.trailerSource = trailerSource;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        Log.d(ClickPlayListener.class.getSimpleName(), "playbutton clicked source is" + trailerSource);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerSource));
        context.startActivity(intent);
    }
}
