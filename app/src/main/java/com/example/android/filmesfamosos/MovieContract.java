package com.example.android.filmesfamosos;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public final static String CONTENT_AUTHORITY = "com.example.android.filmesfamosos";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public final static String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        public final static String _ID = BaseColumns._ID;
        public static final String TABELA_NOME = "movies";
        public static final String COLUNA_TITULO = "titulo";
        public static final String COLUNA_DATA_LANCAMENTO = "dataLancamento";
        public static final String COLUNA_IMAGEM = "imagem";
        public static final String COLUNA_VOTOS = "votos";
        public static final String COLUNA_SINOPSE = "sinopse";
        public static final String COLUNA_ID_FILME = "idFilme";
        public static final String COLUNA_FAVORITO = "favorito";

        public static final String[] COLUMNS = {_ID, COLUNA_TITULO, COLUNA_DATA_LANCAMENTO, COLUNA_IMAGEM,
                COLUNA_VOTOS, COLUNA_SINOPSE, COLUNA_ID_FILME, COLUNA_FAVORITO};

        public static Uri buildTermUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
