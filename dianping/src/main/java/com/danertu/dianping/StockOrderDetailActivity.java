package com.danertu.dianping;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.entity.StockOrderDetailBean;
import com.danertu.entity.StockOrderReturnDetail;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.Result;
import com.danertu.tools.StockAlipayUtil;
import com.danertu.tools.StockOrderReturnAccountPay;
import com.danertu.tools.StockWXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wl.codelibrary.widget.CircleImageView;
import wl.codelibrary.widget.IOSDialog;

import static com.danertu.dianping.MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER;
import static com.danertu.dianping.PaymentCenterActivity.ORDER_TYPE_ORDER_RETURN;
import static com.danertu.dianping.PaymentCenterHandler.WHAT_WECHAT_PAY;

public class StockOrderDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_ALIPAY = 121;
    public static final int RESULT_RETURN = 999;
    private static final int WHAT_TAKE_GOODS_SUCCESS = 122;
    private static final int WHAT_TAKE_GOODS_FAIL = 123;
    private Button btn_back;
    private TextView tv_title;
    private TextView tv_order_trade_state;
    private TextView tv_return_remark;

    private TextView tv_returns_cost;
    private LinearLayout ll_address;
    private TextView tv_payCenter_recName;
    private TextView tv_payCenter_recMobile;
    private TextView tv_payCenter_recAddress;

    private ImageView iv_order_produce_logo;
    private TextView tv_order_produce_title;
    private TextView tv_order_produce_dec;
    private TextView item_joinCount;
    private TextView tv_item_favourable_tip;
    private TextView tv_order_1;
    private TextView tv_order_proMarketPrice;
    private TextView tv_order_discount_price;
    private TextView tv_order_produce_num;

    private TextView tv_produce_total_price;
    private TextView tv_ship_cost;
    private TextView tv_order_total_price;
    private TextView tv_order_remark;
    private TextView tv_order_num;
    private TextView tv_order_createTime;
    private TextView tv_order_payway;
    private Button b_copy_orderNum;
    private Button b_order_opera1;
    private Button b_order_opera2;
    private Button btn_pay_return_cost;

    private PopupWindow returnCostPopup;
    private Dialog payWayDialog;
    private ImageLoader imageLoader;

    private String guid;
    private String returnGuid;//提货退货订单guid
    private String returnNumber;//提货退货订单号
    private String productName;
    private String productImg;
    private String productCount;
    private String returnCost;//退货费用
    private String uid;

    private String outOrderNumber;

    private String orderState;
    private String shipmentState;

    private Context context;

    public static final String BTN_SHIPMENT = "查看物流";
    public static final String BTN_RECEIVED = "确认收货";

    //    private String payWay = "AccountPay";
    private String payWay = "";
    private static final String PAY_WAY_ACCOUNT = "AccountPay";
    private static final String PAY_WAY_ALIPAY = "Alipay";
    private static final String PAY_WAY_WECHAPAY = "WechatPay";

    private PayPswDialog dialog_psw = null;
    private StockWXPay stockWXPay;
    private SetReturnPayWay setReturnPayWay;


    public static Handler newHandler;
    private String buyNumber;
    private String shipNumber;
    private String payStatus;
    private String shipmentStatus;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_order_detail);
        context = this;
        imageLoader = ImageLoader.getInstance();
        setSystemBarWhite();
        findViewById();
        initView();
        initData();
        initHandler();
    }


    private void initData() {
        showLoadDialog();
        uid = getUid();
        Intent intent = getIntent();
        guid = intent.getStringExtra("guid");
        outOrderNumber = intent.getStringExtra("orderNumber");
        orderState = intent.getStringExtra("orderState");
        shipmentState = intent.getStringExtra("shipmentState");
        Logger.e(TAG, "订单状态：==" + orderState + ",物流状态==" + shipmentState);
        position = intent.getIntExtra("position", 0);
        tv_order_trade_state.setText(orderState);
        tv_order_num.setText("订单号：" + outOrderNumber);

        String state = "";
//        if ("1".equals(orderState)) {
        switch (shipmentState) {
            case "0":
                //未发货
                state = "待发货";
                //待发货状态下都不可点击
                new GetInfo().execute(guid);
                b_order_opera1.setEnabled(false);
                b_order_opera1.setVisibility(View.VISIBLE);
                b_order_opera2.setText(BTN_RECEIVED);
                b_order_opera2.setEnabled(false);
                b_order_opera2.setVisibility(View.VISIBLE);
                break;
            case "1":
                //已发货
                state = "待收货";
                new GetInfo().execute(guid);
                b_order_opera1.setEnabled(true);
                b_order_opera2.setEnabled(true);
                b_order_opera1.setVisibility(View.VISIBLE);
                b_order_opera2.setVisibility(View.VISIBLE);
                //确认收货
                b_order_opera2.setText(BTN_RECEIVED);
                b_order_opera2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //to do 确认收货操作
                        takeGoods();
                    }
                });
                break;
            case "2":
                //已收货
                state = "已收货";
                new GetInfo().execute(guid);
                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
                break;
            case "3":
                //配货
                break;
            case "4":
                //退货
                state = "退货中";
                new GetReturnInfo().execute(outOrderNumber);
                tv_order_total_price.setTextColor(ContextCompat.getColor(context, R.color.gray));

                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
                break;
            case "5":
                state = "已退货";
                new GetInfo().execute(guid);
                tv_order_total_price.setTextColor(ContextCompat.getColor(context, R.color.gray));
                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
                break;
            default:
                jsShowMsg("数据加载错误");
                jsFinish();
                break;
        }
