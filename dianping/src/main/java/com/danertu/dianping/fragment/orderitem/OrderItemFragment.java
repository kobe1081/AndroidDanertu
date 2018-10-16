package com.danertu.dianping.fragment.orderitem;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.base.NewBaseFragment;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderShipmentActivity;
import com.danertu.dianping.OrderDetailActivity;
import com.danertu.dianping.PayBackActivity;
import com.danertu.dianping.QRCodeDetailActivity;
import com.danertu.dianping.R;
import com.danertu.entity.NewOrderBean;
import com.danertu.entity.OrderBody;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.PayUtils;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderItemFragment extends NewBaseFragment<OrderItemContact.OrderItemView, OrderItemPresenter> implements OrderItemContact.OrderItemView, SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {


    @BindView(R.id.tv_order_null_text)
    TextView tvOrderNullText;
    @BindView(R.id.xlv_order)
    XListView xlvOrder;
    Unbinder unbinder;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private View view;
    private OrderAdapter adapter;
    public static final int REQUEST_ORDER_DETAIL = 121;
    public static final int REQUEST_QRCODE = 122;
    private long firstClick;
    private boolean isPayLoading;
    private LocalBroadcastManager broadcastManager;
    private DataChangerReceiver dataChangerReceiver;

    @Override
    public OrderItemPresenter initPresenter() {
        return new OrderItemPresenter(context);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_order_item, container, false);
        unbinder = ButterKnife.bind(this, view);
        firstClick = System.currentTimeMillis();
        initBroadcastReceiver();
        presenter.onCreate(getArguments());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (broadcastManager != null && dataChangerReceiver != null) {
            broadcastManager.unregisterReceiver(dataChangerReceiver);
        }
        presenter.onDestroy();
    }

    @Override
    public void initList(List<NewOrderBean> list) {
        adapter = new OrderAdapter(list);
        xlvOrder.setPullRefreshEnable(true);
        xlvOrder.setPullLoadEnable(true);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setEnabled(false);
        xlvOrder.setXListViewListener(this);
        xlvOrder.setAdapter(adapter);
    }

    @Override
    public void notifyChange() {
        if (adapter == null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyChange(int listSize) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (listSize > 0) {
            swipeRefresh.setEnabled(false);
            xlvOrder.setVisibility(View.VISIBLE);
            tvOrderNullText.setVisibility(View.GONE);
        } else {
            swipeRefresh.setEnabled(true);
            xlvOrder.setVisibility(View.GONE);
            tvOrderNullText.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void stopLoadMore() {
        xlvOrder.stopLoadMore();
    }

    @Override
    public void stopRefresh() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        xlvOrder.stopRefresh();
    }

    @Override
    public void loadError() {
        xlvOrder.stopLoadMore();
    }

    @Override
    public void noMoreData() {
        xlvOrder.setPullLoadEnable(false);
    }


    @Override
    public void cancelOrderError(String orderNumber, int position) {

    }

    @Override
    public void cancelOrderFailure(String orderNumber, int position) {

    }


    @Override
    public void sureTakeGoodsError(String orderNumber, int position) {

    }

    @Override
    public void sureTakeGoodsFailure(String orderNumber, int position) {

    }

    @Override
    public void changeOrderStatue(int position, String orderNumber, String orderStatue, String payStatue, String shipStatue) {
        if (adapter != null) {
            adapter.changeOrderStatue(orderNumber, position, orderStatue, payStatue, shipStatue);
        }

    }

    @Override
    public void initBroadcastReceiver() {
        if (broadcastManager == null) {
            broadcastManager = LocalBroadcastManager.getInstance(context);
        }
        if (dataChangerReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.ORDER_DATA_CHANGE);
            dataChangerReceiver = new DataChangerReceiver();
            broadcastManager.registerReceiver(dataChangerReceiver, intentFilter);
        }
    }

    @Override
    public void sendDataChangeBroadcast(String orderNumber, int position, NewOrderBean bean) {
        Logger.e(TAG, bean.toString());
        if (broadcastManager != null) {
            Intent intent = new Intent(Constants.ORDER_DATA_CHANGE);
            Bundle extras = new Bundle();
            extras.putString("orderNumber", orderNumber);
            extras.putInt("position", position);
            extras.putParcelable("orderBean", bean);
            intent.putExtras(extras);
            broadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public void toLogin() {
        jsStartActivity("LoginActivity", "");
    }

    @Override
    public void onRefresh() {
        presenter.refresh();
        xlvOrder.setPullLoadEnable(true);
    }

    @Override
    public void onLoadMore() {
        presenter.loadMore();
    }

    private boolean isClickOften() {
        long millis = System.currentTimeMillis();
        if (millis - firstClick < 1000) {
            Logger.e(TAG, "频繁点击");
            CommonTools.showShortToast(context, "请不要频繁点击。");
            firstClick = millis;
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void payOrder(int position, final String orderNumber, boolean isShowAccountPay, boolean isShowArrivePay) {
        payOrder(position, orderNumber, true, true, isShowAccountPay, isShowArrivePay);
    }

    public void payOrder(final int position, final String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay) {
        if (isPayLoading) {
            return;
        }
        isPayLoading = true;
        PayUtils payUtils = new PayUtils((BaseActivity) context, getUid(), orderNumber, isShowAliPay, isShowWechatPay, isShowAccountPay, isShowArrivePay) {
            @Override
            public void paySuccess() {
                isPayLoading = false;
                jsShowMsg("支付成功");
                presenter.changeOrderStatue(position, orderNumber, null, "2", "0");
                toOrderDetail(orderNumber);
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
//                toOrderDetail(orderNumber);
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


    class OrderAdapter extends BaseAdapter {
        private List<NewOrderBean> list;
        private LayoutInflater inflater;
        private ImageLoader loader;
        private boolean isQuanyan = false;

        private boolean isShowQRCode = false;
        private Dialog askDialog = null;

        public static final String STATUS_NO_PAY = "等待支付";
        public static final String STATUS_NO_SEND = "待发货";
        public static final String STATUS_NO_RECEIVE = "待收货";
        public static final String STATUS_PAY_BACK_ING = "退款中";
        public static final String STATUS_PAY_REFUND = "已退款";
        public static final String STATUS_CANCELED = "已取消";
        public static final String STATUS_INVALID = "已作废";
        public static final String STATUS_SUCCESS = "交易成功";
        public static final String STATUS_RETURNNING = "请退货";
        public static final String STATUS_TICKET_NO_USE = "待使用";
        public static final String STATUS_TICKET_USED = "已使用";


        private static final String BTN_PAY = "支付";
        private static final String BTN_CANCEL_ORDER = "取消订单";
        private static final String BTN_PAYBACK = "申请退款";
        private static final String BTN_SURE_TAKE_GOODS = "确认收货";
        private static final String BTN_CHECK_SHIPMENT = "查看物流";
        private static final String BTN_CHECK = "查看";
        private static final String BTN_QRCODE = "查看券码";
        private boolean isPayLoading;


        public OrderAdapter(List<NewOrderBean> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
            loader = ImageLoader.getInstance();
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_my_order_listitem, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final NewOrderBean bean = list.get(position);
            int yorderCount = 0;
            boolean isCooperateSupplier = false;
            final String orderNumber = bean.getOrderNumber();
            holder.tvOrderNum.setText("订单号：" + orderNumber);
            holder.llParentOfprodeceItem.removeAllViews();
            for (OrderBody.OrderproductlistBean.OrderproductbeanBean orderproductbeanBean : bean.getProductItems()) {
                View productItemView = inflater.inflate(R.layout.activity_my_order_produce_item_new, holder.llParentOfprodeceItem, false);
                ProductItemHolder itemHolder = new ProductItemHolder(productItemView);
                String supplierLoginID = orderproductbeanBean.getSupplierLoginID();
                loader.displayImage(getSmallImgPath(orderproductbeanBean.getSmallImage(), orderproductbeanBean.getAgentID(), supplierLoginID), itemHolder.ivOrderProduceLogo);
                String attrStr = getHandleAttrs(orderproductbeanBean.getAttribute());
                itemHolder.tvOrderProduceTitle.setText(orderproductbeanBean.getName());
                if (!TextUtils.isEmpty(attrStr)) {
                    itemHolder.tvOrderProduceDec.setVisibility(View.VISIBLE);
                    itemHolder.tvOrderProduceDec.setText(attrStr);
                }
                String buyNumber = orderproductbeanBean.getBuyNumber();
                itemHolder.tvOrderProduceNum.setText("x" + buyNumber);
                itemHolder.tvOrderProducePrice.setText("￥" + orderproductbeanBean.getShopPrice());
                itemHolder.tvOrderDiscountPrice.setText("￥" + orderproductbeanBean.getShopPrice());
                itemHolder.tvOrderProMarketPrice.setText("￥" + orderproductbeanBean.getMarketPrice());
                itemHolder.tvOrderProducePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                itemHolder.tvOrderProducePrice.getPaint().setAntiAlias(true);
                itemHolder.tvOrderProMarketPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                itemHolder.tvOrderProMarketPrice.getPaint().setAntiAlias(true);
                itemHolder.tvOrder2.setVisibility(View.GONE);
                itemHolder.tvOrderProducePrice.setVisibility(View.GONE);
                itemHolder.root.setClickable(true);
                itemHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isClickOften()) {
                            return;
                        }
                        Intent intent = new Intent(context, OrderDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putString("orderNumber", orderNumber);
                        bundle.putInt("tab_index", presenter.getTabIndex());
                        bundle.putInt("position", position);
                        intent.putExtras(bundle);
                        getActivity().startActivityForResult(intent, REQUEST_ORDER_DETAIL);
                    }
                });
                int tCount = Integer.parseInt(buyNumber);
                yorderCount += tCount;
//                tCount=getRealCount(tCount,orderproductbeanBean.getJoinCount());
                holder.tvOrderProducePriceSum.setText("共" + yorderCount + "件商品(含运费)：" + "￥" + bean.getShouldPayPrice());
                holder.llParentOfprodeceItem.addView(productItemView);
                //如果供应商ID等于shopnum1说明是泉眼商品
                isQuanyan = supplierLoginID.equals(Constants.QY_SUPPLIERID);
                isCooperateSupplier = Constants.COOPERATE_SUPPLIER_ID.contains(supplierLoginID);
                isShowQRCode = "1".equals(orderproductbeanBean.getIsQRCodeShow());
            }

            String paymentStatus = bean.getPaymentStatus();
            String shipmentStatus = bean.getShipmentStatus();
            String oderStatus = bean.getOderStatus();
            View btnParent = (View) holder.bOrderLeft.getParent();

            if (isShowQRCode) {
                if (isQuanyan) {
                    if ("0".equals(paymentStatus)) {
                        holder.bOrderCenter.setText(BTN_PAY);
                        holder.bOrderCenter.setVisibility(View.VISIBLE);
                        holder.bOrderRight.setText(BTN_QRCODE);
                        holder.bOrderRight.setVisibility(View.VISIBLE);
                        holder.tvMyOrderTradeState.setText(STATUS_TICKET_NO_USE);
                    } else if ("0".equals(shipmentStatus) && "2".equals(paymentStatus)) {
                        holder.tvMyOrderTradeState.setText(STATUS_TICKET_NO_USE);
                        holder.bOrderRight.setText(BTN_QRCODE);
                        holder.bOrderCenter.setText(BTN_PAYBACK);
                        holder.bOrderLeft.setVisibility(View.GONE);
                        holder.bOrderCenter.setVisibility(View.VISIBLE);
                        holder.bOrderRight.setVisibility(View.VISIBLE);
                    }
                }
                if (isCooperateSupplier) {
                    holder.tvMyOrderTradeState.setText(STATUS_TICKET_NO_USE);
                    btnParent.setVisibility(View.VISIBLE);
                    holder.bOrderRight.setText(BTN_QRCODE);
                    holder.bOrderCenter.setText(BTN_PAYBACK);
                    holder.bOrderLeft.setVisibility(View.GONE);
                    holder.bOrderCenter.setVisibility(View.VISIBLE);
                    holder.bOrderRight.setVisibility(View.VISIBLE);
                }
                /**
                 * 已核销的状态下显示已完成
                 */
                if ("1".equals(shipmentStatus) && "2".equals(paymentStatus)) {
                    holder.tvMyOrderTradeState.setText(STATUS_TICKET_USED);
                    btnParent.setVisibility(View.GONE);
                }
            } else {
                if ("0".equals(paymentStatus)) {
                    btnParent.setVisibility(View.VISIBLE);
                    // 付款状态为 未付款
                    holder.bOrderRight.setText(BTN_PAY);
                    holder.bOrderCenter.setVisibility(View.GONE);
                    holder.bOrderRight.setVisibility(View.VISIBLE);
                    holder.tvMyOrderTradeState.setText(STATUS_NO_PAY);
                } else if ("0".equals(shipmentStatus) && "2".equals(paymentStatus)) {
                    btnParent.setVisibility(View.VISIBLE);
                    // 已付款 ，未发货
                    holder.bOrderRight.setText(BTN_CANCEL_ORDER);
                    holder.bOrderRight.setVisibility(View.VISIBLE);
                    holder.bOrderCenter.setVisibility(View.GONE);
                    holder.bOrderLeft.setVisibility(View.GONE);
                    holder.tvMyOrderTradeState.setText(STATUS_NO_SEND);
                } else if ("1".equals(shipmentStatus) && "2".equals(paymentStatus)) {
                    btnParent.setVisibility(View.VISIBLE);
                    // 已发货,买家待收货
                    //普通商品
                    btnParent.setVisibility(View.VISIBLE);
                    holder.tvMyOrderTradeState.setText(STATUS_NO_RECEIVE);
                    holder.bOrderLeft.setVisibility(View.GONE);
                    holder.bOrderCenter.setVisibility(View.VISIBLE);
                    holder.bOrderRight.setVisibility(View.VISIBLE);
//			holder.bOrderLeft.setText("申请退款");
                    holder.bOrderCenter.setText(BTN_CHECK_SHIPMENT);
                    holder.bOrderRight.setText(BTN_SURE_TAKE_GOODS);
//            holder.bOrderRight.setBackgroundResource(R.drawable.b_corner_red1);
//            holder.bOrderRight.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
                }
            }


            switch (oderStatus) {
                case "2": //订单已取消
                    holder.tvMyOrderTradeState.setText(STATUS_CANCELED);
                    btnParent.setVisibility(View.GONE);
                    break;
                case "3":
                    holder.tvMyOrderTradeState.setText(STATUS_INVALID);
                    btnParent.setVisibility(View.GONE);
                    break;
                case "4":
                    btnParent.setVisibility(View.VISIBLE);
                    holder.tvMyOrderTradeState.setText(STATUS_RETURNNING);
                    holder.bOrderCenter.setVisibility(View.GONE);
                    holder.bOrderRight.setVisibility(View.VISIBLE);
                    holder.bOrderLeft.setVisibility(View.GONE);
                    holder.bOrderRight.setText(BTN_CHECK);
                    break;
                case "5": // 已完成订单
                    if (isQuanyan || isCooperateSupplier) {
                        holder.tvMyOrderTradeState.setText(STATUS_TICKET_USED);
                        btnParent.setVisibility(View.GONE);
//                holder.bOrderRight.setBackgroundResource(R.drawable.b_corner_gray1);
//                holder.bOrderRight.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
                    } else {
                        btnParent.setVisibility(View.VISIBLE);
                        holder.tvMyOrderTradeState.setText(STATUS_SUCCESS);
                        holder.bOrderLeft.setVisibility(View.GONE);
                        holder.bOrderCenter.setVisibility(View.GONE);
                        holder.bOrderRight.setVisibility(View.VISIBLE);
//			holder.bOrderLeft.setText("申请退款");
                        holder.bOrderRight.setText(BTN_CHECK_SHIPMENT);
//                holder.bOrderRight.setBackgroundResource(R.drawable.b_corner_gray1);
//                holder.bOrderRight.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
                    }
                    break;
                default:
                    break;
            }
            if ("4".equals(shipmentStatus)) {
                btnParent.setVisibility(View.VISIBLE);
                holder.tvMyOrderTradeState.setText(STATUS_PAY_BACK_ING);
                holder.bOrderLeft.setVisibility(View.GONE);
                holder.bOrderCenter.setVisibility(View.GONE);
                holder.bOrderRight.setVisibility(View.VISIBLE);
                holder.bOrderRight.setText(BTN_CHECK);
            }
            if ("3".equals(paymentStatus)) {// 表示已退款
                holder.tvMyOrderTradeState.setText(STATUS_PAY_REFUND);
                btnParent.setVisibility(View.GONE);
            }
            /**
             * 设置点击事件
             */
            holder.bOrderLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickOften()) {
                        return;
                    }
                    if (BTN_PAYBACK.equals(holder.bOrderLeft.getText().toString())) {
                        applyDrawBack(bean.getShouldPayPrice(), orderNumber);
                    }
                }
            });
            final String shipCode = bean.getLogisticsCompanyCode();//快递公司编码
            final String shipName = bean.getDispatchModeName();
            final String shipNumber = bean.getShipmentNumber();//快递单号
            holder.bOrderCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickOften()) {
                        return;
                    }
                    String centerStr = holder.bOrderCenter.getText().toString();
                    switch (centerStr) {
                        case BTN_CHECK_SHIPMENT: {
                            Intent intent = new Intent(context, MyOrderShipmentActivity.class);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_CODE, shipCode);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER, shipNumber);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NAME, shipName);
                            context.startActivity(intent);
                            break;
                        }
                        case BTN_CANCEL_ORDER:
                            askDialog = MyDialog.getDefineDialog(context, "取消订单", "注意： 订单取消后无法找回");
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
                                    presenter.cancelOrder(orderNumber, position);

                                }
                            });
                            askDialog.show();

                            break;
                        case "追加评论":
                            break;
                        case BTN_QRCODE: {
                            Intent intent = new Intent(context, QRCodeDetailActivity.class);
                            intent.putExtra("orderNumber", orderNumber);
                            intent.putExtra("position", position);
//                        context.startActivity(intent);
                            getActivity().startActivityForResult(intent, REQUEST_QRCODE);
                            break;
                        }
                        case BTN_PAYBACK:

                            applyDrawBack(bean.getShouldPayPrice(), orderNumber);
                            break;
                        case BTN_PAY: //支付
                            payOrder(position, orderNumber, true, false);

