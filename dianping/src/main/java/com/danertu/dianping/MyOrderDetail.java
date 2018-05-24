package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.entity.MyOrderData;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.entity.ProductCategory;
import com.danertu.tools.AccToPay;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.LoadingDialog;
import com.danertu.tools.Logger;
import com.danertu.tools.MathUtils;
import com.danertu.tools.MyDialog;
import com.danertu.tools.PayUtils;
import com.danertu.tools.Result;
import com.danertu.tools.WXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 订单详情页面
 * <p>
 * 2018年5月21日
 * 因为到付订单在核销机上可以修改订单数量，所以修改了onActivityResult中更新订单信息的代码,更新全部的订单信息
 * 注：原本只是修改订单状态
 */
public class MyOrderDetail extends BaseActivity {
    //常量
    public static final String KEY_ORDER_STATE = "orderState";
    public static final String KEY_ORDER_ITEM = "orderItem";
    private static final int REQUEST_SHOW_QRCODE = 22;

    /**
     * 2018年5月22日
     * 温泉产品新页面地址
     */
    public static final String QUANYAN_PRODUCT_URL = "Android/ticket_route.html";
    /**
     * 订单状态
     */
    //未付款
    private final String ORDER_STATUS_NO_PAY = "订单还没付款";
    //门票未付款提示
    private final String ORDER_STATUS_NO_PAY_TIPS_TICKET = "订单还未完成支付";

    //门票已付款并未使用
    private final String ORDER_STATUS_NO_USE = "未使用";
    private final String ORDER_STATUS_NO_USE_TIPS = "订单支付完成，现在可以使用了";
    //门票到付
    private final String ORDER_STATUS_ARRAY_PAY_TICKET = "到店支付入园";
    private final String ORDER_STATUS_ARRAY_PAYTIPS_TICKET = "到付订单可到前台付款，也可更改为在线付款";
    //门票已使用
    private final String ORDER_STATUS_USED_TICKET = "已使用";
    private final String ORDER_STATUS_USED_TIPS_TICKET = "入园时间：";

    //酒店未付款提示
    private final String ORDER_STATUS_NO_PAY_TIPS_HOTEL = "该订单还未付款，请在30分钟内付款";
    //酒店已付款
    private final String ORDER_STATUS_PAYED_HOTEL = "请准时到店入住";
    private final String ORDER_STATUS_PAYED_HOTEL_TIPS = "订单支付完成，现可入住酒店";
    //已完成
    private final String ORDER_STATUS_FINISH_HOTEL = "已完成";
    private final String ORDER_STATUS_FINISH_HOTEL_TIPS = "订单已成功消费";

    //酒水未付款
    private final String ORDER_STATUS_NO_PAY_GOODS = "订单还没付款";
    private final String ORDER_STATUS_NO_PAY_TIPS_GOODS = "付款后我们将会为您安排发货";
    //酒水已付款
    private final String ORDER_STATUS_PAYED_GOODS = "等待商家发货";
    private final String ORDER_STATUS_PAYED_TIPS_GOODS = "订单支付完成，请等待商家为您发货";
    //酒水待收货
    private final String ORDER_STATUS_SENDED_GOODS = "商家已发货";
    private final String ORDER_STATUS_SENDED_TIPS_GOODS = "请留意快递信息，如有疑问，请联系客服";
    //酒水退款
    private final String ORDER_STATUS_RETURNING_GOODS = "退款处理中";
    private final String ORDER_STATUS_RETURNED_GOODS = "资金已退回您账户中";
    private final String ORDER_STATUS_RETURN_TIPS_GOODS = "如有疑问，请联系客服";
    //酒水已完成
    private final String ORDER_STATUS_FINISHED_GOODS = "交易完成";
    private final String ORDER_STATUS_FINISHED_TIPS_GOODS = "如有疑问，请联系客服";


    //已取消、已作废、请退货
    private final String ORDER_STATUS_CANCELED = "已取消";
    private final String ORDER_STATUS_INVALID = "已作废";
    private final String ORDER_STATUS_OTHER_TIPS = "如有疑问，请联系客服";

    //支付信息
    private final String PAY_INFO_NO_PAY = "未付款";
    private final String PAY_INFO_NO_PAY_QUANYAN = "您选择到店支付";
    private final String PAY_INFO_ARRIVE_PAY = "您已完成支付";
    private final String PAY_INFO_ALI_PAY = "您已通过支付宝完成支付";
    private final String PAY_INFO_WECHAT_PAY = "您已通过微信完成支付";
    private final String PAY_INFO_ACCOUNT_PAY = "您已通过单耳兔钱包完成支付";
    private final String PAY_WAY_ALI = "支付宝";
    private final String PAY_WAY_WECHAT = "微信";
    private final String PAY_WAY_ACCOUNT = "账号支付";
    private final String PAY_WAY_ARRIVE = "到付";

    //UI
//	private View contentView;
    private TextView tv_recName, tv_recMobile;
    /**
     * 存在 null 的情况
     */
    private TextView tv_recAddress;         //收货地址
    private TextView tv_orderNum;           //订单号
    private TextView tv_orderCreateTime;    //订单创建时间
    private TextView tv_orderState;         //订单状态
    private TextView tv_orderStateTips;     //订单状态提示
    private TextView tv_help;               //联系客服
    private TextView tv_order_count_price;  //总数量、总价格、运费等信息  共998件商品，共￥12455504.00元（含运费0.00）


    private LinearLayout ll_proParent;
    private TextView tv_payWayInfo;
    //    private Button b_copy_orderNum;
    private Button b_order_opera1;
    private Button b_order_opera2;
    /**
     * 酒店的入住时间
     */
    private LinearLayout ll_hotel_date;
    private TextView tv_order_arrive_time;
    private TextView tv_order_leave_time;

    private PayPswDialog dialog_psw;
    private Context context;
    private AlipayUtil alipayUtil;
    public static Handler handler;

    /**
     * 2018年4月16日 注释
     */
//    private FrameLayout fl_qr_code;
//    private ImageView iv_qr_code;
    //变量
    private HashMap<String, Object> orderBean;
    private String pricedata;
    private String outOrderNumber;
    private String body;
    private String subject;
    private String totalprice;
    private boolean isOnlyHotel = false;
    private boolean isOnlyQY = false;
    //    private MyOrderDetailFragment hotel;
    private boolean isQRCode;

    /**
     * 2018年3月26日
     * 添加此项内容用于订单状态发生变化后及时修改列表
     */
    private int tab_index;//当前商品所在列表
    private int position;//当前商品在列表的位置
    private String oResult;//订单状态
    private String sResult;//物流状态
    private String pResult;//支付状态
    public static final int RESULT_ORDER_STATUS_CHANGE = 123;
    public static final int RESULT_QRCODE_STATUS_CHANGE = 124;

    private boolean isQuanyan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.order_detail);
        initSystemBar();
        setSystemBarWhite();
        handler = new Handler(hCallBack);
        lDialog = new LoadingDialog(this);
