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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.tools.AppManager;
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
    protected void initWebSettings() {
        super.initWebSettings();
//		this.mJsCallback = new JsCallback();
        this.webView.addJavascriptInterface(this, "app");
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
        Logger.e(TAG + "_onCancel", arg0.toString() + ", " + arg1);
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
        Logger.e(TAG + "_onError", arg0.toString() + ", " + arg1 + ", " + arg2.toString());
        hideLoadDialog();
    }

    @JavascriptInterface
    public void login(final String account, final String passwd) {
        Logger.e(TAG, account + "/" + passwd);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoginActivity.this.account = account;
                LoginActivity.this.passwd = passwd;
                HashMap<String, String> Params = new HashMap<>();
                Params.put("apiid", "0009");
                Params.put("uid", account);
                Params.put("pwd", passwd);
                doTaskAsync(Constants.api.POST_LOGIN_INFO, "", Params);
            }
        });
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
    private void setLoginInfo(String uId, String pwd, String email, String nickName, String headimgurl) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        getUserAddress(uId);
    }

    Runnable isFirstSetupRunnable = new Runnable() {
        @Override
        public void run() {
            String result = AppManager.getInstance().isFirstSetUp("0062", getIMEI());
            if (result.equals("false")) {
//                result = AppManager.getInstance().insertFirstSetUp("0063", getIMEI());
                AppManager.getInstance().updateUserScore("0061", account, "500");
            }
        }

    };

    private void getUserAddress(String uId) {
        HashMap<String, String> Params = new HashMap<>();
        Params.put("apiid", "0030");
        Params.put("uId", uId);
        doTaskAsync(Constants.api.GET_USER_ADRESS, "", Params);
    }

    @Override
    public void onTaskComplete(int taskId, final String result) {
        switch (taskId) {
            case Constants.api.POST_LOGIN_INFO:
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            JSONObject obj = new JSONObject(result).getJSONArray("val").getJSONObject(0);
                            score = obj.getString("Score");
                            String email = obj.getString("Email");
                            setLoginInfo(account, passwd, email, nickname, headimgurl);
                        } catch (JSONException e) {
                            jsShowMsg("登录失败，请确认账号与密码的正确性");
                            nickname = null;
                            headimgurl = null;
                        }
                    }
                });
                break;

            case Constants.api.GET_USER_ADRESS:
//			toast("result: " + result);
                try {
                    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(result).getJSONObject("adress");
                    JSONArray jsonArray = jsonObject.getJSONArray("adresslist");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject oj = jsonArray.getJSONObject(i);
                        // Address adressEntity = new Address();
                        HashMap<String, Object> item = new HashMap<>();
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
                break;
            default:
                break;
        }
    }

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