package com.danertu.dianping;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.XNUtil;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;

/**
 * 本activity既包含线下产品也包括线上支付产品
 * 需要的参数有页面的url（线下产品和线上产品区分开来）
 * 店铺信息json数据
 * 产品信息json数据及选中的下标;;;
 *
 * @author dengweilin
 * @see ： 存进SQLite里如果guid最后有 空格 则表示是线下产品，否则线上产品
 */
public class ProductDetailWeb extends BaseActivity {
    public static final String KEY_SHOP_JSON = "shopJson";
    public static final String KEY_PRO_LIST_JSON = "proListJson";
    public static final String KEY_PRO_INDEX = "proIndex";
    public static final String KEY_SHOP_ID = "shopid";
    /**
     * 0表示线下产品，否则表示线上支付产品
     */
    public static final String KEY_PAGE_TYPE = "type";
    public static final String KEY_PRO_NAME = "proName";
    public static final String KEY_PRO_PRICE = "proPrice";

    private Button b_ok = null;
    private Button b_contact = null;
    private Context context = null;
    private FrameLayout title_group;
    Button b_title = null;
    final String TITLE1 = "图文详情";
    final String TITLE = "商品详细";
    final String TITLE_RESERVE = "确认预定";
    final String TITLE_SUBMIT_SUCCESS = "提交成功";
    private final String WV_INTERFACE = "iface_pro_detail";
    private WebSettings setting = null;
    private String shopJson = null;
    private String proListJson = null;
    private int proIndex = 0;
    /**
     * 0表示线下产品，否则表示线上支付产品
     */
    private int pageType = 0;
    private String shopId = null;
    private String url = null;
    private String shopName;
    private String shopLa, shopLt;
    private String phoneNumber = null;
//	private String guid = null;
    /**
     * 预订确认页面
     */
    final String WEBPAGE_CONFIRM_OFFLINE = "Android_offline_confirm.html";
    /**
     * 线下商品详情页面
     */
    final String WEBPAGE_OFFLINE = "Android_offline_proDetail.html";
    /**
     * 预订成功页面
     */
    final String WEBPAGE_RESERVE_SUCCESS = "Android_offline_resSuccess.html";
    private String proName = null;
    private String proPrice = null;

    private XNUtil xnUtil;

