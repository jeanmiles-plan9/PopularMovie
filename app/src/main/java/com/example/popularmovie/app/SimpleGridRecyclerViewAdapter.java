package com.example.popularmovie.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.volley.VolleyManager;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/22/15.
 */

public class SimpleGridRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleGridRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = SimpleGridRecyclerViewAdapter.class.getSimpleName();
    private Context ctx;
    private boolean twoPane;
    public int imagePosition;
    Cursor dataCursor;

    public SimpleGridRecyclerViewAdapter(Context context, boolean twoPane) {
        this.ctx = context;
        this.twoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (dataCursor == null || dataCursor.getCount() == 0) return;
        dataCursor.moveToPosition(position);
        ImageLoader imageLoader = VolleyManager.getInstance(ctx.getApplicationContext()).getImageLoader();
        String posterUrl = MovieContent.getPosterUrl(dataCursor.getString(ItemListActivity.COL_MOVIE_POSTER));
        if (posterUrl != null || !posterUrl.isEmpty()) {
            holder.mNetworkImageView.setImageUrl(posterUrl, imageLoader);
        }
        Log.d(LOG_TAG, "POSITION:" + position +
                " MOVIE TITLE: " + dataCursor.getString(ItemListActivity.COL_MOVIE_TITLE) +
                " MOVIE POSTER: " + dataCursor.getString(ItemListActivity.COL_MOVIE_POSTER));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO:  CONSIDER MOVING SOME OF THIS CODE INTO ACTIVITY CALLBACK onItemSelected()
                if (twoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, dataCursor.getString(ItemListActivity.COL_MOVIE_ID));
                    arguments.putBoolean(ItemDetailFragment.ARG_TWOPANE, twoPane);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    AppCompatActivity activity = (ctx instanceof AppCompatActivity) ? (AppCompatActivity) ctx : null;
                    if (activity != null) {
                        activity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, dataCursor.getString(ItemListActivity.COL_MOVIE_ID));

                    context.startActivity(intent);
                }
                imagePosition = position;
            }
        });

    }

    /**
     * This section of code is for CursorAdapter, since RecyclerView has no options to
     * to extend from CursorAdapter, the methods implementation is added to support the
     * same code base as CursorAdapter.
     **/
    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return cursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    private Object getItem(int position) {
        if (dataCursor != null) {
            dataCursor.moveToPosition(position);
            return dataCursor;
        } else {
            return null;
        }
    }

    //  ViewHolder for RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NetworkImageView mNetworkImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNetworkImageView = (NetworkImageView) view.findViewById(R.id.movie_item_image);
        }
    }
}