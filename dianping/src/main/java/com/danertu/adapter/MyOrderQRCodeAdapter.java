package com.danertu.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderDetail;
import com.danertu.dianping.MyOrderQRCodeActivity;
import com.danertu.dianping.MyOrderQRCodeParentActivity;
import com.danertu.dianping.MyOrderShipmentActivity;
import com.danertu.dianping.PayPrepareActivity;
import com.danertu.dianping.QRCodeDetailActivity;
import com.danertu.dianping.R;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.MyDialog;
import com.danertu.tools.PayUtils;
import com.danertu.widget.CommonTools;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

public class MyOrderQRCodeAdapter extends BaseAdapter {
    public static final int REQUEST_QRCODE_NEW = 1267;
    Context context = null;
    private List<HashMap<String, Object>> dataList = null;
    private DBManager db = null;
    private ImageLoader imgLoader = null;
    private long firstClick;
    private boolean isQuanyan = false;
    public static final int REQUEST_ORDER_DETAIL = 121;
    private int tabIndex;
    private String uid;
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
    private BaseActivity activity;

    public MyOrderQRCodeAdapter(Context context, List<HashMap<String, Object>> data, int tabIndex, String uid) {
        this.context = context;
        activity = (BaseActivity) context;
        this.dataList = data;
        db = DBManager.getInstance();
        imgLoader = ImageLoader.getInstance();
        this.tabIndex = tabIndex;
        this.uid = uid;
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
            String orderNum = data_item.get(MyOrderDataQRCode.ORDER_ORDERNUMBER_KEY).toString();
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
     * 显示商品信息
     *
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
        Logger.i(getClass().getSimpleName(), headItems.toString());
        List<HashMap<String, String>> list_orderSet = (List) headItems.get(MyOrderDataQRCode.ORDER_ITEMSET_KEY);
        ImageView iv_pro_logo = null;
        TextView tv_title = null;
        TextView tv_dec = null;
        TextView tv_num = null;
        TextView tv_price = null;

        TextView tv_order_1 = null;
        TextView tv_order_2 = null;
        TextView tv_favorablePrice = null;

        /**
         * 2018年4月13日
         * 类型：成人票
         * or
         * 入住时间：2018.4.13. 15:00后
         */
        TextView tv_item_quanyan_product_tip = null;

        TextView tv_market_price = null;
        String imgPath = null;

        for (HashMap<String, String> orderItem : list_orderSet) {
            View v_orderItem = LayoutInflater.from(context).inflate(R.layout.activity_my_order_produce_item_new, null);

//			final String guid = orderItem.get("Guid");
            final String title = orderItem.get("Name");
//			final String dec = orderItem.get("Detail");
            final String agentID = orderItem.get("AgentID");
            final String supplierLoginId = orderItem.get("SupplierLoginID");
            final String price = orderItem.get("ShopPrice");
            final String marketPrice = orderItem.get(MyOrderDataQRCode.ORDER_ITEM_MARKEPRICE_KEY);
            final String smallImage = orderItem.get("SmallImage");
            final String num = orderItem.get("BuyNumber");
            final String attrParam = orderItem.get(MyOrderDataQRCode.ORDER_ITEM_ATTRIBUTE);

            final String other1 = orderItem.get("other1");
            final String other2 = orderItem.get("other2");

            //如果供应商ID等于shopnum1说明是泉眼商品
            isQuanyan = supplierLoginId.equals(Constants.QY_SUPPLIERID);


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
            tv_item_quanyan_product_tip = ((TextView) v_orderItem.findViewById(R.id.tv_item_quanyan_product_tip));

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
                        bundle.putInt("tab_index", tabIndex);
                        bundle.putInt("position", p);
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
            tCount = MyOrderDataQRCode.getRealCount(tCount, joinString);

            //TODO 门票票型、酒店入住时间
//            if (isQuanyan){
//
//                //两个都不为空，说明是酒店
//                if (!TextUtils.isEmpty(other1)&&!TextUtils.isEmpty(other2)){
//                    tv_item_quanyan_product_tip.setText("入住时间："+other1);
//                }
//                //other1不为空，other2为空，门票
//                if (!TextUtils.isEmpty(other1)&&!TextUtils.isEmpty(other2)){
//                    String type="";
//                    tv_item_quanyan_product_tip.setText("票型："+other1);
//                }
//
//                tv_item_quanyan_product_tip.setVisibility(View.VISIBLE);
//            }

        }

        String totalPrice = dataList.get(p).get(MyOrderDataQRCode.ORDER_SHOULDPAY_KEY).toString();

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
        return ActivityUtils.getImgUrl(imgName, agentID, supplierID, uid);
    }

