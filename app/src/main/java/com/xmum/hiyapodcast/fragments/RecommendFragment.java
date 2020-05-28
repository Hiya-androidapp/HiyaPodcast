package com.xmum.hiyapodcast.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.xmum.hiyapodcast.interfaces.IRecommendViewCallBack;
import com.xmum.hiyapodcast.presenters.RecommendPresenter;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.xmum.hiyapodcast.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallBack, UILoader.OnRetryClickListener {

    private static final String TAG="RecommendFragment";
    private View mRootView;
    private  RecyclerView mRecommendRV;
    private  RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;
    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

         mUiLoader=new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //view loading complete
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //1.找到控件
        mRecommendRV=mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRV.setLayoutManager(linearLayoutManager);
        mRecommendRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //3.设置适配器
        mRecommendListAdapter=new RecommendListAdapter();
        mRecommendRV.setAdapter(mRecommendListAdapter);
        // get logical layer obj
        mRecommendPresenter = RecommendPresenter.getInstance();
        //register before using inteface
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
        //不允许重复绑定
        if(mUiLoader.getParent() instanceof  ViewGroup)
        {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);
        //return view
        return mRootView;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        return mRootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> albumList) {
        //当我们获取到推荐内容后这个方法被调用（成功）数据回来后更新UI
        mRecommendListAdapter.setData(albumList);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onLoadMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }
    public void onDestroyView()
    {
        super.onDestroyView();
        //dismiss the register of inteface
        if(mRecommendPresenter!=null)
        {
            mRecommendPresenter.unRegisterViewCallBack(this);
        }
    }


    @Override
    public void onRetryClick() {
        //when network error, user touch retry
        //we can just reobtain the data
        mRecommendPresenter.getRecommendList();
    }
}
