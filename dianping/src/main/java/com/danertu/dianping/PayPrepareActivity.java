package com.danertu.dianping;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.AccToPay;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.LoadingDialog;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.Result;
import com.danertu.tools.WXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;

public class PayPrepareActivity extends BaseActivity implements OnClickListener {
    private TextView tv_money, tv_orderNum, tv_payWayTag;
    private Button b_pay;

    private PayPswDialog dialog_psw;
    protected String outOrderNumber;
    protected String totalprice;
    private String subject;
    private String body;
    private String pricedata;
    private boolean isCanUserOrderPayWay = true;
    private String payWayName;
    private boolean isCanArrivePay = false;

    public static final String KEY_MONEY = "money";
    public static final String KEY_ORDER_NUMBER = "orderNumber";
    public static final String KEY_CREATE_TIME = "createTime";
    public static final String KEY_ALIPAY_SUBJECT = "subject";
    public static final String KEY_ALIPAY_BODY = "body";
    public static final String KEY_PRICE_DATA = "pricedata";
    public static final String KEY_CANUSE_ORDER_PAYWAY = "isCanUserOrderPayWay";
    public static final String KEY_PAYWAY_NAME = "payWayName";
    public static final String KEY_CAN_ARRIVEPAY = "isCanArrivePay";

    private Context context;
    protected AlipayUtil alipayUtil;
    public static Handler handler;
    LoadingDialog lDialog = null;
    private boolean isPayClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.pay_prepare);
        setSystemBarWhite();
        PaymentCenterActivity.handler = null;
        handler = new Handler(hCallBack);
        lDialog = new LoadingDialog(this);
        findViewById();
        initData();
        initView();
        initPayDialog();
    }

    private Dialog payWayDialog = null;

    protected void selectPayWay() {
        if (payWayDialog == null) {
            payWayDialog = MyDialog.getDefineDialog(context, R.layout.dialog_payway1);
            ViewGroup rg = (ViewGroup) payWayDialog.findViewById(R.id.rg_payWay_group);
            int len = rg.getChildCount();
            if (isCanArrivePay) {
                payWayDialog.findViewById(R.id.rb_arrivePay).setVisibility(View.VISIBLE);
                payWayDialog.findViewById(R.id.rb_arrivePay_line).setVisibility(View.VISIBLE);
            }
            if (!isCanUserOrderPayWay) {//美食店铺自营产品
                jsShowMsg(getString(R.string.payWay_selectTag));
            }
            for (int i = 0; i < len; i++) {
                View v = rg.getChildAt(i);
                if (!isCanUserOrderPayWay) {
                    if (v.getId() == R.id.rb_accountPay) {
                        v.setVisibility(View.VISIBLE);
                    } else {
                        v.setVisibility(View.GONE);
                    }
                }
                if (v instanceof RadioButton && v.getVisibility() == View.VISIBLE) {
                    final RadioButton btn = (RadioButton) v;
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            payWay = v.getTag().toString();
                            tv_payWayTag.setText(btn.getText().toString());
                            payWayDialog.dismiss();
                        }
                    });
                }
            }
        }
        payWayDialog.show();
    }

    private void initPayDialog() {
        dialog_psw = new PayPswDialog(this, R.style.Dialog) {
            @Override
            public void cancelDialog() {
                dialog_psw.dismiss();
            }

            @Override
            public void passwordRight() {
                accPay();
            }

            @Override
            public void passwordWrong() {
                CommonTools.showShortToast(getContext(), "支付密码不正确！");
            }
        };
    }

    public void accPay() {
        String uid = db.GetLoginUid(getContext());
        String param[] = {uid, outOrderNumber, totalprice, pricedata};
        new AccToPay(getContext()) {

            @Override
            public void paySuccess() {
                dialog_psw.dismiss();
                successFinish();
            }

            @Override
            public void payFail() {
            }

        }.execute(param);
    }

    private void initData() {
        Intent i = getIntent();
        payWay = getString(R.string.payWay_account_key);
        totalprice = i.getStringExtra(KEY_MONEY);
        outOrderNumber = i.getStringExtra(KEY_ORDER_NUMBER);
        subject = i.getStringExtra(KEY_ALIPAY_SUBJECT);
        body = i.getStringExtra(KEY_ALIPAY_BODY);
        pricedata = i.getStringExtra(KEY_PRICE_DATA);
        isCanUserOrderPayWay = i.getBooleanExtra(KEY_CANUSE_ORDER_PAYWAY, true);
        payWayName = i.getStringExtra(KEY_PAYWAY_NAME);
        isCanArrivePay = i.getBooleanExtra(KEY_CAN_ARRIVEPAY, false);
        tv_money.setText("￥" + totalprice);
        tv_orderNum.setText(outOrderNumber);

        Logger.e("test", "PayPrepareActivity initData " + toString());
    }

    @Override
    public String toString() {
        return "PayPrepareActivity{" +
                "outOrderNumber='" + outOrderNumber + '\'' +
                ", totalprice='" + totalprice + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", pricedata='" + pricedata + '\'' +
                ", payWayName='" + payWayName + '\'' +
                ", payWay='" + payWay + '\'' +
                '}';
    }

    @Override
    protected void findViewById() {
        tv_money = (TextView) findViewById(R.id.order_money);
        tv_orderNum = (TextView) findViewById(R.id.order_number);
        tv_payWayTag = (TextView) findViewById(R.id.order_payWayTag);
        b_pay = (Button) findViewById(R.id.b_toPay);
    }

    @Override
    protected void initView() {
        alipayUtil = new AlipayUtil(context);
        initTitle();

        judgePayWay(payWayName);
        b_pay.setOnClickListener(this);
        tv_payWayTag.setOnClickListener(this);
    }

    private void judgePayWay(String payWayName) {
        if (TextUtils.isEmpty(payWayName)) return;
        String payWayTag = getString(R.string.payWay_account_tips);
        if (!isCanUserOrderPayWay) {//美食店铺自营产品
            payWay = getString(R.string.payWay_account_key);
            payWayTag = getString(R.string.payWay_account_tips);
        } else {//普通商品
            if (payWayName.contains("到付")) {
                payWay = getString(R.string.payWay_arrivedPay_key);
                payWayTag = getString(R.string.payWay_arrivedPay_tips);

            } else if (payWayName.contains("账号支付")) {// 默认账号支付
                payWay = getString(R.string.payWay_account_key);
                payWayTag = getString(R.string.payWay_account_tips);

            } else if (payWayName.contains("微信")) {
                payWay = getString(R.string.payWay_wechatPay_key);
                payWayTag = getString(R.string.payWay_wechatPay_tips);

            } else {
                payWay = getString(R.string.payWay_alipay_key);
                payWayTag = getString(R.string.payWay_alipay_tips);
            }
        }
        tv_payWayTag.setText(payWayTag);
    }

    private void initTitle() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("结算");
        findViewById(R.id.b_title_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    private String payWay;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_toPay:
                if (isPayClick) {
                    isPayClick = false;
                    if (payWay.equals(getString(R.string.payWay_account_key))) {
                        dialog_psw.show();
                    } else if (payWay.equals(getString(R.string.payWay_alipay_key))) {
                        new Thread(alipayToPay).start();
                    } else if (payWay.equals(getString(R.string.payWay_arrivedPay_key))) {
                        jsShowMsg("到付-待完善");
                    } else if (payWay.equals(getString(R.string.payWay_wechatPay_key))) {
                        wechatToPay(outOrderNumber);
                    }
                } else {
                    CommonTools.showShortToast(context, "正在支付，请稍后");
                }
                break;
            case R.id.order_payWayTag:
                selectPayWay();
                break;
        }
    }

    private WXPay wxPay;

    public void wechatToPay(String outOrderNumber) {
        if (wxPay == null)
            wxPay = new WXPay(context);
        wxPay.toPay(body.substring(0, body.length() - 1), outOrderNumber + "_" + wxPay.genTimeStamp(), totalprice);
    }

    private Runnable alipayToPay = new Runnable() {
        public void run() {
            String info = alipayUtil.getSignPayOrderInfo(outOrderNumber, subject, body, totalprice);
            PayTask alipay = new PayTask(PayPrepareActivity.this);
            String payStr = alipay.pay(info);
            sendMessage(PaymentCenterActivity.RQF_PAY, payStr);
        }
    };

    public void sendMessage(int what, String data) {
        if (handler != null) {
            handler.sendMessage(getMessage(what, data));
        }
    }

    public Handler.Callback hCallBack = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            if (msg.what == PaymentCenterActivity.RQF_PAY) {
                String payInfo = msg.obj.toString();
                Result result = new Result(payInfo);
                String code = payInfo.substring(payInfo.indexOf("{") + 1, payInfo.indexOf("}"));
                alipaySuccess(code, result);
            } else if (msg.what == PaymentCenterHandler.WHAT_WECHAT_PAY) {

                if (msg.arg1 == 0) {
                    successFinish();
                } else if (msg.arg1 == -2) {
                    CommonTools.showShortToast(context, "您取消了支付");
                } else {
                    CommonTools.showShortToast(context, "支付失败");
                }
            }
            //为了防止针对同一订单多次支付，也可以在此检查一次订单的支付状态
            isPayClick = true;
            return true;
        }
    };

    public void alipaySuccess(String code, Result result) {
        if (code.equals("9000")) {
            successFinish();
        }
    }

    public void successFinish() {
        hideLoadDialog();
        CommonTools.showShortToast(context, "支付成功");
        MyOrderData.payForOrder(outOrderNumber);
        lDialog.show();
        new MyOrderData(this) {
            public void getDataSuccess() {
                lDialog.dismiss();
                setResult(MyOrderParent.REQ_PAY);
                finish();
            }
        };
    }

    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
