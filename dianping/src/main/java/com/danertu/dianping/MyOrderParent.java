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
import static com.danertu.dianping.MyOrderDetail.KEY_ORDER_ITEM;


public abstract class MyOrderParent extends Activity implements XListView.IXListViewListener {
    protected XListView lv_order;
    protected TextView orderNullText;
    protected Context context;
    public static ArrayList<HashMap<String, Object>> data2;

    public static final int REQ_PAY = 33;
    public static MyOrderAdapter adapter;
    private LocalBroadcastManager broadcastManager;
    private LoadOrderReceiver receiver;
    private DataChangerReceiver dataChangerReceiver;

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
        if (resultCode == REQ_PAY) {
            adapter.notifyDataSetChanged();
        }

        /**
         * 更新列表
         */
        if (requestCode == REQUEST_ORDER_DETAIL) {
            adapter.notifyDataSetChanged();
        }

        Logger.e(getClass().getSimpleName(), "接收到数据变化");
//        switch (requestCode) {
//            case REQUEST_ORDER_DETAIL:
//                Logger.e(getClass().getSimpleName(),"接收到数据变化===2");
//                Bundle bundle = data.getBundleExtra("data");
////                Bundle bundle = data.getExtras();
//
//                String orderNumber = data.getStringExtra("orderNumber");
//                HashMap<String, Object> item = (HashMap<String, Object>) bundle.getSerializable(KEY_ORDER_ITEM);
//                Logger.e(getClass().getSimpleName(),item.toString());
//                /**
//                 * 更新全部列表
//                 */
//                int allSize = MyOrderListAllActivity.data2.size();
//                for (int i = 0; i < allSize; i++) {
//                    HashMap<String, Object> orderItem = MyOrderListAllActivity.data2.get(i);
//                    if (orderItem.get("order_orderNumber").toString().equals(orderNumber)) {
//                        MyOrderListAllActivity.data2.remove(i);
//                        MyOrderListAllActivity.data2.add(i, item);
//                        MyOrderListAllActivity.adapter.notifyDataSetChanged();
//                        break;
//                    }
//                }
//
//                String oResult = item.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
//                String sResult = item.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
//                String pResult = item.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();
//
//                if (oResult.equals("1")) {
//                    if (pResult.equals("0")) {// 付款状态为 未付款
//                        for(HashMap<String,Object> orderItem:MyOrderNoPayActivity.data2){
//                            if (orderItem.get("order_orderNumber").toString().equals(orderNumber)) {
//                                MyOrderNoPayActivity.data2.remove(orderItem);
//                                break;
//                            }
//                        }
//                        MyOrderNoPayActivity.data2.add(0, item);
//                        MyOrderNoPayActivity.adapter.notifyDataSetChanged();
//                    } else if (pResult.equals("2") && sResult.equals("0")) {// 已付款 ，未发货
//                        for(HashMap<String,Object> orderItem:MyOrderNoSendActivity.data2){
//                            if (orderItem.get("order_orderNumber").toString().equals(orderNumber)) {
//                                MyOrderNoSendActivity.data2.remove(orderItem);
//                                break;
//                            }
//                        }
//                        MyOrderNoSendActivity.data2.add(0, item);
//                        MyOrderNoSendActivity.adapter.notifyDataSetChanged();
//                    } else if (sResult.equals("1") && pResult.equals("2")) {// 已发货,买家待收货
//                        for(HashMap<String,Object> orderItem:MyOrderNoReceiveActivity.data2){
//                            if (orderItem.get("order_orderNumber").toString().equals(orderNumber)) {
//                                MyOrderNoReceiveActivity.data2.remove(orderItem);
//                                break;
//                            }
//                        }
//                        MyOrderNoReceiveActivity.data2.add(0, item);
//                        MyOrderNoReceiveActivity.adapter.notifyDataSetChanged();
//                    }
//                } else if (oResult.equals("5")) {// 已完成订单,可以评论多次
//                    for(HashMap<String,Object> orderItem:MyOrderNoCommentActivity.data2){
//                        if (orderItem.get("order_orderNumber").toString().equals(orderNumber)) {
//                            MyOrderNoCommentActivity.data2.remove(orderItem);
//                            break;
//                        }
//                    }
//                    MyOrderNoCommentActivity.data2.add(0, item);
//                    MyOrderNoCommentActivity.adapter.notifyDataSetChanged();
//                }
//                break;
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
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
                Logger.e("DataChangerReceiver", " DataChangerReceiver 接收到数据变化");
                adapter.notifyDataSetChanged();
            }
        }
    }

}