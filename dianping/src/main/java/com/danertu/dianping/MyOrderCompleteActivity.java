package com.danertu.dianping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danertu.entity.MyOrderData;

public class MyOrderCompleteActivity extends BaseActivity implements OnClickListener {
    Button b_lookOrder;
    //	Button b_ok;
    Button title_back;
    //	TextView tv_orderNumber;
//	TextView tv_orderPrice;
//	TextView tv_orderPayWay;
    private LinearLayout parent;
    //订单号，订单金额，支付方式
    private String orderNumber, orderPrice, orderPayWay, orderPayTime;

    boolean isPayed = true;
    boolean isBooking = false;

    public static final String KEY_ORDER_NUMBER = "ordernumber";
    public static final String KEY_ORDER_PRICE = "orderprice";
    public static final String KEY_ORDER_PAYWAY = "orderpayway";
    public static final String KEY_ORDER_ISPAYED = "orderispayed";
    public static final String KEY_ORDER_BOOKING = "isBooking";
    public static final String KEY_ORDER_PAYTIME = "orderpaytime";

    public final String TITLE = "提交成功";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_order_complete);
        initIntentMsg();
        findViewById();
        initView();
    }

    private void initIntentMsg() {
        Intent intent = this.getIntent();
        orderNumber = intent.getStringExtra(KEY_ORDER_NUMBER);
        orderPrice = intent.getStringExtra(KEY_ORDER_PRICE);
        orderPayWay = intent.getStringExtra(KEY_ORDER_PAYWAY);
        isPayed = intent.getBooleanExtra(KEY_ORDER_ISPAYED, true);
        isBooking = intent.getBooleanExtra(KEY_ORDER_BOOKING, false);
        orderPayTime = intent.getStringExtra(KEY_ORDER_PAYTIME);
    }

    @Override
    protected void findViewById() {
        b_lookOrder = (Button) findViewById(R.id.b_order_check);
//		b_ok = (Button)findViewById(R.id.b_order_ok);
        title_back = (Button) findViewById(R.id.b_title_back4);
        TextView title = (TextView) findViewById(R.id.tv_title4);
        title.setText(TITLE);
        parent = (LinearLayout) findViewById(R.id.linearLayout1);
        b_lookOrder.setOnClickListener(this);
//		b_ok.setOnClickListener(this);
        title_back.setOnClickListener(this);
    }

    @Override
    protected void initView() {
        String[] keys = new String[]{getString(R.string.pay_orderNum_tips), "支付金额：", "付款日期：", "支付方式："};
        String[] values = new String[]{orderNumber, "￥" + orderPrice, orderPayTime, orderPayWay};
        int len = keys.length;
        for (int i = 0; i < len; i++) {
            String key = keys[i];
            String value = values[i];
            if (TextUtils.isEmpty(value))
                continue;
            View v = LayoutInflater.from(this).inflate(R.layout.item_submit_success, null);
            TextView tv_key = (TextView) v.findViewById(R.id.tv_keys);
            TextView tv_value = (TextView) v.findViewById(R.id.tv_values);
            tv_key.setText(key);
            tv_value.setText(value);
            parent.addView(v);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == b_lookOrder && !isLoading()) {
            if (isBooking) {
                Intent intent = new Intent(this, MyOrderActivity.class);
                intent.putExtra(MyOrderActivity.KEY_TABINDEX, -1);
                startActivity(intent);
                return;
            }
            myOrderData = new MyOrderData(this) {
                @Override
                public void getDataSuccess() {

                }
            };

            int index = 2;
            if (isPayed) {
                index = 2;//待发货
            } else {
                index = 1;//待付款
            }
            Intent intent = new Intent(getContext(), MyOrderActivity.class);
            intent.putExtra(MyOrderActivity.KEY_TABINDEX, index);
            startActivity(intent);

            finish();

        } else if (v == title_back) {
            finish();
        }
    }
}
