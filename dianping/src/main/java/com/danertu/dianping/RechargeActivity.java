package com.danertu.dianping;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.tools.AccountUtil;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.AppUtil;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Result;
import com.danertu.widget.CommonTools;

public class RechargeActivity extends BaseWebActivity {
    private String uid = null;
    /**
     * 0表示充值，1表示提现
     */
    private int inoutTag = 0;
    /**
     * 金额数最多保留两位小数
     */
    private double money;
    /**
     * 备注
     */
    private String remark = "单耳兔账户充值";
    /**
     * 充值渠道
     */
    private String rechargeWay = null;
    /**
     * 充值返回的银行，平台流水号
     */
    private String rechargeCode = "";

    private String subject = "单耳兔账户充值";
    private String body = "给账户充值后能更方便的。。。";

    AccountUtil accUtil = null;
    BaseActivity context = this;
    private AlipayUtil alipayUtil = null;
    private boolean isRecharged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("充值", null);
        if (!isLogined()) {
            CommonTools.showShortToast(getContext(), "请先登录！");
            return;
        }
        rechargeWay = "Android（" + getVersionCode() + "）支付宝支付";
        try {
            accUtil = new AccountUtil();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecharged = false;
        handler = new MyHandler(context);
        alipayUtil = new AlipayUtil(context);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(this, "app");
        startWebView(Constants.appWebPageUrl + "Android_wallet_recharge.html");


        //test-----------------------
//		uid = db.GetLoginUid(getContext());
//		String uids[] = {"15113347438","yutu0118","1222"};
//		for (String uid : uids) {
//			rechargeCode = AppUtil.getFormatTimeStamp();
//			String encryptInfo;
//			try {
//				money = 1000;
//				encryptInfo = accUtil.getPostInfo(uid, inoutTag, money, remark, rechargeWay, rechargeCode);
//				new PostMoneyInfo().execute(encryptInfo);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
        //test-----------------------
    }

    @JavascriptInterface
    public void jsToRecharge(final double tMoney) {
        if (isRecharged)
            return;
        runOnUiThread(new Runnable() {
            public void run() {
                if (tMoney <= 0) {
                    CommonTools.showShortToast(context, "充值金额不能小于等于0");
                } else {
                    money = tMoney;
                    String encryptInfo;
                    rechargeCode = AppUtil.getFormatTimeStamp();
                    try {
                        uid = getUid();
                        encryptInfo = accUtil.getPostInfo(uid, inoutTag, money, remark, rechargeWay, rechargeCode);
                        new PostMoneyInfo().execute(uid, encryptInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    isRecharged = true;
                }
            }
        });
    }

    public class MyHandler extends BaseHandler {

        public MyHandler(BaseActivity ui) {
            super(ui);
        }

        public void handleMessage(Message msg) {
            if (msg.what == PaymentCenterActivity.RQF_PAY) {
                String payInfo = msg.obj.toString();
                Result result = new Result(payInfo);
                String code = payInfo.substring(payInfo.indexOf("{") + 1,
                        payInfo.indexOf("}"));
                alipaySuccess(code, result);
            }
        }
    }

    public void alipaySuccess(String code, Result result) {
        if (code.equals("9000")) {
            jsShowMsg("充值成功");
            setResult(1);
            finish();
        } else {
            jsShowMsg("支付失败");
            finish();
        }
    }

    public class PostMoneyInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... arg0) {
            String uid = arg0[0];
            String param = arg0[1];
            try {
                return appManager.postMoneyInfo(param, uid);
            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
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
                    try {
                        JSONObject obj = new JSONObject(result);
                        String tag = obj.getString("result");
                        String info = obj.getString("info");
                        if (tag.equals("true")) {
                            tAliapy.start();
                        } else
                            CommonTools.showShortToast(context, info);
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

    public Thread tAliapy = new Thread() {
        public void run() {
            String tMoney = String.format("%.2f", money);
            String info = alipayUtil.getSignPayOrderInfo(rechargeCode, subject, body, tMoney);
            PayTask alipay = new PayTask(RechargeActivity.this);
            String payStr = alipay.pay(info);
            handler.sendMessage(getMessage(PaymentCenterActivity.RQF_PAY, payStr));
        }
    };

    public void finish() {
        if (this.webView.canGoBack()) {
            webView.goBack();
        } else
            super.finish();
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
