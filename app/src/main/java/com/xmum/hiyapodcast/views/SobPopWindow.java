package com.xmum.hiyapodcast.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.base.BaseApplication;

public class SobPopWindow extends PopupWindow {
    public SobPopWindow()
    {
        //set height and weight
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //set setBackgroundDrawable before setOutsideTouchable
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //外部可点击
        setOutsideTouchable(true);
        //load view
        View popView=LayoutInflater.from(BaseApplication.getAppContex()).inflate(R.layout.pop_play_list,null);
        //set content
        setContentView(popView);
        //set animation
        setAnimationStyle(R.style.pop_animation);
    }
}
