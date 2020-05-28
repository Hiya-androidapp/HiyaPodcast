package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallBack {

    /**
     *
     * get recommendation result
     * @param result
     * */
    void onRecommendListLoaded(List<Album> result);
    /**
     * load more
     * @param result
     */
    void onLoadMore(List<Album> result);

    /**
     * refresh more
     * @param result
     */
    void onRefreshMore(List<Album> result);


    void onNetworkError();

    void onEmpty();

    void onLoading();

}
