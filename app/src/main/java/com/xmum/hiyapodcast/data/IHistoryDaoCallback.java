package com.xmum.hiyapodcast.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {
    /**
     * add history
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);
    /**
     * add history
     * @param isSuccess
     */

    void onHistoryDel(boolean isSuccess);

    /**
     * load history
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
    /**
     * clean history
     * @param isSuccess
     */
    void onHistoriesClean(boolean isSuccess);
}
