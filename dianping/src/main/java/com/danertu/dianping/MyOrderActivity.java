package com.danertu.dianping;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;

import static com.danertu.dianping.MyOrderParent.REQ_PAY;

/**
 * 旧订单中心，新的订单中心为  OrderCenterActivity
 * 作者:  Viz
 * 日期:  2018/7/30 11:54
 */
public class MyOrderActivity extends BaseActivity implements MyOrderData.LoadDataListener {
    /**
     * tab页标题
     */
    private String tabWeightName[] = {"全部", "待付款", "待发货", "待收货", "退款"};
//    private String tabWeightName[] = {"全部", "待付款", "待发货", "待收货", "待评价"};
    /**
     * 用于指示tab页的数据是否发生变化  true-发生变化  false--未发生变化
     * <p>
     * //一个值代表一个分类得数据是否发生变化   0--全部  1--未付款  2--已付款.......
     */
    public static Boolean dataChanges[];// 在MyOrderData已经被实例化
    private Class<?> className[] = {MyOrderListAllActivity.class, MyOrderNoPayActivity.class, MyOrderNoSendActivity.class, MyOrderNoReceiveActivity.class, MyOrderNoCommentActivity.class};
    Context context;

    View list_views[];// 存储activity的view数组
    //    View list_wait_views[];
    List<View> list_tab_widgets;
    List<String> list_tabIds;
    List<Intent> list_intents;
    LocalActivityManager manager;
    TabHost tabHost;
    //    private TextView tvTabStr;
    private ViewPager viewPager;
    MyPageAdapter myPageAdapter;
    /**
     * 是否为当前页面
     */
    public static boolean isCurrentPage = false;
    /**
     * 表示是否从订单页面中跳转到支付中心的
     */
    public static boolean isOrderToPay = false;
    /**
     * 是否需要初始化myOrderData
     * 从ProductDetailWeb跳转过来的是不需要初始化myOrderData的，此时不能给myOrderData设置LoadListener，否则空指针
     */
    public static boolean isNeedInitMyOrderData = true;
    private ViewGroup title_layout = null;
    private TextView tv_title;
    private RelativeLayout root;
    /**
     * 2017年9月15日
     * true时表示tab尚未初始化，设置此标识时因为将订单列表修改为进入页面加载而不是数据加载完再进入页面时，进入订单详情支付或者取消订单完成后返回订单列表tab会重新add导致tab增加的问题
     */
    private boolean isTabInit = true;
    final String WEB_INTERFACE = "iface_ordercenter";
    final String WEBPAGE_OFFLINE = "Android_offline_orderCenter.html";
    final String WEBPAGE_ONLINE = "";
    final String WEBPAGE_DETAIL_OFFLINE = "Android_offline_orderDetail.html";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        setFitsSystemWindows(false);
//        initSystemBar();
        findViewById();
        setTopPadding(title_layout, getStatusBarHeight());
//        setTopPadding(root,getStatusBarHeight());
        showLoadDialog();
        context = this;
        //获取传递过来的要首先打开的tab页
        tabIndex = getIntent().getIntExtra(KEY_TABINDEX, 0);
        // 实例化一个单例模式的本地Activity的管理器
        manager = new LocalActivityManager(this, true);
        // 调度创建方法
        manager.dispatchCreate(savedInstanceState);
        isCurrentPage = false;// 加载完数据才算是当前页
        //不需要初始化myOrderData，不需要设置监听，否则空指针
        if (isNeedInitMyOrderData) {
            myOrderData.setLoadListener(this);
            myOrderData.clearData();
        }
    }

    public void initTabHost() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        viewPager = (ViewPager) findViewById(R.id.vp_tab_content);
//        tvTabStr= ((TextView) findViewById(R.id.tv_tab_str));
        // 实例化view容器
        list_views = new View[className.length];
