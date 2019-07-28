package com.example.android.filmesfamosos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetalheMovieActivity extends AppCompatActivity {

    private boolean mEstadoEstrelaClicado;
    private final int ESTRELA_SEM_CLICAR=1;
    private final int ESTRELA_CLICADO=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_movie);

        int id=getIntent().getIntExtra("id",0);
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.buildTermUriWithId(id),MovieContract.MovieEntry.COLUMNS,null,null,null);
        cursor.moveToFirst();
        int idFilme=cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_ID_FILME));
        URL url = NetworkUtils.buildUrl(NetworkUtils.BASE_URL + "/movie/"+idFilme+"/videos"+ NetworkUtils.API);
        new QueryTrailer().execute(url);
        url = NetworkUtils.buildUrl(NetworkUtils.BASE_URL + "/movie/"+idFilme+"/reviews"+ NetworkUtils.API);
        new QueryReview().execute(url);

        int favorito=cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_FAVORITO));
        mEstadoEstrelaClicado=false;
        if(favorito==ESTRELA_CLICADO){
            ImageButton imageButtonFavorito = findViewById(R.id.detalhe_favorito);
            imageButtonFavorito.setImageResource(R.drawable.ic_baseline_grade_24px);
            mEstadoEstrelaClicado=true;
        }

        TextView textViewTitulo = (TextView) findViewById(R.id.detalhe_titulo);
        textViewTitulo.setText(getString(R.string.titulo)+" "+cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_TITULO)));

        TextView textViewData = (TextView) findViewById(R.id.detalhe_data);
        textViewData.setText(getString(R.string.lancamento)+cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_DATA_LANCAMENTO)));

        TextView textViewVotos = (TextView) findViewById(R.id.detalhe_votos);
        textViewVotos.setText(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_VOTOS))+" "+getString(R.string.votos));

        TextView textViewSinopse = (TextView) findViewById(R.id.detalhe_sinopse);
        textViewSinopse.setText(getString(R.string.resumo)+" "+cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_SINOPSE)));

        ImageView imageView = (ImageView) findViewById(R.id.detalhe_imagem);
        byte[] imagem=cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_IMAGEM));
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(imagem,0,imagem.length));

    }

    public void alterarEstadoFavorito (View v){
        int id=getIntent().getIntExtra("id",0);
        ImageButton imageButtonFavorito = findViewById(R.id.detalhe_favorito);
        if(mEstadoEstrelaClicado){
            ContentValues cv = new ContentValues();
            imageButtonFavorito.setImageResource(R.drawable.ic_outline_grade_24px);
            cv.put(MovieContract.MovieEntry.COLUNA_FAVORITO,ESTRELA_SEM_CLICAR);
            getContentResolver().update(MovieContract.MovieEntry.buildTermUriWithId(id),cv,null,null);
            mEstadoEstrelaClicado=false;
        }
        else {
            ContentValues cv = new ContentValues();
            imageButtonFavorito.setImageResource(R.drawable.ic_baseline_grade_24px);
            cv.put(MovieContract.MovieEntry.COLUNA_FAVORITO,ESTRELA_CLICADO);
            getContentResolver().update(MovieContract.MovieEntry.buildTermUriWithId(id),cv,null,null);
            mEstadoEstrelaClicado=true;
        }

    }

    public class QueryTrailer extends AsyncTask<URL,Void,ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(URL... params) {
            URL searchUrl = params[0];
            ArrayList<String> list=null;
            try {

                String resultado=NetworkUtils.getResponseFromHttpUrl(searchUrl);
                list= extractFromJson(resultado);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> searchResults) {
            RadioGroup radioGroup=findViewById(R.id.detalhe_trailer);
            if(searchResults==null)
                Toast.makeText(getBaseContext(),getString(R.string.trailer_vazio),Toast.LENGTH_SHORT).show();
            else {
                for (int i = 0; i < searchResults.size(); i++) {
                    final int indice = i;
                    RadioButton radioButton = new RadioButton(getBaseContext());
                    int temp = i + 1;
                    radioButton.setText(getString(R.string.trailer) + " " + temp);
                    radioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String url = NetworkUtils.BASE_URL_TRAILER + searchResults.get(indice);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            startActivity(intent);

                        }
                    });
                    radioGroup.addView(radioButton, i);
                }
            }

        }

        private ArrayList<String>extractFromJson (String stringJson){
            ArrayList<String> trailer = new ArrayList<String>();
            try {
                JSONObject filmeJson = new JSONObject(stringJson);
                JSONArray filmes = filmeJson.getJSONArray("results");
                for(int i=0;i<filmes.length();i++){
                    JSONObject filme = filmes.getJSONObject(i);
                    String chave=filme.getString("key");
                    trailer.add(chave);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return trailer;
        }
    }

    public class QueryReview extends AsyncTask<URL,Void,ArrayList<String[]>> {
        @Override
        protected ArrayList<String[]> doInBackground(URL... params) {
            URL searchUrl = params[0];
            ArrayList<String[]> list=null;
            try {

                String resultado=NetworkUtils.getResponseFromHttpUrl(searchUrl);
                list= extractFromJson(resultado);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> searchResults) {
            TextView textView=findViewById(R.id.detalhe_review);
            String texto="";
            if(searchResults==null)
                Toast.makeText(getBaseContext(),getString(R.string.comentario_vazio),Toast.LENGTH_SHORT).show();
            else {
                for (int i = 0; i < searchResults.size(); i++) {
                    texto = texto + getString(R.string.autor) + " " + searchResults.get(i)[0] + ": " + searchResults.get(i)[1] + "\n";
                }
                textView.setText(texto);
            }
        }

        private ArrayList<String[]>extractFromJson (String stringJson){
            ArrayList<String[]> review = new ArrayList<String[]>();
            try {
                JSONObject filmeJson = new JSONObject(stringJson);
                JSONArray filmes = filmeJson.getJSONArray("results");
                for(int i=0;i<filmes.length();i++){
                    JSONObject filme = filmes.getJSONObject(i);
                    String autor=filme.getString("author");
                    String comentario=filme.getString("content");
                    String []autorComentario= new String[2];
                    autorComentario[0]=autor;
                    autorComentario[1]=comentario;
                    review.add(autorComentario);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
            return review;
        }
    }

}
