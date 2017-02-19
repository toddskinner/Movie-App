package com.example.android.moviesapp;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by toddskinner on 1/25/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private ArrayList<String[]> mMovieItems;
    private int mMovieId;

    final static String PICASSO_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/w342/";

    private final MovieAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(String[] specificMovie);
    }

    /**
     * Constructor for MovieAdapter
     *  @param clickHandler The on-click handler for this adapter. This single handler is called
     */
    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView listMovieImageView;

        public MovieViewHolder(View itemView){
            super(itemView);
            listMovieImageView = (ImageView) itemView.findViewById(R.id.iv_item_movie);
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param view The View that was clicked
         */

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String[] specificMovieDetails = mMovieItems.get(adapterPosition);
            mClickHandler.onClick(specificMovieDetails);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String[] infoForThisMovie = mMovieItems.get(position);
        String posterPath = infoForThisMovie[0];

        Uri builtUri = Uri.parse(PICASSO_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath)
                .build();

        //for debugging purposes
        System.out.println("Built uri is " + builtUri);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(holder.listMovieImageView.getContext()).load(url.toString()).into(holder.listMovieImageView);

        // for debugging
        System.out.println("Picasso called and image set at " + holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        if(mMovieItems == null){
            return 0;
        } else {
            return mMovieItems.size();
        }
    }

    public void setMovieData(ArrayList<String[]> movieData){
        mMovieItems = movieData;
        notifyDataSetChanged();
    }
}