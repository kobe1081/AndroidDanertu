package com.danertu.dianping;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.config.Constants;
import com.danertu.tools.AsyncTask;
import com.danertu.widget.CommonTools;

/**
 * 2017年11月28日
 * 修改加载地址
 */
public class QRCodeActivity extends HomeActivity {

    public static final String KEY_PRO_NAME = "proName";
    public static final String KEY_PRO_IMG_PATH = "proImgPath";
    public static final String KEY_TARGET_PATH = "targetPath";
    private String shopid;
    final int WHAT = 0x7f060123;
    final int WHAT_SHARE_COUNT = 0x7f060456;
    final int WHAT_SHOPNUM = 0x7f060457;
    final int WHAT_SHOPNAME = 0x7f060458;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        // YtTemplate.init(this);
        Intent i = getIntent();
        shopid = i.getStringExtra("shopid");
        if (TextUtils.isEmpty(getUid())) {
            jsStartActivityForResult("LoginActivity", "", 1);
        } else {
            initView();
        }
    }

    @JavascriptInterface
    public String getUid() {
        if (TextUtils.isEmpty(shopid)) {
            shopid = super.getUid();
        }
        return shopid;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginActivity.LOGIN_SUCCESS) {
            shopid = getUid();
            initView();
        } else if (resultCode == LoginActivity.LOGIN_FAILURE) {
            finish();
        }
    }

    @Override
    protected void findViewById() {
        webView = (WebView) findViewById(R.id.wv_qr);
    }

    @Override
    protected void initView() {
        findViewById();

//        webView.loadUrl(Constants.appWebPageUrl + "android/new_share.html");
        /**
         * 2017年11月28日
         * @since  82版本
         * 修改链接地址
         */
        webView.loadUrl(Constants.NEW_SHARE_HALL_ADDRESS);
    }

    private boolean isLoading = false;

    @JavascriptInterface
    public void jsGetData(final String paramStr, final String tag) {
        if (isLoading)
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                new GetData(tag).execute(paramStr);
            }
        });
    }

    private class GetData extends AsyncTask<String, Integer, String> {

        private String tag = null;

        public GetData(String tag) {
            this.tag = tag;
        }

        @Override
        protected String doInBackground(String... arg0) {
            isLoading = true;
            String paramStr = arg0[0];
            try {
                if (paramStr.equals("")) {
                    CommonTools.showShortToast(getContext(), "参数不能为空");
                    return null;
                }
                HashMap<String, String> param = new HashMap<>();
                String[] strList = paramStr.split(",;");
                for (String aStrList : strList) {
                    param.put(aStrList.substring(0, aStrList.indexOf("|")), aStrList.substring(aStrList.indexOf("|") + 1));
                }
                String result = appManager.doPost(param);
                return result.replaceAll("\n|\r", "");
            } catch (Exception e) {
                CommonTools.showShortToast(getApplicationContext(), "出错了：" + e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isLoading = false;
            if (tag != null) {
                webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "','" + tag + "')");
            } else {
                webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "')");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }
}