//        list_wait_views = new View[className.length];

        // 添加activity到view中
        list_intents = new ArrayList<>();
        for (Class<?> aClassName : className) {
            Intent intent = new Intent(context, aClassName);
            list_intents.add(intent);
        }
        list_views[tabIndex] = getView("tab" + tabIndex, list_intents.get(tabIndex));

        tabHost.setup();
        tabHost.setup(manager);

        if (isTabInit) {
            // 设置tabwidget的标题
            list_tab_widgets = new ArrayList<>();
            for (String aTabweightname : tabWeightName) {
                View tab = LayoutInflater.from(context).inflate(R.layout.dd_tabweight, null);
                TextView tv = (TextView) tab.findViewById(R.id.tv_dd_tabweight);
                tv.setText(aTabweightname);
                list_tab_widgets.add(tab);
            }
            // 通过tabId来确定是哪个Activity的,这里只是设置标志信息，设置虚指向
            Intent intent = new Intent(context, Tab_EmptyActivity.class);

            list_tabIds = new ArrayList<>();
            for (int i = 0; i < tabWeightName.length; i++) {
                list_tabIds.add("tab" + i);
                tabHost.addTab(tabHost.newTabSpec("tab" + i).setIndicator(list_tab_widgets.get(i)).setContent(intent));
            }
            myPageAdapter = new MyPageAdapter();
            viewPager.setAdapter(myPageAdapter);
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int index = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                this.index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    setSwipeBackEnable(index == 0);
                    tabHost.setCurrentTab(index);
                    MyOrderData.TAB_INDEX = index;
                    tabIndex = index;
                    // 当数据还没加载时或数据发生改变时，重新获取Activity的View
                    if (list_views[index] == null || dataChanges[index]) {
                        list_views[index] = getView(list_tabIds.get(index), list_intents.get(index));
                        myPageAdapter.notifyDataSetChanged();
                        dataChanges[index] = false;
                    }
                }
                manager.dispatchResume();
            }
        });

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                for (int i = 0; i < tabWeightName.length; i++) {
                    if (tabId.equals(list_tabIds.get(i))) {
                        if (list_views[i] == null || dataChanges[i]) {
                            list_views[i] = getView(list_tabIds.get(i), list_intents.get(i));
                            myPageAdapter.notifyDataSetChanged();
                            dataChanges[i] = false;
                        }
                        tabIndex = i;
                        viewPager.setCurrentItem(i, false);
//                        break;
//                        tvTabStr.setText(tabWeightName[i]);
                        ((TextView) ((RelativeLayout) tabHost.getTabWidget().getChildTabViewAt(i)).getChildAt(0)).setTextSize(22);
                    } else {
                        ((TextView) ((RelativeLayout) tabHost.getTabWidget().getChildTabViewAt(i)).getChildAt(0)).setTextSize(14);
                    }
                }
            }
        });
        ((TextView) ((RelativeLayout) tabHost.getTabWidget().getChildTabViewAt(tabIndex)).getChildAt(0)).setTextSize(22);
        tabHost.setCurrentTab(tabIndex);
