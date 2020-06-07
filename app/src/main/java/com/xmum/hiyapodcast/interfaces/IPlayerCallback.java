package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback  {

    void onPlayStart();

    void onPlayPause();

    void onPlayStop();

    void onPlayError();

    void nextPlay(Track track);

    void onPrePlay(Track track);

    //play list has loaded
    void onListLoaded(List<Track> list);

    //change play mode
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    //change the progress bar
    void onProgressChange(int currentProgress,int total);

    //load the ad from sdk
    void onAdLoading();

    //ad load finished
    void onAdFinished();

    /**
     * 更新当前节目
     * @param track 节目
     */
    // update the current title
    void onTrackUpdate(Track track, int playIndex);

    void updateListOrder(boolean isReverse);
}
