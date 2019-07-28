package com.example.android.filmesfamosos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Andrik on 08/02/2019.
 */

public class NetworkUtils {

    private static String apiChave=BuildConfig.MEU_API_MOVIE;
    final static String BASE_URL="http://api.themoviedb.org/3";
    final static String ORDER_POPULAR="/movie/popular";
    final static String ORDER_MAIS_VOTADOS="/movie/top_rated";
    final static String API="?api_key="+apiChave;
    final static String BASE_URL_IMAGE="http://image.tmdb.org/t/p/";
    final static String LENGTH_IMAGE="w342";
    final static String BASE_URL_TRAILER="https://www.youtube.com/watch?v=";

    public static URL buildUrl(String stringUrl){
        Uri builtUri = Uri.parse(stringUrl).buildUpon().build();
        URL url=null;
        try {
            url = new URL(builtUri.toString());
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

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

    public static Bitmap getImageFromHttpUrl(URL url) throws IOException {
        Bitmap imagem=null;
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            imagem=BitmapFactory.decodeStream(in);
        } finally {
            urlConnection.disconnect();
        }
        return imagem;
    }

}
