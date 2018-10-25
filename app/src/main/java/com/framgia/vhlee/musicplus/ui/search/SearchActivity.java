package com.framgia.vhlee.musicplus.ui.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.ui.LoadMoreAbstract;
import com.framgia.vhlee.musicplus.ui.adapter.TrackAdapter;

public class SearchActivity extends LoadMoreAbstract
        implements View.OnClickListener, TrackAdapter.OnClickItemSongListener {
    private EditText mEditInput;
    private ImageView mImageBack;
    private ImageView mImageClear;
    private ProgressBar mProgressBar;
    private TrackAdapter mTrackAdapter;
    private String mKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViewLoadMore();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_clear:
                mEditInput.setText("");
                break;
            case R.id.image_back:
                super.onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void loadMoreData() {
        mIsScrolling = false;
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void initViewLoadMore() {
        mEditInput = findViewById(R.id.edit_search);
        mImageBack = findViewById(R.id.image_back);
        mImageClear = findViewById(R.id.image_clear);
        mProgressBar = findViewById(R.id.progress_search_more);
        mRecyclerView = findViewById(R.id.recycler_search);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mTrackAdapter = new TrackAdapter(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mTrackAdapter);
        mProgressBar.setVisibility(View.GONE);
        setListener();
        setLoadMore();
    }

    @Override
    public void clickItemSongListener(int position) {
    }

    @Override
    public void showDialodFeatureTrack(int position) {
    }

    private void setListener() {
        mImageBack.setOnClickListener(this);
        mImageClear.setOnClickListener(this);
        mEditInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence keyword, int i, int i1, int i2) {
                mKeyword = keyword.toString();
                if (mKeyword.isEmpty()) mImageClear.setImageResource(R.drawable.ic_arrow_right);
                else mImageClear.setImageResource(R.drawable.ic_delete);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
