package com.xmum.hiyapodcast.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.R;

import java.util.ArrayList;
import java.util.List;


public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {
    private List<Track> mData = new ArrayList<>();

    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //set data
        Track track=mData.get(position);
        TextView trackTitleTv = holder.itemView.findViewById(R.id.track_title_rv);
        trackTitleTv.setText(track.getTrackTitle());


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

    public class InnerHolder extends  RecyclerView.ViewHolder{
        public InnerHolder(View itemView){
            super(itemView);
        }
    }
}
