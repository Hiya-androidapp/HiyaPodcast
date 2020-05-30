package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.xmum.hiyapodcast.base.IBasePresenter;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {
    void play();
    void pause();
    void stop();
    void playPre();
    void playNext();
    void switchPlayMode(XmPlayListControl.PlayMode mode);
    void getPlayList();
    void playByIndex(int index);
    void seekTo(int progress);
}