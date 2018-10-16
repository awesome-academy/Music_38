package com.framgia.vhlee.musicplus.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.Constants.Genre.GenresName;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private List<Genre> mGenres;
    private LayoutInflater mInflater;
    private GenreClickListener mGenreClickListener;

    public GenreAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mGenres = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view, mGenreClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mGenres.get(position));
    }

    @Override
    public int getItemCount() {
        return (mGenres != null) ? mGenres.size() : 0;
    }

    public void addGenre(@GenresName Genre genre) {
        mGenres.add(genre);
        notifyItemInserted(mGenres.size() - Constants.Common.INDEX_UNIT);
    }

    public void setGenreClickListener(GenreClickListener listener) {
        mGenreClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView mImageGenre;
        private TextView mTextGenre;
        private Genre mGenre;
        private GenreClickListener mListener;

        public ViewHolder(@NonNull View itemView, GenreClickListener listener) {
            super(itemView);
            mImageGenre = itemView.findViewById(R.id.image_genre);
            mTextGenre = itemView.findViewById(R.id.text_genre);
            mListener = listener;
        }

        public void bindData(final Genre genre) {
            mGenre = genre;
            mTextGenre.setText(genre.getName());
            mImageGenre.setImageResource(genre.getPhoto());
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    mListener.onItemClick(mGenre);
                    break;
            }
        }
    }

    public interface GenreClickListener {
        void onItemClick(Genre genre);
    }
}
