package com.xmum.hiyapodcast;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.adapters.DetailListAdapter;
import com.xmum.hiyapodcast.base.BaseActivity;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailCallback;
import com.xmum.hiyapodcast.presenters.AlbumDetailPresenter;
import com.xmum.hiyapodcast.presenters.PlayerPresenter;
import com.xmum.hiyapodcast.utils.ImageBlur;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.xmum.hiyapodcast.views.RoundRectImageView;
import com.xmum.hiyapodcast.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;


public class DetailActivity extends BaseActivity implements IAlbumDetailCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener {
    private static final String TAG ="" ;
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage=1;
    private RecyclerView mDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.BLACK);

        initView();

        mAlbumDetailPresenter=AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);

    }

    private void initView() {

        mDetailListContainer = this.findViewById(R.id.detail_list_container);
        //
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }
        mLargeCover=this.findViewById(R.id.iv_large_cover);
        mSmallCover=this.findViewById(R.id.viv_small_cover);
        mAlbumTitle=this.findViewById(R.id.tv_album_title);
        mAlbumAuthor= this.findViewById(R.id.tv_album_author);

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList=detailListView.findViewById(R.id.album_detail_list);
        //use recycleview
        //1.set layout controller
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mDetailList.setLayoutManager(layoutManager);
        //2.set adapter
        mDetailListAdapter = new DetailListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        //set item margin
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });

        mDetailListAdapter.setItemClickListener(this);
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> trackList) {
        //judge data result, control UI based on the result
        if (trackList == null || trackList.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //update/set data
        mDetailListAdapter.setData(trackList);

    }

    @Override
    public void onNetworkError(int i, String s) {
        //error happens, show network error status
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onAlbumLoaded(Album album) {
        //get detail content
        long id=album.getId();
        mCurrentId = id;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int)id,mCurrentPage);
        }
        //get data, show loading status
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        if(mAlbumTitle!=null)
        {
            mAlbumTitle.setText(album.getAlbumTitle());
        }
        if(mAlbumAuthor!=null)
        {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }
        //Frosted glass effect
        if(mLargeCover!=null)
        {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    LogUtil.d(TAG,"onError");
                }
            });
        }
        if(mSmallCover!=null)
        {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mSmallCover);
        }
    }

    @Override
    public void onRetryClick() {
        //click to reloading
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int)mCurrentId, mCurrentPage);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //set player data
        PlayerPresenter playerPresenter=PlayerPresenter.getsPlayerPresenter();
        playerPresenter.setPlayList(detailData,position);
        //TODO: jump to player interface
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }
}
