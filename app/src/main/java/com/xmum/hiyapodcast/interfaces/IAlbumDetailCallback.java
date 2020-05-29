package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailCallback {
    //加载出来详情的内容
    void onDetailListLoaded(List<Track> trackList);
    //网络错误, i是errorCode，s是errorMsg
    void onNetworkError(int i, String s);
    //把ALBUM 传给ui
    void onAlbumLoaded(Album album);
}
