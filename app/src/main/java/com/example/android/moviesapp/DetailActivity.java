package com.example.android.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.data.MoviesContract;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    final static String PICASSO_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/w500/";
    final String YOUTUBE = "http://www.youtube.com/watch?v=";

    private ImageView mDisplayPoster;
    private TextView mDisplayTitle;
    private TextView mDisplayReleaseDate;
    private TextView mDisplayVotes;
    private TextView mDisplaySummary;
    CollapsingToolbarLayout collapsingToolbarLayout;
    URL mTrailerSearchUrl;
    String mTrailerString;
    String[] detailsArray;
    CheckBox favoriteButton;
    String cursorMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDisplayPoster = (ImageView) findViewById(R.id.detail_page_image);
        mDisplayTitle = (TextView) findViewById(R.id.detail_page_title);
        mDisplayReleaseDate = (TextView) findViewById(R.id.detail_page_release_date);
        mDisplayVotes = (TextView) findViewById(R.id.detail_page_votes);
        mDisplaySummary = (TextView) findViewById(R.id.detail_page_summary);
        favoriteButton = (CheckBox) findViewById(R.id.favorite_button);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        Intent detailIntent = getIntent();
        if(detailIntent != null){

            if(detailIntent.hasExtra("detailsArray")){
                detailsArray = detailIntent.getStringArrayExtra("detailsArray");

                String posterPath = detailsArray[0];

                Uri builtUri = Uri.parse(PICASSO_IMAGE_BASE_URL).buildUpon()
                        .appendEncodedPath(posterPath)
                        .build();
                URL url = null;
                try {
                    url = new URL(builtUri.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Picasso.with(this).load(url.toString()).into(mDisplayPoster);

                mDisplayTitle.setText(detailsArray[1]);
                mDisplaySummary.setText(detailsArray[2]);
                mDisplayReleaseDate.setText(detailsArray[3]);
                mDisplayVotes.setText(detailsArray[4]);

                String specificMovieId = detailsArray[5];
                System.out.println("This specific movie id is " + specificMovieId);
                String mSelection = MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{specificMovieId};


                Uri idQueryUri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI
                        .buildUpon().appendQueryParameter(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, specificMovieId).build();


                Cursor checkFavoritesCursor = getContentResolver().query(
                        idQueryUri,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null);

                if(checkFavoritesCursor != null && checkFavoritesCursor.moveToFirst()) {
                    cursorMovieId = checkFavoritesCursor.getString(checkFavoritesCursor.getColumnIndex(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID));
                    checkFavoritesCursor.close();
                    System.out.println("Cursor returned " + cursorMovieId);
                }

                if(detailIntent.hasExtra("favoritesCheck")) {
                    if (detailIntent.getBooleanExtra("favoritesCheck", true)) {
                        favoriteButton.toggle();
                    }
                } else if (cursorMovieId != null && cursorMovieId.equals(specificMovieId)){
                    favoriteButton.toggle();
                }

                URL movieTrailersUrl = NetworkUtils.buildSpecificMovieTrailerUrl(specificMovieId);
                getTrailerData(movieTrailersUrl);
            }
        }
    }

    private void getTrailerData(URL trailerSearchUrl) {
        new TrailerQueryTask().execute(trailerSearchUrl);
    }

    public void playTrailer(View view){
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerString));
        startActivity(trailerIntent);
    }

    public void getReviews(View view){
        Intent reviewsIntent = new Intent(DetailActivity.this, ReviewsActivity.class);
        reviewsIntent.putExtra("detailsArray", detailsArray);
        startActivity(reviewsIntent);
    }

    public class TrailerQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }
            mTrailerSearchUrl = params[0];
            try {
                String trailerSearchResultsJSON = NetworkUtils.getResponseFromHttpUrl(mTrailerSearchUrl);
                String movieTrailerData = OpenMovieJsonUtils.getMovieTrailerFromJson(DetailActivity.this, trailerSearchResultsJSON);
                return movieTrailerData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String trailerData) {
            if (trailerData != null) {
                mTrailerString = YOUTUBE + trailerData;
            }
            //need else
        }
    }

    public void addToRemoveFromFavorites(View view){
        if(favoriteButton.isChecked()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_POSTER, detailsArray[0]);
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_TITLE, detailsArray[1]);
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW, detailsArray[2]);
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_DATE, detailsArray[3]);
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_VOTES, Double.valueOf(detailsArray[4]));
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, Integer.valueOf(detailsArray[5]));
            contentValues.put(MoviesContract.FavoriteMoviesEntry.COLUMN_TRAILER_STRING, mTrailerString);
            Uri uri = getContentResolver().insert(MoviesContract.FavoriteMoviesEntry.CONTENT_URI, contentValues);
            if (uri != null) {
                System.out.println("Saved " + uri.toString());
            }
        }
        if(!favoriteButton.isChecked()){
            String movieId = detailsArray[5];
            int rowsDeleted = getContentResolver().delete(MoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                    MoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=" + movieId, null);
            if (rowsDeleted > 0) {
                System.out.println("Deleted " + detailsArray[5]);
            } else {
                System.out.println("Delete failed");
            }
        }

    }
    
}