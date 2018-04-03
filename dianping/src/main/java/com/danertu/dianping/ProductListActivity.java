package com.danertu.dianping;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProductListActivity extends BaseActivity implements OnClickListener {

    private ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();
    private ListView lv;

    private EditText mSearchBox = null;
    private TextView type1, type2, type3, type4, type5;

    private ArrayList<HashMap<String, Object>> dtType1 = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> dtType2 = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> dtType3 = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> dtType4 = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> dtType5 = new ArrayList<>();

    private String ss;
    MyAdapter1 adapter1;
    String type = "1";
    String from = "";
    String uid = "";
    String result = "";

    // 1 晓镇香 2 红酒 3养生酒 4 祛痛神 5 土豪醇

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        findViewById();
        initView();
        initTitle("名酒世界");
    }

    private void initTitle(String title) {
        Button b_title = (Button) findViewById(R.id.index_top_logo);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
        lv = (ListView) findViewById(R.id.productList);
        mSearchBox = (EditText) findViewById(R.id.index_search_edit);
        type1 = (TextView) findViewById(R.id.type1);
        type2 = (TextView) findViewById(R.id.type2);
        type3 = (TextView) findViewById(R.id.type3);
        type4 = (TextView) findViewById(R.id.type4);
        type5 = (TextView) findViewById(R.id.type5);
    }

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

        class ViewHolder {
            ImageView imgView;
            TextView proName;
            TextView proPrice;
            TextView proNameDetails;
            TextView proMarketPrice;

            public ViewHolder(View v) {
                imgView = (ImageView) v.findViewById(R.id.productIcon);
                proName = (TextView) v.findViewById(R.id.proName);
                proPrice = (TextView) v.findViewById(R.id.proPrice);
                proNameDetails = (TextView) v.findViewById(R.id.proNameDetails);
                proMarketPrice = (TextView) v.findViewById(R.id.mpromarketprice);
            }
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            ViewHolder vh = null;
            if (v == null) {
                v = LayoutInflater.from(ProductListActivity.this).inflate(R.layout.productlist_item, null);
                vh = new ViewHolder(v);
                v.setTag(vh);
            } else {
                vh = (ViewHolder) v.getTag();
                vh.imgView.setImageResource(R.drawable.no_image);
            }
            String imgName = data1.get(position).get("img").toString();
            String agentID = data1.get(position).get("agentID").toString();
            String supplierID = data1.get(position).get("supplierID").toString();
            String marketPrice = "市场价:￥ " + data1.get(position).get("marketPrice").toString();

            ss = getImgUrl(imgName, agentID, supplierID);

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

//			MyImageLoader imageLoader = new MyImageLoader(
//					ProductListActivity.this);
//			imageLoader.DisplayImage(ss, vh.imgView);
            ImageLoader.getInstance().displayImage(ss, vh.imgView);

            return v;
        }

    }

    /**
     * 商品购买量key
     */
    public static final String KEY_BUY_COUNT = "VirtualBuyCount";
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            String result = AppManager.getInstance().postGetProductList("0040", mSearchBox.getText().toString(), type, shopid);
            data1.clear();
            JSONObject jsonObject;
            try {
                // Name, Guid, SmallImage, mobileProductDetail, AgentID,
                // SupplierLoginID, ShopPrice
                jsonObject = new JSONObject(result).getJSONObject("danProductlist");
                JSONArray jsonArray = jsonObject.getJSONArray("danProductbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    String guid = oj.getString("Guid");
                    if (guid.equals("edb05b72-10fd-4902-843d-ba11be607adb")) {
                        continue;  //手机端处理 不显示一元礼包
                    } else {
                        item.put("guid", guid);
                    }
                    item.put("proName", oj.getString("Name"));
                    item.put("img", oj.getString("SmallImage"));
                    item.put("detail", oj.getString("mobileProductDetail"));
                    item.put("agentID", oj.getString("AgentID"));
                    item.put("supplierID", oj.getString("SupplierLoginID"));
                    item.put("price", oj.getString("ShopPrice"));
                    item.put("marketPrice", oj.getString("MarketPrice"));
                    item.put("mobile", "400-995-2220");
                    item.put(KEY_BUY_COUNT, oj.getString(KEY_BUY_COUNT));//商品购买数量
                    data1.add(item);
                }

                switch (type) {
                    case "1":
                        dtType1 = data1;
                        break;
                    case "2":
                        dtType2 = data1;
                        break;
                    case "3":
                        dtType3 = data1;
                        break;
                    case "4":
                        dtType4 = data1;
                        break;
                    case "5":
                        dtType5 = data1;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//			SystemClock.sleep(1000);
            // ProductListActivity.this.HandlerProductList.sendMessage(msg);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = new MyAdapter1();
            sendMessage(msg);
        }
    };


    private String shopid;

    public void setTouchToSearch() {
        mSearchBox.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    openActivity(SearchActivityV2.class);
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void initView() {
        setTouchToSearch();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            shopid = bundle.getString("shopid");
            from = bundle.getString("from");
        }
        if (TextUtils.isEmpty(type)) {
            type = "1";
        } else if (type.equals("4")) {
            type = "3";
        }
        shopid = TextUtils.isEmpty(shopid) ? Constants.CK_SHOPID : shopid;
        changeStyle();
        data1 = initDataSet(type);
        if (data1 == null || data1.size() == 0) {
            showLoadDialog();
            Thread thread1 = new Thread(runnable);
            thread1.start();
        } else {
            adapter1 = new MyAdapter1();
            lv.setAdapter(adapter1);
        }

        type1.setOnClickListener(this);
        type2.setOnClickListener(this);
        type3.setOnClickListener(this);
        type4.setOnClickListener(this);
        type5.setOnClickListener(this);
        uid = db.GetLoginUid(ProductListActivity.this);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap item = (HashMap) parent.getItemAtPosition(position);
                String guid = String.valueOf(item.get("guid").toString());
                String proName = String.valueOf(item.get("proName").toString());
                String img = String.valueOf(item.get("img").toString());
                String detail = String.valueOf(item.get("detail").toString());
                String agentID = String.valueOf(item.get("agentID").toString());
                String supplierID = String.valueOf(item.get("supplierID").toString());
                String price = String.valueOf(item.get("price").toString());
                String marketPrice = String.valueOf(item.get("marketPrice").toString());
                String VirtualBuyCount = item.get(KEY_BUY_COUNT).toString();

                // 添加最近浏览记录
                if (uid != null && uid.trim().length() > 0) {
                    Date date = new Date();
                    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String createTime = f.format(date);
                    db.InsertNearlyBroswer(ProductListActivity.this, guid, proName, agentID, price, img, uid, supplierID, createTime, detail);
                }
                if (from != null && from.equals("index")) {
                    Thread boundThread = new Thread(isBoundRunnable);
                    boundThread.start();
                    try {
                        boundThread.join();
                        if (!"".equals(result)) {
                            shopid = result;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

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
                b.putString("shopid", shopid);
                b.putString("mobile", "400-995-2220");
                b.putString(KEY_BUY_COUNT, VirtualBuyCount);

                // 此处使用putExtras，接受方就响应的使用getExtra
                intent.putExtras(b);
                startActivity(intent);
                // finish();
            }
        });
    }

    private ArrayList<HashMap<String, Object>> initDataSet(String type) {
        if (type == null)
            return null;
        switch (type) {
            case "1":
                return dtType1;
            case "2":
                return dtType2;
            case "3":
                return dtType3;
            case "4":
                return dtType4;
            case "5":
                return dtType5;
        }
        return null;
    }

    MyHandler productHandler = new MyHandler(this);

    public static class MyHandler extends Handler {
        WeakReference<ProductListActivity> wAct = null;
        ProductListActivity pa = null;

        public MyHandler(ProductListActivity pla) {
            wAct = new WeakReference<>(pla);
            pa = wAct.get();
        }

        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0) {
                    pa.adapter1.notifyDataSetChanged();
                    pa.hideLoadDialog();
                } else if (msg.what == 1) {
                    pa.adapter1 = (MyAdapter1) msg.obj;
                    pa.lv.setAdapter(pa.adapter1);
                    pa.hideLoadDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Runnable isBoundRunnable = new Runnable() {

        @Override
        public void run() {
            result = AppManager.getInstance().getBound("0059", uid);

        }
    };

    public void sendMessage(Message msg) {
        if (productHandler == null)
            return;
        productHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View v) {
        if (isLoading()) {
            return;
        }
        switch (v.getId()) {
            case R.id.type1:
                type = "1";
                changeStyle();
                data1 = dtType1;
                if (data1 == null || data1.size() == 0) {
                    showLoadDialog();
                    Thread click1 = new Thread(runnable);
                    click1.start();
                } else
                    sendMessage(getMessage(0, null));
                break;
            case R.id.type2:

                type = "2";
                changeStyle();
                data1 = dtType2;
                if (data1 == null || data1.size() == 0) {
                    showLoadDialog();
                    Thread click2 = new Thread(runnable);
                    click2.start();
                } else
                    sendMessage(getMessage(0, null));
                break;
            case R.id.type3:

                type = "3";
                changeStyle();
                data1 = dtType3;
                if (data1 == null || data1.size() == 0) {
                    showLoadDialog();
                    Thread click3 = new Thread(runnable);
                    click3.start();
                } else
                    sendMessage(getMessage(0, null));
                break;
            case R.id.type4:
                type = "4";
                changeStyle();
                data1 = dtType4;
                if (data1 == null || data1.size() == 0) {
                    showLoadDialog();
                    Thread click4 = new Thread(runnable);
                    click4.start();
                } else
                    sendMessage(getMessage(0, null));
                break;
            case R.id.type5:
                type = "5";
                changeStyle();
                data1 = dtType5;
                if (data1 == null || data1.size() == 0) {
                    showLoadDialog();
                    Thread click5 = new Thread(runnable);
                    click5.start();
                } else
                    sendMessage(getMessage(0, null));
                break;
            default:
                break;
        }
    }

    private void changeStyle() {
        switch (type) {
            case "1":
                type1.setBackgroundResource(R.drawable.segment_selected_1_bg);
                type1.setTextAppearance(this, R.style.style_13_FFFFFF_BOLD);
                type2.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type2.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type3.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type3.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type4.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type4.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type5.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type5.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                break;
            case "2":
                type1.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type1.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type2.setBackgroundResource(R.drawable.segment_selected_1_bg);
                type2.setTextAppearance(this, R.style.style_13_FFFFFF_BOLD);
                type3.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type3.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type4.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type4.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type5.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type5.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                break;
            case "3":
                type1.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type1.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type2.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type2.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type3.setBackgroundResource(R.drawable.segment_selected_1_bg);
                type3.setTextAppearance(this, R.style.style_13_FFFFFF_BOLD);
                type4.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type4.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type5.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type5.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                break;
            case "4":
                type1.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type1.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type2.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type2.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type3.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type3.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type4.setBackgroundResource(R.drawable.segment_selected_1_bg);
                type4.setTextAppearance(this, R.style.style_13_FFFFFF_BOLD);
                type5.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type5.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                break;
            case "5":
                type1.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type1.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type2.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type2.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type3.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type3.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type4.setBackgroundResource(R.drawable.segment_normal_2_bg);
                type4.setTextAppearance(this, R.style.style_13_4B4B4B_BOLD);
                type5.setBackgroundResource(R.drawable.segment_selected_1_bg);
                type5.setTextAppearance(this, R.style.style_13_FFFFFF_BOLD);
                break;
        }

    }
}
