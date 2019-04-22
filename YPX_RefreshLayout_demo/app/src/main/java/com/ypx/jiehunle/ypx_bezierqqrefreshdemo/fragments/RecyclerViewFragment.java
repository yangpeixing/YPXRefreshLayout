package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.adapters.RecyclerViewAdapter;
import com.ypx.refreshlayout.simple.qq.YPXQQRefreshView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView
 * Created by yangpeixing on 17/1/17.
 */
public class RecyclerViewFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    YPXQQRefreshView refreshableView;
    TextView mTvLoadMore;
    RecyclerViewAdapter adapter;
    final int SUCCESS = 1;
    final int FAILED = 0;

    List<String> list = new ArrayList<>();
    int page = 1;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    adapter.notifyDataSetChanged();
                    refreshableView.finishRefresh(true);
                    mTvLoadMore.setVisibility(View.INVISIBLE);
                    break;
                case FAILED:
                    adapter.notifyDataSetChanged();
                    refreshableView.finishRefresh(false);
                    mTvLoadMore.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recyclerview, null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView = (YPXQQRefreshView) view.findViewById(R.id.refreshableView1);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview1);
        adapter = new RecyclerViewAdapter(getActivity(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        mTvLoadMore = (TextView) view.findViewById(R.id.mTvLoadMore);
        refreshableView.setRefreshEnabled(true);
        recyclerView.setClipToPadding(false);
        recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(),
                recyclerView.getPaddingBottom() + dp(40));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    // mTvLoadMore.setTranslationY(-mTvLoadMore.getHeight());
                    mTvLoadMore.setVisibility(View.VISIBLE);
                    if (page == 5) {
                        mTvLoadMore.setText("没有更多数据了!");
                    } else {
                        mTvLoadMore.setText("正在加载...");
                        loadMore();
                    }
                } else {
                    Log.e("onScrolled", "onScrolled: " + getScrollY());
                    if (dy < 0 && mTvLoadMore.getVisibility() == View.VISIBLE) {
                        mTvLoadMore.setTranslationY(dy);
                        if (dy > dp(40)) {
                            mTvLoadMore.setVisibility(View.INVISIBLE);
                        }
                    }
                    //mTvLoadMore.setVisibility(View.INVISIBLE);
                }


            }
        });

        refreshData();
    }

    private int getScrollY() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        View view = layoutManager.findViewByPosition(list.size() - 1);
        if (view != null) {
            return view.getBottom();
        }
        return layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition()).getBottom();
    }

    private void initData() {
        refreshableView.setRefreshListener(new YPXQQRefreshView.RefreshListener() {

            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void refreshData() {
        list.clear();
        page = 1;
        for (int i = 0; i < 50; i++) {
            list.add(i + "");
        }
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                handler.sendEmptyMessage(SUCCESS);

            }
        }, 500);
    }

    private void loadMore() {
        int start = list.size();
        for (int i = 0; i < 20; i++) {
            list.add(start + i + "");
        }
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                handler.sendEmptyMessage(SUCCESS);

            }
        }, 1000);
        page++;
    }

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics());
    }
}
