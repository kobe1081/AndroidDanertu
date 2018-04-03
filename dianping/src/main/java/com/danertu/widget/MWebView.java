package com.danertu.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MWebView extends WebView {
    Context context;
    String interfaceName = "app";

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public MWebView(Context context) {
        super(context);
        this.context = context;
        setAttr();
    }

    public MWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttr();
    }

    public MWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setAttr();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setAttr() {
        this.addJavascriptInterface(context, interfaceName);
        WebSettings settings = this.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.setWebViewClient(new MWebViewClient(context, interfaceName));
        this.setWebChromeClient(new MWebChromeClient(context));
    }

    private OnScrollChangeListener onScrollChangeListener;

    public interface OnScrollChangeListener {
        void onScrollChanged(int top);
    }

    public OnScrollChangeListener getOnScrollChangeListener() {
        return onScrollChangeListener;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null)
            onScrollChangeListener.onScrollChanged(t);
    }

}
