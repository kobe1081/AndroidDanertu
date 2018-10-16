package com.danertu.dianping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;

import com.config.Constants;

public class HtmlActivity extends BaseWebActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.title2).setVisibility(View.GONE);

        webView.addJavascriptInterface(this, "app");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        Intent i = getIntent();
        String pageName = i.getStringExtra("pageName");
        boolean isAutoShowDialog = Boolean.parseBoolean(i.getStringExtra("showDialog"));

        setAutoHideDialog(!isAutoShowDialog);
        if (pageName.contains("/")) {
            startWebView(Constants.appWebPageUrl + pageName);
        } else {
            startWebView(Constants.appWebPageUrl + "Android/" + pageName);
        }
    }
}
