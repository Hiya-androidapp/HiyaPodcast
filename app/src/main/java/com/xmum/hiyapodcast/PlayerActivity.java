package com.xmum.hiyapodcast;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.xmum.hiyapodcast.views.SobPopWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";
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
    private ImageView mplayModeSwitchBtn;
    public final int BG_ANIMATION_DURATION=500;
    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    //dealing with switching play mode
    //1.initial one is PLAY_MODEL_LIST
    //2.loop play PLAY_MODEL_LOOP
    //3.random play PLAY_MODEL_RANDOM
    //4.single loop PLAY_MODEL_SINGLE_LOOP
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeRule=new HashMap<>();
            static{
                sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
                sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
                sPlayModeRule.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_LIST);

            }

    private View mPlayListBtn;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimaator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //在界面初始化以后，才去获取数据
        mPlayerPresenter.getPlayList();
        initEvent();
        startPlay();
        initBgAnimation();
    }
    //使得透明度渐变
    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float) animation.getAnimatedValue();
                updateBgAlpha(value);

            }
        });
        mOutBgAnimaator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimaator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimaator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float) animation.getAnimatedValue();
                updateBgAlpha(value);

            }
        });
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
        mplayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //from current mode to one another
                XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
                if (mCurrentMode!=null)
                {
                    mPlayerPresenter.switchPlayMode(playMode);

                }



            }
        });
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //显示在底部
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //设置透明度渐变
                mEnterBgAnimator.start();
            }
        });
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop window dismiss
                mOutBgAnimaator.start();
            }
        });

        mSobPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //說明播放列表里的item被點擊了
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

    }
    //修改透明度
    public void updateBgAlpha(float alpha){
        Window window= getWindow();
        WindowManager.LayoutParams attributes= window.getAttributes();
        attributes.alpha=alpha;
        window.setAttributes(attributes);
    }

    private void updatePlayModeBtnImg() {
        //update play mode button image by play mode
        int resId=R.drawable.selector_player_list_order;
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
                resId= R.drawable.selector_player_list_order;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId= R.drawable.selector_player_single_loop;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId= R.drawable.selector_player_loop;
                break;
            case PLAY_MODEL_RANDOM:
                resId= R.drawable.selector_player_random;
                break;
        }
        mplayModeSwitchBtn.setImageResource(resId);
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
        //switch play mode
        mplayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
        //play list
        mPlayListBtn = this .findViewById(R.id.player_list);
        mSobPopWindow = new SobPopWindow();

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
        //give list a copy of data after data coming back
        if (mSobPopWindow!=null) {
            mSobPopWindow.setListData(list);
        }
    }


    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
                //renew play mode and change ui
                mCurrentMode=playMode;
                updatePlayModeBtnImg();


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
        //修改播放里的播放位置
        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentPlayPosition(playIndex);
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
