package com.danertu.dianping;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AsyncTask;

public class AccountRecord extends BaseWebActivity {
    private String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String pageName = i.getStringExtra("pageName");
        title = title == null ? "" : title;
        pageName = pageName == null ? "" : pageName;
        setTitle(title, null);
        if (!isLogined()) {
            return;
        }
        uid = db.GetLoginUid(getContext());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "app");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        startWebView(Constants.appWebPageUrl + pageName);
        showLoadDialog();
    }

    private boolean isLoading = false;

    @JavascriptInterface
    public void jsGetData() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!isLoading)
                    new GetAccountRecord().execute(uid);
            }
        });
    }

    private boolean isLoading1 = false;

    @JavascriptInterface
    public void jsGetData1() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!isLoading1)
                    new GetTakeMoneyInfo().execute(uid);
            }
        });
    }

    private JSONArray recharges = null;

    private class GetAccountRecord extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            isLoading = true;
            String result = null;
            try {
                result = appManager.getMoneyRec(uid);
                recharges = new JSONObject(result).getJSONObject("rechargeList").getJSONArray("rechargebean");
            } catch (Exception e) {
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            judgeIsTokenException(result, new TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, String info) {
//                    jsShowMsg(info);
//                    quitAccount();
//                    jsStartActivity("LoginActivity", "");
//                    jsFinish();
                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
                }

                @Override
                public void ok() {
                    webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "')");
                    isLoading = false;
                }
            });
        }

    }

    JSONArray withDraws = null;

    private class GetTakeMoneyInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            isLoading1 = true;
            String result = null;
            try {
                result = appManager.getTakeMoneyInfo(uid);
                withDraws = new JSONObject(result).getJSONObject("cashList").getJSONArray("cashbean");
            } catch (Exception e) {
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            judgeIsTokenException(result, new TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, String info) {
                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
                }

                @Override
                public void ok() {
                    webView.loadUrl(Constants.IFACE + "javaLoadData1('" + result + "')");
                    isLoading1 = false;
                }
            });
        }

    }

    private String recordDetail = null;
    private boolean isRecordDetail = false;
    private ArrayList<Object> item = new ArrayList<>();

    /**
     * @param tag   record , withdraw
     * @param index index
     */
    @JavascriptInterface
    public void WalletRecordDetail(final String tag, final int index, final String pageName) {
        if (isRecordDetail)
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                item.clear();
                try {
                    if (tag.equals("withdraw") && withDraws != null) {
                        item.add(withDraws.get(index));
                    } else if (tag.equals("record") && recharges != null) {
                        item.add(recharges.get(index));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                recordDetail = item.toString();
                isRecordDetail = true;
                // showLoadDialog();
                startWebView(Constants.appWebPageUrl + pageName);
            }
        });
    }

    @JavascriptInterface
    public void jsGetRecordStr() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (isRecordDetail) {
                    isRecordDetail = false;
                    webView.loadUrl(Constants.IFACE + "javaLoadData('" + recordDetail + "')");
                }
            }
        });
    }

    public void finish() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.finish();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.b_title_back2:
                finish();
                break;
            default:
                break;
        }
    }

}
