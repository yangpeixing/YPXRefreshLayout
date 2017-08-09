package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXQQRefresh;


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
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.DateUtils;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.SPUtil;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.ScreenUtils;


/**
 * 刷新控制view
 *
 * @author yangpeixing
 */
public class YPXRefreshView extends LinearLayout {
	/**
	 * 下拉刷新状态
	 */
	public static final int REFRESH_BY_PULLDOWN=0;

	/**
	 * 松开刷新状态
	 */
	public static final int REFRESH_BY_RELEASE=1;
	/**
	 * 正在刷新状态
	 */
	public static final int REFRESHING=2;
	/**
	 * 刷新成功状态
	 */
	public static final int REFRESHING_SUCCESS=3;
	/**
	 * 刷新失败状态
	 */
	public static final int REFRESHING_FAILD=4;

	private View refreshView;
	private int refreshTargetTop;
	ObjectAnimator anim;

	//下拉刷新相关布局
	LinearLayout ll_ok;
	LinearLayout ll_refresh;
	ImageView iv_refresh, iv_ok;
	TextView tv_tip, tv_time, tv_ok;
	ProgressBar pb_refresh;

	private RefreshListener refreshListener;
	private int lastY;
	// 是否可刷新标记
	private boolean isRefreshEnabled = true;
	/**
	 * 刷新时间
	 */
	Calendar LastRefreshTime;

	int refreshState=REFRESH_BY_PULLDOWN;

	private Context mContext;

	public YPXRefreshView(Context context) {
		this(context,null);

	}

	public YPXRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	private void init() {
		//滑动对象，
		LastRefreshTime = Calendar.getInstance();
		//刷新视图顶端的的view
		refreshView = LayoutInflater.from(mContext).inflate(R.layout.layout_refresh_header, null);
		initRefreshView();
		refreshTargetTop =-ScreenUtils.dpToPx(getResources(),60);
		LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		anim = ObjectAnimator.ofFloat(refreshView, "ypx", 0.0f, 1.0f);

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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getRawY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//记录下y坐标
				lastY = y;
				break;

			case MotionEvent.ACTION_MOVE:
				//y移动坐标
				int m = y - lastY;
				doMovement(m);
				//记录下此刻y坐标
				this.lastY = y;
				break;

			case MotionEvent.ACTION_UP:
				fling();
				break;
		}
		return true;
	}


	/**
	 * up事件处理
	 */
	private void fling() {
		LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		if (lp.topMargin > 0) {//拉到了触发可刷新事件
			refresh();
		} else {//收回
			animRefreshView(lp.topMargin,refreshTargetTop,300);
		}
	}


	private void refresh() {
		LayoutParams lp = (LayoutParams) this.refreshView.getLayoutParams();
		int i = lp.topMargin;
		animRefreshView(i,0,200);
		refreshing();
		if (refreshListener != null) {
			refreshListener.onRefresh();
			setRefreshState(REFRESHING);

		}
	}

	/**
	 * 下拉move事件处理
	 *
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		if(anim.isRunning()||refreshState==REFRESHING){
			return;
		}
		LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		float f1 = lp.topMargin;
		int i = (int) (f1 + moveY * 0.4F);
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

	/**
	 * 设置是否可以刷新
	 * @param b
	 */
	public void setRefreshEnabled(boolean b) {
		this.isRefreshEnabled = b;
	}

	/**
	 * 设置刷新回调
	 * @param listener
	 */
	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * 获取当前刷新状态
	 * @return
	 */
	public int getRefreshState() {
		return refreshState;
	}

	/**
	 * 设置当前刷新状态
	 *
	 * @param refreshState
	 */
	public void setRefreshState(int refreshState) {
		this.refreshState = refreshState;
	}


	/**
	 * 结束刷新事件
	 */
	public void finishRefresh(boolean isOK) {
		LayoutParams lp = (LayoutParams) this.refreshView.getLayoutParams();
		final int i = lp.topMargin;
		if (isOK) {
			refreshOK();
		} else {
			refreshFailed();
		}
		if(!anim.isRunning()&&refreshState!=REFRESHING){
			new Handler().postDelayed(new Runnable(){
				public void run() {
					animRefreshView(i,refreshTargetTop,500);
				}
			}, 300);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		if(!isRefreshEnabled){
			return false;
		}
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				lastY = y;
				break;

			case MotionEvent.ACTION_MOVE:
				if (y > lastY && canScroll()) {
					return true;
				}
				//记录下此刻y坐标
				this.lastY = y;
				break;
		}
		return false;
	}

	private boolean canScroll() {
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3 &&
						((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}else if (childView instanceof WebView) {
				if (((WebView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}else if (childView instanceof GridView) {
				int top = ((GridView) childView).getChildAt(0).getTop();
				int pad = ((GridView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3 &&
						((GridView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			}else if (childView instanceof RecyclerView) {
				RecyclerView.LayoutManager manager=((RecyclerView)childView).getLayoutManager();
				int top=0;
				if(manager instanceof LinearLayoutManager){
					top = ((LinearLayoutManager)manager).findFirstVisibleItemPosition();
				}else  if(manager instanceof StaggeredGridLayoutManager){
					top = ((StaggeredGridLayoutManager)manager).findFirstVisibleItemPositions(null)[0];
				}

				if(((RecyclerView)childView).getChildAt(0).getY()==0 &&top==0){
					return true;
				} else {
					return false;
				}

			}

		}
		return false;
	}

	/**
	 * 从开始位置滑动到结束位置
	 *
	 * @param startHeight
	 * @param endHeight
	 */
	public void animRefreshView(final int startHeight,final int endHeight,int duration){
		anim.start();
		anim.setDuration(duration);
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation){
				float cVal = (Float) animation.getAnimatedValue();
				LayoutParams lp = (LayoutParams)refreshView.getLayoutParams();
				int k =startHeight+(int)(cVal*(endHeight-startHeight));
				lp.topMargin = k;
				refreshView.setLayoutParams(lp);
				refreshView.invalidate();
				invalidate();
			}
		});

	}

	/**
	 * 刷新监听接口
	 *
	 * @author Nono
	 */
	public interface RefreshListener {
		 void onRefresh();
	}

	/**
	 * 下拉刷新状态
	 */
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
