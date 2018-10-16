package com.danertu.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderDetail;
import com.danertu.dianping.OrderDetailActivity;
import com.danertu.dianping.R;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.PayWayBean;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.google.gson.Gson;

import java.util.List;

import static com.danertu.dianping.BaseActivity.WHAT_TO_LOGIN;
import static com.danertu.dianping.PaymentCenterHandler.WHAT_WECHAT_PAY;

/**
 * 2018年4月28日
 * 支付成功与否，都跳转至订单详情
 * <p>
 * 2018年9月3日
 * 支付方式通过0351接口获取，不使用传递过来的数据
 * 选择支付方式后通过0353接口更改支付方式之后再调起具体的支付操作
 */
public abstract class PayUtils {
    private static final String TAG = "PayUtils";
    public static final String ORDER_TYPE_STOCK = "warehouse";//囤货--下单后货物放到专属仓库
    public static final String ORDER_TYPE_BACK_CALL = "backcall";//普通后台拿货
    public static final String ORDER_TYPE_ORDER_RETURN = "warehouse_order_return";//退货邮费支付
    private ImageView ivClose;

    private static final int WHAT_PAY = 898;
    private BaseActivity baseActivity;
    public static Handler handler;
    private Context context;
    private String orderNumber;
    private String payPrice;
    private String productName = "";
    private String productGuid = "";
    private String uid;
    private boolean isShowAliPay;
    private boolean isShowWechatPay;
    private boolean isShowAccountPay;
    private boolean isShowArrivePay;
    private boolean isStock = false;      //是否是囤货
    private String orderType = "";          //订单类型

    private boolean isPaying;//支付是否进行中，用于防止启动多个支付进程

    private PayPswDialog dialog_psw = null;
    private PopupWindow popupPayWay;

    private OrderHead orderHead;
    private OrderBody orderBody;

    public static final String PAY_ALI = "Alipay";
    public static final String PAY_WECHAT = "WechatPay";
    public static final String PAY_ACCOUNT = "AccountPay";
    public static final String PAY_ARRIVE = "ArrivedPay";

    public static final String CHECK_TAG_PAY_BEFORE = "payBefore";
    public static final String CHECK_TAG_PAY_AFTER = "payAfter";

    private static int PAY_RESULT_SUCCESS = 1;

    public static int PAY_TYPE_WEB_PAGE = 11;
    public static int PAY_TYPE_APP = 12;
    private AlipayUtil alipayUtil;

    public PayUtils(BaseActivity activity, String uid, String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay) {
        this.context = activity;
        productGuid = "";
        productName = "";
        baseActivity = activity;
        this.uid = uid;
        this.orderNumber = orderNumber;
        this.isShowAliPay = isShowAliPay;
        this.isShowWechatPay = isShowWechatPay;
        this.isShowAccountPay = isShowAccountPay;
        this.isShowArrivePay = isShowArrivePay;
        alipayUtil = new AlipayUtil(context);
        initHandler();
        new GetOrderInfo().execute(orderNumber);
        isStock = false;
        orderType = "";
    }

    public PayUtils(BaseActivity activity, String uid, String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay, boolean isStock, String orderType) {
        this.context = activity;
        productGuid = "";
        productName = "";
        baseActivity = activity;
        this.uid = uid;
        this.orderNumber = orderNumber;
        this.isShowAliPay = isShowAliPay;
        this.isShowWechatPay = isShowWechatPay;
        this.isShowAccountPay = isShowAccountPay;
        this.isShowArrivePay = isShowArrivePay;
        alipayUtil = new AlipayUtil(context);
        initHandler();
        new GetOrderInfo().execute(orderNumber);
        this.isStock = isStock;
        this.orderType = orderType;
    }


