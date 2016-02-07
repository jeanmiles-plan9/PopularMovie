package com.example.popularmovie.app.common;

import android.util.Log;
import android.view.View;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 2/6/16.
 */
public class ClickPlayListener implements View.OnClickListener {
    private String trailerSource;

    public ClickPlayListener(String trailerSource) {
        this.trailerSource = trailerSource;

    }

    @Override
    public void onClick(View v) {
        Log.d("playlistener", "playbutton clicked " + trailerSource);
    }
}
