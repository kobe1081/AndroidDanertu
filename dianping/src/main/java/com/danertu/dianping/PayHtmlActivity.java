package com.danertu.dianping;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.tools.AccToPay;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.Result;
import com.danertu.tools.WXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;

public class PayHtmlActivity extends HtmlActivity {
    protected String outOrderNumber;
    protected String totalprice;
    private String subject;
    private String body;
    private String pricedata;

    /**
     * 设置支付数据
     *
     * @param outOrderNumber 订单号
     * @param totalprice     订单总价
     * @param subject        商城名
     * @param body           订单体信息，用于第三方支付显示订单信息
     * @param pricedata      订单商品信息，账号支付时使用，若不用则可以传空字符串
     */
    @JavascriptInterface
    public void setData(String outOrderNumber, String totalprice, String subject, String body, String pricedata) {
        this.outOrderNumber = outOrderNumber;
        this.totalprice = totalprice;
        this.subject = subject;
        this.body = body;
        this.pricedata = pricedata;
    }

    @JavascriptInterface
    public String getOutOrderNumber() {
        return outOrderNumber;
    }

    @JavascriptInterface
    public void setOutOrderNumber(String outOrderNumber) {
        this.outOrderNumber = outOrderNumber;
    }

    @JavascriptInterface
    public String getTotalprice() {
        return totalprice;
    }

    @JavascriptInterface
    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    @JavascriptInterface
    public String getSubject() {
        return subject;
    }

    @JavascriptInterface
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JavascriptInterface
    public String getBody() {
        return body;
    }

    @JavascriptInterface
    public void setBody(String body) {
        this.body = body;
    }

    @JavascriptInterface
    public String getPricedata() {
        return pricedata;
    }

    @JavascriptInterface
    public void setPricedata(String pricedata) {
        this.pricedata = pricedata;
    }

    private Context context;
    protected AlipayUtil alipayUtil;
    public static Handler handler;

    private PayPswDialog dialog_psw;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        PaymentCenterActivity.handler = null;
        handler = new Handler(hCallBack);
        alipayUtil = new AlipayUtil(context);
        initPayDialog();
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

    private void accPay() {
        String uid = db.GetLoginUid(getContext());
        String param[] = {uid, outOrderNumber, totalprice, pricedata};
        new AccToPay(this) {

            @Override
            public void paySuccess() {
                dialog_psw.dismiss();
                payResult(true, "单耳兔账户支付成功");
            }

            @Override
            public void payFail() {
                payResult(true, "单耳兔账户支付失败");
            }

        }.execute(param);
    }

    @JavascriptInterface
    public void accountToPay() {
        runOnUiThread(new Runnable() {
            public void run() {
                dialog_psw.show();
            }
        });
    }

    private WXPay wxPay;

    @JavascriptInterface
    public void wechatToPay() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (wxPay == null)
                    wxPay = new WXPay(context);
                wxPay.toPay(body, outOrderNumber + "_" + wxPay.genTimeStamp(), totalprice);
            }
        });
    }

    @JavascriptInterface
    public void alipayToPay() {
        runOnUiThread(new Runnable() {
            public void run() {
                new Thread(alipayToPay).start();
            }
        });
    }

    private Runnable alipayToPay = new Runnable() {
        boolean isRunning = false;

        public void run() {
            if (!isRunning) {
                isRunning = true;
                String info = alipayUtil.getSignPayOrderInfo(outOrderNumber, subject, body, totalprice);
                PayTask alipay = new PayTask(PayHtmlActivity.this);
                String payStr = alipay.pay(info);
                sendMessage(PaymentCenterActivity.RQF_PAY, payStr);
            }
            isRunning = false;
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
                    payResult(true, "微信支付成功");
                } else if (msg.arg1 == -2) {
                    payResult(false, "您取消支付了");
                } else {
                    payResult(false, "微信支付失败");
                }
            }
            return true;
        }
    };

    public void alipaySuccess(String code, Result result) {
        if (code.equals("9000")) {
            payResult(true, "支付宝支付成功");
        } else {
            payResult(false, "支付宝支付失败");
        }
    }

    public void payResult(boolean isSuccess, String info) {
        hideLoadDialog();
//		CommonTools.showShortToast(context, "支付成功");
//		finish();
        webView.loadUrl(Constants.IFACE + "payResult(" + isSuccess + ",'" + info + "')");
    }

    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
