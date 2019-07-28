package com.example.android.filmesfamosos;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieContentProvider extends ContentProvider {

    private MovieDbHelper mMovieDbHelper;

    public static final int CODE_MOVIES = 100;
    public static final int CODE_MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", CODE_MOVIES_WITH_ID);
        //uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#/#", CODE_MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(Uri uri,  ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CODE_MOVIES :
                long id = db.insert(MovieContract.MovieEntry.TABELA_NOME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        int tasksUpdated=0;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksUpdated=db.update(MovieContract.MovieEntry.TABELA_NOME,values,"_id=?",new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        if (tasksUpdated != 0) {
            getContext().getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);
        }

        return tasksUpdated;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int tasksDeleted=0;

        switch (match) {
            case CODE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(MovieContract.MovieEntry.TABELA_NOME, "_id=?", new String[]{id});
                break;
            default:
                tasksDeleted = db.delete(MovieContract.MovieEntry.TABELA_NOME, null, null);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return tasksDeleted;
    }

    @Override
    public Cursor query(Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CODE_MOVIES:
                retCursor =  db.query(MovieContract.MovieEntry.TABELA_NOME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor=db.query(MovieContract.MovieEntry.TABELA_NOME,projection, "_id=?", new String[]{id},null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
