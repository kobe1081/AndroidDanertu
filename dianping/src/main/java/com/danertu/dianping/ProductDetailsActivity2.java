package com.danertu.dianping;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.tools.ProductDetailUtil;
import com.danertu.widget.MWebView;
//import android.view.GestureDetector;


/**
 * 2018年4月27日
 * 0026接口接口新增字段，空：普通商品，1：门票，2：客房
 * 用于跳转至不同页面
 * <p>
 * 原商品详情页prodetail.html，现在门票详情页跳转到order_tickets.html
 */
public class ProductDetailsActivity2 extends BaseWebActivity {
    private ProductDetailUtil util = null;

    /**
     * 0表示不能买，1表示只能买一件，否则可任意购买
     */
    int canBuyCount = 0;
    public static final String KEY_CAN_BUY = "canBuy";

    int proCount = 1;

    //常量------------------------------------l
    public static final String NAME = "Name";
    public static final String ORIGINAL_IMAGE = "OriginalImge";
    public static final String AGENT_ID = "AgentId";
    public static final String SUPPLIER_LOGIN_ID = "SupplierLoginID";
    public static final String SMALL_IMAGE = "SmallImage";
    public static final String MARKET_PRICE = "MarketPrice";
    public static final String SHOP_PRICE = "ShopPrice";
    public static final String DETAIL = "Detail";
    public static final String MOBILE_PRODUCT_DETAIL = "mobileProductDetail";
    public static final String CAN_FIRST_BUY = "canfirstbuy";
    public static final String CREATE_USER = "CreateUser";
    public static final String MOBILE_DETAILS_IMG_LIST = "mobileDetailsImgList";
    final String KEY_GUID = "guid";
    final String KEY_MOBILE = "mobile";
    final String KEY_SHOPID = "shopid";

    private final String PAGE_NAME = "android/proDetail.html";
    private final String PAGE_NAME_TICKET = "android/order_tickets.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.product_detail);
        findViewById();
        getPhoneStatePermission();
        initIntentMsg();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public SpannableString formatStr(String marketPrice) {
        SpannableString sp = new SpannableString(marketPrice);
        sp.setSpan(new StrikethroughSpan(), 0, marketPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    @Override
    protected void findViewById() {
        webView = (MWebView) findViewById(R.id.content);
    }

    @Override
    protected void initView() {
    }

    @JavascriptInterface
    public boolean toPayCenter(String guid, String sCount, String imgName, String supId, String shopid, String agentId, String proName, String sPrice, String createUser, String attrParam) {
        return toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, createUser, attrParam, "");
    }

    @JavascriptInterface
    public boolean toPayCenter(String guid, String sCount, String imgName, String supId, String shopid, String agentId, String proName, String sPrice, String createUser, String attrParam, String arriveTime) {
        return toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, createUser, attrParam, arriveTime, "");
    }

    /**
     * versionCode: 72
     */
    @JavascriptInterface
    public boolean toPayCenter(String guid, String sCount, String imgName, String supId, String shopid, String agentId, String proName, String sPrice, String createUser, String attrParam, String arriveTime, String shopName) {
        return util.toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, "", createUser, false, attrParam, arriveTime, "", shopName, "", "", getUid());
    }

    /**
     * versionCode: 77
     */
    @JavascriptInterface
    public boolean toPayCenter(String guid, String sCount, String imgName, String supId, String shopid, String agentId, String proName, String sPrice, String marketPrice, String createUser, String attrParam, String arriveTime, String shopName) {
        return util.toPayCenter(guid, sCount, imgName, supId, shopid, agentId, proName, sPrice, marketPrice, createUser, false, attrParam, arriveTime, "", shopName, "", "", getUid());
    }


    private GetProInfo getInfo = null;

    private void initIntentMsg() {
        Bundle bundle = getIntent().getExtras();
        try {
            String count = bundle.getString(KEY_CAN_BUY);
            if (TextUtils.isEmpty(count))
                canBuyCount = 2;
            else
                canBuyCount = Integer.parseInt(count);
        } catch (NumberFormatException e) {
            canBuyCount = 2;
        }

        util = new ProductDetailUtil(this, canBuyCount, db);
        String guid = bundle.getString("guid");
        getInfo = new GetProInfo();
        getInfo.execute(guid);
    }

    boolean isRefresh = false;

    @JavascriptInterface
    public void jsRefresh(final String param) {
        if (isLoading())
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                isRefresh = true;
                setParam(param);
                String guid = handleParamStr(param).get("guid");
                getInfo = new GetProInfo();
                getInfo.execute(guid);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getInfo != null && !getInfo.isCancelled()) {
            getInfo.cancel(true);
        }
    }

    private String proInfo = null;

    @JavascriptInterface
    public String getProInfo() {
        return proInfo;
    }

    @JavascriptInterface
    public void setProInfo(String proInfo) {
        this.proInfo = proInfo;
    }

    /**
     * 获取产品信息
     */
    private class GetProInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {
            String result = PAGE_NAME;
            String guid = arg0[0];
            try {
                String proInfo = appManager.postGetProductInfo("0026", guid, getIMEI(), getMac(), getDeviceID(), getShopId());
//                Logger.e("json","json="+proInfo);
                proInfo = proInfo.replaceAll("\n|\r", "");
                JSONObject proItem = new JSONObject(proInfo).getJSONArray("val").getJSONObject(0);
                result = "android/" + proItem.getString("pageName");

                /**
                 * 2018年4月27日添加
                 * */
//                String isQYProduct = proItem.get("IsQYProduct").toString();
//                switch (isQYProduct) {
//                    case "1"://门票
//                        result = PAGE_NAME_TICKET;
//                        break;
//                    case "2"://客房
//                        result = "android/" + proItem.getString("pageName");
//                        break;
//                    default:
//                        result = PAGE_NAME;
//                        break;
//                }
                setProInfo(proInfo);
            } catch (Exception e) {
                e.printStackTrace();
                jsShowMsg("商品信息有误");
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String pageName) {
            super.onPostExecute(pageName);
            if (isRefresh) {
                webView.loadUrl(Constants.IFACE + "javaRefresh()");
                isRefresh = false;
            } else if (!TextUtils.isEmpty(pageName)) {
                startWebView(Constants.appWebPageUrl + pageName);
            } else {
                finish();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }
    }

    @JavascriptInterface
    public boolean toShopCar(String productID, String productName, String aID, String buyPrice, String proImage, String buyCount, String uid, String supId, String shopID, String attrParam, String shopName, String createUser) {
        return util.putInShopCar(productID, productName, aID, buyPrice, proImage, buyCount, uid, supId, shopID, attrParam, shopName, createUser);
    }

//    @JavascriptInterface
//    public boolean toShopCar(String productID, String productName, String aID, String buyPrice,String marketPrice, String proImage, String buyCount, String uid, String supId, String shopID, String attrParam, String shopName, String createUser) {
//        return util.putInShopCar(productID, productName, aID, buyPrice, marketPrice,proImage, buyCount, uid, supId, shopID, attrParam, shopName, createUser);
//    }

    @JavascriptInterface
    public void jsCallSupplier(final String shopMobile) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (TextUtils.isEmpty(shopMobile)) {
                    return;
                }
                jsShowMsg(shopMobile);
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + shopMobile));
                startActivity(intent1);
            }
        });
    }

}