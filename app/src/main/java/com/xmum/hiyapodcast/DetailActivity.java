package com.xmum.hiyapodcast;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.base.BaseActivity;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailCallback;
import com.xmum.hiyapodcast.interfaces.IAlbumDetailPresenter;
import com.xmum.hiyapodcast.presenters.AlbumDetailPresenter;
import com.xmum.hiyapodcast.utils.ImageBlur;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.xmum.hiyapodcast.views.RoundRectImageView;

import java.util.List;


public class DetailActivity extends BaseActivity implements IAlbumDetailCallback {
    private static final String TAG ="" ;
    private ImageView mLargeCover;
    private RoundRectImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
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
        mLargeCover=this.findViewById(R.id.iv_large_cover);
        mSmallCover=this.findViewById(R.id.viv_small_cover);
        mAlbumTitle=this.findViewById(R.id.tv_album_title);
        mAlbumAuthor= this.findViewById(R.id.tv_album_author);
    }

    @Override
    public void onDetailListLoaded(List<Track> trackList) {

    }

    @Override
    public void onAlbumLoaded(Album album) {
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
}
