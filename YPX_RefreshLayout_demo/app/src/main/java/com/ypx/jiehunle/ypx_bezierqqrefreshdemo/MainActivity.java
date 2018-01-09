package com.ypx.jiehunle.ypx_bezierqqrefreshdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXTabIndicator.YPXTabIndicator;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.GridViewFragment;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.ListViewFragment;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.NormalRefreshFragment;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.RecyclerViewFragment;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.ScrollViewFragment;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments.WebViewFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ScrollViewFragment scrollViewFragment;
    ListViewFragment listViewFragment;
    WebViewFragment webViewFragment;
    RecyclerViewFragment recyclerViewFragment;
    GridViewFragment gridViewFragment;
    NormalRefreshFragment normalRefreshFragment;
    private List<Fragment> mTabContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private String[] mDatas;
    private YPXTabIndicator mIndicator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setStyle();
    }

    private void initData() {
        mDatas = new String[]{"ScrollView", "ListView", "WebView", "RecyclerView", "GridView", "普通刷新"};
        mTabContents.clear();
        if (scrollViewFragment == null) {
            scrollViewFragment = new ScrollViewFragment();
        }
        mTabContents.add(scrollViewFragment);
        if (listViewFragment == null) {
            listViewFragment = new ListViewFragment();
        }
        mTabContents.add(listViewFragment);
        if (webViewFragment == null) {
            webViewFragment = new WebViewFragment();
        }
        mTabContents.add(webViewFragment);
        if (recyclerViewFragment == null) {
            recyclerViewFragment = new RecyclerViewFragment();
        }
        mTabContents.add(recyclerViewFragment);

        if (gridViewFragment == null) {
            gridViewFragment = new GridViewFragment();
        }
        mTabContents.add(gridViewFragment);

        if (normalRefreshFragment == null) {
            normalRefreshFragment = new NormalRefreshFragment();
        }
        mTabContents.add(normalRefreshFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_vp);
        mIndicator2 = (YPXTabIndicator) findViewById(R.id.netEaseIndicator2);
        mViewPager.setOffscreenPageLimit(6);
    }

    protected void setStyle() {
        mViewPager.setAdapter(mAdapter);
        mIndicator2.setViewPager(mViewPager, 0);
        mIndicator2.setTitles(mDatas);
        mIndicator2.setDefaultHeight(dp(44));//设置默认高度为30dp
        mIndicator2.setTabPadding(dp(10), 0, dp(10), dp(0));//设置tabPadding左右10dp
        mIndicator2.setBackgroundRadius(0);//设置外框半径25dp
        mIndicator2.setShowTabSizeChange(true);//显示字体大小切换效果
        mIndicator2.setShowBackground(false);//不显示背景
        mIndicator2.setShowIndicator(true);//显示指示器
        mIndicator2.setDeuceTabWidth(false);//不平分tab宽度，默认为平分
        mIndicator2.setTabTextSize(14);//设置tab默认字体大小
        mIndicator2.setTabMaxTextSize(16);//设置tab变换字体大小，如果setShowTabSizeChange设置false，则按默认字体大小
        mIndicator2.setTabPressColor(Color.RED);//设置tab选中后的字体颜色
        mIndicator2.setTabTextColor(Color.parseColor("#666666"));//设置未选中时字体颜色
        mIndicator2.setIndicatorColor(Color.RED);//设置指示器颜色为红色
        mIndicator2.setmBackgroundColor(Color.WHITE);//设置背景颜色为红色，如果setShowBackground为false则无背景
        mIndicator2.setBackgroundLineColor(Color.WHITE);//设置背景框颜色，如果setShowBackground为false则无背景框颜色
        mIndicator2.setBackgroundStrokeWidth(0);//设置背景框宽度\
        //mIndicator2.setTabWidth(ScreenUtils.getScreenWidth(this)/3);//强制tab宽度,优先级最高,设置了固定宽度后,不平分宽度设置就会无效
    }

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }

}
