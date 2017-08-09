
package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXNetEaseIndicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util.ScreenUtils;

/**
 * 仿网易圆形移动viewpager指示器
 *
 * 博客地址:http://blog.csdn.net/qq_16674697/article/details/51954228
 *
 * @author yangpeixing
 */
public class NetEaseIndicator extends LinearLayout implements
        OnPageChangeListener {
    private int screenWidth = 0;
    /**
     * 内部椭圆背景paint
     */
    private Paint indicatorPaint;
    /**
     * 外围背景线条宽度，默认为4px
     */
    private int strokeWidth = 4;

    /**
     * 背景色,，默认为红色#ff0000
     */
    private int backgroundColor = Color.RED;
    /**
     * 背景线条色
     */
    private int backgroundLineColor = Color.WHITE;
    /**
     * 指示器颜色
     */
    private int indicatorColor = Color.WHITE;

    /**
     * 背景和指示器半径，默认为40px
     */
    private int backgroundRadius = 40;
    /**
     * 记录tab宽度数组
     */
    private int[] tabLengthArray;

    /**
     * x轴偏移量
     */
    private int mTransitX = 0;
    /**
     * tab默认大小为12
     */
    private int tabTextSize = 12;
    /**
     * 设置tab默认颜色，既未选中时颜色
     */
    private int tabTextColor = Color.WHITE;
    /**
     * tab选中后颜色,默认和背景色一致
     */
    private int tabPressColor = backgroundColor;

    private int mTabWidth = 0;
    private String[] titles;
    private ViewPager viewPager;
    /**
     * 默认高度，用户可自己设置高度，默认50px
     */
    private int defaultHeight = 50;
    /**
     * viewpager坐标
     */
    private int mCurrentIndex = 0;
    /**
     * 默认选中第几个tab
     */
    private int mInitIndex = 0;
    /**
     * 判断是否点击
     */
    private boolean isClick = false;

    /**
     * 是否设置背景
     */
    private boolean isShowBackground = true;
    /**
     * 是否设置指示器
     */
    private boolean isShowIndicator = true;
    /**
     * 是否设置tab字体变换
     */
    private boolean isShowTabSizeChange = true;
    /**
     * 是否平分tab的宽度，若false，则返回每个tab自适应的宽度，适合在多种数目下使用
     */
    private boolean isDeuceTabWidth = true;
    /**
     * 可见tab数量
     */
    private int visiableCounts = -1;
    /**
     * 最大字体大小，默认为14
     */
    private int maxTabTextSize = 14;
    /**
     * tab总数
     */
    private int totalCount = 0;
    /**
     * 是否手动设置了tab的宽度
     */
    private boolean isSetTabWidth=false;
    /**
     * 是否是HorizonScrollView的子View
     */
    private boolean isChildOfHorizontalScrollView = false;

    private int tabPaddingLeft = 0, tabPaddingRight = 0, tabPaddingTop = 0,
            tabPaddingBottom = 0;

    public NetEaseIndicator(Context context) {
        this(context, null);
    }

    public NetEaseIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NetEaseIndicator(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initView();

    }

    private void initView() {
        mCurrentIndex = 0;
        mInitIndex = 0;
        mTabWidth = 0;
        mTransitX = 0;
        titles = new String[]{"tab1", "tab2", "tab3"};
        tabLengthArray = new int[titles.length];
        screenWidth = ScreenUtils.getScreenWidth(getContext());
        setBackgroundShape();
        initPaints();
        setTabViews();
    }

    /**
     * 设置背景
     */
    private void setBackgroundShape() {
        // 创建drawable
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(backgroundColor);
        gd.setCornerRadius(backgroundRadius);
        gd.setStroke(strokeWidth, backgroundLineColor);
        if (isShowBackground) {
            setBackground(gd);
        } else {
            setBackgroundResource(0);
        }
    }

    /**
     * 初始化指示器画笔
     */
    private void initPaints() {
        indicatorPaint = new Paint();
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStyle(Style.FILL);
    }

    /**
     * 获取tab
     */
    private void setTabViews() {
        tabLengthArray = new int[titles.length];
        removeAllViews();
        for (int i = 0; i < titles.length; i++) {
            addView(creatDefaultTab(titles[i], i));
        }
        calculateSize();
        setItemClickEvent();
    }

    /**
     * 根据子textview是否平分宽度来测量可见数目
     */
    private void calculateSize() {
        totalCount = titles.length;
        if (isDeuceTabWidth) {// 如果平分tab宽度，则直接用屏幕宽除tab宽
            visiableCounts = screenWidth / mTabWidth;
        } else {// 如果不平分
            visiableCounts = getDefaultVisiableCount();
        }
    }

    /**
     * 获取屏幕中显示的tab个数
     *
     * @return
     */
    private int getDefaultVisiableCount() {
        int defaultNum = 0;
        for (int i = 0; i < tabLengthArray.length; i++) {
            defaultNum += tabLengthArray[i];
            if (defaultNum >= screenWidth) {
                return i;
            }
        }
        return screenWidth / mTabWidth;
    }

    /**
     * 获取position前几项tab宽度之和
     *
     * @return
     */
    private int getTransitXByPosition(int posotion) {
        int defaultNum = 0;
        for (int i = 0; i < posotion; i++) {
            defaultNum += tabLengthArray[i];
        }
        return defaultNum;
    }

    /**
     * 创建默认tab（Textview）
     *
     * @param string 要显示的文本
     * @param i  坐标
     */
    private TextView creatDefaultTab(String string, int i) {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(tabTextColor);
        textView.setTextSize(tabTextSize);
        textView.setText(string);
        textView.setPadding(tabPaddingLeft, tabPaddingTop, tabPaddingRight,
                tabPaddingBottom);
        TextPaint mTextPaint;
        if (isShowTabSizeChange) {//设置是否字体变换
            TextView dTextView = new TextView(getContext());
            dTextView.setTextSize(maxTabTextSize);
            mTextPaint = dTextView.getPaint();//得到最大尺寸textview的Paint，用于测量宽度
        } else {
            mTextPaint = textView.getPaint();
        }
        if(!isSetTabWidth) {
            mTabWidth = (int) mTextPaint
                    .measureText(isDeuceTabWidth ? getMaxLengthString(titles)
                            : string)
                    + tabPaddingLeft + tabPaddingRight;
        }
        tabLengthArray[i] = mTabWidth;
        textView.setLayoutParams(new LinearLayout.LayoutParams(mTabWidth,
                defaultHeight + tabPaddingBottom + tabPaddingTop));
        return textView;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        defaultHeight = getMeasuredHeight();
        if (mCurrentIndex == 0) {
            mTabWidth = tabLengthArray[0];
        }
        int left = mTransitX + mInitIndex * mTabWidth;// tab左边距离原点的位置
        int right = mTabWidth + left;// 整个tab的位置
        int top = 0;// tab距离顶端的位置
        int bottom = defaultHeight;// 整个tab的高度
        if (isShowIndicator) {
            if (creator != null) {
                creator.drawIndicator(canvas, left, top, right, bottom,
                        indicatorPaint, backgroundRadius);
            } else {
                drawIndicatorWithTransitX(canvas, left, top, right, bottom,
                        indicatorPaint);
            }
        }
        if (mInitIndex != 0) {
            (getTab(mInitIndex)).setTextColor(backgroundColor);
            int centerX = getTransitXByPosition(mInitIndex)
                    - (screenWidth - tabLengthArray[mInitIndex]) / 2;
            parentScrollto(centerX, 0);
        }
        mInitIndex = 0;// 清除第一次默认index
        super.dispatchDraw(canvas);
    }

    /**
     * 默认为圆角矩形指示器，用户可继承重写自定义指示器样式
     *
     * @param canvas
     * @param left   tab左边距离原点的位置
     * @param top    整个tab的位置
     * @param right  tab距离顶端的位置
     * @param bottom 整个tab的高度,既控件高度
     * @param paint  指示器画笔
     */
    public void drawIndicatorWithTransitX(Canvas canvas, int left, int top,
                                          int right, int bottom, Paint paint) {
        if (backgroundRadius < defaultHeight / 2) {
            // 真机运行用这种方式，模拟器圆角会失真
            RectF oval = new RectF(left, top, right, bottom);// 设置个新的长方形，扫描测量
            canvas.drawRoundRect(oval, backgroundRadius, backgroundRadius,
                    paint);
        } else {// 画三段代替圆角矩形，既圆、矩形、圆
            RectF oval2 = new RectF(bottom / 2 + left, top, right - bottom / 2,
                    bottom);
            canvas.drawCircle(oval2.left, bottom / 2, bottom / 2,
                    indicatorPaint);
            canvas.drawRect(oval2, indicatorPaint);
            canvas.drawCircle(oval2.right, bottom / 2, bottom / 2, paint);
        }
    }

    public void setViewPager(ViewPager viewPager, int index) {
        this.viewPager = viewPager;
        this.mCurrentIndex = index;
        mInitIndex = index;
        setItemClickEvent();
        if (getParent() != null
                && (getParent() instanceof HorizontalScrollView)) {
            isChildOfHorizontalScrollView = true;
        }
        viewPager.setOnPageChangeListener(this);
        setmCurrentIndex(index);
    }

    /**
     * 如果父控件是HorizonScrollView，则控制父控件移动
     *
     * @param x
     * @param y
     */
    public void parentScrollto(int x, int y) {
        if (isChildOfHorizontalScrollView) {
            ((HorizontalScrollView) getParent()).smoothScrollTo(x, y);
        }
    }

    /**
     * 设置颜色变换
     *
     * @param position
     * @param positionOffset
     */
    protected void setTabColorChange(int position, float positionOffset) {
        getTab(position).setTextColor(
                blendColors(tabPressColor, tabTextColor, positionOffset));

    }

    /**
     * 设置字体大小变换
     *
     * @param position
     * @param positionOffset
     */
    protected void setTabSizeChange(int position, float positionOffset) {
        getTab(position).setTextSize(
                blendSize(tabTextSize, maxTabTextSize, positionOffset));
    }

    /**
     * 设置点击事件
     */
    public void setItemClickEvent() {
        for (int i = 0; i < totalCount; i++) {
            final int j = i;
            getTab(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isClick = true;
                    if (viewPager != null && viewPager.getAdapter() != null)
                        viewPager.setCurrentItem(j);
                }
            });
        }
    }

    /**
     * 两个大小渐变
     *
     * @param minSize
     * @param maxSize
     * @param ratio
     * @return
     */
    private float blendSize(int minSize, int maxSize, float ratio) {
        return (minSize + (maxSize - minSize) * ratio * 1.0f);
    }

    /**
     * 两个颜色渐变转化
     *
     * @param color1
     * @param color2
     * @param ratio
     * @return
     */
    private int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio)
                + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio)
                + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio)
                + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public String[] getTitles() {
        return titles;
    }

    /**
     * 设置指示器内容,默认tab1，tab2，tab3
     *
     * @param titles
     */
    public void setTitles(String[] titles) {
        if (titles != null && titles.length > 0) {
            this.titles = titles;
            setTabViews();
            setItemClickEvent();
        }
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    /**
     * 设置tab大小，默认大小为12
     *
     * @param tabTextSize
     */
    public void setTabTextSize(int tabTextSize) {
        this.tabTextSize = tabTextSize;
        resetTabSize();
    }

    /**
     * 设置指示器高度，默认50px
     *
     * @param defaultHeight
     */
    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
        setTabViews();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置外框宽度，默认为4px
     *
     * @param strokeWidth
     */
    public void setBackgroundStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        setBackgroundShape();
    }

    /**
     * 获取tab
     *
     * @param i
     * @return
     */
    public TextView getTab(int i) {
        if (i < titles.length) {
            return (TextView) getChildAt(i);
        } else {
            return (TextView) getChildAt(titles.length - 1);
        }
    }

    /**
     * 重置tab颜色
     */
    public void resetTabColor() {
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextColor(tabTextColor);
        }
    }

    /**
     * 重置tab大小
     */
    public void resetTabSize() {
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextSize(tabTextSize);
        }
    }

    public int getTabTextColor() {
        return tabTextColor;
    }

    /**
     * 设置tab默认颜色，既未选中时颜色，默认为白色
     *
     * @param tabTextColor
     */
    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
        for (int i = 0; i < totalCount; i++) {
            getTab(i).setTextColor(tabTextColor);
        }
    }

    public int getmBackgroundColor() {
        return backgroundColor;
    }

    /**
     * 设置背景色,同时也是选中tab的文本颜色，默认为红色
     *
     * @param backgroundColor
     */
    public void setmBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        setBackgroundShape();
    }

    public int getBackgroundRadius() {
        return backgroundRadius;
    }

    /**
     * 设置背景圆角大小，默认为40px
     *
     * @param backgroundRadius
     */
    public void setBackgroundRadius(int backgroundRadius) {
        this.backgroundRadius = backgroundRadius;
        setBackgroundShape();
    }

    /**
     * 设置外框颜色,默认为白色
     *
     * @param backgroundLineColor
     */
    public void setBackgroundLineColor(int backgroundLineColor) {
        this.backgroundLineColor = backgroundLineColor;
        setBackgroundShape();
    }

    /**
     * 获取当前viewpager选中项
     *
     * @return
     */
    public int getmCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 设置默认项
     *
     * @param mCurrentIndex
     */
    private void setmCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.setCurrentItem(mCurrentIndex, true);
        }

    }

    public boolean isShowBackground() {
        return isShowBackground;
    }

    /**
     * 是否显示背景
     *
     * @param isShowBackground
     */
    public void setShowBackground(boolean isShowBackground) {
        this.isShowBackground = isShowBackground;
        setBackgroundShape();
    }

    public boolean isShowIndicator() {
        return isShowIndicator;
    }

    /**
     * 是否显示指示器
     *
     * @param isShowIndicator
     */
    public void setShowIndicator(boolean isShowIndicator) {
        this.isShowIndicator = isShowIndicator;
        invalidate();
    }

    public int getIndicatorColor() {
        return indicatorColor;
    }

    /**
     * 设置指示器颜色，默认为白色
     *
     * @param indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        indicatorPaint.setColor(indicatorColor);
        invalidate();
    }

    /**
     * 设置最大字体大小,默认为14
     *
     * @param maxTabTextSize
     */
    public void setTabMaxTextSize(int maxTabTextSize) {
        this.maxTabTextSize = maxTabTextSize;
        setTabViews();
    }

    public int getVisiableCounts() {
        return visiableCounts;
    }

    public int getTabWidth() {
        return mTabWidth;
    }

    /**
     * 设置tab的宽度
     *
     * @param mTabWidth
     */
    public void setTabWidth(int mTabWidth) {
        this.mTabWidth = mTabWidth;
        this.isSetTabWidth=true;
        setTabViews();
    }

    /**
     * 是否设置tab字体变换效果
     *
     * @param isShowTabSizeChange
     */
    public void setShowTabSizeChange(boolean isShowTabSizeChange) {
        this.isShowTabSizeChange = isShowTabSizeChange;
    }

    public int getTabPressColor() {
        return tabPressColor;
    }

    /**
     * 设置tab选中颜色，默认为红色#ff0000
     *
     * @param tabPressColor
     */
    public void setTabPressColor(int tabPressColor) {
        this.tabPressColor = tabPressColor;
    }

    /**
     * 设置tab的边距,默认都为0
     *
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public void setTabPadding(int l, int t, int r, int b) {
        this.tabPaddingLeft = l;
        this.tabPaddingRight = r;
        this.tabPaddingTop = t;
        this.tabPaddingBottom = b;
        setTabViews();
    }

    public boolean isDeuceTabWidth() {
        return isDeuceTabWidth;
    }

    /**
     * 是否平分tab的宽度，若false，则返回每个tab自适应的宽度，适合在多种数目下使用
     *
     * @param isDeuceTabWidth
     */
    public void setDeuceTabWidth(boolean isDeuceTabWidth) {
        this.isDeuceTabWidth = isDeuceTabWidth;
        setTabViews();
    }

    /**
     * 获取tab中最长的tab文本作为最小tab宽度
     *
     * @param arrStr
     * @return
     */
    private String getMaxLengthString(String[] arrStr) {
        String max = arrStr[0];
        for (int x = 1; x < arrStr.length; x++) {
            if (arrStr[x].length() > max.length())
                max = arrStr[x];
        }
        return max;
    }

    /**
     * 重置数值
     */
    public void resetData() {
        initView();

    }

    /**
     * 指示器实现者
     *
     * @author yangpeixing
     */
    public interface DrawIndicatorCreator {
        /**
         * 默认为圆角矩形指示器，用户可继承重写自定义指示器样式
         *
         * @param canvas
         * @param left   tab左边距离原点的位置
         * @param top    整个tab的位置
         * @param right  tab距离顶端的位置
         * @param bottom 整个tab的高度,既控件高度
         * @param paint  指示器画笔
         * @param raduis 外围圆角半径
         */
        void drawIndicator(Canvas canvas, int left, int top, int right,
                           int bottom, Paint paint, int raduis);
    }

    DrawIndicatorCreator creator;

    /**
     * 设置指示器样式，默认为圆球样式，既网易样式
     *
     * @param creator
     */
    public void setDrawIndicatorCreator(DrawIndicatorCreator creator) {
        this.creator = creator;
    }

    /**
     * 对外的ViewPager的回调接口
     *
     * @author yangpeixing
     */
    public interface PageChangeListener {
        void onPageScrolled(int position, float positionOffset,
                            int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }

    /**
     * 对外的ViewPager的回调接口
     */
    private PageChangeListener onPageChangeListener;

    /**
     * 对外的ViewPager的回调接口的设置
     *
     * @param pageChangeListener
     */
    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            isClick = false;
            if (mCurrentIndex > visiableCounts / 2 - 1
                    && mCurrentIndex < titles.length) {
                int centerX = getTransitXByPosition(mCurrentIndex)
                        - (screenWidth - tabLengthArray[mCurrentIndex]) / 2;
                parentScrollto(centerX, 0);
            }
        }
        // 回调
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        /*if (isDeuceTabWidth && mCurrentIndex > visiableCounts / 2 - 1
				&& mCurrentIndex < titles.length) {
			this.scrollTo((position - (visiableCounts - 2)) * mTabWidth
					+ (int) (mTabWidth * positionOffset), 0);
		}*/
        if (position + 1 != totalCount && !isClick && position < totalCount) {
            if (isShowTabSizeChange) {// 判断是否变换
                setTabSizeChange(position, 1 - positionOffset);
                setTabSizeChange(position + 1, positionOffset);
            }
            setTabColorChange(position, 1 - positionOffset);
            setTabColorChange(position + 1, positionOffset);
        }
        if (positionOffset != 0.0 && position < totalCount - 1) {
            mTransitX = (int) (tabLengthArray[position] * positionOffset + (getTransitXByPosition(position)));
            mTabWidth = (int) (tabLengthArray[position] + (tabLengthArray[position + 1] - tabLengthArray[position])
                    * positionOffset);
        }
        invalidate();
        // 回调
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        mCurrentIndex = arg0;
        if (isClick) {
            resetTabColor();
            resetTabSize();
            getTab(arg0).setTextColor(tabPressColor);
            if (isShowTabSizeChange) {
                getTab(arg0).setTextSize(maxTabTextSize);
            } else {
                getTab(arg0).setTextSize(tabTextSize);
            }
        }
        if (arg0 == 0 && isChildOfHorizontalScrollView) {
            ((HorizontalScrollView) getParent()).scrollTo(0, 0);
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageSelected(arg0);
        }
    }

}
