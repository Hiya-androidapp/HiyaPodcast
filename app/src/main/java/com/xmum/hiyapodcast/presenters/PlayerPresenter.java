package com.xmum.hiyapodcast.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.xmum.hiyapodcast.api.HiyaApi;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.interfaces.IPlayerCallback;
import com.xmum.hiyapodcast.interfaces.IPlayerPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private List<IPlayerCallback> mIPlayerCallbacks=new ArrayList<>();
    private  final XmPlayerManager mPlayerManager;
    private static final String TAG = "PlayerPresenter";
    private Track mCurrentTrack;
    public static final int DEFAULT_PLAY_INDEX = 0;
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;
    private final SharedPreferences mplayModSp;
    //save current play mode
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    private boolean mIsReverse = false;

    public  static final int PLAY_MODE_LIST_INT = 0;
    public  static final int PLAY_MODE_LIST_LOOP_INT = 1;
    public  static final int PLAY_MODE_RANDOM_INT = 2;
    public  static final int PLAY_MODE_SINGLE_LOOP_INT = 3;

    //sp's key and name
    public static  String PLAY_MODE_SP_NAME="PlayMod";
    public static  String PLAY_MODE_SP_KEY="currentPlayMod";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

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
    //check if there is a play list
    public boolean hasPlayList(){
        return isPlayListSet;
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
        //切换播放器到第index的位置进行播放
        if(mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //update progress
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //return the play status
        return mPlayerManager.isPlaying();
    }
    @Override
    public void reversePlayList() {
        //swap play list
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse = !mIsReverse;

        //the first parameter is the play list, the second is the index
        //new index = total - 1 - current index
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);
        //update UI
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //todo:
        //1. get the content of an album
        HiyaApi hiyaApi = HiyaApi.getsHiyaApi();
        hiyaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                //2. set the album content to the player
                List<Track> tracks = trackList.getTracks();
                if (trackList != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, DEFAULT_PLAY_INDEX);
                    isPlayListSet = true;
                    //在一个专辑中第一次打开一个播放界面获取名称
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContex(), "request failed", Toast.LENGTH_SHORT).show();
            }
        }, (int)id, 1);
        //3. play
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //inform the recent audio
        iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgressPosition, mProgressDuration);
        //update state
        handlePlayState(iPlayerCallback);
        //get play mode state from sp
        int modeIndex=mplayModSp.getInt(PLAY_MODE_SP_KEY,PLAY_MODE_LIST_INT);
        mCurrentPlayMode=getModeByInt(modeIndex);
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

        if(!mIPlayerCallbacks.contains(iPlayerCallback))
        {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();
        //invoke interface method based on the state
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallback.onPlayStart();
        } else {
            iPlayerCallback.onPlayPause();
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
                iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
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
        this.mCurrentProgressPosition = currentPos;
        this.mProgressDuration = duration;
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
