package com.danertu.dianping;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AccountUtil;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.tools.MD5Util;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;

public class TakeMoneyActivity extends BaseActivity implements OnClickListener {
    private String uid = null;
    public static final String INTENT_MONEY = "keyMoney";
    public static final String INTENT_CARD_NUM = "keyCardNum";

    private double takeMoney = 0;
    /**
     * 0为提现到银行卡，1为提现到支付宝
     */
    private int type = 0;
    private AccountUtil accUtil = null;
    /**
     * 备注
     */
    private String remark = null;
    private String payPsw = null;
    private String cardNum = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_take_money);
        setTitle("提现");
        if (!isLogined()) {
            CommonTools.showShortToast(getContext(), "请先登录！");
            return;
        }
        remark = "Android（" + getVersionCode() + "）账户提现";
        findViewById();
        try {
            accUtil = new AccountUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
        uid = getUid();
        Intent i = getIntent();
        String money = i.getStringExtra(INTENT_MONEY);
        setBalance(money);
        showLoadDialog();
        new initData().execute();
    }

    private void setTitle(String title) {
        if (title == null) {
            return;
        }
        findViewById(R.id.b_title_back4).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) findViewById(R.id.tv_title4)).setText(title);
    }

    private class initData extends AsyncTask<String, Integer, HashMap<String, String>> {
        final String BALANCE = "balance";
        final String ALIPAY = "alipayNum";
        final String BANDCARD = "bandCardNum";

        @Override
        protected HashMap<String, String> doInBackground(String... param) {
            HashMap<String, String> result = new HashMap<>();
            String postJson = "";
            try {
                String json = appManager.getPayAccountNum(uid);
                JSONObject obj = new JSONObject(json);
                result.put(ALIPAY, obj.getString("CK_ZhiFuBaoNumber").replaceAll(" |\n|\r", ""));
                result.put(BANDCARD, obj.getString("WW").replaceAll(" |\n|\r", ""));
                postJson = appManager.getWalletMoney("0224", uid);
                result.put(BALANCE, String.format("%.2f", Double.parseDouble(postJson)));
            } catch (Exception e) {
                judgeIsTokenException(postJson, "您的登录信息已过期，请重新登录", -1);

//                if (judgeIsTokenException(postJson)) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            jsShowMsg("您的登录信息已过期，请重新登录");
//                            quitAccount();
//                            finish();
//                            jsStartActivity("LoginActivity", "");
//                        }
//                    });
//
//                }
                if (Constants.isDebug)
                    e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            hideLoadDialog();
            if (result.isEmpty()) {
                jsShowMsg("扑通~~~服务器摔跤了");
                return;
            }
            setAlipayNum(result.get(ALIPAY));
            setCardNum(result.get(BANDCARD));
            setBalance(result.get(BALANCE));
            if (rb_alipayNum.getVisibility() == View.GONE && rb_bandcardNum.getVisibility() == View.GONE) {
                jsShowMsg("请先绑定存入账户");
                return;
            }
            initView();
        }
    }

    private RadioButton rb_bandcardNum;
    private RadioButton rb_alipayNum;
    private Button b_toTake;
    private EditText et_takeMoney;
    private TextView tv_balance;

    @Override
    protected void findViewById() {
        tv_balance = (TextView) findViewById(R.id.tv_can_take);
        rb_bandcardNum = (RadioButton) findViewById(R.id.rb_bandcardNum);
        rb_alipayNum = (RadioButton) findViewById(R.id.rb_alipayNum);
        b_toTake = (Button) findViewById(R.id.b_toTake);
        et_takeMoney = (EditText) findViewById(R.id.et_takeMoney);
        CommonTools.addMoneyLimit(et_takeMoney);
    }

    public void setCardNum(String num) {
        if (rb_bandcardNum != null) {
            if (TextUtils.isEmpty(num)) {
                rb_alipayNum.setChecked(true);
                rb_bandcardNum.setVisibility(View.GONE);
                return;
            }
            int len = num.length();
            if (len > 8) {
                rb_bandcardNum.setText(getTextAttr("银行卡\n" + num.substring(0, 4) + " **** **** **** " + num.substring(len - 3, len)));
            }
        }
    }

    public void setAlipayNum(String num) {
        if (rb_alipayNum != null) {
            if (TextUtils.isEmpty(num)) {
                rb_bandcardNum.setChecked(true);
                rb_alipayNum.setVisibility(View.GONE);
                return;
            }
            int len = num.length();
            if (len > 10 && num.contains("@")) {
                rb_alipayNum.setText(getTextAttr("支付宝\n" + num.substring(0, 3) + "******" + num.substring(num.indexOf("@") - 2, len)));
            } else if (len > 6) {
                rb_alipayNum.setText(getTextAttr("支付宝\n" + num.substring(0, 3) + "******" + num.substring(len - 3, len)));
            }
        }
    }

    private SpannableStringBuilder getTextAttr(String text) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.gray));
        ssb.setSpan(fcs, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ssb.setSpan(getASS(18), 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        fcs = new ForegroundColorSpan(Color.parseColor("#ababab"));
        ssb.setSpan(fcs, 4, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        ssb.setSpan(getASS(15), 4, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return ssb;
    }

    private AbsoluteSizeSpan getASS(int sp) {
        return new AbsoluteSizeSpan(CommonTools.sp2px(this, sp));
    }

    @Override
    protected void initView() {
        b_toTake.setOnClickListener(this);
    }

    public void setBalance(String balance) {
        if (!TextUtils.isEmpty(balance)) {
            tv_balance.setText("可提现金额：" + balance);
        }
    }

    private class GetPayPsw extends AsyncTask<String, Integer, Boolean> {

        protected Boolean doInBackground(String... arg0) {
            String psw = arg0[0];
            String pswMD5 = MD5Util.MD5(psw);
            try {
                payPsw = appManager.getPayPswMD5(uid);
                boolean equals = payPsw.equals(pswMD5);
                if (equals) {
                    return true;
                } else {
                    if (judgeIsTokenException(payPsw)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(payPsw, TokenExceptionBean.class);
                                jsShowMsg(tokenExceptionBean.getInfo());
                                quitAccount();
                                finish();
                                jsStartActivity("LoginActivity", "");
                            }
                        });

                    } else {
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                new GetWalletMoney().execute(uid);
            } else {
                CommonTools.showShortToast(getContext(), "支付密码不正确！");
            }
        }
    }

    private class GetWalletMoney extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String result = "0.00";
            String json = "";
            try {
                json = appManager.getWalletMoney("0224", uid);
                result = String.format("%.2f", Double.parseDouble(json));
                Logger.i(TAG, "账户余额为：" + result);
            } catch (Exception e) {
                judgeIsTokenException(json, "您的登录信息已过期，请重新登录", -1);
                if (Constants.isDebug)
                    e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tv_balance.setText("余额：￥" + result);
            if (takeMoney > 0) {
                new PostTakeMoneyInfo().execute(result);
            } else {
                hideLoadDialog();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadDialog();
        }
    }

    public void jsGetBankCardInfo() {
        new GetBankCardInfo().execute(uid);
    }

    private class GetBankCardInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                cardNum = appManager.getBindBankCardInfo(uid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return cardNum;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (judgeIsTokenException(result)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(result, TokenExceptionBean.class);
                        jsShowMsg(tokenExceptionBean.getInfo());
                        quitAccount();
                        finish();
                        jsStartActivity("LoginActivity", "");
                    }
                });

            } else {
                if (result == null || result.equals("")) {
                    CommonTools.showShortToast(getContext(), "请先绑定银行卡！");
                    finish();
                    return;
                }
            }
            cardNum = result;
        }

    }

    final String OK = "完成";
    public final String TAKE_MONEY_SUCCESS = "true";

    private class PostTakeMoneyInfo extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... arg0) {
            String result = null;
            double money = Double.parseDouble(arg0[0]);
            try {
                if (takeMoney <= money) {
                    result = appManager.postTakeMoneyInfo(accUtil.getPostTakeMoneyInfo(uid, takeMoney, remark), type, uid);
                    JSONObject obj = new JSONObject(result);
                    String tag = obj.getString("result");
                    String info = obj.getString("info");
                    if (tag.equals(TAKE_MONEY_SUCCESS)) {
                        result = TAKE_MONEY_SUCCESS;
                    } else {
                        result = info;
                    }
                } else {
                    result = "账户余额不足";
                }
            } catch (Exception e) {
                result = "网络异常，请重新获取！";
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (result.equals(TAKE_MONEY_SUCCESS)) {
                finish();
                jsStartActivity("WapActivity", "webTitle|" + OK + ",;webUrl|" + Constants.appWebPageUrl + "Android_wallet_withdraw1.html");
            } else {
                Logger.e("test", "TakeMoneyActivity onPostExecute  result=" + result);
                if (judgeIsTokenException(result)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(result, TokenExceptionBean.class);
                            jsShowMsg(tokenExceptionBean.getInfo());
                            quitAccount();
                            jsStartActivity("LoginActivity", "");
                            finish();
                        }
                    });

                } else {
                    CommonTools.showShortToast(getContext(), result);
                }
            }
            hideLoadDialog();
        }
    }

    public void jsToNext(double money, final String psw, int type) {
        this.takeMoney = money;
        this.type = type;
        runOnUiThread(new Runnable() {
            public void run() {
                if (takeMoney <= 0) {
                    CommonTools.showShortToast(getContext(), "提取金额不能小于等于0");
                } else {
                    new GetPayPsw().execute(psw);
                }
            }
        });
    }

    public void jsToNext(double money, final String psw) {
        jsToNext(money, psw, 0);
    }

    private PayPswDialog ppd = null;

    public void toTake(double money, int type) {
        this.type = type;
        this.takeMoney = money;
        if (takeMoney <= 0) {
            jsShowMsg("提取金额不能小于等于0");
            return;
        }
        if (ppd != null) {
            if (!ppd.isShowing()) {
                ppd.show();
            }
            return;
        }
        ppd = new PayPswDialog(this) {

            public void passwordWrong() {
                jsShowMsg("支付密码不正确！");
            }

            public void passwordRight() {
                dismiss();
                new GetWalletMoney().execute(uid);
            }

            public void cancelDialog() {
                dismiss();
            }
        };
        ppd.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_toTake:
                //0为提现到银行卡，1为提现到支付宝
                int type = rb_alipayNum.isChecked() ? 1 : 0;
                String sMoney = et_takeMoney.getText().toString();
                sMoney = TextUtils.isEmpty(sMoney) ? "0" : sMoney;
                double money = Double.parseDouble(sMoney);
                toTake(money, type);
                break;
            default:
                break;
        }
    }
}
