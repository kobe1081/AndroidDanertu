package com.danertu.dianping;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.config.Constants;
import com.danertu.entity.OrderBody;
import com.danertu.entity.OrderHead;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.Logger;
import com.danertu.tools.QRCodeUtils;
import com.danertu.widget.CommonTools;
import com.google.gson.JsonSyntaxException;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.File;
import java.io.OutputStream;
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
    private TextView tv_save_qrcode;
    private TextView tv_tips;
    private LinearLayout ll_qr_code;
    private Context context;
    public static final int RESULT_QR_CODE = 21;
    private String orderNumber;
    private int position;
    private String orderStatus;
    private String shipmentStatus;
    private String paymentStatus;
    private String saveDirName;
    private String saveImgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrconde_detail);
        setSystemBarWhite();
        setSwipeBackEnable(true);
        getStoragePermission();
        findViewById();
        initView();
        initData();
        setRes();
    }

    private void initData() {
        context = this;
        Intent intent = getIntent();
        orderNumber = intent.getStringExtra("orderNumber");
        position = intent.getIntExtra("position", -1);
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
            Bitmap qrCode = QRCodeUtils.createQRCode(orderNumber, CommonTools.dip2px(context, 200));
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
//            Bitmap qrCode = QRCodeUtils.createQRImage(orderNumber, CommonTools.dip2px(context, 200), bitmap, true);
            iv_qr_code.setImageBitmap(qrCode);
        } catch (Exception e) {
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
        tv_save_qrcode = $(R.id.tv_save_qrcode);
        ll_qr_code = $(R.id.ll_qr_code);
        tv_tips=$(R.id.tv_tips);
        tv_save_qrcode.setOnClickListener(this);
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
            case R.id.tv_save_qrcode:
                /**
                 * 保存二维码到手机上
                 */
                saveDirName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "Danertu";
                saveImgName = "Danertu_" + DateTimeUtils.getDateToyyyyMMddHHmm() + "_" + orderNumber + ".png";
                File saveFile = new File(saveDirName);
                if (!saveFile.exists()) {
                    saveFile.mkdir();
                }
                try {
                    int width = ll_qr_code.getWidth();
                    int height = ll_qr_code.getHeight();
                    //打开图像缓存
                    ll_qr_code.setDrawingCacheEnabled(true);
                    //测量Linearlayout的大小
                    ll_qr_code.measure(0, 0);
                    width = ll_qr_code.getMeasuredWidth();
                    height = ll_qr_code.getMeasuredHeight();
                    //发送位置和尺寸到LienarLayout及其所有的子View
                    //简单地说，就是我们截取的屏幕区域，注意是以Linearlayout左上角为基准的，而不是屏幕左上角
                    ll_qr_code.layout(0, 0, width, height);
                    //拿到截取图像的bitmap
                    Bitmap drawingCache = ll_qr_code.getDrawingCache();
//                    File file = new File(saveDirName + File.separator + saveImgName);
//                    FileOutputStream fos2 = new FileOutputStream(file);
//                    drawingCache.compress(Bitmap.CompressFormat.PNG, 90, fos2);
//                    fos2.flush();
//                    fos2.close();
                    File file = new File(saveDirName + File.separator + saveImgName);
                    if (file.exists()) {
                        jsShowMsg("二维码已保存");
                        return;
                    }
                    // 系统时间
                    long currentTimeMillis = System.currentTimeMillis();
                    long dateSeconds = currentTimeMillis / 1000;
                    // 保存截屏到系统MediaStore
                    ContentValues values = new ContentValues();
                    ContentResolver resolver = context.getContentResolver();
                    values.put(MediaStore.Images.ImageColumns.DATA, saveDirName + File.separator + saveImgName);
                    values.put(MediaStore.Images.ImageColumns.TITLE, saveImgName);
                    values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, saveImgName);
                    values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, currentTimeMillis);
                    values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
                    values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
                    values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
                    values.put(MediaStore.Images.ImageColumns.WIDTH, drawingCache.getWidth());
                    values.put(MediaStore.Images.ImageColumns.HEIGHT, drawingCache.getHeight());
                    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    try {
                        OutputStream out = resolver.openOutputStream(uri);
                        drawingCache.compress(Bitmap.CompressFormat.PNG, 100, out);// bitmap转换成输出流，写入文件
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // update file size in the database
                    values.clear();
                    values.put(MediaStore.Images.ImageColumns.SIZE, new File(saveDirName + File.separator + saveImgName).length());
                    resolver.update(uri, values, null, null);
                    jsShowMsg("订单二维码保存成功");
                } catch (Exception e) {
                    jsShowMsg("订单二维码保存失败，请重试");
                    //保存失败，可能是没有存储权限引起，所以重新请求存储权限
                    getStoragePermission();
                    e.printStackTrace();
                }
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
        intent.putExtra("position", position);
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
            return appManager.postGetOrderBody(orderNumber, getUid());
        }

        @Override
        protected void onPostExecute(final String result) {
            judgeIsTokenException(result, new TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, String info) {
                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                    jsShowMsg(info);
//                    quitAccount();
//                    finish();
//                    jsStartActivity("LoginActivity", "");
                }

                @Override
                public void ok() {
                    try {
                        OrderBody orderBody = JSONObject.parseObject(result, OrderBody.class);
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
                            ll_order_item.addView(productItem, 0);
                        }

                    } catch (Exception e) {
                        jsShowMsg("出错了");
                        if (Constants.isDebug) {
                            jsShowMsg("获取订单体时出现错误");
                        }
                        finish();
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    /**
     * 获取订单头
     */
    class GetOrderHead extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... param) {
            String orderNumber = param[0];
            return appManager.postGetOrderHead(orderNumber, getUid());
        }

        @Override
        protected void onPostExecute(final String result) {
            judgeIsTokenException(result, new TokenExceptionCallBack() {
                @Override
                public void tokenException(String code, String info) {
                    sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                    jsShowMsg(info);
//                    quitAccount();
//                    finish();
//                    jsStartActivity("LoginActivity", "");
                }

                @Override
                public void ok() {
                    try {
                        OrderHead orderHead = JSONObject.parseObject(result, OrderHead.class);
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
                            tv_tips.setVisibility(View.VISIBLE);
                            hideLoadDialog();
                        } else if ((orderStatus.equals("0") || orderStatus.equals("1")) && shipmentStatus.equals("0") && paymentStatus.equals("0")) {
                            //未支付（即认为到付），未使用
                            tv_use_state.setText("未使用");
                            tv_use_state.setVisibility(View.VISIBLE);
                            tv_tips.setVisibility(View.VISIBLE);
                            hideLoadDialog();
                        } else if ((orderStatus.equals("5") && paymentStatus.equals("2")) || (shipmentStatus.equals("1") && paymentStatus.equals("2"))) {
                            tv_use_state.setText("已使用（" + bean.getDispatchTime().split(" ")[0].replace("/", ".") + ")");
                            tv_use_state.setVisibility(View.VISIBLE);
                            tv_tips.setVisibility(View.GONE);
                        } else {
                            tv_use_state.setText("状态未知");
                            tv_tips.setVisibility(View.GONE);
                        }
                        hideLoadDialog();
                    } catch (Exception e) {
                        jsShowMsg("出错了");
                        if (Constants.isDebug) {
                            jsShowMsg("获取订单头时出现错误");
                        }
                        finish();
                        e.printStackTrace();
                    }
                }
            });

        }
    }

}
