package com.xmum.hiyapodcast.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.xmum.hiyapodcast.DetailActivity;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.adapters.AlbumListAdapter;
import com.xmum.hiyapodcast.base.BaseFragment;
import com.xmum.hiyapodcast.interfaces.IRecommendViewCallBack;
import com.xmum.hiyapodcast.presenters.AlbumDetailPresenter;
import com.xmum.hiyapodcast.presenters.RecommendPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.xmum.hiyapodcast.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallBack, UILoader.OnRetryClickListener, AlbumListAdapter.OnRecommendItemClickListener {

    private static final String TAG="RecommendFragment";
    private View mRootView;
    private  RecyclerView mRecommendRv;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;
    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //get logic layer object
        mRecommendPresenter = RecommendPresenter.getInstance();
        //register before using inteface
        mRecommendPresenter.registerViewCallback(this);
        //get recommendation list
        mRecommendPresenter.getRecommendList();
        //不允许重复绑定
        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);
        //return view
        return mUiLoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view load finished
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //the use of RecycleView
        //1. find the controller
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
        //2. set layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3. set adapter
        mRecommendListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListner(this);
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
        LogUtil.d(TAG,"success");
    }
    public void onDestroyView()
    {
        super.onDestroyView();
        //dismiss the register of inteface
        if(mRecommendPresenter!=null)
        {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }


    @Override
    public void onRetryClick() {
        //when network error, user touch retry
        //we can just reobtain the data
        mRecommendPresenter.getRecommendList();
    }

    @Override
    public void onItemClick(int position,Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);

        //click item, jump to item
        Intent intent= new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
