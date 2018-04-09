package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.config.Constants;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.AccToPay;
import com.danertu.tools.AlipayUtil;
import com.danertu.tools.AppManager;
import com.danertu.tools.LoadingDialog;
import com.danertu.tools.Logger;
import com.danertu.tools.MathUtils;
import com.danertu.tools.MyDialog;
import com.danertu.tools.Result;
import com.danertu.tools.WXPay;
import com.danertu.widget.CommonTools;
import com.danertu.widget.PayPswDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.danertu.dianping.MyOrderParent.TAB_ALL;
import static com.danertu.dianping.MyOrderParent.TAB_NO_COMMENT;
import static com.danertu.dianping.MyOrderParent.TAB_NO_PAY;
import static com.danertu.dianping.MyOrderParent.TAB_NO_SEND;
import static com.danertu.dianping.MyOrderParent.TAB_NP_RECEIVE;

/**
 * 订单详情页面
 */
public class MyOrderDetail extends BaseActivity {
    //常量
    public static final String KEY_ORDER_STATE = "orderState";
    public static final String KEY_ORDER_ITEM = "orderItem";
    private static final int REQUEST_SHOW_QRCODE = 22;

    //UI
//	private View contentView;
    private TextView tv_recName, tv_recMobile;
    /**
     * 存在 null 的情况
     */
    private TextView tv_recAddress;
    private TextView tv_orderNum, tv_orderCreateTime, tv_orderState;
    private LinearLayout ll_proParent;
    private TextView tv_payWay;
    private Button b_copy_orderNum;
    private Button b_order_opera1;
    private Button b_order_opera2;

    private PayPswDialog dialog_psw;
    private Context context;
    private AlipayUtil alipayUtil;
    public static Handler handler;

    private FrameLayout fl_qr_code;
    private ImageView iv_qr_code;
    //变量
    private HashMap<String, Object> orderBean;
    private String pricedata;
    private String outOrderNumber;
    private String body;
    private String subject;
    private String totalprice;
    private boolean isOnlyQY = false;
    private MyOrderDetailFragment hotel;

