package com.xmum.hiyapodcast.interfaces;

import com.xmum.hiyapodcast.base.IBasePresenter;

public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {

    //do search
    void doSearch(String keyword);

    //retry search
    void reSearch();

    //load more search result
    void loadMore();

    //get hot word
    void getHotWord();

    //get related keyword
    void getRecommendWord(String keyword);
}
