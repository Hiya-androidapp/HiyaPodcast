package com.xmum.hiyapodcast.interfaces;

public interface IRecommendPresenter {
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

    /**
     * 这个方法用于注册ui的回调
     * @param callback
     */
    void registerViewCallback(IRecommendViewCallBack callback);

    /**
     * 取消ui的回调注册
     * @param callBack
     */
    void unRegisterViewCallBack(IRecommendViewCallBack callBack);
}
