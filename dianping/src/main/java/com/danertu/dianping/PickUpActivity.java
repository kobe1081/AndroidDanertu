package com.danertu.dianping;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.TokenExceptionBean;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.MD5Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import wl.codelibrary.widget.CircleImageView;

import static com.danertu.dianping.PaymentCenterActivity.REQ_ADDRESS;
import static com.danertu.dianping.StockpileActivity.KEY_PAGE_POSITION;
import static com.danertu.dianping.StockpileActivity.POSITION_PAGE_ORDER;

/**
 * 2017年11月15日
 *
 * @author huangyueliang
 * 提货页面
 */
public class PickUpActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_goods;//商品图片
    private TextView tvPickUp;//提货按钮
    private TextView tv_receiver_name;//收货人姓名
    private TextView tv_receiver_phone;//收货人号码
    private TextView tv_receiver_address;//收货人地址
    private ImageButton ib_select_address;//选址地址按钮
    private LinearLayout ll_contact;//收货人栏
    private RelativeLayout rl_product;
    private TextView tv_product_name;//商品名
    private TextView tv_shop_price;//拿货价
    private TextView tv_market_price;//市场价
    private TextView tv_stock_count;//库存数量
    private TextView tv_pick_up_info;//提示信息---共x件商品，合计
    private TextView tv_total_price;//订单总价
    private ImageView iv_reduce;//数量-
    private ImageView iv_add;//数量+
    private EditText et_pick_up_count;//提货数量
    private EditText et_remark;//留言

    private String uid;
    private String guid;
    private String img;
    private String shopPrice;
    private String marketPrice;
    private String totalPrice;
    private String stockCount;
    private String productGuid;
    private String productName;

    PopupWindow popupWindow;

    public static final int RESULT_PICK_UP_SUCCESS = 222;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        uid = getUid();
        setContentView(R.layout.activity_pick_up);
        setSystemBarWhite();
        findViewById();
        initView();
        initData();
    }

    private void initData() {
        /*
        intent.putExtra("guid",bean.getGuid());
        intent.putExtra("productGuid",bean.getProductGuid());
        intent.putExtra("img",bean.getSmallImage());
        intent.putExtra("shopPrice",bean.getShopPrice());
        intent.putExtra("marketPrice",bean.getMarketPrice());
        intent.putExtra("totalPrice",bean.getTotalPrice());
        intent.putExtra("stockCount",bean.getStockRemain());
         */
        showLoadDialog();
        Cursor cursor = db.GetDefaultAddress(this, db.GetLoginUid(context));
        initDefaultContactMsg(cursor);

        Intent intent = getIntent();
        guid = intent.getStringExtra("guid");
        productGuid = intent.getStringExtra("productGuid");
        img = intent.getStringExtra("img");
        productName = intent.getStringExtra("productName");
        shopPrice = intent.getStringExtra("shopPrice");
        marketPrice = intent.getStringExtra("marketPrice");
        totalPrice = intent.getStringExtra("totalPrice");
        stockCount = intent.getStringExtra("stockCount");
        ImageLoader.getInstance().displayImage(getStockSmallImgPath(img), iv_goods);
        tv_product_name.setText(productName);
        tv_shop_price.setText("￥" + shopPrice);
        tv_market_price.setText("￥" + marketPrice);
        tv_stock_count.setText(stockCount);
        tv_pick_up_info.setText("共1件商品，合计");
        tv_total_price.setText("￥" + shopPrice);
        hideLoadDialog();
    }

    /**
     * 实例化默认联系方式、地址
     *
     * @param cursor 地址游标
     * @see : 若无默认地址，则取最后一个为收货地址
     */
    private void initDefaultContactMsg(Cursor cursor) {
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int index_name = cursor.getColumnIndex("name");
            int index_mobile = cursor.getColumnIndex("mobile");
            int index_address = cursor.getColumnIndex("adress");
            int index_default_tag = cursor.getColumnIndex("IsDefault");
            String recName = cursor.getString(index_name);
            String recMobile = cursor.getString(index_mobile);
            String recAddress = cursor.getString(index_address);
            String defaultTag = cursor.getString(index_default_tag);
            recMobile = Constants.testedMobile == null ? recMobile : Constants.testedMobile;
            tv_receiver_name.setText(recName);
            tv_receiver_address.setText(recAddress);
            tv_receiver_phone.setText(recMobile);
            if (defaultTag.equals("1")) {
                break;
            }
        }
    }

    @Override
    protected void findViewById() {
        tvPickUp = $(R.id.tv_pick_up);
        iv_goods = $(R.id.iv_goods);
        tv_receiver_name = $(R.id.tv_payCenter_recName);
        tv_receiver_phone = $(R.id.tv_payCenter_recMobile);
        tv_receiver_address = $(R.id.tv_payCenter_recAddress);
        ib_select_address = $(R.id.ib_toSelectAddress);

        ll_contact = $(R.id.ll_contact);
        rl_product = $(R.id.rl_product);
        tv_product_name = $(R.id.tv_product_name);
        tv_shop_price = $(R.id.tv_shop_price);
        tv_market_price = $(R.id.tv_market_price);
        tv_pick_up_info = $(R.id.tv_pick_up_info);
        tv_stock_count = $(R.id.tv_stock_count);
        tv_total_price = $(R.id.tv_total_price);
        iv_reduce = $(R.id.iv_reduce);
        iv_add = $(R.id.iv_add);
        et_pick_up_count = $(R.id.et_pick_up_count);
        et_remark = $(R.id.et_remark);
    }

    @Override
    protected void initView() {
        tvPickUp.setOnClickListener(this);
        iv_goods.setOnClickListener(this);
        ib_select_address.setOnClickListener(this);
        ll_contact.setOnClickListener(this);
        iv_reduce.setOnClickListener(this);
        iv_add.setOnClickListener(this);
        tv_market_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);//给原价加上删除线
        tv_market_price.getPaint().setAntiAlias(true);
        et_pick_up_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int sCount = Integer.parseInt(s.toString().trim());
                    if (sCount - 1 <= 0) {
                        jsShowMsg("至少要提货1一件");
                        return;
                    }
                    if (sCount + 1 > Integer.parseInt(stockCount)) {
                        jsShowMsg("提货数量不能超过当前库存");
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //提货
            case R.id.tv_pick_up:
                if (TextUtils.isEmpty(tv_receiver_address.getText().toString())) {
                    jsShowMsg("请添加收货地址");
                    return;
                }
                tvPickUp.setEnabled(false);
                showPopupWindow();
                break;
            //收货地址
            case R.id.ll_contact:
            case R.id.ib_toSelectAddress:
                toAddressActivity(v);
                break;
            //商品信息
            case R.id.rl_product:
                showLoadDialog();
                new GetProduct().execute("");
                break;
            //提货数量-
            case R.id.iv_reduce:
                int count = Integer.parseInt(et_pick_up_count.getText().toString());
                if (count - 1 <= 0) {
                    jsShowMsg("至少要提货1一件");
                    return;
                }
                --count;
                et_pick_up_count.setText(count + "");
                calculateTotalPrice();
                break;
            //提货数量+
            case R.id.iv_add:
                count = Integer.parseInt(et_pick_up_count.getText().toString());
                if (count + 1 > Integer.parseInt(stockCount)) {
                    jsShowMsg("提货数量不能超过当前库存");
                    return;
                }
                count++;
                et_pick_up_count.setText(count + "");
                calculateTotalPrice();
                break;

        }
    }

    /**
     * 获取后台拿货商品列表，拿出当前页面商品具体信息，跳转至当前页面商品的后台拿货页面
     */
    class GetProduct extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String info = "";
            LinkedHashMap<String, String> param = new LinkedHashMap<>();
            param.put("apiid", "0173");
            param.put("dateline", String.valueOf(System.currentTimeMillis()));
            param.put("memberid", uid);
            param.put("tid", uid);
            String json = appManager.doPost(param);
            Logger.e(TAG, json);
            try {
                JSONArray array = new JSONObject(json).getJSONArray("val");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    if (object.getString("Guid").equals(productGuid)) {
                        info = object.toString();
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return info;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            hideLoadDialog();
            if (TextUtils.isEmpty(s)) {
                jsShowMsg("数据有误");
                jsFinish();
                return;
            }
            String param = "pageName|sell_pro_detail.html,;info|" + s;
            jsStartActivity("ProductDetailWebPage", param);
        }
    }

    /**
     * 计算订单总价
     * 共x件商品，合计
     */
    private void calculateTotalPrice() {
        int pickupCount = Integer.parseInt(et_pick_up_count.getText().toString().trim());
        tv_pick_up_info.setText("共" + pickupCount + "件商品 合计");
        tv_total_price.setText("￥" + pickupCount * (Float.parseFloat(shopPrice)));
    }

    /**
     * 添加收货地址
     *
     * @param v
     */
    public void toAddressActivity(View v) {
        if (!isLoading()) {
//                CommonTools.showShortToast(context, "请选择收货地址以及收货信息");
            jsStartActivityForResult("AddressActivity", "manageAddress|true", REQ_ADDRESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQ_ADDRESS) {
            initData();
        }
    }

    /**
     * 验证密码
     * -1:支付密码错误
     * 0：提货失败
     * 1：提货成功
     */
    class ValidPayPW extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            int result = -1;
            String pswMD5 = MD5Util.MD5(strings[0]);
            String payPswMD5 = appManager.getPayPswMD5(getUid());
            boolean equals = pswMD5.equals(payPswMD5);
            if (equals) {
                //验证通过
                //提交订单
                LinkedHashMap<String, String> param = new LinkedHashMap<>();
                param.put("address", tv_receiver_address.getText().toString());
                param.put("apiid", "0327");
                param.put("buyNumber", et_pick_up_count.getText().toString());
                param.put("dateline", String.valueOf(System.currentTimeMillis()));
                param.put("memLoginId", uid);
                param.put("mobile", tv_receiver_phone.getText().toString());
                param.put("name", tv_receiver_name.getText().toString());
                param.put("remark", et_remark.getText().toString());
                param.put("tid", uid);
                param.put("wareHouseGuid", guid);
                final String json = appManager.doPost(param);
                Logger.e(TAG, "提货结果：" + json);

                if (judgeIsTokenException(json)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                TokenExceptionBean tokenExceptionBean = com.alibaba.fastjson.JSONObject.parseObject(json, TokenExceptionBean.class);
                                jsShowMsg(tokenExceptionBean.getInfo());
                                quitAccount();
                                finish();
                                jsStartActivity("LoginActivity", "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    result = 0;
                } else {
                    //{"result":"true","info":"提货成功"}
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        result = jsonObject.getString("result").equals("true") ? 1 : 0;
                    } catch (JSONException e) {
                        result = 0;
                        e.printStackTrace();
                    }
                }

            } else {
                result = -1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case -1:
                    //支付密码验证失败
                    jsShowMsg("支付密码输入错误，请重新输入");
                    popupWindow.getContentView().findViewById(R.id.tv_confirm).setEnabled(true);
                    hideLoadDialog();
                    break;
                case 0:
                    jsShowMsg("提货失败");
                    popupWindow.getContentView().findViewById(R.id.tv_confirm).setEnabled(true);
                    hideLoadDialog();
                    break;
                case 1:
                    jsShowMsg("提货成功");
                    popupWindow.dismiss();
//                    Intent intent = new Intent();
//                    intent.putExtra("count", et_pick_up_count.getText().toString());
//                    setResult(RESULT_PICK_UP_SUCCESS, intent);
                    hideLoadDialog();
                    toStockOrder();
                    jsFinish();
                    break;
            }
        }
    }

    /**
     * 跳转去仓库订单
     */
    private void toStockOrder() {
        Intent intent1 = new Intent(context, StockpileActivity.class);
        intent1.putExtra(KEY_PAGE_POSITION, POSITION_PAGE_ORDER);
        startActivity(intent1);
    }

    /**
     * 输入支付密码进行提货
     * popupWindow
     */
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_pick_up, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
        ImageView iv_close = (ImageView) contentView.findViewById(R.id.iv_close);
        TextView tv_product_name = (TextView) contentView.findViewById(R.id.tv_product_name);
        CircleImageView civ_product = (CircleImageView) contentView.findViewById(R.id.civ_product);
        TextView tv_pick_up_count = (TextView) contentView.findViewById(R.id.tv_pick_up_count);
        final EditText et_pay_pw = (EditText) contentView.findViewById(R.id.et_pay_pw);
        final TextView tv_confirm = (TextView) contentView.findViewById(R.id.tv_confirm);

        tv_product_name.setText(productName);
        ImageLoader.getInstance().displayImage(getStockSmallImgPath(img), civ_product);
        tv_pick_up_count.setText(et_pick_up_count.getText().toString());

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadDialog();
                String payPW = et_pay_pw.getText().toString();
                tv_confirm.setEnabled(false);
                new ValidPayPW().execute(payPW);
            }
        });

        View view = contentView.findViewById(R.id.view);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPickUp.setEnabled(true);
                popupWindow.dismiss();
                hideLoadDialog();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPickUp.setEnabled(true);
                popupWindow.dismiss();
                hideLoadDialog();
            }
        });

        //显示PopupWindow
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) PickUpActivity.this.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
//                tvPickUp.setEnabled(true);
                hideLoadDialog();
                return;
            }
        }
        super.onBackPressed();
    }
}
