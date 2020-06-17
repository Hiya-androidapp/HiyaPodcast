package com.xmum.hiyapodcast;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
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
import com.xmum.hiyapodcast.interfaces.IPlayerCallback;
import com.xmum.hiyapodcast.base.BaseApplication;
import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;


public class DetailActivity extends BaseActivity implements IAlbumDetailCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallback {    private static final String TAG ="" ;
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
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private String mCurrentTrackkTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.BLACK);

        initView();
        //album detail presenter
        mAlbumDetailPresenter=AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //player presenter
        mPlayerPresenter = PlayerPresenter.getsPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        updatePlayState(mPlayerPresenter.isPlaying());
        initListener();

    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //check if player has a play list
                    //todo:
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        //control player status
                        handlePlayControl();
                    }else {
                        handleNoPlayList();
                    }
                }
            }
        });
    }
    //when player has no play list, handle it
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //if is playing, then pause
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
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

        //play control icon
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);

    }
    private boolean mIsLoaderMore=false;
    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mDetailList=detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);
        //use recyclerview
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
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOverScrollBottomShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getsHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "Refresh successful!", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
               if( mAlbumDetailPresenter!=null)
                {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore=true;
                }
            }
        });
        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> trackList) {
        if(mIsLoaderMore&&mRefreshLayout!=null)
        {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore=false;
        }

        this.mCurrentTracks = trackList;
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
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "successfully load " + size + " audios", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no more audios", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

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


    private void updatePlayState(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing? R.drawable.selector_play_control_pause: R.drawable.selector_play_control_play);
            if(!playing)
            {
                mPlayControlTips.setText(R.string.click_play_tips_text);
            }else
            {
                if(!TextUtils.isEmpty(mCurrentTrackkTitle))
                {
                    mPlayControlTips.setText(mCurrentTrackkTitle);
                }
            }
        }
    }

    @Override
    public void onPlayStart() {
        //modify icon to pause, text modified to playing
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        //modify icon to playing, text modified to pause
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        //modify icon to playing, text modified to pause
        updatePlayState(false);
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if(track!=null)
        {
            mCurrentTrackkTitle = track.getTrackTitle();
            if(!TextUtils.isEmpty(mCurrentTrackkTitle)&&mPlayControlTips!=null)
            {
                mPlayControlTips.setText(mCurrentTrackkTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