    /**
     *
     */
    private void initHandler() {
        handler = new Handler(baseActivity.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WHAT_WECHAT_PAY:
                        new UnionPayTask().execute(orderNumber, PAY_WECHAT, CHECK_TAG_PAY_AFTER);
                        break;
                    case WHAT_PAY:
                        boolean result = msg.arg1 == PAY_RESULT_SUCCESS;
                        sendBroadcast(result);
                        isPaying = false;
                        if (result) {
                            paySuccess();
                        } else {
                            payFail();
                        }
                        break;
                }
            }
        };
    }

    public abstract void paySuccess();

    public abstract void payFail();

    public abstract void payCancel();

    public abstract void payError(String message);

    public abstract void dismissOption();

    /**
     * 初始化支付界面
     *
     * @param payPrice
     * @param isShowAccountPay
     * @param isShowArrivePay
     */
    private void initView(String uid, final String orderNumber, final String productName, final String productDetail, final String payPrice, final boolean isShowAccountPay, boolean isShowArrivePay) {
        View view = LayoutInflater.from(context).inflate(R.layout.popup_choose_pay_way, null);
        popupPayWay = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupPayWay.setContentView(view);

        ivClose = (ImageView) view.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView tvPayPrice = (TextView) view.findViewById(R.id.tv_pay_price);
        tvPayPrice.setText("￥" + payPrice);
        View viewById = view.findViewById(R.id.view);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TextView tvPayAli = (TextView) view.findViewById(R.id.tv_pay_ali);
        TextView tvPayWechat = (TextView) view.findViewById(R.id.tv_pay_wechat);
        TextView tvPayWallet = (TextView) view.findViewById(R.id.tv_pay_wallet);
        TextView tvPayArrive = (TextView) view.findViewById(R.id.tv_pay_arrive);

        if (isShowAccountPay) {
            tvPayWallet.setVisibility(View.VISIBLE);
        }
        if (isShowArrivePay) {
            tvPayArrive.setVisibility(View.VISIBLE);
        }
        /**支付宝支付*/
        tvPayAli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowAliPay) {
                    baseActivity.jsShowMsg("此订单不允许使用支付宝支付，请选择其他支付方式");
                    return;
                }
                if (isPaying) {
                    return;
                }
                isPaying = true;
                dismiss();
                new UnionPayTask().execute(orderNumber, PAY_ALI, CHECK_TAG_PAY_BEFORE);
            }
        });
        /**微信支付*/
        tvPayWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowWechatPay) {
                    baseActivity.jsShowMsg("此订单不允许使用微信支付，请选择其他支付方式");
                    return;
                }
                if (isPaying) {
                    return;
                }
                isPaying = true;
                dismiss();
//                new WechatPay().execute(orderNumber, productName, productDetail, payPrice);
                new UnionPayTask().execute(orderNumber, PAY_WECHAT, CHECK_TAG_PAY_BEFORE);
            }
        });
        /**单耳兔钱包支付*/
        tvPayWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowAccountPay) {
                    baseActivity.jsShowMsg("此订单不允许使用单耳兔钱包支付，请选择其他支付方式");
                    return;
                }
                if (isPaying) {
                    return;
                }
                isPaying = true;
                dismiss();
                new UnionPayTask().execute(orderNumber, PAY_ACCOUNT, CHECK_TAG_PAY_BEFORE);
            }
        });
        /**到付*/
        tvPayArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isShowAccountPay) {
                    baseActivity.jsShowMsg("此订单不允许到付，请选择其他支付方式");
                    return;
                }
                if (isPaying) {
                    return;
                }
                isPaying = true;
                dismiss();
                new UnionPayTask().execute(orderNumber, PAY_ARRIVE, CHECK_TAG_PAY_BEFORE);
            }
        });

        //显示PopupWindow
        popupPayWay.setOutsideTouchable(true);
        popupPayWay.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) baseActivity.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        popupPayWay.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    public PayUtils(BaseActivity context, String uid, String orderNumber) {
        this(context, uid, orderNumber, true, true, true, true);
    }

    /**
     * 不显示到付
     *
     * @param context
     * @param orderNumber
     * @param isShowAccountPay
     */
    public PayUtils(BaseActivity context, String uid, String orderNumber, boolean isShowAccountPay) {
        this(context, uid, orderNumber, true, true, isShowAccountPay, false);
    }

    public void dismiss() {
        if (popupPayWay != null) {
            dismissOption();
            popupPayWay.dismiss();
        }

    }

    /**
     * 跳转至订单详情
     *
     * @param orderNumber
     */
    public void toOrderDetail(String orderNumber) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("orderNumber", orderNumber);
