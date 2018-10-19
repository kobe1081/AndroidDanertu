package com.danertu.dianping;


import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.MyOrderData;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AESEncrypt;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.DemoApplication;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.ImageLoaderConfig;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.tools.MD5Util;
import com.danertu.tools.MIUIUtils;
import com.danertu.tools.PayUtils;
import com.danertu.tools.QRCodeUtils;
import com.danertu.tools.ShareUtil;
import com.danertu.tools.StatusBarUtil;
import com.danertu.tools.SystemBarTintManager;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;


import static com.danertu.dianping.activity.choosecoupon.ChooseCouponPresenter.REQUEST_CHOOSE_COUPON;
import static com.danertu.tools.MIUIUtils.isMIUI;

/**
 * Activity基类
 * 包含了较多的公用方法以及变量
 */
public abstract class BaseActivity extends SwipeBackActivity {
    public final String TAG = getClass().getSimpleName();
    protected Handler handler;
    public boolean isLoading = false;
    public ProgressBar pb_loading = null;
    protected DBManager db = null;
    protected AppManager appManager = null;
    public boolean isPause;
    public DemoApplication application = null;
    private String shopId = "";
    private DeviceTag dTag = null;
    public static MyOrderData myOrderData;
    public static MyOrderDataQRCode myOrderDataQRCode;
    public LocationUtil locationUtil;
    public long firstClick;//用于判定频繁点击的参数
    public PayUtils payUtils;
    public static final int REQUEST_PHONE_STATE = 291;
    public static final int REQUEST_WRITE_STORAGE = 292;
    public static final int WHAT_TO_LOGIN = 293;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        firstClick = System.currentTimeMillis();
        application = (DemoApplication) getApplication();
        application.addActivity(this);
        db = DBManager.getInstance();
        //init AppManager
        appManager = AppManager.getInstance();
        appManager.setVersionName(String.valueOf(CommonTools.getVersionCode(this)));
        //init ImageLoader
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
        }
        //init async task myHandler
        this.handler = new BaseHandler(this);
        gson = new Gson();
        Bundle bundle = getIntent().getExtras();
        setParam(bundleToJson(bundle));
        //get device information
        dTag = new DeviceTag(this, gson);
        if (TextUtils.isEmpty(Constants.USER_TOKEN) && isLogined() && !"SplashActivity".equals(TAG) && !"AppShowActivity".equals(TAG)) {
            String userToken = db.getUserToken(this);
            if (TextUtils.isEmpty(userToken)) {
                jsShowMsg("您的登录信息已过期，请重新登录");
                quitAccount();
                jsStartActivity("LoginActivity", "");
                if (!"IndexActivity".equals(TAG)) {
                    jsFinish();
                }
            } else {
                Constants.USER_TOKEN = userToken;
            }
        }
        appManager.setUid(getUid());
    }

    /**
     * 判断是否是泉眼门票
     *
     * @param createUser 创建者
     * @param guid       商品id
     * @return 判断结果
     */
    @JavascriptInterface
    public boolean isQYHotel(String createUser, String guid) {
        return (createUser.equals("shopnum1") && !Constants.list_ticket.contains(guid));
    }

    /**
     * 获取数据
     */
    protected WebView webView;

    @JavascriptInterface
    public void jsGetData(final String paramStr) {
        jsGetData(paramStr, null);
    }

    /**
     * @param paramStr
     * @param tag
     * @since versionCode 57
     */
    @JavascriptInterface
    public void jsGetData(final String paramStr, final String tag) {
//		if(webIsLoading)
//			return;
        runOnUiThread(new Runnable() {
            public void run() {
                new GetData(tag).execute(paramStr);
            }
        });
    }

    /**
     * 获取数据
     */
    private class GetData extends AsyncTask<String, Integer, String> {
        private String tag = null;

        public GetData(String tag) {
            this.tag = tag;
            Logger.i(TAG + "_GetData", tag);
        }

        //async task
        protected String doInBackground(String... arg0) {
//			webIsLoading = true;
            String paramStr = arg0[0];
            try {
                LinkedHashMap<String, String> param = handleParamStr(paramStr);
                if (param == null) {
                    CommonTools.showShortToast(getContext(), "参数不能为空");
                    return null;
                }
                if (!param.containsKey("dateline")) {
                    param.put("dateline", String.valueOf(System.currentTimeMillis()));
                }
                if (!param.containsKey("tid")) {
                    param.put("tid", getUid());
                }
                param = sortLinkedHashMap(param);

                String result = appManager.doPost(param);
                return result.replaceAll("\n|\r", "");
            } catch (Exception e) {
                CommonTools.showShortToast(getApplicationContext(), "出错了：" + e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            synchronized (getContext()) {
//				webIsLoading = false;
                if (tag != null) {
                    webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "','" + tag + "')");
                } else {
                    webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "')");
                }
            }
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    /**
     * @param phoneNum 电话号码
     * @since versionCode 53
     */
    @JavascriptInterface
    public void dial(final String phoneNum) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (TextUtils.isEmpty(phoneNum)) {
                    jsShowMsg("号码不能为空！");
                } else {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum)));
                }
            }
        });
    }

    /**
     * 到订单中心
     *
     * @param index 0全部 1待付款 2
     */
    @JavascriptInterface
    public void jsToOrderActivity(final int index) {
        jsToOrderActivity(index, false);
    }

    @JavascriptInterface
    public void jsToOrderActivity(final int index, final boolean isOnlyHotel, final boolean isOnlyQuanYan) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    Intent intent = new Intent(getContext(), OrderCenterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("TabIndex", index);
                    startActivity(intent);
                } else {
                    if (isLoading)
                        return;
//            showLoadDialog();
//            Intent intent = new Intent(getContext(), MyOrderActivityNew.class);
                    Intent intent = new Intent(getContext(), OrderCenterActivity.class);
                    intent.putExtra("TabIndex", index);
                    intent.putExtra("isOnlyHotel", isOnlyHotel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
//                    myOrderData = new MyOrderData((BaseActivity) getContext(), isOnlyHotel, isOnlyQuanYan) {
//                        @Override
//                        public void getDataSuccess() {
//
//                    if (handler != null) {
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                hideLoadDialog();
//
//                            }
//                        }, 1000);
//                    } else {
//                        hideLoadDialog();
//                    }
//                        }
//                    };

                }
            }
        });
    }

    /**
     * 保留旧的
     *
     * @param index
     * @param isOnlyHotel
     * @param isOnlyQuanYan
     */
    @JavascriptInterface
    public void jsToOldOrderActivity(final int index, final boolean isOnlyHotel, final boolean isOnlyQuanYan) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    Intent intent = new Intent(getContext(), MyOrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("TabIndex", index);
                    startActivity(intent);
                } else {
                    if (isLoading)
                        return;
//            showLoadDialog();
//            Intent intent = new Intent(getContext(), MyOrderActivityNew.class);
                    Intent intent = new Intent(getContext(), MyOrderActivity.class);
                    intent.putExtra("TabIndex", index);
                    intent.putExtra("isOnlyHotel", isOnlyHotel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    myOrderData = new MyOrderData((BaseActivity) getContext(), isOnlyHotel, isOnlyQuanYan) {
                        @Override
                        public void getDataSuccess() {
//
//                    if (handler != null) {
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                hideLoadDialog();
//
//                            }
//                        }, 1000);
//                    } else {
//                        hideLoadDialog();
//                    }
                        }

                        @Override
                        public void needLogin() {
                            jsShowMsg("您的登录信息已过期，请重新登录");
                            jsStartActivity("LoginActivity", "");
                            finish();
                        }
                    };

                }
            }
        });
    }

    /**
     * 到订单中心
     *
     * @param index       0-全部 1-待付款 2-待发货 3-待收货 4-退款
     * @param isOnlyHotel 酒店订单
     */
    @JavascriptInterface
    public void jsToOrderActivity(final int index, final boolean isOnlyHotel) {
        jsToOrderActivity(index, isOnlyHotel, false);
    }

    /**
     * 创建桌面图标
     */
    public void createShortCut() {
        CommonTools.createShortCut(this);
    }

    @JavascriptInterface
    public void jsSetResult(int resultCode) {
        jsSetResult(resultCode, null);
    }

    @JavascriptInterface
    public void jsSetResult(final int resultCode, final String data) {
        runOnUiThread(new Runnable() {
            public void run() {
                Bundle b = getBundle(data);
                Intent i = null;
                if (b != null) {
                    i = getIntent().putExtras(b);
                }
                setResult(resultCode, i);
            }
        });
    }

    /**
     * 将bundle转换成json
     */
    protected Gson gson = null;

    protected String bundleToJson(Bundle bundle) {
        if (bundle == null || bundle.isEmpty())
            return "";
        HashMap<String, Object> param = new HashMap<>();
        for (String key : bundle.keySet()) {
            param.put(key, bundle.get(key));
        }
        shopId = bundle.getString("shopid");
        shopId = shopId == null ? "" : shopId;
        return gson.toJson(param);
    }

    /**
     * @return 客户从某个店铺浏览商品的店铺id
     * @since versionCode 55
     */
    @JavascriptInterface
    public String getShopId() {
        return shopId;
    }

    @JavascriptInterface
    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    /**
     * 缩放图片
     *
     * @param path 图片路径
     * @param size 大小
     * @return bitmap
     * @throws OutOfMemoryError
     */
    public Bitmap getScalePic(String path, int size) throws OutOfMemoryError {
        Options options = new Options();

        //设为true，那么BitmapFactory.decodeFile(String path, Options opt)
        //并不会真的返回一个Bitmap给你，它仅仅会把它的宽，高取回来给你
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);/* 这里返回的bmp是null */
        int h = options.outHeight;
        int w = options.outWidth;
        if (w > h) {
            options.outWidth = size;
            options.outHeight = h * size / w;
            options.inSampleSize = w / size;
        } else {
            options.outHeight = size;
            options.outWidth = w * size / h;
            options.inSampleSize = h / size;
        }
        Log.i("h_w_ssize", options.outHeight + " , " + options.outWidth + " , " + options.inSampleSize);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private String param = "";

    @JavascriptInterface
    public String jsGetParam() {
        return param;
    }

    @JavascriptInterface
    protected void setParam(String param) {
        this.param = param;
    }

    private ShareUtil shareUtil;

    public void setShareUtil(ShareUtil shareUtil) {
        this.shareUtil = shareUtil;
    }

    /**
     * @param platformList 只显示出来的分享平台, Wechat|WechatMoments 表示只显示微信和微信朋友圈
     */
    @JavascriptInterface
    public void share(final String title, final String imgPath, final String targetPath, final String description, final String platformList) {
        runOnUiThread(new Runnable() {
            public void run() {
                initShareUtil();
                shareUtil.share("", "", title, imgPath, targetPath, description, platformList);
            }
        });
    }

    @JavascriptInterface
    public void share(final String type, final String shopid, final String title, final String imgPath, final String targetPath, final String description) {
        runOnUiThread(new Runnable() {
            public void run() {
                initShareUtil();
                shareUtil.share(type, shopid, title, imgPath, targetPath, description);
            }
        });

    }

    /**
     * 初始化分享功能
     */
    private void initShareUtil() {
        if (shareUtil == null) {
            shareUtil = new ShareUtil(getContext());
        }
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    @JavascriptInterface
    public int getVersionCode() {
        return CommonTools.getVersionCode(this);
    }

    /**
     * 获取版本名
     *
     * @return 版本名
     */
    @JavascriptInterface
    public String getVersionName() {
        return "v" + CommonTools.getVersionName(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        FrameLayout fl = new FrameLayout(getContext());
        fl.setId(R.id.container);
        View v = LayoutInflater.from(getContext()).inflate(layoutResID, fl, true);
        setContentView(v);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
    }

    protected FrameLayout fl;

    @JavascriptInterface
    public void setFitsSystemWindows(final boolean isFits) {
        runOnUiThread(new Runnable() {
            public void run() {
                fl.setFitsSystemWindows(isFits);
                if (navigationBarExist2()) {
                    int bottomMargin = isFits ? 0 : getNavigationBarHeight();
                    setMargins(fl, 0, 0, 0, bottomMargin);
                }
            }
        });
    }

    /**
     * 给activity设置view，加上加载动画，同时如果满足条件->Constants.ACT_FILL_STATUSBAR.contains(TAG)时，将内容区域延伸至状态栏区域，达到沉浸效果
     *
     * @param view
     */
    @Override
    public void setContentView(View view) {
        fl = new FrameLayout(getContext());
        fl.addView(view);
        View v1 = LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null);
        pb_loading = (ProgressBar) v1.findViewById(R.id.pb_loading);
        fl.addView(v1);
        super.setContentView(fl);

//        final int naviBarHeight = getNavigationBarHeight();
//        if (navigationBarExist2())
//            setMargins(fl, 0, 0, 0, naviBarHeight);

        if (Constants.ACT_FILL_STATUSBAR.contains(TAG)) {
            final int naviBarHeight = getNavigationBarHeight();
            if (navigationBarExist2()) {
                setMargins(fl, 0, 0, 0, naviBarHeight);
            }
        } else {
            fl.setFitsSystemWindows(true);
        }
        initSystemBar();

    }

    /**
     * 导航栏是否存在？？
     * 判断依据是当前屏幕的宽高度是否大于当前activity view的显示宽高度
     *
     * @return
     */
    public boolean navigationBarExist2() {
        WindowManager windowManager = getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 给指定的view设置margin，在需要将状态栏透明并且内容显示到状态栏以及有底部菜单存在时保证底部不会被遮挡
     *
     * @param v 需要设置margin的view
     * @param l 左
     * @param t 上
     * @param r 右
     * @param b 下
     */
    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * versionCode: 69
     */
    @JavascriptInterface
    public int getAndroidVersion() {
        return VERSION.SDK_INT;
    }

    /**
     * 获取状态高度
     *
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 获取导航栏高度
     *
     * @return 导航栏高度
     */
    public int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
    }

    protected SystemBarTintManager manager;

    /**
     * 设置状态栏样式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initSystemBar() {
        if (VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = getWindow();
        // Translucent status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Translucent navigation bar
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (manager == null) {
            manager = new SystemBarTintManager(this);
        }
        manager.setStatusBarTintEnabled(true);
        manager.setNavigationBarTintEnabled(true);
        manager.setNavigationBarTintResource(R.color.black);
//        setSystemBar(R.color.white);
        setSystemBar(R.color.tab_black);
//        setSystemBarWhite();
//        setSystemBar(R.color.transparent);

    }

    /**
     * 设置状态栏底色为白色
     */
    @JavascriptInterface
    public void setSystemBarWhite(final boolean isDark) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (StatusBarUtil.StatusBarLightMode(BaseActivity.this, isDark) == 0)
                    setSystemBar(R.drawable.bg_white_statubar);
                else
                    setSystemBar(R.color.white);
            }
        });
    }

    /**
     * 设置状态栏底色为白色
     */
    @JavascriptInterface
    public void setSystemBarWhite() {
        setSystemBarWhite(true);
    }

    public void setSystemBar(int res) {
        if (manager != null) {
            manager.setStatusBarTintResource(res);
        }
    }

    public void setSystemBar(Drawable drawable) {
        if (manager != null) {
            manager.setStatusBarTintDrawable(drawable);
        }
    }

    /**
     * 设置状态栏底色
     *
     * @param color 颜色值
     * @since versionCode 74
     */
    @JavascriptInterface
    public void setSystemBarColor(final String color) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (manager != null && !TextUtils.isEmpty(color)) {
                    manager.setStatusBarTintColor(Color.parseColor(color));
                }
            }
        });
    }

    public void onResume() {
        isPause = false;
        super.onResume();
        //友盟推送服务
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        isPause = true;
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    /**
     * versionCode: 69
     */
    @JavascriptInterface
    public void mobOnEventValue(String id, String param, int du) {
        Map<String, String> m = handleParamStr(param);
        MobclickAgent.onEventValue(getContext(), id, m, du);
    }

    /**
     * versionCode: 72
     */
    @JavascriptInterface
    public void mobOnEvent(String id) {
        MobclickAgent.onEvent(getContext(), id);
    }

    /**
     * 保留两位小数
     */
    public Double formatZero2(double num) {
        double result = 0;
        try {
            String s = formatZero2Str(num);
            result = Double.parseDouble(s);
        } catch (Exception e) {
            result = num;
        }
        return result;
    }

    /**
     * 保留两位小数字符串
     */
    public String formatZero2Str(double num) {
        return CommonTools.formatZero2Str(num);
    }

    @JavascriptInterface
    public String getTimeStamp() {
        return DateTimeUtils.getFormatTimeStamp();
    }

    /**
     * H5页面通过js获取设备imei码
     *
     * @return 设备IMEI码
     */
    @JavascriptInterface
    public String getIMEI() {
        return dTag.getImei();
    }

    /**
     * H5页面通过js获取设备Mac地址
     *
     * @return 设备Mac地址
     */
    @JavascriptInterface
    public String getMac() {
        return dTag.getMac();
    }

    /**
     * H5页面通过js获取设备id
     *
     * @return 设备id
     */
    @JavascriptInterface
    public String getDeviceID() {
        return dTag.getDeviceID();
    }

    /**
     * H5页面通过js调用android toast
     *
     * @param msg toast message
     */
    @JavascriptInterface
    public void jsShowMsg(String msg) {
        CommonTools.showShortToast(this, msg);
    }

    public Message getMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        return msg;
    }

    /**
     * 将参数字符串转换成map键值对
     *
     * @param paramStr 参数字符串
     * @return 参数map
     */
    protected LinkedHashMap<String, String> handleParamStr(String paramStr) {
        return CommonTools.handleParamStr(paramStr);
    }

    private String json = null;

    @JavascriptInterface
    public String getData(final String paramStr) {
        Thread t = new Thread() {
            public void run() {
                try {
                    if (paramStr.equals("")) {
                        json = "参数不能为空!";
                        CommonTools.showShortToast(getContext(), json);
                        return;
                    }
                    LinkedHashMap<String, String> param = handleParamStr(paramStr);
                    if (!param.containsKey("dateline")) {
                        param.put("dateline", String.valueOf(System.currentTimeMillis()));
                    }
                    if (!param.containsKey("tid")) {
                        param.put("tid", getUid());
                    }
                    param = sortLinkedHashMap(param);
                    String result = appManager.doPost(param);
                    json = result.replaceAll("\n|\r", "");
                } catch (Exception e) {
                    CommonTools.showShortToast(getApplicationContext(), "出错了：" + e.toString());
                    json = e.toString();
                    e.printStackTrace();
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            json = e.toString();
            e.printStackTrace();
        }
        return json;
    }

    public LinkedHashMap<String, String> sortLinkedHashMap(LinkedHashMap<String, String> map) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        //先转成ArrayList集合
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<String, String> entry : list) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return hashMap;
    }

    /**
     * 2017年9月12日
     * 重写getResources方法，达到字体不跟随系统设置改变大小的效果
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    private Vibrator vibrator = null;

    /**
     * 设置手机震动
     *
     * @param time 震动时间
     */
    @JavascriptInterface
    public void jsVibrate(long time) {
        if (!isPause) {
            if (vibrator == null) {
                vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
            }
            vibrator.vibrate(time);
        }
    }

    /**
     * H5通过js跳转activity
     *
     * @param actName action name
     * @param paraStr 参数字符串
     */
    @JavascriptInterface
    public void jsStartActivity(String actName, String paraStr) {
        try {
            //77版本中将AdressActivity.java改名为AddressActivity.java，为了兼容旧版本而添加
            if (actName.equals("AdressActivity")) {
                actName = "AddressActivity";
            }
            startActivity(getIntent(actName, paraStr));
        } catch (ClassNotFoundException e) {
            if (Constants.isDebug)
                CommonTools.showShortToast(getApplicationContext(), "出错了：" + e);
            else {
                CommonTools.showShortToast(getApplicationContext(), "出错了");
            }
            e.printStackTrace();
        }
    }

    /**
     * @param url 网址
     * @since 81
     * 2017年11月3日
     * 调用系统浏览器加载指定网页
     * <p>
     * app更新的备用方式
     */
    @JavascriptInterface
    public void jsOpenSystemBrower(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * 从5.5版本起支持全包名activity跳转
     * 将参数转成Intent
     */
    protected Intent getIntent(String actName, String paraStr) throws ClassNotFoundException {
        if (!actName.contains(".")) {
            actName = "com.danertu.dianping." + actName;
        }
        Intent intent = new Intent(this, Class.forName(actName));
        if (TextUtils.isEmpty(paraStr)) {
            return intent;
        }
        intent.putExtras(getBundle(paraStr));
        return intent;
    }

    /**
     * 将参数字符串转换成bundle
     *
     * @param paraStr 参数字符串 参数之间使用  ,;  隔开，键与值用  |   隔开
     * @return bundle
     */
    protected Bundle getBundle(String paraStr) {
        if (TextUtils.isEmpty(paraStr)) {
            return null;
        }
        Bundle b = new Bundle();
        String[] strList = paraStr.split(",;");
        for (String aStrList : strList) {
            b.putString(aStrList.substring(0, aStrList.indexOf("|")), aStrList.substring(aStrList.indexOf("|") + 1));
        }
        return b;
    }

    /**
     * 页面js调起原生StartActivityForResult
     *
     * @param actName     指定activity
     * @param paraStr     参数字符串
     * @param requestCode 请求码
     */
    @JavascriptInterface
    public void jsStartActivityForResult(String actName, String paraStr, int requestCode) {
        try {
            startActivityForResult(getIntent(actName, paraStr), requestCode);
        } catch (ClassNotFoundException e) {
            CommonTools.showShortToast(getApplicationContext(), "出错了：" + e);
            e.printStackTrace();
        }
    }

    /**
     * 获取纬度
     *
     * @return 纬度值
     */
    @JavascriptInterface
    public String getLa() {
        return Constants.getLa();
    }

    /**
     * 获取经度
     *
     * @return 经度值
     */
    @JavascriptInterface
    public String getLt() {
        return Constants.getLt();
    }

    /**
     * 获取城市名
     *
     * @return 当前城市名
     */
    @JavascriptInterface
    public String getCityName() {
        return Constants.getCityName();
    }

    /**
     * @return 省份名
     * @since 80版本
     * 2017年10月16日
     * huangyeliang
     * 获取当前所在位置的省份
     */
    @JavascriptInterface
    public String getCurrentProvince() {
        return Constants.getCurrentProvince();
    }

    /**
     * 获取uid
     *
     * @return uid
     */
    @JavascriptInterface
    public String getUid() {
        return db.GetLoginUid(getContext());
    }


    @JavascriptInterface
    public void jsStartActivityClearTop(String actName, String paraStr) {
        try {
            Intent intent = getIntent(actName, paraStr);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            CommonTools.showShortToast(getApplicationContext(), "出错了：" + e);
            e.printStackTrace();
        }
    }

    /**
     * H5页面通过js结束当前页面
     */
    @JavascriptInterface
    public void jsFinish() {
        finish();
    }

    public void finish() {
        overridePendingTransition(0, 0);
        application.removeActivity(this);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //remove all myHandler task before destroy
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    protected abstract void findViewById();

    protected abstract void initView();

    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    /**
     * 通过class启动activity，并且含有bundle数据
     *
     * @param pClass  activity的class
     * @param pBundle bundle数据
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 通过action启动activity
     *
     * @param pAction action
     */
    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    /**
     * 通过Action启动Activity，并且含有Bundle数据
     *
     * @param pAction action
     * @param pBundle bundle
     */
    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * H5页面通过js调起原生加载对话框
     */
    @JavascriptInterface
    public void showLoadDialog() {
        if (isLoading)
            return;
        isLoading = true;
        runOnUiThread(new Runnable() {
            public void run() {
                pb_loading.setVisibility(View.VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
                pb_loading.startAnimation(anim);
            }
        });
    }

    /**
     * H5页面通过js隐藏原生加载对话框
     */
    @JavascriptInterface
    public void hideLoadDialog() {
        if (!isLoading)
            return;
        isLoading = false;
        runOnUiThread(new Runnable() {
            public void run() {
                if (handler == null) {
                    pb_loading.setVisibility(View.GONE);
                    return;
                }
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out);
                pb_loading.startAnimation(anim);
                handler.postDelayed(new Runnable() {
                    public void run() {
                        pb_loading.setVisibility(View.GONE);
                    }
                }, anim.getDuration());
            }
        });
    }

    /**
     * 加载动画正在加载
     */
    @JavascriptInterface
    public boolean isLoading() {
        return isLoading;
    }

    public Context getContext() {
        return this;
    }

    private static AESEncrypt aes;

    /**
     * 加密
     *
     * @param param 被加密字符串
     * @return 加密后字符串
     */
    @JavascriptInterface
    public String jsEncrypt(String param) {
        String encrypt = "";
        try {
            if (aes == null)
                aes = new AESEncrypt();
            encrypt = aes.encrypt(param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * 解密
     *
     * @param aesStr 被解密字符串
     * @return 解密后的字符串
     */
    @JavascriptInterface
    public String jsDecrypt(String aesStr) {
        String decrypt = "";
        try {
            if (aes == null)
                aes = new AESEncrypt();
            decrypt = aes.decrypt(aesStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decrypt;
    }

    /**
     * @param text 字符串
     * @return md5加密字符串
     * @since versionCode 53
     */
    public String md5(String text) {
        return MD5Util.MD5(text);
    }

    /**
     * 保存信息到指定的SharedPreferences
     *
     * @param fileName
     * @param param
     */
    @JavascriptInterface
    public void saveMsg(String fileName, String param) {
        SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);
        param = jsEncrypt(param);
        sp.edit().putString(fileName, param).apply();
        sp = getSharedPreferences("fileList", MODE_PRIVATE);
        Map<String, ?> params = sp.getAll();
        if (params.get(fileName) == null)
            sp.edit().putString(fileName, fileName).apply();
    }

    /**
     * 获取保存的信息
     *
     * @param fileName
     * @return
     */
    @JavascriptInterface
    public String getSaveMsg(String fileName) {
        SharedPreferences sp = getSharedPreferences(fileName, MODE_PRIVATE);
        String msg = sp.getString(fileName, "");
        msg = jsDecrypt(msg);
        return msg;
    }

    /**
     * 发送消息
     */
    public void sendMessage(int what) {
        sendMessage(what, null, null);
    }

    public void sendMessage(int what, String data) {
        Bundle b = new Bundle();
        b.putString("data", data);
        sendMessage(what, null, b);
    }

    public void sendMessageNew(int what, int arg1, Object obj) {
        if (handler == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.arg1 = arg1;
        message.obj = obj;
        handler.sendMessage(message);
    }

    public void sendMessage(int what, int taskId, String data) {
        Bundle b = new Bundle();
        b.putInt("task", taskId);
        b.putString("data", data);
        sendMessage(what, null, b);
    }

    public void sendMessage(int what, Object obj, Bundle data) {
        if (handler == null) {
            return;
        }
        if (obj == null && data == null) {
            handler.sendEmptyMessage(what);
            return;
        }
        Message msg = Message.obtain();
        msg.what = what;

        if (data != null) {
            msg.setData(data);
        }

        if (obj != null) {
            msg.obj = obj;
        }

        handler.sendMessage(msg);
    }

    public void onTaskComplete(int taskId, String result) {
    }

    public void onTaskComplete(int taskId) {
    }

    public void onNetworkError(int taskId) {
        hideLoadDialog();
        toast(Constants.err.NETWORK);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 上传文件至Server的方法
     */
    @JavascriptInterface
    public boolean uploadFile(String picName, Bitmap upBitmap) {
        String actionUrl = "http://115.28.77.246:8098/";
        return CommonTools.uploadFile(this, actionUrl, picName, upBitmap, null, 0);
    }

    @JavascriptInterface
    public boolean uploadFile(String picName, Bitmap upBitmap, HashMap<String, String> param) {
        return uploadFile(picName, upBitmap, param, 0);
    }

    @JavascriptInterface
    public boolean uploadFile(String picName, Bitmap upBitmap, HashMap<String, String> param, int quality) {
        String actionUrl = "http://115.28.77.246:8098/AppProductUpload.aspx";
        return CommonTools.uploadFile(this, actionUrl, picName, upBitmap, param, quality);
    }

    @JavascriptInterface
    public boolean uploadFile(String picName, Bitmap upBitmap, String paramStr) {
        String actionUrl = "http://115.28.55.222:8085/RequestApi.aspx";
        HashMap<String, String> param = handleParamStr(paramStr);
        return CommonTools.uploadFile(this, actionUrl, picName, upBitmap, param, 0);
    }

    /**
     * 等比例缩放图片
     */
    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        return CommonTools.zoomBitmap(bitmap, width, height);
    }

    /**
     * H5页面获取屏幕宽度
     *
     * @return 屏幕宽度
     */
    @JavascriptInterface
    public int getScreenWidth() {
        if (SCREEN_WIDTH <= 0) {
            initScreenData();
        }
        return SCREEN_WIDTH;
    }

    /**
     * H5页面获取屏幕高度
     *
     * @return 屏幕高度
     */
    @JavascriptInterface
    public int getScreenHeight() {
        if (SCREEN_HEIGHT <= 0) {
            initScreenData();
        }
        return SCREEN_HEIGHT;
    }

    /**
     * H5页面获取设备屏幕密度
     *
     * @return 屏幕密度
     */
    @JavascriptInterface
    public float getScreenDensity() {
        if (SCREEN_DENSITY <= 0) {
            initScreenData();
        }
        return SCREEN_DENSITY;
    }

    /**
     * 初始化设备屏幕参数
     */
    private static int SCREEN_HEIGHT = 0;
    private static int SCREEN_WIDTH = 0;
    private static float SCREEN_DENSITY = 0;

    /**
     * 获取屏幕密度、宽高度
     */
    private void initScreenData() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREEN_DENSITY = metrics.density;
        SCREEN_HEIGHT = metrics.heightPixels;
        SCREEN_WIDTH = metrics.widthPixels;
    }

    /**
     * 获取图片地址
     *
     * @param imgName    图片名称
     * @param agentID    代理商id
     * @param supplierID 供应商id
     * @return 图片地址
     */
    @JavascriptInterface
    public String getImgUrl(String imgName, String agentID, String supplierID) {
        return ActivityUtils.getImgUrl(imgName, agentID, supplierID, getUid());
    }

    /**
     * H5页面判断用户是否登录
     *
     * @return 判断结果
     */
    @JavascriptInterface
    public boolean isLogined() {
        String uid = db.GetLoginUid(this);
        return !TextUtils.isEmpty(uid);
    }

    /**
     * 返回首页
     *
     * @return 返回结果
     */
    @JavascriptInterface
    public boolean backToHome() {
        application.backToActivity("IndexActivity");
        return true;
    }

    /**
     * 2017年10月13日
     *
     * @since version 80
     * huangyeliang
     * 发送刷新首页广播
     * 用于通知首页刷新
     */
    @JavascriptInterface
    public void jsSendRefreshBroadcast() {
        Logger.e("refresh", "jsSendRefreshBroadcast");
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(Constants.REFRESH_INDEX);
        manager.sendBroadcast(intent);
    }

    /**
     * 为解决点击多次打开多个页面问题添加
     * huangyeliang
     *
     * @param secondClick 点击时的毫秒数
     */
    public boolean isClickMoreTimesShortTime(long secondClick) {
        if (secondClick - firstClick > 800) {
            firstClick = secondClick;
            return true;
        } else {
            return false;
        }
    }

    public boolean isClickMoreTimesShortTime() {
        return isClickMoreTimesShortTime(System.currentTimeMillis());
    }

    public <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 囤货相关页面获取图片路径
     *
     * @param imgName
     * @return
     */
    public String getStockSmallImgPath(String imgName) {
        return Constants.APP_URL.imgServer + "sysProduct/" + imgName;
    }

    public void setTopPadding(View view, int top) {
        view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
    }

    private boolean isPayLoading = false;

    @JavascriptInterface
    public void payOrder(String orderNumber) {
        payOrder(orderNumber, true);
    }

    /**
     * 支付后原生跳转至订单详情
     *
     * @param orderNumber     订单号
     * @param isShowArrivePay 是否可以使用到付
     */
    @JavascriptInterface
    public void payOrder(String orderNumber, boolean isShowArrivePay) {
        payOrder(orderNumber, true, isShowArrivePay);
    }

    /**
     * 支付后原生跳转至订单详情
     *
     * @param orderNumber      订单号
     * @param isShowAccountPay 是否可以使用单耳兔钱包支付
     * @param isShowArrivePay  是否可以使用到付
     */
    @JavascriptInterface
    public void payOrder(final String orderNumber, boolean isShowAccountPay, boolean isShowArrivePay) {
        payOrder(orderNumber, true, true, isShowAccountPay, isShowArrivePay);
    }

    @JavascriptInterface
    public void payOrder(final String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay) {
        if (isPayLoading) {
            return;
        }
        isPayLoading = true;
        payUtils = new PayUtils(this, getUid(), orderNumber, isShowAliPay, isShowWechatPay, isShowAccountPay, isShowArrivePay) {
            @Override
            public void paySuccess() {
                isPayLoading = false;
                jsShowMsg("支付成功");
                finish();
                toOrderDetail(orderNumber);
            }

            @Override
            public void payFail() {
                isPayLoading = false;
                jsShowMsg("支付失败,请检查");
                finish();
                toOrderDetail(orderNumber);
            }

            @Override
            public void payCancel() {
                isPayLoading = false;
                jsShowMsg("您已取消支付");
                finish();
                toOrderDetail(orderNumber);
            }

            @Override
            public void payError(String message) {
                isPayLoading = false;
                jsShowMsg(message);
            }

            @Override
            public void dismissOption() {
                if (!isPaying()) {
                    finish();
                    toOrderDetail(orderNumber);
                }
                isPayLoading = false;
            }
        };
    }

    @JavascriptInterface
    public void payOrder(String orderNumber, String callBackMethod) {
        payOrder(orderNumber, true, true, callBackMethod);
    }

    @JavascriptInterface
    public void payOrder(String orderNumber, boolean isShowArrivePay, String callBackMethod) {
        payOrder(orderNumber, true, isShowArrivePay, callBackMethod);
    }

    /**
     * 支付后回调页面方法处理
     *
     * @param orderNumber      订单号
     * @param isShowAccountPay 是否可以使用单耳兔钱包支付
     * @param isShowArrivePay  是否可以使用到付
     * @param callBackMethod   回调的页面方法  1-支付成功，2-支付失败，3-取消支付，4-发生错误
     */
    @JavascriptInterface
    public void payOrder(String orderNumber, boolean isShowAccountPay, boolean isShowArrivePay, final String callBackMethod) {
        payOrder(orderNumber, true, true, isShowAccountPay, isShowArrivePay, callBackMethod);
    }

    @JavascriptInterface
    public void payOrder(String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay, final String callBackMethod) {
        if (isPayLoading) {
            return;
        }
        isPayLoading = true;
        payUtils = new PayUtils(this, getUid(), orderNumber, isShowAliPay, isShowWechatPay, isShowAccountPay, isShowArrivePay) {
            @Override
            public void paySuccess() {
                isPayLoading = false;
                if (webView != null)
                    webView.loadUrl(Constants.IFACE + callBackMethod + "(‘1’)");
            }

            @Override
            public void payFail() {
                isPayLoading = false;
                if (webView != null)
                    webView.loadUrl(Constants.IFACE + callBackMethod + "(‘2’)");
            }

            @Override
            public void payCancel() {
                isPayLoading = false;
                if (webView != null)
                    webView.loadUrl(Constants.IFACE + callBackMethod + "(‘3’)");
            }

            @Override
            public void payError(String message) {
                isPayLoading = false;
                if (webView != null)
                    webView.loadUrl(Constants.IFACE + callBackMethod + "(‘4’)");
            }

            @Override
            public void dismissOption() {
                isPayLoading = false;
                if (TAG.contains("HtmlActivity")) {
                    finish();
                }
            }
        };
    }


    @JavascriptInterface
    public boolean checkOpsPermission(String permission) {
        return checkOpsPermission(this, permission);
    }

    @JavascriptInterface
    public boolean checkOpsPermission(Context context, String permission) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            String opsName = AppOpsManager.permissionToOp(permission);
            if (opsName == null) {
                return true;
            }
            int opsMode = appOpsManager.checkOpNoThrow(opsName, Process.myUid(), context.getPackageName());
            return opsMode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.printStackTrace();
            return true;
        }
    }

    private boolean isPhoneStatePermission = false;

    /**
     * 获取读取手机状态权限，用于获取mac、imei、设备id
     */
    public void getPhoneStatePermission() {
        //如果当前系统为MIUI
        if (isMIUI() && isPhoneStatePermission) {
            isPhoneStatePermission = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (!checkOpsPermission(this, android.Manifest.permission.READ_PHONE_STATE)) {
                    jsShowMsg("请授予单耳兔权限");
                    MIUIUtils.gotoMiuiPermission(this);
                    return;
                }

            }
        }

        /**权限检查*/
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED && !isPhoneStatePermission) {
            isPhoneStatePermission = true;
            jsShowMsg("请授予单耳兔权限");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_STATE);
        }
    }

    private boolean isStoragePermission = false;

    public void getStoragePermission() {
        //如果当前系统为MIUI
        if (isMIUI() && isStoragePermission) {
            isStoragePermission = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (!checkOpsPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    jsShowMsg("请授予单耳兔权限");
                    MIUIUtils.gotoMiuiPermission(this);
                    return;
                }

            }
        }

        /**权限检查*/
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && !isStoragePermission) {
            isStoragePermission = true;
            jsShowMsg("请授予单耳兔权限");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PHONE_STATE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_COUPON:
                if (resultCode != RESULT_CANCELED) {
                    //从领取优惠券页面返回
                    if (data.getExtras() == null) {
                        return;
                    }
                    Bundle bundle = data.getExtras();
                    String callBackMethod = bundle.getString("callbackMethod"); //页面回调方法
                    String isUseCoupon = bundle.getString("isUseCoupon");       //0-不使用优惠券,1-使用优惠券
                    String bundleString = bundle.getString("data");             //优惠券数据
                    Logger.i(TAG, "callBackMethod=" + callBackMethod + ",isUseCoupon=" + isUseCoupon + ",bundleString=" + bundleString);
                    if (webView != null) {
                        webView.loadUrl(Constants.IFACE + callBackMethod + "(\'" + isUseCoupon + "\',\'" + bundleString + "\')");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 创建一张包含二维码的图片并分享
     *
     * @param imgSrc         指定的图片链接
     * @param qrCodeContent  二维码内容
     * @param startX         起始点x坐标，传递指定的图片长的百分比
     * @param startY         起始点y坐标，传递指定的图片高的百分比
     * @param widthAndHeight 二维码边长，单位为像素
     * @param platformList   只显示出来的分享平台, Wechat&WechatMoments 表示只显示微信和微信朋友圈，空则表示全部
     */
    @JavascriptInterface
    public void shareImgWithQRCode(String imgSrc, final String qrCodeContent, final float startX, final float startY, final int widthAndHeight, final String platformList) {
        if (TextUtils.isEmpty(imgSrc)) {
            jsShowMsg("图片地址不能为空");
            return;
        }
        showLoadDialog();
        ImageLoader.getInstance().loadImage(imgSrc, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                jsShowMsg("创建失败");
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                //拿到了图片
                try {
                    Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
                    Bitmap qrCode = QRCodeUtils.createQRImage(qrCodeContent, widthAndHeight, logo, false);
                    if (qrCode == null) {
                        jsShowMsg("创建失败");
                        hideLoadDialog();
                        return;
                    }

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    float locationX = width * startX;//二维码具体的起始点x坐标
                    float locationY = height * startY;//二维码具体的起始点y坐标
                    Bitmap shareBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(shareBitmap);
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    canvas.drawBitmap(qrCode, locationX, locationY, null);
                    canvas.save(Canvas.ALL_SAVE_FLAG);
                    canvas.restore();
                    // 系统时间
                    long currentTimeMillis = System.currentTimeMillis();
                    long dateSeconds = currentTimeMillis / 1000;
                    String saveDirName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Danertu";
                    String saveImgName = "Danertu_" + currentTimeMillis + ".png";
                    File saveFile = new File(saveDirName);
                    if (!saveFile.exists()) {
                        saveFile.mkdir();
                    }
                    final File file = new File(saveDirName + File.separator + saveImgName);

                    // 保存图片到系统MediaStore
                    ContentValues values = new ContentValues();
                    ContentResolver resolver = getContext().getContentResolver();
                    values.put(MediaStore.Images.ImageColumns.DATA, saveDirName + File.separator + saveImgName);
                    values.put(MediaStore.Images.ImageColumns.TITLE, saveImgName);
                    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, saveImgName);
                    values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, currentTimeMillis);
                    values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
                    values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
                    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
                    values.put(MediaStore.Images.ImageColumns.WIDTH, shareBitmap.getWidth());
                    values.put(MediaStore.Images.ImageColumns.HEIGHT, shareBitmap.getHeight());
                    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    OutputStream out = resolver.openOutputStream(uri);
                    shareBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);// bitmap转换成输出流，写入文件
                    out.flush();
                    out.close();
                    // update file size in the database
                    values.clear();
                    values.put(MediaStore.Images.ImageColumns.SIZE, file.length());
                    resolver.update(uri, values, null, null);
                    //绘制完成，保存，分享
                    if (shareUtil == null) {
                        initShareUtil();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoadDialog();
                            shareUtil.shareImg(null, null, file.getAbsolutePath(), platformList, new PlatformActionListener() {
                                @Override
                                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                                    jsShowMsg("分享成功");
                                }

                                @Override
                                public void onError(Platform platform, int i, Throwable throwable) {
                                    jsShowMsg("分享失败");

                                }

                                @Override
                                public void onCancel(Platform platform, int i) {
                                    jsShowMsg("您已取消分享");
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    jsShowMsg("创建失败");
                    hideLoadDialog();
                    getStoragePermission();
                    if (Constants.isDebug)
                        e.printStackTrace();
                }

            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                jsShowMsg("创建取消");
                hideLoadDialog();
            }
        });
    }

    @JavascriptInterface
    public void quitAccount() {
        Logger.e(TAG, "quitAccount");
        String uid = getUid();
        db.DeleteLoginInfo(getContext(), uid);
        Constants.USER_TOKEN = "";
        //防止登录删除不成功
        if (db.GetLoginUid(getContext()).equals(uid)) {
            if (Constants.isDebug) {
                CommonTools.showShortToast(getContext(), "退出登录后登录信息更新不成功");
            }
            db.DeleteLoginInfo(getContext(), uid);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                jsSendRefreshBroadcast();

            }
        });

    }

    public boolean judgeIsTokenException(String json) {
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        try {
            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
            return bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode());
        } catch (Exception e) {
            if (Constants.isDebug)
                e.printStackTrace();
        }

        return false;
    }

    public void judgeIsTokenException(String json, TokenExceptionCallBack callBack) {
        if (TextUtils.isEmpty(json)) {
            callBack.ok();
        }
        try {
            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
            if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                callBack.tokenException(bean.getCode(), bean.getInfo());
            } else {
                callBack.ok();
            }
            bean = null;
            return;
        } catch (Exception e) {
            if (Constants.isDebug)
                e.printStackTrace();
            callBack.ok();
        }
        callBack.ok();
    }

    public interface TokenExceptionCallBack {
        void tokenException(String code, String info);

        void ok();
    }

    public void judgeIsTokenException(String json, final String errorMsg, final int requestCode) {
        if (TextUtils.isEmpty(json)) {
//            jsShowMsg("出错了");
            return;
        }
        try {
            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
            if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                if (handler != null) {
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_TO_LOGIN;
                    msg.arg1 = requestCode;
                    msg.obj = bean.getInfo();
                    handler.sendMessage(msg);
                }
            }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
////                        if (!TextUtils.isEmpty(errorMsg)) {
////                            jsShowMsg(errorMsg);
////                        }
//                        jsShowMsg(finalBean.getInfo());
//                        quitAccount();
//
//                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        if (requestCode == -1) {
//                            startActivity(intent);
//                            jsFinish();
//                        } else {
//                            startActivityForResult(intent, requestCode);
//                        }
//                    }
//                });
//            }
            bean = null;
        } catch (Exception e) {
            if (Constants.isDebug)
                e.printStackTrace();
            jsShowMsg("出错了");
        }
    }

    /**
     * ----------------------------小能相关移除后保留下来的方法，防止页面js无法正常加载----------------------------------
     */
    @JavascriptInterface
    public void postCustomerTrack() {
        hideLoadDialog();
    }

    @JavascriptInterface
    public void postCustomerTrack(String proName, String price) {
        hideLoadDialog();
    }

    @JavascriptInterface
    public void contactService(final String shopid, final String guid, final String proName, final String price, final String imgPath) {

    }

    @JavascriptInterface
    public void setContactService(String proName, String orderPrice, String userName, String userId) {

    }

    @JavascriptInterface
    public void setXNUtil() {

    }

    /**
     * ----------------------------小能相关移除后保留下来的方法，防止页面js无法正常加载----------------------------------
     */
}