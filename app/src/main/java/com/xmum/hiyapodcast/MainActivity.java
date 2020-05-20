package com.xmum.hiyapodcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.xmum.hiyapodcast.adapters.IndicatorAdapter;
import com.xmum.hiyapodcast.utils.LogUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.support.v7.app.AppCompatActivity;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private  MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        Map<String, String> map = new HashMap<>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
                List<Category> categories = categoryList.getCategories();
                if (categories != null) {
                    int size = categories.size();
                    Log.d(TAG, "categories size --- < " + size);
                    for (Category category : categories) {
                       // Log.d(TAG, "category --- >" + category.getCategoryName());
                        LogUtil.d(TAG,"Category --> "+ category.getCategoryName());
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                //Log.e(TAG, "error code -- " + i + "error msg == > " + s);
                LogUtil.e(TAG, "error code -- " + i + "error msg == > " + s);
            }
        });
*/
        initView();
    }

    private void initView()
    {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getColor(R.color.main_color));
        //create indicator adapter
        IndicatorAdapter adapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(adapter);

        //viewpager
        mContentPager= this.findViewById(R.id.content_paper);

        //bind viewpage and indicator together
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);
    }
}
