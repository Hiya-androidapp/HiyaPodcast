package com.xmum.hiyapodcast.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    //search result
    void onSearchResultLoaded(List<Album> result);

    //get recommended hot word
    void onHotWordLoaded(List<HotWord> hotWordList);

    //return load more result, true refers to load successful and vice versa
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    //callback method of recommended keyword
    void onRecommendMoreLoaded(List<QueryResult> keyWordList);
}
