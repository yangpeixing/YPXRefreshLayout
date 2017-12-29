package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXNormalRefresh;


import java.util.Calendar;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXRefreshBaseView;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.DateUtils;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.SPUtil;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.ScreenUtils;


/**
 * 刷新控制view
 *
 * @author yangpeixing
 */
public class YPXNormalRefreshView extends YPXRefreshBaseView {


	private View refreshView;

	//下拉刷新相关布局
	LinearLayout ll_ok;
	LinearLayout ll_refresh;
	ImageView iv_refresh, iv_ok;
	TextView tv_tip, tv_time, tv_ok;
	ProgressBar pb_refresh;
	/**
	 * 刷新时间
	 */
	Calendar LastRefreshTime;

	public YPXNormalRefreshView(Context context) {
		super(context);
	}

	public YPXNormalRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	protected View getRefreshHeaderView() {
		LastRefreshTime = Calendar.getInstance();
		refreshView = LayoutInflater.from(mContext).inflate(R.layout.layout_refresh_header, null);
		initRefreshView();
		return refreshView;
	}

	@Override
	protected void doMovement(LayoutParams lp, int lastTop) {
		if(anim.isRunning()||refreshState==REFRESHING){
			return;
		}
		float f1 = lp.topMargin;
		int i = (int) (f1 + lastTop * 0.4F);
		if (i >= refreshTargetTop) {//如果下拉大于-60dp的高度,动态刷新子视图
			lp.topMargin = i;
			refreshView.setLayoutParams(lp);
			refreshView.invalidate();
			invalidate();
		}

		if (lp.topMargin > 0) {//松开刷新状态
			if(refreshState!=REFRESH_BY_RELEASE) {
				pullUpToRefresh();
				setRefreshState(REFRESH_BY_RELEASE);
			}
		} else {//下拉刷新状态
			if(refreshState!=REFRESH_BY_PULLDOWN) {
				setRefreshState(REFRESH_BY_PULLDOWN);
				pullDownToRefresh();
			}

		}

	}

	@Override
	protected void fling(LayoutParams lp) {
//		if (lp.topMargin > 0) {//拉到了触发可刷新事件
//			refresh();
//		} else {//收回
//			animRefreshView(lp.topMargin,refreshTargetTop,300);
//		}
	}

	private void initRefreshView() {
		ll_ok = (LinearLayout) refreshView.findViewById(R.id.ll_ok);
		ll_refresh = (LinearLayout) refreshView.findViewById(R.id.ll_refresh);
		iv_refresh = (ImageView) refreshView.findViewById(R.id.iv_refresh);
		iv_ok = (ImageView) refreshView.findViewById(R.id.iv_ok);
		tv_tip = (TextView) refreshView.findViewById(R.id.tv_tip);
		tv_time = (TextView) refreshView.findViewById(R.id.tv_time);
		tv_ok = (TextView) refreshView.findViewById(R.id.tv_ok);
		pb_refresh = (ProgressBar) refreshView.findViewById(R.id.pb_refresh);
	}



	/**
	 * 下拉刷新状态
	 */
	@Override
	public void pullDownToRefresh() {
		setRefreshState(REFRESH_BY_PULLDOWN);
		ll_refresh.setVisibility(View.VISIBLE);
		ll_ok.setVisibility(View.GONE);
		tv_tip.setText("下拉刷新");
		getRefreshTime();
		RotateAnimation anim1 = new RotateAnimation(0, 180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim1.setDuration(300);
		anim1.setFillAfter(true);
		iv_refresh.clearAnimation();
		iv_refresh.startAnimation(anim1);
		pb_refresh.setVisibility(View.GONE);
		iv_refresh.setVisibility(View.VISIBLE);
		Log.i("下拉刷新","下拉刷新");
	}

	/**
	 * 松开刷新状态
	 */
	@Override
	public void pullUpToRefresh() {
		setRefreshState(REFRESH_BY_RELEASE);
		ll_refresh.setVisibility(View.VISIBLE);
		ll_ok.setVisibility(View.GONE);
		tv_tip.setText("松开刷新");
		getRefreshTime();
		iv_refresh.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.pull_up));
		RotateAnimation anim1 = new RotateAnimation(180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		anim1.setDuration(300);
		anim1.setFillAfter(true);
		iv_refresh.clearAnimation();
		iv_refresh.startAnimation(anim1);
		pb_refresh.setVisibility(View.GONE);
		iv_refresh.setVisibility(View.VISIBLE);
		Log.i("松开刷新", "松开刷新");
	}

	/**
	 * 正在刷新状态
	 */
	@Override
	public void refreshing() {
		setRefreshState(REFRESHING);
		ll_refresh.setVisibility(View.VISIBLE);
		ll_ok.setVisibility(View.GONE);
		tv_tip.setText("正在刷新...");
		getRefreshTime();
		SPUtil.getInstance(mContext).setRefreshTime("MyMobile", "" +
				DateUtils.getDate(DateUtils.MM_DD_HH_MM, System.currentTimeMillis()));
		iv_refresh.clearAnimation();
		iv_refresh.setVisibility(View.GONE);
		pb_refresh.setVisibility(View.VISIBLE);
	}

	/**
	 * 刷新成功状态
	 */
	@Override
	public void refreshOK() {
		setRefreshState(REFRESHING_SUCCESS);
		ll_refresh.setVisibility(View.GONE);
		ll_ok.setVisibility(View.VISIBLE);
		tv_ok.setText("刷新成功");
		iv_ok.setImageDrawable(getResources().getDrawable(R.mipmap.pull_ok));
	}

	/**
	 * 刷新失败状态
	 */
	@Override
	public void refreshFailed() {
		setRefreshState(REFRESHING_FAILD);
		ll_refresh.setVisibility(View.GONE);
		ll_ok.setVisibility(View.VISIBLE);
		tv_ok.setText("刷新失败");
		iv_ok.setImageDrawable(getResources().getDrawable(R.mipmap.pull_failure));
	}


	public void getRefreshTime(){
		String time = SPUtil.getInstance(mContext).getRefreshTime("MyMobile");
		if (time == null || "".equals(time)) {
			tv_time.setVisibility(View.GONE);
		} else {
			tv_time.setVisibility(View.VISIBLE);
			tv_time.setText("上次刷新:" + time);
		}
	}
}
