package com.example.android.moviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mSearchResultsTextView;
    TextView mErrorMessageTextView;
    URL mMovieSearchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSearchResultsTextView = (TextView) findViewById(R.id.movie_search_results_json);
        mErrorMessageTextView = (TextView) findViewById(R.id.error_message_display);

        mMovieSearchUrl = NetworkUtils.buildPopularUrl();
        loadMovieData(mMovieSearchUrl);
    }

    // TODO (8) Create a method that will get the user's preferred location and execute your new AsyncTask and call it loadWeatherData
    private void loadMovieData(URL movieSearchUrl){
        new MovieQueryTask().execute(movieSearchUrl);
    }

    public class MovieQueryTask extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }
            mMovieSearchUrl = params[0];
            try {
                String movieSearchResults = NetworkUtils.getResponseFromHttpUrl(mMovieSearchUrl);
                String[] readableJsonMovieData = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(MainActivity.this, movieSearchResults);
                return readableJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
//            mProgressBar.setVisibility(View.INVISIBLE);
            if (movieData != null && !movieData.equals("")) {
                showJsonDataView();
                for (String movieString : movieData) {
                    mSearchResultsTextView.append((movieString) + "\n\n\n");
                }
            } else {
                showErrorMessage();
            }
        }
    }

    //method called to show the data and hide the error
    private void showJsonDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    //method to show the error and hide the data
    private void showErrorMessage(){
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
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
                mSearchResultsTextView.setText("");
                loadMovieData(movieSearchPopularUrl);
                return true;
            case R.id.action_sort_top_rated:
                URL movieSearchTopRatedUrl = NetworkUtils.buildTopRatedUrl();
                mSearchResultsTextView.setText("");
                loadMovieData(movieSearchTopRatedUrl);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
