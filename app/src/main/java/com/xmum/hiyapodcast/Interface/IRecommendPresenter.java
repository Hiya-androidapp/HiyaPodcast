package com.xmum.hiyapodcast.Interface;

public interface IRecommendPresenter {
    /*获取推荐内容*/
    void getRecommendList();

    /*下拉刷新内容*/
    void pull2RefreshMore();

    /*上接加载更多*/
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
