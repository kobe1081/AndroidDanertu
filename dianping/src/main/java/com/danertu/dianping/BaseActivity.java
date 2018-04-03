package com.danertu.dianping;


import java.util.HashMap;
import java.util.Map;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.AESEncrypt;
import com.danertu.tools.AppManager;
import com.danertu.tools.AppUtil;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.DemoApplication;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.ImageLoaderConfig;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.tools.MD5Util;
import com.danertu.tools.ShareUtil;
import com.danertu.tools.StatusBarUtil;
import com.danertu.tools.SystemBarTintManager;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity基类
 * 包含了较多的公用方法以及变量
 */
public abstract class BaseActivity extends SwipeBackActivity {
    public final String TAG = getClass().getSimpleName();
    protected Handler handler;
    protected BaseTaskPool taskPool;
    private boolean isLoading = false;
    private ProgressBar pb_loading = null;
    protected DBManager db = null;
    protected AppManager appManager = null;
    public boolean isPause;
    public DemoApplication application = null;
    private String shopId = "";
    private DeviceTag dTag = null;
    public static MyOrderData myOrderData;
    public LocationUtil locationUtil;
    public long firstClick;//用于判定频繁点击的参数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        firstClick = System.currentTimeMillis();
        application = (DemoApplication) getApplication();
        application.addActivity(this);
        //init AppManager
        appManager = AppManager.getInstance();
        //init ImageLoader
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfig.initImageLoader(this, Constants.BASE_IMAGE_CACHE);
        }
        //init async task myHandler
        this.handler = new BaseHandler(this);
        db = DBManager.getInstance();
        gson = new Gson();
        Bundle bundle = getIntent().getExtras();
        setParam(bundleToJson(bundle));
        Logger.i("param", TAG + "\n" + jsGetParam());
        //get device information
        dTag = new DeviceTag(this, gson);

        if (CommonTools.isNavigationBarShow(this)){
            getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.red));
        }
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
                HashMap<String, String> param = handleParamStr(paramStr);
                if (param == null) {
                    CommonTools.showShortToast(getContext(), "参数不能为空");
                    return null;
                }
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

    /**
     * 到订单中心
     *
     * @param index       0全部 1待付款 2
     * @param isOnlyHotel 酒店订单
     */
    @JavascriptInterface
    public void jsToOrderActivity(final int index, final boolean isOnlyHotel) {
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
            myOrderData = new MyOrderData(this, isOnlyHotel) {
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
            };

        }
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
        shopId = bundle.getString("shopid");
        shopId = shopId == null ? "" : shopId;
        HashMap<String, Object> param = new HashMap<>();
        for (String key : bundle.keySet()) {
            param.put(key, bundle.get(key));
        }
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

    private FrameLayout fl;

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

        if (Constants.ACT_FILL_STATUSBAR.contains(TAG)) {
            final int naviBarHeight = getNavigationBarHeight();
            if (navigationBarExist2())
                setMargins(fl, 0, 0, 0, naviBarHeight);
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

    private SystemBarTintManager manager;

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
        setSystemBar(R.color.tab_black);
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
        appManager = AppManager.getInstance();
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
        return AppUtil.getFormatTimeStamp();
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
    protected HashMap<String, String> handleParamStr(String paramStr) {
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
                    HashMap<String, String> param = handleParamStr(paramStr);
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
        Logger.e("test", actName);
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

    /**
     * 开始异步任务
     *
     * @param taskId    任务id
     * @param delayTime 延迟时间
     */
    public void doTaskAsync(int taskId, int delayTime) {
        BaseTask baseTask = new BaseTask() {
            @Override
            public void onComplete() {
                sendMessage(BaseTask.TASK_COMPLETE, this.getId(), null);
            }

            @Override
            public void onError(String error) {
                hideLoadDialog();
                sendMessage(BaseTask.NETWORK_ERROR, this.getId(), null);
            }
        };
        doTaskAsync(taskId, baseTask, delayTime);
    }

    public void doTaskAsync(int taskId, String taskUrl) {
        showLoadDialog();
        BaseTask baseTask = new BaseTask() {
            @Override
            public void onComplete(String httpResult) {
                sendMessage(BaseTask.TASK_COMPLETE, this.getId(), httpResult);
            }

            @Override
            public void onError(String error) {
                hideLoadDialog();
                sendMessage(BaseTask.NETWORK_ERROR, this.getId(), null);
            }
        };
        addTask(taskId, taskUrl, null, baseTask, 0);
    }

    /**
     * 登录
     *
     * @param taskId
     * @param taskUrl
     * @param taskArgs
     */
    public void doTaskAsync(int taskId, String taskUrl,
                            HashMap<String, String> taskArgs) {
        showLoadDialog();
        BaseTask baseTask = new BaseTask() {
            @Override
            public void onComplete(String httpResult) {
                sendMessage(BaseTask.TASK_COMPLETE, this.getId(), httpResult);
            }

            @Override
            public void onError(String error) {
                hideLoadDialog();
                sendMessage(BaseTask.NETWORK_ERROR, this.getId(), null);
            }
        };
        addTask(taskId, taskUrl, taskArgs, baseTask, 0);
    }

    public void doTaskAsync(int taskId, BaseTask baseTask, int delayTime) {
        addTask(taskId, null, null, baseTask, delayTime);
    }

    public void addTask(int taskId, String taskUrl, HashMap<String, String> taskArgs, BaseTask baseTask, int delayTime) {
        if (taskPool == null) {
            // init task pool
            this.taskPool = new BaseTaskPool(this);
        }
        taskPool.addTask(taskId, taskUrl, taskArgs, baseTask, delayTime);
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
        return ActivityUtils.getImgUrl(imgName, agentID, supplierID);
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
     * @param imgName
     * @return
     */
    public String getStockSmallImgPath(String imgName) {
        return Constants.imgServer + "sysProduct/" + imgName;
    }
}