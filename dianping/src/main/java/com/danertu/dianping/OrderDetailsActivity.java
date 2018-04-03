package com.danertu.dianping;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.danertu.tools.AppManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


public class OrderDetailsActivity extends BaseActivity implements OnClickListener {

    private TextView head_back_text, textName, textMobile, text_Adress, textOrderNumber,
            textDetailState, textPayMemntName, textCreateTime, textDispatchName, textDispatchNumber, textInvoice, textInvoiceTitle, textRemark;
    private TextView textproductPrice, textPrice6;
    private String strNumber, result;

    private void initTitle(String string) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(string);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        initTitle("订单详情");
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
//		head_back_text = (TextView) findViewById(R.id.head_back_text);
        textName = (TextView) findViewById(R.id.textName);
        textMobile = (TextView) findViewById(R.id.textMobile);
        text_Adress = (TextView) findViewById(R.id.text_Adress);
        textOrderNumber = (TextView) findViewById(R.id.textOrderNumber);
        textDetailState = (TextView) findViewById(R.id.textDetailState);
        textPayMemntName = (TextView) findViewById(R.id.textPayMemntName);
        textCreateTime = (TextView) findViewById(R.id.textCreateTime);
        textDispatchName = (TextView) findViewById(R.id.textDispatchName);
        textDispatchNumber = (TextView) findViewById(R.id.textDispatchNumber);
        textInvoice = (TextView) findViewById(R.id.textInvoice);
        textInvoiceTitle = (TextView) findViewById(R.id.textInvoiceTitle);
        textRemark = (TextView) findViewById(R.id.textRemark);

        textproductPrice = (TextView) findViewById(R.id.textproductPrice);
        textPrice6 = (TextView) findViewById(R.id.textPrice6);
    }

    @Override
    protected void initView() {
//		head_back_text.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				Intent mIntent=new Intent(OrderDetailsActivity.this, MyOrderActivity.class);
//			    startActivity(mIntent);
//			}
//		});
        strNumber = getIntent().getExtras().getString("ordernumber");
        try {
            tGetOrderInfoShow.start();
            tGetOrderInfoShow.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BindData();
    }

    @Override
    public void onClick(View arg0) {

    }

    private Handler HandlerGetOrderInfoShow = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            //	bindListData();
            super.handleMessage(msg);

        }

    };


    private Thread tGetOrderInfoShow = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            try {
                result = AppManager.getInstance().postGetOrderInfoShow("0036", strNumber);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            SystemClock.sleep(1000);
            Message msg = new Message();
            OrderDetailsActivity.this.HandlerGetOrderInfoShow.sendMessage(msg);

        }

    });

    private void BindData() {
        if (result == null) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(result).getJSONObject("orderinfolist");
            JSONArray jsonArray = jsonObject.getJSONArray("orderinfobean");
            if (jsonArray.length() > 0) {
                String oderStatus = "";
                String shipmentStatus = "";
                String paymentStatus = "";

                JSONObject oj = jsonArray.getJSONObject(0);
//				Address addressEntity = new Address();
//            	HashMap<String, Object> item = new HashMap<>();
                textName.setText(oj.getString("Name"));
                textMobile.setText(oj.getString("Mobile"));
                text_Adress.setText(oj.getString("Address"));
                textOrderNumber.setText(oj.getString("OrderNumber"));

                String oResult = oj.getString("OderStatus");
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
                    default:
                        break;
                }

                String sResult = oj.getString("ShipmentStatus");
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
                    default:
                        break;
                }

                String pResult = oj.getString("PaymentStatus");
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
                    default:
                        break;
                }

                textDetailState.setText(oderStatus + " " + shipmentStatus + " " + paymentStatus);
                textPayMemntName.setText(oj.getString("PaymentName"));
                textCreateTime.setText(oj.getString("CreateTime"));
                textDispatchName.setText(oj.getString("DispatchModeName"));
                textDispatchNumber.setText(oj.getString("ShipmentNumber"));
                textInvoiceTitle.setText(oj.getString("InvoiceTitle"));
                if (oj.getString("InvoiceTitle").equals("")) {
                    textInvoice.setText("否");
                } else {
                    textInvoice.setText("是");
                }
                textRemark.setText(oj.getString("ClientToSellerMsg"));
                textproductPrice.setText(oj.getString("ShouldPayPrice"));
                textPrice6.setText(oj.getString("ShouldPayPrice"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