//        initTitle("订单详情");
        initIntentMsg(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ll_proParent.removeAllViews();
        initIntentMsg(intent);
    }

    private void initTitle(String string) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(string);

    }

    private void initIntentMsg(Intent intent) {
        Bundle bundle = intent.getExtras();
        orderBean = (HashMap<String, Object>) bundle.get(KEY_ORDER_ITEM);
        outOrderNumber = bundle.getString("orderNumber");
        tab_index = bundle.getInt("tab_index");
        position = bundle.getInt("position");
        isQRCode = bundle.getBoolean("isQRCode", false);
//		outOrderNumber = "201703281556687";//test
        if (orderBean == null) {
            isToComplete = false;
            if (TextUtils.isEmpty(outOrderNumber)) {
                jsShowMsg("订单号不能为空");
                finish();
                return;
            }
            showLoadDialog();
            new MyOrderData(this, outOrderNumber) {
                public void getDataSuccess() {
                    orderBean = getItemOrder();
                    if (orderBean == null || orderBean.isEmpty()) {
                        jsShowMsg("订单数据有误");
                        finish();
                        return;
                    }
                    initView();
                    hideLoadDialog();
                }
            };
        } else {
            initView();
        }
    }

    private boolean isToComplete = false;

    public void toOrderComplete(String orderNum, String payWayTag, String totalPrice, boolean isPayed, String payTime) {
        boolean isBooking = false;
        Intent i = new Intent(context, MyOrderCompleteActivity.class);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_NUMBER, orderNum);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_PAYWAY, payWayTag);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_PRICE, totalPrice);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_ISPAYED, isPayed);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_BOOKING, isBooking);
        i.putExtra(MyOrderCompleteActivity.KEY_ORDER_PAYTIME, payTime);
        startActivity(i);
    }

    @Override
    protected void findViewById() {
        tv_orderNum = (TextView) findViewById(R.id.tv_order_num);
        tv_payWayInfo = (TextView) findViewById(R.id.tv_order_payway);
        tv_orderCreateTime = (TextView) findViewById(R.id.tv_order_createTime);
        tv_orderState = (TextView) findViewById(R.id.tv_order_trade_state);
        tv_orderStateTips = (TextView) findViewById(R.id.tv_order_trade_state_tips);
        tv_help = (TextView) findViewById(R.id.tv_help);
        tv_order_count_price = (TextView) findViewById(R.id.tv_order_count_price);


        ll_proParent = (LinearLayout) findViewById(R.id.ll_orderDetail_proParent);
//        b_copy_orderNum = (Button) findViewById(R.id.b_copy_orderNum);
        b_order_opera1 = (Button) findViewById(R.id.b_order_opera1);
        b_order_opera2 = (Button) findViewById(R.id.b_order_opera2);
        ll_hotel_date = (LinearLayout) findViewById(R.id.ll_hotel_date);
        tv_order_arrive_time = (TextView) findViewById(R.id.tv_order_arrive_time);
        tv_order_leave_time = (TextView) findViewById(R.id.tv_order_leave_time);

        findViewById(R.id.b_title_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 收货地址控件
     *
     * @param id
     */
    private void initRecView(int id) {
        View v = findViewById(id);
        v.setVisibility(View.VISIBLE);
        tv_recAddress = (TextView) v.findViewById(R.id.tv_payCenter_recAddress);
        tv_recMobile = (TextView) v.findViewById(R.id.tv_payCenter_recMobile);
        tv_recName = (TextView) v.findViewById(R.id.tv_payCenter_recName);

    }


    /**
     * 初始化
     */
    @Override
    protected void initView() {
        setFitsSystemWindows(true);
        setSystemBarWhite();
        findViewById();

        Logger.e(TAG, orderBean.toString());
        if (alipayUtil == null) {
            alipayUtil = new AlipayUtil(context);
        }
        String recAddress = orderBean.get(MyOrderData.HEAD_P_ADDRESS).toString();
        String recMobile = orderBean.get(MyOrderData.HEAD_P_MOBILE).toString();
        String recName = orderBean.get(MyOrderData.HEAD_P_NAME).toString();
        this.outOrderNumber = orderBean.get(MyOrderData.ORDER_ORDERNUMBER_KEY).toString();
        String orderCreate = orderBean.get(MyOrderData.ORDER_CREATETIME_KEY).toString();
        String yunFei = orderBean.get(MyOrderData.ORDER_YUNFEI_KEY).toString();
        this.totalprice = orderBean.get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();
        String paymentName = orderBean.get(MyOrderData.ORDER_PAYMENTNAME_KEY).toString();
        oResult = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
        sResult = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
        pResult = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();

        Object dispatchTime1 = orderBean.get("DispatchTime");
        String dispatchTime = "";
        if (dispatchTime1 != null) {
            dispatchTime = DateTimeUtils.formatDateStr(dispatchTime1.toString().replace("/", "."), "yyyy.MM.dd HH:mm");
        }
        final String shipCode = orderBean.get(MyOrderData.ORDER_LOGISTCODE_KEY).toString();// 快递公司编码
        final String shipName = orderBean.get(MyOrderData.ORDER_DISPATMODENAME_KEY).toString();
        final String shipNumber = orderBean.get(MyOrderData.ORDER_SHIPNUMBER_KEY).toString();// 快递单号
        String orderType = orderBean.get(MyOrderData.ORDER_TYPE_KEY).toString();
        ArrayList<HashMap<String, String>> items = (ArrayList<HashMap<String, String>>) orderBean.get(MyOrderData.ORDER_ITEMSET_KEY);

//        String supplierId = items.get(0).get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY).toString();//供应商id
        initPayDialog();
        isOnlyHotel = orderType.equals("1");
        yunFei = TextUtils.isEmpty(yunFei) ? "0.00" : CommonTools.formatZero2Str(Double.parseDouble(yunFei));
        /**
         * 门票以及普通订单
         */

        if (TextUtils.isEmpty(recAddress)) {
            //收货地址为空，是温泉门票??
            initRecView(R.id.contact);
            tv_recName.setText(recName);
            tv_recAddress.setVisibility(View.GONE);
        } else {
            //普通订单，显示收货地址
            initRecView(R.id.contact);
            tv_recAddress.setText(recAddress);
            tv_recName.setText(recName);
        }
        //下单时间
        tv_recMobile.setText(recMobile);
        tv_orderCreateTime.setText("下单时间：" + orderCreate.replace("/", "."));

        //订单号
        String tagOrderNum = getString(R.string.pay_orderNum_tips);
        tv_orderNum.setText(tagOrderNum + outOrderNumber);
        /**
         * 如果是酒店订单，显示入住以及离开时间
         */
        if (isOnlyHotel) {
            //如果是酒店订单，打开MyOrderDetailFragment
//            hotel = new MyOrderDetailFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
//            beginTransaction.add(R.id.container, hotel);
//            beginTransaction.addToBackStack(null);
//            beginTransaction.commit();
//            hotel.initOrderHead(totalprice, recName, recMobile, outOrderNumber, paymentName, orderCreate);


            ll_hotel_date.setVisibility(View.VISIBLE);
            String arriveTime = items.get(0).get("other2");
            String leaveTime = items.get(0).get("other1");
            if (TextUtils.isEmpty(arriveTime)) {
                tv_order_arrive_time.setText("--");
            } else {
                tv_order_arrive_time.setText(leaveTime);
            }
            if (TextUtils.isEmpty(leaveTime)) {
                tv_order_leave_time.setText("--");
            } else {
                tv_order_leave_time.setText(arriveTime);
            }
            tv_recAddress.setVisibility(View.GONE);
        }


//            if (!TextUtils.isEmpty(paymentName)) {
//                tv_payWay.setVisibility(View.VISIBLE);
//
//                tv_payWay.setText("支付方式：" + paymentName);
//            }

//            final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//            b_copy_orderNum.setOnClickListener(new View.OnClickListener() {
//                @SuppressWarnings("deprecation")
//                public void onClick(View v) {
//                    cm.setText(outOrderNumber);
//                    jsShowMsg("复制成功");
//                }
//            });
        b_order_opera1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String tag = b_order_opera1.getText().toString();
                switch (tag) {
                    case BTN_LEFT_DRAWBACK:
//                            MyOrderData.toPayBackActivity(context, outOrderNumber, getUid(), totalprice);
                        MyOrderData.toPayBackActivityForResult(MyOrderDetail.this, outOrderNumber, getUid(), totalprice, position, REQUEST_SHOW_QRCODE);
                        break;
                    case BTN_LEFT_CANCEL:
                        askDialog(tag, "取消订单", "注意： 订单取消后无法找回");

                        break;
                    case BTN_LEFT_CHECKLOGI:
                        Intent intent = new Intent(context, MyOrderShipmentActivity.class);
                        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_CODE, shipCode);
                        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER, shipNumber);
                        intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NAME, shipName);
                        context.startActivity(intent);
                        break;
                    case BTN_LEFT_PAY:
                        //门票支付
//                        selectPayWay();
                        pay(outOrderNumber, true, isQuanyan);
                        break;
                }
            }
        });
        /**在新版本app中，由于按钮中的文字添加上了价格，所以只能通过判断是否包含   支付  来判断*/
        b_order_opera2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String tag = b_order_opera2.getText().toString();
                if (tag.contains("支付")) {
//                    selectPayWay();
                    isToComplete = false;
                    pay(outOrderNumber, true, isQuanyan);

                } else {
                    switch (tag) {
                        case BTN_RIGHT_PAY:
//                            selectPayWay();
                            pay(outOrderNumber, true, isQuanyan);
                            break;
                        case BTN_RIGHT_TAKEOVER:
                            askDialog(tag, "确定收货", "请确定收到货物才进行此操作");
                            break;
                        case BTN_RIGHT_COMMENT:
                            List<HashMap<String, String>> items = (List<HashMap<String, String>>) orderBean.get(MyOrderData.ORDER_ITEMSET_KEY);
                            String guid = items.get(0).get(MyOrderData.ORDER_ITEM_GUID_KEY);
                            String agentID = orderBean.get(MyOrderData.ORDER_AGENTID_KEY).toString();
                            MyOrderData.startProductCommentAct(context, outOrderNumber, guid, agentID);
                            break;
                        case BTN_RIGHT_QRCODE:
                            //查看券码
                            Intent intent = new Intent(context, QRCodeDetailActivity.class);
                            intent.putExtra("orderNumber", outOrderNumber);
                            startActivityForResult(intent, REQUEST_SHOW_QRCODE);
                            break;
                    }
                }
            }
        });

        initItems(items, yunFei, this.totalprice);
        initStateView(oResult, sResult, pResult, paymentName, totalprice, dispatchTime);

        /**已支付的情况下提示*/
        switch (pResult) {
            case "2":
                if (paymentName.contains(PAY_WAY_ALI)) {
                    tv_payWayInfo.setText(PAY_INFO_ALI_PAY);
                } else if (paymentName.contains(PAY_WAY_WECHAT)) {
                    tv_payWayInfo.setText(PAY_INFO_WECHAT_PAY);
                } else if (paymentName.contains(PAY_WAY_ACCOUNT)) {
                    tv_payWayInfo.setText(PAY_INFO_ACCOUNT_PAY);
                } else if (paymentName.equals(PAY_WAY_ARRIVE)) {
                    tv_payWayInfo.setText(PAY_INFO_ARRIVE_PAY);
                }
                break;
            case "0":
                if (isQuanyan) {
                    tv_payWayInfo.setText(PAY_INFO_NO_PAY_QUANYAN);
                } else {
                    tv_payWayInfo.setText(PAY_INFO_NO_PAY);
                }
                break;
            default:
                tv_payWayInfo.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 显示二维码选项
     */
//    private void showQRCode() {
//        fl_qr_code = $(R.id.fl_qr_code);
//        iv_qr_code = $(R.id.iv_qr_code);
//        fl_qr_code.setVisibility(View.VISIBLE);
//        fl_qr_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isClickMoreTimesShortTime()) {
//                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
//                    intent.putExtra("orderNumber", outOrderNumber);
//                    startActivityForResult(intent, REQUEST_SHOW_QRCODE);
//                }
//            }
//        });
//        iv_qr_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isClickMoreTimesShortTime()) {
//                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
//                    intent.putExtra("orderNumber", outOrderNumber);
//                    startActivityForResult(intent, REQUEST_SHOW_QRCODE);
//                }
//            }
//        });
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Dialog askDialog;

    /**
     * 弹窗确认是否取消订单对话框
     *
     * @param tag
     * @param title
     * @param content
     */
    private void askDialog(final String tag, String title, String content) {
        askDialog = MyDialog.getDefineDialog(context, title, content);
        final Button b_cancel = (Button) askDialog.findViewById(R.id.b_dialog_left);
        final Button b_sure = (Button) askDialog.findViewById(R.id.b_dialog_right);
        b_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                askDialog.dismiss();
            }
        });
        b_sure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                b_sure.setText("正在取消...");
                b_sure.setEnabled(false);
                if (tag.equals(BTN_LEFT_CANCEL)) {
                    new Thread(cancelOrderRun).start();
                } else if (tag.equals(BTN_RIGHT_TAKEOVER)) {
                    new Thread(sureTakeGoods).start();
                }
            }
        });
        askDialog.show();
    }

    public final int WHAT_CANCEL_ORDER_SUCCESS = 2;
    public final int WHAT_CANCEL_ORDER_FAIL = -2;
    public final int WHAT_TAKE_GOODS_SUCCESS = 10;
    public final int WHAT_TAKE_GOODS_FAIL = -10;
    //    发起取消订单请求
    public Runnable cancelOrderRun = new Runnable() {
        public void run() {
            if (AppManager.getInstance().postCancelOrder(outOrderNumber)) {
                sendMessage(WHAT_CANCEL_ORDER_SUCCESS, null);
            } else {
                sendMessage(WHAT_CANCEL_ORDER_FAIL, null);
            }
        }
    };
    //    发起确认收货请求
    public Runnable sureTakeGoods = new Runnable() {
        public void run() {
            if (AppManager.getInstance().postFinishOrder(outOrderNumber)) {
                sendMessage(WHAT_TAKE_GOODS_SUCCESS, null);// 表示确定收货成功
            } else {
                sendMessage(WHAT_TAKE_GOODS_FAIL, null);
            }
        }
    };

    /**
     * 支付
     */
    private String payWayTag = "";

    private void toPay(String payWay) {
        if (payWay.equals(getString(R.string.payWay_account_key))) {
            payWayTag = getString(R.string.payWay_account_tips);
            dialog_psw.show();
        } else if (payWay.equals(getString(R.string.payWay_alipay_key))) {
            payWayTag = getString(R.string.payWay_alipay_tips);
            new Thread(aliPayToPay).start();
        } else if (payWay.equals(getString(R.string.payWay_arrivedPay_key))) {
            payWayTag = getString(R.string.payWay_arrivedPay_tips);
            payWayDialog.dismiss();
            if (!isToComplete)
                jsShowMsg("您选择了到付");
            finish();
        } else if (payWay.equals(getString(R.string.payWay_wechatPay_key))) {
            payWayTag = getString(R.string.payWay_wechatPay_tips);
            wechatToPay(outOrderNumber);
        }
    }

    private Dialog payWayDialog = null;
    private boolean isCanUserOrderPayWay = true;

    /**
     * 选择支付方式
     */
    protected void selectPayWay() {
        if (payWayDialog == null) {
            payWayDialog = MyDialog.getDefineDialog(context, R.layout.dialog_payway);
            ViewGroup rg = (ViewGroup) payWayDialog.findViewById(R.id.rg_payWay_group);
            int len = rg.getChildCount();
            if (isOnlyQY) {
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
                    RadioButton btn = (RadioButton) v;
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String payWay = v.getTag().toString();
                            toPay(payWay);
                        }
                    });
                }
            }
        }
        payWayDialog.show();
    }

    /**
     * 商品信息
     *
     * @param itemSet   商品信息
     * @param yunFei    运费
     * @param shouldPay 应支付金额
     */
    private void initItems(ArrayList<HashMap<String, String>> itemSet, String yunFei, String shouldPay) {
        this.body = "";
        this.pricedata = "";
        int qyCount = 0;
        double ttPrice = 0;
        int joinCount = 0;
        int totalCount = 0;//订单总数量
        String phoneAndShopName = "";
        final ArrayList<String> phoneCalls = new ArrayList<>();
        for (HashMap<String, String> item : itemSet) {
            final String agentID = item.get(MyOrderData.ORDER_ITEM_AGENTID_KEY);
            final String buyNum = item.get(MyOrderData.ORDER_ITEM_BUYNUMBER_KEY);
            final String createUser = item.get(MyOrderData.ORDER_ITEM_CREATEUSER);
            final String guid = item.get(MyOrderData.ORDER_ITEM_GUID_KEY);
            final String image = item.get(MyOrderData.ORDER_ITEM_IMAGE_KEY);
            final String tJoinCount = item.get(MyOrderData.ORDER_ITEM_JOINCOUNT_KEY);
            final String attrParam = item.get(MyOrderData.ORDER_ITEM_ATTRIBUTE);
            final String marketPrice = item.get(MyOrderData.ORDER_ITEM_MARKEPRICE_KEY);
            String agentMobile = item.get(MyOrderData.ORDER_ITEM_MOBILE_KEY);
            final String proName = item.get(MyOrderData.ORDER_ITEM_NAME_KEY);
            final String price = item.get(MyOrderData.ORDER_ITEM_PRICE_KEY);
            String shopName = item.get(MyOrderData.ORDER_ITEM_SHOPNAME_KEY);

            final String arrTime = item.get(MyOrderData.ORDER_ITEM_ARRIVE_TIME);
            final String leaveTime = item.get(MyOrderData.ORDER_ITEM_LEAVE_TIME);
            final String supID = item.get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY);

            isQuanyan = supID.equals(Constants.QY_SUPPLIERID);

            final String discountBuyNum = item.get(MyOrderData.ORDER_ITEM_DISCOUNT_BUY_NUM);//优惠条件
            final String discountPrice = item.get(MyOrderData.ORDER_ITEM_DISCOUNT_PRICE);//优惠幅度

            final String bannerUrl = getImgUrl(image, agentID, supID);
            this.payTime = arrTime;

            try {
                int tBuyNum = Integer.parseInt(buyNum);

                int sum = MyOrderData.getRealCount(tBuyNum, tJoinCount);
//				int song = tBuyNum - sum;
//				buyNum = String.valueOf(sum);
                totalCount += sum;
                double tPrice = Double.parseDouble(price);
                double singleSum = MathUtils.multiply(sum, tPrice).doubleValue();
                this.pricedata += createUser + "," + singleSum + "|";

                ttPrice = MathUtils.add(ttPrice, MathUtils.multiply(tBuyNum, tPrice).doubleValue());
                joinCount = Integer.parseInt(tJoinCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(shopName)) {
                shopName = "醇康";
            }

            if (TextUtils.isEmpty(agentMobile)) {
                agentMobile = Constants.CK_PHONE;
            }
            this.body += proName + ",";
            if (!TextUtils.isEmpty(agentID))
                isCanUserOrderPayWay = false;
            //泉眼商品
            if (supID.equals(Constants.QY_SUPPLIERID)) {
                qyCount++;
            }
            if (!phoneCalls.contains(agentMobile)) phoneCalls.add(agentMobile);

            //拼接联系客服电话
            phoneAndShopName += shopName + "|" + agentMobile + ",";
//            if (!phoneCalls.contains(agentMobile)) phoneCalls.add(agentMobile);


//            if (isOnlyHotel) {
//                hotel.initOrderBody(bannerUrl, proName, buyNum, arrTime, leaveTime, agentMobile);
//
//            } else {
            //initView------------------------
            View v = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_item, null);
            TextView tv_shopName = (TextView) v.findViewById(R.id.tv_shopName);
            tv_shopName.setVisibility(View.GONE);
            View proMsg = v.findViewById(R.id.item_proMsg);
            ImageView iv_proImg = (ImageView) proMsg.findViewById(R.id.iv_order_produce_logo);
            TextView tv_proTitle = (TextView) proMsg.findViewById(R.id.tv_order_produce_title);
            TextView tv_attrs = (TextView) v.findViewById(R.id.tv_order_produce_dec);
            TextView tv_proPrice = (TextView) proMsg.findViewById(R.id.tv_order_produce_price);
            TextView tv_order_1 = (TextView) proMsg.findViewById(R.id.tv_order_1);
            TextView tv_order_2 = (TextView) proMsg.findViewById(R.id.tv_order_2);
            TextView tv_order_3 = (TextView) proMsg.findViewById(R.id.tv_order_3);
            TextView tv_discountPrice = (TextView) proMsg.findViewById(R.id.tv_order_discount_price);
            TextView tv_marketPrice = (TextView) proMsg.findViewById(R.id.tv_order_proMarketPrice);
            TextView tv_num = (TextView) proMsg.findViewById(R.id.tv_order_produce_num);
            TextView tv_item_favourable_tip = (TextView) proMsg.findViewById(R.id.tv_item_favourable_tip);
            final String mobile = agentMobile;
            proMsg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (isQuanyan) {
                        new ToQuanyanProductPage().execute(guid);
                    } else {
                        startToProDetail(guid, proName, image, "", agentID, supID, price, mobile);
                    }
                }
            });

            if (joinCount > 0) {
                TextView joinView = (TextView) v.findViewById(R.id.item_joinCount);
                joinView.setVisibility(View.VISIBLE);
                joinView.setText("买" + joinCount + "赠1");
            }

