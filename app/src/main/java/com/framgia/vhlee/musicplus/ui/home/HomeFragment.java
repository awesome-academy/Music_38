package com.framgia.vhlee.musicplus.ui.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.vhlee.musicplus.R;
import com.framgia.vhlee.musicplus.ui.adapter.GenreAdapter;

public class HomeFragment extends Fragment {
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

    private void initUI(View view) {
        RecyclerView recyclerGenres = view.findViewById(R.id.recycler_genres);
        recyclerGenres.setAdapter(new GenreAdapter(getActivity()));
    }
}
