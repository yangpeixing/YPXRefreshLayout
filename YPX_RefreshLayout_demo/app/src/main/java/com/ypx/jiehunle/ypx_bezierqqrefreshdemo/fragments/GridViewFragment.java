package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXQQRefresh.YPXQQRefreshView;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.adapters.ListViewAdapter;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;

/**
 * 作者：yangpeixing on 17/1/18 11:14
 * 博客主页：http://blog.csdn.net/qq_16674697?viewmode=list
 */
public class GridViewFragment extends Fragment{
    View view;
    YPXQQRefreshView refreshableView;
    GridView gridView;
        ListViewAdapter adapter;
    final int SUCCESS = 1;
    final int FAILED = 0;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    refreshableView.finishRefresh(true);
                    break;
                case FAILED:
                    refreshableView.finishRefresh(false);
                    break;
                default:
                    break;
            }
        };
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gridview,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView = (YPXQQRefreshView) view.findViewById(R.id.refreshableView1);
        gridView= (GridView) view.findViewById(R.id.gridView1);
        adapter=new ListViewAdapter(getActivity());
        gridView.setAdapter(adapter);
    }

    private void initData() {
        refreshableView.setRefreshListener(new YPXQQRefreshView.RefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        handler.sendEmptyMessage(FAILED);

                    }
                }, 500);
            }
        });
    }
}