    /**
     * 设置按钮显示文字
     *
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

            if (isQuanyan) {
                b_center.setText(BTN_PAY);
                b_right.setText(BTN_QRCODE);
                b_center.setVisibility(View.VISIBLE);
                b_right.setVisibility(View.VISIBLE);
                tv_traceState.setText(STATUS_TICKET_NO_USE);
            } else {
                b_right.setText(BTN_PAY);
                b_center.setVisibility(View.GONE);
                b_right.setVisibility(View.VISIBLE);
                tv_traceState.setText(STATUS_NO_PAY);
            }
//            b_right.setBackgroundResource(R.drawable.b_corner_red1);
//            b_right.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
        } else if (sResult.equals("0") && pResult.equals("2")) {// 已付款 ，未发货
//            b_right.setBackgroundResource(R.drawable.b_corner_gray1);
//            b_right.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
            if (isQuanyan) {
                tv_traceState.setText(STATUS_TICKET_NO_USE);
                b_right.setText(BTN_QRCODE);
                b_center.setText(BTN_PAYBACK);
                b_left.setVisibility(View.GONE);
                b_center.setVisibility(View.VISIBLE);
                b_right.setVisibility(View.VISIBLE);
            } else {
                b_right.setText(BTN_CANCEL_ORDER);
                b_right.setVisibility(View.VISIBLE);
                b_center.setVisibility(View.GONE);
                b_left.setVisibility(View.GONE);
                tv_traceState.setText(STATUS_NO_SEND);
            }

        } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
            if (isQuanyan) {
                //泉眼
                tv_traceState.setText(STATUS_TICKET_USED);
                parent.setVisibility(View.GONE);
            } else {
                //普通商品
                tv_traceState.setText(STATUS_NO_RECEIVE);
                b_left.setVisibility(View.GONE);
                b_center.setVisibility(View.VISIBLE);
                b_right.setVisibility(View.VISIBLE);
//			b_left.setText("申请退款");
                b_center.setText(BTN_CHECK_SHIPMENT);
                b_right.setText(BTN_SURE_TAKE_GOODS);
//            b_right.setBackgroundResource(R.drawable.b_corner_red1);
//            b_right.setTextColor(ContextCompat.getColor(context, R.color.red_text1));
            }

        }

        switch (oResult) {
            case "2": //订单已取消
                tv_traceState.setText(STATUS_CANCELED);
                parent.setVisibility(View.GONE);

                break;
            case "3":
                tv_traceState.setText(STATUS_INVALID);
                parent.setVisibility(View.GONE);

                break;
            case "4":
                tv_traceState.setText(STATUS_RETURNNING);
//                parent.setVisibility(View.GONE);
                b_center.setVisibility(View.GONE);
                b_right.setVisibility(View.VISIBLE);
                b_left.setVisibility(View.GONE);
                b_right.setText(BTN_CHECK);
                break;
            case "5": // 已完成订单
                if (isQuanyan) {
                    tv_traceState.setText(STATUS_TICKET_USED);
                    parent.setVisibility(View.GONE);
//                b_right.setBackgroundResource(R.drawable.b_corner_gray1);
//                b_right.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
                } else {
                    tv_traceState.setText(STATUS_SUCCESS);
                    b_left.setVisibility(View.GONE);
                    b_center.setVisibility(View.VISIBLE);
                    b_right.setVisibility(View.VISIBLE);
//			b_left.setText("申请退款");
                    b_center.setText(BTN_CHECK_SHIPMENT);
//                b_right.setBackgroundResource(R.drawable.b_corner_gray1);
//                b_right.setTextColor(ContextCompat.getColor(context, R.color.gray_text_aaa));
                }

                break;
            default:
                break;
        }
        if (sResult.equals("4")) {//退款中
            tv_traceState.setText(STATUS_PAY_BACK_ING);
            b_left.setVisibility(View.GONE);
            b_center.setVisibility(View.GONE);
            b_right.setVisibility(View.VISIBLE);
            b_right.setText(BTN_CHECK);
        }
        if (pResult.equals("3")) {// 表示已退款
            tv_traceState.setText(STATUS_PAY_REFUND);
            parent.setVisibility(View.GONE);
        }


        setClickListener(p, b_left, b_center, b_right, sResult);
    }

    Dialog askDialog = null;

    /**
     * 设置按钮点击事件
     *
     * @param p
     * @param b_left
     * @param b_center
     * @param b_right
     * @param shipmentStatus
     */
    private void setClickListener(final int p, final Button b_left, final Button b_center, final Button b_right, String shipmentStatus) {
        final HashMap<String, Object> headItems = dataList.get(p);
        final String orderNum = headItems.get(MyOrderDataQRCode.ORDER_ORDERNUMBER_KEY).toString();
        b_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (isClickOften()) return;
                if (b_left.getText().toString().equals(BTN_PAYBACK)) {
                    String priceSum = headItems.get(MyOrderDataQRCode.ORDER_SHOULDPAY_KEY).toString();
                    applyDrawBack(priceSum, orderNum);
                }
            }
        });

        b_center.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isClickOften()) return;
                // List proItems = (List) headItems.get(MyOrderDataQRCode.ORDER_ITEMSET_KEY);

                String shipCode = headItems.get("order_LogisticsCompanyCode").toString();// 快递公司编码
                String shipName = headItems.get(MyOrderDataQRCode.ORDER_DISPATMODENAME_KEY).toString();
                String shipNumber = headItems.get("order_ShipmentNumber").toString();// 快递单号

                Logger.e("shipment", "shipCode=" + shipCode + "/shipName=" + shipName + "/shipNumber=" + shipNumber);
                String btnCenterStr = b_center.getText().toString();
                switch (btnCenterStr) {
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
                                Logger.e("dialog_order", orderNum + "");
                                b_sure.setText("正在取消...");
                                b_sure.setEnabled(false);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        /**
                                         * 取消订单
                                         */
                                        String json = AppManager.getInstance().postCancelOrder(orderNum, db.GetLoginUid(context));
                                        if ("true".equals(json)) {
                                            mHandler.sendEmptyMessage(WHAT_CANCLEORDER_SUCCESS);
                                        } else {
                                            judgeIsTokenException(json, new BaseActivity.TokenExceptionCallBack() {
                                                @Override
                                                public void tokenException(String code, final String info) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            activity.jsShowMsg(info);
                                                            activity.quitAccount();
                                                            activity.jsStartActivity("LoginActivity", "");
                                                            activity.finish();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void ok() {
                                                    mHandler.sendEmptyMessage(WHAT_CANCLEORDER_FAIL);
                                                }
                                            });
                                        }
                                    }
                                }).start();

                            }
                        });
                        askDialog.show();

                        break;
                    case "追加评论":

                        break;
                    case BTN_QRCODE: {
                        Intent intent = new Intent(context, QRCodeDetailActivity.class);
                        intent.putExtra("orderNumber", orderNum);
                        ((MyOrderQRCodeParentActivity) context).getParent().startActivityForResult(intent, REQUEST_QRCODE_NEW);
                        break;
                    }
                    case BTN_PAYBACK:
                        String priceSum = headItems.get(MyOrderDataQRCode.ORDER_SHOULDPAY_KEY).toString();
                        applyDrawBack(priceSum, orderNum);
                        break;
                    case BTN_PAY:

                        List<HashMap<String, String>> list_orderSet = (List) headItems.get(MyOrderDataQRCode.ORDER_ITEMSET_KEY);
                        String supplierLoginID = list_orderSet.get(0).get("SupplierLoginID").toString();
                        payOrder(orderNum, true, supplierLoginID.equals(Constants.QY_SUPPLIERID));

                        break;
                }
            }
        });
        b_right.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(View v) {
                if (isClickOften()) return;
                List<HashMap<String, String>> items = (List<HashMap<String, String>>) headItems.get(MyOrderDataQRCode.ORDER_ITEMSET_KEY);
                final String agentID = headItems.get("order_AgentId").toString();// shopID

                String btnRightStr = b_right.getText().toString();
                if (btnRightStr.equals(BTN_PAYBACK)) {
                    String priceSum = headItems.get(MyOrderDataQRCode.ORDER_SHOULDPAY_KEY).toString();
                    applyDrawBack(priceSum, orderNum);
                }

                switch (btnRightStr) {
                    case BTN_PAY:

                        String supplierLoginID = items.get(0).get("SupplierLoginID").toString();
                        payOrder(orderNum, true, supplierLoginID.equals(Constants.QY_SUPPLIERID));

                        break;
                    case BTN_SURE_TAKE_GOODS:
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
                                Logger.e("dialog_order", orderNum + "");
                                b_sure.setText("正在确定...");
                                b_sure.setEnabled(false);
                                new Thread(
                                        new Runnable() {

                                            @Override
                                            public void run() {
                                                /**
                                                 * 确认收货
                                                 */
                                                String json = AppManager.getInstance().postFinishOrder(orderNum, db.GetLoginUid(context));
                                                if ("true".equals(json)) {
                                                    sendMessage(WHAT_TAKEGOODS_SUCCESS, orderNum);// 表示确定收货成功
                                                } else {
                                                    judgeIsTokenException(json, new BaseActivity.TokenExceptionCallBack() {
                                                        @Override
                                                        public void tokenException(String code, final String info) {
                                                            activity.runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    activity.jsShowMsg(info);
                                                                    activity.quitAccount();
                                                                    activity.jsStartActivity("LoginActivity", "");
                                                                    activity.finish();
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void ok() {
                                                            sendMessage(WHAT_TAKEGOODS_FAIL, orderNum);// 表示确定收货失败
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                ).start();


                            }
                        });
                        askDialog.show();

                        break;
                    case BTN_CANCEL_ORDER:

                        askDialog = MyDialog.getDefineDialog(context, "取消订单", "注意： 订单取消后无法找回");
                        final Button b_cancel2 = (Button) askDialog.findViewById(R.id.b_dialog_left);
                        final Button b_sure2 = (Button) askDialog.findViewById(R.id.b_dialog_right);
                        b_cancel2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View arg0) {
                                askDialog.dismiss();
                            }
                        });
                        b_sure2.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View arg0) {
                                Logger.e("dialog_order", orderNum + "");
                                b_sure2.setText("正在取消...");
                                b_sure2.setEnabled(false);

//                                new Thread(cancelOrderRun).start();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String result = AppManager.getInstance().postCancelOrder(orderNum, db.GetLoginUid(context));
                                        if ("true".equals(result)) {
//                                            mHandler.sendEmptyMessage(WHAT_CANCLEORDER_SUCCESS);
                                            sendMessage(WHAT_CANCLEORDER_SUCCESS, orderNum);
                                        } else {
                                            judgeIsTokenException(result, new BaseActivity.TokenExceptionCallBack() {
                                                @Override
                                                public void tokenException(String code, final String info) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            activity.jsShowMsg(info);
                                                            activity.quitAccount();
                                                            activity.jsStartActivity("LoginActivity", "");
                                                            activity.finish();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void ok() {
                                                    sendMessage(WHAT_CANCLEORDER_FAIL, orderNum);
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        });
                        askDialog.show();

                        break;
                    case "评价订单":
                        if (items != null && !items.isEmpty()) {
                            String guid = items.get(0).get("Guid");
                            MyOrderDataQRCode.startProductCommentAct(context, orderNum, guid, agentID);

                        } else {
                            CommonTools.showShortToast(context, "订单信息出错！");
                        }

                        break;
                    case BTN_CHECK: {
                        Intent intent = new Intent(context, MyOrderDetail.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(MyOrderDetail.KEY_ORDER_ITEM, headItems);
                        bundle.putInt("tab_index", tabIndex);
                        bundle.putInt("position", p);
                        intent.putExtras(bundle);
                        ((Activity) context).startActivityForResult(intent, REQUEST_ORDER_DETAIL);
                        break;
                    }
                    case BTN_QRCODE: {
                        Intent intent = new Intent(context, QRCodeDetailActivity.class);
                        intent.putExtra("orderNumber", orderNum);
                        ((MyOrderQRCodeParentActivity) context).getParent().startActivityForResult(intent, REQUEST_QRCODE_NEW);
//                    ((MyOrderQRCodeParentActivity) context).startActivityForResult(intent,);
                        break;
                    }
                }
            }
        });


    }


    public void payOrder(final String orderNumber, boolean isShowAccountPay, boolean isShowArrivePay) {
        payOrder(orderNumber, true, true, isShowAccountPay, isShowArrivePay);
    }

    public void payOrder(final String orderNumber, boolean isShowAliPay, boolean isShowWechatPay, boolean isShowAccountPay, boolean isShowArrivePay) {
        Logger.e("MyOrderQRCodeAdapter", orderNumber);
        if (isPayLoading) {
            return;
        }
        PayUtils payUtils = new PayUtils((BaseActivity) context, db.GetLoginUid(context), orderNumber, isShowAliPay, isShowWechatPay, isShowAccountPay, isShowArrivePay) {
            @Override
            public void paySuccess() {
                isPayLoading = false;
                jsShowMsg("支付成功");
                MyOrderDataQRCode.payForOrder(orderNumber);
                notifyDataSetChanged();
//                toOrderDetail(orderNumber);
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

    private void jsShowMsg(String message) {
        CommonTools.showShortToast(context, message);
    }

    private boolean isClickOften() {
        long millis = System.currentTimeMillis();
        if (millis - firstClick < 1000) {
            Logger.e("order", "频繁点击");
            CommonTools.showShortToast(context, "请不要频繁点击。");
            firstClick = millis;
            return true;
        }
        return false;
    }

    /**
     * 跳转至支付
     *
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
        ((Activity) context).startActivityForResult(intent, MyOrderQRCodeParentActivity.REQ_PAY);
    }

    public static final int WHAT_CANCLEORDER_SUCCESS = 2;
    public static final int WHAT_CANCLEORDER_FAIL = -2;
    public static final int WHAT_TAKEGOODS_SUCCESS = 10;
    public static final int WHAT_TAKEGOODS_FAIL = -10;

    public void sendMessage(int what, Object obj) {
        Message message = mHandler.obtainMessage();
        message.what = what;
        message.obj = obj;
        mHandler.sendMessage(message);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case WHAT_TAKEGOODS_SUCCESS:
                    if (askDialog.isShowing())
                        askDialog.dismiss();
                    CommonTools.showShortToast(context, "确定收货成功");
                    MyOrderDataQRCode.sureTakeGoods(msg.obj.toString());
                    notifyDataSetChanged();
                    break;
                case WHAT_TAKEGOODS_FAIL:
                    CommonTools.showShortToast(context, "确定收货失败");
                    break;
                case WHAT_CANCLEORDER_SUCCESS:
                    if (askDialog.isShowing())
                        askDialog.dismiss();
                    CommonTools.showShortToast(context, "取消订单成功");
                    MyOrderDataQRCode.cancelOrder(msg.obj.toString());
                    MyOrderQRCodeActivity.isCurrentPage = false;
                    notifyDataSetChanged();
                    break;
                case WHAT_CANCLEORDER_FAIL:
                    CommonTools.showShortToast(context, "取消订单失败");
                    break;
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
        MyOrderDataQRCode.toPayBackActivity(context, orderNumber, uid, priceSum);
    }

    /**
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

    public boolean judgeIsTokenException(String json) {
        Gson gson = new Gson();
        TokenExceptionBean bean = gson.fromJson(json, TokenExceptionBean.class);
        return bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode());
    }

    public void judgeIsTokenException(String json, final String errorMsg, final int requestCode) {
        Gson gson = new Gson();
        TokenExceptionBean bean = gson.fromJson(json, TokenExceptionBean.class);
        if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
            final BaseActivity activity = (BaseActivity) this.context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(errorMsg)) {
                        jsShowMsg(errorMsg);
                    }
                    activity.quitAccount();
                    if (requestCode == -1) {
                        activity.jsStartActivity("LoginActivity", "");
                        activity.jsFinish();
                    } else {
                        activity.jsStartActivityForResult("LoginActivity", "", requestCode);
                    }
                }
            });
        }
        bean = null;
    }

    public void judgeIsTokenException(String json, BaseActivity.TokenExceptionCallBack callBack) {
        if (TextUtils.isEmpty(json)) {
            callBack.ok();
        }
        try {
            TokenExceptionBean bean = JSONObject.parseObject(json, TokenExceptionBean.class);
            if (bean != null && "false".equals(bean.getResult()) && "-1".equals(bean.getCode())) {
                callBack.tokenException(bean.getCode(), bean.getInfo());
            }else {
                callBack.ok();
            }
            bean = null;
            return;
        } catch (Exception e) {
            if (Constants.isDebug)
                e.printStackTrace();
            callBack.ok();
        }
        callBack.ok();
    }
}
