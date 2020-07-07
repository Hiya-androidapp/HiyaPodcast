package com.xmum.hiyapodcast.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {
    /**
     * set callback interface
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);
    /**
     * add history
     * @param track
     */
    void addHistory(Track track);
    /**
     * delete history
     * @param track
     */
    void delHistory(Track track);
    /**
     * clean history
     */
    void cleanHistories();
    /**
     * add history
     * /
     */
    void listHistories();
}
