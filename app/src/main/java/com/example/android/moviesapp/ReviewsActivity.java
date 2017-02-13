package com.example.android.moviesapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {

    private ReviewsAdapter mReviewsAdapter;
    private RecyclerView mReviewsListRecyclerView;
    TextView mReviewsErrorMessageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mReviewsListRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        mReviewsErrorMessageTextView = (TextView) findViewById(R.id.reviews_error_message_display);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mReviewsListRecyclerView.setLayoutManager(layoutManager);

        mReviewsAdapter = new ReviewsAdapter();
        mReviewsListRecyclerView.setAdapter(mReviewsAdapter);

        Intent detailIntent = getIntent();
        if(detailIntent != null) {
            if (detailIntent.hasExtra("detailsArray")) {
                String[] detailsArray = detailIntent.getStringArrayExtra("detailsArray");
                String specificMovieId = detailsArray[5];
                URL movieReviewsUrl = NetworkUtils.buildSpecificMovieReviewsUrl(specificMovieId);
                getReviewsData(movieReviewsUrl);
            }
        }
    }


    private void getReviewsData(URL reviewsSearchUrl){
        new ReviewQueryTask().execute(reviewsSearchUrl);
    }

    public class ReviewQueryTask extends AsyncTask<URL, Void, ArrayList<String[]>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String[]> doInBackground(URL... params) {
            if (params.length == 0) {
                return null;
            }
            URL mReviewsSearchUrl = params[0];
            try {
                String reviewsSearchResults = NetworkUtils.getResponseFromHttpUrl(mReviewsSearchUrl);
                ArrayList<String[]> readableJsonMovieData = OpenMovieJsonUtils.getSimpleReviewsStringsFromJson(ReviewsActivity.this, reviewsSearchResults);
                return readableJsonMovieData;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> reviewsData) {
            if (reviewsData != null && reviewsData.size() > 0) {
                showReviewJsonDataView();
                mReviewsAdapter.setReviewsData(reviewsData);
            } else {
                showReviewErrorMessage();
            }
        }
    }

    private void showReviewJsonDataView(){
        mReviewsErrorMessageTextView.setVisibility(View.INVISIBLE);
        mReviewsListRecyclerView.setVisibility(View.VISIBLE);
    }

    //method to show the error and hide the data
    private void showReviewErrorMessage(){
        mReviewsErrorMessageTextView.setVisibility(View.VISIBLE);
        mReviewsListRecyclerView.setVisibility(View.INVISIBLE);
    }
}
