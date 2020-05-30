package com.xmum.hiyapodcast.presenters;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailCallback;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailPresenter;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private Album mTargetAlbum=null;
    private static String TAG="AlbumDetailPresenter";
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
    public void getAlbumDetail(int albumID, int page) {
        //get detail refer to page and album
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumID+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constant.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>(){

            @Override
            public void onSuccess(TrackList trackList) {

                if(trackList !=null)
                {
                    List<Track> tracks=trackList.getTracks();
                    LogUtil.d(TAG,"tracks size -->"+tracks.size());
                    handerAlbumDetailResult(tracks);
                }
            }

            private void handerAlbumDetailResult(List<Track> tracks) {
                for(IAlbumDetailCallback mCallback:mCallbacks)
                {
                    mCallback.onDetailListLoaded(tracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"errorCode -->"+i);
                LogUtil.d(TAG,"errorMsg" +s);
                handlerError(i, s);
            }
        });
    }
    //if there is error, inform UI
    private void handlerError(int i, String s) {
        for (IAlbumDetailCallback callback : mCallbacks) {
            callback.onNetworkError(i, s);
        }
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
