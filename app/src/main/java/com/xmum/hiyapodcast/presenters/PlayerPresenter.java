package com.xmum.hiyapodcast.presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.interfaces.IPlayerCallback;
import com.xmum.hiyapodcast.interfaces.IPlayerPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private List<IPlayerCallback> mIPlayerCallbacks=new ArrayList<>();
    private  final XmPlayerManager mPlayerManager;
    private static final String TAG="PlayerPresenter";
    private Track mCurrentTrack;
    private int mCurrentIndex=0;
    private final SharedPreferences mplayModSp;
    //save current play mode
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    public  static final int PLAY_MODE_LIST_INT =0;
    public  static final int PLAY_MODE_LIST_LOOP_INT =1;
    public  static final int PLAY_MODE_RANDOM_INT =2;
    public  static final int PLAY_MODE_SINGLE_LOOP_INT =3;

    //sp's key and name
    public static  String PLAY_MODE_SP_NAME="PlayMod";
    public static  String PLAY_MODE_SP_KEY="currentPlayMod";

    private PlayerPresenter (){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContex());
        //ad material
        mPlayerManager.addAdsStatusListener(this);
        //register player status inteface
        mPlayerManager.addPlayerStatusListener(this);
        //record current play mode
        mplayModSp = BaseApplication.getAppContex().getSharedPreferences("PlayMod", Context.MODE_PRIVATE);


    }
    private  static PlayerPresenter sPlayerPresenter;

    public  static PlayerPresenter getsPlayerPresenter(){
        if(sPlayerPresenter==null)
        {
            synchronized (PlayerPresenter.class)
            {
                if(sPlayerPresenter==null)
                    sPlayerPresenter=new PlayerPresenter();
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet=false;

    public  void setPlayList(List<Track> list, int playIndex){
        if(mPlayerManager!=null){


            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet=true;
            //在一个专辑中第一次打开一个播放界面获取名称
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex=playIndex;
        } else {
            LogUtil.d(TAG,"mPlayerManager null");
        }

    }
    @Override
    public void play() {
        if(isPlayListSet)
        {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if(mPlayerManager!=null)
        {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if(mPlayerManager!=null)
        {
            mPlayerManager.playPre();;
        }
    }

    @Override
    public void playNext() {
        if(mPlayerManager!=null)
        {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode=mode;
            mPlayerManager.setPlayMode(mode);
            //inform ui update the mode
            for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks){
                iPlayerCallback.onPlayModeChange(mode);
            }
            //save state to sp
            SharedPreferences.Editor edit= mplayModSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            edit.commit();
        }

    }
    private  int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch(mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODE_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODE_LIST_LOOP_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODE_SINGLE_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODE_RANDOM_INT;
        }
        return PLAY_MODE_LIST_INT;
    }
    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch(index){
            case PLAY_MODE_LIST_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
            case PLAY_MODE_LIST_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
            case PLAY_MODE_SINGLE_LOOP_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODE_RANDOM_INT:
                return XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
        }
        return XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    }
    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
        //mPlayerManager.getCommonTrackLis
    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
        //update progress
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //return the play status
        return mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onCheckUpdate(mCurrentTrack,mCurrentIndex);
        //get play mode state from sp
        int modeIndex=mplayModSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODE_LIST_INT);
        mCurrentPlayMode=getModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

        if(!mIPlayerCallbacks.contains(iPlayerCallback))
        {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        mIPlayerCallbacks.remove(iPlayerCallback);
    }
    // -----------------ad start----------------
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int i, int i1) {
        LogUtil.d(TAG,"onError what-->"+i+"extral-->"+i1);
    }
    //--------------------ad end------------------
    // -----------------player status start----------------
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks)
        {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks)
        {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks)
        {
            iPlayerCallback.onPlayStop();
        }
    }
    //play completed
    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }
    //prepare completed
    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备完了 可以去播放了
            mPlayerManager.play();
        }
        //.d(TAG,"current status ==> "+mPlayerManager.getPlayerStatus());
    }

    //switch mode
    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {

        //curMordel stand for current content
        //getKind() to get the kind of content
        //track represent track type
        mCurrentIndex=mPlayerManager.getCurrentIndex();
        if(curModel instanceof Track)
        {
            Track currentTrack = (Track) curModel;
            mCurrentTrack=currentTrack;
            LogUtil.d(TAG,"TITLE "+mCurrentTrack);
            //update ui
            for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks)
            {
                iPlayerCallback.onCheckUpdate(mCurrentTrack,mCurrentIndex);
            }
        }

    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"Buffer Progress"+progress);
    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        LogUtil.d(TAG,"onPlayProgress");
        //unit ms
        for(IPlayerCallback iPlayerCallback:mIPlayerCallbacks)
        {
            iPlayerCallback.onProgressChange(currentPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"onError-->"+e);
        return false;
    }
    // -----------------player status end----------------
}
