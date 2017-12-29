package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXQQRefresh.YPXQQRefreshView;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.adapters.RecyclerViewAdapter;

/**
 * Created by yangpeixing on 17/1/17.
 */
public class RecyclerViewFragment extends Fragment{
    View view;
    RecyclerView recyclerView;
    YPXQQRefreshView refreshableView;
    RecyclerViewAdapter adapter;
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
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_recyclerview,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView = (YPXQQRefreshView) view.findViewById(R.id.refreshableView1);
        recyclerView= (RecyclerView) view.findViewById(R.id.recyclerview1);
        adapter=new RecyclerViewAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        refreshableView.setRefreshListener(new YPXQQRefreshView.RefreshListener() {

            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        handler.sendEmptyMessage(SUCCESS);

                    }
                }, 500);
            }
        });
    }
}
