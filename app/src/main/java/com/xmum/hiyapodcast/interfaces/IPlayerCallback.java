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
    void onListLoaded(List<Track> list);
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);
    void onProgressChange(long currentProgress,long total);
    void onAdLoading();
    void onAdFinished();

    

}
