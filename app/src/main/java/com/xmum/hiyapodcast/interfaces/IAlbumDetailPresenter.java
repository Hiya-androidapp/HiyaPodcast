package com.xmum.hiyapodcast.interfaces;

import com.xmum.hiyapodcast.base.IBasePresenter;

public interface IAlbumDetailPresenter  extends IBasePresenter<IAlbumDetailCallback> {
    /**
     * pull down to refresh more
     */
    void pull2RefreshMore();

    /**
     * get load more
     */
    void loadMore();
    /**
     * get album detail
     */
    void getAlbumDetail(int album,int page);
    /**
     * register and unregister ui inform
     */

}
