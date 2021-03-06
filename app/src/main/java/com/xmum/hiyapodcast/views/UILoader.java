package com.xmum.hiyapodcast.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.utils.LogUtil;

public abstract class UILoader extends FrameLayout {
    private static final String TAG ="UILoder" ;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStatus=UIStatus.NONE;
    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private OnRetryClickListener mOnRetryClickListener = null;
    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }
    public void updateStatus(UIStatus status)
    {
        mCurrentStatus=status;
        // ui upatedate must in the main process
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }
    private void init() {
        switchUIByCurrentStatus ();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);

        }
        //根据状态设置是否可见
        LogUtil.d(TAG,"loading"+mCurrentStatus);
        mLoadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);

        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);


        //网路错误页面
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        mNetworkErrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);


        //数据为空的界面
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY ? VISIBLE : GONE);


    }

    protected View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    protected  View getNetworkErrorView(){
        View networkErrorView= LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,this,false);
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            //reobtain data
            public void onClick(View v) {
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return  networkErrorView;
    }

    //因为不知道返回什么所以是抽象类，其余都是固定页面
    protected abstract View getSuccessView(ViewGroup container);

    protected View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }
    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

    public interface OnRetryClickListener {
        void onRetryClick();
    }

}