//        bundle.putBoolean("isNotToComplete", true);
        intent.putExtras(bundle);
        baseActivity.startActivityForResult(intent, 121);
    }

    public boolean isPaying() {
        return isPaying;
    }

    /**
     * 支付结果判定
     *
     * @param result
     */
    private void payResult(boolean result) {
        String str;
        int arg1 = 2;
        if (result) {
            arg1 = PAY_RESULT_SUCCESS;
            str = "支付成功";
        } else {
            str = "支付失败，请确认订单";
        }
        sendMessage(WHAT_PAY, str, arg1, -1);
    }

    private void cancelOrder() {
        payCancel();
    }

    private void sendMessage(int what, Object obj, int arg1, int arg2) {
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = obj;
        message.arg1 = arg1;
        message.arg2 = arg2;
        handler.sendMessage(message);
    }

    /**
     * 检查支付状态
     *
     * @param orderNumber
     * @return
     */
    public boolean checkPayStatus(String orderNumber) {
        String orderHead = AppManager.getInstance().postGetOrderHead(orderNumber, uid);
        try {
            TokenExceptionBean bean = JSONObject.parseObject(orderHead, TokenExceptionBean.class);
            if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                final TokenExceptionBean finalBean = bean;
                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        baseActivity.jsShowMsg(finalBean.getInfo());
                        baseActivity.quitAccount();
                        baseActivity.finish();
                        baseActivity.jsStartActivity("LoginActivity", "");
                    }
                });
                return false;
            }
            bean = null;
        } catch (Exception e) {
            if (Constants.isDebug)
                e.printStackTrace();
            OrderHead fromJson = JSONObject.parseObject(orderHead, OrderHead.class);
            return fromJson.getOrderinfolist().getOrderinfobean().get(0).getPaymentStatus().equals("2");
        }
        return false;
    }


    public void initAccountPay() {
        dialog_psw = new PayPswDialog(context, R.style.Dialog) {
            public void cancelDialog() {
                com.danertu.widget.AlertDialog dialog = new com.danertu.widget.AlertDialog(baseActivity, R.style.Dialog) {
                    public void sure() {
                        dismiss();
                    }

                    public void cancelDialog() {
                        dialog_psw.dismiss();
                        dismiss();
                        cancelOrder();
                    }
                };
                //常规订单
                dialog.setTitle("取消付款");
                dialog.setContent("订单已生成,取消后可到订单中心付款");
                dialog.setCancelButton("取消付款");
                dialog.setSureButton("继续付款");
                dialog.setCanBack(false);
                dialog.show();
            }

            @Override
            public void passwordRight() {
                String param[] = {uid, orderNumber, CommonTools.formatZero2Str(Double.parseDouble(payPrice)), ""};
//                String param[] = {uid, outOrderNumber, "0.01", "0.01"};
                AccToPay accToPay = new AccToPay(baseActivity,isStock) {

                    @Override
                    public void paySuccess() {
                        dialog_psw.dismiss();
                        payResult(true);
                    }

                    @Override
                    public void payFail() {
                        payResult(false);
                    }
                };
                accToPay.execute(param);
            }

            @Override
            public void passwordWrong() {
                CommonTools.showShortToast(baseActivity, "支付密码不正确！");
            }
        };
    }

    /**
     * 发出支付结果广播
     *
     * @param result
     */
    public void sendBroadcast(boolean result) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
        intent.putExtra("result", result);
        manager.sendBroadcast(intent);
    }


    class GetProductPayWay extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String productGuids = param[0];
            if (TextUtils.isEmpty(productGuids)) {
                return "";
            }
            return AppManager.getInstance().getProductPayway(productGuids);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(result)) {
                baseActivity.jsShowMsg("支付失败");
                return;
            }
            try {
                PayWayBean payWayBean = JSONObject.parseObject(result, PayWayBean.class);
                if ("false".equals(payWayBean.getResult()) && "-1".equals(payWayBean.getCode())) {
                    baseActivity.sendMessageNew(WHAT_TO_LOGIN, -1, payWayBean.getInfo());
                    isPaying = false;
                    return;
                }
                if (payWayBean.getVal() == null || payWayBean.getVal().size() == 0 || TextUtils.isEmpty(payWayBean.getVal().get(0).getPayWay())) {
                    baseActivity.jsShowMsg("无可用支付方式");
                    isPaying = false;
                    return;
                }
                List<PayWayBean.ValBean> val = payWayBean.getVal();
                String[] payWays = val.get(0).getPayWay().split(",");
                isShowAliPay = false;
                isShowWechatPay = false;
                isShowAccountPay = false;
                isShowArrivePay = false;
                for (String payWay : payWays) {
                    switch (payWay) {
                        case Constants.PAY_WAY.PAY_WAY_ALI:
                            isShowAliPay = true;
                            break;
                        case Constants.PAY_WAY.PAY_WAY_WECHAT:
                            isShowWechatPay = true;
                            break;
                        case Constants.PAY_WAY.PAY_WAY_ACCOUNT:
                            isShowAccountPay = true;
                            break;
                        case Constants.PAY_WAY.PAY_WAY_ARRIVE:
                            isShowArrivePay = true;
                            break;
                    }
                }
                initView(uid, orderNumber, productName, productName, payPrice, isShowAccountPay, isShowArrivePay);
                initAccountPay();
            } catch (Exception e) {
                baseActivity.jsShowMsg("出错了");
                isPaying = false;
                if (Constants.isDebug)
                    e.printStackTrace();
            }
        }
    }

    /**
     * 获取订单信息，用于传递给支付宝、微信支付
     * param[0]:订单号
     */
    class GetOrderInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String orderNum = param[0];
            if (TextUtils.isEmpty(orderNum)) {
                return null;
            }
            Gson gson = new Gson();
            AppManager appManager = AppManager.getInstance();
            final String orderHeadStr = appManager.postGetOrderHead(orderNum, uid);
            if (!TextUtils.isEmpty(orderHeadStr)) {
                if (baseActivity.judgeIsTokenException(orderHeadStr)) {
                    TokenExceptionBean tokenExceptionBean = JSONObject.parseObject(orderHeadStr, TokenExceptionBean.class);
                    baseActivity.sendMessageNew(WHAT_TO_LOGIN, -1, tokenExceptionBean.getInfo());
//                    baseActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                TokenExceptionBean tokenExceptionBean = JSONObject.parseObject(orderHeadStr, TokenExceptionBean.class);
//                                baseActivity.jsShowMsg(tokenExceptionBean.getInfo());
//                                baseActivity.quitAccount();
//                                baseActivity.finish();
//                                baseActivity.jsStartActivity("LoginActivity", "");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                    return "";
                } else {
                    orderHead = gson.fromJson(orderHeadStr, OrderHead.class);
                }
            }
            final String orderBodyStr = appManager.postGetOrderBody(orderNum, uid);
            if (!TextUtils.isEmpty(orderBodyStr)) {
                if (baseActivity.judgeIsTokenException(orderBodyStr)) {
                    TokenExceptionBean tokenExceptionBean = JSONObject.parseObject(orderHeadStr, TokenExceptionBean.class);
                    baseActivity.sendMessageNew(WHAT_TO_LOGIN, -1, tokenExceptionBean.getInfo());
//                    baseActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                TokenExceptionBean tokenExceptionBean = JSONObject.parseObject(orderBodyStr, TokenExceptionBean.class);
//                                baseActivity.jsShowMsg(tokenExceptionBean.getInfo());
//                                baseActivity.quitAccount();
//                                baseActivity.finish();
//                                baseActivity.jsStartActivity("LoginActivity", "");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                    return "";
                } else {
                    orderBody = gson.fromJson(orderBodyStr, OrderBody.class);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (orderHead == null || orderBody == null) {
                    baseActivity.jsShowMsg("获取订单信息失败，请重试");
                    return;
                }
                OrderHead.OrderinfolistBean.OrderinfobeanBean orderHeadBean = orderHead.getOrderinfolist().getOrderinfobean().get(0);
                List<OrderBody.OrderproductlistBean.OrderproductbeanBean> orderproductbeanBeanList = orderBody.getOrderproductlist().getOrderproductbean();
                OrderBody.OrderproductlistBean.OrderproductbeanBean orderBodyBean = orderproductbeanBeanList.get(0);
                if (orderHeadBean == null) {
                    baseActivity.jsShowMsg("获取订单信息失败，请重试");
                    return;
                }
                if (orderBodyBean == null) {
                    baseActivity.jsShowMsg("获取订单信息失败，请重试");
                    return;
                }

                for (OrderBody.OrderproductlistBean.OrderproductbeanBean bean : orderproductbeanBeanList) {
                    productName += bean.getName() + "，";
                    productGuid += bean.getGuid() + ",";
                }

                productName = productName.substring(0, productName.length() - 1);
                productGuid = productGuid.substring(0, productGuid.length() - 1);
                payPrice = orderHeadBean.getShouldPayPrice();
                new GetProductPayWay().execute(productGuid);
            } catch (Exception e) {
                isPaying = false;
                payError("订单信息有误");
                e.printStackTrace();
            }
        }
    }

    /**
     * 统一支付
     * param[0]:订单号
     * param[1]:当前进行的支付前的订单状态检查还是进行支付
     */
    class UnionPayTask extends AsyncTask<String, Integer, String> {
        private String payWay;
        private String checkTag;

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            this.payWay = param[1];
            this.checkTag = param[2];
            return AppManager.getInstance().postGetOrderHead(orderNumber, uid);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            baseActivity.judgeIsTokenException(result, new BaseActivity.TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, final String info) {
                    baseActivity.sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                    baseActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            baseActivity.jsShowMsg(info);
//                            baseActivity.quitAccount();
//                            baseActivity.finish();
//                            baseActivity.jsStartActivity("LoginActivity", "");
//                        }
//                    });
                }

                @Override
                public void ok() {
                    try {
                        OrderHead fromJson = JSONObject.parseObject(result, OrderHead.class);
                        OrderHead.OrderinfolistBean.OrderinfobeanBean bean = fromJson.getOrderinfolist().getOrderinfobean().get(0);
                        boolean checkResult = bean.getPaymentStatus().equals("0") && (bean.getOderStatus().equals("0") || bean.getOderStatus().equals("1")) && bean.getShipmentStatus().equals("0");
                        switch (checkTag) {
                            case CHECK_TAG_PAY_BEFORE:
                                /**支付前的订单状态检查,确认是未支付状态下才能进行支付操作*/
                                if (checkResult) {
                                    new ChangePayWay().execute(orderNumber, uid, payWay);
                                } else {
                                    //已支付、已取消的订单
                                    baseActivity.jsShowMsg("订单状态异常，请确认后再支付");
                                }
                                break;
                            case CHECK_TAG_PAY_AFTER:
                                payResult(!checkResult);
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    class ChangePayWay extends AsyncTask<String, Integer, String> {
        String paymentName = "";

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            String uid = param[1];
            paymentName = param[2];
            return AppManager.getInstance().getChangeOrderPayway(orderNumber, uid, Constants.deviceType, paymentName);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(result)) {
                baseActivity.jsShowMsg("支付失败");
                isPaying = false;
                return;
            }
            try {
                TokenExceptionBean bean = JSONObject.parseObject(result, TokenExceptionBean.class);
                if ("true".equals(bean.getResult())) {
                    switch (paymentName) {
                        case PAY_ALI:
                            new AliPay().execute(orderNumber, productName, productName, CommonTools.formatZero2Str(Double.parseDouble(payPrice)));
                            break;
                        case PAY_WECHAT:
                            WXPay wxPay = new WXPay(context);
                            if (isStock) {
                                wxPay.toPay(productName, orderNumber, CommonTools.formatZero2Str(Double.parseDouble(payPrice)), orderType);
                            } else {
                                wxPay.toPay(productName, orderNumber, CommonTools.formatZero2Str(Double.parseDouble(payPrice)));
                            }
                            break;
                        case PAY_ACCOUNT:
                            dialog_psw.show();
                            break;
                        case PAY_ARRIVE:
                            if (isStock) {
                                baseActivity.jsShowMsg("囤货订单不可使用到付");
                                return;
                            }
                            baseActivity.jsShowMsg("您选择了到付");
                            toOrderDetail(orderNumber);
                            isPaying = false;
                            break;
                    }

                } else if ("false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                    baseActivity.sendMessageNew(WHAT_TO_LOGIN, -1, bean.getInfo());
                } else {
                    baseActivity.jsShowMsg("支付失败");
                    isPaying = false;
                }

            } catch (Exception e) {
                baseActivity.jsShowMsg("支付失败");
                isPaying = false;
                if (Constants.isDebug)
                    e.printStackTrace();
            }
        }
    }

    /**
     * 支付宝支付线程
     */
    class AliPay extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... param) {
            String orderNumber = param[0];
            String name = param[1];
            String info = param[2];
            String payPrice = param[3];

            String payInfo = null;
            boolean payResult = false;
            try {
                String orderInfo = "";
                if (isStock) {
                    orderInfo = alipayUtil.getSignPayOrderInfo(orderNumber, name, info, CommonTools.formatZero2Str(Double.parseDouble(payPrice)), orderType);
                } else {
                    orderInfo = alipayUtil.getSignPayOrderInfo(orderNumber, name, info, CommonTools.formatZero2Str(Double.parseDouble(payPrice)));
                }
                PayTask aliPay = new PayTask(baseActivity);
                payInfo = aliPay.pay(orderInfo);

                Result result = new Result(payInfo);
                String code = payInfo.substring(payInfo.indexOf("{") + 1, payInfo.indexOf("}"));

//                if (code.equals("9000")) {
//                    //支付成功
//                }
                //向服务器请求一次订单数据，检查支付状态
                payResult = checkPayStatus(orderNumber);
            } catch (Exception e) {
                payResult = false;
                baseActivity.jsShowMsg("支付宝获取参数错误");
                e.printStackTrace();
            }
            return payResult;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            payResult(result);
        }
    }


    /**
     * 微信支付
     */
    class WechatPay extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... param) {
            String orderNumber = param[0];
            String name = param[1];
            String info = param[2];
            String payPrice = param[3];

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

        }
    }

}
