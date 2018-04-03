package com.danertu.dianping;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;

public class WapActivity extends BaseWebActivity {
    Context context;
    //	WebView webView;
    private Button b_opera;

    public static boolean isRecorded = false;
    public static boolean is1YFQ = false;
    public static String guid;
    public static String proName;
    public static String img;
    public static String shopid;
    public static String detail;
    public static String agentID;
    public static String supplierID;
    public static String price;

    public static final String KEY_WEB_TITLE = "webTitle";
    public static final String KEY_WEB_PNUM = "webPNum";
    public static final String KEY_WEB_URL = "webUrl";
    private String title = null;
    private String url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initIntentMsg();
        initTitle(title);
        initView();
    }

    private void initIntentMsg() {
        Intent intent = getIntent();
        title = intent.getStringExtra(KEY_WEB_TITLE);
        url = intent.getStringExtra(KEY_WEB_URL);
    }

    private void initTitle(String title) {
        Button b[] = setTitle(title, null);
//		Button b_title = (Button) findViewById(R.id.b_title_back2);
//		b_opera = (Button) findViewById(R.id.b_title_operation2);
//		b_opera.setVisibility(View.GONE);
//		b_title.setText(title);
        b[0].setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                setResult(IndexActivity.REQUEST_WAP);
                finish();
            }
        });
    }

    @JavascriptInterface
    public void jsSetOperation(final String operaName, final String url) {
        if (b_opera == null || webView == null)
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                b_opera.setVisibility(View.VISIBLE);
                b_opera.setText(operaName);
                b_opera.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        webView.loadUrl(url);
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (title.equals("0.1元购")) {
                finish();
            } else if (webView.canGoBack())
                webView.goBack();
            else {
                setResult(IndexActivity.REQUEST_WAP);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void findViewById() {
        webView = (WebView) findViewById(R.id.wv_container);
    }

    @Override
    protected void initView() {
//		loadingDialog = new LoadingDialog(context);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        webView.addJavascriptInterface(this, IndexActivity.WV_INTERFACE);
        loadWapContent();

    }

    private void setWebClient(WebView webView) {
        webView.setWebViewClient(new MWebViewClient(this, IndexActivity.WV_INTERFACE) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }

            public void onPageFinished(WebView view, String url) {
                if (title.equals("特价区")) {
                    new Thread(rGetTJQPro).start();
                } else
                    hideLoadDialog();
                super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoadDialog();
                super.onPageStarted(view, url, favicon);
            }

        });
    }

    final int WHAT_GETTJQPRO_SUCCESS = 1;
    public Runnable rGetTJQPro = new Runnable() {
        public void run() {
            String result = AppManager.getInstance().postGetTJQPro();
            Message msg = new Message();
            msg.what = WHAT_GETTJQPRO_SUCCESS;
            msg.obj = result;
            myHandler.sendMessage(msg);
        }
    };

    public MyHandler myHandler = new MyHandler(this);

    public static class MyHandler extends Handler {
        WeakReference<WapActivity> wAct;
        WapActivity wa = null;

        public MyHandler(WapActivity wa) {
            wAct = new WeakReference<>(wa);
            this.wa = wAct.get();
        }

        public void handleMessage(Message msg) {
            if (msg.what == wa.WHAT_GETTJQPRO_SUCCESS) {
                wa.hideLoadDialog();
                Object result = msg.obj;
                if (result == null) {
                    CommonTools.showShortToast(wa, "特价商品为:" + result);
                    return;
                }
                wa.javaLoadTeJiaQu(result.toString());
            }
        }
    }

    public void javaLoadTeJiaQu(String jsonStr) {
        Logger.i("特价商品信息", jsonStr + "");
        webView.loadUrl("javascript:javaLoadTeJiaQu('" + jsonStr + "')");
    }

    @JavascriptInterface
    public void loadWapContent() {
        // 加载assets文件夹下的default.htm（测试时用）
        runOnUiThread(new Runnable() {
            public void run() {
                setWebClient(webView);
                try {
                    webView.loadUrl(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @JavascriptInterface
    public void jsGet01Pro(final String url) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!isLogined()) {
                    startActivity(new Intent(context, LoginActivity.class));
                } else {
                    webView.loadUrl(url);
                }
            }
        });
    }

    /**
     * 跳转到商品详细页面
     *
     * @param guid
     * @param proName
     * @param img
     * @param shopid
     * @param proDetail
     * @param tAgentid
     * @param supplierId
     * @param price
     */
    @JavascriptInterface
    public void jsToProDetailActivity(String guid, String proName, String img, String shopid, String proDetail, String tAgentid, String supplierId, String price) {

        ActivityUtils.toProDetail2(context, guid, proName, img, proDetail, "", supplierId, price, "0", "", "0", 2);
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
        intent.setClass(this, IndexTypeActivity.class);
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsStartNoticectivity() {
        Toast.makeText(this, "公告", Toast.LENGTH_SHORT).show();
    }

    /**
     * 通过js提供的产品id启动列表的项的详细信息activity
     *
     * @param shopid 产品id
     */
    @JavascriptInterface
    public void jsStartDetailActivity(final String shopid) {
//		 Toast.makeText(this, "点击事件"+shopId, Toast.LENGTH_SHORT).show();
        this.runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                Bundle b = new Bundle();
                b.putString("shopid", shopid);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
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
        Logger.e("uid--", uid);
        if (uid != null && uid.trim().length() > 0) {
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.HuDongActivity");
            startActivity(intent);
        } else {
            CommonTools.showShortToast(this, "请先登录！");
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


    @JavascriptInterface
    public void jsStartJLB() {
        Intent intent = new Intent();
        String uid = db.GetLoginUid(getApplicationContext());
        if (uid == null || uid.trim().equals("")) {
            CommonTools.showShortToast(getApplicationContext(), "请先登录");

            intent.setClass(getApplicationContext(), LoginActivity.class);
        } else {
            intent.setClass(getApplicationContext(), ScoreCenterActivity.class);
        }
        startActivity(intent);
    }

    @JavascriptInterface
    public void jsShowPage(String url) {
        this.url = url;
        loadWapContent();
    }

    @JavascriptInterface
    public void jsShowError(String error) {
        CommonTools.showShortToast(context, error);
    }

    @JavascriptInterface
    public void jsStart1YGProActivity(String pNum) {
//		Intent intent = new Intent(this, ProductDetailsActivity.class);
        if (pNum != null && !pNum.equals("")) {
            String price = Constants.price01;
            String proName = Constants.proName01;
            String agentID = Constants.agentID01;
            String guid = Constants.guid01;
            String detail = "";
            supplierID = "";
            String mobile = "400-995-2220";
            Constants.testedMobile = pNum;
            ActivityUtils.toProDetail2(context, guid, proName, "20140819143356515.jpg", detail, agentID, supplierID, price, "50", mobile, "0", 1);
//			startActivity(intent);
        } else {
            CommonTools.showShortToast(this, "请输入手机号");
        }
    }

    /**
     * 一元秒杀
     *
     * @param isOpen 0表示秒杀进行中，1表示秒杀停止
     */
    @JavascriptInterface
    public void jsStart1YFQProActivity(String guid1, String proName1, String img1, String shopid1, String detail1, String agentID1, String supplierID1, String price1, int isOpen) {
        is1YFQ = true;
        guid = guid1;
        proName = proName1;
        img = img1;
        shopid = shopid1;
        detail = detail1;
        agentID = agentID1;
        supplierID = supplierID1;
        price = "1";
        Logger.i("活动是否开始", isOpen + " : 0表示秒杀进行中，1表示秒杀停止");
        int count = isOpen == 0 ? 1 : 0;
        img1 = img1.substring(img1.lastIndexOf("/") + 1, img1.length());
        ActivityUtils.toProDetail2(context, guid1, proName1, img1, detail1, agentID1, supplierID1, price, "0", "", "0", count);
    }

    public void onDestroy() {
        super.onDestroy();
        is1YFQ = false;
        guid = null;
        proName = null;
        img = null;
        shopid = null;
        detail = null;
        agentID = null;
        supplierID = null;
        price = null;
//		webView.stopLoading();
    }
}
