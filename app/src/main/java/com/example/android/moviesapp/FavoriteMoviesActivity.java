package com.example.android.moviesapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.moviesapp.data.MoviesContract.FavoriteMoviesEntry;
import com.example.android.moviesapp.data.MoviesDbHelper;

public class FavoriteMoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private MoviesDbHelper mDbHelper;
    // Identifies a particular Loader being used in this component
    private static final int FAVORITE_MOVIES_URL_LOADER = 0;

    FavoriteMoviesCursorAdapter mCursorAdapter;
    private RecyclerView mFavoriteMoviesListRecyclerView;
    TextView mErrorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);

        setTitle(R.string.favorites_title);

        mFavoriteMoviesListRecyclerView = (RecyclerView) findViewById(R.id.rv_favorite_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.favorite_movies_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mFavoriteMoviesListRecyclerView.setLayoutManager(layoutManager);
        mFavoriteMoviesListRecyclerView.setHasFixedSize(true);

        mCursorAdapter = new FavoriteMoviesCursorAdapter(this);
        mFavoriteMoviesListRecyclerView.setAdapter(mCursorAdapter);

        //kick off the loader
        getLoaderManager().initLoader(FAVORITE_MOVIES_URL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        //Define a projection that specifies the columns from the table we care about
        String[] projection = {
                FavoriteMoviesEntry._ID,
                FavoriteMoviesEntry.COLUMN_MOVIE_POSTER,
                FavoriteMoviesEntry.COLUMN_MOVIE_TITLE,
                FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW,
                FavoriteMoviesEntry.COLUMN_MOVIE_DATE,
                FavoriteMoviesEntry.COLUMN_MOVIE_VOTES,
                FavoriteMoviesEntry.COLUMN_MOVIE_ID};

        //this loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                this,   // Parent activity context
                FavoriteMoviesEntry.CONTENT_URI,        // Table to query
                projection,     // Projection to return
                null,            // No selection clause
                null,            // No selection arguments
                null             // Default sort order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.favorites, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_favorites_sort_popular:
//                URL movieSearchPopularUrl = NetworkUtils.buildPopularUrl();
//                Intent popularIntent = new Intent(FavoriteMoviesActivity.this, MainActivity.class);
//                popularIntent.putExtra("popularUrl", movieSearchPopularUrl);
//                startActivity(popularIntent);
//                return true;
//            case R.id.action_favorites_sort_top_rated:
//                URL movieSearchTopRatedUrl = NetworkUtils.buildTopRatedUrl();
//                Intent topRatedIntent = new Intent(FavoriteMoviesActivity.this, MainActivity.class);
//                topRatedIntent.putExtra("topRatedUrl", movieSearchTopRatedUrl);
//                startActivity(topRatedIntent);
//                return true;
//            case R.id.action_favorites_sort_favorites:
//                Intent intent = new Intent(this, FavoriteMoviesActivity.class);
//                startActivity(intent);
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
