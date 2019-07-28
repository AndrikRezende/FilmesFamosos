package com.example.android.filmesfamosos;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener,LoaderManager.LoaderCallbacks<Cursor>{

    private static final String CURSOR="cursor";
    private static final int ID_INVALIDO=-1;
    private static final String ID_ITEM="id_item";

    private RecyclerView mListMovie;
    private MovieAdapter mAdapter;
    private static Cursor mCursor=null;
    private static int mIdItem=ID_INVALIDO;
    private static Bundle mSalvo=null;
    private static boolean mCreateLoader=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListMovie=findViewById(R.id.list_movie);
        mListMovie.setHasFixedSize(true);
        mListMovie.setLayoutManager(new GridLayoutManager(this,calculateBestSpanCount(400),1,false));
        mAdapter = new MovieAdapter(MainActivity.this,this);
        getLoaderManager().initLoader(0,null,this);

        if(mSalvo==null) {
            if (isOnline()) {
                URL url = NetworkUtils.buildUrl(NetworkUtils.BASE_URL + NetworkUtils.ORDER_POPULAR + NetworkUtils.API);
                new QueryMovies().execute(url);
            } else {
                if (mCursor != null && mCursor.getCount() > 0) {
                    mAdapter.setCursor(mCursor);
                    mListMovie.setAdapter(mAdapter);
                } else
                    Toast.makeText(getBaseContext(), getString(R.string.lista_vazio), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(CURSOR,mCursor.getExtras());
        outState.putInt(ID_ITEM,mIdItem);
        mSalvo=outState;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCursor.respond(savedInstanceState.getBundle(CURSOR));
        mIdItem=savedInstanceState.getInt(ID_ITEM);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mCreateLoader=true;
        if (mSalvo != null)
            mIdItem=mSalvo.getInt(ID_ITEM);
        switch (mIdItem){
            case R.id.menu_favoritos:
                return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,MovieContract.MovieEntry.COLUNA_FAVORITO+"=?",new String[]{"2"},null);
            case R.id.menu_mais_votados:
                return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,null,null,MovieContract.MovieEntry.COLUNA_VOTOS+" DESC");
            default:
                //R.id.menu_popular
                return new CursorLoader(this, MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,null,null,null);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mCreateLoader) {
            mCursor=data;
            mAdapter.setCursor(data);
            mListMovie.setAdapter(mAdapter);
        } else {
            if (mSalvo != null) {
                mCursor.respond(mSalvo.getBundle(CURSOR));
                mAdapter.setCursor(mCursor);
                    mListMovie.setAdapter(mAdapter);
            }
        }
        mCreateLoader = false;
    }

    @Override
    public void onListItemClick(Cursor cursor, int position) {
        mCursor=cursor;
        Intent intent = new Intent(MainActivity.this, DetalheMovieActivity.class);
        if(cursor.moveToPosition(position)) {
            intent.putExtra("id", cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID)));
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Cursor cursor =null;
        int itemThatWasClickedId = item.getItemId();
        mIdItem=itemThatWasClickedId;
        switch (itemThatWasClickedId){
            case R.id.menu_favoritos:
                cursor =getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, MovieContract.MovieEntry.COLUMNS,MovieContract.MovieEntry.COLUNA_FAVORITO+"=?",new String[]{"2"},null);
                break;
            case R.id.menu_mais_votados:
                cursor =getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,null,null,MovieContract.MovieEntry.COLUNA_VOTOS+" DESC");
                break;
            default:
                //R.id.menu_popular
                cursor =getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,null,null,null);
                break;
        }
        mCursor=cursor;
        mAdapter.setCursor(cursor);
        mListMovie.setAdapter(mAdapter);
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private int calculateBestSpanCount(int posterWidth) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / posterWidth);
    }

    public class QueryMovies extends AsyncTask<URL,Void,ArrayList<Movie>>{

        @Override
        protected ArrayList<Movie> doInBackground(URL... params) {
            URL searchUrl = params[0];
            ArrayList<Movie> movies=null;
            try {

                String resultado=NetworkUtils.getResponseFromHttpUrl(searchUrl);
                movies = extractFromJson(resultado);
                for(int i=0;i<movies.size();i++) {
                    URL urlImagem = NetworkUtils.buildUrl(NetworkUtils.BASE_URL_IMAGE + NetworkUtils.LENGTH_IMAGE + movies.get(i).getNomeImagem());
                    Bitmap imageResults=NetworkUtils.getImageFromHttpUrl(urlImagem);
                    movies.get(i).setImagem(imageResults);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> searchResults) {//Cursor
            if(searchResults!=null){
                insertData(searchResults);
                Cursor cursor =getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,MovieContract.MovieEntry.COLUMNS,null,null,null);
                mCursor=cursor;
                mAdapter.setCursor(cursor);
                mListMovie.setAdapter(mAdapter);
            }

        }

        private ArrayList<Movie>extractFromJson (String stringJson){
            ArrayList<Movie> movies = new ArrayList<Movie>();
            try {
                JSONObject filmeJson = new JSONObject(stringJson);
                JSONArray filmes = filmeJson.getJSONArray("results");
                for(int i=0;i<filmes.length();i++){
                    JSONObject filme = filmes.getJSONObject(i);
                    double votos=filme.getDouble("vote_average");
                    String nomeImagem=filme.getString("poster_path");
                    String titulo=filme.getString("original_title");
                    String data=filme.getString("release_date");
                    String sinopse=filme.getString("overview");
                    int id=filme.getInt("id");
                    movies.add(new Movie(titulo,data,nomeImagem,votos,sinopse,id));
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return movies;
        }

        private void insertData(ArrayList<Movie> listMovie){
            String [] projection = {
                    MovieContract.MovieEntry._ID
            };
            Cursor cursor =getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,projection,null,null,null);
            boolean estaVazio=!cursor.moveToFirst();
            List<ContentValues> listContent= new ArrayList<ContentValues>();
            for(int i=0;i<listMovie.size();i++){
                ContentValues cv = new ContentValues();
                Movie movie=listMovie.get(i);
                cv.put(MovieContract.MovieEntry.COLUNA_TITULO,movie.getTituloOriginal());
                cv.put(MovieContract.MovieEntry.COLUNA_DATA_LANCAMENTO,movie.getDataLancamento());
                cv.put(MovieContract.MovieEntry.COLUNA_SINOPSE,movie.getSinopseEnredo());
                    if(estaVazio)
                        cv.put(MovieContract.MovieEntry.COLUNA_FAVORITO,1);
                cv.put(MovieContract.MovieEntry.COLUNA_VOTOS,movie.getVotos());
                cv.put(MovieContract.MovieEntry.COLUNA_ID_FILME,movie.getIdFilme());
                Bitmap imagem=movie.getImagem();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG,0,outputStream);
                cv.put(MovieContract.MovieEntry.COLUNA_IMAGEM,outputStream.toByteArray());
                listContent.add(cv);
            }
            try {
                if(estaVazio){
                    for (int i = 0; i < listContent.size(); i++) {
                        getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, listContent.get(i));
                    }
                }
                else {
                    int id=cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    for (int i = 0; i < listContent.size(); i++) {
                        getContentResolver().update(MovieContract.MovieEntry.buildTermUriWithId(id+i),listContent.get(i),null,null);
                    }
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }

        }

    }

}
