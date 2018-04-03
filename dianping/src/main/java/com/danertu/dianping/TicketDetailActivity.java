package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.adapter.IndexGalleryAdapter;
import com.danertu.entity.IndexGalleryItemData;
import com.danertu.tools.AppManager;

public class TicketDetailActivity extends BaseActivity {
    private Gallery mStormGallery = null;
    private IndexGalleryAdapter mStormAdapter = null;
    String shopProductResult = "";
    String shopId = "";
    String phoneNumber = "";
    String agentID = "";
    private String mImageUrl = null;
    private IndexGalleryItemData mItemData = null;
    private List<IndexGalleryItemData> mStormListData = new ArrayList<>();
    ArrayList<HashMap<String, Object>> shopkeeperRecommended = new ArrayList<>();
    private int TouchPost = 0;

    TextView detail_ticketName, detail_ticketMoney;
    RelativeLayout shopInfo;
    Button btn_use;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        findViewById();
        initView();
        newHandler.sendEmptyMessage(0);
        initTitle();
    }

    private void initTitle() {
        Button back = (Button) findViewById(R.id.b_order_title_back);
        back.setText("优惠券详情");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }

    @Override
    protected void findViewById() {
        mStormGallery = (Gallery) findViewById(R.id.ticket_detail_gallery);
        detail_ticketName = (TextView) findViewById(R.id.detail_ticketName);
        detail_ticketMoney = (TextView) findViewById(R.id.detail_ticketMoney);
        shopInfo = (RelativeLayout) findViewById(R.id.lay_shopInfo);
        btn_use = (Button) findViewById(R.id.btn_use);
    }

    @Override
    protected void initView() {
        String ticket_name = getIntent().getExtras().getString("ticketName");
        String ticket_money = getIntent().getExtras().getString("ticketMoney");
        detail_ticketName.setText(ticket_name);
        detail_ticketMoney.setText("￥" + ticket_money);
        shopId = getIntent().getExtras().getString("shopid");
        BindShopProduct();
        shopInfo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopId);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(TicketDetailActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        btn_use.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("shopid", shopId);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(TicketDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

    }

    private Thread tGetProList = new Thread(new Runnable() {
        @Override
        public void run() {
            // 耗时操作
            shopProductResult = AppManager.getInstance().postGetShopProduct("0042", shopId);

        }

    });

    Handler newHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            tGetshopdetails.start();
            super.handleMessage(msg);

        }
    };

    private void BindShopProduct() {
        tGetProList.start();
        try {
            tGetProList.join();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(shopProductResult).getJSONObject("shopprocuctList");
            JSONArray jsonArray = jsonObject.getJSONArray("shopproductbean");
            for (int i = 0; i < jsonArray.length(); i++) {
                mItemData = new IndexGalleryItemData();
                JSONObject oj = jsonArray.getJSONObject(i);
                String s = "";
                String smallImg = oj.getString("SmallImage");
                String originalImge = oj.getString("OriginalImge");
                agentID = oj.getString("AgentId");
                try {
                    if (smallImg != null && smallImg.trim().length() > 0) {
                        if (smallImg.contains("/")) {
                            s = Constants.imgServer
                                    + "Member/"
                                    + shopId
                                    + "/"
                                    + smallImg.substring(smallImg
                                    .lastIndexOf("/") + 1);
                        } else {
                            s = Constants.imgServer + "Member/" + shopId + "/"
                                    + smallImg;
                        }
                    } else {
                        if (originalImge.contains("/")) {
                            s = Constants.imgServer
                                    + "Member/"
                                    + shopId
                                    + "/"
                                    + originalImge.substring(originalImge
                                    .lastIndexOf("/") + 1);
                        } else {
                            s = Constants.imgServer + "Member/" + shopId + "/"
                                    + originalImge;
                        }

                    }

                } catch (Exception e) {
                    s = "";
                }

                mImageUrl = s.substring(s.lastIndexOf("/") + 1);
                mItemData.setId(oj.getString("Guid"));
                mItemData.setImageUrl(s);
                mItemData.setPrice(oj.getString("ShopPrice"));
                mItemData.setName(oj.getString("Name"));
                mItemData.setDetail(oj.getString("mobileProductDetail"));
                mStormListData.add(mItemData);
            }

            // }

            // }

        } catch (Exception e) {
            e.printStackTrace();
            // CommonTools.showShortToast(DetailActivity.this, "读取图片出错");
            return;
        }
        try {
            mStormAdapter = new IndexGalleryAdapter(this, R.layout.activity_index_gallery_item, mStormListData, new int[]{R.id.index_gallery_item_image, R.id.index_gallery_item_text});

            mStormGallery.setAdapter(mStormAdapter);
            mStormGallery.setSelection(3);

            mStormGallery.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    IndexGalleryItemData item = (IndexGalleryItemData) adapterView.getItemAtPosition(position);

                    TouchPost = position;
                    String guid = item.getId();
                    String price = item.getPrice();
                    String imgUrl = item.getImageUrl();
                    String detail = item.getDetail();
                    String proName = item.getName();
                    for (int i = 0; i < adapterView.getCount() - 1; i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("guid", guid);
                        map.put("price", ((IndexGalleryItemData) adapterView.getItemAtPosition(i)).getPrice());
                        map.put("img", ((IndexGalleryItemData) adapterView.getItemAtPosition(i)).getImageUrl());
                        map.put("proName", ((IndexGalleryItemData) adapterView.getItemAtPosition(i)).getName());
                        map.put("detail", ((IndexGalleryItemData) adapterView.getItemAtPosition(i)).getDetail());
                        map.put("supplierID", "");
                        map.put("agentID", agentID);
                        map.put("shopid", shopId);
                        map.put("mobile", phoneNumber);
                        shopkeeperRecommended.add(map);

                    }

                    Bundle b = new Bundle();
                    b.putString("guid", guid);
                    b.putString("price", price);
                    b.putString("img", imgUrl);
                    b.putString("shopid", shopId);
                    b.putString("proName", proName);
                    b.putString("detail", detail);
                    b.putString("agentID", agentID);
                    b.putString("supplierID", "");
                    b.putString("mobile", phoneNumber);
                    Intent intent = new Intent();
                    intent.setClassName(getApplicationContext(), "com.danertu.dianping.ProductDetailsActivity2");
                    // 标记
                    b.putInt("TouchPost", TouchPost);
                    intent.putExtra("arrayList", shopkeeperRecommended);
                    intent.putExtras(b);
                    startActivity(intent);

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            // CommonTools.showShortToast(DetailActivity.this, "221");
        }
    }

    private Thread tGetshopdetails = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            try {
                String result = AppManager.getInstance().postGetShopDetails("0041", shopId);
                JSONObject jsonObject = new JSONObject(result).getJSONObject("shopdetails");
                JSONArray jsonArray = jsonObject.getJSONArray("shopbean");
                if (jsonArray.length() > 0) {
                    JSONObject oj = jsonArray.getJSONObject(0);

//                    String ss = Constants.imgServer + "Member/" + oj.getString("Mobile") + "/" + oj.getString("EntityImage");
                    phoneNumber = oj.getString("Mobile");

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // String s ="123";
            }

        }

    });

}