//        tvTabStr.setText(tabWeightName[tabIndex]);
        viewPager.setCurrentItem(tabIndex, false);
        isTabInit = false;
    }

    @Override
    public void hideLoad() {
        hideLoadDialog();
    }

    @Override
    public void loadFinish() {
        sendLoadFinishBroadcast();
    }

    @Override
    public void dataChanged() {
        sendDataChangeBroadcast();
    }

    /**
     * 发送广播
     */
    private void sendLoadFinishBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_FINISH);
        manager.sendBroadcast(intent);
    }

    private void sendDataChangeBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
        manager.sendBroadcast(intent);
    }

    private void sendActivityResultBroadcast(int requestCode, int resultCode, String orderNumber) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_ON_ACTIVITY_FOR_RESULT);
        intent.putExtra("orderNumber", orderNumber);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("resultCode", resultCode);
        manager.sendBroadcast(intent);
    }

    private class MyPageAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (list_views[position] != null) {
                ViewPager viewPager = ((ViewPager) container);
                viewPager.removeView(list_views[position]);
            }
        }

        public int getCount() {
            return className.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (list_views[position] != null) {
                ViewPager viewPager = ((ViewPager) container);
                viewPager.addView(list_views[position]);
                return list_views[position];
            }
            return null;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private View getView(String tabId, Intent intent) {
        if (tabId == null || intent == null)
            return null;
        return manager.startActivity(tabId, intent).getDecorView();
    }

    int tabIndex = 0;


    protected void onSaveInstanceState(Bundle outState) {
        Logger.i("MyOrderActivity_onSaveInstanceState", "待完善");
        super.onSaveInstanceState(outState);
    }

    public static final String KEY_TABINDEX = "TabIndex";

    public void onResume() {
        super.onResume();
        //执行子activity中的onResume()方法
        manager.dispatchResume();
        if (isCurrentPage) {
            return;
        }
        String title = getIntent().getBooleanExtra("isOnlyHotel", false) ? "酒店订单" : "我的订单";

        MyOrderData.TAB_INDEX = tabIndex;
        initTitle(title);

        if (tabIndex < 0) {
            int res = R.drawable.app_title_bg_offline;
            setSystemBar(res);
            title_layout.setBackgroundResource(res);
            tv_title.setText("我的预订");
            webView = (WebView) findViewById(R.id.wv_orderCenter);
            initWebContent();
            return;
        }

        setSystemBarWhite();
        initTabHost();
        isCurrentPage = true;
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebContent() {
        webView.setVisibility(View.VISIBLE);
        WebSettings setting = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, WEB_INTERFACE);
        webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_OFFLINE);
        webView.setWebViewClient(new MWebViewClient(this, WEB_INTERFACE) {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isDetailPage) {
                    hideLoadDialog();
                    initJavaDetailPage();
                    isDetailPage = false;
                } else
                    new Thread(rGetOfflineData).start();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadDialog();
            }

        });
    }

    private void initJavaDetailPage() {
        webView.loadUrl("javascript:javaLoadOrderDetailOffLine('" + Name + "','" + Mobile + "','" + Address
                + "','" + ShopName + "','" + ProductName + "','" + ProductPrice + "','" + BuyCount + "','" + ShouldPayPrice
                + "','" + OrderStatus + "','" + agentID + "','" + smallImg + "')");
    }

    boolean isDetailPage = false;
    String Name, Mobile, Address, ShopName, ProductName, ProductPrice, BuyCount, ShouldPayPrice, OrderStatus, agentID, smallImg;

    @JavascriptInterface
    public void javaLoadOrderDetailOffLine(String Name, String Mobile, String Address, String ShopName, String ProductName, String ProductPrice, String BuyCount, String ShouldPayPrice, String OrderStatus, String agentID, String smallImg) {
        this.Name = Name;
        this.Mobile = Mobile;
        this.Address = Address;
        this.ShopName = ShopName;
        this.ProductName = ProductName;
        this.ProductPrice = ProductPrice;
        this.BuyCount = BuyCount;
        this.ShouldPayPrice = ShouldPayPrice;
        this.OrderStatus = OrderStatus;
        this.agentID = agentID;
        this.smallImg = smallImg;
        isDetailPage = true;
        runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_DETAIL_OFFLINE);
            }
        });
    }

    int WHAT_CANCEL_RESERVE = 11;

    @JavascriptInterface
    public void jsCancleOrder(final String guid) {
        Logger.i("guid_cancle", guid + "");
        runOnUiThread(new Runnable() {
            public void run() {
                cancelDialog(guid);
            }
        });
    }

    @JavascriptInterface
    public void jsToProWebActivity(String shopID, String proName, String proPrice) {
        Intent intent = new Intent(context, ProductDetailWeb.class);
        intent.putExtra(ProductDetailWeb.KEY_PAGE_TYPE, 0);
        intent.putExtra(ProductDetailWeb.KEY_SHOP_ID, shopID);
        intent.putExtra(ProductDetailWeb.KEY_PRO_NAME, proName);
        intent.putExtra(ProductDetailWeb.KEY_PRO_PRICE, proPrice);
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsToShopDetail(String agentID) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_SHOP_ID, agentID);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Dialog askDialog = null;

    public void cancelDialog(final String guid) {
        askDialog = MyDialog.getDefineDialog(context, "取消订单", "注意： 订单取消后无法找回");
        final Button b_cancel = (Button) askDialog.findViewById(R.id.b_dialog_left);
        final Button b_sure = (Button) askDialog.findViewById(R.id.b_dialog_right);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                askDialog.dismiss();
            }
        });
        b_sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                b_sure.setText("正在取消...");
                b_sure.setEnabled(false);
                new Thread(new RCancelOrder(guid)).start();
            }
        });
        askDialog.show();
    }

    public class RCancelOrder implements Runnable {
        String guid = null;

        /**
         * 取消线下订单
         */
        public RCancelOrder(String guid) {
            this.guid = guid;
        }

        public void run() {
            String json = appManager.postCancelReserveOrder(guid, getUid());
            if ("true".equals(json)) {
                Message msg = new Message();
                msg.obj = guid;
                msg.what = WHAT_CANCEL_RESERVE;
                if (myHandler != null)
                    myHandler.sendMessage(msg);
            } else {
                judgeIsTokenException(json, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, final String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                jsShowMsg(info);
//                                quitAccount();
//                                finish();
//                                jsStartActivity("LoginActivity", "");
//                            }
//                        });
                    }

                    @Override
                    public void ok() {
                        jsShowMsg("取消失败");
                    }
                });

            }
        }
    }

    public Runnable rGetOfflineData = new Runnable() {
        public void run() {
            String uid = db.GetLoginUid(context);
            String reserveMsg = appManager.postGetReserveMsg(uid);

            Message msg = new Message();
            msg.obj = reserveMsg;
            msg.what = WHAT_LOAD_OFFLINE;
            if (myHandler != null)
                myHandler.sendMessage(msg);
        }
    };

    public MyHandler myHandler = new MyHandler(this);
    public final int WHAT_LOAD_OFFLINE = 10;

    public class MyHandler extends Handler {
        WeakReference<MyOrderActivity> wAct = null;
        MyOrderActivity moa = null;

        public MyHandler(MyOrderActivity act) {
            wAct = new WeakReference<>(act);
            moa = wAct.get();
        }

        public void handleMessage(Message msg) {
            if (msg.what == moa.WHAT_LOAD_OFFLINE) {

                moa.hideLoadDialog();
                if (msg.obj != null) {
                    final String reserveMsg = msg.obj.toString().replaceAll("\n", "");
                    judgeIsTokenException(reserveMsg, new TokenExceptionCallBack() {
                        @Override
                        public void tokenException(String code, final String info) {
                            sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    jsShowMsg(info);
//                                    quitAccount();
//                                    finish();
//                                    jsStartActivity("LoginActivity", "");
//                                }
//                            });
                        }

                        @Override
                        public void ok() {
                            moa.webView.loadUrl("javascript:javaLoadOrderCenterOffLine('" + reserveMsg + "','" + (Math.abs(moa.tabIndex) - 1) + "')");
                        }
                    });
                    Logger.i("预订单数据", reserveMsg + "");
                } else
                    moa.webView.loadUrl("javascript:javaLoadOrderCenterOffLine('','0')");

            } else if (msg.what == moa.WHAT_CANCEL_RESERVE) {

                CommonTools.showShortToast(moa, "订单取消成功");
                moa.askDialog.dismiss();
                moa.webView.loadUrl("javascript:javaDeleteOffLineOrder('" + msg.obj.toString() + "')");

            }
        }
    }

    public void showCancleSucDialog(String title, String content) {
        final Dialog v = MyDialog.getDefineDialog(context, title, content);
        v.findViewById(R.id.b_dialog_left).setVisibility(View.GONE);
        v.findViewById(R.id.b_dialog_right).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                v.dismiss();
            }
        });
    }

    public void initTitle(String title) {

//        tv_title.setText(title);
        findViewById(R.id.b_title_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
        isOrderToPay = false;// 初始化
        MyOrderData.tOrderNumber = null;
        if (isNeedInitMyOrderData)
            myOrderData.clearData();
        // Toast.makeText(context, isOrderToPay+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        manager.dispatchDestroy(isFinishing());
    }

    @Override
    protected void findViewById() {
        title_layout = (ViewGroup) findViewById(R.id.ll_title_order);
        tv_title = (TextView) findViewById(R.id.tv_title);
        root = ((RelativeLayout) findViewById(R.id.root));
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e(TAG, "onActivityResult 接收到数据变化");
        /**
         * 因为虚指向的问题，通过发广播的方式通知子tab页更新数据
         */
        try {
            String orderNumber = data.getStringExtra("orderNumber");
            sendActivityResultBroadcast(requestCode, resultCode, orderNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
