package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;

public class ScoreCenterActivity extends BaseWebActivity {
    private Intent mIntent = null;
    private ArrayList<HashMap<String, Object>> data1;
    private ArrayList<HashMap<String, Object>> productLists = new ArrayList<>();
    private int index, index2;
    private int TouchPost = 0;
    String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_score_center);
        initTitle("金萝卜");
        initData();

        this.startWebView(Constants.appWebPageUrl + "gold_radish1.html");
//		this.startWebView("file:///android_asset/gold_radish.html");
//		findViewById();
//		initView();
    }


    private void initTitle(String string) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        b_title.setText(string);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        findViewById(R.id.b_title_operation2).setVisibility(View.GONE);
    }

    private void initData() {
        uid = db.GetLoginUid(ScoreCenterActivity.this);

        getFirstCategory();
//		getDiscountProducts();
    }

    @Override
    protected void initWebSettings() {
        super.initWebSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(new JsCallback(), "app");
    }

    final class JsCallback {
        @JavascriptInterface
        public void jsStartIndexBannerToActivity(String toActivityName, String paraStr) {
            try {
                Intent intent = new Intent(ScoreCenterActivity.this, Class.forName("com.danertu.dianping." + toActivityName));
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

        @JavascriptInterface
        public void huDong() {
            String uid = db.GetLoginUid(getContext());
            Bundle bundle = new Bundle();
            Intent intent = new Intent();
            if (uid != null && uid.trim().length() > 0) {
                bundle.putString("foruid", uid);
                intent.putExtras(bundle);
                intent.setClassName(getApplicationContext(), "com.danertu.dianping.HuDongActivity");

            } else {
                CommonTools.showShortToast(getContext(), "请先登录！");
                intent.setClassName(getApplicationContext(), "com.danertu.dianping.LoginActivity");
            }
            startActivity(intent);
        }

        @JavascriptInterface
        public void updateUserScore() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = db.GetLoginInfo(getContext());
                    String uid = "";
                    String score = "";
                    if (cursor.moveToNext()) {
                        uid = cursor.getString(0);
                        score = cursor.getString(3);
                    }
                    webView.loadUrl("javascript:javaSetUserScore('" + uid + "', '" + score + "')");

                }
            });
        }

        @JavascriptInterface
        public void updateProductList() {
            getDiscountProducts();
        }

        @JavascriptInterface
        public void jsStartCalendarActivity() {
            mIntent = new Intent(ScoreCenterActivity.this, CalendarActivity.class);
            startActivity(mIntent);
        }

        @JavascriptInterface
        public void jsToProductList() {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ProductListActivity.class);
            startActivity(intent);
        }

        @JavascriptInterface
        public void jsToProductDetailsActivity2(int index) {
            index2 = index;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    HashMap<String, Object> item = productLists.get(index2);
                    TouchPost = index2;
                    String guid = String.valueOf(item.get("guid").toString());
                    String proName = String.valueOf(item.get("proName").toString());
                    String img = String.valueOf(item.get("img").toString());
                    String detail = String.valueOf(item.get("detail").toString());
                    String agentID = String.valueOf(item.get("agentID").toString());
                    String supplierID = String.valueOf(item.get("supplierID").toString());
                    String price = String.valueOf(item.get("price").toString());
                    String marketPrice = String.valueOf(item.get("marketPrice").toString());
                    String VirtualBuyCount = item.get("VirtualBuyCount").toString();

                    // 添加最近浏览记录
                    if (uid != null && uid.trim().length() > 0) {
                        Date date = new Date();
                        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String createTime = f.format(date);
                        db.InsertNearlyBroswer(ScoreCenterActivity.this, guid, proName, agentID, price, img, uid, supplierID, createTime, detail);
                    }

                    String shopid = "";

                    Intent intent = new Intent();
                    intent.setClassName(getApplicationContext(), "com.danertu.dianping.ProductDetailsActivity2");
                    // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                    Bundle b = new Bundle();
                    b.putString("guid", guid);
                    b.putString("proName", proName);
                    b.putString("img", img);
                    b.putString("detail", detail);
                    b.putString("agentID", agentID);
                    b.putString("supplierID", supplierID);
                    b.putString("price", price);
                    b.putString("marketprice", marketPrice);
                    b.putString("shopid", shopid);
                    b.putString("act", "ProductListActivity");
                    b.putInt("TouchPost", TouchPost);
                    b.putString("mobile", "400-995-2220");
                    b.putString("VirtualBuyCount", VirtualBuyCount);
                    intent.putExtra("arrayList", productLists);

                    // 此处使用putExtras，接受方就响应的使用getExtra
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });
        }

        @JavascriptInterface
        public void gotoSecondCategory(int pos) {
            index = pos;

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    HashMap<String, Object> item = data1.get(index);
                    String categoryID = (String) item.get("id");
                    String categoryName = (String) item.get("name");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", categoryID);
                    bundle.putString("name", categoryName);
                    bundle.putString("from", "1");
                    Intent intent = new Intent();
                    intent.putExtra("categoryData", data1);
                    intent.putExtras(bundle);
                    intent.setClass(ScoreCenterActivity.this, SecondCategoryActivity.class);
                    startActivity(intent);

                }
            });
        }

    }

    private void getFirstCategory() {
//        Hashtable<String, String> Params = new Hashtable<>();
//        Params.put("apiid", "0073");
//        doTaskAsync(Constants.api.GET_FIRST_CATEGORY, "", Params);
        new GetFirstCategory().execute();
    }

    class GetFirstCategory extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return appManager.getFirstCategory("0073");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            data1 = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result).getJSONObject("firstCategoryList");
                JSONArray jsonArray = jsonObject.getJSONArray("firstCategorybean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("id", oj.get("ID"));
                    item.put("name", oj.get("Name"));
                    item.put("key", oj.get("Keywords"));
                    item.put("family", oj.get("Family"));
                    item.put("img", oj.getString("PhoneImage"));
                    data1.add(item);
                }
            } catch (Exception e) {
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
                if (Constants.isDebug) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void getDiscountProducts() {
//        Hashtable<String, String> Params = new Hashtable<>();
//        Params.put("apiid", "0040");
//        Params.put("type", "1");
//        Params.put("kword", "");
//        doTaskAsync(Constants.api.POST_GET_PRODUCTLIST, "", Params);
        new GetDiscountProduct().execute();
    }

    class GetDiscountProduct extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... strings) {
            return appManager.postGetProductList("0040", "", "1", "");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            productLists = new ArrayList<>();
            JSONObject jsonObject;
            try {
                // Name, Guid, SmallImage, mobileProductDetail, AgentID,
                // SupplierLoginID, ShopPrice, VirtualBuyCount
//                    JSONObject json = new JSONObject();
                jsonObject = new JSONObject(result).getJSONObject("danProductlist");
                JSONArray jsonArray = jsonObject.getJSONArray("danProductbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    String guid = oj.getString("Guid");
                    if (guid.equals("edb05b72-10fd-4902-843d-ba11be607adb") || guid.equals("507817c9-5155-481a-9df4-7db4e8683937")) {
                        continue; // 手机端处理 不显示一元礼包
                    } else {
                        item.put("guid", guid);
                    }
                    String name = oj.getString("Name");
                    String proRealName = "";
                    String guige = "";
                    if (name.contains("|")) {
                        proRealName = name.substring(0, name.indexOf("|"));
                        guige = name.substring(name.indexOf("|") + 1);
                    } else {
                        proRealName = name;
                    }
                    item.put("proName", proRealName);
                    item.put("img", oj.getString("SmallImage"));
                    item.put("detail", guige);
                    item.put("agentID", oj.getString("AgentID"));
                    item.put("supplierID", oj.getString("SupplierLoginID"));
                    item.put("price", oj.getString("ShopPrice"));
                    item.put("marketPrice", oj.getString("MarketPrice"));
                    item.put("mobile", "400-995-2220");
                    item.put("VirtualBuyCount", oj.getString("VirtualBuyCount"));// 商品购买数量
                    productLists.add(item);
                }
                Gson gson = new Gson();
                webView.loadUrl("javascript:javaLoadProductList('" + gson.toJson(productLists) + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void findViewById() {

    }

    @Override
    protected void initView() {

    }


}