    /**
     * 2018年3月26日
     * 添加此项内容用于订单状态发生变化后及时修改列表
     */
    private int tab_index;//当前商品缩所在列表
    private int position;//当前商品在列表的位置
    private String oResult;//订单状态
    private String sResult;//物流状态
    private String pResult;//支付状态
    public static final int RESULT_ORDER_STATUS_CHANGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        this.setContentView(R.layout.order_detail);
        handler = new Handler(hCallBack);
        lDialog = new LoadingDialog(this);
        initTitle("订单详情");
        initIntentMsg();
    }

    private void initTitle(String string) {
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(string);
        findViewById(R.id.b_title_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void initIntentMsg() {
        Bundle bundle = getIntent().getExtras();
        orderBean = (HashMap<String, Object>) bundle.get(KEY_ORDER_ITEM);
        outOrderNumber = bundle.getString("orderNumber");
        tab_index = bundle.getInt("tab_index");
        position = bundle.getInt("position");

//		outOrderNumber = "201703281556687";//test
        if (orderBean == null) {
            isToComplete = true;
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
        findViewById(R.id.ib_toSelectAddress).setVisibility(View.GONE);
        findViewById(R.id.fl_line).setVisibility(View.GONE);
        tv_orderNum = (TextView) findViewById(R.id.tv_order_num);
        tv_payWay = (TextView) findViewById(R.id.tv_order_payway);
        tv_orderCreateTime = (TextView) findViewById(R.id.tv_order_createTime);
        tv_orderState = (TextView) findViewById(R.id.tv_order_trade_state);
        ll_proParent = (LinearLayout) findViewById(R.id.ll_orderDetail_proParent);
        b_copy_orderNum = (Button) findViewById(R.id.b_copy_orderNum);
        b_order_opera1 = (Button) findViewById(R.id.b_order_opera1);
        b_order_opera2 = (Button) findViewById(R.id.b_order_opera2);

    }

    private void initRecView(int id) {
        View v = findViewById(id);
        v.setVisibility(View.VISIBLE);
        if (id != R.id.contact1)
            tv_recAddress = (TextView) v.findViewById(R.id.tv_payCenter_recAddress);
        tv_recMobile = (TextView) v.findViewById(R.id.tv_payCenter_recMobile);
        tv_recName = (TextView) v.findViewById(R.id.tv_payCenter_recName);

    }

    boolean isOnlyHotel = false;

    /**
     * 初始化
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void initView() {
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
        final String shipCode = orderBean.get(MyOrderData.ORDER_LOGISTCODE_KEY).toString();// 快递公司编码
        final String shipName = orderBean.get(MyOrderData.ORDER_DISPATMODENAME_KEY).toString();
        final String shipNumber = orderBean.get(MyOrderData.ORDER_SHIPNUMBER_KEY).toString();// 快递单号
        String orderType = orderBean.get(MyOrderData.ORDER_TYPE_KEY).toString();


        ArrayList<HashMap<String, String>> items = (ArrayList<HashMap<String, String>>) orderBean.get(MyOrderData.ORDER_ITEMSET_KEY);
//        String supplierId = items.get(0).get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY).toString();//供应商id
        initPayDialog();
        isOnlyHotel = orderType.equals("1");

        if (isOnlyHotel) {
            //如果是酒店订单，打开MyOrderDetailFragment
            hotel = new MyOrderDetailFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction beginTransaction = fragmentManager.beginTransaction();
            beginTransaction.add(R.id.container, hotel);
            beginTransaction.addToBackStack(null);
            beginTransaction.commit();
            hotel.initOrderHead(totalprice, recName, recMobile, outOrderNumber, paymentName, orderCreate);
        } else {
            setFitsSystemWindows(true);
            setSystemBarWhite();
            findViewById();

            if (TextUtils.isEmpty(recAddress)) {
                //收货地址为空，是温泉门票??
                initRecView(R.id.contact1);
//                tv_recName.setText("取票人:" + recName);
                tv_recName.setText(recName);
                showQRCode();
            } else {
                initRecView(R.id.contact);
                tv_recAddress.setText(recAddress);
                tv_recName.setText(recName);
            }

            tv_recMobile.setText(recMobile);
            tv_orderCreateTime.setText("创建时间：" + orderCreate.replace("/", "."));

            String tagOrderNum = getString(R.string.pay_orderNum_tips);
            tv_orderNum.setText(tagOrderNum + outOrderNumber);

            if (!TextUtils.isEmpty(paymentName)) {
                tv_payWay.setVisibility(View.VISIBLE);
                tv_payWay.setText("支付方式：" + paymentName);
            }

            final ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            b_copy_orderNum.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    cm.setText(outOrderNumber);
                    jsShowMsg("复制成功");
                }
            });
            b_order_opera1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final String tag = b_order_opera1.getText().toString();
                    switch (tag) {
                        case BTN_LEFT_DRAWBACK:
//                            MyOrderData.toPayBackActivity(context, outOrderNumber, getUid(), totalprice);
                            MyOrderData.toPayBackActivityForResult(MyOrderDetail.this, outOrderNumber, getUid(), totalprice,position,REQUEST_SHOW_QRCODE);

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
                    }
                }
            });
            b_order_opera2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final String tag = b_order_opera2.getText().toString();
                    switch (tag) {
                        case BTN_RIGHT_PAY:
                            selectPayWay();
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
                    }
                }
            });
        }

        initStateView(oResult, sResult, pResult);
        initItems(items, yunFei, this.totalprice);
    }

    /**
     * 显示二维码选项
     */
    private void showQRCode() {
        fl_qr_code = $(R.id.fl_qr_code);
        iv_qr_code = $(R.id.iv_qr_code);
        fl_qr_code.setVisibility(View.VISIBLE);
        fl_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickMoreTimesShortTime()) {
                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", outOrderNumber);
                    startActivityForResult(intent, REQUEST_SHOW_QRCODE);
                }
            }
        });
        iv_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickMoreTimesShortTime()) {
                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", outOrderNumber);
                    startActivityForResult(intent, REQUEST_SHOW_QRCODE);
                }
            }
        });
    }

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

    private void initItems(ArrayList<HashMap<String, String>> itemSet, String yunFei, String shouldPay) {
        this.body = "";
        this.pricedata = "";
        int qyCount = 0;
        double ttPrice = 0;
        int joinCount = 0;
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

            final String discountBuyNum = item.get(MyOrderData.ORDER_ITEM_DISCOUNT_BUY_NUM);//优惠条件
            final String discountPrice = item.get(MyOrderData.ORDER_ITEM_DISCOUNT_PRICE);//优惠幅度

            final String bannerUrl = getImgUrl(image, agentID, supID);
            this.payTime = arrTime;

            try {
                int tBuyNum = Integer.parseInt(buyNum);
                int sum = MyOrderData.getRealCount(tBuyNum, tJoinCount);
//				int song = tBuyNum - sum;
//				buyNum = String.valueOf(sum);

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


            if (isOnlyHotel) {
                hotel.initOrderBody(bannerUrl, proName, buyNum, arrTime, leaveTime, agentMobile);

            } else {
                //initView------------------------
                View v = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_item, null);
                TextView tv_shopName = (TextView) v.findViewById(R.id.tv_shopName);
                View proMsg = v.findViewById(R.id.item_proMsg);
                ImageView iv_proImg = (ImageView) proMsg.findViewById(R.id.iv_order_produce_logo);
                TextView tv_proTitle = (TextView) proMsg.findViewById(R.id.tv_order_produce_title);
                TextView tv_attrs = (TextView) v.findViewById(R.id.tv_order_produce_dec);
                TextView tv_proPrice = (TextView) proMsg.findViewById(R.id.tv_order_produce_price);
                TextView tv_order_1 = (TextView) proMsg.findViewById(R.id.tv_order_1);
                TextView tv_order_2 = (TextView) proMsg.findViewById(R.id.tv_order_2);
                TextView tv_discountPrice = (TextView) proMsg.findViewById(R.id.tv_order_discount_price);
                TextView tv_marketPrice = (TextView) proMsg.findViewById(R.id.tv_order_proMarketPrice);
                TextView tv_num = (TextView) proMsg.findViewById(R.id.tv_order_produce_num);
                TextView tv_item_favourable_tip = (TextView) proMsg.findViewById(R.id.tv_item_favourable_tip);
                final String mobile = agentMobile;
                proMsg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startToProDetail(guid, proName, image, "", agentID, supID, price, mobile);
                    }
                });

                if (joinCount > 0) {
                    TextView joinView = (TextView) v.findViewById(R.id.item_joinCount);
                    joinView.setVisibility(View.VISIBLE);
                    joinView.setText("买" + joinCount + "赠1");
                }
                if (!TextUtils.isEmpty(arrTime)) {
                    TextView arr = (TextView) v.findViewById(R.id.item_arriveTime);
                    arr.setVisibility(View.VISIBLE);
                    arr.setText("到店时间：" + arrTime);
                }
                if (!TextUtils.isEmpty(leaveTime)) {
                    TextView lea = (TextView) v.findViewById(R.id.item_leaveTime);
                    lea.setVisibility(View.VISIBLE);
                    lea.setText("离店时间：" + leaveTime);
                }
                String attrStr = getHandleAttrs(attrParam);
                if (!TextUtils.isEmpty(attrStr)) {
                    tv_attrs.setVisibility(View.VISIBLE);
                    tv_attrs.setText(attrStr);
                }
                tv_num.setText("x" + buyNum);
                tv_shopName.setText(shopName);
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
                tv_marketPrice.setText("￥" + marketPrice);
                tv_marketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                tv_marketPrice.getPaint().setAntiAlias(true);

                ll_proParent.addView(v);
            }
        }
        if (!isOnlyHotel) {
            this.isOnlyQY = qyCount == itemSet.size();
            this.subject = TextUtils.isEmpty(body) ? "" : body.substring(0, body.length() - 1);
            this.pricedata = TextUtils.isEmpty(pricedata) ? "" : pricedata.substring(0, pricedata.length() - 1);

            View v = LayoutInflater.from(this).inflate(R.layout.order_detail_prosummsg, null);
            TextView yf = (TextView) v.findViewById(R.id.yunfei);
            TextView sp = (TextView) v.findViewById(R.id.should_pay);
            TextView tp = (TextView) v.findViewById(R.id.total_price);
            yf.setText("￥" + yunFei);
            sp.setText("￥" + shouldPay);
            tp.setText("￥" + formatZero2Str(ttPrice));
//		联系商家
            v.findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
                Dialog dialog;

                public void onClick(View v) {
                    if (phoneCalls.size() > 1) {
                        if (dialog == null) {
                            dialog = MyDialog.getDefineDialog(context, R.layout.dialog_list_phone);
                            ListView lv = (ListView) dialog.findViewById(R.id.content);
                            ArrayAdapter<String> ada = new ArrayAdapter<>(context, R.layout.item_textview_phone, phoneCalls);
                            lv.setAdapter(ada);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent,
                                                        View view, int position, long id) {
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
            ll_proParent.addView(v);
        }
    }

    private final String BTN_LEFT_CHECKLOGI = "查看物流";
    private final String BTN_LEFT_CANCEL = "取消订单";
    private final String BTN_LEFT_DRAWBACK = "申请退款";
    private final String BTN_RIGHT_PAY = "付款";
    private final String BTN_RIGHT_TAKEOVER = "确定收货";
    private final String BTN_RIGHT_COMMENT = "评价";
    private final String STATE_NOPAY = "待支付";
    private final String STATE_PAYED = "已付款";

    private void initStateView(String oResult, String sResult, String pResult) {
        if (pResult.equals("0")) {// 付款状态为 未付款
            setViewValue(R.drawable.ic_state_nopay, STATE_NOPAY, BTN_LEFT_CANCEL, BTN_RIGHT_PAY);

        } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
            setViewValue(R.drawable.ic_state_paied, STATE_PAYED, BTN_LEFT_DRAWBACK, null);

        } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
            setViewValue(R.drawable.ic_state_sended, "已发货", BTN_LEFT_CHECKLOGI, BTN_RIGHT_TAKEOVER);

        }
        switch (oResult) {
            case "2": //订单已取消
                setViewValue(0, "已取消", null, null);

                break;
            case "3":
                setViewValue(0, "已作废", null, null);

                break;
            case "4":
                setViewValue(0, "请退货", null, null);

                break;
            case "5": // 已完成订单
                setViewValue(R.drawable.ic_state_success, "交易成功", BTN_LEFT_CHECKLOGI, BTN_RIGHT_COMMENT);
                break;
        }
        if (sResult.equals("4")) {
            setViewValue(0, "退款中", null, null);
        }
        if (pResult.equals("3")) {// 表示已退款
            setViewValue(0, "已退款", null, null);
        }
    }

    private void setViewValue(int drawableId, String state, String leftStr, String rightStr) {
        if (isOnlyHotel) {
            switch (state) {
                case STATE_NOPAY:
                    hotel.setPayState(1);
                    break;
                case STATE_PAYED:
                    hotel.setPayState(2);
                    break;
                default:
                    hotel.setPayState(0);
                    break;
            }

        } else {
//			Drawable stateDrawable = drawableId != 0 ? getResources().getDrawable(drawableId) : null;
//			if(stateDrawable != null)
//				stateDrawable.setBounds(0, 0, stateDrawable.getIntrinsicWidth(), stateDrawable.getIntrinsicHeight());
//			tv_orderState.setCompoundDrawables(null, null, stateDrawable, null);
            tv_orderState.setText(state);
            boolean isLeftStrEmp = TextUtils.isEmpty(leftStr);
            boolean isRightStrEmp = TextUtils.isEmpty(rightStr);
            int visitLeft = isLeftStrEmp ? View.GONE : View.VISIBLE;
            int visitRight = isRightStrEmp ? View.GONE : View.VISIBLE;
            b_order_opera1.setVisibility(visitLeft);
            b_order_opera2.setVisibility(visitRight);
            if (!isLeftStrEmp) b_order_opera1.setText(leftStr);
            if (!isRightStrEmp) b_order_opera2.setText(rightStr);
            //如果是已取消订单，那么隐藏查看券码
            if (fl_qr_code != null && state.equals("已取消")) {
                fl_qr_code.setVisibility(View.GONE);
            }
        }
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
        payWayDialog.dismiss();
        hideLoadDialog();
        jsShowMsg("支付成功");
        MyOrderData.payForOrder(outOrderNumber);
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
            new MyOrderData(this, true, outOrderNumber) {
                public void getDataSuccess() {
                    orderBean = getItemOrder();
                    if (orderBean == null || orderBean.isEmpty()) {
                        jsShowMsg("订单数据有误");
                        finish();
                        return;
                    }
                    Logger.e(TAG, "开始改变世界");
                    String orderStatus = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
                    String shipmentStatus = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
                    String payStatus = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();

                    if (!orderStatus.equals(oResult) || !shipmentStatus.equals(sResult) || !payStatus.equals(sResult)) {
                        /**
                         * 如果订单状态发生了改变，那么应该通知列表进行更新
                         */
//------------------------------全部订单--------------------------------------------
                        for (HashMap<String, Object> orderItem : MyOrderListAllActivity.data2) {
                            String order_orderNumber = orderItem.get("order_orderNumber").toString();
                            //更新所有订单列表
                            if (outOrderNumber.equals(order_orderNumber)) {
                                orderItem.remove("order_PaymentStatus");
                                orderItem.put("order_PaymentStatus", payStatus);
                                orderItem.remove("order_ShipmentStatus");
                                orderItem.put("order_ShipmentStatus", shipmentStatus);
                                orderItem.remove(MyOrderData.ORDER_ORDERSTATUS_KEY);
                                orderItem.put(MyOrderData.ORDER_ORDERSTATUS_KEY, orderStatus);
                                MyOrderActivity.dataChanges[0] = true;
                            }
                        }
                        for (HashMap<String, Object> orderItem : MyOrderData.order_all) {
                            String order_orderNumber = orderItem.get("order_orderNumber").toString();
                            //更新所有订单列表
                            if (outOrderNumber.equals(order_orderNumber)) {
                                orderItem.remove("order_PaymentStatus");
                                orderItem.put("order_PaymentStatus", payStatus);
                                orderItem.remove("order_ShipmentStatus");
                                orderItem.put("order_ShipmentStatus", shipmentStatus);
                                orderItem.remove(MyOrderData.ORDER_ORDERSTATUS_KEY);
                                orderItem.put(MyOrderData.ORDER_ORDERSTATUS_KEY, orderStatus);
                                break;
                            }
                        }
//------------------------------全部订单--------------------------------------------------

//------------------------------未付款订单-------------------------------------------------
                        if (pResult.equals("0")) {//未付款-->已完成订单

                            boolean removeData = removeData(MyOrderNoPayActivity.data2, outOrderNumber);
//                            MyOrderNoPayActivity.adapter.deleteData(position);
                            if (removeData)
                                MyOrderActivity.dataChanges[1] = true;
//------------------------------未付款订单-------------------------------------------------

//------------------------------已付款订单-------------------------------------------------
                        } else if (pResult.equals("2")) {//已付款-->已完成
                            boolean removeData = removeData(MyOrderNoSendActivity.data2, outOrderNumber);
                            if (removeData)
                                MyOrderActivity.dataChanges[2] = true;
                        }

//------------------------------已付款订单-------------------------------------------------

                        //已完成订单，添加到已完成列表
                        if (orderStatus.equals("5") && shipmentStatus.equals("2") && payStatus.equals("2")) {
                            Logger.e(TAG,"row 1011 添加至已完成列表");
                            MyOrderNoCommentActivity.adapter.addDateItem(0, orderBean);
                            MyOrderActivity.dataChanges[4] = true;
                        }

                        Logger.e(TAG, "数据更新完毕");

                        setResult(RESULT_ORDER_STATUS_CHANGE);
                    }
                    initView();
                    hideLoadDialog();
                }
            };
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

    private void sendDataChangeBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
        manager.sendBroadcast(intent);
    }
}
