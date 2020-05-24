package com.xmum.hiyapodcast.Presenters;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.xmum.hiyapodcast.Interface.IRecommendPresenter;
import com.xmum.hiyapodcast.Interface.IRecommendViewCallBack;
import com.xmum.hiyapodcast.utils.Constants;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter{

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallBack> mCallBacks =new ArrayList<>();

    private RecommendPresenter(){

    }

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

    //get recommend resource(guess what u like)
    @Override
    public void getRecommendList() {
        //获取推荐内容
        getRecommendData();
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name --> "+Thread.currentThread().getName());
                if(gussLikeAlbumList != null){
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    //after getting data, update UI
                    //upRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"error -- >" + i);
                LogUtil.d(TAG,"errorMsg -- >" + s);
            }
        });

    }


    private void handlerRecommendResult(List<Album> albumList) {
        //inform the ui to update
        if (mCallBacks!=null) {
            for (IRecommendViewCallBack callback : mCallBacks) {
                callback.onRecommendListLoaded(albumList);
                
            }
        }

    }

    private void handlerRecommendResult() {
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }


    @Override
    public void registerViewCallback(IRecommendViewCallBack callback){
        if (!mCallBacks.contains(callback) && mCallBacks != null) {
            mCallBacks.add(callback);
        }
    }


    /**
     * 取消ui的回调注册
     * @param callBack
     */
    public void unRegisterViewCallBack(IRecommendViewCallBack callBack){
        if (mCallBacks!=null) {
            mCallBacks.remove(mCallBacks);
        }
        }

}
