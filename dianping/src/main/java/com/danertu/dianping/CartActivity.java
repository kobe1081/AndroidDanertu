package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.entity.ShopCar;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebChromeClient;
import com.danertu.widget.MWebViewClient;
import com.google.gson.Gson;

import wl.codelibrary.widget.IOSDialog;

public class CartActivity extends HomeActivity {

    // private Button cart_market, goShopping, cart_go;
    private Button del_btn;
    // private View layout_noProduct;
    private Intent mIntent;

    // private ListView lv;
    List<ShopCar> carEntity;
    // private GoodsCollectAdapter adp1;
    public ArrayList<Map<String, Object>> list = new ArrayList<>();
    // private String givecount;
    private int totalgivecount = 0;
    private int curselectcount = 0;

    View foot;
    public static final String KEY_SHOPCAR_LIST = "ShoppingCarList";
    public static final int REQ_PAYCENTER = 1;
    private final int REQ_LOGIN = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        String proAct = getIntent().getStringExtra("isProductDetail");
        boolean isProAct = !TextUtils.isEmpty(proAct) && Boolean.parseBoolean(proAct);
        if (isProAct) setTabVisibility(View.GONE);
        initWebView();
//		bindData();
        initTitle("购物车");
        if (!isLogin()) {
            jsStartActivityForResult("LoginActivity", "", REQ_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_LOGIN && resultCode == LoginActivity.LOGIN_FAILURE) {
            finish();
        }
    }

    private void initWebView() {
        // customize webview function
        this.webView = (WebView) findViewById(R.id.wv_container);
        this.webView.setWebChromeClient(new MWebChromeClient(this));

        webView.setWebViewClient(new MWebViewClient(this, "app"));
        initWebSettings();
    }

