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
import com.xmum.hiyapodcast.Interface.IRecommendViewCallBack;
import com.xmum.hiyapodcast.Presenters.RecommendPresenter;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.adapters.RecommendListAdapter;
import com.xmum.hiyapodcast.base.BaseFragment;
import com.xmum.hiyapodcast.utils.Constants;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallBack {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;




    public final class UIUtil{
        public UIUtil(){

        }
        public static int dip2px(Context context, double dpValue){
            float density = context.getResources().getDisplyaMetrics().density;
            return (int)(dpValue*(double)density+0.50);
        }

        public static int getScreenWidth(Context context){
            return Context.getResources().getDisplyaMetrics().widthPixels;
        }
    }

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
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration(){
            @Override
            public void getItemOffsets(Rect outRect, View view, recyclerview parent, RecyclerView.State state){
                outRect.top=UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom=UIUtil.dip2px(view.getContext(), 5);
                outRect.left=UIUtil.dip2px(view.getContext(), 5);
                outRect.right=UIUtil.dip2px(view.getContext(), 5);
                //super.getItemOffsets(outRect, view, parent, state);
            }
        });
        //3. set adapter
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        //fetch object to logic layer
        getRecommendData();

        mRecommendPresenter = RecommendPresenter.getInstance();
        //先设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this)
        //getRecommendList
        mRecommendPresenter.getRecommendList();
        return mRootView;
    }

}



    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //获取推荐内容的时候这个方法就会被调用（成功）
        //数据回来以后就是更新ui
        // set data to adapter, and update UI
        mRecommendListAdapter.setData(albumList);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    public void onDestroyView(){
        super.onDestroyView();//取消接口的注册
        if (mRecommendPresenter!=null) {
            mRecommendPresenter.unRegisterViewCallBack(this);
        }
    }
