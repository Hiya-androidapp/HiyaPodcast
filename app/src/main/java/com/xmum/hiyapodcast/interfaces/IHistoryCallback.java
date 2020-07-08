package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.base.IBasePresenter;

import java.util.List;

public interface IHistoryCallback  {
    //loading history
    void onHistoriesLoaded(List<Track> tracks);
}