    public void onResume() {
        super.onResume();
        if (isLogin()) {
            BindData();
            setDelVisibility(list.size());
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initWebSettings() {
        WebSettings mWebSettings;
        mWebSettings = webView.getSettings();
        mWebSettings.setDefaultTextEncodingName("utf-8");
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(this, "app");
    }

    Gson gson = new Gson();

    @JavascriptInterface
    public void jsLoadShoppingCarList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String jsonStr = gson.toJson(list);
                Logger.i("购物车数据", jsonStr + "");
                webView.loadUrl(Constants.IFACE + "javaLoadProductList('" + jsonStr + "')");
            }
        });
    }

    @JavascriptInterface
    public void jsUpdatePrice(int id, double price) {
        jsUpdatePrice(id, price, null);
    }

    @JavascriptInterface
    public void jsUpdatePrice(int id, double price, String attrJson) {
        HashMap<String, Object> item = (HashMap<String, Object>) list.get(id);
        final String guid = String.valueOf(item.get(k_productID).toString());
        final String shopID = item.get(DBHelper.SHOPCAR_SHOPID).toString();
        Object att = item.get(DBHelper.SHOPCAR_ATTRJSON);
        attrJson = att == null ? null : att.toString();
        db.updateProductPriceInCar(getContext(), guid, getUid(), price, attrJson, shopID);
        item.put(k_price, price);
    }

    @JavascriptInterface
    public void jsUpdateProductCount(final int id, int count) {
        jsUpdateProductCount(id, count, null);
    }

    @JavascriptInterface
    public void jsUpdateProductCount(final int id, int count, String attrJson) {
        HashMap<String, Object> item = (HashMap<String, Object>) list.get(id);
        final String guid = String.valueOf(item.get("productID").toString());
        final String shopID = item.get(DBHelper.SHOPCAR_SHOPID).toString();
        Object att = item.get(DBHelper.SHOPCAR_ATTRJSON);
        attrJson = att == null ? null : att.toString();
        db.updateProductCountInCar(getContext(), guid, getUid(), count, attrJson, shopID);
        item.put("count", count);
    }

    @JavascriptInterface
    public void jsSetProductSelect(int id, int status) {
        if (status == 1) {
            list.get(id).put("selected", true);
        } else if (status == 0) {
            list.get(id).put("selected", false);
        }
    }

    @JavascriptInterface
    public void jsSetArriveTime(int id, String date) {
        if (date.length() > 10) {
            list.get(id).put("arriveTime", date);
        }
    }

    @JavascriptInterface
    public void jsSetLeaveTime(int id, String date) {
        if (date.length() > 10) {
            list.get(id).put("leaveTime", date);
        }
    }

    @JavascriptInterface
    public void jsUpdateSelectCount(int count) {
        curselectcount = count;
    }

    @JavascriptInterface
    public void jsStartIndexActivity() {
        application.backToActivity("IndexActivity");
    }

    boolean isToPay = false;

    @JavascriptInterface
    public boolean jsStartPayMementCenterActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isToPay)
                    return;
                if (!checkErrorMsg()) {
                    int allCount = 0;
                    double totalMoney = 0;

                    Iterator<Map<String, Object>> iter = list.iterator();
                    while (iter.hasNext()) {
                        Map<String, Object> item = iter.next();
                        if ((Boolean) item.get("selected")) {
                            double price = Double.parseDouble(item.get("price").toString());
                            String agentid = item.get("agentID").toString();
                            int count = Integer.parseInt(item.get("count").toString());
                            allCount += count;
                            if (agentid != null && agentid.equals("")) {
                                totalMoney += (price * count);
                            }
                        } else {
                            iter.remove();
                        }
                    }

                    mIntent = new Intent();
                    mIntent.setClassName(getApplicationContext(), "com.danertu.dianping.PaymentCenterActivity");
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                    Bundle b = new Bundle();
                    b.putString("allCount", String.valueOf(allCount + totalgivecount));
                    b.putString("allMoney", String.valueOf(totalMoney));
                    // 此处使用putExtras，接受方就响应的使用getExtra
                    mIntent.putExtra(KEY_SHOPCAR_LIST, list);
                    mIntent.putExtras(b);
                    startActivityForResult(mIntent, REQ_PAYCENTER);
                    isToPay = true;
                    // finish();
                } else
                    isToPay = false;
            }
        });
        return isToPay;
    }

    private boolean checkErrorMsg() {
        if (curselectcount <= 0) {
            showErrorDialog("提示", "请至少选择一项商品！");
            return true;
        }
        for (Map<String, Object> item : list) {
            if ((Boolean) item.get("selected") && (Boolean) item.get("isQuanYanProduct")) {
                if (TextUtils.isEmpty(item.get("arriveTime").toString())) {
                    showErrorDialog("提示", "请确认已选定 预计抵达时间！");
                    return true;
                }
                if ((Boolean) item.get("isQuanYanHotel")) {
                    if (TextUtils.isEmpty(item.get("leaveTime").toString())) {
                        showErrorDialog("提示", "请确认已选定 预计抵达时间 和 预计离开时间！");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @JavascriptInterface
    public void showErrorDialog(String title, String msg) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("确定", null).show();
    }

    public void initTitle(String title) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title2);
        tv_title.setText(title);
        findViewById(R.id.b_title_back2).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
//				jsStartIndexActivity();
            }
        });
        del_btn = (Button) findViewById(R.id.b_title_operation2);
        del_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Logger.e("test","del_btn_click");
                if (del_btn.getText().equals(getString(R.string.car_edit))) {
                    Logger.e("test","del_btn_click "+del_btn.getText());
                    del_btn.setText(getString(R.string.car_done));
                    webView.loadUrl(Constants.IFACE + "javaEditProduct(true)");
                } else {
                    Logger.e("test","del_btn_click "+del_btn.getText());
                    del_btn.setText(getString(R.string.car_edit));
                    webView.loadUrl(Constants.IFACE + "javaEditProduct(false)");
                }
            }
        });
        findViewById(R.id.msgbox).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
//                jsStartActivity("MessageCenterActivity", null);
                Intent intent = new Intent();
                intent.putExtra("memberid", getUid());
                intent.setClass(getApplicationContext(), MessageCenterActivity.class);
                startActivity(intent);
