package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.refreshlayout.simple.YPXNormalRefreshView;

/**
 * 作者：yangpeixing on 17/1/19 11:14
 * 博客主页：http://blog.csdn.net/qq_16674697?viewmode=list
 */
public class NormalRefreshFragment extends Fragment {
    YPXNormalRefreshView refreshableView;
    LinearLayout layout;
    final int SUCCESS = 1;
    final int FAILED = 0;
    View view;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    refreshableView.finishRefresh(true);
                    TextView textView = new TextView(getActivity());
                    textView.setTextColor(Color.parseColor("#666666"));
                    textView.setTextSize(18);
                    textView.setText("这是刷新的文本");
                    textView.setPadding(dp(15),dp(10),dp(15),dp(10));
                    layout.addView(textView,0);
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
        view= LayoutInflater.from(getContext()).inflate(R.layout.fragment_normalrefresh,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView = (YPXNormalRefreshView) view.findViewById(R.id.refreshableView1);
        layout = (LinearLayout) view.findViewById(R.id.ll_layout);
    }

    private void initData() {
        layout.removeAllViews();
        for (int i = 0; i < 50; i++) {
            final TextView textView = new TextView(getActivity());
            textView.setTextColor(Color.parseColor("#666666"));
            textView.setTextSize(18);
            textView.setPadding(dp(15),dp(10),dp(15),dp(10));
            textView.setText("这是第" + i + "个文本");
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),textView.getText(),Toast.LENGTH_SHORT).show();
                }
            });
            layout.addView(textView);
        }
        refreshableView.setRefreshListener(new YPXNormalRefreshView.RefreshListener() {

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

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,getResources().getDisplayMetrics());
    }
}
