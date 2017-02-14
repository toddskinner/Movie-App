package com.example.android.moviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.moviesapp.MovieAdapter.MovieAdapterOnClickHandler;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String STORED_QUERY_URL = "query";
    private MovieAdapter mMovieAdapter;
    private RecyclerView mMoviesListRecyclerView;

    TextView mErrorMessageTextView;
    URL mMovieSearchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesListRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mMoviesListRecyclerView.setLayoutManager(layoutManager);
        mMoviesListRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mMoviesListRecyclerView.setAdapter(mMovieAdapter);

        if(savedInstanceState == null) {
            mMovieSearchUrl = NetworkUtils.buildPopularUrl();
        } else {
            mMovieSearchUrl = NetworkUtils.buildSavedInstanceStateUrl(savedInstanceState.getString(STORED_QUERY_URL));
        }

        loadMovieData(mMovieSearchUrl);
    }

    // method that will execute AsyncTask
    private void loadMovieData(URL movieSearchUrl){
        new MovieQueryTask().execute(movieSearchUrl);
    }

    //create new function for data fetched from database

    @Override
    public void onClick(String[] specificMovie) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("detailsArray", specificMovie);
        startActivity(intent);
    }

    public class MovieQueryTask extends AsyncTask<URL, Void, ArrayList<String[]>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String[]> doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }
            mMovieSearchUrl = params[0];
            try {
                String movieSearchResults = NetworkUtils.getResponseFromHttpUrl(mMovieSearchUrl);
                ArrayList<String[]> readableJsonMovieData = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(MainActivity.this, movieSearchResults);
                return readableJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> movieData) {
            if (movieData != null) {
                showJsonDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
        }
    }

    //method called to show the data and hide the error
    private void showJsonDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMoviesListRecyclerView.setVisibility(View.VISIBLE);
    }

    //method to show the error and hide the data
    private void showErrorMessage(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mMoviesListRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_popular:
                URL movieSearchPopularUrl = NetworkUtils.buildPopularUrl();
                mMovieAdapter.setMovieData(null);
                loadMovieData(movieSearchPopularUrl);
                return true;
            case R.id.action_sort_top_rated:
                URL movieSearchTopRatedUrl = NetworkUtils.buildTopRatedUrl();
                mMovieAdapter.setMovieData(null);
                loadMovieData(movieSearchTopRatedUrl);
                return true;
            //use a different function than loadMovieData for database
            //getLoaderManager?
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String storedUrl = mMovieSearchUrl.toString();
        outState.putString(STORED_QUERY_URL, storedUrl);
    }
}