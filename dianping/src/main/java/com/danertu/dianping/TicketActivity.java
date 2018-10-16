package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.config.Constants;
import com.danertu.tools.AppManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 2017年7月31日
 * haungyeliang
 * 将MyAdapter1重命名为TicketAdapter，添加holder类实现item复用
 */
public class TicketActivity extends BaseActivity {

    ListView listView;
    ArrayList<HashMap<String, Object>> data;
    String shopid = "15876045777";
    String ticketName = "苓轩茶庄";
    String ticketMoney = "50";
    int isFirst = 1;
    TextView noTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        findViewById();
        initView();
        initTitle();
    }

    private void initTitle() {
        Button back = (Button) findViewById(R.id.b_order_title_back);
        back.setText("优惠券");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
        listView = (ListView) findViewById(R.id.ticketList);
        noTicket = (TextView) findViewById(R.id.no_ticket);
    }

    @Override
    protected void initView() {
        Thread ticketThread = new Thread(ticketRunnable);
        ticketThread.start();
        try {
            ticketThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TicketAdapter adapter1 = new TicketAdapter();
        listView.setAdapter(adapter1);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopid);
                bundle.putString("ticketName", ticketName);
                bundle.putString("ticketMoney", ticketMoney);
                intent.putExtras(bundle);
                intent.setClass(TicketActivity.this, TicketDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    private class TicketAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(TicketActivity.this).inflate(R.layout.ticket_item, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((Holder) convertView.getTag());
            }
            // String url = data.get(position).get("img").toString();
            // String name = data.get(position).get("ticketName").toString();
            // String money = data.get(position).get("money").toString();

            holder.ticket_name.setText("单耳兔");
            holder.ticket_money.setText("20");
            holder.layout.setBackgroundResource(R.drawable.ticket_bg1);
            // MyImageLoader imageLoader = new
            // MyImageLoader(TicketActivity.this);
            // imageLoader.DisplayImage(url, ticket_shop_icon);

            return convertView;
        }

        class Holder {
            ImageView ticket_shop_icon;
            TextView ticket_name;
            TextView ticket_money;
            TextView ticket_time;
            RelativeLayout layout;

            Holder(View view) {
                ticket_shop_icon = (ImageView) view.findViewById(R.id.tick_shop_icon);
                ticket_name = (TextView) view.findViewById(R.id.ticket_name);
                ticket_money = (TextView) view.findViewById(R.id.ticket_money);
                ticket_time = (TextView) view.findViewById(R.id.ticket_time);
                layout = (RelativeLayout) view.findViewById(R.id.ticket_bg);
            }
        }

    }

    Runnable ticketRunnable = new Runnable() {

        @Override
        public void run() {
            data = new ArrayList<>();
            String result = AppManager.getInstance().getTicket("0070", Constants.getCityName());
            try {
                JSONObject jsonObject = new JSONObject(result).getJSONObject("ticketsList");
                JSONArray jsonArray = jsonObject.getJSONArray("ticketbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    String productGuid = oj.getString("ProductGuid");
                    shopid = oj.getString("shopid");
                    String supplierId = oj.getString("supplierid");
                    String ticketImg = oj.getString("Image");
                    ticketMoney = oj.getString("TicketMoney");
                    ticketName = oj.getString("TicketName");
                    String description = oj.getString("TicketDescription");
                    if (ticketImg.contains("/")) {
                        ticketImg = ticketImg.substring(ticketImg.lastIndexOf("/") + 1);
                    }
                    int type = oj.getInt("type");
                    String imgUrl = "";
                    switch (type) {
                        case 1: // 单个商品优惠券
                            imgUrl = Constants.APP_URL.imgServer + "sysProduct/" + ticketImg;
                            break;
                        case 2: // 店铺优惠券
                            imgUrl = Constants.APP_URL.imgServer + "Member/" + shopid + "/" + ticketImg;
                            break;
                        case 3: // 供应商优惠券
                            imgUrl = Constants.APP_URL.imgServer + "SupplierProduct/" + supplierId + "/" + ticketImg;
                            break;
                    }
                    item.put("productGuid", productGuid);
                    item.put("shopid", shopid);
                    item.put("supplierId", supplierId);
                    item.put("img", imgUrl);
                    item.put("money", ticketMoney);
                    item.put("ticketName", ticketName);
                    item.put("description", description);
                    data.add(item);
                }

				/*
                 * if (data.size() == 0) { noTicket.setVisibility(View.VISIBLE);
				 * listView.setVisibility(View.GONE); }
				 */

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

}
