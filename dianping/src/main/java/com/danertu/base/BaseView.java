package com.danertu.base;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;


/**
 * 基础view
 * Created by Viz on 2017/10/23.
 */

public interface BaseView {

    @JavascriptInterface
    void jsShowLoading();

    @JavascriptInterface
    void jsHideLoading();

    /**
     * @return 版本号
     */
    @JavascriptInterface
    int jsGetVersionCode();

    /**
     * @return 版本名
     */
    @JavascriptInterface
    String jsGetVersionName();

    @JavascriptInterface
    void jsFinish();

    /**
     * @return 当前位置经度
     */
    @JavascriptInterface
    String jsGetCurrentLa();

    /**
     * @return 当前位置纬度
     */
    @JavascriptInterface
    String jsGetCurrentLt();

    /**
     * @return 当前位置省份
     */
    @JavascriptInterface
    String jsGetCurrentProvince();

    /**
     * @return 当前位置城市
     */
    @JavascriptInterface
    String jsGetCurrentCity();

    @JavascriptInterface
    void jsStartActivity(String targetActivity);

    @JavascriptInterface
    void jsStartActivity(String targetActivity, String param);

    @JavascriptInterface
    void jsStartActivityForResult(String targetActivity, String param, int requestCode);

    @JavascriptInterface
    void jsSetResult(int resultCode);

    @JavascriptInterface
    void jsSetResult(int resultCode, String data);

    /**
     * 隐藏actionBar
     */
    @JavascriptInterface
    void jsHideActionBar();

    @JavascriptInterface
    String getUid();

    /**
     * @return imei码
     */
    @JavascriptInterface
    String jsGetIMEI();

    /**
     * @return 设备mac地址
     */
    @JavascriptInterface
    String jsGetMac();

    /**
     * @return 设备ID
     */
    @JavascriptInterface
    String jsGetDeviceID();

    /**
     * @param message 要toast的消息
     */
    @JavascriptInterface
    void jsShowToast(String message);


    /**
     * @return 是否已登录
     */
    @JavascriptInterface
    boolean jsIsLogin();

    /**
     * 使用系统浏览器打开指定的链接
     *
     * @param url url
     */
    @JavascriptInterface
    void jsOpenSystemBrowser(String url);

    /**
     * 分享方法
     *
     * @param title
     * @param imgPath
     * @param url
     * @param description
     * @param platformList
     */
    @JavascriptInterface
    void jsShare(String title, String imgPath, String carId, String url, String description, String platformList);

    /**
     * 初始化分享功能
     */
    void initShareUtils();

    /**
     * 设置margins
     *
     * @param viewGroup viewGroup
     * @param left      左
     * @param top       上
     * @param right     右
     * @param bottom    下
     */
    void setMargins(ViewGroup viewGroup, int left, int top, int right, int bottom);

    /**
     * 给指定的viewGroup设置topMargins
     *
     * @param viewGroup 指定的viewGroup
     * @param top       topMargins值
     */
    void setTopMargins(ViewGroup viewGroup, int top);

    /**
     * 给指定的view设置padding
     *
     * @param view   指定的view
     * @param left   左
     * @param top    上
     * @param right  右
     * @param bottom 下
     */
    void setPadding(View view, int left, int top, int right, int bottom);

    /**
     * 用于设置标题栏的顶部padding，配合 initSystemBar()以及setSystemBarWhite()达到状态栏沉浸效果
     *
     * @param view 要设置topPadding的view
     * @param top  topPadding的值
     */
    void setTopPadding(View view, int top);

    /**
     * 为解决点击多次打开多个页面问题添加
     * huangyeliang
     *
     * @param secondClick 点击时的毫秒数
     */
    @JavascriptInterface
    boolean isClickMoreTimesShortTime(long secondClick);
}
