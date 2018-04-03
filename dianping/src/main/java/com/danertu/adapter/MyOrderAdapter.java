package com.danertu.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderActivity;
import com.danertu.dianping.MyOrderDetail;
import com.danertu.dianping.MyOrderNoPayActivity;
import com.danertu.dianping.MyOrderParent;
import com.danertu.dianping.MyOrderShipmentActivity;
import com.danertu.dianping.PayPrepareActivity;
import com.danertu.dianping.QRCodeDetailActivity;
import com.danertu.dianping.R;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

import static com.danertu.entity.MyOrderData.ORDER_ITEM_ARRIVE_TIME;

public class MyOrderAdapter extends BaseAdapter {
    Context context = null;
    private List<HashMap<String, Object>> dataList = null;
    private DBManager db = null;
    private ImageLoader imgLoader = null;
    private long firstClick;
    private boolean isQuanyan = false;
    public static final int REQUEST_ORDER_DETAIL=121;
    private int tabIndex;
    public MyOrderAdapter(Context context, List<HashMap<String, Object>> data,int tabIndex) {
        this.context = context;
        this.dataList = data;
        db = DBManager.getInstance();
        imgLoader = ImageLoader.getInstance();
        this.tabIndex=tabIndex;
        firstClick = System.currentTimeMillis();
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int arg0) {
        return dataList.get(arg0);
    }

    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int p, View v, ViewGroup arg2) {
        // 表头数据
        final HashMap<String, Object> data_item = dataList.get(p);
        if (data_item != null) {
            final ViewHolder vh;
            if (v == null) {
                v = LayoutInflater.from(context).inflate(R.layout.activity_my_order_listitem, null);
                vh = new ViewHolder();
                vh.rl_orderItem_head = (RelativeLayout) v.findViewById(R.id.rl_orderItem_head);
                vh.tv_orderNum = (TextView) v.findViewById(R.id.tv_order_num);
                vh.tv_traceState = (TextView) v.findViewById(R.id.tv_my_order_trade_state);
                vh.tv_priceSum = (TextView) v.findViewById(R.id.tv_order_produce_priceSum);
                // vh.b_store =
                // (Button)v.findViewById(R.id.b_my_order_storelogo);
                vh.b_left = (Button) v.findViewById(R.id.b_order_left);
                vh.b_center = (Button) v.findViewById(R.id.b_order_center);
                vh.b_right = (Button) v.findViewById(R.id.b_order_right);
                vh.ll_proParent = (LinearLayout) v.findViewById(R.id.ll_parentOfprodeceItem);
                v.setTag(vh);// 设置标签
            } else {
                vh = (ViewHolder) v.getTag();
                // 子控件初始化
                vh.ll_proParent.removeAllViews();
                vh.b_left.setVisibility(View.GONE);
                vh.b_center.setVisibility(View.GONE);
                vh.b_right.setVisibility(View.GONE);
                vh.b_center.setEnabled(true);
                vh.b_left.setEnabled(true);
                vh.b_right.setEnabled(true);
            }
            String orderNum = data_item.get(MyOrderData.ORDER_ORDERNUMBER_KEY).toString();
            vh.tv_orderNum.setText("订单号：" + orderNum);

            String oResult = data_item.get("order_OderStatus").toString();
            String sResult = data_item.get("order_ShipmentStatus").toString();
            String pResult = data_item.get("order_PaymentStatus").toString();


            initListItemMsg(p, vh.ll_proParent, vh.tv_priceSum, vh.tv_traceState);
            initListItem(p, vh.tv_traceState, vh.b_left, vh.b_center, vh.b_right, oResult, sResult, pResult);

        }
        return v;
    }

    class ViewHolder {
        TextView tv_orderNum;
        TextView tv_traceState;
        TextView tv_priceSum;
        Button b_store;
        Button b_left;
        Button b_center;
        Button b_right;

        LinearLayout ll_proParent;
        RelativeLayout rl_orderItem_head;
    }

    /**
     *  显示商品信息
     * @param p
     * @param ll_parent
     * @param tv_priceSum
     * @param tv_traceState
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initListItemMsg(final int p, LinearLayout ll_parent, TextView tv_priceSum, final TextView tv_traceState) {
        // 实例化好列表里面的商品信息
        int yorderCount = 0;
        final HashMap<String, Object> headItems = dataList.get(p);
        Logger.i(getClass().getSimpleName(),headItems.toString());
        List<HashMap<String, String>> list_orderSet = (List) headItems.get(MyOrderData.ORDER_ITEMSET_KEY);
        ImageView iv_pro_logo = null;
        TextView tv_title = null;
        TextView tv_dec = null;
        TextView tv_num = null;
        TextView tv_price = null;

        TextView tv_order_1 = null;
        TextView tv_order_2 = null;
        TextView tv_favorablePrice = null;

        TextView tv_market_price = null;
        String imgPath = null;
        for (HashMap<String, String> orderItem : list_orderSet) {
            View v_orderItem = LayoutInflater.from(context).inflate(R.layout.activity_my_order_produce_item, null);

//			final String guid = orderItem.get("Guid");
            final String title = orderItem.get("Name");
//			final String dec = orderItem.get("Detail");
            final String agentID = orderItem.get("AgentID");
            final String supplierLoginId = orderItem.get("SupplierLoginID");
            final String price = orderItem.get("ShopPrice");
            final String marketPrice = orderItem.get(MyOrderData.ORDER_ITEM_MARKEPRICE_KEY);
            final String smallImage = orderItem.get("SmallImage");
            final String num = orderItem.get("BuyNumber");
            final String attrParam = orderItem.get(MyOrderData.ORDER_ITEM_ATTRIBUTE);

            //获取供应商ID，如果等于shopnum1说明是泉眼商品
            String suppID = orderItem.get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY);
            isQuanyan = suppID.equals(Constants.QY_SUPPLIERID);


//			final String mobile = orderItem.get("mobile");
            iv_pro_logo = (ImageView) v_orderItem.findViewById(R.id.iv_order_produce_logo);
            tv_title = (TextView) v_orderItem.findViewById(R.id.tv_order_produce_title);
            tv_dec = (TextView) v_orderItem.findViewById(R.id.tv_order_produce_dec);
            tv_num = (TextView) v_orderItem.findViewById(R.id.tv_order_produce_num);
            tv_price = (TextView) v_orderItem.findViewById(R.id.tv_order_produce_price);

            tv_market_price = (TextView) v_orderItem.findViewById(R.id.tv_order_proMarketPrice);

            tv_order_1 = ((TextView) v_orderItem.findViewById(R.id.tv_order_1));
            tv_order_2 = ((TextView) v_orderItem.findViewById(R.id.tv_order_2));
            tv_favorablePrice = ((TextView) v_orderItem.findViewById(R.id.tv_order_discount_price));

            imgPath = getSmallImgPath(smallImage, agentID, supplierLoginId);

//            Logger.e("图片链接：",imgPath);
            imgLoader.displayImage(imgPath, iv_pro_logo);
            tv_title.setText(title);
            String attrStr = getHandleAttrs(attrParam);
            if (!TextUtils.isEmpty(attrStr)) {
                tv_dec.setVisibility(View.VISIBLE);
                tv_dec.setText(attrStr);
            }
            tv_num.setText("x" + num);
            tv_price.setText("￥" + price);
            tv_favorablePrice.setText("￥" + price);
            tv_market_price.setText("￥" + marketPrice);
            tv_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tv_price.getPaint().setAntiAlias(true);
            tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tv_market_price.getPaint().setAntiAlias(true);
//            if(!isDetail){
//                tv_order_2.setVisibility(View.GONE);
//                tv_price.setVisibility(View.GONE);
//            }
            tv_order_2.setVisibility(View.GONE);
            tv_price.setVisibility(View.GONE);
            v_orderItem.setClickable(true);
            v_orderItem.setOnClickListener(new View.OnClickListener() {// 设置点击事件
                public void onClick(View arg0) {
//					startToProDetail(guid, title, smallImage, dec,
//						agentID, supplierLoginId, price, mobile);
                    long secondClick = System.currentTimeMillis();
                    if (secondClick - firstClick > 1500) {
                        firstClick = secondClick;
                        Intent intent = new Intent(context, MyOrderDetail.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MyOrderDetail.KEY_ORDER_ITEM, headItems);
                        bundle.putInt("tab_index",tabIndex);
                        bundle.putInt("position",p);
                        intent.putExtras(bundle);
                        ((Activity) context).startActivityForResult(intent, REQUEST_ORDER_DETAIL);
                    }
                }
            });
            // 添加view
            ll_parent.addView(v_orderItem);
            int tCount = Integer.parseInt(num);//总数
            yorderCount += tCount;
            String joinString = orderItem.get("joinCount");
            tCount = MyOrderData.getRealCount(tCount, joinString);

        }

        String totalPrice = dataList.get(p).get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();

        tv_priceSum.setText("共" + yorderCount + "件商品(含运费)：" + "￥" + totalPrice);
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

    /**
     * 获取商品图片路径
     *
     * @param imgName
     * @param agentID
     * @param supplierID
     * @return
     */
    public String getSmallImgPath(String imgName, String agentID, String supplierID) {
        return ActivityUtils.getImgUrl(imgName, agentID, supplierID);
    }

    /**
     * 设置按钮显示文字
     * @param p             position
     * @param tv_traceState 物流状态
     * @param b_left
     * @param b_center
     * @param b_right
     * @param oResult
     * @param sResult
     * @param pResult
     */
    private void initListItem(final int p, TextView tv_traceState, Button b_left, Button b_center, Button b_right, String oResult, String sResult, String pResult) {
        View parent = (View) b_left.getParent();
        parent.setVisibility(View.VISIBLE);
        if (pResult.equals("0")) {// 付款状态为 未付款
            tv_traceState.setText("待付款");
            b_center.setVisibility(View.VISIBLE);
            b_right.setVisibility(View.VISIBLE);
            b_center.setText("取消订单");
            b_right.setText("付款");
            b_right.setBackgroundResource(R.drawable.b_corner_red1);
            b_right.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
        } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
            tv_traceState.setText("已付款");
            b_center.setVisibility(View.GONE);
            b_right.setVisibility(View.VISIBLE);
            b_left.setVisibility(View.GONE);
            b_right.setText("申请退款");
            b_right.setBackgroundResource(R.drawable.b_corner_gray1);
            b_right.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));


        } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货

            //普通商品
            tv_traceState.setText("待收货");
            b_left.setVisibility(View.GONE);
            b_center.setVisibility(View.VISIBLE);
            b_right.setVisibility(View.VISIBLE);
