package com.xmum.hiyapodcast;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.xmum.hiyapodcast.adapters.PlayerTrackPagerAdapter;
import com.xmum.hiyapodcast.base.BaseActivity;
import com.xmum.hiyapodcast.interfaces.IPlayerCallback;
import com.xmum.hiyapodcast.presenters.PlayerPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "?";//这里有问题 但我没看到他什么时候创建的tag
    private PlayerPresenter mPlayerPresenter;
    private ImageView mControlBtn;

    private SimpleDateFormat mMinFormat= new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat= new SimpleDateFormat("HH:mm:ss");
    private TextView mTotalDuration;
    private TextView mMCurrentPosition;
    private SeekBar mMSeekBar;
    private int mCurrentProgress=0;
    private boolean mIsUserTouchProgressBar= false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPretBtn;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initView();
        //在界面初始化以后，才去获取数据
        mPlayerPresenter.getPlayList();
        initEvent();
        startPlay();
    }

    protected void onDestroy()
    {
        super.onDestroy();
        if(mPlayerPresenter!=null)
        {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter=null;
        }
    }

    private void startPlay() {
        if(mPlayerPresenter!=null)
        {
            mPlayerPresenter.play();
        }

    }

    //set controller event
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:
                if(mPlayerPresenter.isPlay())
                {
                    //if status is playing ,then pause
                    mPlayerPresenter.pause();
                }
                else
                {
                    //if status is not playing ,then play
                    mPlayerPresenter.play();
                }

            }
        });
        mMSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if(isFromUser)
                {
                    mCurrentProgress=progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar=false;
                //update when touch ended
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play next
                if(mPlayerPresenter!=null)
                {
                    mPlayerPresenter.playNext();
                }
            }
        });
        mPlayPretBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play pre
                if(mPlayerPresenter!=null)
                {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mTrackPageView.addOnPageChangeListener(this);

        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager=true;
                    break;
                }
                return false;
            }
        });
    }

    //find each controller
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mMCurrentPosition = this.findViewById(R.id.current_position);
        mMSeekBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPretBtn = this.findViewById(R.id.play_pre);
        mTrackTitle = this.findViewById(R.id.track_title);
        if(!TextUtils.isEmpty(mTrackTitleText))
        {
            mTrackTitle.setText(mTrackTitleText);
        }
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
    }

    @Override
    public void onPlayStart() {
        //start play, change the button to pause
        if(mControlBtn!=null)
        {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        //start play, change the button to pause
        if(mControlBtn!=null)
        {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        //start play, change the button to pause
        if(mControlBtn!=null)
        {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        //LogUtil.d(TAG, "list -- > "+list);
        //把数据设置到适配器里
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        mMSeekBar.setMax(total);
        //update progress bar
        //update total
        String totalDuration;
        String currnPosition;

        if(total>1000*60*60)
        {
            totalDuration = mHourFormat.format(total);
            currnPosition= mHourFormat.format(currentProgress);
        }
        else
        {
            totalDuration=mMinFormat.format(total);
            currnPosition= mMinFormat.format(currentProgress);
        }
        if(mTotalDuration!=null)
        {
            mTotalDuration.setText(totalDuration);
        }

        //update current time
        if( mMCurrentPosition!=null)
        {
            mMCurrentPosition.setText(currnPosition);
        }
        //update progress
        if(!mIsUserTouchProgressBar)
        {
            mMSeekBar.setProgress(currentProgress);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onCheckUpdate(Track track, int playIndex) {
        this.mTrackTitleText=track.getTrackTitle();
        if(mTrackTitle!=null)
        {//设置当前节目标题
            mTrackTitle.setText(mTrackTitleText);
        }
        //当节目改变的时候我们就获取当前播放中的位置
        //当前的节目改变以后 要修改页面的图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(playIndex,true);
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d(TAG, "position -- > "+position);//创建了一个local TAG因为原本的不可读
        //当页面选中，就切换播放的内容
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager=false;

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
