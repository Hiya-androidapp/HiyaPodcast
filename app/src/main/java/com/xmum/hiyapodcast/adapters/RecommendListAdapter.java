package com.xmum.hiyapodcast.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.xmum.hiyapodcast.R;

import java.util.ArrayList;
import java.util.List;

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {
    private  List<Album> mData=new ArrayList<>();
    private static String TAG="RecommendListAdapter";
    private OnRecommendItemClickListener mItemClickListner=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //set data
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mItemClickListner!=null)
                {
                    int ClickPosition=(int) v.getTag();
                    mItemClickListner.onItemClick(ClickPosition,mData.get(ClickPosition));
                }
                Log.d(TAG,"Click-->"+v.getTag());
            }
        });
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //return the number of displayed
        if(mData!=null)
        {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if(mData!=null)
        {
            mData.clear();
            mData.addAll(albumList);
            //update ui
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //find controller,set data
            //find each controller and set data
            //album cover
            ImageView albumCoverIv = (ImageView) itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //description
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //play count
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //content size
            TextView albumContentSizeTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() + "");
            albumContentSizeTv.setText(album.getIncludeTrackCount() + "");

            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverIv);

        }
    }
    public void setOnRecommendItemClickListner(OnRecommendItemClickListener listner)
    {
        this.mItemClickListner=listner;
    }
    public interface OnRecommendItemClickListener {
        void onItemClick(int position,Album album);
    }
}