//                if (!TextUtils.isEmpty(arrTime)) {
//                    TextView arr = (TextView) v.findViewById(R.id.item_arriveTime);
//                    arr.setVisibility(View.VISIBLE);
//                    arr.setText("到店时间：" + arrTime);
//                }
//                if (!TextUtils.isEmpty(leaveTime)) {
//                    TextView lea = (TextView) v.findViewById(R.id.item_leaveTime);
//                    lea.setVisibility(View.VISIBLE);
//                    lea.setText("离店时间：" + leaveTime);
//                }
            String attrStr = getHandleAttrs(attrParam);
            if (!TextUtils.isEmpty(attrStr)) {
                tv_attrs.setVisibility(View.VISIBLE);
                tv_attrs.setText(attrStr);
            }
            tv_num.setText("x" + buyNum);
//                tv_shopName.setText(shopName);
            ImageLoader.getInstance().displayImage(bannerUrl, iv_proImg);
            tv_proTitle.setText(proName);

            if (TextUtils.isEmpty(discountPrice)) {
                //非后台拿货
                tv_order_2.setVisibility(View.GONE);
                tv_discountPrice.setText("￥" + price);
                tv_proPrice.setVisibility(View.GONE);
            } else {
                //后台拿货
                tv_item_favourable_tip.setVisibility(View.VISIBLE);
                if (Integer.parseInt(buyNum) >= Integer.parseInt(discountBuyNum)) {
                    //达到优惠条件，显示优惠价，并给拿货价加上删除线
                    tv_discountPrice.setText("￥" + price);
                    tv_proPrice.setText("￥" + CommonTools.formatZero2Str(Double.parseDouble(price) + Double.parseDouble(discountPrice)));
                    tv_proPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    tv_proPrice.getPaint().setAntiAlias(true);
                } else {
                    //未达到优惠条件,显示拿货价，
                    tv_discountPrice.setVisibility(View.GONE);
                    tv_order_1.setVisibility(View.GONE);
                    tv_order_2.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                    tv_proPrice.setText("￥" + price);
                }
            }
            /**
             * 隐藏，隐藏，隐藏
             */
            tv_order_1.setVisibility(View.GONE);
            tv_order_2.setVisibility(View.GONE);
            tv_order_3.setVisibility(View.GONE);
            tv_marketPrice.setText("￥" + marketPrice);
            tv_marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tv_marketPrice.getPaint().setAntiAlias(true);

            ll_proParent.addView(v);
