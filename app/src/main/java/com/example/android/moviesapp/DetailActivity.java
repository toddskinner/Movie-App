package com.example.android.moviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    final static String PICASSO_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/w500/";

    private ImageView mDisplayPoster;
    private TextView mDisplayTitle;
    private TextView mDisplayReleaseDate;
    private TextView mDisplayVotes;
    private TextView mDisplaySummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDisplayPoster = (ImageView) findViewById(R.id.detail_page_image);
        mDisplayTitle = (TextView) findViewById(R.id.detail_page_title);
        mDisplayReleaseDate = (TextView) findViewById(R.id.detail_page_release_date);
        mDisplayVotes = (TextView) findViewById(R.id.detail_page_votes);
        mDisplaySummary = (TextView) findViewById(R.id.detail_page_summary);

        Intent detailIntent = getIntent();
        if(detailIntent != null){
            if(detailIntent.hasExtra("detailsArray")){
                String[] detailsArray = detailIntent.getStringArrayExtra("detailsArray");

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
            }
        }

    }
}