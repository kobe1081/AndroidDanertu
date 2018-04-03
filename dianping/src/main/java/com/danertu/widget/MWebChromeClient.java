package com.danertu.widget;

import wl.codelibrary.widget.IOSDialog;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class MWebChromeClient extends WebChromeClient {
    private Context context;
    final String TAG = "MWebChromeClient";
    String titles[] = {"提示!", "对话框", "提示!"};

    public MWebChromeClient(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        alert(0, message, result);
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message,
                               JsResult result) {
        alert(1, message, result);
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message,
                              String defaultValue, JsPromptResult result) {
        alert(2, message, result);
        return true;
    }

    public void alert(int title_index, String content, final JsResult result) {
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if (act.isFinishing()) {
                result.cancel();
                return;
            }
        }
        final IOSDialog aDialog = new IOSDialog(context, result);
        if (title_index == 0) {//alert dialog
            aDialog.setSingleButton("确定", new View.OnClickListener() {
                public void onClick(View v) {
                    aDialog.setComfim(false);
                    aDialog.dismiss();
                }
            });
        } else {
            aDialog.setPositiveButton("是", new View.OnClickListener() {
                public void onClick(View v) {
                    aDialog.setComfim(true);
                    aDialog.dismiss();
                }
            });
            aDialog.setNegativeButton("否", new View.OnClickListener() {
                public void onClick(View v) {
                    aDialog.setComfim(false);
                    aDialog.dismiss();
                }
            });
        }
        aDialog.setTitle(titles[title_index]);
        aDialog.setMessage(content);
        aDialog.setCanceledOnTouchOutside(true);
        aDialog.setComfim(false);
        aDialog.show();
    }
}
