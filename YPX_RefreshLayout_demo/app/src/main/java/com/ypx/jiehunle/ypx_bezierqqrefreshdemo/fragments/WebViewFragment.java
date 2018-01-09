package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.refreshlayout.simple.qq.YPXQQRefreshView;

/**
 * WebView
 * Created by yangpeixing on 17/1/17.
 */
public class WebViewFragment extends Fragment{
    View view;
    WebView webView;
    YPXQQRefreshView refreshableView;
    final int SUCCESS = 1;
    final int FAILED = 0;
    @SuppressLint("HandlerLeak")
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
        view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_webview,null);
        initView();
        initData();
        return view;
    }

    private void initView() {
        refreshableView= (YPXQQRefreshView) view.findViewById(R.id.refreshableView1);
        webView= (WebView) view.findViewById(R.id.webView1);
        webView.loadUrl("http://blog.csdn.net/qq_16674697/article/details/54341455");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
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
