package com.xmum.hiyapodcast.presenters;

import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
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

    private PlayerPresenter (){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContex());
        //ad material
        mPlayerManager.addAdsStatusListener(this);
        //register player status inteface
        mPlayerManager.addPlayerStatusListener(this);
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
        }else {
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

    }

    @Override
    public void getPlayList() {

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
    }

    //switch mode
    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        LogUtil.d(TAG,"onSoundSwitch");
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
