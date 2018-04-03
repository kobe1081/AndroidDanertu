package com.danertu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class YsnowScrollViewPageOne extends ScrollView {
    public float oldY;
    /**
     * t表示本scrollview向上滑动的距离
     */
    private int t;
    /**
     * 表示是否比父控件更高
     */
    private boolean isHigher = false;

    public YsnowScrollViewPageOne(Context context) {
        this(context, null);
    }

    public YsnowScrollViewPageOne(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YsnowScrollViewPageOne(Context context, AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = ev.getX();
                int edge = CommonTools.dip2px(getContext(), 15);
                if (x < edge)
                    return super.dispatchTouchEvent(ev);
                // 手指按下的时候，获得滑动事件，也就是让顶级scrollview失去滑动事件
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                // 并且记录Y点值
                oldY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 在滑动的时候获得当前值，并计算得到YS,用来判断是向上滑动还是向下滑动
                float Y = ev.getY();
                float Ys = Y - oldY;

                // 得到scrollview里面空间的高度
                int childHeight = this.getChildAt(0).getMeasuredHeight();
                isHigher = childHeight > SlidingMenu.mScreenHeight;
                // 子控件高度减去scrollview向上滑动的距离
                int padding = childHeight - t;
                // Ys<0表示手指正在向上滑动，padding==mScreenHeight表示本scrollview已经滑动到了底部
                if (Ys < 0) {
//                    if ((isHigher && padding == SlidingMenu.mScreenHeight) || !isHigher) {
                    if (!isHigher || padding == SlidingMenu.mScreenHeight) {
                        // 让顶级的scrollview重新获得滑动事件
                        getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        // t表示本scrollview向上滑动的距离
        this.t = t;
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    private ScrollChangeListener listener = null;

    public void setScrollChangeListener(ScrollChangeListener listener) {
        this.listener = listener;
    }

    public interface ScrollChangeListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
