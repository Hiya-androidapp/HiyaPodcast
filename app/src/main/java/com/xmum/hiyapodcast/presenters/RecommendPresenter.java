package com.xmum.hiyapodcast.presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.xmum.hiyapodcast.interfaces.IRecommendPresenter;
import com.xmum.hiyapodcast.interfaces.IRecommendViewCallBack;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    public static String TAG="RecommendPresenter";
    private List<IRecommendViewCallBack> mCallBacks =new ArrayList<>();
    private RecommendPresenter(){}
    private static RecommendPresenter sInstance=null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance==null) {
            synchronized (RecommendPresenter.class){
                if (sInstance==null) {
                    sInstance=new RecommendPresenter();
                }
            }
        }

        return sInstance;
    }
    @Override
    public void getRecommendList() {
        updateLoading();
        Map<String, String> map = new HashMap<>();
        //number of return
        map.put(DTransferConstants.LIKE_COUNT, Constant.RECOMMAND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>(){

            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                // return success
                if(gussLikeAlbumList!=null)
                {
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    //update ui
                   // upRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //return fail
                LogUtil.d(TAG,"error -->"+i);
                LogUtil.d(TAG,"errorMsg -->"+s);
                handlerError();
            }
        });
    }

    private void handlerError() {
        if(mCallBacks!=null)
        {
            for(IRecommendViewCallBack callBack:mCallBacks)
            {
                callBack.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {

        //inform ui to update
        if(mCallBacks!=null)
        {
            if(albumList.size()==0)
            {
                for(IRecommendViewCallBack callBack:mCallBacks)
                    callBack.onEmpty();
            }
            else{
            for(IRecommendViewCallBack callBack:mCallBacks)
            {
                callBack.onRecommendListLoaded(albumList);
            }
            }
        }
    }
    private void updateLoading()
    {
        for(IRecommendViewCallBack callBack:mCallBacks)
        {
            callBack.onLoading();
        }
        LogUtil.d(TAG,"in -->"+"load");

    }
    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallBack callback) {
        if(mCallBacks!=null&&!mCallBacks.contains(callback))
        {
            mCallBacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallBack(IRecommendViewCallBack callBack) {
        if(mCallBacks!=null)
        {
            mCallBacks.remove(mCallBacks);
        }
    }
}
