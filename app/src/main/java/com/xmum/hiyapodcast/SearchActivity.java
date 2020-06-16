package com.xmum.hiyapodcast;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.xmum.hiyapodcast.adapters.AlbumListAdapter;
import com.xmum.hiyapodcast.adapters.SearchRecommendAdapter;
import com.xmum.hiyapodcast.base.BaseActivity;
import com.xmum.hiyapodcast.interfaces.ISearchCallback;
import com.xmum.hiyapodcast.presenters.SearchPresenter;
import com.xmum.hiyapodcast.utils.LogUtil;
import com.xmum.hiyapodcast.views.FlowTextLayout;
import com.xmum.hiyapodcast.views.UILoader;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {

    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private FlowTextLayout mFlowTextLayout;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private InputMethodManager mImm;
    private AlbumListAdapter mAlbumListAdapter;
    private View mDelBtn;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mRecommendAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mImm=(InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //register the interface of ui update
        mSearchPresenter.registerViewCallback(this);
        //get hot words
        mSearchPresenter.getHotWord();
    }
    public void onDestroy(){
        super.onDestroy();
        if(mSearchPresenter!=null)
        {
            //destroy ui update interface
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter=null;
        }
    }
    private void initEvent() {

        if (mRecommendAdapter != null) {
            mRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    //执行搜索
                    Switch2Search(keyword);
                }
            });
        }

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputBox.setText("");
            }
        });
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Switch2Search(text);
            }
        });
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String keyword=mInputBox.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入框为空，显示重新显示热词
                if(TextUtils.isEmpty(s))
                {
                    mSearchPresenter.getHotWord();
                    mDelBtn.setVisibility(View.GONE);

                }else {
                    mDelBtn.setVisibility(View.VISIBLE);
                    //Trigger associative query
                    getSuggestWord(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
//            @Override
//            public void onItemClick(String text) {
//
//            }
//        });
    }

    private void Switch2Search(String text) {
        //set hot words into search box
        mInputBox.setText(text);
        mInputBox.setSelection(text.length());
        //go search
        if(mSearchPresenter!=null){
            mSearchPresenter.doSearch(text);
        }
        //change UI status
        if(mUILoader!=null){
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    //获取联想的关键词
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }


    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mDelBtn = this.findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);
        //进入搜索弹出输入法
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mImm.showSoftInput(mInputBox,InputMethodManager.SHOW_IMPLICIT);
            }
        },500);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);
       // mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
        if (mUILoader == null)
        {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if(mUILoader.getParent() instanceof ViewGroup)
            {
                ((ViewGroup)  mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);

        }


    }
    //创建数据请求成功的view
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        //show hot words
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        //2.set adapter
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //search recommendation
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        LinearLayoutManager layoutManager2=new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(layoutManager2);
        mRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mRecommendAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        hideSuccessView();
        mResultListView.setVisibility(View.VISIBLE);
        //HIDE KEYBOARD

        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        if(result!=null)
        {
            if(result.size()==0)
            {
                if(mUILoader !=null)
                {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            }else {
                //如果数据不为空
                mAlbumListAdapter.setData(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if(mUILoader!= null){
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG,"hotwordlist-->"+hotWordList.size());
        List<String> hotwords=new ArrayList<>();
        hotwords.clear();
        for(HotWord hotWord:hotWordList)
        {
            String searchWord=hotWord.getSearchword();
            hotwords.add(searchWord);
        }
        Collections.sort(hotwords);
        //update ui
        mFlowTextLayout.setTextContents(hotwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendMoreLoaded(List<QueryResult> keyWordList) {
        //联想相关关键字的联想词
        if (mRecommendAdapter != null) {
            mRecommendAdapter.setData(keyWordList);
        }
        //control the status of ui and hide display
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //control display and hide
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);

    }
    private void hideSuccessView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mResultListView.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }
    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }

    }
}
