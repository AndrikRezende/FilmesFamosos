package com.example.android.filmesfamosos;

import android.graphics.Bitmap;

/**
 * Created by Andrik on 09/02/2019.
 */

public class Movie {
    private String mTituloOriginal;
    private String mDataLancamento;
    private String mNomeImagem;
    private Bitmap mImagem;
    private double mMediaVotos;
    private String mSinopseEnredo;
    private int mIdFilme;

    public Movie(String titulo,String dataLancamento,String nomeImagem,double votos,String sinopse,int id){
        mTituloOriginal=titulo;
        mDataLancamento=dataLancamento;
        mNomeImagem=nomeImagem;
        mMediaVotos=votos;
        mImagem=null;
        mSinopseEnredo=sinopse;
        mIdFilme=id;
    }

    public String getTituloOriginal() {
        return mTituloOriginal;
    }

    public String getDataLancamento() {
        return mDataLancamento;
    }

    public String getSinopseEnredo() {
        return mSinopseEnredo;
    }

    public String getNomeImagem(){
        return mNomeImagem;
    }

    public Bitmap getImagem(){
        return mImagem;
    }

    public void setImagem(Bitmap imagem){
        mImagem=imagem;
    }

    public double getVotos(){
        return mMediaVotos;
    }

    public int getIdFilme(){
        return mIdFilme;
    }

}
