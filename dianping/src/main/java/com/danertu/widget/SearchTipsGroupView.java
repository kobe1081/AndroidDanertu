package com.danertu.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.danertu.dianping.R;

/**
 * Description:很多搜索界面弹出来的提示语
 * User: xjp
 * Date: 2015/4/15
 * Time: 9:09
 */

public class SearchTipsGroupView extends LinearLayout {

    private Context context;

    public SearchTipsGroupView(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchTipsGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public SearchTipsGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);//设置方向
    }

    public interface OnItemClick {
        void onClick(String value);
    }

    private int marginTop = 0, marginRight = 0;

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    private int bgId = 0, colorId;

    public void setContentBg(int resid) {
        this.bgId = resid;
    }

    public void setContentColor(int resid) {
        this.colorId = resid;
    }

    /**
     * 外部接口调用
     *
     * @param items
     * @param onItemClick
     */
    public void initViews(final String items[], final OnItemClick onItemClick) {
        if (items == null || items.length == 0)
            return;
        int length = 0;//一行加载item 的宽度

        LinearLayout layout = null;

        LayoutParams layoutLp = null;

        boolean isNewLine = true;//是否换行

        int screenWidth = getScreenWidth();//屏幕的宽度

        int size = items.length;
        int margin = CommonTools.dip2px(context, 15);
        for (int i = 0; i < size; i++) {//便利items
            if (isNewLine) {//是否开启新的一行
                layout = new LinearLayout(context);
                layout.setOrientation(HORIZONTAL);
                layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutLp.topMargin = marginTop == 0 ? margin : marginTop;
            }

            View view = LayoutInflater.from(context).inflate(R.layout.item_textview, null);
            TextView itemView = (TextView) view.findViewById(R.id.text);
            if (bgId != 0) itemView.setBackgroundResource(bgId);
            if (colorId != 0) itemView.setTextColor(colorId);
            itemView.setText(items[i]);

            if (null != onItemClick) {
                final int j = i;
                itemView.setOnClickListener(new OnClickListener() {// 给每个item设置点击事件
                    @Override
                    public void onClick(View v) {
                        onItemClick.onClick(items[j]);
                    }
                });
            }

            //设置item的参数
            LayoutParams itemLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemLp.rightMargin = marginRight == 0 ? margin : marginRight;

            //得到当前行的长度
            length += margin + getViewWidth(itemView);
            if (length > screenWidth) {//当前行的长度大于屏幕宽度则换行
                length = 0;
                addView(layout, layoutLp);
                isNewLine = true;
                i--;
            } else {//否则添加到当前行
                isNewLine = false;
                layout.addView(view, itemLp);
            }
        }
        addView(layout, layoutLp);
    }

    /**
     * @param items
     * @param onItemClick
     */
    public void initViews(List<String> items, OnItemClick onItemClick) {
        if (items != null && items.size() > 0) {
            initViews(items.toArray(new String[items.size()]), onItemClick);
        }
    }

    /**
     * 得到手机屏幕的宽度
     *
     * @return
     */
    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 得到view控件的宽度
     *
     * @param view
     * @return
     */
    private int getViewWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }
}
