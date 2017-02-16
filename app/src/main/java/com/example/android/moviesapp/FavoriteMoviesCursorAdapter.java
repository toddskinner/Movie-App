package com.example.android.moviesapp;

import android.content.Context;
import android.content.Intent;
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
    public String[] specificMovieDetails = new String[6];
    final static String PICASSO_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/w342/";

    public FavoriteMoviesCursorAdapter(Context mContext){
        this.mContext = mContext;
    }

    private final FavoriteMoviesCursorAdapterOnClickHandler mClickHandler = new FavoriteMoviesCursorAdapterOnClickHandler() {
        @Override
        public void onClick(String[] specificMovie) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("detailsArray", specificMovie);
            intent.putExtra("favoritesCheck", true);
            mContext.startActivity(intent);
        }
    };

    public interface FavoriteMoviesCursorAdapterOnClickHandler {
        void onClick(String[] specificMovie);
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
    public class FavoriteMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView favoriteMovieImageView;

        public FavoriteMoviesViewHolder(View itemView) {
            super(itemView);
            favoriteMovieImageView = (ImageView) itemView.findViewById(R.id.iv_item_favorite_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int posterIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER);
            int titleIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE);
            int overviewIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW);
            int dateIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE);
            int voteIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES);
            int idIndex = mCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID);

            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

            Double movieVotes = mCursor.getDouble(voteIndex);
            int movieId = mCursor.getInt(idIndex);

            specificMovieDetails[0] = mCursor.getString(posterIndex);
            specificMovieDetails[1] = mCursor.getString(titleIndex);
            specificMovieDetails[2] = mCursor.getString(overviewIndex);
            specificMovieDetails[3] = mCursor.getString(dateIndex);
            specificMovieDetails[4] = Double.valueOf(movieVotes).toString();
            specificMovieDetails[5] = Integer.valueOf(movieId).toString();

            mClickHandler.onClick(specificMovieDetails);
        }
    }
}
