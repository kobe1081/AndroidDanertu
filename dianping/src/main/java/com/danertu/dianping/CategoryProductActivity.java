package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.danertu.tools.AppManager;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CategoryProductActivity extends BaseActivity {
    ListView category_product_list;
    private ArrayList<HashMap<String, Object>> data1;
    String categoryID = "";
    MyAdapter1 myAdapter1;
    TextView noData;
    Handler dataHandler;
    Button category_product_btn_back;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);
        dataHandler = new Handler();
        imageLoader = ImageLoader.getInstance();
        showLoadDialog();
        try {
            findViewById();
            initView();
            new Thread(dataRunnable).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void findViewById() {
        category_product_list = (ListView) findViewById(R.id.category_product_list);
        noData = (TextView) findViewById(R.id.nodata);
        category_product_btn_back = (Button) findViewById(R.id.b_order_title_back);
        category_product_btn_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        categoryID = getIntent().getExtras().getString("threeID");
        String title = getIntent().getExtras().getString("name");
        category_product_btn_back.setText(title);
    }

    Runnable dataRunnable = new Runnable() {

        @Override
        public void run() {
//			Thread thread = new Thread(listRunnable);
//			thread.start();
//			try {
//				thread.join();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
            listRunnable.run();
            dataHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (data1.size() <= 0) {
                        category_product_list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        myAdapter1 = new MyAdapter1();
                        category_product_list.setAdapter(myAdapter1);
                    }
                }
            });

            category_product_list
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {

                            HashMap<String, Object> item = myAdapter1.getItem(arg2);
                            String guid = item.get("guid").toString();
                            String proName = item.get("proName").toString();
                            String img = item.get("img").toString();
                            String agentID = item.get("agentID").toString();
                            String supplierID = item.get("supplierID").toString();
                            String price = item.get("price").toString();
                            String marketPrice = item.get("marketPrice").toString();
                            String mobile = item.get("mobile").toString();
                            if (mobile.equals("")) {
                                mobile = "400-995-2220";
                            }
                            Intent intent = new Intent();
                            intent.setClassName(getApplicationContext(), "com.danertu.dianping.ProductDetailsActivity2");
                            // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                            Bundle b = new Bundle();
                            b.putString("guid", guid);
                            b.putString("proName", proName);
                            b.putString("img", img);
                            b.putString("agentID", agentID);
                            b.putString("supplierID", supplierID);
                            b.putString("price", price);
                            b.putString("marketprice", marketPrice);
                            b.putString("shopid", getShopId());
                            b.putString("act", "ProductListActivity");
                            b.putString("mobile", mobile);
                            // 此处使用putExtras，接受方就响应的使用getExtra
                            intent.putExtras(b);
                            startActivity(intent);
                            // finish();
                        }
                    });
            hideLoadDialog();
        }
    };

    private class MyAdapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return data1.size();
        }

        @Override
        public HashMap<String, Object> getItem(int position) {
            return data1.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            ImageView imgView;
            TextView proNameDetails;
            TextView proMarketPrice;
            TextView proName;
            TextView proPrice;
        }

        @Override
        public View getView(int position, View v, ViewGroup arg2) {
            ViewHolder vh = null;
            if (v == null) {
                v = LayoutInflater.from(CategoryProductActivity.this).inflate(R.layout.productlist_item, null);
                vh = new ViewHolder();
                vh.imgView = (ImageView) v.findViewById(R.id.productIcon);
                vh.proNameDetails = (TextView) v.findViewById(R.id.proNameDetails);
                vh.proMarketPrice = (TextView) v.findViewById(R.id.mpromarketprice);
                vh.proName = (TextView) v.findViewById(R.id.proName);
                vh.proPrice = (TextView) v.findViewById(R.id.proPrice);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
            }

            String imgName = data1.get(position).get("img").toString();
            String agentID = data1.get(position).get("agentID").toString();
            String supplierID = data1.get(position).get("supplierID").toString();
            String marketPrice = "市场价:￥ " + data1.get(position).get("marketPrice").toString();
            String ss = ActivityUtils.getImgUrl(imgName, agentID, supplierID);
            String name = data1.get(position).get("proName").toString().trim();
            if (name.contains("|")) {
                String proRealName = name.substring(0, name.indexOf("|"));
                String guige = name.substring(name.indexOf("|") + 1);
                vh.proName.setText(proRealName);
                if (!guige.equals("")) {
                    vh.proNameDetails.setText(guige);
                } else {
                    vh.proNameDetails.setVisibility(View.GONE);
                }
            } else {
                vh.proNameDetails.setVisibility(View.GONE);
                vh.proName.setText(data1.get(position).get("proName").toString());
            }
            vh.proPrice.setText("￥ " + data1.get(position).get("price").toString());
            SpannableString sp = new SpannableString(marketPrice);
            sp.setSpan(new StrikethroughSpan(), 0, marketPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            vh.proMarketPrice.setText(sp);
            imageLoader.displayImage(ss, vh.imgView);

            return v;
        }

    }

    Runnable listRunnable = new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            data1 = new ArrayList<>();
            String result = AppManager.getInstance().getProductByCategory("0081", categoryID);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("productList");
                JSONArray jsonArray = jsonObject.getJSONArray("productbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    String guid = oj.getString("Guid");
                    if (guid.equals("edb05b72-10fd-4902-843d-ba11be607adb")) {
                        continue; // 手机端处理 不显示一元礼包
                    } else {
                        item.put("guid", guid);
                    }
                    item.put("proName", oj.getString("Name"));
                    item.put("img", oj.getString("SmallImage"));
                    item.put("detail", oj.getString("mobileProductDetail"));
                    String aID = oj.getString("AgentID");
                    item.put("agentID", aID);
                    String sID = oj.getString("SupplierLoginID");
                    item.put("supplierID", sID);
                    item.put("price", oj.getString("ShopPrice"));
                    item.put("marketPrice", oj.getString("MarketPrice"));
                    item.put("mobile", oj.getString("ContactTel"));
                    item.put("VirtualBuyCount", oj.getString("VirtualBuyCount"));// 商品购买数量
                    data1.add(item);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            SystemClock.sleep(1000);
        }

    };

}
