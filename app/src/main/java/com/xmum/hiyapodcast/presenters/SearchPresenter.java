package com.xmum.hiyapodcast.presenters;

import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
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
        search(keyword);
    }

    private void search(String keyword) {
        mHiyaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onSearchResultLoaded(albums);
                    }
                } else {
                    LogUtil.d(TAG, "album is null..");
                }

            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
                for(ISearchCallback iSearchCallback:mCallback)
                {
                    iSearchCallback.onError(errorCode,errorMsg);
                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        //todo: hotword buffer for data saving
        mHiyaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if(hotWordList!=null)
                {
                    List<HotWord> hotWords=hotWordList.getHotWordList();
                    LogUtil.d(TAG,"hotWords size-->"+hotWords.size());
                    for(ISearchCallback iSearchCallback:mCallback)
                    {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "getHotWord errorCode -- > " + i);
                LogUtil.d(TAG, "getHotWord errorMsg -- > " + s);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mHiyaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if(suggestWords!=null)
                {
                    List<QueryResult> keyWordList=suggestWords.getKeyWordList();
                    LogUtil.d(TAG,"hotWords size-->"+keyWordList.size());

                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onRecommendMoreLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "getRecommendWord errorCode -- > " + i);
                LogUtil.d(TAG, "getRecommendWord errorMsg -- > " + s);
            }
        });
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