//        }
        tv_order_trade_state.setText(state);
//
//        switch (orderState) {
//            case "待发货":
//
//                break;
//            case "待收货":
//
//
//                break;
//            case "已完成":
//
//                break;
//            case "退货中":
//
//                break;
//            case "已退货":
//
//                break;
//            case "已取消":
//                new GetInfo().execute(guid);
//                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
//                break;
//            case "已作废":
//                new GetInfo().execute(guid);
//                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
//                break;
//            case "请退货":
//                new GetInfo().execute(guid);
//                b_order_opera2.setVisibility(View.GONE);
//                break;
//            case "交易成功":
//                new GetInfo().execute(guid);
//                ((View) b_order_opera2.getParent()).setVisibility(View.GONE);
//                break;
//
//        }
    }

    /**
     * 弹窗提示用户收到货后确认
     */
    private void takeGoods() {
        final IOSDialog dialog = new IOSDialog(context);
        dialog.setMessage("请确认您已收到货物后操作");
        dialog.setPositiveButton("我已收到货", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showLoadDialog();
                new Thread(sureTakeGoods).start();
            }
        });
        dialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //    发起确认收货请求
    public Runnable sureTakeGoods = new Runnable() {
        public void run() {
            if (AppManager.getInstance().postFinishOrder(outOrderNumber)) {
                sendHandlerMessage(WHAT_TAKE_GOODS_SUCCESS, null);// 表示确定收货成功
            } else {
                sendHandlerMessage(WHAT_TAKE_GOODS_FAIL, null);
            }

        }
    };

    public void initHandler() {
        newHandler = new Handler(context.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_ALIPAY:
                        //支付宝支付回调结果
                        String payInfo = msg.obj.toString();
                        Result result = new Result(payInfo);
                        String code = payInfo.substring(payInfo.indexOf("{") + 1, payInfo.indexOf("}"));
                        alipaySuccess(code, result);
                        setResultReturn(RESULT_RETURN, position);
                        break;
                    case WHAT_WECHAT_PAY:
                        //微信支付回调结果
                        if (msg.arg1 == 0) {
                            //支付成功
                            returnSuccess();
                        } else if (msg.arg1 == -2) {
                            CommonTools.showShortToast(context, "您取消了支付");
                        } else {
                            CommonTools.showShortToast(context, "支付失败");
                        }
                        break;
                    case WHAT_TAKE_GOODS_SUCCESS:
                        jsShowMsg("确认收货成功");
                        tv_order_trade_state.setText("已收货");
                        b_order_opera2.setEnabled(false);
                        b_order_opera2.setVisibility(View.GONE);
                        hideLoadDialog();
                        break;
                    case WHAT_TAKE_GOODS_FAIL:
                        jsShowMsg("确认收货失败");
                        hideLoadDialog();
                        break;
                }
            }
        };
    }

    /**
     * 获取订单信息.接口为0329
     * 而退货订单接口为 0333
     */
    class GetInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String guid = strings[0];
            Map<String, String> map = new HashMap<>();
            map.put("apiid", "0329");
            map.put("memLoginId", uid);
            map.put("guid", guid);
            return appManager.doPost(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //显示数据
                StockOrderDetailBean.ValBean bean = gson.fromJson(s, StockOrderDetailBean.class).getVal().get(0);
                //地址
                tv_payCenter_recName.setText(bean.getName());
                tv_payCenter_recAddress.setText(bean.getAddress());
                tv_payCenter_recMobile.setText(bean.getMobile());
                //商品信息
                productImg = bean.getSmallImage();
                imageLoader.displayImage(getStockSmallImgPath(productImg), iv_order_produce_logo);
                productName = bean.getProductName();
                tv_order_produce_title.setText(productName);
                tv_order_proMarketPrice.setText("￥" + bean.getMarketPrice());
                tv_order_proMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//加上删除线
                tv_order_proMarketPrice.getPaint().setAntiAlias(true);
                tv_order_1.setText("商城价");
                tv_order_discount_price.setText("￥" + bean.getShopPrice());
                productCount = bean.getBuyNumber();
                tv_order_produce_num.setText("x" + productCount);
                tv_order_total_price.setText("￥" + bean.getTotalPrice());
                tv_produce_total_price.setText("￥" + bean.getTotalPrice());
                tv_ship_cost.setText("￥0.00");
                String remark = TextUtils.isEmpty(bean.getRemark()) ? "无" : bean.getRemark();
                tv_order_remark.setText(remark);
                tv_order_createTime.setText("创建时间：" + bean.getCreateTime().replace("/", "."));

                shipNumber = bean.getShipmentNumber();


                //订单号为空的时候，查看物流不可点击
                if (TextUtils.isEmpty(shipNumber)) {
                    b_order_opera1.setEnabled(false);
                }
                //查看物流
                b_order_opera1.setText(BTN_SHIPMENT);
                b_order_opera1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //to do 查看物流
                        Intent intent1 = new Intent(context, MyOrderShipmentActivity.class);
                        intent1.putExtra(KEY_SHIPMENT_NUMBER, shipNumber);
                        startActivity(intent1);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                jsShowMsg("数据加载错误");
                jsFinish();
            }
            hideLoadDialog();
        }
    }

    /**
     * 2018年1月3日
     * 获取退货商品详细信息 接口-->0333
     */
    class GetReturnInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String outOrderNumber = strings[0];
            Map<String, String> map = new HashMap<>();
            map.put("apiid", "0333");
            map.put("memLoginId", uid);
            map.put("wareHouseOrderOrderNumber", outOrderNumber);
