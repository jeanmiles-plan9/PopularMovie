package com.example.popularmovie.app;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

/**
 * UdaCity Android Nanodegree
 * Created by jeanmiles-plan9 on 12/22/15.
 */

public class SimpleGridRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleGridRecyclerViewAdapter.ViewHolder> {

    private static final String LOG_TAG = SimpleGridRecyclerViewAdapter.class.getSimpleName();
    private final List<MovieContent.MovieItem> mValues;
    private Context ctx;
    private boolean twoPane;

    public SimpleGridRecyclerViewAdapter(Context context, List<MovieContent.MovieItem> items, boolean twoPane) {
        this.ctx = context;
        this.twoPane = twoPane;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageLoader imageLoader = VolleyManager.getInstance(ctx.getApplicationContext()).getImageLoader();
        holder.mItem = mValues.get(position);
        holder.mNetworkImageView.setImageUrl(holder.mItem.getPosterUrl(), imageLoader);
        Log.d(LOG_TAG, "POSITION:" + position + " MOVIE TITLE: " + holder.mItem.title);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (twoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
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
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final NetworkImageView mNetworkImageView;
        public MovieContent.MovieItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNetworkImageView = (NetworkImageView) view.findViewById(R.id.movie_item_image);
        }
     }
}