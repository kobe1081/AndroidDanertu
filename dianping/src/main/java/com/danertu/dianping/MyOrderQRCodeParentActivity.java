package com.danertu.dianping;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.adapter.MyOrderAdapter;
import com.danertu.adapter.MyOrderQRCodeAdapter;
import com.danertu.entity.MyOrderData;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.tools.Logger;
import com.danertu.widget.XListView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.danertu.adapter.MyOrderAdapter.REQUEST_ORDER_DETAIL;
import static com.danertu.adapter.MyOrderQRCodeAdapter.REQUEST_QRCODE_NEW;


public abstract class MyOrderQRCodeParentActivity extends BaseActivity implements XListView.IXListViewListener {
    protected XListView lv_order;
    protected TextView orderNullText;
    protected Context context;
    public static ArrayList<HashMap<String, Object>> data2;

    public static final int REQ_PAY = 33;
    public static MyOrderQRCodeAdapter adapter;
    private LocalBroadcastManager broadcastManager;
    private LoadOrderReceiver receiver;
    private DataChangerReceiver dataChangerReceiver;
    private AdapterOnActivityResult adapterOnActivityResult;

    public static final int TAB_NO_USE = 0;
    public static final int TAB_COMPLETE = 1;


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
        if ((data2 == null || data2.size() == 0) && MyOrderDataQRCode.isFinish) {
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
            intentFilter.addAction(Constants.ORDER_DATA_ON_ACTIVITY_FOR_RESULT_QRCODE);
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
                    case REQUEST_QRCODE_NEW:
                        new MyOrderDataQRCode(MyOrderQRCodeParentActivity.this, true, orderNumber) {

                            @Override
                            public void getDataSuccess() {
                                HashMap<String, Object> orderBean = getItemOrder();
                                if (orderBean == null || orderBean.isEmpty()) {
                                    jsShowMsg("订单数据有误");
                                    finish();
                                    return;
                                }
                                boolean hasAllChange = false;
                                //------------------------------全部订单--------------------------------------------
                                for (HashMap<String, Object> orderItem : MyOrderDataQRCode.order_list_no_use) {
                                    String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                    //更新所有订单列表
                                    if (orderNumber.equals(order_orderNumber)) {
                                        for (String s : orderItem.keySet()) {
                                            orderItem.put(s, orderBean.get(s));
                                        }
                                        hasAllChange = true;
                                        Logger.e(TAG, "  order_list_no_use change");
                                        MyOrderNoUseActivity.adapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                                if (!hasAllChange)
                                    for (HashMap<String, Object> orderItem : MyOrderDataQRCode.order_no_use) {
                                        String order_orderNumber = orderItem.get("order_orderNumber").toString();
                                        //更新所有订单列表
                                        if (orderNumber.equals(order_orderNumber)) {

                                            for (String s : orderItem.keySet()) {
                                                orderItem.put(s, orderBean.get(s));
                                            }
                                            Logger.e(TAG, "  order_no_use change");
                                            break;
                                        }
                                    }
//------------------------------全部订单--------------------------------------------------
                                String orderStatus = orderBean.get(MyOrderData.ORDER_ORDERSTATUS_KEY).toString();
                                String shipmentStatus = orderBean.get(MyOrderData.ORDER_SHIPSTATUS_KEY).toString();
                                String payStatus = orderBean.get(MyOrderData.ORDER_PAYSTATUS_KEY).toString();
                                if (orderStatus.equals("5") && shipmentStatus.equals("2") && payStatus.equals("2")) {
                                    MyOrderDataQRCode.order_list_complete.add(0, orderBean);
                                    MyOrderQRCodeActivity.dataChanges[1] = true;
                                    MyOrderCompleteQRCodeActivity.adapter.notifyDataSetChanged();
                                }

                                Logger.e(TAG, "数据更新完毕");
                            }

                            @Override
                            public void needLogin() {
                                jsShowMsg("您的登录信息已过期，请重新登录");
                                quitAccount();
                                finish();
                                jsStartActivity("LoginActivity", "");
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