//                    List<HashMap<String, String>> items = (List<HashMap<String, String>>) headItems.get(MyOrderData.ORDER_ITEMSET_KEY);
//                    String body = "";
//                    StringBuilder sb = new StringBuilder();
//                    boolean isCanUserOrderPayWay = true;
//                    int qyCount = 0;
//                    for (int i = 0; i < items.size(); i++) {
//                        HashMap<String, String> item = items.get(i);
//                        String proName = item.get("Name");
//                        String price = item.get("ShopPrice");
//                        String buyCount1 = item.get("BuyNumber");//单项商品总数
//                        String createUser = item.get(MyOrderData.ORDER_ITEM_CREATEUSER);
//                        String agentid = item.get(MyOrderData.ORDER_ITEM_AGENTID_KEY);
//                        String suppID = item.get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY);
//                        if (!TextUtils.isEmpty(agentid))
//                            isCanUserOrderPayWay = false;
//                        if (suppID.equals(Constants.QY_SUPPLIERID)) {//泉眼商品
//                            qyCount++;
//                        }
//                        int tCount = 0;
//                        int yCount = Integer.parseInt(buyCount1);
//                        String joinString = item.get("joinCount");
//                        tCount = MyOrderData.getRealCount(yCount, joinString);
//                        double tPrice = Double.parseDouble(price);
//                        double singleSum = tCount * tPrice;
//
//                        body += proName + ",";
//                        sb.append(createUser).append(",").append(singleSum).append("|");
////                        pricedata += createUser + "," + singleSum + "|";
//                    }
//                    String pricedata = sb.toString();
//
//                    String createTime = headItems.get(MyOrderData.ORDER_CREATETIME_KEY).toString();
////					String totalPrice = String.format("%.2f", priceSum);//保留两位小数
//                    String totalPrice = headItems.get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();//保留两位小数
//                    String subject = body.substring(0, body.length() - 1);
//                    pricedata = pricedata.substring(0, pricedata.length() - 1);
//                    String payWayName = headItems.get(MyOrderData.ORDER_PAYMENTNAME_KEY).toString();
//                    toPayPrepare(createTime, totalPrice, orderNumber, subject, body, pricedata, isCanUserOrderPayWay, payWayName, qyCount == items.size());

                            break;
                    }
                }
            });

            holder.bOrderRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickOften()) {
                        return;
                    }
                    String rightStr = holder.bOrderRight.getText().toString();
                    switch (rightStr) {
                        case BTN_CHECK_SHIPMENT: {
                            Intent intent = new Intent(context, MyOrderShipmentActivity.class);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_CODE, shipCode);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER, shipNumber);
                            intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NAME, shipName);
                            context.startActivity(intent);
                            break;
                        }
                        case BTN_PAYBACK:
                            applyDrawBack(bean.getShouldPayPrice(), orderNumber);
                            break;
                        case BTN_PAY:
                            payOrder(position, orderNumber, true, false);
                            break;
                        case BTN_SURE_TAKE_GOODS: {
                            askDialog = MyDialog.getDefineDialog(context, "确定收货", "请确定收到货物才进行此操作");
                            Button b_cancel = (Button) askDialog.findViewById(R.id.b_dialog_left);
                            final Button b_sure = (Button) askDialog.findViewById(R.id.b_dialog_right);
                            b_cancel.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View arg0) {
                                    askDialog.dismiss();
                                }
                            });
                            b_sure.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View arg0) {
                                    b_sure.setText("正在确定...");
                                    b_sure.setEnabled(false);
                                    presenter.sureTakeGoods(orderNumber, position);

                                }
                            });
                            askDialog.show();
                        }
                        break;
                        case BTN_CANCEL_ORDER: {
                            askDialog = MyDialog.getDefineDialog(context, "取消订单", "注意： 订单取消后无法找回");
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
                                    presenter.cancelOrder(orderNumber, position);

                                }
                            });
                            askDialog.show();

                        }
                        break;
                        case "评价订单":
