package com.xmum.hiyapodcast.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.base.BaseFragment;

public class RecommendFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        return rootView;
    }
}
