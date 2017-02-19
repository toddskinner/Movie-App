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
    private TextView mErrorMessageTextView;
    private ArrayList<String[]> mMovieItems;
    URL mMovieSearchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesListRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_display);

        if(savedInstanceState != null){
            String queryUrl = savedInstanceState.getString(STORED_QUERY_URL);
            if(queryUrl.equals(NetworkUtils.buildPopularUrl().toString())){
                mMovieSearchUrl = NetworkUtils.buildPopularUrl();
                setTitle(R.string.popular_title);
            } else {
                mMovieSearchUrl = NetworkUtils.buildTopRatedUrl();
                setTitle(R.string.top_rated_title);
            }
        } else if (getIntent().hasExtra("topRatedUrl")){
            mMovieSearchUrl = NetworkUtils.buildTopRatedUrl();
            setTitle(R.string.top_rated_title);
        } else {
            mMovieSearchUrl = NetworkUtils.buildPopularUrl();
            setTitle(R.string.popular_title);
        }
        loadMovieData(mMovieSearchUrl);
    }

    // method that will execute AsyncTask
    private void loadMovieData(URL movieSearchUrl){
        showJsonDataView();
        new MovieQueryTask().execute(movieSearchUrl);
    }

    private void setAdapterWithFetchedData(ArrayList<String[]> movieData){
        //using these println to check when Async Task complete
        System.out.println("setAdapterWithFetchedData started");

        if (movieData != null) {

            //for debugging - check if retrieving proper data and in time prior to setting adapter
            System.out.println(movieData);
            String[] testArray = movieData.get(0);
            System.out.println(testArray[0]);
            System.out.println(testArray[1]);
            System.out.println(testArray[2]);
            System.out.println(testArray[3]);
            System.out.println(testArray[4]);
            System.out.println(testArray[5]);

            mMovieAdapter = new MovieAdapter(this);
            mMovieAdapter.setMovieData(movieData);
            mMoviesListRecyclerView.setAdapter(mMovieAdapter);

            System.out.println("setAdapter(mMovieAdapter done");

            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            mMoviesListRecyclerView.setLayoutManager(layoutManager);
            mMoviesListRecyclerView.setHasFixedSize(true);
        } else {
            showErrorMessage();
        }
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
                System.out.println("doInBackground done");
                return readableJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> movieData) {
            System.out.println("OnPostExecute started");
            setAdapterWithFetchedData(movieData);
            System.out.println("OnPostExecute done");
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
                setTitle(R.string.popular_title);
                return true;
            case R.id.action_sort_top_rated:
                URL movieSearchTopRatedUrl = NetworkUtils.buildTopRatedUrl();
                mMovieAdapter.setMovieData(null);
                loadMovieData(movieSearchTopRatedUrl);
                setTitle(R.string.top_rated_title);
                return true;
            case R.id.action_sort_favorites:
                Intent intent = new Intent(MainActivity.this, FavoriteMoviesActivity.class);
                startActivity(intent);
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