//            map.put("memLoginId", "15015007777");
//            map.put("wareHouseOrderOrderNumber", "201712192358144");
            return appManager.doPost(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                //显示数据
                StockOrderReturnDetail.ValBean bean = gson.fromJson(s, StockOrderReturnDetail.class).getVal().get(0);
                //地址
                returnGuid = bean.getGuid();
                returnNumber = bean.getOrderNumber();
                tv_payCenter_recName.setText(bean.getName());
                tv_payCenter_recAddress.setText(bean.getAddress());
                tv_payCenter_recMobile.setText(bean.getMobile());
                //商品信息
                productImg = bean.getSmallImage();
                imageLoader.displayImage(getStockSmallImgPath(productImg), iv_order_produce_logo);
                productName = bean.getProductName();
                tv_order_produce_title.setText(productName);
                tv_order_proMarketPrice.setText("￥" + bean.getMarketPrice());
                tv_order_proMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//加上删除线
                tv_order_proMarketPrice.getPaint().setAntiAlias(true);
                tv_order_1.setText("商城价");
                tv_order_discount_price.setText("￥" + bean.getShopPrice());
                productCount = bean.getBuyNumber();
                tv_order_produce_num.setText("x" + productCount);
                tv_order_total_price.setText("￥" + bean.getTotalPrice());
                tv_produce_total_price.setText("￥" + bean.getTotalPrice());
                tv_ship_cost.setText("￥0.00");
                String remark = TextUtils.isEmpty(bean.getRemark()) ? "无" : bean.getRemark();
                tv_order_remark.setText(remark);
                tv_order_createTime.setText("创建时间：" + bean.getCreateTime().replace("/", "."));

                buyNumber = bean.getBuyNumber();
                returnCost = bean.getWareHouseOrderReturnFreight();

                btn_pay_return_cost.setVisibility(View.VISIBLE);

                payStatus = bean.getPayStatus();//0-->未付款，2-->已付款
                shipmentStatus = bean.getShipmentStatus();//0 未发货，1 已发货，2 已收货，3 配货，4 退货中，5 已退货

                orderState = "退货中";
                if ("0".equals(payStatus)) {
                    //运费未支付
                    tv_return_remark.setText("退货数量为" + buyNumber + "件,退货产生运费￥" + returnCost + ",支付费用后货物将回到您的仓库中");
                    tv_return_remark.setVisibility(View.VISIBLE);
                    btn_pay_return_cost.setVisibility(View.VISIBLE);
                } else if ("2".equals(payStatus)) {
                    //运费已支付
                    switch (shipmentStatus) {
                        case "0":
                            break;
                        case "1":
                            break;
                        case "2":
                            break;
                        case "3":
                            break;
                        case "4":
                            orderState = "退货中";
                            tv_return_remark.setText("退货数量为" + buyNumber + "件,退货产生运费￥" + returnCost + ",货物将回到您的仓库中");
                            tv_return_remark.setVisibility(View.VISIBLE);
                            btn_pay_return_cost.setVisibility(View.VISIBLE);
                            break;
                        case "5":
                            //退货已完成
                            orderState = "已退货";
                            tv_return_remark.setText("退货数量为" + buyNumber + "件,退货产生运费￥" + returnCost + ",货物已回到您的仓库中");
                            tv_return_remark.setVisibility(View.VISIBLE);
                            btn_pay_return_cost.setVisibility(View.GONE);
                            break;
                    }

                }
//                switch (payStatus) {
//                    case "0"://未付款
//
//                        break;
////                    case "1"://付款中
////                        orderState="退货中";
////                        break;
//                    case "2"://已付款
//                        orderState="退货中";
//                        break;
////                    case "3"://退款
////                        orderState="退款中";
////                        break;
////                    case "4":
////
////                        break;
////                    case "5"://退货已完成
////                        orderState="已退货";
////                        tv_return_remark.setText("退货数量为" + buyNumber + "件,退货产生运费￥" + returnCost + ",货物已回到您的仓库中");
////                        tv_return_remark.setVisibility(View.VISIBLE);
////                        btn_pay_return_cost.setVisibility(View.GONE);
////                        break;
//                }
//
//                switch (shipmentStatus){
//                    case "0":
//                        break;
//                    case "1":
//                        break;
//                    case "2":
//                        break;
//                    case "3":
//                        break;
//                    case "4":
//                        orderState="已退货";
//                        break;
//                }
//                ((View) b_order_opera1.getParent()).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
                jsShowMsg("数据加载错误");
                jsFinish();
            }
            hideLoadDialog();
        }
    }

    @Override
    protected void findViewById() {
        btn_back = $(R.id.b_title_back);
        tv_title = $(R.id.tv_title);
        $(R.id.b_title_operation).setVisibility(View.GONE);
        $(R.id.tv_order_2).setVisibility(View.GONE);
        $(R.id.tv_order_produce_price).setVisibility(View.GONE);
        tv_order_1 = $(R.id.tv_order_1);

        tv_order_trade_state = $(R.id.tv_order_trade_state);
        tv_returns_cost = $(R.id.tv_returns_cost);
        tv_return_remark = $(R.id.tv_return_remark);
        ll_address = $(R.id.ll_address);
        tv_payCenter_recName = $(R.id.tv_payCenter_recName);
        tv_payCenter_recMobile = $(R.id.tv_payCenter_recMobile);
        tv_payCenter_recAddress = $(R.id.tv_payCenter_recAddress);
        iv_order_produce_logo = $(R.id.iv_order_produce_logo);

        tv_order_produce_title = $(R.id.tv_order_produce_title);
        tv_order_produce_dec = $(R.id.tv_order_produce_dec);
        item_joinCount = $(R.id.item_joinCount);
        tv_item_favourable_tip = $(R.id.tv_item_favourable_tip);
        tv_order_proMarketPrice = $(R.id.tv_order_proMarketPrice);
        tv_order_discount_price = $(R.id.tv_order_discount_price);
        tv_order_produce_num = $(R.id.tv_order_produce_num);

        tv_produce_total_price = $(R.id.tv_produce_total_price);
        tv_ship_cost = $(R.id.tv_ship_cost);
        tv_order_total_price = $(R.id.tv_order_total_price);
        tv_order_remark = $(R.id.tv_order_remark);
        tv_order_num = $(R.id.tv_order_num);
        tv_order_createTime = $(R.id.tv_order_createTime);
        tv_order_payway = $(R.id.tv_order_payway);
        b_copy_orderNum = $(R.id.b_copy_orderNum);
        b_order_opera1 = $(R.id.b_order_opera1);
        b_order_opera2 = $(R.id.b_order_opera2);
        btn_pay_return_cost = $(R.id.btn_pay_return_cost);
        $(R.id.ib_toSelectAddress).setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        tv_title.setText("订单详细");
        tv_order_1.setText("商城价:");
        btn_back.setOnClickListener(this);
        b_copy_orderNum.setOnClickListener(this);
        btn_pay_return_cost.setOnClickListener(this);
        btn_pay_return_cost.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back:
                jsFinish();
                break;
            case R.id.b_copy_orderNum:
                //复制订单号
                final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(outOrderNumber);
                jsShowMsg("复制成功");
                break;
            case R.id.btn_pay_return_cost:
                //支付退货费用
                showReturnCostPopup();
                break;
        }
    }

    /**
     * 选择退货费用支付方式
     */
    private void selectPayWay(final TextView tv_pay_way) {
        if (payWayDialog == null) {
            payWayDialog = MyDialog.getDefineDialog(context, R.layout.dialog_payway);
            ViewGroup rg = (ViewGroup) payWayDialog.findViewById(R.id.rg_payWay_group);
            int len = rg.getChildCount();
            for (int i = 0; i < len; i++) {
                View v = rg.getChildAt(i);
                if (v instanceof RadioButton && v.getVisibility() == View.VISIBLE) {
                    final RadioButton btn = (RadioButton) v;
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            payWay = btn.getTag().toString();
                            if (setReturnPayWay == null)
                                setReturnPayWay = new SetReturnPayWay();
                            //修改提货退货支付方式
                            setReturnPayWay.execute(returnGuid);

                            tv_pay_way.setText(btn.getText().toString());
                        }
                    });
                }
            }
        }
        payWayDialog.show();
    }

    /**
     * 2018年1月5日
     * 设置支付方式时通知后台
     */
    class SetReturnPayWay extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String wareHouseOrderReturnGuid = strings[0];
            HashMap<String, String> map = new HashMap<>();
            map.put("apiid", "0334");
            map.put("wareHouseOrderReturnGuid", wareHouseOrderReturnGuid);
            map.put("mobilePayWay", payWay);
            map.put("deviceType", Constants.deviceType);

            return appManager.doPost(map);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //{ "result":"true", "info": "设置成功"}
            try {
                JSONObject object = new JSONObject(s);
                boolean result = object.getBoolean("result");
                String info = object.getString("info");
                if (result) {
                    //设置成功
                    payWayDialog.dismiss();
                    //支付按钮可点击
                    returnCostPopup.getContentView().findViewById(R.id.tv_confirm).setEnabled(true);
                } else {
                    jsShowMsg(info);
//                    setReturnPayWay.execute(guid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 支付退货费用弹窗
     */
    public void showReturnCostPopup() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_returns_goods, null);
        returnCostPopup = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        returnCostPopup.setContentView(contentView);
        ImageView iv_close = (ImageView) contentView.findViewById(R.id.iv_close);
        TextView tv_product_name = (TextView) contentView.findViewById(R.id.tv_product_name);
        CircleImageView civ_product = (CircleImageView) contentView.findViewById(R.id.civ_product);
        TextView tv_returns_count = (TextView) contentView.findViewById(R.id.tv_returns_count);
        TextView tv_returns_cost = (TextView) contentView.findViewById(R.id.tv_returns_cost);
        final TextView tv_pay_way = (TextView) contentView.findViewById(R.id.tv_pay_way);
        TextView tv_confirm = (TextView) contentView.findViewById(R.id.tv_confirm);

        tv_confirm.setEnabled(false);

        imageLoader.displayImage(getStockSmallImgPath(productImg), civ_product);

//        tv_pay_way.setText(getResources().getString(R.string.payWay_account_tips));

        tv_product_name.setText(productName);
        tv_returns_count.setText(productCount);
        tv_returns_cost.setText("￥" + returnCost);

        //支付方式
        tv_pay_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPayWay(tv_pay_way);
            }
        });

        //支付费用
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //根据选择的支付方式调用支付
                //这里的支付退货费用相当于购买一件商品
