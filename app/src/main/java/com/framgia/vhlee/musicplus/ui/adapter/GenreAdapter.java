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

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {
    private static final String[] GENRES_NAME = {"All music", "All Audio", "Alternativerock",
            "Ambient", "Classical", "Country"};
    private static final int[] GENRES_IMAGE = {R.drawable.default_artwork};
    private LayoutInflater mInflater;

    public GenreAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = mInflater.inflate(R.layout.item_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return GENRES_NAME.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageGenre;
        private TextView mTextGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageGenre = itemView.findViewById(R.id.image_genre);
            mTextGenre = itemView.findViewById(R.id.text_genre);
        }

        public void bindData(final int position) {
            mImageGenre.setImageResource(GENRES_IMAGE[0]);
            mTextGenre.setText(GENRES_NAME[position]);
        }
    }
}
