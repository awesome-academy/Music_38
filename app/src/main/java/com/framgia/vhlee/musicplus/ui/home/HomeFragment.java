package com.framgia.vhlee.musicplus.ui.home;

import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.GenreKey;
import com.framgia.vhlee.musicplus.data.model.GenreName;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackDataRepository;
import com.framgia.vhlee.musicplus.service.MyService;
import com.framgia.vhlee.musicplus.ui.adapter.GenreAdapter;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.ui.genres.GenresActivity;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.MySharedPreferences;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment
        implements GenreAdapter.GenreClickListener, HomeContract.View,
        TrackAdapter.OnClickItemSongListener, View.OnClickListener {
    private static final int NUMBER_OF_GENRES = 6;
    private static final int NUMBER_1 = 1;
    private static final int NUMBER_2 = 2;
    private static final int NUMBER_3 = 3;
    private static final int NUMBER_4 = 4;
    private static final int NUMBER_5 = 5;
    private static final int NUMBER_6 = 6;
    private static final String ARTWORK_DEFAULT_SIZE = "large";
    private static final String ARTWORK_MAX_SIZE = "t500x500";
    private HomeContract.Presenter mPresenter;
    private GenreAdapter mGenreAdapter;
    private ImageView mImageCover;
    private ImageView mImageAvatar;
    private TextView mTextTitle;
    private TextView mTextArtist;
    private List<Track> mTracks;
    private static MyService mService;
    private TrackAdapter mAdapter;
    private MySharedPreferences mPreferences;
    private RecyclerView mRecyclerRecent;
    private List<Track> mHighLightTrack;
    private View mViewHighlight;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.LocalBinder binder = (MyService.LocalBinder) iBinder;
            mService = binder.getService();
            initRecyclerRecent();
            getRecentTrack();
            loadHighlight();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            getActivity().unbindService(mConnection);
        }
    };

    private void loadHighlight() {
        String genresKey = randomGenresKey();
        String source = StringUtil.initGenreApi(genresKey, 0);
        mPresenter.loadHighlight(source);
    }

    private String randomGenresKey() {
        String genresKey;
        Random r = new Random();
        int result = r.nextInt(NUMBER_OF_GENRES) + Constants.INDEX_UNIT;
        switch (result) {
            case NUMBER_1:
                genresKey = GenreKey.ALL_MUSIC;
                break;
            case NUMBER_2:
                genresKey = GenreKey.ALL_AUDIO;
                break;
            case NUMBER_3:
                genresKey = GenreKey.ALTERNATIVE;
                break;
            case NUMBER_4:
                genresKey = GenreKey.AMBIENT;
                break;
            case NUMBER_5:
                genresKey = GenreKey.CLASSICAL;
                break;
            case NUMBER_6:
                genresKey = GenreKey.COUNTRY;
                break;
            default:
                genresKey = GenreKey.ALL_MUSIC;
                break;
        }
        return genresKey;
    }

    private void getRecentTrack() {
        mPreferences = new MySharedPreferences(getActivity());
        long[] idRecentTracks = mPreferences.getData();
        for (int i = 0; i < idRecentTracks.length; i++) {
            String api = StringUtil.initDetailApi(idRecentTracks[i]);
            mPresenter.loadRecent(api);
        }
    }

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUI(view);
        getActivity().bindService(MyService.getMyServiceIntent(getActivity()), mConnection, Context.BIND_AUTO_CREATE);
        return view;
    }

    /**
     * Get tracks callback
     *
     * @param tracks
     */
    @Override
    public void showHighLight(List<Track> tracks) {
        mViewHighlight.setEnabled(true);
        Track track = tracks.get(0);
        String artworkCover = track.getArtworkUrl()
                .replace(ARTWORK_DEFAULT_SIZE, ARTWORK_MAX_SIZE);
        setImage(mImageAvatar, track.getArtworkUrl());
        setImage(mImageCover, artworkCover);
        mTextTitle.setText(track.getTitle());
        mTextArtist.setText(track.getArtist());
        mHighLightTrack = tracks;
    }

    @Override
    public void showNoHighlight(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showRecent(List<Track> tracks) {
        mTracks.addAll(tracks);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showRecentFail(String message) {

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
        mViewHighlight = view.findViewById(R.id.view_content_highlight);
        mViewHighlight.setOnClickListener(this);
        mViewHighlight.setEnabled(false);
        RecyclerView recyclerGenres = view.findViewById(R.id.recycler_genres);
        initRecycler(recyclerGenres);
        mRecyclerRecent = view.findViewById(R.id.recycler_recent);
        initData();
    }

    private void initRecyclerRecent() {
        mTracks = new ArrayList<>();
        mAdapter = new TrackAdapter(mTracks, this);
        mAdapter.setRecentTracks(true);
        mRecyclerRecent.setAdapter(mAdapter);
    }

    private void initRecycler(RecyclerView recyclerGenres) {
        mGenreAdapter = new GenreAdapter(getActivity());
        mGenreAdapter.setGenreClickListener(this);
        recyclerGenres.setAdapter(mGenreAdapter);
    }

    private void initData() {
        mGenreAdapter.addGenre(new Genre(GenreKey.ALL_MUSIC,
                GenreName.ALL_MUSIC, R.drawable.all_music));
        mGenreAdapter.addGenre(new Genre(GenreKey.ALL_AUDIO,
                GenreName.ALL_AUDIO, R.drawable.all_audio));
        mGenreAdapter.addGenre(new Genre(GenreKey.ALTERNATIVE,
                GenreName.ALTERNATIVE, R.drawable.rock));
        mGenreAdapter.addGenre(new Genre(GenreKey.AMBIENT,
                GenreName.AMBIENT, R.drawable.ambient));
        mGenreAdapter.addGenre(new Genre(GenreKey.CLASSICAL,
                GenreName.CLASSICAL, R.drawable.classical));
        mGenreAdapter.addGenre(new Genre(GenreKey.COUNTRY,
                GenreName.COUNTRY, R.drawable.country));
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
        if (getContext() != null) {
            Glide.with(getContext())
                    .load(source)
                    .apply(requestOptions)
                    .into(image);
        }
    }

    @Override
    public void clickItemSongListener(int position) {
        mService.setTracks(mTracks);
        mService.requestCreate(position);
    }

    @Override
    public void showDialodFeatureTrack(int position) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_content_highlight:
                mService.setTracks(mHighLightTrack);
                mService.requestCreate(0);
                break;
            case R.id.image_cover:
                break;
            default:
                break;
        }
    }
}
