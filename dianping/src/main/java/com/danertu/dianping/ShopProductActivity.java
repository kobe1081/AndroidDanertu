package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.widget.MWebViewClient;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ShopProductActivity extends BaseActivity {

    // 用于下面点点指示

    //	private GridView grid;
    private ArrayList<HashMap<String, Object>> data1 = null;
    private String ss;

    String type = "1";
    MyAdapter1 adapter1 = new MyAdapter1();
    private int TouchPost = 0;

    //	private LinearLayout layGrid, layNoProduct;
    String shopid = "";
    String phoneNum = "";
    String shopJson = null;
    String proListJson = null;
    private String uid;
//	MyImageLoader imageLoader = new MyImageLoader(this); 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product);
        uid=getUid();
//		Bundle bundle = getIntent().getExtras();
//		if (bundle != null) {
//			shopid = getIntent().getExtras().getString("shopid"); // 获取传过来的店铺id
//			phoneNum = getIntent().getExtras().getString("mobile"); // 获取传过来的店铺电话
//			proListJson = bundle.getString(ProductDetailWeb.KEY_PRO_LIST_JSON);
//		}
        shopJson = getIntent().getStringExtra(ProductDetailWeb.KEY_SHOP_JSON);
        findViewById();
        initView();
//		grid = (GridView) findViewById(R.id.shop_product_gridView);
//		grid.setOnItemClickListener(mOnClickListener);
//
//		myHandler = new MyHandler(this);
//		Thread tProductList = new Thread(productRunnable);
//		tProductList.start();
    }

//	private MyHandler myHandler = null;
//	public static class MyHandler extends Handler{
//		WeakReference<ShopProductActivity> wAct;
//		ShopProductActivity spa;
//		public MyHandler(ShopProductActivity act){
//			wAct = new WeakReference<ShopProductActivity>(act);
//			spa = wAct.get();
//		}
//		
//		public void handleMessage(Message msg){
//			if(msg.what == 1){
//				if (spa.data1.size() == 0) {
//					spa.layGrid.setVisibility(View.GONE);
//					spa.layNoProduct.setVisibility(View.VISIBLE);
//				}else
//					spa.grid.setAdapter(spa.adapter1);
//			}
//		}
//	}

    /**
     * 线下产品的html点击事件, 把线下产品的json传到商品详细页
     *
     * @param index 控件底标(json选中项的底标)
     */
    @JavascriptInterface
    public void jsClickItem(int index) {
        Log.e("Json_item", index + "");
        Intent intent = new Intent(this, ProductDetailWeb.class);
        intent.putExtra(ProductDetailWeb.KEY_PRO_INDEX, index);
        intent.putExtra(ProductDetailWeb.KEY_PRO_LIST_JSON, proListJson);
        intent.putExtra(ProductDetailWeb.KEY_SHOP_JSON, shopJson);
        intent.putExtra(ProductDetailWeb.KEY_PAGE_TYPE, 0);
        intent.putExtra(ProductDetailWeb.KEY_SHOP_ID, shopid);
        startActivity(intent);
    }

    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            jsClickItem(arg2);

