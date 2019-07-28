package com.example.android.filmesfamosos;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Andrik on 09/02/2019.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    final private ListItemClickListener mOnClickListener;
    private Context mContext;
    private Cursor mCursor;

    public interface ListItemClickListener {
        void onListItemClick(Cursor cursor,int position);
    }

    public MovieAdapter(Context context, ListItemClickListener listener) {
        mContext=context;
        mOnClickListener=listener;
    }

    public void setCursor(Cursor cursor){
        mCursor = cursor;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

            if (mCursor.moveToPosition(position)) {
                double votos = mCursor.getDouble(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_VOTOS));
                byte[] imagem = mCursor.getBlob(mCursor.getColumnIndex(MovieContract.MovieEntry.COLUNA_IMAGEM));

                holder.mVotos.setText(String.valueOf(votos));
                holder.mImage.setImageBitmap(BitmapFactory.decodeByteArray(imagem, 0, imagem.length));
            }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImage;
        TextView mVotos;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mVotos=itemView.findViewById(R.id.text_votos);
            mImage=itemView.findViewById(R.id.image_movie);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(mCursor,position);
        }
    }

}
