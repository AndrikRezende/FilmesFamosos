package com.example.android.filmesfamosos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABELA_NOME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUNA_TITULO + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUNA_DATA_LANCAMENTO + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUNA_IMAGEM + " BLOB NOT NULL," +
                MovieContract.MovieEntry.COLUNA_FAVORITO + " INTEGER NOT NULL DEFAULT 0, " +
                MovieContract.MovieEntry.COLUNA_SINOPSE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUNA_ID_FILME + " INTEGER NOT NULL DEFAULT 0, " +
                MovieContract.MovieEntry.COLUNA_VOTOS+ " INTEGER NOT NULL DEFAULT 0" +
                "); ";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABELA_NOME);
        onCreate(db);
    }
}
