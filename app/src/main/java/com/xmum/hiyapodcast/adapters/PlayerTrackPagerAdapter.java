package com.xmum.hiyapodcast.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PlayerTrackPagerAdapter extends PagerAdapter {


    private List<Track> mData=new ArrayList<>();
    private static String TAG="PlayerTrackPagerAdapter";
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_check_pager, container, false);
        container.addView(itemView);

        //设置数据
        //找到控件
        ImageView item=itemView.findViewById(R.id.track_pager_item);
        //设置图片
        if(mData!=null)
        {
            Track track = mData.get(position);
            String coverUrlLarge = track.getCoverUrlLarge();

            if(!coverUrlLarge.isEmpty())
            {
                LogUtil.d(TAG,"URL-->"+coverUrlLarge);
                Picasso.with(container.getContext()).load(coverUrlLarge).into(item);

            }else
            {
                Picasso.with(container.getContext()).load(R.mipmap.aa).into(item);
            }


        }

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();

    }
}
