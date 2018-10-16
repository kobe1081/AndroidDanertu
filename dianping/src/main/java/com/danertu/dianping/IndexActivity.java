package com.danertu.dianping;

import java.lang.ref.WeakReference;

import org.json.JSONArray;
import org.json.JSONObject;

import wl.codelibrary.widget.v4.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.config.Constants;
import com.danertu.dianping.R.color;
import com.danertu.entity.CheckAppBean;
import com.danertu.entity.LeaderBean;
import com.danertu.entity.ShopDetailBean;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.tools.StatusBarUtil;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebChromeClient;
import com.danertu.widget.MWebView;
import com.danertu.widget.MWebViewClient;
import com.joker.annotation.PermissionsGranted;
import com.joker.api.Permissions4M;

public class IndexActivity extends HomeActivity implements OnClickListener {
    public int pageindex = 1; // 当前页数
    int temp = 0;
    /**
     * Permissions4M 的定位requestCode
     */
    private static final int LOCATION_CODE = 11;
    /**
     * 与js交互的接口名
     */
    public static final String WV_INTERFACE = "index_wv_interface";
    private String wapTitle, wapUrl;
    //    private Button mMsg;
    String allJsonMsg = "";
    String secendPageJson = "";
    final String KEY_SHOPID = "shopid";
    private LocalBroadcastManager broadcastManager;
    private LoginSuccessReceiver loginSuccessReceiver;
    private LogoutSuccessReceiver logoutSuccessReceiver;
    private RefreshIndexReceiver refreshIndexReceiver;
    private GetLocationReceiver getLocationReceiver;
    private boolean localSuccess = false;
//    private TextView tvLocation;
//    private TextView tvMsgCount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        index_wap_name = WEBPAGE_NAME_RESERVE;
        setContentView(R.layout.activity_index);
        getLocation();
        locationUtil = new LocationUtil(this);
        locationUtil.startLocate();
        setSystemBarWhite();
        setSwipeBackEnable(false);
        findViewById();
        showLoadDialog();
        initUI();
        new Thread(getParamsRunnable).start();
        new GetShop().execute(getLoginId());
        initBroadcastReceiver();