//                payReturnCost(productName, productCount, payWay, returnCost);
                payReturnCost(productName, productCount, payWay, returnCost);
            }
        });

        View view = contentView.findViewById(R.id.view);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnCostPopup.dismiss();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnCostPopup.dismiss();
            }
        });

        //显示PopupWindow
        returnCostPopup.setOutsideTouchable(true);
        returnCostPopup.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        returnCostPopup.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 支付退货费用
     *
     * @param productName
     * @param productCount
     * @param payWay
     * @param returnCost
     */
    private void payReturnCost(String productName, String productCount, String payWay, String returnCost) {
        switch (payWay) {
            case PAY_WAY_ACCOUNT:
                initPswDialog(uid, returnNumber, returnCost);
//                initPswDialog("15015007777", "201801034248611", "30.00");
                break;
            case PAY_WAY_WECHAPAY:
                wechatToPay(productName, returnNumber, returnCost);
                break;
            case PAY_WAY_ALIPAY:
                aliPay(productName, productName + "x" + productCount, returnNumber, returnCost);
//                aliPay(productName, productName + "x" + productCount, "201801034248611", returnCost);
                break;
        }
    }

    /**
     * 账号支付窗口
     */
    private void initPswDialog(final String uid, final String orderNumber, final String returnCost) {
        dialog_psw = new PayPswDialog(this, R.style.Dialog) {
            public void cancelDialog() {
                com.danertu.widget.AlertDialog dialog = new com.danertu.widget.AlertDialog(getContext(), R.style.Dialog) {
                    public void sure() {
                        dismiss();
                    }

                    public void cancelDialog() {
                        dialog_psw.dismiss();
                        dismiss();
                    }
                };
                dialog.setTitle("取消付款");
                dialog.setContent("为尽快完成退货流程，请您及时支付退货费用");
                dialog.setCancelButton("取消付款");
                dialog.setSureButton("继续付款");
                dialog.setCanBack(false);
                dialog.show();
            }

            @Override
            public void passwordRight() {
                String param[] = {uid, orderNumber, returnCost};
//                String param[] = {uid, outOrderNumber, "0.01", "0.01"};
                StockOrderReturnAccountPay stockAccountPay = new StockOrderReturnAccountPay(getContext()) {
                    @Override
                    public void paySuccess() {
                        returnSuccess();
                        dialog_psw.dismiss();
                        //运费账号支付成功
                    }

                    @Override
                    public void payFail() {

                    }

                    @Override
                    public void payError() {
                        //支付出错
                        hideLoadDialog();
                    }
                };
                stockAccountPay.execute(param);
            }

            @Override
            public void passwordWrong() {
                CommonTools.showShortToast(getContext(), "支付密码不正确！");
            }
        };
        dialog_psw.show();
    }

    /**
     * 退货成功
     */
    private void returnSuccess() {
        setResultReturn(RESULT_RETURN, position);
        btn_pay_return_cost.setEnabled(false);
        btn_pay_return_cost.setVisibility(View.GONE);
        if (returnCostPopup != null)
            returnCostPopup.dismiss();
        tv_order_trade_state.setText("已退货");
        tv_return_remark.setText("退货数量为" + buyNumber + "件,退货产生运费￥" + returnCost + ",货物已回到您的仓库中");
    }

    /**
     * 退货费用微信支付
     * 微信支付
     */
    public void wechatToPay(String productName, String orderNumber, String price) {
        if (stockWXPay == null)
            stockWXPay = new StockWXPay(context);
        stockWXPay.toPay(productName, orderNumber, price, ORDER_TYPE_ORDER_RETURN);
    }

    /**
     * 退货费用支付宝支付
     *
     * @param productName
     * @param productDetail
     * @param orderNumber
     * @param totalPrice
     */
    public void aliPay(final String productName, final String productDetail, final String orderNumber, final String totalPrice) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String orderInfo = new StockAlipayUtil(getContext()).getSignPayOrderInfo(orderNumber, productName, productDetail, totalPrice, ORDER_TYPE_ORDER_RETURN);
