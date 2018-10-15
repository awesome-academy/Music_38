package com.framgia.vhlee.musicplus.ui.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.vhlee.musicplus.BuildConfig;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackDataRepository;
import com.framgia.vhlee.musicplus.ui.adapter.GenreAdapter;
import com.framgia.vhlee.musicplus.ui.genres.GenresActivity;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.List;

public class HomeFragment extends Fragment
        implements GenreAdapter.GenreClickListener, HomeContract.View {
    private HomeContract.Presenter mPresenter;
    private GenreAdapter mGenreAdapter;
    private ImageView mImageCover;
    private ImageView mImageAvatar;
    private TextView mTextTitle;
    private TextView mTextArtist;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);
        return view;
    }

    /**
     * Get tracks callback
     *
     * @param tracks
     */
    @Override
    public void showHighLight(List<Track> tracks) {
        Track track = tracks.get(0);
        String artworkCover = track.getArtworkUrl()
                .replace(Constants.Track.ARTWORK_DEFAULT_SIZE, Constants.Track.ARTWORK_MAX_SIZE);
        setImage(mImageAvatar, track.getArtworkUrl());
        setImage(mImageCover, artworkCover);
        mTextTitle.setText(track.getTitle());
        mTextArtist.setText(track.getArtist());
    }

    @Override
    public void showNoHighlight(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRecent(List<Track> tracks) {

    }

    /**
     * Click listener
     *
     * @param genre
     */
    @Override
    public void onItemClick(Genre genre) {
        startActivity(GenresActivity.getGenresIntent(getActivity(), genre));
    }

    private void initUI(View view) {
        TrackDataRepository repository = TrackDataRepository.getsInstance();
        mPresenter = new HomePresenter(this, repository);
        mImageCover = view.findViewById(R.id.image_cover);
        mImageAvatar = view.findViewById(R.id.image_artwork);
        mTextTitle = view.findViewById(R.id.text_title_highlight);
        mTextArtist = view.findViewById(R.id.text_subtitle_highlight);
        RecyclerView recyclerGenres = view.findViewById(R.id.recycler_genres);
        initRecycler(recyclerGenres);
        initData();
    }

    private void initRecycler(RecyclerView recyclerGenres) {
        mGenreAdapter = new GenreAdapter(getActivity());
        mGenreAdapter.setGenreClickListener(this);
        recyclerGenres.setAdapter(mGenreAdapter);
    }

    private void initData() {
        String source = StringUtil.append(Constants.ApiConfig.BASE_URL_GENRES,
                Constants.ApiConfig.GENRES_ALL_MUSIC,
                Constants.ApiConfig.CLIENT_ID,
                BuildConfig.CLIENT_ID);
        mPresenter.loadHighlight(source);
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_ALL_MUSIC,
                Constants.Genre.ALL_MUSIC, R.drawable.default_artwork));
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_ALL_AUDIO,
                Constants.Genre.ALL_AUDIO, R.drawable.default_artwork));
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_ALTERNATIVEROCK,
                Constants.Genre.ALTERNATIVEROCK, R.drawable.default_artwork));
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_AMBIENT,
                Constants.Genre.AMBIENT, R.drawable.default_artwork));
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_CLASSICAL,
                Constants.Genre.CLASSICAL, R.drawable.default_artwork));
        mGenreAdapter.addGenre(new Genre(Constants.ApiConfig.GENRES_COUNTRY,
                Constants.Genre.COUNTRY, R.drawable.default_artwork));
    }

    /**
     * Load source into imageView
     *
     * @param image
     * @param source
     */
    public void setImage(ImageView image, String source) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.default_artwork);
        Glide.with(getContext())
                .load(source)
                .apply(requestOptions)
                .into(image);
    }
}
