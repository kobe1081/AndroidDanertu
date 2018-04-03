package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import com.config.Constants;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.MD5Util;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.google.gson.Gson;

public class MyWalletActivity extends BaseWebActivity {
    private String uid = null;
    Button[] titles;
    private Gson gson = null;
    /**
     * 1表示首次使用钱包，0表示已使用
     */
    private int isFirst = 1;
    private boolean isCanTakeMoney = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = setTitle("我的钱包", "更多");
        if (!isLogined()) {
            CommonTools.showShortToast(this, "请先登录！");
            openActivity(LoginActivity.class);
            finish();
            return;
        }
        gson = new Gson();
        uid = db.GetLoginUid(this);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        setAutoHideDialog(false);
        webView.addJavascriptInterface(this, "app");
    }

    private PayPswDialog payPswDialog = null;

    @JavascriptInterface
    public void showPswDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (payPswDialog == null) {
                    payPswDialog = new PayPswDialog(getContext(), R.style.Dialog) {

                        @Override
                        public void passwordWrong() {
                            webView.loadUrl(Constants.IFACE + "passwordWrong()");
                        }

                        @Override
                        public void passwordRight() {
                            webView.loadUrl(Constants.IFACE + "passwordRight()");
                        }

                        @Override
                        public void cancelDialog() {
                            dismiss();
                            CommonTools.showShortToast(getContext(), "您取消了付款!");
                        }
                    };
                }
                payPswDialog.show();
            }
        });
    }

    @JavascriptInterface
    public void canclePswDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (payPswDialog != null)
                    payPswDialog.dismiss();
            }
        });
    }

    public void onResume() {
        super.onResume();
        if (!isLogined()) {
            CommonTools.showShortToast(this, "请先登录！");
            return;
        }
        isFirst = 1;
        isCanTakeMoney = false;
        showLoadDialog();
        startWebView(Constants.appWebPageUrl + "Android_wallet_index1.html");
    }

    @JavascriptInterface
    public void jsGetWalletInfo() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!isLoading)
                    new GetWalletInfo().execute(uid);
            }
        });
    }

    public static final String KEY_BINDCARD_INFO = "bindCardInfo";
    public static final String KEY_ACCOUNT_MONEY = "accountMoney";
    public static final String KEY_ACCOUNT_ISFIRST = "accountIsFirst";
    private boolean isLoading = false;

    public class GetWalletInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            isLoading = true;
            HashMap<String, Object> result = new HashMap<>();
            String money = "0.00";
            String bindCard = "";
            try {
                money = appManager.getWalletMoney("0108", uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                bindCard = appManager.getBindBankCardInfo(uid);
                String payPswMD5 = appManager.getPayPswMD5(uid);
                String phone = appManager.getBindPNum(uid);
                String specialNum = "123456";//支付密码不能为123456
                specialNum = MD5Util.MD5(specialNum);
                if (payPswMD5.equals("") || phone.equals("") || payPswMD5.equals(specialNum))
                    isFirst = 1;//表示首次使用钱包
                else
                    isFirst = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
            isCanTakeMoney = !bindCard.equals("");
            result.put(KEY_BINDCARD_INFO, bindCard);
            result.put(KEY_ACCOUNT_MONEY, money);
            result.put(KEY_ACCOUNT_ISFIRST, isFirst);
            ArrayList<HashMap<String, Object>> param = new ArrayList<>();
            param.add(result);
            return gson.toJson(param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            isLoading = false;
            javaLoadWalletInfo(result);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {//表示充值或提现成功
            showLoadDialog();
            jsGetWalletInfo();
        }
    }

    public void javaLoadWalletInfo(String json) {
        webView.loadUrl(Constants.IFACE + "javaLoadWalletInfo('" + json + "')");
    }

    public void finish() {
        if (this.webView.canGoBack()) {
            webView.goBack();
        } else
            super.finish();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.b_title_back2:
                finish();
                break;
            case R.id.b_title_operation2:
                if (isFirst != 1)
                    openActivity(MyWalletMoreActivity.class);
                break;
            default:
                break;
        }
    }

}
