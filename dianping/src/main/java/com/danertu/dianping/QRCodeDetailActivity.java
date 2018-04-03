package com.danertu.dianping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Map;

public class QRCodeDetailActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_back;
    private TextView tv_title;
    private ImageView iv_product;
    private TextView tv_shop_price;
    private TextView tv_market_price;
    private TextView tv_buy_count;
    private TextView tv_product_name;
    private TextView tv_sub_name;
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
        iv_product = $(R.id.iv_product);
        tv_shop_price = $(R.id.tv_shop_price);
        tv_market_price = $(R.id.tv_market_price);
        tv_buy_count = $(R.id.tv_buy_count);
        tv_product_name = $(R.id.tv_product_name);
        tv_sub_name = $(R.id.tv_sub_name);
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
        setResult(RESULT_QR_CODE);
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
                OrderBody.OrderproductlistBean.OrderproductbeanBean bean = orderBody.getOrderproductlist().getOrderproductbean().get(0);
                ImageLoader.getInstance().displayImage(getImgUrl(bean.getSmallImage(), bean.getAgentID(), bean.getSupplierLoginID()), iv_product);
                tv_product_name.setText(bean.getName());
                tv_shop_price.setText("￥" + bean.getShopPrice());
                tv_market_price.setText("￥" + bean.getMarketPrice());
                tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//给原价加上删除线
                tv_market_price.getPaint().setAntiAlias(true);
                tv_buy_count.setText("x" + bean.getBuyNumber());
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

                Logger.e(TAG,"oderStatus="+orderStatus+",shipmentStatus="+shipmentStatus+",paymentStatus="+paymentStatus);
                //(OderStatus==0 || OderStatus==1 )&&ShipmentStatus==0&&PaymentStatus==2时说明未使用
                if ((orderStatus.equals("0")||orderStatus.equals("1"))&&shipmentStatus.equals("0")&&paymentStatus.equals("2")){
                    //已支付，未使用
                    tv_use_state.setText("未使用");
                    tv_use_state.setVisibility(View.VISIBLE);
                    hideLoadDialog();
                }else if ((orderStatus.equals("0")||orderStatus.equals("1"))&&shipmentStatus.equals("0")&&paymentStatus.equals("0")){
                    //未支付（即认为到付），未使用
                    tv_use_state.setText("未使用");
                    tv_use_state.setVisibility(View.VISIBLE);
                    hideLoadDialog();
                }else {
                    new GetOrderRecord().execute(orderNumber);
                }

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

    /**
     * 获取订单的操作记录
     * apiid  QYHX_0004
     */
    class GetOrderRecord extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            Map<String, String> map = new HashMap<>();
            map.put("apiid", "QYHX_0004");
            map.put("orderNumber", orderNumber);
            return AppManager.getInstance().doPost(map);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoadDialog();
            HistoryRecordBean recordBean = gson.fromJson(result, HistoryRecordBean.class);
            if (recordBean == null) {
                jsShowMsg("获取券码使用状态失败");
                if (Constants.isDebug) {
                    Logger.e(TAG, "接口 QYHX_0004接口返回的数据为：" + result);
                }
                finish();
                return;
            }
            if (recordBean.getVal().size() == 0) {
                //旧订单没有核销平台的记录，所以需要判断是否已经使用过并提示
                if (paymentStatus.equals("2")&&orderStatus.equals("5")&&(shipmentStatus.equals("1")||shipmentStatus.equals("2"))){
                    tv_use_state.setVisibility(View.VISIBLE);
                    tv_use_state.setText("已使用");
                    return;
                }

                //记录为空，未使用
                if (Constants.isDebug){
                    jsShowMsg("QYHX_0004接口获取到的数据列表size为0");
                }else {
                    jsShowMsg("获取券码使用状态失败");
                }
//                finish();
            } else {
                String createTime = recordBean.getVal().get(0).getCreateTime();
                tv_use_state.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(createTime)) {
                    tv_use_state.setText("已使用");
                } else {
                    //不为空，已使用
                    tv_use_state.setText("已使用（" + createTime.split(" ")[0].replace("/", ".") + ")");
                }
            }
        }
    }
}
