package com.xmum.hiyapodcast.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.xmum.hiyapodcast.R;
import com.xmum.hiyapodcast.adapters.RecommendListAdapter;
import com.xmum.hiyapodcast.base.BaseFragment;
import com.xmum.hiyapodcast.utils.Constant;
import com.xmum.hiyapodcast.utils.LogUtil;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment {

    private static final String TAG="RecommendFragment";
    private View mRootView;
    private  RecyclerView mRecommendRV;
    private  RecommendListAdapter mRecommendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
       //view loading complete
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //1.找到控件
        mRecommendRV=mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRV.setLayoutManager(linearLayoutManager);
        mRecommendRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom=UIUtil.dip2px(view.getContext(),5);
                outRect.left=UIUtil.dip2px(view.getContext(),5);
                outRect.right=UIUtil.dip2px(view.getContext(),5);
            }
        });
        //3.设置适配器
        mRecommendListAdapter=new RecommendListAdapter();
        mRecommendRV.setAdapter(mRecommendListAdapter);

       //get data
        getRecommendData();

        //return view
        return mRootView;
    }
    /*
     * get recommend content
     * */
    private void getRecommendData() {
        Map<String, String> map = new HashMap<>();
        //number of return
        map.put(DTransferConstants.LIKE_COUNT, Constant.RECOMMAND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>(){

            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
            // return success
                if(gussLikeAlbumList!=null)
                {
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    //update ui
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
            //return fail
                LogUtil.d(TAG,"error -->"+i);
                LogUtil.d(TAG,"errorMsg -->"+s);
            }
        });
    }

    private void upRecommendUI(List<Album> albumList) {
        //set data to the adapter and update
        mRecommendListAdapter.setData(albumList);
    }
}
