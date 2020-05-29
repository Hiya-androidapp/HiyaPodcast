package com.xmum.hiyapodcast.interfaces;

public interface IAlbumDetailPresenter  {
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
    void registerViewCallback(IAlbumDetailCallback detailCallback);
    void unRegisterViewCallback(IAlbumDetailCallback detailCallback);
}
