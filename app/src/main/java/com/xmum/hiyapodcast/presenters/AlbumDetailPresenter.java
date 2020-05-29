package com.xmum.hiyapodcast.presenters;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailCallback;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailPresenter;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private Album mTargetAlbum=null;
    private List<IAlbumDetailCallback> mCallbacks=new ArrayList<>();

    private AlbumDetailPresenter()
    {}
    private static AlbumDetailPresenter sInstance=null;

    public static AlbumDetailPresenter getInstance()
    {
        if(sInstance==null)
        {
            synchronized (AlbumDetailPresenter.class){
                if(sInstance==null){
                    sInstance=new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int album, int page) {

    }

    @Override
    public void registerViewCallback(IAlbumDetailCallback detailCallback) {
        if(!mCallbacks.contains(detailCallback))
        {
            mCallbacks.add(detailCallback);
            if(mTargetAlbum!=null)
            {
                detailCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailCallback detailCallback) {
        mCallbacks.remove(detailCallback);
    }

    public void setTargetAlbum(Album targetAlbum)
    {
        this.mTargetAlbum=targetAlbum;
    }
}
