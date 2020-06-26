package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.base.IBasePresenter;

public interface IHistoryPresenter extends IBasePresenter {
    /**
     * get history
     * /
     */
    void listHistories();
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
     * /
     */
    void cleanHistories();

}