//			b_left.setText("申请退款");
            b_center.setText("查看物流");
            b_right.setText("确定收货");
            b_right.setBackgroundResource(R.drawable.b_corner_red1);
            b_right.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
        }

        switch (oResult) {
            case "2": //订单已取消
                tv_traceState.setText("已取消");
                parent.setVisibility(View.GONE);

                break;
            case "3":
                tv_traceState.setText("已作废");
                parent.setVisibility(View.GONE);

                break;
            case "4":
                tv_traceState.setText("请退货");
                parent.setVisibility(View.GONE);

                break;
            case "5": // 已完成订单
                tv_traceState.setText("交易成功");
                b_left.setVisibility(View.GONE);
                b_center.setVisibility(View.VISIBLE);
                b_right.setVisibility(View.VISIBLE);
//			b_left.setText("申请退款");
                b_center.setText("查看物流");
                b_right.setText("评价订单");
                b_right.setBackgroundResource(R.drawable.b_corner_gray1);
                b_right.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
                break;
            default:
                break;
        }
        if (sResult.equals("4")) {
            tv_traceState.setText("退款中");
            parent.setVisibility(View.GONE);
        }
        if (pResult.equals("3")) {// 表示已退款
            tv_traceState.setText("已退款");
            parent.setVisibility(View.GONE);
        }

        setClickListener(p, b_left, b_center, b_right,sResult);
    }

    String orderNumber = null;
    Dialog askDialog = null;

    /**
     * 设置按钮点击事件
     * @param p
     * @param b_left
     * @param b_center
     * @param b_right
     * @param shipmentStatus
     */
    private void setClickListener(final int p, final Button b_left, final Button b_center, final Button b_right, String shipmentStatus) {
        b_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                HashMap<String, Object> headItems = dataList.get(p);
                orderNumber = headItems.get(MyOrderData.ORDER_ORDERNUMBER_KEY).toString();

                if (b_left.getText().toString().equals("申请退款")) {
                    String priceSum = headItems.get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();
                    applyDrawBack(priceSum, orderNumber);
                }
            }
        });

        b_center.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                HashMap<String, Object> headItems = dataList.get(p);
                // List proItems = (List) headItems.get(MyOrderData.ORDER_ITEMSET_KEY);
                orderNumber = headItems.get(MyOrderData.ORDER_ORDERNUMBER_KEY).toString();

                String shipCode = headItems.get("order_LogisticsCompanyCode").toString();// 快递公司编码
                String shipName = headItems.get(MyOrderData.ORDER_DISPATMODENAME_KEY).toString();
                String shipNumber = headItems.get("order_ShipmentNumber").toString();// 快递单号

                Logger.e("shipment", "shipCode=" + shipCode + "/shipName=" + shipName + "/shipNumber=" + shipNumber);
                if (b_center.getText().toString().equals("查看物流")) {

                    Intent intent = new Intent(context, MyOrderShipmentActivity.class);
                    intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_CODE, shipCode);
                    intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NUMBER, shipNumber);
                    intent.putExtra(MyOrderShipmentActivity.KEY_SHIPMENT_NAME, shipName);
                    context.startActivity(intent);

                } else if (b_center.getText().toString().equals("取消订单")) {

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
                            Logger.e("dialog_order", orderNumber + "");
                            b_sure.setText("正在取消...");
                            b_sure.setEnabled(false);
                            new Thread(cancelOrderRun).start();
                        }
                    });
                    askDialog.show();

                } else if (b_center.getText().toString().equals("追加评论")) {

                }
            }
        });
        b_right.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                long millis = System.currentTimeMillis();
                if (millis - firstClick < 1000) {
                    Logger.e("order", "频繁点击");
                    CommonTools.showShortToast(context, "请不要频繁点击。");
                    firstClick = millis;
                    return;
                }
                HashMap<String, Object> headItem = dataList.get(p);
                List<HashMap<String, String>> items = (List<HashMap<String, String>>) headItem.get(MyOrderData.ORDER_ITEMSET_KEY);
                orderNumber = headItem.get("order_orderNumber").toString();
                final String agentID = headItem.get("order_AgentId").toString();// shopID

                if (b_right.getText().toString().equals("申请退款")) {
                    String priceSum = headItem.get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();
                    applyDrawBack(priceSum, orderNumber);
                }

                if (b_right.getText().toString().equals("付款")) {
                    String body = "";
                    StringBuilder sb = new StringBuilder();
                    boolean isCanUserOrderPayWay = true;
                    int qyCount = 0;
                    for (int i = 0; i < items.size(); i++) {
                        HashMap<String, String> item = items.get(i);
                        String proName = item.get("Name");
                        String price = item.get("ShopPrice");
                        String buyCount1 = item.get("BuyNumber");//单项商品总数
                        String createUser = item.get(MyOrderData.ORDER_ITEM_CREATEUSER);
                        String agentid = item.get(MyOrderData.ORDER_ITEM_AGENTID_KEY);
                        String suppID = item.get(MyOrderData.ORDER_ITEM_SUPPLIERID_KEY);
                        if (!TextUtils.isEmpty(agentid))
                            isCanUserOrderPayWay = false;
                        if (suppID.equals(Constants.QY_SUPPLIERID)) {//泉眼商品
                            qyCount++;
                        }
                        int tCount = 0;
                        int yCount = Integer.parseInt(buyCount1);
                        String joinString = item.get("joinCount");
                        tCount = MyOrderData.getRealCount(yCount, joinString);
                        double tPrice = Double.parseDouble(price);
                        double singleSum = tCount * tPrice;

                        body += proName + ",";
                        sb.append(createUser).append(",").append(singleSum).append("|");
//                        pricedata += createUser + "," + singleSum + "|";
                    }
                    String pricedata = sb.toString();

                    String createTime = headItem.get(MyOrderData.ORDER_CREATETIME_KEY).toString();
//					String totalPrice = String.format("%.2f", priceSum);//保留两位小数 
                    String totalPrice = headItem.get(MyOrderData.ORDER_SHOULDPAY_KEY).toString();//保留两位小数
                    String subject = body.substring(0, body.length() - 1);
                    pricedata = pricedata.substring(0, pricedata.length() - 1);
                    String payWayName = headItem.get(MyOrderData.ORDER_PAYMENTNAME_KEY).toString();
                    toPayPrepare(createTime, totalPrice, orderNumber, subject, body, pricedata, isCanUserOrderPayWay, payWayName, qyCount == items.size());

                } else if (b_right.getText().toString().equals("确定收货")) {

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
                            Logger.e("dialog_order", orderNumber + "");
                            b_sure.setText("正在确定...");
                            b_sure.setEnabled(false);
                            new Thread(sureTakeGoods).start();
                        }
                    });
                    askDialog.show();

                } else if (b_right.getText().toString().equals("评价订单")) {
                    if (items != null && !items.isEmpty()) {
                        String guid = items.get(0).get("Guid");
                        MyOrderData.startProductCommentAct(context, orderNumber, guid, agentID);

                    } else {
                        CommonTools.showShortToast(context, "订单信息出错！");
                    }

                }
            }
        });

        //如果是温泉门票、客房
        if (isQuanyan&&shipmentStatus.equals("0")) {
            b_right.setText("查看券码");
            b_right.setBackgroundResource(R.drawable.b_corner_red1);
            b_right.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
            b_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, Object> headItems = dataList.get(p);
                    orderNumber = headItems.get(MyOrderData.ORDER_ORDERNUMBER_KEY).toString();
                    Intent intent = new Intent(context, QRCodeDetailActivity.class);
                    intent.putExtra("orderNumber", orderNumber);
                    context.startActivity(intent);
//                    ((MyOrderParent) context).startActivityForResult(intent,);
                }
            });
        }
    }

    /**
     * 跳转至支付
     * @param createTime
     * @param money
     * @param orderNumber
     * @param subject
     * @param body
     * @param pricedata
     * @param isCanUserOrder
     * @param payWayName
     * @param isQyOrder
     */
    public void toPayPrepare(String createTime, String money, String orderNumber, String subject, String body, String pricedata, boolean isCanUserOrder, String payWayName, boolean isQyOrder) {
        Intent intent = new Intent(context, PayPrepareActivity.class);
        intent.putExtra(PayPrepareActivity.KEY_CREATE_TIME, createTime);
        intent.putExtra(PayPrepareActivity.KEY_MONEY, money);
        intent.putExtra(PayPrepareActivity.KEY_ORDER_NUMBER, orderNumber);
        intent.putExtra(PayPrepareActivity.KEY_ALIPAY_BODY, body);
        intent.putExtra(PayPrepareActivity.KEY_ALIPAY_SUBJECT, subject);
        intent.putExtra(PayPrepareActivity.KEY_PRICE_DATA, pricedata);
        intent.putExtra(PayPrepareActivity.KEY_CANUSE_ORDER_PAYWAY, isCanUserOrder);
        intent.putExtra(PayPrepareActivity.KEY_PAYWAY_NAME, payWayName);
        intent.putExtra(PayPrepareActivity.KEY_CAN_ARRIVEPAY, isQyOrder);
        ((Activity) context).startActivityForResult(intent, MyOrderParent.REQ_PAY);
    }

    public static final int WHAT_CANCLEORDER_SUCCESS = 2;
    public static final int WHAT_CANCLEORDER_FAIL = -2;
    public static final int WHAT_TAKEGOODS_SUCCESS = 10;
    public static final int WHAT_TAKEGOODS_FAIL = -10;
    /**
     * 取消订单
     */
    public Runnable cancelOrderRun = new Runnable() {
        public void run() {
            if (AppManager.getInstance().postCancelOrder(orderNumber)) {
                mHandler.sendEmptyMessage(WHAT_CANCLEORDER_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(WHAT_CANCLEORDER_FAIL);
            }
        }
    };
    /**
     * 确认收货
     */
    public Runnable sureTakeGoods = new Runnable() {
        public void run() {
            if (AppManager.getInstance().postFinishOrder(orderNumber)) {
                mHandler.sendEmptyMessage(WHAT_TAKEGOODS_SUCCESS);// 表示确定收货成功
            } else {
                mHandler.sendEmptyMessage(WHAT_TAKEGOODS_FAIL);
            }
        }
    };

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_TAKEGOODS_SUCCESS) {
                if (askDialog.isShowing())
                    askDialog.dismiss();
                CommonTools.showShortToast(context, "确定收货成功");
                MyOrderData.sureTakeGoods(orderNumber);
                notifyDataSetChanged();

            } else if (msg.what == WHAT_CANCLEORDER_SUCCESS) {
                if (askDialog.isShowing())
                    askDialog.dismiss();
                CommonTools.showShortToast(context, "取消订单成功");
                MyOrderData.cancelOrder(orderNumber);
                MyOrderActivity.isCurrentPage = false;
                notifyDataSetChanged();
            }
        }
    };

    /**
     * 申请退款
     *
     * @param priceSum    订单体数据
     * @param orderNumber 订单号
     */
    public void applyDrawBack(String priceSum, String orderNumber) {
        String uid = db.GetLoginUid(context);
        MyOrderData.toPayBackActivity(context, orderNumber, uid, priceSum);
    }

    /**
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
            double tprice = Double.parseDouble(price);
            if (tprice == 1.0 || tprice == 0.1) {
                ActivityUtils.toProDetail2(context, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 0);
            } else {
                ActivityUtils.toProDetail2(context, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取订单状态
     */
    public String getorderStatus(String oresult) {
        String oderStatus = null;
        switch (oresult) {
            case ("0"):
                oderStatus = "未确认";
                break;
            case ("1"):
                oderStatus = "已确认";
                break;
            case ("2"):
                oderStatus = "已取消";
                break;
            case ("3"):
                oderStatus = "已作废";
                break;
            case ("4"):
                oderStatus = "退货";
                break;
            case ("5"):
                oderStatus = "已完成";
                break;
            default:
                break;
        }
        return oderStatus;
    }

    /**
     * 获取订单发货状态
     */
    public String getordersresult(String sresult) {
        String shipmentStatus = "";
        switch (sresult) {
            case ("0"):
                shipmentStatus = "未发货";
                break;
            case ("1"):
                shipmentStatus = "待收货";
                break;
            case ("2"):
                shipmentStatus = "已收货";
                break;
            case ("3"):
                shipmentStatus = "配货中";
                break;
            case ("4"):
                shipmentStatus = "已退货";
                break;
            default:
                break;
        }
        return shipmentStatus;
    }

    /**
     * 获取订单支付状态
     */
    public String getorderpaymentStatus(String presult) {
        String paymentStatus = "";
        switch (presult) {
            case ("0"):
                paymentStatus = "未付款";
                break;
            case ("1"):
                paymentStatus = "付款中";
                break;
            case ("2"):
                paymentStatus = "已付款";
                break;
            case ("3"):
                paymentStatus = "已退款";
                break;
            default:
                break;
        }
        return paymentStatus;
    }

    public void addDate(List<HashMap<String, Object>> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addDateItem(HashMap<String, Object> data) {
        dataList.add(dataList.size(), data);
        notifyDataSetChanged();
    }

    public void addDateItem(int index, HashMap<String, Object> data) {
        dataList.add(index, data);
        notifyDataSetChanged();
    }

    public void deleteData(int position) {
        dataList.remove(position);
        notifyDataSetChanged();
    }

    public void deleteData(HashMap<String, Object> data) {
        dataList.remove(data);
        notifyDataSetChanged();
    }

}