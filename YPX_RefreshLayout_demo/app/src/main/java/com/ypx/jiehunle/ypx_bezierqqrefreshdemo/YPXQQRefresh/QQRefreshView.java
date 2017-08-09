package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXQQRefresh;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.R;
import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.ScreenUtils;

/**
 * 作者：yangpeixing on 16/11/17 11:14
 * 博客主页：http://blog.csdn.net/qq_16674697?viewmode=list
 */
public class QQRefreshView extends LinearLayout {
    /**
     * 下拉刷新状态
     */
    public static final int REFRESH_BY_PULLDOWN = 0;
    /**
     * 松开刷新状态
     */
    public static final int REFRESH_BY_RELEASE = 1;

    /**
     * 正在刷新状态
     */
    public static final int REFRESHING = 2;
    /**
     * 刷新成功状态
     */
    public static final int REFRESHING_SUCCESS = 3;
    /**
     * 刷新失败状态
     */
    public static final int REFRESHING_FAILD = 4;

    /**
     * 收回到刷新位置状态
     */
    public static final int TAKEBACK_REFRESH = -1;
    /**
     * 收回到初始位置状态
     */
    public static final int TAKEBACK_RESET = -2;
    /**
     * 从头收到尾,不考虑中间状态
     */
    public static final int TAKEBACK_ALL = -3;

    private int refreshTargetTop=dp(-60);//刷新头部高度
    ObjectAnimator anim;

    //下拉刷新相关变量
    private View refreshView;
    LinearLayout ll_ok;
    LinearLayout ll_refresh;
    ImageView iv_ok;
    TextView tv_ok;
    ProgressBar pb_refresh;
    YPXBezierView bezierView;

    private RefreshListener refreshListener;
    private int lastY;
    private int lastTop;
    /**
     * 刷新状态
     */
    int refreshState = REFRESH_BY_PULLDOWN;
    /**
     * 收回状态
     */
    int takeBackState = TAKEBACK_RESET;
    /**
     * 是否可刷新标记
     */
    private boolean isRefreshEnabled = true;

    private float topCircleRadius;//默认上面圆形半径
    private float topCircleX;//默认上面圆形x
    private float topCircleY;//默认上面圆形y

    private int refreshMaxHeight;//刷新小球可滑动的最大距离

    boolean bezierLock = false;
    private Context mContext;

    public QQRefreshView(Context context) {
        this(context, null);

    }

    public QQRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        initRefreshView();
       // initLoadMoreView();
        anim = ObjectAnimator.ofFloat(refreshView, "ypx", 0.0f, 1.0f);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float cVal = (Float) valueAnimator.getAnimatedValue();
                LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
                switch (takeBackState) {
                    case TAKEBACK_REFRESH:
                        lp.height = lp.height + (int) (cVal * (-refreshTargetTop - lp.height));
                        lp.topMargin = 0;
                        break;
                    case TAKEBACK_RESET:
                        lp.topMargin = lp.topMargin + (int) (cVal * (refreshTargetTop - lp.topMargin));
                        lp.height = -refreshTargetTop;
                        break;
                    case TAKEBACK_ALL:
                        lp.topMargin = lp.topMargin + (int) (cVal * (refreshTargetTop - lp.topMargin));
                        lp.height = lp.height + (int) (cVal * (-refreshTargetTop - lp.height));
                        //bezierView.reset((float) Math.pow(cVal, 2 / 5.0));
                        break;
                }

