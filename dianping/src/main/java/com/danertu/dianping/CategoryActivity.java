package com.danertu.dianping;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.danertu.widget.MWebChromeClient;
import com.danertu.widget.MWebViewClient;

public class CategoryActivity extends BaseActivity {

    private ArrayList<HashMap<String, Object>> data1;
    List<String> categoryData;
    String[] data2;

    EditText category_search_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        findViewById();
        initWebContent();
    }

    public void clickToIndex(View v) {
        finish();
    }

    protected void findViewById() {
        category_search_edit = (EditText) findViewById(R.id.category_search_edit);
        webView = (WebView) findViewById(R.id.wv_category_content);
        category_search_edit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    openActivity(SearchActivityV2.class);
                    return true;
                }
                return false;
            }
        });
    }

    public static final String INTERFACE = "iface_category";

    protected void initView() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebContent() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(Constants.appWebPageUrl + "Android/Androidclassify.html");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//		webView.addJavascriptInterface(this, INTERFACE);
        webView.addJavascriptInterface(this, "app");
        webView.setWebViewClient(new MWebViewClient(this, INTERFACE) {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//				if(data1 == null){
//					Thread thread = new Thread(firstCategory);
//					thread.start();
//				}
            }
        });
        webView.setWebChromeClient(new MWebChromeClient(this));
    }

    Runnable firstCategory = new Runnable() {
        public void run() {
            try {
                String result = AppManager.getInstance().getFirstCategory("0073");
                data1 = new ArrayList<>();
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
                handler.sendEmptyMessage(KEY_GETDATA_SUCCESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private MyHandler handler = new MyHandler(this);
    final int KEY_GETDATA_SUCCESS = 1;

    public static class MyHandler extends Handler {
        WeakReference<CategoryActivity> cAct;

        public MyHandler(CategoryActivity act) {
            this.cAct = new WeakReference<>(act);
        }

        public void handleMessage(Message msg) {
            CategoryActivity ca = cAct.get();
            if (msg.what == ca.KEY_GETDATA_SUCCESS) {
                ca.hideLoadDialog();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
