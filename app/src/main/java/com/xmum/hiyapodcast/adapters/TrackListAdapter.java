package com.xmum.hiyapodcast.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.xmum.hiyapodcast.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    //format time
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener = null;
    private ItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.InnerHolder holder, final int position) {
        //find controller, set data
        View itemView = holder.itemView;
        //order ID
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //title
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //play count
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        //time
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        //update date
        TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);

        //set data
        final Track track = mDetailData.get(position);
        orderTv.setText((position+1) + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount() + "");

        int durationMil = track.getDuration() * 1000;
        String duration = mDurationFormat.format(durationMil);
        durationTv.setText(duration);
        String updateTimeText = mSimpleDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeText);

        //set item click event
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "You click item " + position, Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    //need list and position
                    mItemClickListener.onItemClick(mDetailData,position);

                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> trackList) {
        //clear old data
        mDetailData.clear();
        //add new data
        mDetailData.addAll(trackList);
        //update ui
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder{
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(List<Track> detailData, int position);
    }
    public void setItemLongClickListener(ItemLongClickListener listener) {
        this.mItemLongClickListener = listener;
    }

    public interface ItemLongClickListener {
        void onItemLongClick(Track track);
    }
}
