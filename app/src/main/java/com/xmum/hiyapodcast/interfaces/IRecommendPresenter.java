package com.xmum.hiyapodcast.interfaces;

import com.xmum.hiyapodcast.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallBack> {
    /**
     * get recommend content
     */
    void getRecommendList();

    /**
     * pull down to refresh more
     */
    void pull2RefreshMore();

    /**
     * get load more
     */
    void loadMore();


}
