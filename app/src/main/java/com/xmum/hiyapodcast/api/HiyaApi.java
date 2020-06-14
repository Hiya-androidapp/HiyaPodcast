package com.xmum.hiyapodcast.api;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.xmum.hiyapodcast.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class HiyaApi {

    private HiyaApi() {

    }

    private static HiyaApi sHiyaApi;
    public static HiyaApi getsHiyaApi() {
        if (sHiyaApi == null) {
            synchronized (HiyaApi.class) {
                if (sHiyaApi == null) {
                    sHiyaApi = new HiyaApi();
                }
            }
        }
        return sHiyaApi;
    }
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        //number of return
        map.put(DTransferConstants.LIKE_COUNT, Constant.COUNT_RECOMMAND +"");
        //get recommendation
        CommonRequest.getGuessLikeAlbum(map, callback);
    }

    //get detail based on album id
    public void getAlbumDetail(IDataCallBack<TrackList> callback, long albumId, int pageIndex) {
        Map<String, String> map = new HashMap<String, String>();

        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constant.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, callback);
    }

    //search based on keyword
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constant.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }
}