    @JavascriptInterface
    public void contactService() {
        xnUtil.communicte();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_pro_detail_webpage);
        initTitle(TITLE);
        initIntentMsg();
        findViewById();
        initView();
    }

    private void initIntentMsg() {
        Intent intent = getIntent();
        shopJson = intent.getStringExtra(KEY_SHOP_JSON);
        proListJson = intent.getStringExtra(KEY_PRO_LIST_JSON);
        proIndex = intent.getIntExtra(KEY_PRO_INDEX, 0);
        //其他入口这两个必须得实例化到
        pageType = intent.getIntExtra(KEY_PAGE_TYPE, 0);
        shopId = intent.getStringExtra(DetailActivity.KEY_SHOP_ID);
        //从预订中心过来的参数
        proName = intent.getStringExtra(KEY_PRO_NAME);
        proPrice = intent.getStringExtra(KEY_PRO_PRICE);

    }

    private void initXNParam() {
        xnUtil = new XNUtil(this);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONObject(proListJson).getJSONObject("shopprocuctList").getJSONArray("shopproductbean");
            JSONObject item = jsonArray.getJSONObject(proIndex);
            String price = item.getString("ShopPrice");
            String name = item.getString("Name");
            String guid = item.getString("Guid");
            String agentID = item.getString("AgentId");
            String imgName = item.getString("SmallImage");
            imgName = imgName.equals("") ? item.getString("OriginalImge") : imgName;
            String imgPath = getImgUrl(imgName, agentID, "");
            xnUtil.setTitle(name);
            xnUtil.setUsername(getUid());
            xnUtil.setUserid(getUid());
            xnUtil.setOrderprice(price);
            String goodsUrl = "http://" + shopId + ".danertu.com/ProductDetail/" + guid + ".html";
            xnUtil.setItemparam(xnUtil.genProParam(guid, name, Double.parseDouble(price), imgPath, goodsUrl, goodsUrl));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void setCustomServiceViewVisi(int visibility) {
        b_contact.setVisibility(visibility);
        findViewById(R.id.line).setVisibility(visibility);
    }

    public final int LOAD_WEBPAGE = 1;

    protected void findViewById() {
        b_ok = (Button) findViewById(R.id.b_order_ok);
        b_contact = (Button) findViewById(R.id.b_contact_service);
        title_group = (FrameLayout) findViewById(R.id.proDetail_top_layout);
        webView = (WebView) findViewById(R.id.wv_proDetail_content);
    }

    /**
     * 注意： 存进SQLite里如果guid最后有 “-”号 则表示是线下产品，否则线上产品
     */
    @JavascriptInterface
    public void collectPro(String guid, String proName,
                           String price, String imgName, String supplierID, String detail) {
        Logger.e("收藏商品_线下", guid + "");
        if (!db.isLogin(context))
            return;
        String uid = db.GetLoginUid(this);
        Cursor cursor = db.allreadyCollectProduct(this, guid, uid);
        if (cursor.getCount() > 0) {
            CommonTools.showShortToast(this, "您已经收藏过该商品了！");
        } else {
            db.InsertCollectProduct(this, guid, proName, shopId, price, imgName, uid, supplierID, detail);
            CommonTools.showShortToast(this, "商品收藏成功！");
        }
    }

    private Runnable rInitData = new Runnable() {
        @Override
        public void run() {
            AppManager appManager = AppManager.getInstance();
            try {
                //获取店铺详细信息
                shopJson = appManager.postGetShopDetails("0041", shopId);
                //获取店铺线下产品
                proListJson = appManager.postGetShopProduct("0042", shopId);
                initProJson(proListJson, -1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    initXNParam();
                    xnUtil.postCustomerTrack();
                    webView.reload();
                    setOnContactClick();
                }
            });
        }

    };

    private void setOnContactClick() {
        b_contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contactService();
            }
        });
    }

    String imgPath = "";

    /**
     * @param proListJson 产品列表json
     * @param index       被选中的产品底标，若无则传入小于的数
     * @throws JSONException Exception
     */
    public void initProJson(String proListJson, int index) {
        try {
            JSONArray jsonArr = new JSONObject(proListJson).getJSONObject("shopprocuctList").getJSONArray("shopproductbean");
            String agentID = "";
            String imgName = "";
            if (index < 0) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject item = jsonArr.getJSONObject(i);
                    // String proID = item.getString("Guid");
                    String proPri = item.getString("ShopPrice");
                    String proNam = item.getString("Name");
                    double pPrice = Double.parseDouble(proPri);
                    double cPri = Double.parseDouble(proPrice);
                    agentID = item.getString("AgentId");
                    imgName = item.getString("SmallImage");
                    imgName = imgName.equals("") ? item.getString("OriginalImge") : imgName;
                    if (proNam.equals(proName) && pPrice == cPri) {
                        proIndex = i;
                        break;
                    }
                }
            } else {
                JSONObject item = jsonArr.getJSONObject(index);
                proName = item.getString("Name");
                agentID = item.getString("AgentId");
                imgName = item.getString("SmallImage");
                imgName = imgName.equals("") ? item.getString("OriginalImge") : imgName;
            }
            imgPath = getImgUrl(imgName, agentID, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void initView() {
        if (pageType == 0) {//线下产品url
            title_group.setBackgroundResource(R.drawable.app_title_bg_offline);
            //程序发布时地址
            url = Constants.appWebPageUrl + WEBPAGE_OFFLINE;
        } else {//线上产品url
            title_group.setBackgroundResource(R.drawable.app_title_bg);
        }
        setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, WV_INTERFACE);
        if (shopJson == null || proListJson == null) {
            showLoadDialog();
            new Thread(rInitData).start();
        } else {
            initXNParam();
            xnUtil.postCustomerTrack();
            setOnContactClick();
        }
        webView.loadUrl(url);
        webView.setWebViewClient(new MWebViewClient(this, WV_INTERFACE) {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                final String title = b_title.getText().toString();
                if (title.equals(TITLE)) {
                    setCustomServiceViewVisi(View.VISIBLE);
                    b_ok.setVisibility(View.VISIBLE);
                    b_ok.setText("分享");
                } else {
                    setCustomServiceViewVisi(View.GONE);
                    b_ok.setVisibility(View.GONE);
                }
                if (isOrderComplete) {
                    b_title.setText(TITLE_SUBMIT_SUCCESS);
                    b_ok.setVisibility(View.VISIBLE);
                    b_ok.setText("完成");
                    webView.loadUrl("javascript:javaLoadSuccessOffLineOrder('" + reserveNum + "','" + shouldPay + "')");
                    isOrderComplete = false;
                } else {
                    javaLoadContent(shopJson, 2);// 必须首先实例化店铺数据
                    javaLoadContent(proListJson, 1);
                    javaLoadUserInfo(recName, recMobile, recAddress);
                }
                hideLoadDialog();
                b_ok.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        String title = b_title.getText().toString();
                        if (title.equals(TITLE_SUBMIT_SUCCESS))
                            finish();
                        else if (title.equals(TITLE)) {
                            jsStartActivity("QRCodeActivity", QRCodeActivity.KEY_PRO_NAME + "|" + proName + ",;" + QRCodeActivity.KEY_PRO_IMG_PATH + "|" + imgPath + ",;shopid|" + shopId);
                        }
                    }
                });
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadDialog();
            }

        });
    }

    /**
     * 加载html内容
     *
     * @param jsonStr json
     * @param type    1表示产品数据， 2表示店铺信息数据
     * @author dengweilin
     */
    private void javaLoadContent(String jsonStr, int type) {
        if (jsonStr == null)
            return;
        jsonStr = jsonStr.replaceAll("\n", "");
        jsonStr = jsonStr.replaceAll("\r", "");
        if (type == 1) {
            initProJson(jsonStr, proIndex);
            webView.loadUrl("javascript:javaLoadProDetail('" + jsonStr + "'," + proIndex + ")");
        } else if (type == 2) {
            webView.loadUrl("javascript:javaLoadShopDetail('" + jsonStr + "')");
        }
    }

    private void javaLoadUserInfo(String name, String mobile, String address) {
        webView.loadUrl("javascript:javaLoadUserInfo('" + name + "','" + mobile + "','" + address + "')");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode) {
            initDefaultContactMsg();
            javaLoadUserInfo(recName, recMobile, recAddress);
        }
    }

    @JavascriptInterface
    public void jsInitShopData(String shopName, String shopLa, String shopLt, String phoneNum) {
        this.phoneNumber = phoneNum;
        this.shopName = shopName;
        this.shopLa = shopLa;
        this.shopLt = shopLt;
    }

    @JavascriptInterface
    public void jsPhoneCall() {
        dial(phoneNumber);
    }

    boolean isOrderComplete = false;
    /**
     * 预订应付金额
     */
    double shouldPay = 0;
    /**
     * 预订成功返回的订单号
     */
    String reserveNum = null;

    //确定预订
    @JavascriptInterface
    public void jsSureReserve(String proGuid, String proName, String buyCount, String realPrice, String marketPrice) {
        if (recName == null || recName.equals("")) {
            CommonTools.showShortToast(context, "警告: 请先到个人中心填写收货人信息");
            return;
        }
        try {
            double real = Double.parseDouble(realPrice);
            int count = Integer.parseInt(buyCount);
            shouldPay = real * count;
            String uid = db.GetLoginUid(context);
            reserveNum = AppManager.getInstance().postReserve(uid, shopId, proGuid, recName, recAddress, recMobile, count, shouldPay, real, proName, shopName);
            if (!"".equals(reserveNum)) {
                isOrderComplete = true;
                runOnUiThread(new Runnable() {
                    public void run() {
                        webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_RESERVE_SUCCESS);
                    }
                });
            } else {
                CommonTools.showShortToast(context, "预订失败，请重新开启程序预订");
            }
        } catch (Exception e) {
            CommonTools.showShortToast(context, "页面出现异常，暂不提供预订功能");
            return;
        }
        Logger.i("确定预订_提交数据", proName + " , " + buyCount + " , " + realPrice + " , " + marketPrice + " , shouldPay：" + shouldPay + " , " + proGuid);
    }

    /**
     * 图文详情
     *
     * @param proDetail 商品详细介绍，若无则直接传""。
     */
    @JavascriptInterface
    public void jsShowTeletext(final String proDetail) {
        if (proDetail == null || proDetail.equals("")) {
            CommonTools.showShortToast(this, "此商品暂无详细介绍");
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                b_title.setText(TITLE1);
                String data = Html.fromHtml(proDetail).toString();
                setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
                webView.loadData(data, "text/html; charset=UTF-8", null);
            }
        });
    }

    /**
     * 到订单中心
     *
     * @param index 0全部 1待付款 2
     */
    @JavascriptInterface
    public void jsToOrderActivity(int index) {
        if (index < 0) {
            MyOrderActivity.isNeedInitMyOrderData=false;
            Intent intent = new Intent(context, MyOrderActivity.class);
            intent.putExtra("TabIndex", index);
            startActivity(intent);

//            myOrderData=new MyOrderData(this,false) {
//                @Override
//                public void getDataSuccess() {
//
//                }
//            };
            finish();
        }
    }

    public void finish() {
        if (b_title == null || webView == null) {
            super.finish();
        } else if (webView.canGoBack()) {
            String title = b_title.getText().toString();
            webView.goBack();
            if (title.equals(TITLE_SUBMIT_SUCCESS))
                b_title.setText(TITLE_RESERVE);
            else
                b_title.setText(TITLE);
        } else
            super.finish();
    }

    private Dialog dialog = null;

    @JavascriptInterface
    public void jsShowSelectDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                dialog = new Dialog(context, R.style.Dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.dialog_route_select);
                dialog.show();
                dialog.findViewById(R.id.b_route_car).setOnClickListener(dialogClick);
                dialog.findViewById(R.id.b_route_bus).setOnClickListener(dialogClick);
                dialog.findViewById(R.id.b_route_walk).setOnClickListener(dialogClick);
            }
        });
    }

    public View.OnClickListener dialogClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_route_car:
                    toRouteGoPlanActivity("1");
                    break;
                case R.id.b_route_bus:
                    toRouteGoPlanActivity("2");
                    break;
                case R.id.b_route_walk:
                    toRouteGoPlanActivity("3");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @param type 1 自驾， 2  公交， 3  步行
     */
    @JavascriptInterface
    public void toRouteGoPlanActivity(String type) {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.danertu.dianping.RouteGoPlanActivity");
        // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
        Bundle b = new Bundle();
        b.putString("endName", shopName);
        b.putString("la", shopLa);
        b.putString("lt", shopLt);
        b.putString("type", type);
        b.putString("cityName", Constants.getCityName());
        // 此处使用putExtras，接受方就响应的使用getExtra
        intent.putExtras(b);
        startActivity(intent);
    }

    final int UI_RESERVE = 22;

    /**
     * 立即预定
     */
    @JavascriptInterface
    public void jsReserve() {
        if (!db.isLogin(context))
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                b_title.setText(TITLE_RESERVE);
                initDefaultContactMsg();
                webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_CONFIRM_OFFLINE);
            }
        });
    }

    String recName, recMobile, recAddress;

    /**
     * 实例化默认联系方式
     */
    private void initDefaultContactMsg() {
        String uid = db.GetLoginUid(context);
        Cursor cursor = db.GetDefaultAddress(context, uid);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            recName = cursor.getString(cursor.getColumnIndex("name"));
            recMobile = cursor.getString(cursor.getColumnIndex("mobile"));
            recAddress = cursor.getString(cursor.getColumnIndex("adress"));
            String defaultTag = cursor.getString(cursor.getColumnIndex("IsDefault"));
            // recMobile = Constants.testedMobile == null ? recMobile
            // : Constants.testedMobile;
            if (defaultTag.equals("1")) {
                Logger.i("默认地址：", recName + " , " + recMobile + " , " + recAddress);
                break;
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void initTitle(String title) {
        setSystemBar(R.drawable.app_title_bg_offline);
        b_title = (Button) findViewById(R.id.b_title_back);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }
}