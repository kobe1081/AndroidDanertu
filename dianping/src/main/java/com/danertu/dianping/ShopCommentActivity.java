package com.danertu.dianping;

import java.util.HashMap;

import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.widget.Button;

import com.config.Constants;
import com.danertu.tools.AppManager;

public class ShopCommentActivity extends BaseWebActivity implements OnClickListener {
    //<---数据--->
    private String shopid = null;
    private String shopJson = null;
    private String shopCommentJson = null;
    private HashMap<String, String> shopData = null;

    final String IFACE_NAME = "app";

    public static final String KEY_SHOP_JSON = "shopJson";
    public static final String KEY_SHOP_COMMENT_JSON = "shopCommentJson";

    public static final int WHAT_GET_COMMENT_SUCCESS = 1;
    public static final String WEB_PAGE_NAME = "Android_overall_evaluation.html";
//	/**测试html页面地址*/
//	public final String TEST_URL = "file:///android_asset/"+WEB_PAGE_NAME;
//	public final String TEST_URL1 = "http://192.168.1.129:778/"+WEB_PAGE_NAME;
    /**
     * 服务器html页面地址
     */
    public final String SERVER_URL = Constants.appWebPageUrl + WEB_PAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        handler = new MyHandler(this);
        initUI();
    }

    private void initData() {
        Bundle b = getIntent().getExtras();
        shopid = b.getString(DetailActivity.KEY_SHOP_ID);
        shopJson = b.getString(KEY_SHOP_JSON);
        shopCommentJson = b.getString(KEY_SHOP_COMMENT_JSON);
    }

    /**
     * 获取店铺详细信息,评论
     */
    private Runnable tGetShopDetails = new Runnable() {
        @Override
        public void run() {
            // 耗时操作
            Message msg = null;
            try {
                AppManager am = AppManager.getInstance();
                if (shopJson == null || shopJson.equals(""))
                    shopJson = am.postGetShopDetails("0041", shopid);
                // Log.e("店铺详细", shopJson);
                try {
                    shopData = ActivityUtils.getInstance().analyzeShopJson(shopJson);
                } catch (JSONException e) {
                    shopData = new HashMap<>();
                    e.printStackTrace();
                }

                if (shopCommentJson == null || shopCommentJson.equals(""))
                    shopCommentJson = AppManager.getInstance().getCommentList(shopid);
                msg = getMessage(WHAT_GET_COMMENT_SUCCESS, shopCommentJson);
                handler.sendMessage(msg);
            } catch (Exception e) {
                shopJson = "";
                shopCommentJson = "";
                e.printStackTrace();
            }
        }

    };

    public void initUI() {
        startWebView(SERVER_URL);
        webView.addJavascriptInterface(this, IFACE_NAME);
        initTitle("店铺评论", "去评论");
    }

    boolean isFirst = true;

    @JavascriptInterface
    public void jsInitShopComment(int pageSize) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isFirst) {
                    showLoadDialog();
                    isFirst = false;
                    new Thread(tGetShopDetails).start();
                } else {
                    webView.loadUrl(Constants.IFACE + "javaLoadShopComment('','','')");
                }
            }
        });
    }

    public class MyHandler extends BaseHandler {

        public MyHandler(BaseActivity ui) {
            super(ui);
        }

        public void handleMessage(Message msg) {
            hideLoadDialog();
            if (msg.what == WHAT_GET_COMMENT_SUCCESS) {
                String shopName = shopData.get(ActivityUtils.SHOP_NAME);
                String score = shopData.get(ActivityUtils.SHOP_AVGSCORE);
                String json = msg.obj.toString();
                Log.e("传上页面店铺数据", shopName + " , " + score + " , " + json);
                webView.loadUrl(Constants.IFACE + "javaLoadShopComment('" + shopName + "','" + score + "','" + json + "')");

            }
        }
    }

    private void initTitle(String title, String opera) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        Button b_opera = (Button) findViewById(R.id.b_title_operation2);
        b_title.setText(title);
        b_opera.setText(opera);
        b_title.setOnClickListener(this);
        b_opera.setOnClickListener(this);
    }

    int count = 1;
    public static final int REQ_COMMENT = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back2:
                finish();
                break;

            case R.id.b_title_operation2:
                if (isLogined()) {
                    Intent intent = new Intent(getContext(), ShopToComment.class);
                    intent.putExtra(DetailActivity.KEY_SHOP_ID, shopid);
                    intent.putExtra(KEY_SHOP_JSON, shopJson);
                    startActivityForResult(intent, REQ_COMMENT);
                } else {
                    openActivity(LoginActivity.class);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQ_COMMENT) {//评论成功
            isFirst = true;
            shopJson = null;
            shopCommentJson = null;
            webView.removeAllViews();
            startWebView(SERVER_URL);
        }
    }

}
