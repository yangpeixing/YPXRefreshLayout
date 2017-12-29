package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXQQRefresh.YPXQQRefreshView;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.adapters.ListViewAdapter;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;

/**
 * Created by yangpeixing on 17/1/17.
 */
public class ListViewFragment extends Fragment {
    View view;
    YPXQQRefreshView refreshableView;
    ListView listView;
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
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_listview,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView = (YPXQQRefreshView) view.findViewById(R.id.refreshableView1);
        listView= (ListView) view.findViewById(R.id.listView1);
        adapter=new ListViewAdapter(getActivity());
        listView.setAdapter(adapter);
        //设置是否可以刷新,默认可以刷新
        refreshableView.setRefreshEnabled(true);
        //设置刷新颜色,默认颜色值#999999
	/*	refreshableView.setRefreshColor(Color.parseColor("#26B8F2"));
		//设置刷新图标,默认刷新图标
		refreshableView.setRefreshIcon(R.mipmap.ic_launcher);
		//设置刷新球最大拉伸距离,默认为刷新头部高度
		refreshableView.setRefreshMaxHeight(refreshableView.dp(150));
		//设置刷新球半径,默认15dp
		refreshableView.setTopCircleRadius(refreshableView.dp(30));
		//设置刷新球圆心X值,默认屏宽一半
		refreshableView.setTopCircleX(refreshableView.dp(50));
		//设置刷新球圆心Y值,默认30dp
		refreshableView.setTopCircleY(refreshableView.dp(30));  */
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
