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
import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.GenreKey;
import com.framgia.vhlee.musicplus.data.model.GenreName;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.data.repository.TrackDataRepository;
import com.framgia.vhlee.musicplus.ui.adapter.GenreAdapter;
import com.framgia.vhlee.musicplus.ui.genres.GenresActivity;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.List;

public class HomeFragment extends Fragment
        implements View.OnClickListener,
        GenreAdapter.GenreClickListener, HomeContract.View {
    private static final String ARTWORK_DEFAULT_SIZE = "large";
    private static final String ARTWORK_MAX_SIZE = "t500x500";
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
                .replace(ARTWORK_DEFAULT_SIZE, ARTWORK_MAX_SIZE);
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
        String source = StringUtil.initGenreApi(GenreKey.ALL_MUSIC, 0);
        mPresenter.loadHighlight(source);
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
        Glide.with(getContext())
                .load(source)
                .apply(requestOptions)
                .into(image);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_cover:
                break;
            default:
                break;
        }
    }
}
