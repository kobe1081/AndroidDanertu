package com.danertu.dianping;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;

/**
 * 能送餐或订餐的店铺列表
 *
 * @author dengweilin
 * @see intent type: 0表示能送餐，1表示能订餐
 */
public class FoodShopActivity extends BaseWebActivity implements OnClickListener {
    private int pageindex = 0;
    /**
     * intent参数
     */
    private int type;
    public static final String KEY_INTENT = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        jsRefreshWebContent(type);
        handler = new MyHandler(this);
        findViewById();
        initTitle(type);
        showLoadDialog();
//		String url = "file:///android_asset/Android_food_Shop.html";

        String url = Constants.appWebPageUrl + "Android_food_Shop.html";
        this.webView.addJavascriptInterface(this, "app");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        startWebView(url);
    }

    public void initTitle(int type) {
        if (type == 1) {
            b_title.setText("订餐");
        } else {
            b_title.setText("送餐");
        }
        b_title.setOnClickListener(this);
    }

    private void initData() {
        pageindex = 0;
        try {
            String tType = getIntent().getStringExtra(KEY_INTENT);
            type = Integer.parseInt(tType);
        } catch (NumberFormatException e) {
            type = 0;
            Logger.e("err_line34", e.toString());
        }
    }

    private boolean isLoading = false;

    @JavascriptInterface
    public void jsGetData() {
        if (isLoading)
            return;
        isLoading = true;
        runOnUiThread(new Runnable() {
            public void run() {
                pageindex++;
                new Thread(rGetShopList).start();
            }
        });
    }

    public void javaLoadData(String shopListJson) {
        hideLoadDialog();
        this.webView.loadUrl(Constants.IFACE + "javaLoadData(" + type + ",'" + shopListJson + "')");
        isLoading = false;
    }

    private final int WHAT_GETSHOPLIST_SUCCESS = 1;
    public Runnable rGetShopList = new Runnable() {
        public void run() {
            try {
                String result = getShopList("1", isCanOrder, isCanSell);
                result = result.replaceAll("\n|\r", "");
                handler.sendMessage(getMessage(WHAT_GETSHOPLIST_SUCCESS, result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public class MyHandler extends BaseHandler {
        public MyHandler(BaseActivity ui) {
            super(ui);
        }

        public void handleMessage(Message msg) {
            if (msg.what == WHAT_GETSHOPLIST_SUCCESS) {
                javaLoadData(msg.obj.toString());
            }
        }
    }

    Button b_title = null;

    @Override
    protected void findViewById() {
        super.findViewById();
        b_title = (Button) findViewById(R.id.b_title_back2);
    }

    /**
     * 不为空字符则表示能订餐的店铺
     */
    String isCanOrder = "";
    /**
     * 不为空字符则表示能送餐的店铺
     */
    String isCanSell = "";

    /**
     * 刷新店铺列表
     *
     * @param tag 1表示能订餐的店铺，否则能送餐的店铺
     */
    @JavascriptInterface
    public void jsRefreshWebContent(int tag) {
        if (tag == 1) {
            isCanSell = "";
            isCanOrder = "1";
        } else {
            isCanSell = "1";
            isCanOrder = "";
        }
    }

    /**
     * 订餐和送餐不能
     *
     * @param type       1表示美食
     * @param isCanorder 能订餐
     * @param isCanSell  能送餐
     * @return
     * @throws Exception
     */
    @JavascriptInterface
    public String getShopList(String type, String isCanorder, String isCanSell) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0037");
        param.put("pagesize", String.valueOf(Constants.pagesize));
        param.put("pageIndex", String.valueOf(pageindex));
        param.put("kword", "");
        param.put("type", type);
        param.put("isCanOrder", isCanOrder);
        param.put("isCanSell", isCanSell);
        String cityName = Constants.getCityName();
        String dCityName = Constants.getDcityName();
        if (cityName != null && dCityName != null) {
            if (cityName.equals(dCityName)) {
                param.put("gps", Constants.getLa() + "," + Constants.getLt());
                param.put("less", "80000");
                param.put("areaCode", "");
            } else {
                param.put("areaCode", cityName);
            }
        } else {
            param.put("areaCode", "0000");
        }
        String result = AppManager.getInstance().doPost(param);
        result = result == null ? "" : result.replaceAll("\n|\r", "");
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == b_title) {
            finish();
        }
    }

}
