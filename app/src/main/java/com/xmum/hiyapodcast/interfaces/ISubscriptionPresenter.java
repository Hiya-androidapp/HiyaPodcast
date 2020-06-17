package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import com.xmum.hiyapodcast.base.IBasePresenter;
//max limit of subscription might be 100
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     * @param
     */
    void addSubscription(Album album);


    /**
     * 删除订阅
     * @param
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscriptionList();

}
