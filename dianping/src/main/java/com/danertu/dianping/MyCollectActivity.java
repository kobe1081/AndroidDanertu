package com.danertu.dianping;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.danertu.db.DBManager;
import com.danertu.tools.BaseSLViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 我的收藏
 * <p>
 * 保存在本地数据库中
 */
public class MyCollectActivity extends BaseActivity implements OnClickListener {
    private Button b_shops;
    private Button b_goods;
    private SwipeListView mListView;
    private Context context;

    private TextView tv_null;
    private Button head_back;
    /**
     * 商品收藏适配器
     */
    public GoodsCollectAdapter adapter1;
    /**
     * 店铺收藏适配器
     */
    public ShopCollectAdapter adapter2;
    /**
     * 商品收藏数据
     */
    private ArrayList<HashMap<String, Object>> data1 = new ArrayList<>();
    /**
     * 店铺收藏数据
     */
    private ArrayList<HashMap<String, Object>> data2 = new ArrayList<>();

    private String netImgpath;
    String uid;

    private Handler HandlerCollectProduct = new Handler() {

        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            adapter1 = new GoodsCollectAdapter(MyCollectActivity.this);
            mListView.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();
            setNullText(adapter1.getCount());
            super.handleMessage(msg);
        }
    };

    public void jsToProWebActivity(String shopID, String proName, String proPrice) {
        Intent intent = new Intent(context, ProductDetailWeb.class);
        intent.putExtra(ProductDetailWeb.KEY_PAGE_TYPE, 0);
        intent.putExtra(DetailActivity.KEY_SHOP_ID, shopID);
        intent.putExtra(ProductDetailWeb.KEY_PRO_NAME, proName);
        intent.putExtra(ProductDetailWeb.KEY_PRO_PRICE, proPrice);
        startActivity(intent);
    }

    private Handler HandlerCollectShop = new Handler() {

        public void handleMessage(Message msg) {
            // //执行接收到的通知，更新UI 此时执行的顺序是按照队列进行，即先进先出
            adapter2 = new ShopCollectAdapter(MyCollectActivity.this);
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        context = this;
        uid= db.GetLoginUid(context);
        data1=new ArrayList<>();
        data2=new ArrayList<>();
        findViewById();
        initView();
    }

    public void setNullText(int size) {
        if (size <= 0) {
            mListView.setVisibility(View.GONE);
            tv_null.setVisibility(View.VISIBLE);
        } else {
            mListView.setVisibility(View.VISIBLE);
            tv_null.setVisibility(View.GONE);
        }
    }

    protected void findViewById() {
        tv_null = (TextView) findViewById(R.id.mycollect_productnull_text);
        head_back = (Button) findViewById(R.id.b_order_title_back);
        b_shops = (Button) findViewById(R.id.b_collect_shops);
        b_goods = (Button) findViewById(R.id.b_collect_goods);
        mListView = (SwipeListView) findViewById(R.id.lv_collect_content);
        b_shops.setOnClickListener(this);
        b_goods.setOnClickListener(this);

        mListView.setOffsetLeft(getScreenWidth() - getResources().getDimension(R.dimen.back_w_size));
        mListView.setSwipeListViewListener(new BaseSLViewListener() {

            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                BaseAdapter ba = (BaseAdapter) mListView.getAdapter();
                clickItem(position, ba instanceof GoodsCollectAdapter);
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                super.onDismiss(reverseSortedPositions);
                BaseAdapter ba = (BaseAdapter) mListView.getAdapter();
                for (int position : reverseSortedPositions) {
                    if (ba instanceof GoodsCollectAdapter) {
                        String key = "productID";
                        db.deleteCollectItem(context, "CollectProduct", key, data1.get(position).get(key).toString());
                        data1.remove(position);
                    } else if (ba instanceof ShopCollectAdapter) {
                        String key = "shopID";
                        db.deleteCollectItem(context, "CollectShop", key, data2.get(position).get(key).toString());
                        data2.remove(position);
                    }
                }
                ba.notifyDataSetChanged();
            }

            @Override
            public void onClickBackView(final int position) {
                super.onClickBackView(position);
                mListView.closeAnimate(position);
                mListView.dismiss(position);
            }
        });

    }

    protected void initView() {
        b_goods.setTextColor(ContextCompat.getColor(this,R.color.red));
//        b_goods.setTextColor(res.getColor(R.color.red,null));
        b_shops.setTextColor(ContextCompat.getColor(this,R.color.black));

        head_back.setText("我的收藏");
        head_back.setOnClickListener(this);

        tCollectProduct.start();
        setNullText(0);

        tCollectShop.start();

    }

    @Override
    public void onClick(View v) {
        int count;
        switch (v.getId()) {
            case R.id.b_collect_shops:
                //店铺收藏
                if (adapter2 == null) {
                    break;
                }
                b_shops.setTextColor(ContextCompat.getColor(this,R.color.red));
                b_goods.setTextColor(ContextCompat.getColor(this,R.color.black));
                count = adapter2.getCount();
                setNullText(count);
                if (count > 0) {
                    mListView.setAdapter(adapter2);
                }
                break;
            case R.id.b_collect_goods:
//			商品收藏
                if (adapter1 == null) {
                    break;
                }
                b_goods.setTextColor(ContextCompat.getColor(this,R.color.red));
                b_shops.setTextColor(ContextCompat.getColor(this,R.color.black));
                count = adapter1.getCount();
                setNullText(count);
                if (count > 0) {
                    mListView.setAdapter(adapter1);
                }
                break;
            case R.id.b_order_title_back:
//			返回
                finish();
                break;
            default:
                break;
        }

    }

    //	获取商品收藏数据
    private Thread tCollectProduct = new Thread(new Runnable() {
        public void run() {
            Cursor cursor = db.GetCollectProduct(MyCollectActivity.this, uid);
            if (cursor.getCount() > 0) {
                try {
                    while (cursor.moveToNext()) {
                        HashMap<String, Object> dataMap1 = new HashMap<>();
                        String productID = cursor.getString(0);
                        String proName = cursor.getString(1);
                        String agentID = cursor.getString(2);
                        String price = cursor.getString(3);
                        String imgName = cursor.getString(4);
                        String supplierID = cursor.getString(6);
                        String detail = cursor.getString(7);
                        String imgURL = getImgUrl(imgName, agentID, supplierID);
                        try {
                            URL url = new URL(imgURL);
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                            dataMap1.put("banner", bitmap);
                        } catch (OutOfMemoryError | Exception e) {
                            e.printStackTrace();
                        }
                        dataMap1.put("productID", productID);
                        dataMap1.put("proName", proName);
                        dataMap1.put("agentID", agentID);
                        dataMap1.put("price", price);
                        dataMap1.put("imgURL", imgURL);
                        dataMap1.put("imgName", imgName);
                        dataMap1.put("supplierID", supplierID);
                        dataMap1.put("detail", detail);

                        data1.add(dataMap1);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (!cursor.isClosed()) {
                        cursor.close();
                    }
                }
            }

            Message msg = new Message();
            MyCollectActivity.this.HandlerCollectProduct.sendMessage(msg);
        }
    });

    /**
     * 商品收藏adapter
     */
    private class GoodsCollectAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext = null;

        GoodsCollectAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public HashMap<String, Object> getItem(int position) {
            return data1.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            //商品收藏adapter
            if (convertView == null) {
                // 和item_custom.xml脚本关联
                convertView = mInflater.inflate(R.layout.collect_product_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }

            viewHolder.itemName.setText(getItem(position).get("proName").toString());
            viewHolder.itemPrice.setText("￥" + getItem(position).get("price").toString());
            netImgpath = getItem(position).get("imgURL").toString();

            Bitmap bitmap = (Bitmap) getItem(position).get("banner");
            if (bitmap != null) {
                viewHolder.imgView.setImageBitmap(bitmap);
            } else {
                ImageLoader.getInstance().displayImage(netImgpath, viewHolder.imgView);
            }
            return convertView;
        }

        public int getCount() {
            return data1.size();
        }

        class ViewHolder {
            TextView itemName;
            TextView itemPrice;
            ImageView imgView;

            public ViewHolder(View view) {
                itemName = (TextView) view.findViewById(R.id.tv_collect_proTitle);
                itemPrice = (TextView) view.findViewById(R.id.tv_collect_proPrice);
                imgView = (ImageView) view.findViewById(R.id.iv_collect_pro_logo);
            }
        }

    }

    //	获取店铺收藏数据
    private Thread tCollectShop = new Thread(new Runnable() {

        @Override
        public void run() {
            Cursor cursor = db.GetCollectShop(MyCollectActivity.this, uid);
            if (cursor.getCount() > 0) {
                HashMap<String, Object> dataMap;
                try {
                    while (cursor.moveToNext()) {
                        dataMap = new HashMap<>();
                        String shopID = cursor.getString(0);
                        String shopName = cursor.getString(1);
                        String address = cursor.getString(2);
                        String mobile = cursor.getString(3);
                        String jyfw = cursor.getString(4);
                        String banner = cursor.getString(5);
                        String bannerName;
                        if (banner != null && banner.contains("/")) {
                            bannerName = banner.substring(banner.lastIndexOf("/") + 1);
                        } else {
                            bannerName = banner;
                        }

                        String imgURL = getImgUrl(bannerName, shopID, null);
                        try {
                            URL url = new URL(imgURL);
                            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                            dataMap.put("banner", bitmap);
                        } catch (OutOfMemoryError | Exception e) {
                            e.printStackTrace();
                        }
                        dataMap.put("shopID", shopID);
                        dataMap.put("shopName", shopName);
                        dataMap.put("address", address);
                        dataMap.put("mobile", mobile);
                        dataMap.put("jyfw", jyfw);//经营范围
                        dataMap.put("baner", banner);
                        dataMap.put("banerName", bannerName);
                        data2.add(dataMap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Message msg = new Message();
            MyCollectActivity.this.HandlerCollectShop.sendMessage(msg);
        }
    });

    /**
     * 店铺收藏adapter
     */
    private class ShopCollectAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext = null;

        ShopCollectAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(mContext);
        }

        public HashMap<String, Object> getItem(int position) {
            return data2.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                // 和item_custom.xml脚本关联
                convertView = mInflater.inflate(R.layout.collect_shop_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            viewHolder.shopName.setText(getItem(position).get("shopName").toString());
            viewHolder.shopSale.setText("主营：" + getItem(position).get("jyfw").toString());
            Object oBanner = getItem(position).get("baner");
            String banner = null;
            if (oBanner != null)
                banner = oBanner.toString();
            viewHolder.address.setText(getItem(position).get("address").toString());

            Bitmap bitmap = (Bitmap) getItem(position).get("banner");
            if (bitmap != null) {
                viewHolder.shopBanner.setImageBitmap(bitmap);
            } else {
                ImageLoader.getInstance().displayImage(banner, viewHolder.shopBanner);
            }
            return convertView;
        }

        public int getCount() {
            return data2.size();
        }

        class ViewHolder {
            TextView shopName;
            TextView shopSale;
            TextView address;
            ImageView shopBanner;

            public ViewHolder(View view) {
                shopName = (TextView) view.findViewById(R.id.tv_collect_shopName);
                shopSale = (TextView) view.findViewById(R.id.tv_collect_shopSale);
                address = (TextView) view.findViewById(R.id.tv_collect_shopAddress);
                shopBanner = (ImageView) view.findViewById(R.id.iv_collect_shopLogo);
            }
        }
    }

    //	item点击事件
    public void clickItem(int position, boolean isPro) {
        if (!isPro) {
//			店铺
            HashMap<String, Object> item = data2.get(position);
            String shopId = String.valueOf(item.get("shopID"));
            Intent intent = new Intent();
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.DetailActivity");
            // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
            Bundle b = new Bundle();
            b.putString("shopid", shopId);
            // 此处使用putExtras，接受方就响应的使用getExtra
            intent.putExtras(b);
            startActivity(intent);
        } else {
//			商品
            final HashMap<String, Object> item = data1.get(position);
            String guid = String.valueOf(item.get("productID").toString());
            String proName = String.valueOf(item.get("proName").toString());
            String img = String.valueOf(item.get("imgURL").toString());
            String detail = String.valueOf(item.get("detail").toString());
            String agentID = String.valueOf(item.get("agentID").toString());
            String supplierID = String.valueOf(item.get("supplierID").toString());
            String price = String.valueOf(item.get("price").toString());

            if (guid.toCharArray()[guid.length() - 1] == '-') {//表示是线下商品
                jsToProWebActivity(agentID, proName, price);
                return;
            }
            ActivityUtils.toProDetail2(context, guid, proName, img, detail, "", supplierID, price, "0", "", "0", 2);
        }
    }
}
