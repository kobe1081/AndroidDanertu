package com.danertu.dianping;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Button;

import com.danertu.widget.MWebViewClient;

public class MyOrderShipmentActivity extends BaseActivity {
    /**
     * 快递公司名
     */
    public static final String KEY_SHIPMENT_NAME = "shipmentName";
    /**
     * 快递公司编码
     */
    public static final String KEY_SHIPMENT_CODE = "shipmentCode";
    /**
     * 快递单号
     */
    public static final String KEY_SHIPMENT_NUMBER = "shipmentNumber";

    Intent intent = null;
    Context context;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_my_order_shipment);
        intent = getIntent();
        initTitle();
        webView = (WebView) findViewById(R.id.wv_order_shipmentContent);
        webView.setWebViewClient(new MWebViewClient(this, "app") {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideLoadDialog();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadDialog();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        String shipCode = intent.getStringExtra(KEY_SHIPMENT_CODE);
        String shipNumber = intent.getStringExtra(KEY_SHIPMENT_NUMBER);
        String shipName = intent.getStringExtra(KEY_SHIPMENT_NAME);
        String type = TextUtils.isEmpty(shipCode) ? shipName : shipCode;
        //webView.loadUrl("http://wap.kuaidi100.com/wap_result.jsp?rand=20120517&id=quanfengkuaidi&fromWeb=null&&postid=123456");
//		webView.loadUrl("http://wap.kuaidi100.com/wap_result.jsp?rand=20120517&id="+shipCode+"&fromWeb=null&&postid="+shipNumber);

        webView.loadUrl("http://m.kuaidi100.com/index_all.html?type=" + type + "&postid=" + shipNumber);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initTitle() {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText("物流情况");
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
    }

    @Override
    protected void initView() {
    }
}