//            }
        }

        String unSignFomat = "共%s件商品，共￥%.2f元（含运费%.2f）";
        double parseDouble = Double.parseDouble(shouldPay);
        String format = String.format(unSignFomat, totalCount, parseDouble, Double.parseDouble(yunFei));
        SpannableStringBuilder stringBuilder = setStyleForUnSignNum(format, parseDouble);
        //总数量、总价格、运费等信息  共998件商品，共￥12455504.00元（含运费0.00）
        tv_order_count_price.setText(stringBuilder);

        phoneAndShopName = phoneAndShopName.substring(0, phoneAndShopName.length() - 1);

        //暂时使用旧方案
        tv_help.setOnClickListener(new View.OnClickListener() {
            Dialog dialog;

            public void onClick(View v) {
                if (phoneCalls.size() > 1) {
                    if (dialog == null) {
                        dialog = MyDialog.getDefineDialog(context, R.layout.dialog_list_phone);
                        ListView lv = (ListView) dialog.findViewById(R.id.content);
                        ArrayAdapter<String> ada = new ArrayAdapter<>(context, R.layout.item_textview_phone, phoneCalls);
                        lv.setAdapter(ada);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                dial(phoneCalls.get(position));
                            }
                        });
                    }
                    dialog.show();
                } else {
                    dial(phoneCalls.get(0));
                }
            }
        });

