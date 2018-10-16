package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WaitPayOrderActivity extends BaseActivity implements OnClickListener {

    TextView head_back;

    private ListView listview, my_order_listAll;
    public MyAdapter1 adapter1;
    public MyAdapter2 adapter2;
    private TextView my_order_month, my_order_all;
    private ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> data2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("test", "WaitPayOrderActivity onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_pay_order);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        head_back = (TextView) findViewById(R.id.head_back_text);
        head_back.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.my_order_list);
        my_order_listAll = (ListView) findViewById(R.id.my_order_listAll);
        my_order_month = (TextView) findViewById(R.id.my_order_month);
        my_order_all = (TextView) findViewById(R.id.my_order_all);
    }

    @Override
    protected void initView() {
        try {

            my_order_month.setOnClickListener(this);
            my_order_all.setOnClickListener(this);

            tGetMonthOrder.start();
            // try {
            // tGetUserOrderHead.join();
            //
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            tGetAllOrder.start();
            // try {
            // tGetUserOrderHeadAll.join();
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }

            adapter1 = new MyAdapter1();
            listview.setAdapter(adapter1);

            adapter2 = new MyAdapter2();
            my_order_listAll.setAdapter(adapter2);

        } catch (Exception ex) {
            ex.printStackTrace();
            // String s = "123";
            // CommonTools.showShortToast(MyOrderActivity.this, "123");
        }

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                String orderNo = data1.get(position).get("order_orderNumber").toString();
                Intent intent = new Intent();
                intent.setClassName(getApplicationContext(), "com.danertu.dianping.OrderDetailsActivity");
                // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                Bundle b = new Bundle();
                b.putString("ordernumber", orderNo);
                // 此处使用putExtras，接受方就响应的使用getExtra
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }

        });
    }

    private Handler HandlerListAll = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            adapter1.notifyDataSetChanged();
            super.handleMessage(msg);

        }

    };

    private Handler HandlerListMonth = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            adapter2.notifyDataSetChanged();
            super.handleMessage(msg);

        }

    };

    private Thread tGetMonthOrder = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            data1 = new ArrayList<>();
            String result = AppManager.getInstance().postGetWaitPayOrder("0055", db.GetLoginUid(WaitPayOrderActivity.this), "1");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("orderlist");
                JSONArray jsonArray = jsonObject.getJSONArray("orderbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("order_orderNumber", oj.getString("OrderNumber"));
                    item.put("order_OderStatus", oj.getString("OderStatus"));
                    item.put("order_ShipmentStatus", oj.getString("ShipmentStatus"));
                    item.put("order_PaymentStatus", oj.getString("PaymentStatus"));
                    item.put("order_ShouldPayPrice", oj.getString("ShouldPayPrice"));
                    item.put("order_CreateTime", oj.getString("CreateTime"));
                    item.put("order_PaymentName", oj.getString("PaymentName"));
                    data1.add(item);
                }
            } catch (Exception e) {
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
                if (Constants.isDebug)
                    e.printStackTrace();
            }
            Message msg = new Message();
            WaitPayOrderActivity.this.HandlerListMonth.sendMessage(msg);

        }

    });

    //所有代付款订单
    private Thread tGetAllOrder = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            data2 = new ArrayList<>();
            String result = AppManager.getInstance().postGetUserOrderHead("0055", db.GetLoginUid(WaitPayOrderActivity.this), "2");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("orderlist");
                JSONArray jsonArray = jsonObject.getJSONArray("orderbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("order_orderNumber", oj.getString("OrderNumber"));
                    item.put("order_OderStatus", oj.getString("OderStatus"));
                    item.put("order_ShipmentStatus", oj.getString("ShipmentStatus"));
                    item.put("order_PaymentStatus", oj.getString("PaymentStatus"));
                    item.put("order_ShouldPayPrice", oj.getString("ShouldPayPrice"));
                    item.put("order_CreateTime", oj.getString("CreateTime"));
                    item.put("order_PaymentName", oj.getString("PaymentName"));
                    data2.add(item);
                }
            } catch (Exception e) {
                judgeIsTokenException(result, "您的登录信息已过期，请重新登录", -1);
                e.printStackTrace();
            }
            Message msg = new Message();
            WaitPayOrderActivity.this.HandlerListAll.sendMessage(msg);

        }

    });

    private class MyAdapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return data1.size();
        }

        @Override
        public Object getItem(int position) {
            return data1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            String oderStatus = "";
            String shipmentStatus = "";
            String paymentStatus = "";
            String oResult = data1.get(position).get("order_OderStatus").toString();
            switch (oResult) {
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
            }

            String sResult = data1.get(position).get("order_ShipmentStatus").toString();
            switch (sResult) {
                case ("0"):
                    shipmentStatus = "未发货";
                    break;
                case ("1"):
                    shipmentStatus = "已发货";
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
            }

            String pResult = data1.get(position).get("order_PaymentStatus").toString();
            switch (pResult) {
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
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(WaitPayOrderActivity.this).inflate(R.layout.wait_pay_order_item, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((Holder) convertView.getTag());
            }

            holder.orderId_text.setText(data1.get(position).get("order_orderNumber").toString());
            holder.orderPrice_text.setText(data1.get(position).get("order_ShouldPayPrice").toString());
            holder.orderTime_text.setText(data1.get(position).get("order_CreateTime").toString());
            holder.orderState_text.setText(oderStatus + " " + shipmentStatus + " " + paymentStatus);
            holder.orderPayment_text.setText(data1.get(position).get("order_PaymentName").toString());
            return convertView;
        }

        class Holder {
            TextView orderId_text;
            TextView orderPrice_text;
            TextView orderTime_text;
            TextView orderState_text;
            TextView orderPayment_text;

            Holder(View view) {
                orderId_text = (TextView) view.findViewById(R.id.orderId_text);
                orderPrice_text = (TextView) view.findViewById(R.id.orderPrice_text);
                orderTime_text = (TextView) view.findViewById(R.id.orderTime_text);
                orderState_text = (TextView) view.findViewById(R.id.orderState_text);
                orderPayment_text = (TextView) view.findViewById(R.id.orderPayment_text);
            }
        }
    }

    private class MyAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return data2.size();
        }

        @Override
        public Object getItem(int position) {
            return data2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            String oderStatus = "";
            String shipmentStatus = "";
            String paymentStatus = "";
            String oResult = data2.get(position).get("order_OderStatus").toString();
            switch (oResult) {
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
            }

            String sResult = data2.get(position).get("order_ShipmentStatus").toString();
            switch (sResult) {
                case ("0"):
                    shipmentStatus = "未发货";
                    break;
                case ("1"):
                    shipmentStatus = "已发货";
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
            }

            String pResult = data2.get(position).get("order_PaymentStatus").toString();
            switch (pResult) {
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
            }
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(WaitPayOrderActivity.this).inflate(R.layout.wait_pay_order_item, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((Holder) convertView.getTag());
            }
            holder.orderId_text.setText(data2.get(position).get("order_orderNumber").toString());
            holder.orderPrice_text.setText(data2.get(position).get("order_ShouldPayPrice").toString());
            holder.orderTime_text.setText(data2.get(position).get("order_CreateTime").toString());
            holder.orderState_text.setText(oderStatus + " " + shipmentStatus + " " + paymentStatus);
            holder.orderPayment_text.setText(data2.get(position).get("order_PaymentName").toString());
            return convertView;
        }

        class Holder {
            TextView orderId_text;
            TextView orderPrice_text;
            TextView orderTime_text;
            TextView orderState_text;
            TextView orderPayment_text;

            Holder(View view) {
                orderId_text = (TextView) view.findViewById(R.id.orderId_text);
                orderPrice_text = (TextView) view.findViewById(R.id.orderPrice_text);
                orderTime_text = (TextView) view.findViewById(R.id.orderTime_text);
                orderState_text = (TextView) view.findViewById(R.id.orderState_text);
                orderPayment_text = (TextView) view.findViewById(R.id.orderPayment_text);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_back_text:
                WaitPayOrderActivity.this.finish();
                break;
            case R.id.my_order_all:
                my_order_month.setBackgroundResource(R.drawable.segment_normal_2_bg);
                my_order_all.setBackgroundResource(R.drawable.segment_selected_1_bg);
                if (Build.VERSION.SDK_INT < 23) {
                    my_order_month.setTextAppearance(WaitPayOrderActivity.this, R.style.style_13_4B4B4B_BOLD);
                    my_order_all.setTextAppearance(WaitPayOrderActivity.this, R.style.style_13_FFFFFF_BOLD);
                } else {
                    my_order_month.setTextAppearance(R.style.style_13_4B4B4B_BOLD);
                    my_order_all.setTextAppearance(R.style.style_13_FFFFFF_BOLD);
                }
                // tGetUserOrderHeadAll.start();
                // try {
                // tGetUserOrderHeadAll.join();
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                listview.setVisibility(TextView.GONE);
                my_order_listAll.setVisibility(TextView.VISIBLE);
                break;
            case R.id.my_order_month:
                my_order_month.setBackgroundResource(R.drawable.segment_selected_1_bg);
                my_order_all.setBackgroundResource(R.drawable.segment_normal_2_bg);
                if (Build.VERSION.SDK_INT < 23) {
                    my_order_month.setTextAppearance(WaitPayOrderActivity.this, R.style.style_13_FFFFFF_BOLD);
                    my_order_all.setTextAppearance(WaitPayOrderActivity.this, R.style.style_13_4B4B4B_BOLD);
                } else {
                    my_order_month.setTextAppearance(R.style.style_13_FFFFFF_BOLD);
                    my_order_all.setTextAppearance(R.style.style_13_4B4B4B_BOLD);
                }
                listview.setVisibility(TextView.VISIBLE);
                my_order_listAll.setVisibility(TextView.GONE);
                break;
            default:
                break;
        }
    }

}
