package com.xmum.hiyapodcast.fragments;

import android.app.VoiceInteractor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.base.BaseFragment;
import com.xmum.hiyapodcast.utils.Constants;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private static final String TAG = "RecommendFragment";

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

        getRecommendData();
        return rootView;
    }
//get recommend resource(guess what u like)
    private void getRecommendData() {
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if(gussLikeAlbumList!=null){
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    if(albumList!=null) {
                        LogUtil.d(TAG, "size -->" + albumList.size());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"error -->"+i);
                LogUtil.d(TAG,"errorMsg -->"+s);
            }
        });


    }
}