//                    String orderInfo = new StockAlipayUtil(getContext()).getSignPayOrderInfo(orderNumber, productName, productDetail, "0.01", ORDER_TYPE_ORDER_RETURN);
                    PayTask aliPay = new PayTask(StockOrderDetailActivity.this);
                    String result = aliPay.pay(orderInfo);
                    Logger.i("alipayResult", result);
                    sendHandlerMessage(MSG_ALIPAY, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    jsShowMsg("支付宝获取参数错误");
                }
            }
        }).start();
    }

    public void alipaySuccess(String code, Result result) {
        if (TextUtils.equals(code, "9000")) {
//			tupdate.start();//支付宝支付成功后需要更新订单状态
//            toOrderComplete(outOrderNumber, getString(R.string.payWay_alipay_tips), price.getTotalPrice(), true);
            jsShowMsg("支付成功");
            returnSuccess();

        } else {
            // 判断resultStatus 为非“9000”则代表可能支付失败
            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
            if (TextUtils.equals(code, "8000")) {
                jsShowMsg("支付结果确认中，请以实际为准");
                btn_pay_return_cost.setEnabled(false);
                btn_pay_return_cost.setVisibility(View.GONE);
                returnCostPopup.dismiss();
            }
        }
    }

    public void sendHandlerMessage(int what, Object obj) {
        Message message = newHandler.obtainMessage();
        message.what = what;
        message.obj = obj;
        newHandler.sendMessage(message);
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void setResultReturn(int resultCode, int position) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        setResult(resultCode, intent);
    }
}
