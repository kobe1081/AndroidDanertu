
package com.danertu.dianping;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.widget.CommonTools;

public class ChangePasswordActivity extends BaseActivity implements OnClickListener {

    private Button goBack = null;
    private EditText oldPassword, newPassword, determineNewPassword;
    private Button modifyBtn, cancelBtn;
    private TextView tv_uid;
    private String uid = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_change_password);
        } catch (Exception ex) {
            ex.printStackTrace();
            finish();
            return;
        }
        uid = getIntent().getStringExtra("uid");
        if (uid == null || TextUtils.isEmpty(uid))
            uid = getUid();
        if (uid == null) {
            jsShowMsg(getString(R.string.tips_toLogin));
            finish();
            return;
        }
        findViewById();
        initView();

    }

    @Override
    protected void findViewById() {
        tv_uid = (TextView) findViewById(R.id.tv_uid);
        goBack = (Button) findViewById(R.id.b_order_title_back);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);
        determineNewPassword = (EditText) findViewById(R.id.determine_new_password);
        modifyBtn = (Button) findViewById(R.id.modify_password_button);
        cancelBtn = (Button) findViewById(R.id.cancel_password_button);
    }

    @Override
    protected void initView() {
        goBack.setOnClickListener(this);
        modifyBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        goBack.setText("修改密码");
        tv_uid.setText(getResources().getString(R.string.loginacount) + uid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_order_title_back:
                finish();
                break;
            case R.id.modify_password_button:
                checkedData();
                break;
            case R.id.cancel_password_button:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 判断密码是否满足要求
     */
    public void checkedData() {
        String oldPsw = oldPassword.getText().toString();
        String newPsw = newPassword.getText().toString();
        String newPsw1 = determineNewPassword.getText().toString();
        if (oldPsw.equals("")) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "旧密码不能为空");
        } else if (newPsw.equals("")) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "新密码不能为空");
        } else if (newPsw1.equals("")) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "确认密码不能为空");
        } else if (!newPsw.equals(newPsw1)) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "两次密码输入不同");
        } else if (newPsw.length() < 6 || newPsw.length() > 30) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "新密码长度应6~30");
        } else if (oldPsw.equals(newPsw)) {
            CommonTools.showShortToast(ChangePasswordActivity.this, "新密码不能和原密码一样");
        } else {
            String param[] = {oldPsw, newPsw, newPsw1};
            new ChangePsw().execute(param);
        }
    }

    public class ChangePsw extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            try {
                String pwd = arg0[0];
                String newPwd = arg0[1];
                return AppManager.getInstance().postChangePassword("0048", uid, pwd, newPwd);
            } catch (Exception e) {
                System.out.print("错误：" + e.toString());
            }
            return "";
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
                    if (!result.equals("false")) {
                        CommonTools.showShortToast(getApplicationContext(), "修改成功，请重新登录");
//                DBManager dbr = new DBManager();
//                DBManager dbr = DBManager.getInstance();
//                String mid = dbr.GetLoginUid(ChangePasswordActivity.this);
                        String mid = db.GetLoginUid(ChangePasswordActivity.this);
                        db.DeleteLoginInfo(ChangePasswordActivity.this, mid);
                        application.backToActivity("IndexActivity");
                        jsStartActivity("PersonalActivity", "");
                    } else {
                        CommonTools.showShortToast(getApplicationContext(), "修改失败");
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
}
