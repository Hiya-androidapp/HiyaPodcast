package com.xmum.hiyapodcast.presenters;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.xmum.hiyapodcast.api.HiyaApi;
import com.xmum.hiyapodcast.interfaces.ISearchCallback;
import com.xmum.hiyapodcast.interfaces.ISearchPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private static final String TAG = "SearchPresenter";
    //current keyword
    private String mCurrentKeyword = null;
    private final HiyaApi mHiyaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter(){
        mHiyaApi = HiyaApi.getsHiyaApi();

    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter(){
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    private List<ISearchCallback> mCallback = new ArrayList<>();
    @Override
    public void doSearch(String keyword) {
        //be used to get new search result
        //when network is not good, the user will click Re-Search
        this.mCurrentKeyword = keyword;
        mHiyaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                } else {
                    LogUtil.d(TAG, "album is null..");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void reSearch() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {

    }

    @Override
    public void getRecommendWord(String keyword) {

    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallback.remove(iSearchCallback);
    }
}
