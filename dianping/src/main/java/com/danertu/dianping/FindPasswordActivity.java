package com.danertu.dianping;

import java.util.HashMap;
import java.util.Hashtable;

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
        public void jsSendCode(final String moblie) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Hashtable<String, String> Params = new Hashtable<>();
                    Params.put("apiid", "0077");
                    Params.put("mobile", moblie);
                    doTaskAsync(Constants.api.POST_SEND_VERITY_CODE, "", Params);
                }
            });
        }

        @JavascriptInterface
        public void jsCheckCode(final String mobile, final String vCode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Hashtable<String, String> Params = new Hashtable<>();
                    Params.put("apiid", "0078");
                    Params.put("mobile", mobile);
                    Params.put("vcode", vCode);
                    doTaskAsync(Constants.api.POST_CHECK_VERITY_CODE, "", Params);
                }
            });
        }

        @JavascriptInterface
        public void jsResetPasswd(final String passwd) {
            runOnUiThread(new Runnable() {
                @SuppressWarnings("unchecked")
                @Override
                public void run() {
                    HashMap<String, String> Params = new HashMap<>();
                    Params.put("apiid", "0048");
                    Params.put("mid", uId);
                    Params.put("pwd", passwd);
//					doTaskAsync(Constants.api.POST_RESET_PASSWORD, "", Params);
                    new ChangePsw().execute(Params);
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

    public class ChangePsw extends AsyncTask<HashMap<String, String>, Integer, Boolean> {

        protected Boolean doInBackground(HashMap<String, String>... arg0) {
            try {
                String result = appManager.doPost(arg0[0]);
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

    @Override
    public void onTaskComplete(int taskId, String result) {
        switch (taskId) {
            case Constants.api.POST_SEND_VERITY_CODE:
                webView.loadUrl("javascript:javaSendVerifyCode(" + result + ")");
                break;
            case Constants.api.POST_CHECK_VERITY_CODE:
                boolean status = result.equals("true");
                webView.loadUrl("javascript:javaToResetPasswd(" + status + ")");
                break;
            case Constants.api.POST_RESET_PASSWORD:
                hideLoadDialog();
                if (result.equals("true")) {
                    CommonTools.showShortToast(getContext(), "修改成功");
                    finish();
                } else {
                    CommonTools.showShortToast(getContext(), "修改失败");
                }
                break;

            default:
                break;
        }
    }


}