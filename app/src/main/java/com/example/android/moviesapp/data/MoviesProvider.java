package com.example.android.moviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.moviesapp.data.MoviesContract.FavoriteMoviesEntry;

import static com.example.android.moviesapp.data.MoviesContract.PATH_FAVORITES;

/**
 * Created by toddskinner on 2/13/17.
 */

public class MoviesProvider extends ContentProvider {

    private MoviesDbHelper mDbHelper;
    public static final String LOG_TAG = MoviesProvider.class.getSimpleName();

    // URI matcher code for the content URI for the favorite movies table */
    private static final int FAVORITE_MOVIES = 100;

    // URI matcher code for the content URI for a single favorite movie in the favoritemovies table */
    private static final int FAVORITE_MOVIES_ID = 101;

    // UriMatcher object to match a content URI to a corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, PATH_FAVORITES, FAVORITE_MOVIES);
        sUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, PATH_FAVORITES + "/#", FAVORITE_MOVIES_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE_MOVIES:
                cursor = database.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVORITE_MOVIES_ID:
                selection = FavoriteMoviesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(FavoriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case FAVORITE_MOVIES:
                return FavoriteMoviesEntry.CONTENT_LIST_TYPE;
            case FAVORITE_MOVIES_ID:
                return FavoriteMoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case FAVORITE_MOVIES:
                return insertFavoriteMovieItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for " + uri);
        }
    }

    private Uri insertFavoriteMovieItem(Uri uri, ContentValues values){

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FavoriteMoviesEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //notify all listeners that the data has changed for the inventory content URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, newRowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case FAVORITE_MOVIES_ID:
                // Delete a single row given by the ID in the URI
                selection = FavoriteMoviesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = db.delete(FavoriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case FAVORITE_MOVIES:
                return updateFavoriteMovies(uri, contentValues, selection, selectionArgs);
            case FAVORITE_MOVIES_ID:
                // Update a single row given by the ID in the URI
                selection = FavoriteMoviesEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateFavoriteMovies(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateFavoriteMovies(Uri uri, ContentValues values, String selection, String[] selectionArgs){

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(FavoriteMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