//                            if (items != null && !items.isEmpty()) {
//                                String guid = items.get(0).get("Guid");
//                                MyOrderData.startProductCommentAct(context, number, guid, agentID);
//                            } else {
//                                CommonTools.showShortToast(context, "订单信息出错！");
//                            }
                            break;
                        case BTN_CHECK: {
                            Intent intent = new Intent(context, OrderDetailActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            Bundle bundle = new Bundle();
                            bundle.putString("orderNumber", orderNumber);
                            bundle.putInt("tab_index", presenter.getTabIndex());
                            bundle.putInt("position", position);
                            intent.putExtras(bundle);
                            getActivity().startActivityForResult(intent, REQUEST_ORDER_DETAIL);
                            break;
                        }
                        case BTN_QRCODE: {
                            Intent intent = new Intent(context, QRCodeDetailActivity.class);
                            intent.putExtra("position", position);
                            intent.putExtra("orderNumber", orderNumber);
                            getActivity().startActivityForResult(intent, REQUEST_QRCODE);
                            break;
                        }
                    }
                }
            });

            return convertView;
        }

        /**
         * 申请退款
         *
         * @param priceSum    订单体数据
         * @param orderNumber 订单号
         */
        public void applyDrawBack(String priceSum, String orderNumber) {
            String uid = getUid();
            toPayBackActivity(context, orderNumber, uid, priceSum);
        }


        public void toPayBackActivity(Context context, String orderNumber, String uid, String price) {
            Intent intent = new Intent(context, PayBackActivity.class);
            intent.putExtra("ordernumber", orderNumber);
            intent.putExtra("memberid", uid);
            intent.putExtra("price", price);
            context.startActivity(intent);
        }

        /**
         * 获取除去赠送数量的真实数量
         *
         * @param count      单项商品总数
         * @param joinString 买几赠一 的几
         * @return 除去赠送数量的真实数量
         */
        public int getRealCount(int count, String joinString) {
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

        protected String getHandleAttrs(String attrParam) {
            StringBuilder sb = new StringBuilder();
            String result = "";
            try {
                if (!TextUtils.isEmpty(attrParam)) {
                    String[] item = attrParam.split(";");
                    String[] names = item[0].split(",");
                    String[] values = item[1].split(",");
                    for (int i = 0; i < names.length; i++) {
                        sb.append(names[i]).append("; ").append(values[i]).append("; ");
//                    result += names[i] + ":" + values[i] + "; ";
                    }
                    result = sb.toString();
                    result = result.substring(0, result.length() - 2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public void changeOrderStatue(String orderNumber, int position, String orderStatue, String payStatue, String shipStatue) {
            NewOrderBean bean = list.get(position);
            if (!TextUtils.isEmpty(orderStatue)) {
                bean.setOderStatus(orderStatue);
            }
            if (!TextUtils.isEmpty(payStatue)) {
                bean.setPaymentStatus(payStatue);
            }
            if (!TextUtils.isEmpty(shipStatue)) {
                bean.setShipmentStatus(shipStatue);
            }
            dismissDialog();
            notifyDataSetChanged();
        }

        public void dismissDialog() {
            if (askDialog != null && askDialog.isShowing()) {
                askDialog.dismiss();
            }
        }

        public void addData(NewOrderBean bean, int position) {
            list.add(position, bean);
            notifyDataSetChanged();
        }

        public void addData(NewOrderBean bean) {
            list.add(0, bean);
            notifyDataSetChanged();
        }

        public void removeData(int position) {
            list.remove(position);
            notifyDataSetChanged();
        }


        class ViewHolder {
            @BindView(R.id.tv_my_order_trade_state)
            TextView tvMyOrderTradeState;
            @BindView(R.id.tv_order_num)
            TextView tvOrderNum;
            @BindView(R.id.rl_orderItem_head)
            RelativeLayout rlOrderItemHead;
            @BindView(R.id.ll_parentOfprodeceItem)
            LinearLayout llParentOfprodeceItem;
            @BindView(R.id.tv_order_produce_priceSum)
            TextView tvOrderProducePriceSum;
            @BindView(R.id.b_order_right)
            Button bOrderRight;
            @BindView(R.id.b_order_center)
            Button bOrderCenter;
            @BindView(R.id.b_order_left)
            Button bOrderLeft;

            public ViewHolder(View view) {
//                R.layout.activity_my_order_listitem
                ButterKnife.bind(this, view);
            }
        }

        class ProductItemHolder {
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
            @BindView(R.id.tv_item_quanyan_product_tip)
            TextView tvItemQuanyanProductTip;
            @BindView(R.id.root)
            FrameLayout root;

            public ProductItemHolder(View view) {
//                R.layout.activity_my_order_produce_item_new
                ButterKnife.bind(this, view);
            }
        }
    }

    class DataChangerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.dataChange(intent);
        }
    }
}
