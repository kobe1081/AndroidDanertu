package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;


public class FindPasswordActivity extends BaseWebActivity {

    private String uId;
    private Boolean userIsExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle("找回密码");
        this.startWebView(Constants.appWebPageUrl + "forgot_password.html");
    }

    @Override
    protected void initWebSettings() {
        super.initWebSettings();
        webView.addJavascriptInterface(new JsCallback(), "app");
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    private void initTitle(String string) {
        Button b_title = (Button) findViewById(R.id.b_title_back2);
        b_title.setText(string);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
        findViewById(R.id.b_title_operation2).setVisibility(View.GONE);
    }

    final class JsCallback {
        @JavascriptInterface
        public boolean userIsExist(String uId) {
            FindPasswordActivity.this.uId = uId;
            Thread exists = new Thread(userExistsRunnable);
            exists.start();
            try {
                exists.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return userIsExist;
        }

        @JavascriptInterface
        public void jsSendCode(final String mobile) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Hashtable<String, String> Params = new Hashtable<>();
//                    Params.put("apiid", "0077");
//                    Params.put("mobile", mobile);
//                    doTaskAsync(Constants.api.POST_SEND_VERITY_CODE, "", Params);
                    new GetVerityCode().execute(mobile);
                }
            });
        }

        class GetVerityCode extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... param) {
                return appManager.postGetVerityCode(param[0]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                webView.loadUrl("javascript:javaSendVerifyCode(" + result + ")");
            }
        }

        @JavascriptInterface
        public void jsCheckCode(final String mobile, final String vCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Hashtable<String, String> Params = new Hashtable<>();
//                    Params.put("apiid", "0078");
//                    Params.put("mobile", mobile);
//                    Params.put("vcode", vCode);
//                    doTaskAsync(Constants.api.POST_CHECK_VERITY_CODE, "", Params);
                    new CheckVerityCode().execute(mobile, vCode);
                }
            });
        }

        class CheckVerityCode extends AsyncTask<String, Integer, String> {
            @Override
            protected String doInBackground(String... param) {
                return appManager.postCheckCode(param[0], param[1]);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                boolean status = result.equals("true");
                webView.loadUrl("javascript:javaToResetPasswd(" + status + ")");
            }
        }


        @JavascriptInterface
        public void jsResetPasswd(final String passwd) {
            runOnUiThread(new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    LinkedHashMap<String,String> params=new LinkedHashMap<>();
                    params.put("apiid", "0048");
                    params.put("dateline", String.valueOf(System.currentTimeMillis()));
                    params.put("mid", uId);
                    params.put("pwd", passwd);
                    params.put("tid", uId);
//					doTaskAsync(Constants.api.POST_RESET_PASSWORD, "", Params);
                    new ChangePsw().execute(params);
                }
            });
        }

        @JavascriptInterface
        public void goBack() {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }

    }

    public class ChangePsw extends AsyncTask<LinkedHashMap<String,String>, Integer, Boolean> {

        protected Boolean doInBackground(LinkedHashMap<String,String>... arg0) {
            try {
                LinkedHashMap<String,String> params = arg0[0];
                String result = appManager.doPost(params);
                result += "";
                if (!result.equals("false")) {
                    return true;
                }
            } catch (Exception e) {
                System.out.print("错误：" + e.toString());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                CommonTools.showShortToast(getApplicationContext(), "修改成功，请重新登录");
                String mid = db.GetLoginUid(getContext());
                db.DeleteLoginInfo(getContext(), mid);
//                DBManager dbr = new DBManager();
//                String mid = dbr.GetLoginUid(getContext());
//                dbr.DeleteLoginInfo(getContext(), mid);
                application.backToActivity("IndexActivity");
                jsStartActivity("PersonalActivity", "");
            } else {
                CommonTools.showShortToast(getApplicationContext(), "修改失败");
            }
            hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }
    }

    protected class DemoJs {
        public void testCallBack(String testParam) {
            Logger.w("DemoJs", testParam);
        }
    }

    Runnable userExistsRunnable = new Runnable() {

        @Override
        public void run() {
            String s = AppManager.getInstance().postUserIsExist("0029", uId);
            userIsExist = s.equalsIgnoreCase("true");
        }
    };

}