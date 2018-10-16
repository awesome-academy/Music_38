package com.framgia.vhlee.musicplus.ui.genres;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Genre;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;
import com.framgia.vhlee.musicplus.util.Constants;
import com.framgia.vhlee.musicplus.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GenresActivity extends AppCompatActivity implements GenresContract.View {
    private TrackAdapter mAdapter;
    private List<Track> mTracks;
    private GenresContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        String title = null;
        initToolbar(title);
        mPresenter = new GenresPresenter(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_list_tracks);
        mTracks = new ArrayList<>();
        mAdapter = new TrackAdapter(mTracks);
        recyclerView.setAdapter(mAdapter);
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_genres, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar(String title) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (title == null || title.isEmpty()) {
            setTitle(getString(R.string.default_toolbar_title));
        } else {
            setTitle(title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        Intent intent = getIntent();
        Genre genres = (Genre) intent.getSerializableExtra(Constants.Common.EXTRA_GENRES);
        String api = StringUtil.getTrackByGenreApi(genres.getKey());
        mPresenter.getTracks(api);
    }

    @Override
    public void onLoadTracksSuccess(List<Track> tracks) {
        mAdapter.updateTracks(tracks);
    }

    @Override
    public void onLoadTracksFail(String message) {
        Toast.makeText(GenresActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    public static Intent getGenresIntent(Context context, Genre genre) {
        Intent intent = new Intent(context, GenresActivity.class);
        intent.putExtra(Constants.Common.EXTRA_GENRES, genre);
        return intent;
    }
}
