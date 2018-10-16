package com.danertu.dianping;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

import com.danertu.tools.Logger;
import com.danertu.tools.Result;
import com.danertu.widget.CommonTools;

public class PaymentCenterHandler extends Handler {
    public static final int WHAT_ORDER_ALREADY_SUBMIT = 13;
    public static final int WHAT_GETYUNFEI_SUCCESS = 12;
    public static final int WHAT_GETYUNFEI_FAIL = -12;
    public static final int WHAT_GETJLB_FAIL = -11;
    public static final int WHAT_ORDER_SUCCESS = 10;
    public static final int WHAT_ORDER_FAIL = -10;
    public static final int WHAT_WECHAT_PAY = 14;
    public static final int WHAT_JUDGE_FAVORAL = 15;
    WeakReference<PaymentCenterActivity> wAct = null;
    PaymentCenterActivity pa = null;

    public PaymentCenterHandler(PaymentCenterActivity pAct) {
        wAct = new WeakReference<>(pAct);
        pa = wAct.get();
    }

    public void handleMessage(Message msg) {
//		pa.txt_submitOrder.setClickable(true);
        switch (msg.what) {
            case WHAT_ORDER_SUCCESS:
                pa.hideLoadDialog();
                if (pa.price.getUseScore() > 0) {
                    Thread updateScore = new Thread(pa.scoreRunnable);
                    updateScore.start();
                }

                String orderNum = msg.obj.toString();
                if (pa.payWay.equals(pa.getString(R.string.payWay_account_key))) {

                    pa.accountToPay(orderNum);

                } else if (pa.payWay.equals(pa.getString(R.string.payWay_alipay_key))) {
                    pa.alipayToPay(orderNum);

                } else if (pa.payWay.equals(pa.getString(R.string.payWay_arrivedPay_key))) {
                    pa.toOrderComplete(orderNum, pa.getString(R.string.payWay_arrivedPay_tips), pa.price.getTotalPrice(), false);

                } else if (pa.payWay.equals(pa.getString(R.string.payWay_wechatPay_key))) {
                    pa.wechatToPay(orderNum);

                }
                break;
            case PaymentCenterActivity.RQF_PAY:
                String payInfo = msg.obj.toString();
                Result result = new Result(payInfo);
                String code = payInfo.substring(payInfo.indexOf("{") + 1, payInfo.indexOf("}"));
                pa.alipaySuccess(code, result);
                break;
            case WHAT_GETJLB_FAIL:
                CommonTools.showShortToast(pa, "获取金萝卜失败");
                break;
            case WHAT_GETYUNFEI_SUCCESS:
                pa.initView();
                break;
            case WHAT_ORDER_FAIL:
                pa.hideLoadDialog();
                if (!msg.obj.toString().equals("")) {
                    String message = msg.obj.toString();
                    Logger.e("test", "PaymentCenterHandler handleMessage MESSAGE=" + message);
                    CommonTools.showShortToast(pa.context, message);
                    pa.finish();
                } else {
                    CommonTools.showShortToast(pa.context, "提交订单失败，请检查网络状态。");
                }
                break;
            case WHAT_ORDER_ALREADY_SUBMIT:
                pa.hideLoadDialog();
                if (pa.isIsStock()) {
//                CommonTools.showShortToast(pa, "订单已经提交过,请到订单中心查看");
                } else {
                    CommonTools.showShortToast(pa, "订单已经提交过,请到订单中心查看");
                }
                break;
            case WHAT_WECHAT_PAY:
                //微信支付
                if (pa.isIsStock()) {
                    if (msg.arg1 == 0) {
                        CommonTools.showShortToast(pa, "支付成功");
                    } else {
                        CommonTools.showShortToast(pa, "支付失败");
                    }
                    pa.toWarehouse();
                    pa.finish();
                } else {
                    if (msg.arg1 == 0) {
                        CommonTools.showShortToast(pa, "支付成功");
                    } else {
                        CommonTools.showShortToast(pa, "订单已生成，请到订单中心支付");
                    }
                    pa.toOrderComplete(pa.outOrderNumber, pa.getString(R.string.payWay_wechatPay_tips), pa.price.getTotalPrice(), msg.arg1 == 0);
                }
                break;
        }

    }
}
