package com.danertu.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class SlidingMenu extends ScrollView {
    public static int mScreenHeight;
//      private int mOnePage;
//      private int mMenuPadding=220;


    private YsnowScrollViewPageOne wrapperMenu;
    private WebView wrapperContent;
    private boolean isSetted = false;
    private boolean ispageOne = true;

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public void setOnSlidingListener(SlidingListener listener) {
        this.listener = listener;
    }

    SlidingListener listener;

    public interface SlidingListener {
        void onSlided(boolean isOpen);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isSetted) {
            mScreenHeight = getHeight();
            //得到里面的控件
            final RelativeLayout wrapper = (RelativeLayout) getChildAt(0);
            wrapperMenu = (YsnowScrollViewPageOne) wrapper.getChildAt(0);
            wrapperContent = (WebView) wrapper.getChildAt(1);
            //设置两个子View的高度为手机的高度
            wrapperMenu.getLayoutParams().height = mScreenHeight;
            wrapperContent.getLayoutParams().height = mScreenHeight;
            isSetted = true;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(0, 0);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float x = ev.getX();
                int edge = CommonTools.dip2px(getContext(), 10);
                if (x < edge)
                    return false;
            case MotionEvent.ACTION_UP:
                //隐藏在左边的距离
                int scrollY = getScrollY();
                int creteria = mScreenHeight / 5;//滑动多少距离
                if (ispageOne) {
                    if (scrollY <= creteria) {
                        //显示菜单
                        this.smoothScrollTo(0, 0);
                    } else {
                        //隐藏菜单
                        if (listener == null) {
                            this.smoothScrollTo(0, 0);
                            return true;
                        }
                        this.smoothScrollTo(0, mScreenHeight);
                        this.setFocusable(false);
                        ispageOne = false;
                        listener.onSlided(true);
                    }
                } else {
                    int scrollpadding = mScreenHeight - scrollY;
                    if (scrollpadding >= creteria) {
                        if (listener == null) {
                            this.smoothScrollTo(0, mScreenHeight);
                            return true;
                        }
                        this.smoothScrollTo(0, 0);
                        ispageOne = true;
                        listener.onSlided(false);
                    } else {
                        this.smoothScrollTo(0, mScreenHeight);
                    }
                }

                return true;
        }
        return super.onTouchEvent(ev);
    }


    public void closeMenu() {
        if (ispageOne) return;
        this.smoothScrollTo(0, 0);
        ispageOne = true;
    }

    public void openMenu() {
        if (!ispageOne) return;
        this.smoothScrollTo(0, mScreenHeight);
        ispageOne = false;
    }

    /**
     * 打开和关闭菜单
     */
    public void toggleMenu() {
        if (ispageOne) {
            openMenu();
        } else {
            closeMenu();
        }
    }


}
