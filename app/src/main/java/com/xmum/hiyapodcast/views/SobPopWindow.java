package com.xmum.hiyapodcast.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.adapters.PlayListAdapter;
import com.xmum.hiyapodcast.base.BaseApplication;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mClosebtn;
    private RecyclerView mtrackList;
    private PlayListAdapter mPlayListAdapter;

    public SobPopWindow()
    {
        //set height and weight
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //set setBackgroundDrawable before setOutsideTouchable
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //外部可点击
        setOutsideTouchable(true);
        //load view
        mPopView = LayoutInflater.from(BaseApplication.getAppContex()).inflate(R.layout.pop_play_list,null);
        //set content
        setContentView(mPopView);
        //set animation
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
    }

    private void initEvent() {
        //click close , the window is closed
        mClosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
    }

    private void initView() {
        mClosebtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找到控件
        mtrackList = mPopView.findViewById(R.id.play_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContex());
        mtrackList.setLayoutManager(layoutManager);
        mPlayListAdapter = new PlayListAdapter();
        mtrackList.setAdapter(mPlayListAdapter);
    }
    public void setListData(List<Track> data){
        //设置适配器
        if (mPlayListAdapter!=null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
        }
    }
}