//        setContactKF(phoneAndShopName);
        /*
         * 2018年4月16日
         * 这部分内容改版删除
         */
//        if (!isOnlyHotel) {
//            this.isOnlyQY = qyCount == itemSet.size();
//            this.subject = TextUtils.isEmpty(body) ? "" : body.substring(0, body.length() - 1);
//            this.pricedata = TextUtils.isEmpty(pricedata) ? "" : pricedata.substring(0, pricedata.length() - 1);
//
//            View v = LayoutInflater.from(this).inflate(R.layout.order_detail_prosummsg, null);
//            TextView yf = (TextView) v.findViewById(R.id.yunfei);
//            TextView sp = (TextView) v.findViewById(R.id.should_pay);
//            TextView tp = (TextView) v.findViewById(R.id.total_price);
//            yf.setText("￥" + yunFei);
//            sp.setText("￥" + shouldPay);
//            tp.setText("￥" + formatZero2Str(ttPrice));
////		联系商家
//            v.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
//                Dialog dialog;
//
//                public void onClick(View v) {
//                    if (phoneCalls.size() > 1) {
//                        if (dialog == null) {
//                            dialog = MyDialog.getDefineDialog(context, R.layout.dialog_list_phone);
//                            ListView lv = (ListView) dialog.findViewById(R.id.content);
//                            ArrayAdapter<String> ada = new ArrayAdapter<>(context, R.layout.item_textview_phone, phoneCalls);
//                            lv.setAdapter(ada);
//                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                public void onItemClick(AdapterView<?> parent,
//                                                        View view, int position, long id) {
//                                    dial(phoneCalls.get(position));
//                                }
//                            });
//                        }
//                        dialog.show();
//                    } else {
//                        dial(phoneCalls.get(0));
//                    }
//                }
//            });
//            ll_proParent.addView(v);
//        }
    }

    public SpannableStringBuilder setStyleForUnSignNum(String text, double num) {
        SpannableStringBuilder unSignNumBuilder = new SpannableStringBuilder(text);
        int start = text.indexOf("" + num);
        int end = start + String.valueOf(num).length() + 1;
        unSignNumBuilder.setSpan(new RelativeSizeSpan(1.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        unSignNumBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#555555")), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return unSignNumBuilder;
    }

    /**
     * 联系客服
     *
     * @param phoneAndShopName TODO 联系电话列表
     */
    public void setContactKF(final String phoneAndShopName) {
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] split = phoneAndShopName.split(",");
                if (split.length <= 1) {
                    String[] split1 = split[0].split("|");

                } else {

                }
            }
        });
    }

    public void callPhone(String shopName, String phoneNum) {

    }

    private final String BTN_LEFT_CHECKLOGI = "查看物流";
    private final String BTN_LEFT_CANCEL = "取消订单";
    private final String BTN_LEFT_DRAWBACK = "申请退款";
    private final String BTN_LEFT_PAY = "去付款";
    private final String BTN_RIGHT_PAY = "支付";
    private final String BTN_RIGHT_TAKEOVER = "确定收货";
    private final String BTN_RIGHT_COMMENT = "评价";
    private final String BTN_RIGHT_QRCODE = "查看券码";
    private final String STATE_NOPAY = "待支付";
    private final String STATE_PAYED = "已付款";


    /**
     * 设置订单状态信息
     *
     * @param oResult
     * @param sResult
     * @param pResult
     * @param paymentName
     * @param totalprice
     */
    private void initStateView(String oResult, String sResult, String pResult, String paymentName, String totalprice, String ticketTime) {

        if (isQuanyan) {
            if (isOnlyHotel) {
                /**
                 * 酒店客房
                 */
                if (pResult.equals("0")) {// 付款状态为 未付款
                    setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY, ORDER_STATUS_NO_PAY_TIPS_HOTEL, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + totalprice);

                } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
                    setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_PAYED_HOTEL, ORDER_STATUS_PAYED_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);


                } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
                    setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_FINISH_HOTEL, ORDER_STATUS_FINISH_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);

                }
                //订单已完成
                if (oResult.equals("5")) {
                    setViewValue(R.drawable.ic_state_success, ORDER_STATUS_FINISH_HOTEL, ORDER_STATUS_FINISH_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);
                }

            } else {
                /**
                 * 门票等泉眼商品
                 */
                if (pResult.equals("0")) {// 付款状态为 未付款
                    if (paymentName.contains("到付")) {
                        setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_ARRAY_PAY_TICKET, ORDER_STATUS_ARRAY_PAYTIPS_TICKET, BTN_LEFT_PAY, BTN_RIGHT_QRCODE);
                    } else {
                        setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY, ORDER_STATUS_NO_PAY_TIPS_TICKET, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + totalprice);
                    }

                } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
                    setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_NO_USE, ORDER_STATUS_NO_USE_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);


                } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
                    setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_USED_TICKET, ORDER_STATUS_USED_TIPS_TICKET + ticketTime, null, BTN_RIGHT_QRCODE);

                }

                //订单已完成
                if (oResult.equals("5")) {
                    setViewValue(R.drawable.ic_state_success, ORDER_STATUS_USED_TICKET, ORDER_STATUS_USED_TIPS_TICKET + ticketTime, null, BTN_RIGHT_QRCODE);
                }

            }
        } else {
            /**
             * 普通商品
             */

            if (pResult.equals("0")) {// 付款状态为 未付款
                setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY_GOODS, ORDER_STATUS_NO_PAY_TIPS_GOODS, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + totalprice);

            } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
                setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_PAYED_GOODS, ORDER_STATUS_PAYED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_LEFT_DRAWBACK);


            } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
                setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_SENDED_GOODS, ORDER_STATUS_SENDED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_RIGHT_TAKEOVER);

            }
            //订单已完成
            if (oResult.equals("5")) {
                setViewValue(R.drawable.ic_state_success, ORDER_STATUS_FINISHED_GOODS, ORDER_STATUS_FINISHED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_LEFT_DRAWBACK);
            }

        }


        switch (oResult) {
            case "2": //订单已取消
                setViewValue(0, ORDER_STATUS_CANCELED, ORDER_STATUS_OTHER_TIPS, null, null);

                break;
            case "3":
                setViewValue(0, ORDER_STATUS_INVALID, ORDER_STATUS_OTHER_TIPS, null, null);

                break;
            case "4":
                setViewValue(0, ORDER_STATUS_RETURNING_GOODS, ORDER_STATUS_OTHER_TIPS, null, null);

                break;
//            case "5": // 已完成订单
//                setViewValue(R.drawable.ic_state_success, ORDER_STATUS_FINISH_HOTEL, ORDER_STATUS_FINISH_HOTEL_TIPS, BTN_LEFT_CHECKLOGI, BTN_RIGHT_COMMENT);
//                break;
        }
        if (sResult.equals("4")) {
            setViewValue(0, ORDER_STATUS_RETURNING_GOODS, ORDER_STATUS_RETURN_TIPS_GOODS, BTN_LEFT_CHECKLOGI, null);
        }
        if (pResult.equals("3")) {// 表示已退款
            setViewValue(0, ORDER_STATUS_RETURNED_GOODS, ORDER_STATUS_RETURN_TIPS_GOODS, null, null);
        }

