package com.xmum.hiyapodcast;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xmum.hiyapodcast.base.BaseActivity;

public class PlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
    }
}
