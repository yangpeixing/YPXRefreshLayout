/**
 *
 */
package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.YPXNetEaseIndicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * @author yangpeixing
 *
 */
public class UnderLineIndicator extends NetEaseIndicator {

	/**
	 * @param context
	 */
	public UnderLineIndicator(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public UnderLineIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public UnderLineIndicator(Context context, AttributeSet attrs,
							  int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void drawIndicatorWithTransitX(Canvas canvas, int left,
										  int top, int right, int bottom, Paint paint) {
		this.drawRoundRectIndicatorWithTransitX(canvas, left, top, right,
				bottom, paint);
	}

	/**
	 * 下划线指示器
	 *
	 * @param canvas
	 */
	public void drawUnderLineIndicatorWithTransitX(Canvas canvas, int left,
												   int top, int right, int bottom, Paint paint) {
		RectF oval = new RectF(left, bottom - 10, right, bottom);
		canvas.drawRect(oval, paint);
	}

	/**
	 * 圆角矩形指示器
	 *
	 * @param canvas
	 */
	public void drawRoundRectIndicatorWithTransitX(Canvas canvas, int left, int top,
												   int right, int bottom, Paint paint) {
		if (getBackgroundRadius() < bottom / 2) {
			// 真机运行用这种方式，模拟器圆角会失真
			RectF oval = new RectF(left, top, right, bottom);
			canvas.drawRoundRect(oval, getBackgroundRadius(), getBackgroundRadius(),
					paint);
		} else {//画三段代替圆角矩形，既圆、矩形、圆
			RectF oval2 = new RectF(bottom / 2 + left, top, right - bottom / 2,
					bottom);
			canvas.drawCircle(oval2.left, bottom / 2, bottom / 2,
					paint);
			canvas.drawRect(oval2, paint);
			canvas.drawCircle(oval2.right, bottom / 2, bottom / 2, paint);
		}
	}

	/**
	 * 三角形指示器
	 *
	 * @param canvas
	 */
	public void drawTriangleIndicatorWithTransitX(Canvas canvas, int left, int top,
												  int right, int bottom, Paint paint) {
		Path mPath = new Path();
		int mTriangleHeight = (bottom / 3) - dp(5);
		mPath.moveTo(left / 2 - dp(50), bottom);
		mPath.lineTo(left / 2 + dp(50), bottom);
		mPath.lineTo(left / 2, -mTriangleHeight);
		mPath.close();
		canvas.save();
		// 画笔平移到正确的位置
		canvas.translate(left / 2 + (right - left) / 2, bottom + 1);
		canvas.drawPath(mPath, paint);
		canvas.restore();
	}

	public int dp(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dp, getContext().getResources().getDisplayMetrics());
	}

}
