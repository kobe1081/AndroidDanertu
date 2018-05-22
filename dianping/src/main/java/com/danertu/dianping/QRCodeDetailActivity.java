package com.danertu.dianping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.HistoryRecordBean;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.tools.QRCodeUtils;
import com.danertu.widget.CommonTools;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.WriterException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_back;
    private TextView tv_title;
    private LinearLayout ll_order_item;
    private ImageView iv_qr_code;
    private TextView tv_use_state;
    private TextView tv_order_number;
    private Context context;
    public static final int RESULT_QR_CODE = 21;
    private String orderNumber;
    private String orderStatus;
    private String shipmentStatus;
    private String paymentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrconde_detail);
        setSystemBarWhite();
        setSwipeBackEnable(true);
        findViewById();
        initView();
        initData();
        setRes();
    }

    private void initData() {
        context = this;
        Intent intent = getIntent();
        orderNumber = intent.getStringExtra("orderNumber");
        if (TextUtils.isEmpty(orderNumber)) {
            if (Constants.isDebug)
                jsShowMsg("传递过来的订单号为空");
            jsShowMsg("数据出错");
            finish();
        }
        tv_order_number.setText("订单号：" + orderNumber);
        showLoadDialog();
        new GetOrderBody().execute(orderNumber);

        try {
            Bitmap qrCode = QRCodeUtils.createQRCode(orderNumber, CommonTools.dip2px(context, 170));
            iv_qr_code.setImageBitmap(qrCode);
        } catch (WriterException e) {
            jsShowMsg("出错了，请重试");
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void findViewById() {
        btn_back = $(R.id.b_title_back);
        tv_title = $(R.id.tv_title);
        ll_order_item = $(R.id.ll_order_item);
        iv_qr_code = $(R.id.iv_qr_code);
        tv_use_state = $(R.id.tv_use_state);
        tv_order_number = $(R.id.tv_order_number);
    }

    @Override
    protected void initView() {
        tv_title.setText("券码详细");
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        setRes();
    }

    public void setRes() {
        Intent intent = new Intent();
        intent.putExtra("orderNumber", orderNumber);
        setResult(RESULT_QR_CODE, intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 获取订单体
     */
    class GetOrderBody extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            Map<String, String> map = new HashMap<>();
            map.put("apiid", "0072");
            map.put("orderNumber", orderNumber);
            return AppManager.getInstance().doPost(map);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                OrderBody orderBody = gson.fromJson(result, OrderBody.class);
                if (orderBody == null) {
                    jsShowMsg("数据有误");
                    if (Constants.isDebug) {
                        Logger.e(TAG, "接口 0072接口返回的数据为：" + result);
                    }
                    finish();
                    return;
                }
                //通过获取订单体来得到订单的使用状态
                new GetOrderHead().execute(orderNumber);
                List<OrderBody.OrderproductlistBean.OrderproductbeanBean> beanList = orderBody.getOrderproductlist().getOrderproductbean();
                for (OrderBody.OrderproductlistBean.OrderproductbeanBean bean : beanList) {
                    View productItem = LayoutInflater.from(context).inflate(R.layout.item_qrcode_order, null, false);
                    ImageView iv_product = ((ImageView) productItem.findViewById(R.id.iv_product));
                    TextView tv_shop_price = ((TextView) productItem.findViewById(R.id.tv_shop_price));
                    TextView tv_market_price = ((TextView) productItem.findViewById(R.id.tv_market_price));
                    TextView tv_buy_count = ((TextView) productItem.findViewById(R.id.tv_buy_count));
                    TextView tv_product_name = ((TextView) productItem.findViewById(R.id.tv_product_name));
                    TextView tv_sub_name = ((TextView) productItem.findViewById(R.id.tv_sub_name));

                    ImageLoader.getInstance().displayImage(getImgUrl(bean.getSmallImage(), bean.getAgentID(), bean.getSupplierLoginID()), iv_product);
                    tv_product_name.setText(bean.getName());
                    tv_shop_price.setText("￥" + bean.getShopPrice());
                    tv_market_price.setText("￥" + bean.getMarketPrice());
                    tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//给原价加上删除线
                    tv_market_price.getPaint().setAntiAlias(true);
                    tv_buy_count.setText("x" + bean.getBuyNumber());
                    ll_order_item.addView(productItem);
                }

            } catch (JsonSyntaxException e) {
                jsShowMsg("出错了");
                if (Constants.isDebug) {
                    jsShowMsg("获取订单体时出现错误");
                }
                finish();
                e.printStackTrace();
            }

        }
    }

    /**
     * 获取订单头
     */
    class GetOrderHead extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            Map<String, String> map = new HashMap<>();
            map.put("apiid", "0036");
            map.put("orderNumber", orderNumber);
            return AppManager.getInstance().doPost(map);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                OrderHead orderHead = gson.fromJson(result, OrderHead.class);
                if (orderHead == null) {
                    jsShowMsg("数据有误");
                    if (Constants.isDebug) {
                        Logger.e(TAG, "接口 0036接口返回的数据为：" + result);
                    }
                    finish();
                    return;
                }
                OrderHead.OrderinfolistBean.OrderinfobeanBean bean = orderHead.getOrderinfolist().getOrderinfobean().get(0);
                shipmentStatus = bean.getShipmentStatus();
                paymentStatus = bean.getPaymentStatus();
                orderStatus = bean.getOderStatus();

                Logger.e(TAG, "oderStatus=" + orderStatus + ",shipmentStatus=" + shipmentStatus + ",paymentStatus=" + paymentStatus);
                //(OderStatus==0 || OderStatus==1 )&&ShipmentStatus==0&&PaymentStatus==2时说明未使用
                if ((orderStatus.equals("0") || orderStatus.equals("1")) && shipmentStatus.equals("0") && paymentStatus.equals("2")) {
                    //已支付，未使用
                    tv_use_state.setText("未使用");
                    tv_use_state.setVisibility(View.VISIBLE);
                    hideLoadDialog();
                } else if ((orderStatus.equals("0") || orderStatus.equals("1")) && shipmentStatus.equals("0") && paymentStatus.equals("0")) {
                    //未支付（即认为到付），未使用
                    tv_use_state.setText("未使用");
                    tv_use_state.setVisibility(View.VISIBLE);
                    hideLoadDialog();
                } else if ((orderStatus.equals("5") && paymentStatus.equals("2")) || (shipmentStatus.equals("1") && paymentStatus.equals("2"))) {
                    tv_use_state.setText("已使用（" + bean.getDispatchTime().split(" ")[0].replace("/", ".") + ")");
                    tv_use_state.setVisibility(View.VISIBLE);
                } else {
                    tv_use_state.setText("状态未知");
                }
                hideLoadDialog();
            } catch (JsonSyntaxException e) {
                jsShowMsg("出错了");
                if (Constants.isDebug) {
                    jsShowMsg("获取订单头时出现错误");
                }
                finish();
                e.printStackTrace();
            }

        }
    }

}
