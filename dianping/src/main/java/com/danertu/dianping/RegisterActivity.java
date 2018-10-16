package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Button;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.tools.AsyncTask;
import com.danertu.widget.CommonTools;

public class RegisterActivity extends BaseWebActivity {

    private String account, passwd, passwd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle("注册");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        this.startWebView(Constants.appWebPageUrl + "register.html");
    }

    @Override
    protected void initWebSettings() {
        super.initWebSettings();
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
        findViewById(R.id.b_title_operation2).setVisibility(View.GONE);
    }

    @JavascriptInterface
    public void register(final String account, final String passwd, final String passwd2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RegisterActivity.this.account = account;
                RegisterActivity.this.passwd = passwd;
                RegisterActivity.this.passwd2 = passwd2;
                String msg = checkData();
                if (!msg.equals("")) {
                    CommonTools.showShortToast(getContext(), msg);
                } else {
                    new Register().execute();
                }
            }
        });
    }

    private String checkData() {
        String Msg = "";
        String acc_regEx = "[0-9a-zA-Z]{2,18}";
        String psw_regEx = "[0-9a-zA-Z]{6,20}";
        if (!match(acc_regEx, account)) {
            Msg = "请输入2--18位数字、字母或字母数字混合的用户名";
        } else if (!match(psw_regEx, passwd)) {
            Msg = "请输入6--15位字母、数字或字母数字混合的密码";
        } else if (!passwd.equals(passwd2)) {
            Msg = "两次密码输入不一致";
        }
        return Msg;
    }

    public static boolean match(String regEx, String match) {
        Pattern p = Pattern.compile(regEx);
        return p.matcher(match).matches();
    }

    private class Register extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            String result = "";
            try {
//				判断用户是否已经被使用
                result = appManager.postUserIsExist("0029", account);
                if (result.equals("True")) {
                    CommonTools.showShortToast(getContext(), "该用户名已被使用");
                    return true;// 注册失败
                } else {
//                    HashMap<String, String> Params = new HashMap<>();
//                    // Post方式提交注册信息
//                    Params.put("apiid", "0011");
//                    Params.put("uid", account);
//                    Params.put("pwd", passwd);
//                    Params.put("email", "");
                    result = appManager.postRegister(account, passwd, "").replaceAll("\n|\r| ", "").toLowerCase();
                    return Boolean.parseBoolean(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return true;// 注册失败
            }
        }

        @Override
        protected void onPostExecute(Boolean isFail) {
            super.onPostExecute(isFail);
            if (isFail) {
                CommonTools.showShortToast(getContext(), "注册失败");
            } else {
                setLoginInfo(account, passwd, "");
            }
            isFail = !isFail;
            webView.loadUrl(Constants.IFACE + "registerCallback('" + isFail.toString() + "')");
//			hideLoadDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }

    }

    private void setLoginInfo(String uId, String pwd, String email) {
        // ContentValues values = new ContentValues();
        // //像ContentValues中存放数据
        // values.put("id", 1);
        // values.put("name","zhangsan");
        DBHelper dbHelper3 = DBHelper.getInstance(getContext());

        // 数据库执行插入命令
        // db3.insert("user", null, values);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        Cursor cursor = dbr.query("userLoginInfo", new String[]{"uId", "pwd", "email", "loginTime"}, "uId=?", new String[]{uId}, null, null, null);
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            values.put("uId", uId);
            values.put("pwd", pwd);
            values.put("email", email);
            values.put("loginTime", dateFormat.format(date));
            // 数据库执行插入命令
            try {
                dbr.insert("userLoginInfo", null, values);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        cursor = dbr.query("userLoginInfo", new String[]{"uId", "pwd", "email", "loginTime"}, "uId=?", new String[]{uId}, null, null, null);
        if (cursor.getCount() > 0) {
            CommonTools.showShortToast(getContext(), "注册成功");
//			finish();
        }
    }

}