        StatusBarUtil.StatusBarLightMode(this,true);
    }

    class CheckApp extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            String uid = param[0];
            String shopId = param[1];
            return appManager.checkAppState(uid, shopId);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //isForced：  0--可关闭，
            //{"val":[{"Url":"http://115.28.55.222:8018/articlescrap/qy_rule.html","Title":"测试强制弹窗","IsForced":"0"}]}
            initPage();
            try {
                CheckAppBean bean = gson.fromJson(result, CheckAppBean.class);
                if (bean != null && bean.getVal() != null && bean.getVal().size() > 0 && bean.getVal().get(0) != null) {
                    final CheckAppBean.ValBean valBean = bean.getVal().get(0);

                    final Dialog dialog = new Dialog(context);
                    View view = View.inflate(context, R.layout.dialog_index, null);
                    dialog.setContentView(view);
                    WebView wvDialog = (WebView) view.findViewById(R.id.webView_dialog);
                    ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
                    //设置支持js
                    WebSettings settings = wvDialog.getSettings();
                    settings.setDefaultTextEncodingName("utf-8");
                    settings.setJavaScriptEnabled(true);
                    settings.setTextZoom(100);
                    settings.setSupportZoom(false);
                    settings.setBuiltInZoomControls(false);
                    settings.setDisplayZoomControls(false);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    // webView.getSettings().setAppCacheEnabled(false);
                    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
                    //js调用本地方法
                    wvDialog.addJavascriptInterface(IndexActivity.this, "app");
                    wvDialog.setWebViewClient(new MWebViewClient(IndexActivity.this, "app") {

                    });
                    wvDialog.setWebChromeClient(new MWebChromeClient(IndexActivity.this));
                    ViewGroup.MarginLayoutParams layoutParams = ((ViewGroup.MarginLayoutParams) wvDialog.getLayoutParams());
                    ViewGroup.MarginLayoutParams ivCloseLayoutParams = (ViewGroup.MarginLayoutParams) ivClose.getLayoutParams();
                    ViewGroup.MarginLayoutParams viewLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    layoutParams.width = getScreenWidth();
                    layoutParams.height = getScreenHeight() - ivCloseLayoutParams.height - ivCloseLayoutParams.topMargin * 2;
                    viewLayoutParams.setMargins(5, 0, 5, 0);
                    wvDialog.setLayoutParams(layoutParams);
                    wvDialog.loadUrl(valBean.getUrl());
                    dialog.setTitle(valBean.getTitle());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final String isForced = valBean.getIsForced();
                    dialog.setCanceledOnTouchOutside("0".equals(isForced));
                    dialog.setCancelable("0".equals(isForced));
                    dialog.show();
                    if ("0".equals(isForced)) {
                        ivClose.setVisibility(View.VISIBLE);
                    } else {
                        ivClose.setVisibility(View.GONE);
                    }
                    ivClose.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!"0".equals(isForced)) {
                                jsShowMsg("此窗口不可关闭");
                                return;
                            }
                            dialog.dismiss();
                        }
                    });
                    if (wvDialog.getLayoutParams() == null) {
                        wvDialog.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    if (ivClose.getLayoutParams() == null) {
                        ivClose.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                    if (view.getLayoutParams() == null) {
                        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }


                }
            } catch (Exception e) {
                if (Constants.isDebug)
                    e.printStackTrace();
            }

        }
    }

    class GetShop extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... param) {
            String uid = param[0];
            if (TextUtils.isEmpty(uid)) {
                setShopId(Constants.CK_SHOPID);
                return null;
            }
            String details = appManager.postGetShopDetails("0041", uid);
            ShopDetailBean shopDetailBean = gson.fromJson(details, ShopDetailBean.class);
            if (shopDetailBean == null || shopDetailBean.getShopdetails().getShopbean() == null || shopDetailBean.getShopdetails().getShopbean().size() == 0) {
                String data = getData("apiid|0245,;shopid|" + uid);
                LeaderBean leaderBean = gson.fromJson(data, LeaderBean.class);
                if (leaderBean == null || leaderBean.getLeaderInfo() == null || leaderBean.getLeaderInfo().getLeaderBean().size() == 0) {
                    setShopId(Constants.CK_SHOPID);
                } else {
                    LeaderBean.LeaderInfoBean.LeaderBeanBean bean = leaderBean.getLeaderInfo().getLeaderBean().get(0);
                    if ("chunkang".equals(bean.getMemberid())) {
                        setShopId(Constants.CK_SHOPID);
                    } else {
                        setShopId(bean.getMemberid());
                    }
                }
            } else {
                setShopId(uid);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new CheckApp().execute(getLoginId(), getShopId());
        }
    }

    /**
     * 获取位置信息权限
     */
    private void getLocation() {
        Permissions4M.get(this)
                .requestForce(true)
                .requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .requestCode(LOCATION_CODE)
                .request();
    }

    @PermissionsGranted(LOCATION_CODE)
    public void granted() {
//        Toast.makeText(this, "获取权限成功", Toast.LENGTH_SHORT).show();
        if (locationUtil != null) {
            locationUtil.startLocate();
        }
    }

    //初始化登录、注销广播接收器
    private void initBroadcastReceiver() {
        if (broadcastManager == null) {
            broadcastManager = LocalBroadcastManager.getInstance(this);
        }
        if (loginSuccessReceiver == null) {
            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(Constants.LOGIN_SUCCESS_BROADCAST);
            loginSuccessReceiver = new LoginSuccessReceiver();
            broadcastManager.registerReceiver(loginSuccessReceiver, filter1);
        }
        if (logoutSuccessReceiver == null) {
            IntentFilter filter2 = new IntentFilter();
            filter2.addAction(Constants.LOGOUT_SUCCESS_BROADCAST);
            logoutSuccessReceiver = new LogoutSuccessReceiver();
            broadcastManager.registerReceiver(logoutSuccessReceiver, filter2);
        }
        if (refreshIndexReceiver == null) {
            IntentFilter filter3 = new IntentFilter();
            filter3.addAction(Constants.REFRESH_INDEX);
            refreshIndexReceiver = new RefreshIndexReceiver();
            broadcastManager.registerReceiver(refreshIndexReceiver, filter3);
        }
        if (getLocationReceiver == null) {
            IntentFilter filter3 = new IntentFilter();
            filter3.addAction(Constants.GET_LOCATION_FINISH);
            getLocationReceiver = new GetLocationReceiver();
            broadcastManager.registerReceiver(getLocationReceiver, filter3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播接收器
        broadcastManager.unregisterReceiver(loginSuccessReceiver);
        broadcastManager.unregisterReceiver(logoutSuccessReceiver);
        broadcastManager.unregisterReceiver(refreshIndexReceiver);
        broadcastManager.unregisterReceiver(getLocationReceiver);
    }

    @JavascriptInterface
    public String getUid() {
        String uid = super.getUid();
        uid = TextUtils.isEmpty(uid) ? "chunkang" : uid;
        return uid;
    }

    @JavascriptInterface
    public String getLoginId() {
        return super.getUid();
    }


    @JavascriptInterface
    public boolean backToHome() {
        String shopId = getShopId();
        if (isLoading() || shopId == null)
            return false;
        boolean isBack = super.backToHome();
        if (!isBack) {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                shopId = b.getString(KEY_SHOPID);
                shopId = shopId == null ? "" : shopId;
            }

            if (!shopId.equals(getUid())) {
                shopId = getUid();
                b = b == null ? new Bundle() : b;
                b.putString(KEY_SHOPID, shopId);
                getIntent().putExtras(b);
                initShop(bundleToJson(b));
            }
        }
        return isBack;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null)
            return;
        super.onNewIntent(intent);
        String shopId = getShopId();
        Bundle b = intent.getExtras();
        if (b == null)
            return;
        setIntent(intent);
        shopId = b.getString(KEY_SHOPID);
        if (!TextUtils.isEmpty(shopId) && shopId.equals(getUid())) {
            //底部提示不变
        }
        String param = bundleToJson(b);
        Logger.e(TAG, "onNewIntent:" + param);
        initShop(param);
    }

    private void initShop(String jsonParam) {
        setParam(jsonParam);
        webView.loadUrl(Constants.IFACE + "onNewIntent('" + jsonParam + "')");
    }

    /**
     * 加载首页--http://192.168.1.137:778/Android/AndroidIndex.html
     */
    public void loadPage() {
        String pageName = index_wap_name;
        pageName = pageName.equals(WEBPAGE_NAME) ? WEBPAGE_NAME : WEBPAGE_NAME_RESERVE;
        webView.loadUrl(Constants.appWebPageUrl + pageName);
//        webView.loadUrl("https://kyfw.12306.cn/");
//        webView.loadUrl("https://appweb.danertu.com:8444/activity/0817share.html");
    }

    @JavascriptInterface
    public void jsToTab(int tabIndex) {
        if (tabIndex == 1) {
            toCategory();
        }
    }

    public void initUI() {
        initWebContent();

//        tv_store_name.setOnClickListener(this);
    }

    private void initWebContent() {
        //设置支持js
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //js调用本地方法
        //加入两个接口名
        webView.addJavascriptInterface(this, WV_INTERFACE);
        webView.addJavascriptInterface(this, "app");

        webView.setWebViewClient(new MWebViewClient(this, WV_INTERFACE) {
            public void onPageFinished(WebView view, String url) {
                if (view == null)
                    return;
                super.onPageFinished(view, url);
                view.postDelayed(new Runnable() {
                    public void run() {
                        hideLoadDialog();
                        srl_webView.setRefreshing(false);
                    }
                }, 1000);
//				txtcity.setText(Constants.getCityName());
            }
        });
        webView.setWebChromeClient(new MWebChromeClient(this));
    }

    public void initPage() {
        long delayMillis = 0;
        if (Constants.getLa() == null) {//当还没有定位到时，休眠两秒
            delayMillis = 2000;// 休眠两秒，等待定位
        }
        handler.postDelayed(new Runnable() {
            public void run() {
                loadPage();
            }
        }, delayMillis);
    }

    public void javaLoadShopList(String jsonStr) {
        webView.loadUrl("javascript:javaLoadShopList('" + jsonStr + "')");
    }

    private MyHandler handler = new MyHandler(this);
    public static final int LOAD_WEBPAGE = 5;
    public static final int WHAT_GETSHOPLIST_SUCCESS = 1;
    public static final int WHAT_SUBMIT_SHARECODE_SUCCESS = 111;
    public static final int WHAT_FAIL = -111;
    final static String WEBPAGE_NAME = "AndroidIndex2.htm";

    final static String WEBPAGE_NAME_RESERVE = "Android/AndroidIndex.html";