//        if (pResult.equals("0")) {// 付款状态为 未付款
//            setViewValue(R.drawable.ic_state_nopay, STATE_NOPAY, BTN_LEFT_CANCEL, BTN_RIGHT_PAY+totalprice);
//
//        } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
//            setViewValue(R.drawable.ic_state_paied, STATE_PAYED, BTN_LEFT_DRAWBACK, null);
//
//
//        } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
//            setViewValue(R.drawable.ic_state_sended, "已发货", BTN_LEFT_CHECKLOGI, BTN_RIGHT_TAKEOVER);
//
//        }
//        switch (oResult) {
//            case "2": //订单已取消
//                setViewValue(0, "已取消", null, null);
//
//                break;
//            case "3":
//                setViewValue(0, "已作废", null, null);
//
//                break;
//            case "4":
//                setViewValue(0, "请退货", null, null);
//
//                break;
//            case "5": // 已完成订单
//                setViewValue(R.drawable.ic_state_success, "已完成", BTN_LEFT_CHECKLOGI, BTN_RIGHT_COMMENT);
//                break;
//        }
//        if (sResult.equals("4")) {
//            setViewValue(0, "退款中", null, null);
//        }
//        if (pResult.equals("3")) {// 表示已退款
//            setViewValue(0, "已退款", null, null);
//        }
    }

    /**
     * 门票入园时间
     *
     * @param ticketTime
     */
    public void setTicketTime(String ticketTime) {

    }

    /**
     * 状态设值
     *
     * @param drawableId
     * @param state
     * @param leftStr
     * @param rightStr
     */
    private void setViewValue(int drawableId, String state, String stateTips, String leftStr, String rightStr) {
//        if (isOnlyHotel) {
//            switch (state) {
//                case STATE_NOPAY:
//                    hotel.setPayState(1);
//                    break;
//                case STATE_PAYED:
//                    hotel.setPayState(2);
//                    break;
//                default:
//                    hotel.setPayState(0);
//                    break;
//            }
//
//        } else {
//			Drawable stateDrawable = drawableId != 0 ? getResources().getDrawable(drawableId) : null;
//			if(stateDrawable != null)
//				stateDrawable.setBounds(0, 0, stateDrawable.getIntrinsicWidth(), stateDrawable.getIntrinsicHeight());
//			tv_orderState.setCompoundDrawables(null, null, stateDrawable, null);
        tv_orderState.setText(state);
        if (TextUtils.isEmpty(stateTips)) {
            tv_orderStateTips.setVisibility(View.GONE);
        } else {
            tv_orderStateTips.setText(stateTips);
        }

        boolean isLeftStrEmp = TextUtils.isEmpty(leftStr);
        boolean isRightStrEmp = TextUtils.isEmpty(rightStr);
        int visitLeft = isLeftStrEmp ? View.GONE : View.VISIBLE;
        int visitRight = isRightStrEmp ? View.GONE : View.VISIBLE;
        b_order_opera1.setVisibility(visitLeft);
        b_order_opera2.setVisibility(visitRight);
        if (!isLeftStrEmp) b_order_opera1.setText(leftStr);
        if (!isRightStrEmp) b_order_opera2.setText(rightStr);
//            //如果是已取消订单，那么隐藏查看券码
//            if (fl_qr_code != null && state.equals("已取消")) {
//                fl_qr_code.setVisibility(View.GONE);
//            }
//        }
    }

    protected String getHandleAttrs(String attrParam) {
        StringBuilder sb = new StringBuilder();
        String result = "";
        try {
            if (!TextUtils.isEmpty(attrParam)) {
                String[] item = attrParam.split(";");
                String[] names = item[0].split(",");
                String[] values = item[1].split(",");
                for (int i = 0; i < names.length; i++) {
//                    result += names[i] + ":" + values[i] + "; ";
                    sb.append(names[i]).append(":").append(values[i]).append("; ");
                }
                result = sb.toString();
                result = result.substring(0, result.length() - 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void setItem(View v, String tag, String value) {
        TextView tv_tag, tv_value;
        tv_tag = (TextView) v.findViewById(R.id.tv_tag);
        tv_value = (TextView) v.findViewById(R.id.tv_value);
        tv_tag.setText(tag);
        tv_value.setText(value);
    }

    /**
     * 跳转至商品详情
     *
     * @param guid
     * @param proName
     * @param img
     * @param detail
     * @param agentID
     * @param supplierID
     * @param price
     * @param mobile
     */
    public void startToProDetail(String guid, String proName, String img, String detail, String agentID, String supplierID, String price, String mobile) {
        try {
            double tPrice = Double.parseDouble(price);
            if (tPrice == 1 || tPrice == 0.1) {
                ActivityUtils.toProDetail2(this, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 0);
            } else {
                ActivityUtils.toProDetail2(this, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "startToProDetail: " + e.getMessage());
        }
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

    /**
     * 账号支付
     */
    public void accPay() {
        String uid = getUid();
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

    public Handler.Callback hCallBack = new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            if (msg.what == PaymentCenterActivity.RQF_PAY) {
                String payInfo = msg.obj.toString();
                Result result = new Result(payInfo);
                String code = payInfo.substring(payInfo.indexOf("{") + 1, payInfo.indexOf("}"));
                aliPaySuccess(code, result);

            } else if (msg.what == PaymentCenterHandler.WHAT_WECHAT_PAY) {
                if (msg.arg1 == 0) {
                    successFinish();
                } else if (msg.arg1 == -2) {
                    jsShowMsg("您取消支付了");
                } else {
                    jsShowMsg("支付失败");
                }

            } else if (msg.what == WHAT_TAKE_GOODS_SUCCESS) {
                if (askDialog.isShowing())
                    askDialog.dismiss();
                jsShowMsg("确定收货成功");
                MyOrderData.sureTakeGoods(outOrderNumber);
                setResult(MyOrderParent.REQ_PAY);
                finish();

            } else if (msg.what == WHAT_CANCEL_ORDER_SUCCESS) {
                if (askDialog.isShowing())
                    askDialog.dismiss();
                jsShowMsg("取消订单成功");
                MyOrderData.cancelOrder(outOrderNumber);
                MyOrderActivity.isCurrentPage = false;
                isToComplete = false;
                setResult(MyOrderParent.REQ_PAY);
                finish();
            }
            return true;
        }
    };

    public void aliPaySuccess(String code, Result result) {
        if (code.equals("9000")) {
            successFinish();
        }
    }

    LoadingDialog lDialog = null;

    public void successFinish() {
        isPayed = true;
        if (payWayDialog != null)
            payWayDialog.dismiss();
        hideLoadDialog();
        //jsShowMsg("支付成功");
        if (isQRCode) {
            MyOrderData.payForOrder(outOrderNumber);
        } else {
            MyOrderDataQRCode.payForOrder(outOrderNumber);
        }
        lDialog.show();
        setResult(MyOrderParent.REQ_PAY);
        finish();
    }

    private boolean isPayed = false;
    private String payTime = "";

    @SuppressLint("SimpleDateFormat")
    public void finish() {
        if (isToComplete) {
            if (isPayed)
                payTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            toOrderComplete(outOrderNumber, payWayTag, totalprice, isPayed, payTime);
        }

        super.finish();
    }

    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    private WXPay wxPay;

    public void wechatToPay(String outOrderNumber) {
        if (wxPay == null)
            wxPay = new WXPay(context);
        wxPay.toPay(body.substring(0, body.length() - 1), outOrderNumber + "_" + wxPay.genTimeStamp(), totalprice);
    }

    private Runnable aliPayToPay = new Runnable() {
        private boolean isRunning = false;

        public void run() {
            if (isRunning)
                return;
            isRunning = true;
            String info = alipayUtil.getSignPayOrderInfo(outOrderNumber, subject, body, totalprice);
            PayTask aliPay = new PayTask(MyOrderDetail.this);
            String payStr = aliPay.pay(info);
            sendMessage(PaymentCenterActivity.RQF_PAY, payStr);
            isRunning = false;
        }
    };

    public void sendMessage(int what, String data) {
        if (handler != null) {
            handler.sendMessage(getMessage(what, data));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW_QRCODE) {
            ll_proParent.removeAllViews();
            showLoadDialog();
            if (isQRCode) {
                //从消费码页面进入
                new MyOrderDataQRCode(this, true, outOrderNumber) {
                    public void getDataSuccess() {
                        orderBean = getItemOrder();
                        if (orderBean == null || orderBean.isEmpty()) {
                            jsShowMsg("订单数据有误");
                            finish();
                            return;
                        }
                        String orderStatus = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
                        String shipmentStatus = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
                        String payStatus = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();

                        if (!orderStatus.equals(oResult) || !shipmentStatus.equals(sResult) || !payStatus.equals(sResult)) {
                            Logger.e(TAG, "订单状态发生了变化");
                            /**
                             * 如果订单状态发生了改变，那么应该通知列表进行更新
                             */
                            boolean hasAllChange = false;
                            Logger.e(TAG, "row 1378  消费码 ");
                            //------------------------------全部订单--------------------------------------------
                            for (HashMap<String, Object> orderItem : MyOrderDataQRCode.order_list_no_use) {
                                String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                //更新所有订单列表
                                if (outOrderNumber.equals(order_orderNumber)) {

                                    for (String s : orderItem.keySet()) {
                                        orderItem.put(s, orderBean.get(s));
                                    }
                                    hasAllChange = true;
                                    Logger.e(TAG, "  order_list_no_use change");
                                    MyOrderQRCodeActivity.dataChanges[0] = true;
                                    break;
                                }
                            }
                            if (!hasAllChange)
                                for (HashMap<String, Object> orderItem : MyOrderDataQRCode.order_no_use) {
                                    String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                    //更新所有订单列表
                                    if (outOrderNumber.equals(order_orderNumber)) {

                                        for (String s : orderItem.keySet()) {
                                            orderItem.put(s, orderBean.get(s));
                                        }
                                        Logger.e(TAG, "  order_no_use change");
                                        break;
                                    }
                                }

                            //如果是已完成订单，添加至已完成列表
                            if (orderStatus.equals("5") && payStatus.equals("2") && shipmentStatus.equals("2")) {
                                MyOrderDataQRCode.order_list_complete.add(0, orderBean);
                                MyOrderQRCodeActivity.dataChanges[1] = true;
                            }

                            setResult(RESULT_QRCODE_STATUS_CHANGE);
                        }
                        initView();
                        hideLoadDialog();
                    }
                };
            } else {
                //从订单中心进入
                new MyOrderData(this, true, outOrderNumber) {
                    public void getDataSuccess() {
                        orderBean = getItemOrder();
                        if (orderBean == null || orderBean.isEmpty()) {
                            jsShowMsg("订单数据有误");
                            finish();
                            return;
                        }
                        String orderStatus = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
                        String shipmentStatus = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
                        String payStatus = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();


                        if (!orderStatus.equals(oResult) || !shipmentStatus.equals(sResult) || !payStatus.equals(sResult)) {
                            Logger.e(TAG, "订单状态发生了变化");
                            /**
                             * 如果订单状态发生了改变，那么应该通知列表进行更新
                             */
                            //从订单中心过来
                            Logger.e(TAG, "订单中心 ");
                            boolean hasAllChange = false;
                            //------------------------------全部订单--------------------------------------------
                            for (HashMap<String, Object> orderItem : MyOrderData.order_list_all) {
                                String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                //更新所有订单列表
                                if (outOrderNumber.equals(order_orderNumber)) {
//                                    orderItem.remove("order_PaymentStatus");
//                                    orderItem.put("order_PaymentStatus", payStatus);
//                                    orderItem.remove("order_ShipmentStatus");
//                                    orderItem.put("order_ShipmentStatus", shipmentStatus);
//                                    orderItem.remove(MyOrderData.ORDER_ORDERSTATUS_KEY);
//                                    orderItem.put(MyOrderData.ORDER_ORDERSTATUS_KEY, orderStatus);

                                    for (String s : orderItem.keySet()) {
                                        orderItem.put(s, orderBean.get(s));
                                    }
                                    hasAllChange = true;
                                    Logger.e(TAG, "  order_list_all change");
                                    MyOrderActivity.dataChanges[0] = true;
                                    break;
                                }
                            }
                            if (!hasAllChange)
                                for (HashMap<String, Object> orderItem : MyOrderData.order_all) {
                                    String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                    //更新所有订单列表
                                    if (outOrderNumber.equals(order_orderNumber)) {
//                                        orderItem.remove("order_PaymentStatus");
//                                        orderItem.put("order_PaymentStatus", payStatus);
//                                        orderItem.remove("order_ShipmentStatus");
//                                        orderItem.put("order_ShipmentStatus", shipmentStatus);
//                                        orderItem.remove(MyOrderData.ORDER_ORDERSTATUS_KEY);
//                                        orderItem.put(MyOrderData.ORDER_ORDERSTATUS_KEY, orderStatus);

                                        for (String s : orderItem.keySet()) {
                                            orderItem.put(s, orderBean.get(s));
                                        }
                                        Logger.e(TAG, "  order_all change");
                                        break;
                                    }
                                }
//------------------------------全部订单--------------------------------------------------

//------------------------------未付款订单-------------------------------------------------
                            if (pResult.equals("0")) {//未付款-->已完成订单

                                boolean removeData = removeData(MyOrderData.order_list_noPay, outOrderNumber);
                                if (!removeData)
                                    removeData(MyOrderData.order_noPay, outOrderNumber);
//                            MyOrderNoPayActivity.adapter.deleteData(position);
                                if (removeData)
                                    MyOrderActivity.dataChanges[1] = true;

//------------------------------未付款订单-------------------------------------------------

//------------------------------已付款订单-------------------------------------------------
                            } else if (pResult.equals("2")) {//已付款-->已完成
                                boolean removeData = removeData(MyOrderData.order_list_noSend, outOrderNumber);
                                if (!removeData)
                                    removeData(MyOrderData.order_noSend, outOrderNumber);
                                if (removeData)
                                    MyOrderActivity.dataChanges[2] = true;
                            }

//------------------------------已付款订单-------------------------------------------------

                            //已完成订单，添加到已完成列表
                            if (orderStatus.equals("5") && shipmentStatus.equals("2") && payStatus.equals("2")) {
                                Logger.e(TAG, "row 1011 添加至已完成列表");
//                            MyOrderNoCommentActivity.adapter.addDateItem(0, orderBean);
//                            MyOrderActivity.dataChanges[4] = true;
                            }
                            Logger.e(TAG, "数据更新完毕");
                            setResult(RESULT_ORDER_STATUS_CHANGE);
                            sendDataChangeBroadcast();
                        }
                        initView();
                        hideLoadDialog();
                    }
                };
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean removeData(ArrayList<HashMap<String, Object>> list, String orderNumber) {
        boolean result = false;
        for (HashMap<String, Object> item : list) {
            String number = item.get("order_orderNumber").toString();
            if (number.equals(orderNumber)) {
                list.remove(item);
                result = true;
                break;
            }
        }
        return result;
    }


    boolean isPayLoading;

    public void pay(final String orderNumber, boolean isShowAccountPay, boolean isShowArrivePay) {
        pay(orderNumber, true, true, isShowAccountPay, isShowArrivePay);
    }

    public void pay(final String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay) {
        if (isPayLoading) {
            return;
        }
        isPayLoading = true;
        PayUtils payUtils = new PayUtils((BaseActivity) context, getUid(), orderNumber, isShowAliPay, isShowWechatPay, isShowAccountPay, isShowArrivePay) {
            @Override
            public void paySuccess() {
                isPayLoading = false;
//                if (isQRCode) {
//                    MyOrderDataQRCode.payForOrder(orderNumber);
//                } else {
//                    MyOrderData.payForOrder(orderNumber);
//                }
                successFinish();
            }

            @Override
            public void payFail() {
                isPayLoading = false;
                jsShowMsg("支付失败,请检查");
            }

            @Override
            public void payCancel() {
                isPayLoading = false;
                jsShowMsg("您已取消支付");
            }

            @Override
            public void payError(String message) {
                isPayLoading = false;
                jsShowMsg(message);
            }

            @Override
            public void dismissOption() {
                isPayLoading = false;
            }
        };
    }


    private void sendDataChangeBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
        manager.sendBroadcast(intent);
    }

    /**
     * 跳转至温泉产品页面,1-门票，2-客房
     * ticket_route.html?productCategory=*&guid=*
     */
    class ToQuanyanProductPage extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            String productGuid = param[0];
            if (TextUtils.isEmpty(productGuid)) {
                return "";
            }
            return appManager.postGetProductCategory(productGuid);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (TextUtils.isEmpty(result)) {
                jsShowMsg("商品信息有误");
                return;
            }
            try {
                ProductCategory category = gson.fromJson(result, ProductCategory.class);
                ProductCategory.ValBean bean = category.getVal().get(0);
                String productCategory = bean.getProductCategory();
                String guid = bean.getProductGuid();

                Intent intent = new Intent(context, HtmlActivityNew.class);
                String url = Constants.appWebPageUrl + QUANYAN_PRODUCT_URL + "?productCategory=" + productCategory + "&guid=" + guid;
                intent.putExtra("url", url);
                startActivity(intent);
            } catch (JsonSyntaxException e) {
                jsShowMsg("商品信息有误");
                e.printStackTrace();
            }

        }
    }
}
