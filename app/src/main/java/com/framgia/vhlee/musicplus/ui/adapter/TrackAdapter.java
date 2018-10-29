package com.framgia.vhlee.musicplus.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.MyViewHolder> {
    private List<Track> mTracks;
    private OnClickItemSongListener mListener;
    private OnDragDropListener mDragDropListener;
    private boolean mIsNowPlaying;
    private boolean mIsRecentTracks;
    private OnClickDeleteListener mDeleteListener;
    private boolean isFavarite;

    public TrackAdapter(OnClickItemSongListener listener) {
        mTracks = new ArrayList<>();
        mListener = listener;
    }

    public TrackAdapter(List<Track> tracks, OnClickItemSongListener listener) {
        mTracks = tracks;
        mListener = listener;
    }

    public TrackAdapter(List<Track> tracks, OnClickItemSongListener listener, OnDragDropListener dragDropListener) {
        mTracks = tracks;
        mListener = listener;
        mDragDropListener = dragDropListener;
    }

    public TrackAdapter(List<Track> tracks, OnClickItemSongListener songListener,
                        OnClickDeleteListener deleteListener) {
        mTracks = tracks;
        mDeleteListener = deleteListener;
        mListener = songListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_track, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.bindData(i, mListener);
    }


    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mTrackImage;
        private TextView mTrackName;
        private TextView mSingerName;
        private ImageView mFeature;
        private ImageView mAddNowPlaying;
        private ImageView mDeleteFavorite;
        private OnClickItemSongListener mListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTrackImage = itemView.findViewById(R.id.image_track);
            mTrackName = itemView.findViewById(R.id.text_song_name);
            mSingerName = itemView.findViewById(R.id.text_singer_name);
            mFeature = itemView.findViewById(R.id.image_feature);
            mAddNowPlaying = itemView.findViewById(R.id.image_add_now_play);
            mDeleteFavorite = itemView.findViewById(R.id.image_delete_favorite);
            if (mIsNowPlaying) mAddNowPlaying.setVisibility(View.GONE);
            if (mIsRecentTracks || mIsNowPlaying) {
                mAddNowPlaying.setVisibility(View.GONE);
                mFeature.setVisibility(View.GONE);
            }
            if (isFavarite) {
                mAddNowPlaying.setVisibility(View.GONE);
                mFeature.setVisibility(View.GONE);
                mDeleteFavorite.setVisibility(View.VISIBLE);
            }
        }

        public void bindData(final int position, final OnClickItemSongListener listener) {
            mTrackName.setText(mTracks.get(position).getTitle());
            mSingerName.setText(mTracks.get(position).getArtist());
            setImage(mTrackImage, mTracks.get(position).getArtworkUrl());
            mListener = listener;
            itemView.setOnClickListener(this);
            mFeature.setOnClickListener(this);
            mDeleteFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_feature:
                    mListener.showDialodFeatureTrack(getAdapterPosition());
                    break;
                case R.id.image_delete_favorite:
                    mDeleteListener.deleteFromFavorite(getAdapterPosition());
                    break;
                default:
                    mListener.clickItemSongListener(getAdapterPosition());
            }
        }

        public void setImage(ImageView image, String source) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.error(R.drawable.default_artwork);
            Glide.with(image.getContext())
                    .load(source)
                    .apply(requestOptions)
                    .into(image);
        }
    }

    public void updateTracks(List<Track> tracks) {
        if (tracks != null) {
            mTracks.clear();
            mTracks.addAll(tracks);
            notifyDataSetChanged();
        }
    }

    public void addTracks(List<Track> tracks) {
        if (tracks != null) {
            mTracks.addAll(tracks);
            notifyDataSetChanged();
        }
    }

    public void onMove(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(mTracks, i, i + Constants.INDEX_UNIT);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(mTracks, i, i - Constants.INDEX_UNIT);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
        mDragDropListener.onDropViewHolder(mTracks);
    }

    public void swipe(int position, int direction) {
        if (mTracks.size() > Constants.INDEX_UNIT) {
            mTracks.remove(position);
            notifyItemRemoved(position);
            mDragDropListener.onSwipeViewHolder(mTracks);
            return;
        }
        notifyDataSetChanged();
    }

    public TrackAdapter setNowPlaying(boolean nowPlaying) {
        mIsNowPlaying = nowPlaying;
        return this;
    }

    public TrackAdapter setRecentTracks(boolean recentTracks) {
        mIsRecentTracks = recentTracks;
        return this;
    }

    public TrackAdapter setFavarite(boolean favarite) {
        isFavarite = favarite;
        return this;
    }

    public interface OnClickItemSongListener {
        void clickItemSongListener(int position);

        void showDialodFeatureTrack(int position);
    }


    public interface OnClickDeleteListener {
        void deleteFromFavorite(int position);
    }

    public interface OnDragDropListener {
        void onDropViewHolder(List<Track> tracks);

        void onSwipeViewHolder(List<Track> tracks);
    }
}
