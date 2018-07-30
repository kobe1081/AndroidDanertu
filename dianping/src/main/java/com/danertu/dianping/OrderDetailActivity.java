package com.danertu.dianping;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.base.NewBaseActivity;
import com.danertu.dianping.activity.orderdetail.OrderDetailContact;
import com.danertu.dianping.activity.orderdetail.OrderDetailPresenter;
import com.danertu.entity.MyOrderData;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.entity.QuanYanProductCategory;
import com.danertu.tools.MathUtils;
import com.danertu.tools.MyDialog;
import com.danertu.tools.PayUtils;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.danertu.dianping.activity.orderdetail.OrderDetailPresenter.REQUEST_SHOW_QRCODE;

/**
 * 作者:  Viz
 * 日期:  2018/7/30 14:12
 *
 * 描述： 新的订单详情
*/
public class OrderDetailActivity extends NewBaseActivity<OrderDetailContact.OrderDetailView, OrderDetailPresenter> implements OrderDetailContact.OrderDetailView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.b_title_back)
    Button bTitleBack;
    @BindView(R.id.b_title_operation)
    Button bTitleOperation;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.title)
    FrameLayout title;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.b_order_opera1)
    Button bOrderOpera1;
    @BindView(R.id.b_order_opera2)
    Button bOrderOpera2;
    @BindView(R.id.bottom)
    LinearLayout bottom;
    @BindView(R.id.tv_order_trade_state)
    TextView tvOrderTradeState;
    @BindView(R.id.tv_order_trade_state_tips)
    TextView tvOrderTradeStateTips;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.fl_qr_code)
    FrameLayout flQrCode;
    @BindView(R.id.tv_payCenter_recName)
    TextView tvPayCenterRecName;
    @BindView(R.id.tv_payCenter_recMobile)
    TextView tvPayCenterRecMobile;
    @BindView(R.id.tv_payCenter_recAddress)
    TextView tvPayCenterRecAddress;
    @BindView(R.id.contact)
    LinearLayout contact;
    @BindView(R.id.ll_contact)
    LinearLayout llContact;
    @BindView(R.id.tv_order_arrive_time)
    TextView tvOrderArriveTime;
    @BindView(R.id.tv_order_leave_time)
    TextView tvOrderLeaveTime;
    @BindView(R.id.ll_hotel_date)
    LinearLayout llHotelDate;
    @BindView(R.id.tv_order_count_price)
    TextView tvOrderCountPrice;
    @BindView(R.id.tv_order_payway)
    TextView tvOrderPayway;
    @BindView(R.id.ll_pay_info)
    LinearLayout llPayInfo;
    @BindView(R.id.tv_order_createTime)
    TextView tvOrderCreateTime;
    @BindView(R.id.tv_order_num)
    TextView tvOrderNum;
    @BindView(R.id.tv_copy_order_num)
    TextView tvCopyOrderNum;
    @BindView(R.id.ll_more)
    LinearLayout llMore;
    @BindView(R.id.ll_orderDetail_proParent)
    LinearLayout llOrderDetailProParent;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;


    private boolean isQuanyan = false;
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
    @BindView(R.id.rl_order)
    RelativeLayout rlOrder;
    @BindView(R.id.tv_order_fail_text)
    TextView tvOrderFailText;
    private String orderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initSystemBar();
        setSystemBarWhite();
        swipeRefresh.setEnabled(false);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras == null) {
            jsShowMsg("发生了错误");
            return;
        }
        orderNumber = extras.getString("orderNumber", "");
        presenter.onCreate(intent);
    }

    @OnClick(R.id.b_title_back)
    public void onBackClick(View view) {
        jsFinish();
    }

    @OnClick(R.id.tv_copy_order_num)
    public void onTvCopyOrderNumClick(View view) {
        presenter.copyOrderNumber();
    }

    @OnClick(R.id.tv_order_fail_text)
    public void onTvFailClick(View view) {
        presenter.refresh();
    }

    @OnClick(R.id.b_order_opera1)
    public void onOrderOpera1Click(View view) {
        String opera1Str = bOrderOpera1.getText().toString();
        switch (opera1Str) {
            case BTN_LEFT_DRAWBACK:
                presenter.toPayBackForResult(getUid());
                break;
            case BTN_LEFT_CANCEL:
                askDialog(opera1Str, "取消订单", "注意： 订单取消后无法找回", orderNumber);
                break;
            case BTN_LEFT_CHECKLOGI:
                presenter.toShipment();
                break;
            case BTN_LEFT_PAY:
                pay(orderNumber, true, isQuanyan);
                break;
        }
    }


    @OnClick(R.id.b_order_opera2)
    public void onOrderOpera2Click(View view) {
        String opera2Str = bOrderOpera2.getText().toString();
        /**在新版本app中，由于按钮中的文字添加上了价格，所以只能通过判断是否包含   支付  来判断*/
        if (opera2Str.contains("支付")) {
            pay(orderNumber, true, isQuanyan);
        } else {
            switch (opera2Str) {
                case BTN_RIGHT_PAY:
                    pay(orderNumber, true, isQuanyan);
                    break;
                case BTN_RIGHT_TAKEOVER:
                    askDialog(opera2Str, "确定收货", "请确定收到货物才进行此操作", orderNumber);
                    break;
                case BTN_RIGHT_COMMENT:
                    //评价功能已经删除
                    break;
                case BTN_RIGHT_QRCODE:
                    //查看券码
                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", orderNumber);
                    startActivityForResult(intent, REQUEST_SHOW_QRCODE);
                    break;
                case BTN_LEFT_DRAWBACK:
                    presenter.toPayBackForResult(getUid());
                    break;
            }
        }
    }

    @Override
    public OrderDetailPresenter initPresenter() {
        return new OrderDetailPresenter(context);
    }


    private Dialog askDialog;

    /**
     * 弹窗确认是否取消订单对话框
     *
     * @param tag
     * @param title
     * @param content
     */
    private void askDialog(final String tag, String title, String content, final String orderNumber) {
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
                    presenter.cancelOrder(orderNumber);
                } else if (tag.equals(BTN_RIGHT_TAKEOVER)) {
                    presenter.sureTakeGoods(orderNumber);
                }
            }
        });
        askDialog.show();
    }

    @Override
    public void showOrderDetail(OrderHead.OrderinfolistBean orderHead, final OrderBody.OrderproductlistBean orderBody) {
        rlOrder.setVisibility(View.VISIBLE);
        tvOrderFailText.setVisibility(View.GONE);
        final OrderHead.OrderinfolistBean.OrderinfobeanBean bean = orderHead.getOrderinfobean().get(0);
        String orderStatue = bean.getOderStatus();
        String payStatue = bean.getPaymentStatus();
        String shipStatue = bean.getShipmentStatus();
        String paymentName = bean.getPaymentName();
        String dispatchTime = TextUtils.isEmpty(bean.getDispatchTime()) ? "" : bean.getDispatchTime().replace("/", ".");
        String shouldPayPrice = bean.getShouldPayPrice();
        tvPayCenterRecName.setText(bean.getName());
        tvPayCenterRecMobile.setText(bean.getMobile());
        if (TextUtils.isEmpty(bean.getAddress())) {
            //收货地址为空，是温泉门票??
            tvPayCenterRecAddress.setVisibility(View.GONE);
        } else {
            //普通订单，显示收货地址
            tvPayCenterRecAddress.setText(bean.getAddress());
            tvPayCenterRecAddress.setVisibility(View.GONE);
        }
        tvOrderCreateTime.setText("下单时间：" + bean.getCreateTime().replace("/", "."));
        tvOrderNum.setText(getString(R.string.pay_orderNum_tips) + bean.getOrderNumber());
        boolean isHotel = "1".equals(bean.getOrderType());
        /**
         * 如果是酒店订单，显示入住以及离开时间
         */
        if (isHotel) {
            llHotelDate.setVisibility(View.VISIBLE);
            OrderBody.OrderproductlistBean.OrderproductbeanBean bodyBean = orderBody.getOrderproductbean().get(0);
            String other1 = TextUtils.isEmpty(bodyBean.getOther1()) ? "--" : bodyBean.getOther1();
            String other2 = TextUtils.isEmpty(bodyBean.getOther2()) ? "--" : bodyBean.getOther2();
            tvOrderArriveTime.setText(other1);
            tvOrderLeaveTime.setText(other2);
            tvPayCenterRecAddress.setVisibility(View.GONE);
        } else {
            tvPayCenterRecAddress.setVisibility(View.VISIBLE);
            llHotelDate.setVisibility(View.GONE);
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        ImageLoader imageLoader = ImageLoader.getInstance();
        int qyCount = 0;
        double ttPrice = 0;
        int joinCount = 0;
        int totalCount = 0;//订单总数量
        final ArrayList<String> phoneCalls = new ArrayList<>();
        for (final OrderBody.OrderproductlistBean.OrderproductbeanBean bodyBean : orderBody.getOrderproductbean()) {
            View productItemView = inflater.inflate(R.layout.order_detail_item, llOrderDetailProParent, false);
            ViewHolder holder = new ViewHolder(productItemView);
            final String supplierLoginID = bodyBean.getSupplierLoginID();
            isQuanyan = Constants.QY_SUPPLIERID.equals(supplierLoginID);
            final String agentID = bodyBean.getAgentID();
            List<OrderBody.OrderproductlistBean.OrderproductbeanBean.ProductRankBean> productRank = bodyBean.getProductRank();
            String discountBuyNum = "";
            String discountPrice = "";
            if (productRank != null && productRank.size() != 0 && productRank.get(0) != null) {
                discountBuyNum = productRank.get(0).getDiscountBuyNum();
                discountPrice = productRank.get(0).getDiscountPrice();
            }
            final String agentMobile = TextUtils.isEmpty(bodyBean.getTel()) ? Constants.CK_PHONE : bodyBean.getTel();
            if (!phoneCalls.contains(agentMobile)) phoneCalls.add(agentMobile);
            String shopName = TextUtils.isEmpty(bodyBean.getShopName()) ? "醇康" : bodyBean.getShopName();
            final String shopPrice = bodyBean.getShopPrice();
            final String buyNumberStr = bodyBean.getBuyNumber();
            int buyNumberInt = 0;
            try {
                buyNumberInt = Integer.parseInt(buyNumberStr);
                int sumCount = getRealCount(buyNumberInt, bodyBean.getISGive());
                totalCount += sumCount;
                double tPrice = Double.parseDouble(shopPrice);
                double singleSum = MathUtils.multiply(sumCount, tPrice).doubleValue();
                ttPrice = MathUtils.add(ttPrice, MathUtils.multiply(buyNumberInt, tPrice).doubleValue());
                joinCount = Integer.parseInt(bodyBean.getISGive());
            } catch (Exception e) {
                if (Constants.isDebug) {
                    e.printStackTrace();
                }
                rlOrder.setVisibility(View.GONE);
                tvOrderFailText.setVisibility(View.VISIBLE);
                break;
            }

//            if (!TextUtils.isEmpty(agentID)){
//
//            }
            //泉眼商品
            if (isQuanyan) {
                qyCount++;
            }


            imageLoader.displayImage(getImgUrl(bodyBean.getSmallImage(), agentID, supplierLoginID), holder.ivOrderProduceLogo);
            holder.flProMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isQuanyan) {
                        presenter.toQuanYan(bodyBean.getGuid(), agentID);
                    } else {
                        presenter.startToProDetail(bodyBean.getGuid(), bodyBean.getName(), bodyBean.getSmallImage(), "", agentID, supplierLoginID, shopPrice, agentMobile);
                    }
                }
            });

            if (joinCount > 0) {
                holder.itemJoinCount.setVisibility(View.VISIBLE);
                holder.itemJoinCount.setText("买" + joinCount + "赠1");
            } else {
                holder.itemJoinCount.setVisibility(View.GONE);
            }
            String handleAttrs = getHandleAttrs(bodyBean.getAttribute());
            if (TextUtils.isEmpty(handleAttrs)) {
                holder.tvOrderProduceDec.setVisibility(View.GONE);
            } else {
                holder.tvOrderProduceDec.setVisibility(View.VISIBLE);
                holder.tvOrderProduceDec.setText(handleAttrs);
            }
            holder.tvOrderProduceNum.setText("x" + buyNumberStr);
            holder.tvOrderProduceTitle.setText(bodyBean.getName());
            if (TextUtils.isEmpty(discountPrice)) {
                //非后台拿货
                holder.tvOrder2.setVisibility(View.GONE);
                holder.tvOrderProducePrice.setVisibility(View.GONE);
                holder.tvOrderDiscountPrice.setText("￥" + shopPrice);
            } else {
                holder.tvItemFavourableTip.setVisibility(View.VISIBLE);
                if (buyNumberInt > Integer.parseInt(discountBuyNum)) {
                    //达到优惠条件，显示优惠价，并给拿货价加上删除线
                    holder.tvOrderDiscountPrice.setText("￥" + shopPrice);
                    holder.tvOrderProducePrice.setText("￥" + CommonTools.formatZero2Str(Double.parseDouble(shopPrice) + Double.parseDouble(discountPrice)));
                    holder.tvOrderProducePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.tvOrderProducePrice.getPaint().setAntiAlias(true);
                } else {
                    //未达到优惠条件,显示拿货价
                    holder.tvOrderDiscountPrice.setVisibility(View.GONE);
                    holder.tvOrder1.setVisibility(View.GONE);
                    holder.tvOrder2.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                    holder.tvOrderProducePrice.setText("￥" + shopPrice);
                }
            }
            holder.tvOrder1.setVisibility(View.GONE);
            holder.tvOrder2.setVisibility(View.GONE);
            holder.tvOrder3.setVisibility(View.GONE);
            holder.tvOrderProMarketPrice.setText("￥" + bodyBean.getMarketPrice());
            holder.tvOrderProMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvOrderProMarketPrice.getPaint().setAntiAlias(true);
            llOrderDetailProParent.addView(productItemView);
        }
        String unSignFomat = "共%s件商品，共￥%.2f元（含运费%.2f）";
        double parseDouble = Double.parseDouble(shouldPayPrice);
        String format = String.format(unSignFomat, totalCount, parseDouble, Double.parseDouble(bean.getDispatchPrice()));
        SpannableStringBuilder stringBuilder = setStyleForUnSignNum(format, parseDouble);
        //总数量、总价格、运费等信息  共998件商品，共￥12455504.00元（含运费0.00）
        tvOrderCountPrice.setText(stringBuilder);

        tvHelp.setOnClickListener(new View.OnClickListener() {
            Dialog dialog;

            @Override
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

        if (isQuanyan) {
            if (isHotel) {
                /**
                 * 酒店客房
                 */
                if (payStatue.equals("0")) {// 付款状态为 未付款
                    setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY, ORDER_STATUS_NO_PAY_TIPS_HOTEL, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + shouldPayPrice);

                } else if (shipStatue.equals("0") && payStatue.equals("2")) {// 已付款 ，未发货
                    setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_PAYED_HOTEL, ORDER_STATUS_PAYED_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);


                } else if (shipStatue.equals("1") && payStatue.equals("2")) {// 已发货,买家待收货
                    setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_FINISH_HOTEL, ORDER_STATUS_FINISH_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);

                }
                //订单已完成
                if (orderStatue.equals("5")) {
                    setViewValue(R.drawable.ic_state_success, ORDER_STATUS_FINISH_HOTEL, ORDER_STATUS_FINISH_HOTEL_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);
                }
            } else {
                /**
                 * 门票等泉眼商品
                 */
                if (payStatue.equals("0")) {// 付款状态为 未付款
                    if (paymentName.contains("到付")) {
                        setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_ARRAY_PAY_TICKET, ORDER_STATUS_ARRAY_PAYTIPS_TICKET, BTN_LEFT_PAY, BTN_RIGHT_QRCODE);
                    } else {
                        setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY, ORDER_STATUS_NO_PAY_TIPS_TICKET, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + shouldPayPrice);
                    }

                } else if (shipStatue.equals("0") && payStatue.equals("2")) {// 已付款 ，未发货
                    setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_NO_USE, ORDER_STATUS_NO_USE_TIPS, BTN_LEFT_DRAWBACK, BTN_RIGHT_QRCODE);


                } else if (shipStatue.equals("1") && payStatue.equals("2")) {// 已发货,买家待收货
                    setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_USED_TICKET, ORDER_STATUS_USED_TIPS_TICKET + shouldPayPrice, null, BTN_RIGHT_QRCODE);

                }

                //订单已完成
                if (orderStatue.equals("5")) {
                    setViewValue(R.drawable.ic_state_success, ORDER_STATUS_USED_TICKET, ORDER_STATUS_USED_TIPS_TICKET + dispatchTime, null, BTN_RIGHT_QRCODE);
                }
            }
        } else {
            /**
             * 普通商品
             */
            if (payStatue.equals("0")) {// 付款状态为 未付款
                setViewValue(R.drawable.ic_state_nopay, ORDER_STATUS_NO_PAY_GOODS, ORDER_STATUS_NO_PAY_TIPS_GOODS, BTN_LEFT_CANCEL, BTN_RIGHT_PAY + shouldPayPrice);

            } else if (shipStatue.equals("0") && payStatue.equals("2")) {// 已付款 ，未发货
                setViewValue(R.drawable.ic_state_paied, ORDER_STATUS_PAYED_GOODS, ORDER_STATUS_PAYED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_LEFT_DRAWBACK);


            } else if (shipStatue.equals("1") && payStatue.equals("2")) {// 已发货,买家待收货
                setViewValue(R.drawable.ic_state_sended, ORDER_STATUS_SENDED_GOODS, ORDER_STATUS_SENDED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_RIGHT_TAKEOVER);

            }
            //订单已完成
            if (orderStatue.equals("5")) {
                setViewValue(R.drawable.ic_state_success, ORDER_STATUS_FINISHED_GOODS, ORDER_STATUS_FINISHED_TIPS_GOODS, BTN_LEFT_CHECKLOGI, BTN_LEFT_DRAWBACK);
            }
        }

        switch (orderStatue) {
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
        if (shipStatue.equals("4")) {
            setViewValue(0, ORDER_STATUS_RETURNING_GOODS, ORDER_STATUS_RETURN_TIPS_GOODS, BTN_LEFT_CHECKLOGI, null);
        }
        if (payStatue.equals("3")) {// 表示已退款
            setViewValue(0, ORDER_STATUS_RETURNED_GOODS, ORDER_STATUS_RETURN_TIPS_GOODS, null, null);
        }

        switch (payStatue) {
            case "2":
                if (paymentName.contains(PAY_WAY_ALI)) {
                    tvOrderPayway.setText(PAY_INFO_ALI_PAY);
                } else if (paymentName.contains(PAY_WAY_WECHAT)) {
                    tvOrderPayway.setText(PAY_INFO_WECHAT_PAY);
                } else if (paymentName.contains(PAY_WAY_ACCOUNT)) {
                    tvOrderPayway.setText(PAY_INFO_ACCOUNT_PAY);
                } else if (paymentName.equals(PAY_WAY_ARRIVE)) {
                    tvOrderPayway.setText(PAY_INFO_ARRIVE_PAY);
                }
                break;
            case "0":
                if (isQuanyan) {
                    tvOrderPayway.setText(PAY_INFO_NO_PAY_QUANYAN);
                } else {
                    tvOrderPayway.setText(PAY_INFO_NO_PAY);
                }
                break;
            default:
                tvOrderPayway.setVisibility(View.GONE);
                break;
        }

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
        tvOrderTradeState.setText(state);
        if (TextUtils.isEmpty(stateTips)) {
            tvOrderTradeStateTips.setVisibility(View.GONE);
        } else {
            tvOrderTradeStateTips.setText(stateTips);
        }

        boolean isLeftStrEmp = TextUtils.isEmpty(leftStr);
        boolean isRightStrEmp = TextUtils.isEmpty(rightStr);
        int visitLeft = isLeftStrEmp ? View.GONE : View.VISIBLE;
        int visitRight = isRightStrEmp ? View.GONE : View.VISIBLE;
        bOrderOpera1.setVisibility(visitLeft);
        bOrderOpera2.setVisibility(visitRight);
        if (!isLeftStrEmp) bOrderOpera1.setText(leftStr);
        if (!isRightStrEmp) bOrderOpera2.setText(rightStr);
//            //如果是已取消订单，那么隐藏查看券码
//            if (fl_qr_code != null && state.equals("已取消")) {
//                fl_qr_code.setVisibility(View.GONE);
//            }
//        }
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
                presenter.changeOrderState(null, "2", null);
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

    public SpannableStringBuilder setStyleForUnSignNum(String text, double num) {
        SpannableStringBuilder unSignNumBuilder = new SpannableStringBuilder(text);
        int start = text.indexOf("" + num);
        int end = start + String.valueOf(num).length() + 1;
        unSignNumBuilder.setSpan(new RelativeSizeSpan(1.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        unSignNumBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#555555")), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return unSignNumBuilder;
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
            if (Constants.isDebug)
                e.printStackTrace();
//            Log.e(TAG, "startToProDetail: " + e.getMessage());
        }
    }

    @Override
    public void toQuanYanPage(QuanYanProductCategory.ValBean bean, String shopId) {
        jsStartActivity("com.danertu.dianping.HtmlActivity", "pageName|" + QUANYAN_PRODUCT_URL + "?&platform=android&timestamp=" + System.currentTimeMillis() + ",;guid|" + bean.getProductGuid() + ",;shopid|" + shopId + ",;productCategory|" + bean.getProductCategory());
    }

    /**
     * 获取除去赠送数量的真实数量
     *
     * @param count      单项商品总数
     * @param joinString 买几赠一 的几
     * @return 除去赠送数量的真实数量
     */
    public static int getRealCount(int count, String joinString) {
        int joinCount = 0;
        int songCount = 0;
        if (!joinString.equals(""))
            joinCount = Integer.parseInt(joinString);
        if (joinCount > 0) {// 表示买 joinCount 赠 1
            songCount = count / (joinCount + 1);
        }
        count -= songCount;
        return count;
    }

    @Override
    public void stopRefresh() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void setSwipeRefreshEnable(boolean enable) {
        swipeRefresh.setEnabled(enable);
    }

    @Override
    public void getOrderInfoError() {
        rlOrder.setVisibility(View.GONE);
        tvOrderFailText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SHOW_QRCODE:
                llOrderDetailProParent.removeAllViews();
                showLoadDialog();
                presenter.getOrderInfo(true);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
    }

    class ViewHolder {
        @BindView(R.id.tv_shopName)
        TextView tvShopName;
        @BindView(R.id.iv_order_produce_logo)
        ImageView ivOrderProduceLogo;
        @BindView(R.id.tv_order_1)
        TextView tvOrder1;
        @BindView(R.id.tv_order_2)
        TextView tvOrder2;
        @BindView(R.id.tv_order_3)
        TextView tvOrder3;
        @BindView(R.id.tv_order_discount_price)
        TextView tvOrderDiscountPrice;
        @BindView(R.id.tv_order_produce_price)
        TextView tvOrderProducePrice;
        @BindView(R.id.tv_order_proMarketPrice)
        TextView tvOrderProMarketPrice;
        @BindView(R.id.tv_order_produce_num)
        TextView tvOrderProduceNum;
        @BindView(R.id.ll_right)
        LinearLayout llRight;
        @BindView(R.id.tv_order_produce_title)
        TextView tvOrderProduceTitle;
        @BindView(R.id.tv_order_produce_dec)
        TextView tvOrderProduceDec;
        @BindView(R.id.item_arriveTime)
        TextView itemArriveTime;
        @BindView(R.id.item_leaveTime)
        TextView itemLeaveTime;
        @BindView(R.id.item_joinCount)
        TextView itemJoinCount;
        @BindView(R.id.tv_item_favourable_tip)
        TextView tvItemFavourableTip;
        @BindView(R.id.ll_orderItem)
        LinearLayout llOrderItem;
        @BindView(R.id.item_proMsg)
        FrameLayout flProMsg;

        public ViewHolder(View view) {
//            R.layout.order_detail_item
            ButterKnife.bind(this, view);
        }
    }
}
