package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;

public class LoginActivity extends BaseWebActivity implements PlatformActionListener {

    private String account, passwd;
    private String score;
    public final static int LOGIN_SUCCESS = 11;
    public final static int LOGIN_FAILURE = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isLogined()) {
            jsShowMsg(getString(R.string.tips_already_logined));
            finish();
            return;
        }
        initTitle("登录");

        this.startWebView(Constants.appWebPageUrl + "login.html");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.e(TAG,"onNewIntent-" +getParent());
    }

    @Override
    protected void initWebSettings() {
        super.initWebSettings();
//		this.mJsCallback = new JsCallback();
        this.webView.addJavascriptInterface(this, "app");
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

        Button opera = (Button) findViewById(R.id.b_title_operation2);
        opera.setVisibility(View.GONE);
        opera.setText("微信授权登录");
        opera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                authorizeLogin(Wechat.NAME);
            }
        });
    }

    @JavascriptInterface
    public void authorizeLogin(final String platform) {
        runOnUiThread(new Runnable() {
            public void run() {
                Platform plat = ShareSDK.getPlatform(platform);
                authorize(plat);
            }
        });
    }

    private void authorize(Platform plat) {
        if (plat == null || isLoading()) {
            return;
        }
        showLoadDialog();
        if (plat.isAuthValid()) {
            plat.removeAccount(true);
        }
        plat.setPlatformActionListener(this);
        //关闭SSO授权
        plat.SSOSetting(true);
        plat.showUser(null);
    }

    @Override
    public void onCancel(Platform arg0, int arg1) {
        hideLoadDialog();
    }

    private String headimgurl;
    private String nickname;

    @Override
    public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
        Logger.e(TAG + "_onComplete", arg0.toString() + ", " + arg1 + ", " + arg2.toString());
        if (arg1 == Platform.ACTION_USER_INFOR && arg0 instanceof Wechat) {//获取微信平台账号信息
            String unionid = arg2.get("unionid").toString();
            headimgurl = arg2.get("headimgurl").toString();
            nickname = arg2.get("nickname").toString();
            login(unionid, "123456");
        }
    }

    @Override
    public void onError(Platform arg0, int arg1, Throwable arg2) {
        hideLoadDialog();
    }

    @JavascriptInterface
    public void login(final String account, final String passwd) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoginActivity.this.account = account;
                LoginActivity.this.passwd = passwd;
                new LoginAsync().execute(account, passwd);
            }
        });
    }

    class LoginAsync extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String uid = param[0];
            String pwd = param[1];
            return appManager.postLoginInfo("0009", uid, pwd);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject obj = jsonObject.getJSONArray("val").getJSONObject(0);
                JSONArray adr = jsonObject.getJSONArray("adr");
                score = obj.getString("Score");
                String email = obj.getString("Email");
                String token = obj.getString("Token");
                Constants.USER_TOKEN = token;
                setLoginInfo(account, passwd, email, nickname, headimgurl, token);
            } catch (JSONException e) {
                jsShowMsg("登录失败，请确认账号与密码的正确性");
                nickname = null;
                headimgurl = null;
            }
        }
    }

    @JavascriptInterface
    public void jsStartRegisterActivity() {
        Intent it = new Intent(getContext(), RegisterActivity.class);
        startActivity(it);
    }

    @JavascriptInterface
    public void jsStartFindPasswordActivity() {
        Intent it = new Intent(getContext(), FindPasswordActivity.class);
        startActivity(it);
    }

    @SuppressLint("SimpleDateFormat")
    @JavascriptInterface
    private void setLoginInfo(String uId, String pwd, String email, String nickName, String headimgurl, String token) {
        new Thread(isFirstSetupRunnable).start();
        DBHelper dbHelper3 = DBHelper.getInstance(getContext());
        // 数据库执行插入命令
        // db3.insert("user", null, values);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        values.put("uId", uId);
        values.put("pwd", pwd);
        values.put("email", email);
        values.put("score", score);
        values.put("token", token);
        values.put("loginTime", dateFormat.format(date));
        values.put("isLogin", "1");
        if (!TextUtils.isEmpty(nickName)) {
            values.put("nickname", nickName);
        }
        if (!TextUtils.isEmpty(headimgurl)) {
            values.put("headimgurl", headimgurl);
        }
        // 数据库执行插入命令
        try {
            if (db.IsUidExists(getContext(), uId))
                dbr.update("userLoginInfo", values, "uId = ?", new String[]{uId});
            else
                dbr.insert("userLoginInfo", null, values);
            db.setLoginUserIsDefault(getContext(), uId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetUserAddress().execute(uId);
    }

    class GetUserAddress extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String uid = param[0];
            return appManager.postGetUserAddress("0030", uid);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
            try {
                JSONObject jsonObject = new JSONObject(result).getJSONObject("adress");
                JSONArray jsonArray = jsonObject.getJSONArray("adresslist");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    // Adress adressEntity = new Adress();
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("adress_uid", account);
                    item.put("adress_name", oj.getString("name"));
                    item.put("adress_tel", oj.getString("tel"));
                    item.put("adress_mobile", oj.getString("mobile"));
                    item.put("adress_Adress", oj.getString("adress"));
                    item.put("adress_isdefault", oj.getString("ck"));
                    item.put("adress_time", oj.getString("time"));
                    item.put("adress_guid", oj.getString("guid"));
                    data.add(item);

                }
                db.TogetherPcUserAddress(getContext(), account, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    Runnable isFirstSetupRunnable = new Runnable() {
        @Override
        public void run() {
            String result = appManager.isFirstSetUp("0062", getIMEI());
            if (result.equals("false")) {
//                result = AppManager.getInstance().insertFirstSetUp("0063", getIMEI());
                appManager.updateUserScore("0061", account, "500");
            }
        }

    };

    public void finish() {
        SQLiteDatabase dbr = DBHelper.getInstance(getContext()).getReadableDatabase();
        Cursor cursor = dbr.query("userLoginInfo", new String[]{"uId", "pwd", "email", "score", "loginTime"}, " isLogin=1", null, null, null, null);
        if (cursor.moveToNext()) {
            jsShowMsg("登录成功");
            //发送登录成功广播，提示首页刷新
            sendLoginBroadcast();
            setResult(LOGIN_SUCCESS);
        } else {
            setResult(LOGIN_FAILURE);
        }
        super.finish();
    }

    private void sendLoginBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        Intent intent = new Intent(Constants.LOGIN_SUCCESS_BROADCAST);
        manager.sendBroadcast(intent);
    }
}