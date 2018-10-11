package com.framgia.vhlee.musicplus.ui.genres;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.data.model.Track;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.List;

public class GenresActivity extends AppCompatActivity {
    private TrackAdapter mAdapter;
    private List<Track> mTracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genres);
        String title = null;
        initToolbar(title);
        initData();
        RecyclerView recyclerView = findViewById(R.id.recycler_list_tracks);
        mAdapter = new TrackAdapter(mTracks);
        recyclerView.setAdapter(mAdapter);
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
        toolbar.setTitleTextColor(Color.RED);
        toolbar.setSubtitleTextColor(Color.RED);
        if (title == null || title.isEmpty()) {
            setTitle(getString(R.string.default_toolbar_title));
        } else {
            setTitle(title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        mTracks = new ArrayList<>();
    }
}