//                startActivity(new Intent(context,JPushMessageActivity.class));
            }
        });
    }

    @JavascriptInterface
    public void deleteProItem() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (curselectcount <= 0) {
                    CommonTools.showShortToast(getContext(), "请选择你要删除的商品");
                    return;
                }

                final IOSDialog iosDialog = new IOSDialog(context);
                iosDialog.setTitle("删除商品");
                iosDialog.setMessage("确定要从购物车删除所有选择商品？");
                iosDialog.setNegativeButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosDialog.dismiss();
                        String uid = getUid();
                        Iterator<Map<String, Object>> iter = list.iterator();
                        while (iter.hasNext()) {
                            Map<String, Object> item = iter.next();
                            if ((Boolean) item.get("selected")) {
                                String guid = item.get("productID").toString();
                                Object param = item.get(DBHelper.SHOPCAR_ATTRJSON);
                                String shopid = item.get(DBHelper.SHOPCAR_SHOPID).toString();
                                String attrParam = param == null ? null : param.toString();
                                db.delProductInCar(CartActivity.this, guid, uid, attrParam, shopid);
                                iter.remove();
                            }
                        }
                        webView.loadUrl(Constants.IFACE + "javaReloadProductList()");
                        if (list.size() <= 0) {
                            loadPage("Android_shoppingcart_tip.html");
                            del_btn.setVisibility(View.GONE);
                        }
                        initShopCarCount();
                    }
                });
                iosDialog.setPositiveButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        iosDialog.dismiss();
                    }
                });
                iosDialog.show();
            }
        });
    }

    public void loadPage(String pageName) {
        startWebView(Constants.appWebPageUrl + pageName);
    }

    public void startWebView(String url) {
        webView.loadUrl(url);
    }

    public void setDelVisibility(int listSize) {
        if (listSize > 0) {
            del_btn.setText(getString(R.string.car_edit));
            del_btn.setVisibility(View.VISIBLE);
            loadPage("Android_shoppingcart.html");

        } else {
            del_btn.setVisibility(View.GONE);
            loadPage("Android_shoppingcart_tip.html");
        }
    }

    public final static String k_isQuanYanProduct = "isQuanYanProduct";
    public final static String k_isQuanYanHotel = "isQuanYanHotel";
    public final static String k_productID = "productID";
    public final static String k_proName = "proName";
    public final static String k_price = "price";
    public final static String k_count = "count";
    public final static String k_agentID = "agentID";
    public final static String k_imgURL = "imgURL";
    public final static String k_supplierID = "supplierID";
    public final static String k_shopID = "shopID";
    public final static String k_selected = "selected";
    public final static String k_arriveTime = "arriveTime";
    public final static String k_leaveTime = "leaveTime";

    private void BindData() {
        isToPay = false;
        Cursor cursor = db.GetShopCar(getContext(), db.GetLoginUid(getContext()));
        if (cursor == null)
            return;
        list = new ArrayList<>();
        int size = cursor.getCount();

        for (int i = 0; i < size; i++) {
            cursor.moveToPosition(i);
            String productID = cursor.getString(0);
            String proName = cursor.getString(1);
            String price = cursor.getString(3);
            String count = cursor.getString(5);
            String agentID = cursor.getString(2);
            String imgName = cursor.getString(4);
            String supplierID = cursor.getString(7);
            String shopID = cursor.getString(8);
            String attrJson = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_ATTRJSON));
            String createUser = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_CREATEUSER));
            String shopName = cursor.getString(cursor.getColumnIndex(DBHelper.SHOPCAR_SHOPNAME));
//( isSelect,  productID,  buycount,  imgName,  supplierID, shopID,  agentID,  proName,  price,  marketPrice,  createUser,  attrJson,  arriveTime,  leaveTime,  shopName,  discountNum,  discountPrice)
            HashMap<String, Object> dataMap = ActivityUtils.getShopCarItem(false, productID, count, imgName, supplierID, shopID, agentID, proName, price, "", createUser, attrJson, "", "", shopName, "", "");
            list.add(dataMap);

        }
    }
}
