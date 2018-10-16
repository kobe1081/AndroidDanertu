package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.config.Constants;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.tools.MD5Util;
import com.danertu.widget.CommonTools;

public class MyWalletFirstSet extends BaseWebActivity {
    private String pswMD5 = null;
    private String pNum = null;
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
            CommonTools.showShortToast(getContext(), "请先登录！");
            return;
        }
        uid = db.GetLoginUid(getContext());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(this, "app");
        startWebView(Constants.appWebPageUrl + pageName);
    }

    @JavascriptInterface
    public void jsInitPsw(String psw) {
        this.pswMD5 = MD5Util.MD5(psw);
    }

    @JavascriptInterface
    public void jsCheckPsw(final String psw) {
        runOnUiThread(new Runnable() {
            public void run() {
                String tPsw = psw.replaceAll(" ", "");
                if (tPsw.equals("")) {
                    CommonTools.showShortToast(getContext(), "输入密码不能为空！");
                    return;
                }
                showLoadDialog();
                pswMD5 = MD5Util.MD5(tPsw);
                new GetPayPswMD5().execute(uid);
            }
        });
    }

    public class GetPayPswMD5 extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                String payPswMD5 = appManager.getPayPswMD5(uid);
                judgeIsTokenException(payPswMD5, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, final String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                jsShowMsg(info);
//                                quitAccount();
//                                finish();
//                                jsStartActivity("LoginActivity", "");
//                            }
//                        });
                    }

                    @Override
                    public void ok() {

                    }
                });
                return pswMD5.equals(payPswMD5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                startWebView(Constants.appWebPageUrl + "Android_wallet_password1.html");
            } else {
                CommonTools.showShortToast(getContext(), "支付密码不正确！");
            }
            hideLoadDialog();
        }

    }

    @JavascriptInterface
    public void jsGetSMSCode(final String mobile) {
        runOnUiThread(new Runnable() {
            public void run() {
                pNum = mobile;
                new Thread() {
                    public void run() {
                        LinkedHashMap<String,String> param=new LinkedHashMap<>();
                        param.put("apiid", "0077");
                        param.put("mobile", pNum);
                        try {
                            appManager.doPost(param);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    //验证手机验证码
    @JavascriptInterface
    public void jsCheckSMSCode(final String vcode) {
        runOnUiThread(new Runnable() {
            public void run() {
                String isFirstSet = "true";
                String[] param = {pNum, vcode, isFirstSet};
                new CheckSMS().execute(param);
            }
        });
    }

    @JavascriptInterface
    public void jsChangePsw(final String psw, final String psw1) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (psw.equals(psw1)) {
                    pswMD5 = MD5Util.MD5(psw);
                    new SetPayPsw().execute(pswMD5);
                }
            }
        });
    }

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
    public void jsToFindPswNext(final String vcode) {
        runOnUiThread(new Runnable() {
            public void run() {
                String[] param = {pNum, vcode};
                new CheckSMS().execute(param);
            }
        });
    }

    public boolean isFirstSet = false;

    public class CheckSMS extends AsyncTask<String, Integer, Boolean> {
        String putPNum = "";
        String vcode = "";

        @Override
        protected Boolean doInBackground(String... arg0) {
            if (arg0.length > 2) {
                isFirstSet = arg0[2].equals("true");
            }
            this.putPNum = arg0[0];
            this.vcode = arg0[1];
            try {
                return appManager.checkSMSCode(putPNum, vcode);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;

//			return true;//测试----------------
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {//验证码正确
                if (isFirstSet) {
                    new BindPhoneNum().execute(pNum);
                } else
                    startWebView(Constants.appWebPageUrl + "Android_wallet_forgetpassword1.html");

            } else
                CommonTools.showShortToast(getContext(), "验证码错误！");
            hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }

    }

    private class BindPhoneNum extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String pNum = arg0[0];
            if (pNum == null) {
                return null;
            }
            try {
                return appManager.postBindPNumber(uid, pNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                CommonTools.showShortToast(getContext(), "请先获取验证码");
            } else if (isFirstSet) {
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
                        new SetPayPsw().execute(uid);
                    }
                });
            }
        }

    }

    public class GetBindPhoneNum extends AsyncTask<String, Integer, String> {
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
//                        jsShowMsg(info);
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

    public class SetPayPsw extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String result = null;
            try {
                result = appManager.postSetPayPsw(db.GetLoginUid(getContext()), pswMD5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (result == null) {
                CommonTools.showShortToast(getContext(), "支付密码修改失败，请重试");
                return;
            }
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
                    try {
                        JSONObject obj = new JSONObject(result);
                        String tag = obj.getString("result");
                        String info = obj.getString("info");
                        if (tag.equals("true")) {
                            isFirstSet = false;
                            setResult(1);//通知钱包首页刷新数据
                            finish();
                        }
                        CommonTools.showShortToast(getContext(), info);
                    } catch (JSONException e) {
                        e.printStackTrace();
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
