package com.danertu.dianping;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.danertu.adapter.GuidePagerAdapter;
import com.danertu.widget.CommonTools;

/**
 * 首次安装进入app的引导页
 */
public class AppShowActivity extends BaseActivity implements OnClickListener {

    private int[] ids = {R.drawable.guide_1, R.drawable.guide_2,
            R.drawable.guide_3, R.drawable.guide_4, R.drawable.guide_5};

    private List<View> guides = new ArrayList<>();
    private ViewPager pager;
    private ImageView open;
    private ImageView curDot;
    private LinearLayout llDot;
    private int offset;// 位移量
    private int curPos = 0;// 记录当前的位置
    private final String KEY_ISFIRSTIN = "isFirstIn";

    /**
     * Called when the activity is first created.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_app_show);
            setSwipeBackEnable(false);
            setOverrideExitAniamtion(false);
            setSystemBar(R.color.red_dark);
            SharedPreferences pref = getSharedPreferences("myActivityName", MODE_PRIVATE);
            if (pref.getBoolean(KEY_ISFIRSTIN, true)) {
                createShortCut();//首次进入app，创建桌面快捷方式
            }
            String keyVCode = "versionCode";
            int vCode = CommonTools.getVersionCode(this);
            Editor editor = pref.edit();
            editor.putBoolean(KEY_ISFIRSTIN, false);
            editor.putInt(keyVCode, vCode);
            editor.apply();
            llDot = (LinearLayout) findViewById(R.id.ll_dot);
            for (int id : ids) {
                ImageView iv = new ImageView(this);
                iv.setImageResource(id);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                iv.setLayoutParams(params);
                iv.setScaleType(ScaleType.FIT_XY);
                guides.add(iv);
                ImageView ivDot = new ImageView(this);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                ivDot.setLayoutParams(params);
                ivDot.setImageResource(R.drawable.dot1_w);
                llDot.addView(ivDot);
            }

            curDot = (ImageView) findViewById(R.id.cur_dot);
            open = (ImageView) findViewById(R.id.open);
            open.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    openActivity(IndexActivity.class);
                    finish();
                }
            });

            curDot.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    offset = curDot.getWidth();
                    return true;
                }
            });

            GuidePagerAdapter adapter = new GuidePagerAdapter(guides);
            pager = (ViewPager) findViewById(R.id.contentPager);
            pager.setAdapter(adapter);
            pager.setOnPageChangeListener(new OnPageChangeListener() {
                public void onPageSelected(int position) {
                    moveCursorTo(position);
                    if (position == ids.length - 1) {// 到最后一张了
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                open.setVisibility(View.VISIBLE);
                            }
                        }, 500);
                    } else if (curPos == ids.length - 1) {
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                open.setVisibility(View.GONE);
                            }
                        }, 100);
                    }
                    curPos = position;
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageScrollStateChanged(int state) {
                }

            });
        } catch (Exception | Error e) {
            e.printStackTrace();
            openActivity(IndexActivity.class);
            finish();
        }
    }

    /**
     * 移动指针到相邻的位置
     *
     * @param position 指针的索引值
     */
    private void moveCursorTo(int position) {
        TranslateAnimation anim = new TranslateAnimation(offset * curPos, offset * position, 0, 0);
        anim.setDuration(300);
        anim.setFillAfter(true);
        curDot.startAnimation(anim);
    }

    @Override
    public void onClick(View arg0) {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void initView() {

    }

}