                refreshView.setLayoutParams(lp);
                refreshView.invalidate();
                invalidate();
                if (lp.height == -refreshTargetTop
                        && lp.topMargin == refreshTargetTop) {//动画完成
                    resetRefreshView();
                }
            }
        });
    }

    private void initRefreshView() {
        //刷新视图顶端的的view
        refreshView = LayoutInflater.from(mContext).inflate(R.layout.layout_qqrefresh_header, null);
        ll_ok = (LinearLayout) refreshView.findViewById(R.id.ll_ok);
        ll_refresh = (LinearLayout) refreshView.findViewById(R.id.ll_refresh);
        bezierView = (YPXBezierView) refreshView.findViewById(R.id.bview);
        iv_ok = (ImageView) refreshView.findViewById(R.id.iv_ok);
        tv_ok = (TextView) refreshView.findViewById(R.id.tv_ok);
        pb_refresh = (ProgressBar) refreshView.findViewById(R.id.pb_refresh);
        LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, -refreshTargetTop);
        lp.topMargin = refreshTargetTop;
        addView(refreshView, lp);
        resetData();
        bezierView.setOnAnimResetListener(new YPXBezierView.OnAnimResetListener() {
            @Override
            public void onReset() {
                animRefreshView(200, TAKEBACK_REFRESH);
                if (refreshListener != null && refreshState == REFRESH_BY_RELEASE) {
                    refreshing();
                    refreshListener.onRefresh();
                    setRefreshState(REFRESHING);
                }
            }
        });
    }

    private  void resetData(){
        lastTop = refreshTargetTop;
        refreshMaxHeight=-refreshTargetTop;
        topCircleX=ScreenUtils.getScreenWidth(mContext)/2;
        topCircleY=-refreshTargetTop/2;
        topCircleRadius=-refreshTargetTop/4;
        bezierView.setTopCircleX(topCircleX);
        bezierView.setTopCircleY(topCircleY);
        bezierView.setTopCircleRadius(topCircleRadius);
        bezierView.setMaxHeight(refreshMaxHeight);
        bezierView.resetBottomCricle();
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
        //当前状态不是下拉刷新状态且刷新头为初始状态,此时可以认为是已经触发了刷新完成后离开手指
        if (refreshState != REFRESH_BY_PULLDOWN
                || (lp.topMargin == refreshTargetTop && lp.height == -refreshTargetTop)) {
            return;
        }
        bezierView.setTopCircleRadius(topCircleRadius);
        bezierView.resetBottomCricle();
        //没刷新收回状态
        animRefreshView(500, TAKEBACK_ALL);
    }


    /**
     * 下拉move事件处理
     *
     * @param moveY
     */
    private void doMovement(float moveY) {
        if ((refreshState != REFRESH_BY_RELEASE && refreshState != REFRESH_BY_PULLDOWN)
                ||anim.isRunning()) {
            return;
        }
        LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
        lastTop += moveY * 0.5;
        if (lastTop < 0) {
            lp.topMargin = lastTop;
            lp.height = -refreshTargetTop;
            setRefreshState(REFRESH_BY_PULLDOWN);
            pullDownToRefresh();
        } else {
            lp.topMargin = 0;
            lp.height = lastTop - refreshTargetTop;
            float offset = 1 - (lastTop * 1.0f) /refreshMaxHeight;//1~0
             if (offset < 0.2) {
                 if(offset<0){//lastTop>refreshMaxHeight
                     return;
                 }
                if (!bezierLock&&takeBackState!=TAKEBACK_ALL) {
                    bezierView.animToReset(bezierLock);
                    refreshState = REFRESH_BY_RELEASE;//松开刷新状态
                    bezierLock = true;
                }
            } else {
                bezierView.setBottomCircleY(bezierView.getTopCircleY() + (lastTop));
                bezierView.setBottomCircleRadius(bezierView.getDefaultRadius() * offset);

                bezierView.setOffset(offset);
                bezierView.setTopCircleRadius((float) (bezierView.getDefaultRadius() * (Math.pow(offset, 1 / 3.0))));
            }
            bezierView.postInvalidate();
        }
        refreshView.setLayoutParams(lp);
        refreshView.invalidate();
        invalidate();

    }

    /**
     * 结束刷新事件
     */
    public void finishRefresh(boolean isOK) {
        if (isOK) {
            refreshOK();
        } else {
            refreshFailed();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                animRefreshView(500, TAKEBACK_RESET);
            }
        }, 300);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isRefreshEnabled) {
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
     * @param duration
     */
    public void animRefreshView(int duration, int takeBackState) {
        this.takeBackState = takeBackState;
        if (!anim.isRunning()) {
            anim.start();
            anim.setDuration(duration);
        }
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
        pb_refresh.setVisibility(View.GONE);
        bezierView.setVisibility(View.VISIBLE);
    }


    /**
     * 正在刷新状态
     */
    public void refreshing() {
        setRefreshState(REFRESHING);
        ll_refresh.setVisibility(View.VISIBLE);
        ll_ok.setVisibility(View.GONE);
        bezierView.setVisibility(View.GONE);
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

    /**
     * 初始化
     */
    public void resetRefreshView() {
        Log.i("i", "resetRefreshView: 页面重置");
        bezierLock = false;
        lastTop = refreshTargetTop;
        takeBackState=TAKEBACK_RESET;
        LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
        lp.height = -refreshTargetTop;
        refreshView.setLayoutParams(lp);
        refreshView.invalidate();
        bezierView.setTopCircleX(topCircleX);
        bezierView.setTopCircleY(topCircleY);
        bezierView.setTopCircleRadius(topCircleRadius);
        bezierView.setMaxHeight(refreshMaxHeight);
        bezierView.resetBottomCricle();
        pullDownToRefresh();
    }

    public int dp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getContext().getResources().getDisplayMetrics());
    }

    /**
     * 设置是否可以刷新
     *
     * @param b
     */
    public void setRefreshEnabled(boolean b) {
        this.isRefreshEnabled = b;
    }

    /**
     * 设置刷新回调
     *
     * @param listener
     */
    public void setRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }

    /**
     * 获取当前刷新状态
     *
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

    public float getTopCircleY() {
        return topCircleY;
    }

    public void setTopCircleY(float topCircleY) {
        this.topCircleY = topCircleY;
        bezierView.setTopCircleY(topCircleY);
        bezierView.resetBottomCricle();
    }

    public float getTopCircleX() {
        return topCircleX;

    }

    public void setTopCircleX(float topCircleX) {
        this.topCircleX = topCircleX;
        bezierView.setTopCircleX(topCircleX);
        bezierView.resetBottomCricle();
    }

    public float getTopCircleRadius() {
        return topCircleRadius;
    }

    public void setTopCircleRadius(float topCircleRadius) {
        this.topCircleRadius = topCircleRadius;
        bezierView.setTopCircleRadius(topCircleRadius);
        bezierView.resetBottomCricle();
    }

    public int getRefreshMaxHeight() {
        return refreshMaxHeight;
    }

    public void setRefreshMaxHeight(int refreshMaxHeight) {
        this.refreshMaxHeight = refreshMaxHeight;
        bezierView.setMaxHeight(refreshMaxHeight);
    }

    public void setRefreshIcon(int drawableID){
        bezierView.setDrawableID(drawableID);
    }

    public void setRefreshColor(int color){
        bezierView.setColor(color);
    }

    public void setRefreshViewHeight(int height){
       this.refreshTargetTop=-height;
        LayoutParams lp = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, this.refreshTargetTop);
        refreshView.setLayoutParams(lp);
        resetData();
    }
}


