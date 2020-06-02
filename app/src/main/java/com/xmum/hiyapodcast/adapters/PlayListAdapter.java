package com.xmum.hiyapodcast.adapters;

import android.content.ContentUris;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.base.BaseApplication;
import com.xmum.hiyapodcast.views.SobPopWindow;

import java.util.ArrayList;
import java.util.List;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {
    private List<Track> mData = new ArrayList<>();
    private int playingIndex=0;
    private SobPopWindow.PlayListItemClickListener mItemClickListener=null;

    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(position);
                }
            }
        });
        //set data
        Track track=mData.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_rv);
        //set font color
        trackTitleTv.setTextColor(BaseApplication.getAppContex().getResources().
                getColor(playingIndex==position?R.color.second_color:R.color.play_list_text_color));

        trackTitleTv.setText(track.getTrackTitle());
        //找播放状态的图标
        View playingIconView=holder.itemView.findViewById((R.id.play_icon_iv));
        playingIconView.setVisibility(playingIndex==position?View.VISIBLE:View.GONE);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(List<Track> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        playingIndex=position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listener) {
        this.mItemClickListener=listener;
    }

    public class InnerHolder extends  RecyclerView.ViewHolder{
        public InnerHolder(View itemView){
            super(itemView);
        }
    }
}
