package com.danertu.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.danertu.db.DBManager;
import com.danertu.dianping.R;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.LoadingDialog;
import com.danertu.tools.MD5Util;

public abstract class PayPswDialog extends Dialog {

    @SuppressWarnings("unused")
    private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "请不要动这些代码，用于";
    public static String translateKeyword = "%E7%94%B5%E8%AF%9D";

    private boolean isCanBack = false;
    static Activity act = null;
    private String uid = "";

    public PayPswDialog(Context context) {
        this(context, R.style.Dialog);
//		super(context);
//		init(context);
    }

    public PayPswDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context) {
        act = (Activity) context;
        db = DBManager.getInstance();
        uid = db.GetLoginUid(act);
        loadDialog = new LoadingDialog(act);
        this.setContentView(R.layout.pay_psw_dialog);
        initPswDialog();
    }

    private EditText et_psw;

    private void initPswDialog() {
        this.setCanceledOnTouchOutside(false);
        et_psw = (EditText) findViewById(R.id.et_payPsw_content);
        Button b_cancel = (Button) findViewById(R.id.b_payPsw_cancel);
        Button b_ok = (Button) findViewById(R.id.b_payPsw_ok);
        b_ok.setOnClickListener(ocl);
        b_cancel.setOnClickListener(ocl);
    }

    public void dismiss() {
        super.dismiss();
        et_psw.setText("");
    }

    private View.OnClickListener ocl = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_payPsw_ok:
                    String psw = et_psw.getText().toString();
                    if (!TextUtils.isEmpty(psw))
                        new CheckPayPswMD5().execute(psw);
                    break;
                case R.id.b_payPsw_cancel:
                    cancelDialog();
                    break;
                default:
                    break;
            }
        }
    };

    public abstract void passwordRight();

    public abstract void passwordWrong();

    public abstract void cancelDialog();

    private DBManager db = null;
    private LoadingDialog loadDialog = null;

    public class CheckPayPswMD5 extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                String pswMD5 = MD5Util.MD5(arg0[0]);
                String payPswMD5 = AppManager.getInstance().getPayPswMD5(uid);
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
                passwordRight();
            } else {
                passwordWrong();
            }
            loadDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadDialog.show();
        }

    }

    public void onBackPressed() {
        if (!isCanBack())
            return;
        super.onBackPressed();
    }

    public boolean isCanBack() {
        return isCanBack;
    }

    public void setCanBack(boolean isCanBack) {
        this.isCanBack = isCanBack;
    }

}
