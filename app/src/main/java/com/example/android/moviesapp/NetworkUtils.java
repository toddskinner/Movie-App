package com.example.android.moviesapp;

/**
 * Created by toddskinner on 1/24/17.
 */


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.moviesapp.BuildConfig.API_KEY;

/**
 * These utilities will be used to communicate with the network.
 */

public class NetworkUtils {

    final static String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie/";

    //final static String MOVIEDB_POPULAR_URL =
      //      "https://api.themoviedb.org/3/movie/popular";

//    final static String MOVIEDB_TOPRATED_URL =
  //          "https://api.themoviedb.org/3/movie/top_rated";

    final static String PARAM_API_KEY = "api_key";

    /**
     * Builds the URL used to query MovieDB for movies sorted by popularity.
     *
     * @return The URL to use to query the MovieDB server for popular movies.
     */
    public static URL buildPopularUrl() {
        String mMovieDbPopularUrl = MOVIEDB_BASE_URL + "popular";
        Uri builtUri = Uri.parse(mMovieDbPopularUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildTopRatedUrl(){
        String mMovieDbTopRatedUrl = MOVIEDB_BASE_URL + "top_rated";
        Uri builtUri = Uri.parse(mMovieDbTopRatedUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildSpecificMovieTrailerUrl(String id) {
        String mMovieDbSpecificMovieUrl = MOVIEDB_BASE_URL + id + "/videos";
        Uri builtUri = Uri.parse(mMovieDbSpecificMovieUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildSpecificMovieReviewsUrl(String id) {
        String mMovieDbSpecificMovieUrl = MOVIEDB_BASE_URL + id + "/reviews";
        Uri builtUri = Uri.parse(mMovieDbSpecificMovieUrl).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildSavedInstanceStateUrl(String storedUrl) {
        Uri builtUri = Uri.parse(storedUrl);

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
