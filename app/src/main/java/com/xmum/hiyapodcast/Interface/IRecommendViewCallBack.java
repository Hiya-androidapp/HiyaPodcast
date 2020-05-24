package com.xmum.hiyapodcast.Interface;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;
public interface IRecommendViewCallBack {

    /**
     *
     * 获取推荐内容的结果
     * @param result
     * */
    void onRecommendListLoaded(List<Album> result);


    /**
     *
     * 加载更多
     * @param result
     * */
    void onLoaderMore(List<Album> result);


    /**
     *
     * 下拉加载更多结果
     * @param result
     * */
    void onRefreshMore(List<Album> result);



}