//    final static String WEBPAGE_NAME_RESERVE = "new/index_android.html";

    public static class MyHandler extends Handler {
        WeakReference<IndexActivity> wAct;

        public MyHandler(IndexActivity act) {
            wAct = new WeakReference<>(act);
        }

        public void handleMessage(Message msg) {
            IndexActivity ia = wAct.get();
            if (msg.what == WHAT_GETSHOPLIST_SUCCESS) {//表示获取店铺列表的json数据成功
                ia.srl_webView.setRefreshing(false);
                ia.hideLoadDialog();
                String shopListjson = msg.obj.toString();
                if (!TextUtils.isEmpty(shopListjson)) {
                    ia.pageindex++;
                }
                ia.isLoading = false;
                ia.javaLoadShopList(shopListjson);

            } else if (msg.what == WHAT_FAIL) {
                ia.hideLoadDialog();
                ia.jsShowMsg(msg.obj.toString());

            }
        }
    }

    @JavascriptInterface
    public void refresh() {
//        shopId = getUid();

        new GetShop().execute(getLoginId());
        pageindex = 1;
//        if (webView != null) {
//            loadPage();
//            Logger.i(TAG, "刷新页面");
//        }
    }

    public void openNetWork() {
        AppManager.getInstance().setMobileDataStatus(this, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e(TAG, requestCode + " , " + resultCode);
        switch (requestCode) {
            case REQUEST_JLB:
                if (resultCode == LoginActivity.LOGIN_SUCCESS) {
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), ScoreCenterActivity.class);
                    startActivity(intent);
                }
                break;
            case REQUEST_WAP:
                if (resultCode == LoginActivity.LOGIN_SUCCESS) {
                    Intent it = new Intent(this, WapActivity.class);
                    it.putExtra(WapActivity.KEY_WEB_TITLE, wapTitle);
                    it.putExtra(WapActivity.KEY_WEB_URL, wapUrl);
                    startActivityForResult(it, REQUEST_WAP);
                }
                break;
            case SetCityActivity.SET_CITY_SUCCESS:
                if (resultCode == SetCityActivity.SET_CITY_SUCCESS) {
                    refresh();
                }
                break;
            case REQUEST_LOGIN:
                Logger.e("test", "登录成功");
                refresh();
                break;
            default:
                break;
        }
    }

    public static final int REQUEST_JLB = 7;
    public static final int REQUEST_WAP = 1;
    public static final int REQUEST_LOGIN = 88;

    @JavascriptInterface
    public void jsStartWapActivity(String wapTitle, String url) {
        this.wapTitle = wapTitle;
        this.wapUrl = url;
        toWapActivity(url);

    }

    @JavascriptInterface
    public void toWapActivity(String url) {
        Intent intent = new Intent(this, WapActivity.class);
        intent.putExtra(WapActivity.KEY_WEB_TITLE, wapTitle);
        intent.putExtra(WapActivity.KEY_WEB_URL, url);
        startActivityForResult(intent, REQUEST_WAP);
    }

    @JavascriptInterface
    public void jsStartCategoryActivity() {
        Intent intent = new Intent(this, CategoryActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到 吃住玩购 Activity
     *
     * @param type 1吃 2住 3玩 4购
     */
    @JavascriptInterface
    public void jsStartIndexTypeActivity(String type) {
        Intent intent = new Intent();
        intent.putExtra("type", type);
        intent.setClass(IndexActivity.this, IndexTypeActivity.class);
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsStartNoticectivity() {
        Toast.makeText(this, "公告", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void jsStartIndexBannerToActivity(String toActivityName, String paraStr) {
        try {

            Intent intent = new Intent(this, Class.forName("com.danertu.dianping." + toActivityName));
            Bundle b = new Bundle();
            String[] strList = paraStr.split(",;");
            for (String aStrList : strList) {
                b.putString(aStrList.substring(0, aStrList.indexOf("|")), aStrList.substring(aStrList.indexOf("|") + 1));
            }
            intent.putExtras(b);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过js提供的产品id启动列表的项的详细信息activity
     *
     * @param shopId 店铺id
     */
    @JavascriptInterface
    public void jsStartDetailActivity(String shopId) {
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle b = new Bundle();
        b.putString("shopid", shopId);
        intent.putExtras(b);
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsStartCalendarActivity() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    @JavascriptInterface
    public void jsStartTicketActivity() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent();
                String uid = db.GetLoginUid(getApplicationContext());
                if (uid == null || uid.trim().equals("")) {
                    CommonTools.showShortToast(getApplicationContext(), "请先登录");

                    intent.setClass(getApplicationContext(), LoginActivity.class);
                } else {
                    intent.setClass(getApplicationContext(), TicketActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    @JavascriptInterface
    public void jsStartJLB() {
        Intent intent = new Intent();
        String uid = db.GetLoginUid(getApplicationContext());
        if (uid == null || uid.trim().equals("")) {
            CommonTools.showShortToast(getApplicationContext(), "请先登录");

            intent.setClass(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_JLB);
        } else {
            intent.setClass(getApplicationContext(), ScoreCenterActivity.class);
            startActivity(intent);
        }
    }

    @JavascriptInterface
    public void jsStartProductListActivity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("from", "index");
        intent.putExtras(bundle);
        intent.setClass(getApplicationContext(), ProductListActivity.class);
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsStartHuDongActivity() {
        Intent intent = new Intent();
        String uid = db.GetLoginUid(getApplicationContext());
        Logger.w("uid--", uid);
        if (uid != null && uid.trim().length() > 0) {
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.HuDongActivity");
            startActivity(intent);
        } else {
            CommonTools.showShortToast(IndexActivity.this, "请先登录！");
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.LoginActivity");
            startActivity(intent);
        }
    }

    @JavascriptInterface
    public void jsStartNearByShopActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), NearByShopActivity.class);
                String city = Constants.getCityName();
                if (city != null && city.trim().length() > 0) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "定位失败，请手动选择一个城市!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isLoading = false;

    @JavascriptInterface
    public void jsLoadMoreData() {
        if (!isLoading) {
//			threadPool.submit(listRunnable);
            new Thread(listRunnable).start();
            isLoading = true;
        }
    }

    private SwipeRefreshLayout srl_webView;
    //    private TextView tv_store_name;
//    private LinearLayout fl_title;
//    private View top_bg;

    @SuppressWarnings("deprecation")
    @Override
    protected void findViewById() {
//        top_bg = findViewById(R.id.top_bg);
//        fl_title = (LinearLayout) findViewById(R.id.index_top_layout);
//        final int statusBarHeight = getStatusBarHeight();
//        if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//            setMargins(fl_title, 0, statusBarHeight, 0, 0);
//        View parent = ((View) fl_title.getParent());
//        parent.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//        top_bg.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, parent.getMeasuredHeight()));
//            setTitleBgAlpha(0);

//        tv_store_name = (TextView) findViewById(R.id.tv_store_name);

        srl_webView = (SwipeRefreshLayout) findViewById(R.id.srl_webView);
        srl_webView.setColorScheme(color.red, color.palegreen, color.yellow, color.green);
        srl_webView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                if (!isLoading()) {
                    refresh();
                } else {
                    srl_webView.setRefreshing(false);
                }
            }
        });
        webView = (WebView) srl_webView.findViewById(R.id.wv_index_content);
        srl_webView.setOnRefreshScroll(new SwipeRefreshLayout.OnRefreshScroll() {
            public void onScroll(float percent) {
//                fl_title.setAlpha(percent);
            }
        });
        ((MWebView) webView).setOnScrollChangeListener(new MWebView.OnScrollChangeListener() {
            public void onScrollChanged(int top) {
                setTitleBgAlpha(top);
            }
        });
        //左上角定位
//        tvLocation = ((TextView) findViewById(R.id.tv_location));
        setLocation();
        //右上角消息计数
//        tvMsgCount = ((TextView) findViewById(R.id.tv_msg_count));
//        tvLocation.setOnClickListener(this);
//        mMsg = (Button) findViewById(R.id.msgbox);
//        mMsg.setOnClickListener(this);
    }

    /**
     * 设置标题栏背景透明度
     *
     * @param alpha
     */
    public void setTitleBgAlpha(int alpha) {
//        if (getAndroidVersion() >= 11) {
//            float a = (float) alpha / 255;
//            top_bg.setAlpha(a);
//        }
    }

    @JavascriptInterface
    public void setShopName(final String name) {
        runOnUiThread(new Runnable() {
            public void run() {
//                tv_store_name.setText(name);
            }
        });
    }

    @Override
    protected void initView() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.index_search_edit:
                openActivity(SearchActivityV2.class);
                break;

//            case R.id.msgbox:
//                Intent intent = new Intent();
//                intent.putExtra("memberid", getUid());
//                intent.setClass(getApplicationContext(), MessageCenterActivity.class);
//                startActivity(intent);
////                startActivity(new Intent(this,JPushMessageActivity.class));
//                break;

//            case R.id.tv_store_name:
//                jsStartActivity("SearchActivityV2", KEY_SHOPID + "|" + shopid);
//                break;
//            case R.id.tv_location:
//                //定位按钮
//                CommonTools.showShortToast(context, "正在定位...");
//                localSuccess = true;
//                locationUtil.startLocate();
//                break;
            default:
                break;

        }
    }

    Runnable listRunnable = new Runnable() {
        public void run() {
            // 耗时操作
            String result = "";
            try {
                result = appManager.getFoodList(10, pageindex, "", 0, 0, "", getLa(), getLt());
                // result = getShopList(type, isCanOrder, isCanSell);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Message msg = new Message();
            msg.what = WHAT_GETSHOPLIST_SUCCESS;
            msg.obj = result;
            handler.sendMessage(msg);

        }

    };

    Runnable getParamsRunnable = new Runnable() {
        public void run() {
            // 耗时操作
            String result = appManager.getParams("0053");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("infoList");
                JSONArray jsonArray = jsonObject.getJSONArray("infobean");
                JSONObject oj = jsonArray.getJSONObject(0);
                Constants.pId = oj.getString("pId");
                JSONObject oj1 = jsonArray.getJSONObject(1);
                Constants.seller = oj1.getString("seller");
                JSONObject oj2 = jsonArray.getJSONObject(2);
                Constants.privateCode = oj2.getString("mPrivateCode");
                JSONObject oj3 = jsonArray.getJSONObject(3);
                Constants.publicCode = oj3.getString("publicCode");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @JavascriptInterface
    public void startLocate() {
        localSuccess = true;
        locationUtil.startLocate();
    }

    public void setLocation() {
        if (localSuccess) {
            if (TextUtils.isEmpty(Constants.getCityName())) {
                //定位失败
                CommonTools.showShortToast(context, "位置信息获取失败，请在设置中给予单耳兔App定位权限后重试");
            } else {
                CommonTools.showShortToast(context, "定位成功");
            }
            localSuccess = false;
        }

//        if (webView!=null){
//            webView.loadUrl(Constants.IFACE + callBackMethod + "(‘4’)");
//        }
//        if (TextUtils.isEmpty(Constants.getCityName())) {
//            tvLocation.setText("定位");
//        } else {
//            tvLocation.setText(Constants.getCityName().length() > 3 ? Constants.getCityName().substring(0, 2) + "..." : Constants.getCityName().substring(0, 2));
//        }
    }

    /**
     * 返回键
     */
    private long st, et;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            et = System.currentTimeMillis();
            if (et - st > 2000) {
                st = et;
                jsShowMsg("再按一次退出单耳兔");
                return true;
            } else {
                appManager.appExit(context);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 2017年10月13日
     * LoginSuccessReceiver、LogoutSuccessReceiver暂时保留，新增RefreshIndexReceiver
     * 刷新首页
     */
    class RefreshIndexReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("RefreshIndexReceiver ", getShopId());
            refresh();
        }
    }

    /**
     * 登录成功
     */
    class LoginSuccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("test", getShopId());
            refresh();
        }
    }

    /**
     * 注销登录成功
     */
    class LogoutSuccessReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e("test", getShopId());
            refresh();
        }
    }

    class GetLocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setLocation();
        }
    }


}
