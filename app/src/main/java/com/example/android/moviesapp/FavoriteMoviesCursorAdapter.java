package com.example.android.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moviesapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by toddskinner on 2/13/17.
 */

public class FavoriteMoviesCursorAdapter extends RecyclerView.Adapter<FavoriteMoviesCursorAdapter.FavoriteMoviesViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    final static String PICASSO_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/w342/";

    public FavoriteMoviesCursorAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public FavoriteMoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.favorite_movie_list_item, parent, false);

        return new FavoriteMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMoviesCursorAdapter.FavoriteMoviesViewHolder holder, int position) {
        // Indice for the ID and poster column
        int idIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry._ID);
        int posterIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER);

        mCursor.moveToPosition(position); // get to the right location in the cursor

        // Determine the values of the wanted data
        final int id = mCursor.getInt(idIndex);
        holder.itemView.setTag(id);
        String posterPath = mCursor.getString(posterIndex);
        Uri builtUri = Uri.parse(PICASSO_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(holder.favoriteMovieImageView.getContext()).load(url.toString()).into(holder.favoriteMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    // Inner class for creating ViewHolders
    class FavoriteMoviesViewHolder extends RecyclerView.ViewHolder {

        ImageView favoriteMovieImageView;

        /**
         * Constructor for the FavoriteMovieViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public FavoriteMoviesViewHolder(View itemView) {
            super(itemView);
            favoriteMovieImageView = (ImageView) itemView.findViewById(R.id.iv_item_favorite_movie);
        }
    }
}