//			HashMap item = (HashMap) arg0.getItemAtPosition(arg2);
//			TouchPost = arg2;
//			String guid = String.valueOf(item.get("guid").toString());
//			String proName = String.valueOf(item.get("proName").toString());
//			String img = String.valueOf(item.get("img").toString());
//			String detail = String.valueOf(item.get("detail").toString());
//			String agentID = String.valueOf(item.get("agentID").toString());
//			String supplierID = String.valueOf(item.get("supplierID")
//					.toString());
//			String price = String.valueOf(item.get("price").toString());
//			String marketprice = String.valueOf(item.get("marketPrice")
//					.toString());
//
//			// 添加最近浏览记录
//			DBManager db = new DBManager();
//			String uid = db.GetLoginUid(ShopProductActivity.this);
//			if (uid != null && uid.trim().length() > 0) {
//				Date date = new Date();
//				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//				String createTime = f.format(date);
//				db.InsertNearlyBroswer(ShopProductActivity.this, guid, proName,
//						agentID, price, img, uid, supplierID,
//						createTime.toString(), detail);
//			}
//
//			Intent intent = new Intent();
//			intent.setClassName(getApplicationContext(),
//					"com.danertu.dianping.ProductDetailsActivity2");
//			// Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
//			Bundle b = new Bundle();
//			b.putString("guid", guid);
//			b.putString("proName", proName);
//			b.putString("img", img);
//			b.putString("detail", detail);
//			b.putString("agentID", agentID);
//			b.putString("supplierID", supplierID);
//			b.putString("price", price);
//			b.putString("marketprice", marketprice);
//			b.putString("shopid", shopid);
//			b.putString("act", "ProductListActivity");
//			b.putString("mobile", phoneNum);
//			b.putInt("TouchPost", TouchPost);
//			intent.putExtra("arrayList", data1);
//
//			// 此处使用putExtras，接受方就响应的使用getExtra
//			intent.putExtras(b);
//			startActivity(intent);
            // finish();
        }
    };

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

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ShopProductActivity.this).inflate(R.layout.shop_product_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = ((ViewHolder) convertView.getTag());
            }

            String imgName = data1.get(position).get("img").toString();
            String agentID = data1.get(position).get("agentID").toString();
            String supplierID = data1.get(position).get("supplierID").toString();
            String marketPrice = data1.get(position).get("marketPrice").toString();

            ss = ActivityUtils.getImgUrl(imgName, agentID, supplierID,uid);

            if (data1.get(position).get("proName").toString().indexOf(" | ") > 0) {
                String[] strPro = data1.get(position).get("proName").toString().split(" | ");
                String proRealName = "";
                for (int i = 0; i < strPro.length - 2; i++) {
                    proRealName += " " + strPro[i];
                }
                holder.proName.setText(proRealName);
                holder.proNameDetails.setText(strPro[strPro.length - 1]);
            } else {
                holder.proName.setText(data1.get(position).get("proName").toString().trim());
            }

            holder.proPrice.setText("￥ " + data1.get(position).get("price").toString());

            SpannableString sp = new SpannableString(marketPrice);
            sp.setSpan(new StrikethroughSpan(), 0, marketPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.proMarketPrice.setText(sp);

//			imageLoader.DisplayImage(ss, imgView);
            ImageLoader.getInstance().displayImage(ss, holder.imgView);

            return convertView;
        }

        class ViewHolder {
            ImageView imgView;
            TextView proName;
            TextView proPrice;
            TextView proNameDetails;
            TextView proMarketPrice;

            public ViewHolder(View view) {
                imgView = (ImageView) view.findViewById(R.id.shop_product_image);
                proName = (TextView) view.findViewById(R.id.txt_shopproductname);
                proPrice = (TextView) view.findViewById(R.id.txt_shopprice);
                proNameDetails = (TextView) view.findViewById(R.id.shopitemdetial);
                proMarketPrice = (TextView) view.findViewById(R.id.txt_marketprice);
            }
        }

    }

    Runnable productRunnable = new Runnable() {

        @Override
        public void run() {
            // 耗时操作
//			String shopId = getIntent().getExtras().getString("shopid")
//					.toString();
//			data1 = new ArrayList<HashMap<String, Object>>();
////			String result = AppManager.getInstance().postGetShopProduct("0042",
////					shopId);
//
//			JSONObject jsonObject;
//			try {
//				// Name, Guid, SmallImage, mobileProductDetail, AgentID,
//				// SupplierLoginID, ShopPrice
//				jsonObject = new JSONObject(proListJson)
//						.getJSONObject("shopprocuctList");
//				JSONArray jsonArray = jsonObject
//						.getJSONArray("shopproductbean");
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject oj = jsonArray.getJSONObject(i);
//					HashMap<String, Object> item = new HashMap<String, Object>();
//					item.put("guid", oj.getString("Guid"));
//					item.put("proName", oj.getString("Name"));
//					item.put("img", oj.getString("SmallImage"));
//					item.put("detail", oj.getString("mobileProductDetail"));
//					item.put("agentID", oj.getString("AgentId"));
//					item.put("supplierID", "");
//					item.put("price", oj.getString("ShopPrice"));
//					item.put("marketPrice", oj.getString("MarketPrice"));
//					item.put("mobile", phoneNum);
//					data1.add(item);
//				}
//			} catch (Exception e) {
//
//				e.printStackTrace();
//			}
//			
////			SystemClock.sleep(1000);
//			// ProductListActivity.this.HandlerProductList.sendMessage(msg);
//			myHandler.sendEmptyMessage(1);
        }
    };

    @Override
    protected void findViewById() {
        // top_shopname = (TextView) findViewById(R.id.top_MyShopName);
//		layGrid = (LinearLayout) findViewById(R.id.lay_grid);
//		layNoProduct = (LinearLayout) findViewById(R.id.lay_noproduct);
        webView = (WebView) findViewById(R.id.wv_shopIntroduce);
    }

    final String WV_INTERFACE = "iface_shopintroduce";
    final String WEBPAGE_NAME = "Android_shop_info.html";

    protected void initView() {
//		String shopname = getIntent().getExtras().getString("shopName");
        // top_shopname.setText(shopname);
//		initTitle(shopname);

        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(this, WV_INTERFACE);

//		webView.loadUrl("file:///android_asset/" + WEB_PAGE_NAME);
        webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_NAME);
//		webView.loadUrl("http://192.168.1.129:778/shop_info.html");

        webView.setWebViewClient(new MWebViewClient(this, WV_INTERFACE) {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                javaLoadContent(shopJson);
                hideLoadDialog();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                showLoadDialog();
            }

        });
    }

    public void javaLoadContent(String shopJsonStr) {
        webView.loadUrl("javascript:javaLoadShopIntroduce('" + shopJsonStr + "')");
    }

    @JavascriptInterface
    public void initTitle(final String title) {
        runOnUiThread(new Runnable() {
            public void run() {
                Button b_title = (Button) findViewById(R.id.b_order_title_back);
                b_title.setText(title);
                b_title.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        finish();
                    }
                });
            }
        });
    }

}