package com.danertu.dianping;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.entity.ProductInRecordBean;
import com.danertu.entity.StockDetailBean;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockDetailActivity extends BaseActivity implements View.OnClickListener, XListView.IXListViewListener {
    private static final int RESULT_STICK_DETAIL = 234;
    private Button btn_back;
    private TextView tv_title;
    private ImageView iv_product;//商品图片
    private TextView tv_stock_product_name;//商品名
    private TextView tv_valuation;//估值
    private TextView tv_stock_count;//库存剩余
    private TextView tv_no_data;//库存剩余
    private TextView tv_product_in;//入货
    private TextView tv_pick_up;//提货
    private XListView lv_product_in;
    private String guid;//此guid为仓库id，非商品id
    private String img;
    private String shopPrice;
    private String marketPrice;
    private String totalPrice;
    private String stockCount;
    private String productGuid;
    private String productName;
    public static final int REQUEST_PICK_UP = 111;
    public static final int REQUEST_STOCK_IN = 112;
    private Context context;
    private List<ProductInRecordBean.ValBean> productInList;
    private ListAdapter adapter;
    private String uid;
    public static boolean isToIn;//是否跳去入货

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e(TAG, "onCreate()");
        context = this;
        isToIn = false;
        setContentView(R.layout.activity_stock_detail);
        initSystemBar();
        setSystemBarWhite();
        findViewById();
        initView();
        initData();
        initLv();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.e(TAG, "onNewIntent()");
    }

    private void loadData() {
        //        //根据guid获取详情
        new GetProductInfo().execute(guid);
        new GetProductInRecord().execute(guid);
    }

    private void initData() {
        showLoadDialog();
        uid = getUid();
        productInList = new ArrayList<>();
        Intent intent = getIntent();
        guid = intent.getStringExtra("guid");
        if (TextUtils.isEmpty(guid)) {
            jsShowMsg("数据加载失败");
            jsFinish();
        }
        loadData();
    }

    private void initLv() {
        adapter = new ListAdapter(productInList);
        lv_product_in.setAdapter(adapter);
        lv_product_in.setPullRefreshEnable(false);
        lv_product_in.setPullLoadEnable(false);
        lv_product_in.setXListViewListener(StockDetailActivity.this);
    }

    /**
     * 获取商品数据
     */
    class GetProductInfo extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String guid = strings[0];
            return appManager.getStockProductInfo(uid, guid);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                StockDetailBean.ValBean bean = gson.fromJson(s, StockDetailBean.class).getVal().get(0);
                productGuid = bean.getProductGuid();
                img = bean.getSmallImage();
                productName = bean.getProductName();
                shopPrice = bean.getShopPrice();
                marketPrice = bean.getMarketPrice();
                totalPrice = CommonTools.formatZero2Str(Float.parseFloat(bean.getShopPrice()) * Integer.parseInt(bean.getStock()));
                stockCount = bean.getStock();
                ImageLoader.getInstance().displayImage(getStockSmallImgPath(bean.getSmallImage()), iv_product);
                tv_stock_product_name.setText(productName);
                tv_valuation.setText("估值：￥" + totalPrice);
                tv_stock_count.setText(stockCount);
                hideLoadDialog();
            } catch (Exception e) {
                judgeIsTokenException(s, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        jsShowMsg(info);
//                        quitAccount();
//                        finish();
//                        jsStartActivity("LoginActivity", "");
                    }

                    @Override
                    public void ok() {
                        jsShowMsg("数据加载错误");
                        jsFinish();
                    }
                });
                if (Constants.isDebug)
                    e.printStackTrace();
            }
        }
    }

    /**
     * 获取入货记录
     */
    class GetProductInRecord extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String wareHouseGuid = strings[0];
            return appManager.getProductInRecord(uid, wareHouseGuid);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                ProductInRecordBean productInRecordBean = gson.fromJson(s, ProductInRecordBean.class);
                Logger.e(TAG, productInRecordBean.toString());
                if (productInRecordBean == null) {
                    tv_no_data.setVisibility(View.VISIBLE);
                    lv_product_in.setVisibility(View.GONE);
                    return;
                }
                List<ProductInRecordBean.ValBean> list = productInRecordBean.getVal();
                if (list.size() > 0) {
                    productInList.addAll(list);
                    tv_no_data.setVisibility(View.GONE);
                    lv_product_in.setVisibility(View.VISIBLE);
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    lv_product_in.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                judgeIsTokenException(s, new TokenExceptionCallBack() {
                    @Override
                    public void tokenException(String code, String info) {
                        sendMessageNew(WHAT_TO_LOGIN, -1, info);
//                        jsShowMsg(info);
//                        quitAccount();
//                        jsStartActivity("LoginActivity", "");
//                        finish();
                    }

                    @Override
                    public void ok() {
                        tv_no_data.setVisibility(View.VISIBLE);
                        tv_no_data.setText("加载错误");
                        lv_product_in.setVisibility(View.GONE);
                    }
                });
                if (Constants.isDebug) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void findViewById() {
        btn_back = $(R.id.b_title_back);
        tv_title = $(R.id.tv_title);
        tv_product_in = $(R.id.tv_product_in);
        tv_pick_up = $(R.id.tv_pick_up);
        iv_product = $(R.id.iv_product);
        tv_stock_product_name = $(R.id.tv_stock_product_name);
        tv_valuation = $(R.id.tv_valuation);
        tv_stock_count = $(R.id.tv_stock_count);
        tv_no_data = $(R.id.tv_no_data);
        lv_product_in = $(R.id.lv_product_in);

        $(R.id.b_title_operation).setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        tv_title.setText("商品详细");
        btn_back.setOnClickListener(this);
        tv_product_in.setOnClickListener(this);
        tv_pick_up.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_title_back:
                jsFinish();
                break;
            case R.id.tv_product_in:
                //入货--跳转至当前商品后台拿货详细页面
                showLoadDialog();
                new GetProduct().execute("");
                break;
            case R.id.tv_pick_up:
                //提货--0327
                Intent intent = new Intent(this, PickUpActivity.class);
                intent.putExtra("guid", guid);
                intent.putExtra("productGuid", productGuid);
                intent.putExtra("img", img);
                intent.putExtra("productName", productName);
                intent.putExtra("shopPrice", shopPrice);
                intent.putExtra("marketPrice", marketPrice);
                intent.putExtra("totalPrice", totalPrice);
                intent.putExtra("stockCount", stockCount);
                startActivityForResult(intent, REQUEST_PICK_UP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_UP:
                if (resultCode != RESULT_CANCELED) {
                    setResult(RESULT_STICK_DETAIL);
                    //提货成功,更新库存
                    String stringExtra = data.getStringExtra("count");
                    int count;
                    try {
                        count = Integer.parseInt(stringExtra);
                    } catch (NumberFormatException e) {
                        count = 0;
                        e.printStackTrace();
                    }
                    int stock = Integer.parseInt(stockCount);
                    int last = stock - count;
                    if (last == 0) {
                        //已经没库存了
                        jsShowMsg("当前商品已经没有库存了，请及时入货");
                        tv_pick_up.setEnabled(false);
                    }
                    tv_stock_count.setText(last + "");
                }
                break;
            case REQUEST_STOCK_IN:
                //跳去了入货界面，不管入没入，刷新页面
                adapter.clearData();
                loadData();
                setResult(RESULT_STICK_DETAIL);
                break;

        }
    }

    @Override
    public void finish() {
        super.finish();
        setResult(RESULT_STICK_DETAIL);
    }

    /**
     * 获取后台拿货商品列表，拿出当前页面商品具体信息，跳转至当前页面商品的后台拿货页面
     * 因为没有单独的接口获取指定商品的信息，所以只能拿到所有后台拿货的商品列表跑循环对比
     */
    class GetProduct extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String info = "";
            String json = appManager.getBackcallProduct(uid);
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
                judgeIsTokenException(json, "您的登录信息已过期，请重新登录", -1);
                if (Constants.isDebug)
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
            isToIn = true;
            jsStartActivityForResult("ProductDetailWebPage", param, REQUEST_STOCK_IN);
        }
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    /**
     * 入货记录adapter
     */
    class ListAdapter extends BaseAdapter {
        private List<ProductInRecordBean.ValBean> list;

        public ListAdapter(List<ProductInRecordBean.ValBean> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void clearData() {
            list.clear();
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_stock_detail_product_in, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((ViewHolder) convertView.getTag());
            }
            ProductInRecordBean.ValBean bean = list.get(position);
            holder.tv_goods_in_time.setText(bean.getCreateTime().replace("/", "-"));

            holder.tv_goods_in_count.setText(bean.getBuyNumber());
            if (bean.getOrderType().equals("1")) {
                //OrderType=1时，为客户退货,其他值为入货
                holder.tv_goods_in_price.setText("客户退货");
            } else {
                holder.tv_goods_in_price.setText("入货总价：￥" + bean.getTotalPrice());
            }
            return convertView;
        }

        class ViewHolder {
            private TextView tv_goods_in_time;
            private TextView tv_goods_in_count;
            private TextView tv_goods_in_price;

            public ViewHolder(View itemView) {
                tv_goods_in_time = ((TextView) itemView.findViewById(R.id.tv_goods_in_time));
                tv_goods_in_count = ((TextView) itemView.findViewById(R.id.tv_goods_in_count));
                tv_goods_in_price = ((TextView) itemView.findViewById(R.id.tv_goods_in_price));
            }
        }

    }
}
