package com.example.android.moviesapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by toddskinner on 1/24/17.
 */

public class OpenMovieJsonUtils {
    public static final String OM_RESULTS = "results";

    public static ArrayList<String[]> getSimpleMovieStringsFromJson(Context context, String moviesJsonStr)
            throws JSONException, ParseException {

        /* Movie information. Each movie's info is an element of the "results" array */

        final String OM_POSTER = "poster_path";
        final String OM_OVERVIEW = "overview";
        final String OM_TITLE = "title";
        final String OM_RELEASE_DATE = "release_date";
        final String OM_VOTE_AVG = "vote_average";
        final String OM_ID = "id";


        /* String array to hold each movie's info String */


        JSONObject forecastJson = new JSONObject(moviesJsonStr);

        JSONArray movieListArray = forecastJson.getJSONArray(OM_RESULTS);

        ArrayList<String[]> parsedMovieData = new ArrayList<String[]>();

        for (int i = 0; i < movieListArray.length(); i++) {
            String poster;
            String overview;
            String title;
            String dateString;
            int id;
            double votes;

            /* Get the JSON object representing the movie */
            JSONObject specificMovieInfo = movieListArray.getJSONObject(i);
            overview = specificMovieInfo.getString(OM_OVERVIEW);
            poster = specificMovieInfo.getString(OM_POSTER);
            id = specificMovieInfo.getInt(OM_ID);
            title = specificMovieInfo.getString(OM_TITLE);
            votes = specificMovieInfo.getDouble(OM_VOTE_AVG);
            dateString = specificMovieInfo.getString(OM_RELEASE_DATE);

            // reformat Date string --> code snippet found on StackOverflow
            // http://stackoverflow.com/questions/9277747/android-simpledateformat-how-to-use-it
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date date = fmt.parse(dateString);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM dd, yyyy");
            String formattedDate = fmtOut.format(date);

            String[] specificMovieData = new String[7];
            specificMovieData[0] = poster;
            specificMovieData[1] = title;
            specificMovieData[2] = overview;
            specificMovieData[3] = formattedDate;
            specificMovieData[4] = Double.valueOf(votes).toString();
            specificMovieData[5] = Integer.valueOf(id).toString();

            parsedMovieData.add(specificMovieData);
        }

        return parsedMovieData;
    }

    public static String getMovieTrailerFromJson(Context context, String trailerJsonStr)
            throws JSONException, ParseException {

        final String OM_TRAILER_KEY = "key";

        JSONObject forecastJson = new JSONObject(trailerJsonStr);
        JSONArray trailerListArray = forecastJson.getJSONArray(OM_RESULTS);
        JSONObject firstTrailerInfo = trailerListArray.getJSONObject(0);
        String trailerKey = firstTrailerInfo.getString(OM_TRAILER_KEY);
        return trailerKey;
    }
}