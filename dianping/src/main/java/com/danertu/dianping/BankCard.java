package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

public class BankCard extends BaseWebActivity {
    String uid = null;

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
            CommonTools.showShortToast(getContext(), "请先登录！");
            return;
        }
        uid = db.GetLoginUid(getContext());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "app");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        startWebView(Constants.appWebPageUrl + pageName);
    }

    @JavascriptInterface
    public void jsBindBankCard(final String cardNum) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (cardNum.length() < 16) {
                    CommonTools.showShortToast(getContext(), "银行卡号错误");
                } else {
                    String[] params = {db.GetLoginUid(getContext()), cardNum};
                    new BindBankCard().execute(params);
                }
            }
        });
    }

    /**
     * 绑定银行卡
     */
    private class BindBankCard extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String uid = arg0[0];
            String cardNum = arg0[1];
            String result = null;
            try {
                result = appManager.bindBankCard(uid, cardNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (result != null) {
                judgeIsTokenException(result, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        jsShowMsg(info);
//                        quitAccount();
//                        finish();
//                        jsStartActivity("LoginActivity", "");
                    }

                    @Override
                    public void ok() {
                        try {
                            JSONObject obj = new JSONObject(result);
                            if (Boolean.parseBoolean(obj.getString("result"))) {
                                setResult(1);
                                finish();
                            }
                            CommonTools.showShortToast(getContext(), obj.getString("info"));
                        } catch (Exception e) {
                            e.printStackTrace();
                            CommonTools.showShortToast(getContext(), e.toString());
                        }
                    }
                });

            }
            hideLoadDialog();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }

    }

    @JavascriptInterface
    public void jsGetBankCard() {
        runOnUiThread(new Runnable() {
            public void run() {
                new GetBankCardInfo().execute(uid);
            }
        });
    }

    private class GetBankCardInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            try {
                return appManager.getBindBankCardInfo(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            judgeIsTokenException(result, new TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, String info) {
                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                    jsShowMsg(info);
//                    quitAccount();
//                    finish();
//                    jsStartActivity("LoginActivity", "");
                }

                @Override
                public void ok() {
                    if (result != null && !result.equals(""))
                        webView.loadUrl(Constants.IFACE + "javaLoadData('" + result + "')");
                    else {
                        CommonTools.showShortToast(getContext(), "请先绑定银行卡");
                        finish();
                    }
                }
            });
            hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }

    }

    @JavascriptInterface
    public void jsGetSMSCode(final String mobile) {
        new Thread() {
            public void run() {
                LinkedHashMap<String, String> param = new LinkedHashMap<>();
                param.put("apiid", "0077");
                param.put("mobile", mobile);
                param.put("tid", uid);
                try {
                    appManager.doPost(param);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String pNum = null;

    @JavascriptInterface
    public void jsFindPswGetSMSCode(final String num) {
        runOnUiThread(new Runnable() {
            public void run() {
                pNum = num;
                new GetBindPhoneNum().execute(pNum);
            }
        });
    }

    @JavascriptInterface
    public void jsToFindPswNext(final String vCode) {
        runOnUiThread(new Runnable() {
            public void run() {
                String[] param = {pNum, vCode};
                new CheckSMS().execute(param);
            }
        });
    }

    private class CheckSMS extends AsyncTask<String, Integer, Boolean> {
        String putPNum = "";
        String vcode = "";

        @Override
        protected Boolean doInBackground(String... arg0) {
            this.putPNum = arg0[0];
            this.vcode = arg0[1];
            try {
                return appManager.checkSMSCode(putPNum, vcode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

//			return true;//测试---------
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result)
                startWebView(Constants.appWebPageUrl + "Android_wallet_changebindcard1.html");
            else
                CommonTools.showShortToast(getContext(), "验证码错误！");
            hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }

    }

    private class GetBindPhoneNum extends AsyncTask<String, Integer, String> {
        //用户输入的手机号
        String putPNum = "";

        @Override
        protected String doInBackground(String... arg0) {
            putPNum = arg0[0];
            try {
                return appManager.getBindPNum(uid);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("获取手机号失败", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String pNum) {
            super.onPostExecute(pNum);
            if (pNum != null) {
                judgeIsTokenException(pNum, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        jsShowMsg(pNum);
//                        quitAccount();
//                        finish();
//                        jsStartActivity("LoginActivity", "");
                    }

                    @Override
                    public void ok() {
                        if (putPNum.equals(pNum)) {
                            jsGetSMSCode(pNum);
                        } else {
                            CommonTools.showShortToast(getContext(), "输入手机号不正确！");
                        }
                    }
                });

            }
            hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
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
