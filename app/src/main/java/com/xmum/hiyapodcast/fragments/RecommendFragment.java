package com.xmum.hiyapodcast.fragments;

import android.app.VoiceInteractor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.adapters.RecommendListAdapter;
import com.xmum.hiyapodcast.base.BaseFragment;
import com.xmum.hiyapodcast.utils.Constants;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

        //the use of RecyclerView
        //1. find the controller
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        //2. set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        //3. set adapter
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        //fetch data
        getRecommendData();
        return mRootView;
    }
//get recommend resource(guess what u like)
    private void getRecommendData() {
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                if(gussLikeAlbumList != null){
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    //after getting data, update UI
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG,"error -- >" + i);
                LogUtil.d(TAG,"errorMsg -- >" + s);
            }
        });


    }

    private void upRecommendUI(List<Album> albumList) {
        //set data to adapter, and update UI
        mRecommendListAdapter.setData(albumList);
    }
}