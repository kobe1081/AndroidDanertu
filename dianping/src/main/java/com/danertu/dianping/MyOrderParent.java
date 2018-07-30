package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import com.config.Constants;
import com.danertu.adapter.MyOrderAdapter;
import com.danertu.entity.MyOrderData;
import com.danertu.tools.Logger;
import com.danertu.widget.XListView;
import com.testin.agent.TestinAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import static com.danertu.adapter.MyOrderAdapter.REQUEST_ORDER_DETAIL;
import static com.danertu.adapter.MyOrderAdapter.REQUEST_QRCODE;
import static com.danertu.dianping.MyOrderDetail.KEY_ORDER_ITEM;

/**
 * 作者:  Viz
 * 日期:  2018/7/30 14:10
 *
 * 描述： 旧的订单中心子项父类，已弃用
*/

public abstract class MyOrderParent extends BaseActivity implements XListView.IXListViewListener {
    protected XListView lv_order;
    protected TextView orderNullText;
    protected Context context;
    public static ArrayList<HashMap<String, Object>> data2;

    public static final int REQ_PAY = 33;
    public static MyOrderAdapter adapter;
    private LocalBroadcastManager broadcastManager;
    private LoadOrderReceiver receiver;
    private DataChangerReceiver dataChangerReceiver;
    private AdapterOnActivityResult adapterOnActivityResult;

    public static final int TAB_ALL = 0;
    public static final int TAB_NO_PAY = 1;
    public static final int TAB_NO_SEND = 2;
    public static final int TAB_NP_RECEIVE = 3;
    public static final int TAB_NO_COMMENT = 4;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_parent);
        context = this;
        lv_order = ((XListView) findViewById(R.id.xlv_order));
        orderNullText = ((TextView) findViewById(R.id.tv_order_null_text));
        //不可下拉刷新
        lv_order.setPullRefreshEnable(false);
        lv_order.setXListViewListener(this);
        lv_order.setPullLoadEnable(true);// 设置让它上拉，FALSE为不让上拉，便不加载更多数据
        initBroadcastReceiver();
    }

    public void updateView() {
        Logger.e(getClass().getSimpleName(), "updateView");
        if ((data2 == null || data2.size() == 0) && MyOrderData.isFinish) {
            lv_order.setVisibility(View.GONE);
            orderNullText.setVisibility(View.VISIBLE);
        } else {
            lv_order.setVisibility(View.VISIBLE);
            orderNullText.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    //初始化广播接收器
    private void initBroadcastReceiver() {
        if (broadcastManager == null) {
            broadcastManager = LocalBroadcastManager.getInstance(this);
        }
        if (receiver == null) {
            IntentFilter filter1 = new IntentFilter();
            filter1.addAction(Constants.ORDER_FINISH);
            receiver = new LoadOrderReceiver();
            broadcastManager.registerReceiver(receiver, filter1);
        }
        if (dataChangerReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.ORDER_DATA_CHANGE);
            dataChangerReceiver = new DataChangerReceiver();
            broadcastManager.registerReceiver(dataChangerReceiver, intentFilter);
        }
        if (adapterOnActivityResult == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.ORDER_DATA_ON_ACTIVITY_FOR_RESULT);
            adapterOnActivityResult = new AdapterOnActivityResult();
            broadcastManager.registerReceiver(adapterOnActivityResult, intentFilter);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void findViewById() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Logger.e("test",getClass().getSimpleName()+"onDestroy");
        //不能在此注销广播接收器，否则将收不到订单加载完成后发出的广播
//        broadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.e(getClass().getSimpleName(), "onActivityResult 接收到数据变化");

        if (resultCode == REQ_PAY) {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoadMore() {
        loadMore();
    }

    abstract void refresh();

    abstract void loadMore();


    class LoadOrderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.i("order", "订单数据解析完毕");
            updateView();
        }
    }

    class DataChangerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.e(getClass().getSimpleName(), " DataChangerReceiver 接收到数据变化");
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 为了避免出现问题(比如在全部列表进入券码页面核销，返回列表再进入未付款页面后，已核销的订单在未付款列表显示已使用)，直接在这里操作列表数据
     */
    class AdapterOnActivityResult extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                int requestCode = intent.getIntExtra("requestCode", -1);
                int resultCode = intent.getIntExtra("resultCode", -1);
                final String orderNumber = intent.getStringExtra("orderNumber");
                switch (requestCode) {
                    case REQUEST_QRCODE:
                        new MyOrderData(MyOrderParent.this, true, orderNumber) {

                            @Override
                            public void getDataSuccess() {
                                HashMap<String, Object> orderBean = getItemOrder();
                                if (orderBean == null || orderBean.isEmpty()) {
                                    jsShowMsg("订单数据有误");
                                    finish();
                                    return;
                                }
                                //从订单中心过来
                                boolean hasAllChange = false;
                                //------------------------------全部订单--------------------------------------------
                                for (HashMap<String, Object> orderItem : MyOrderData.order_list_all) {
                                    String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                    //更新所有订单列表
                                    if (orderNumber.equals(order_orderNumber)) {
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
                                        MyOrderListAllActivity.adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (!hasAllChange)
                                    for (HashMap<String, Object> orderItem : MyOrderData.order_all) {
                                        String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                        //更新所有订单列表
                                        if (orderNumber.equals(order_orderNumber)) {
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

                                String orderStatus = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
                                String shipmentStatus = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
                                String payStatus = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();
                                if (orderStatus.equals("5") && shipmentStatus.equals("2") && payStatus.equals("2")) {
                                    //未付款
                                    boolean removeData = removeData(MyOrderData.order_list_noPay, orderNumber);
                                    if (removeData) {
                                        MyOrderNoPayActivity.adapter.notifyDataSetChanged();
                                    }
                                    if (!removeData) {
                                        removeData = removeData(MyOrderData.order_noPay, orderNumber);
                                    }
                                    //已付款
                                    if (!removeData) {
                                        removeData = removeData(MyOrderData.order_list_noSend, orderNumber);
                                        if (removeData) {
                                            MyOrderNoSendActivity.adapter.notifyDataSetChanged();
                                        }
                                    }
                                    if (!removeData) {
                                        removeData(MyOrderData.order_noSend, orderNumber);
                                    }

                                }


                                Logger.e(TAG, "数据更新完毕");
                            }
                        };
                        break;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}