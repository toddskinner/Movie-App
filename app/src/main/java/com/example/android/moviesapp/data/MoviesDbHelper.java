package com.example.android.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.moviesapp.data.MoviesContract.FavoriteMoviesEntry;

/**
 * Created by toddskinner on 2/13/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "favoritemovies.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " (" +
                    FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_POSTER + " TEXT," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_TITLE + " TEXT," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_DATE + " TEXT," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_VOTES + " REAL," +
                    FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER" + ")";

    public MoviesDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
