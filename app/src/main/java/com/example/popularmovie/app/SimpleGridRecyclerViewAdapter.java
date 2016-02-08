package com.example.popularmovie.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.popularmovie.app.common.MovieConstant;
import com.example.popularmovie.app.common.MovieSortOrder;
import com.example.popularmovie.app.content.MovieContent;
import com.example.popularmovie.app.data.MovieContract;
import com.example.popularmovie.app.volley.VolleyManager;

import java.util.List;

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

    // data holder favorites dataCursor,  popular and highest rating list of contentvalues
    private Cursor dataCursor;
    private final List<ContentValues> movieItems;

    public SimpleGridRecyclerViewAdapter(Context context, boolean twoPane, List<ContentValues> movieItems) {
        this.ctx = context;
        this.twoPane = twoPane;
        this.movieItems = movieItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String movieId = null;
        Uri contentUri = null;
        ImageLoader imageLoader = VolleyManager.getInstance(ctx.getApplicationContext()).getImageLoader();

        if (MovieContent.getMovieSortOrder() == MovieSortOrder.FAVORITE) {
//            if (dataCursor == null || dataCursor.getCount() == 0) return;
            dataCursor.moveToPosition(position);
            String poster = dataCursor.getString(MovieConstant.COL_MOVIE_POSTER);
            String posterUrl = MovieContent.getPosterUrl(poster);
            movieId = dataCursor.getString(MovieConstant.COL_MOVIE_ID);
            contentUri = MovieContract.MovieEntry.buildMovieReviewVideo(movieId);
            // update UI
            if (poster != null || !poster.isEmpty()) {
                holder.mNetworkImageView.setImageUrl(posterUrl, imageLoader);
            }
//        Log.d(LOG_TAG, "POSITION:" + position +
//                " MOVIE TITLE: " + dataCursor.getString(MovieConstant.COL_MOVIE_TITLE) +
//                " MOVIE POSTER: " + dataCursor.getString(MovieConstant.COL_MOVIE_POSTER));
        } else {
            holder.contentValues = movieItems.get(position);
            String poster = (String) holder.contentValues.get(MovieContract.MovieEntry.COLUMN_POSTER);
            String posterUrl = MovieContent.getPosterUrl(poster);
            movieId = String.valueOf(holder.contentValues.get(MovieContract.MovieEntry.COLUMN_ID));
            contentUri = MovieContract.MovieEntry.buildMovieReviewVideo(movieId);
            // update UI
            if (poster != null || !poster.isEmpty()) {
                holder.mNetworkImageView.setImageUrl(posterUrl, imageLoader);
            }
        }


        // this is setup for either array or datacursor data.
        final String movieIdArg = movieId;
        final Uri contentUriArg = contentUri;
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, " Detail content uri " + contentUriArg.toString());
                if (twoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_MOVIE_ID, movieIdArg);
                    arguments.putSerializable(ItemDetailFragment.ARG_SORTORDER, MovieContent.getMovieSortOrder());
                    arguments.putParcelable(ItemDetailFragment.ARG_DETAIL_URI, contentUriArg);
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
                    intent.setData(contentUriArg);
                    intent.putExtra(ItemDetailFragment.ARG_MOVIE_ID, movieIdArg);
                    intent.putExtra(ItemDetailFragment.ARG_SORTORDER, MovieContent.getMovieSortOrder());
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
        int count = 0;

        switch (MovieContent.getMovieSortOrder()) {
            case POPULAR: {
                count = movieItems.size();
                break;
            }
            case RATING: {
                count = movieItems.size();
                break;
            }
            case FAVORITE: {
                count = dataCursor == null ? 0 : dataCursor.getCount();
                break;
            }
        }
        return count;
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
        public ContentValues contentValues;
        public final View mView;
        public final NetworkImageView mNetworkImageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNetworkImageView = (NetworkImageView) view.findViewById(R.id.movie_item_image);
        }
    }
}