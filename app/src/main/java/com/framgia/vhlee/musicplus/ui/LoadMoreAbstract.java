package com.framgia.vhlee.musicplus.ui;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ProgressBar;

public abstract class LoadMoreAbstract extends AppCompatActivity {
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected boolean mIsScrolling = false;
    protected int mCurrentItem, mTotalItem, mScrollOutItem;
    protected ProgressBar mProgressBar;

    protected void setLoadMore() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mCurrentItem = mLinearLayoutManager.getChildCount();
                mTotalItem = mLinearLayoutManager.getItemCount();
                mScrollOutItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (mIsScrolling && (mCurrentItem + mScrollOutItem == mTotalItem)) {
                    loadMoreData();
                }
            }
        });
    }

    public abstract void loadMoreData();

    public abstract void initViewLoadMore();
}
