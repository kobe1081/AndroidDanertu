package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.config.Constants;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchShopProductActivity extends BaseActivity implements OnItemClickListener, OnKeyListener, OnClickListener, OnScrollListener {

    private MyAdapter adapter;
    private ListView resultList;
    //	private TextView noresult;
    private View noResource;
    private Button b_title_back;
    private EditText mEditText;
    private Button search_btn;
    private String shopId, agentID, keyword;
    private boolean refreshable, isLastPage;
    private String lastResult;
    private LinearLayout loading;
    ArrayList<HashMap<String, Object>> data = new ArrayList<>();
    ArrayList<HashMap<String, Object>> dataTemp;
    private int pageindex = 1; // 当前页数
    private searchProductTasks mTask;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shopproduct);
        shopId = getIntent().getExtras().getString("shopid").trim();
        agentID = getIntent().getExtras().getString("agentID").trim();
        keyword = getIntent().getExtras().getString("keyword").trim();
        uid = getUid();
        findViewById();
        initView();
        updateProductList();

    }

    @Override
    protected void findViewById() {
        resultList = (ListView) findViewById(R.id.resultlist);
        mEditText = (EditText) findViewById(R.id.search_edit);
        search_btn = (Button) findViewById(R.id.search_btn);
//		noresult = (TextView) findViewById(R.id.noresult_text);
        noResource = findViewById(R.id.noResource);
        b_title_back = (Button) findViewById(R.id.b_title_back);
        loading = (LinearLayout) findViewById(R.id.loading);
    }

    @Override
    protected void initView() {
        mEditText.setOnKeyListener(this);
        search_btn.setOnClickListener(this);
        adapter = new MyAdapter();
        resultList.setAdapter(adapter);
        resultList.setOnItemClickListener(this);
        resultList.setOnScrollListener(this);
        b_title_back.setOnClickListener(this);
        refreshable = true;
        isLastPage = false;
    }

    public void updateProductList() {
        mTask = new searchProductTasks();
        mTask.execute();
    }

    private Dialog loadingDialog;

    private class MyAdapter extends BaseAdapter {

        public Object getItem(int arg0) {

            return data.get(arg0);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getCount() {

            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SearchShopProductActivity.this).inflate(R.layout.supplier_product_item, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = ((ViewHolder) convertView.getTag());
            }
            String imgName = data.get(position).get("img").toString();
            String supplierID = data.get(position).get("supplierID").toString();
            String ss = ActivityUtils.getImgUrl(imgName, agentID, supplierID, uid);
            if (data.get(position).get("proName").toString().indexOf(" | ") > 0) {
                String[] strPro = data.get(position).get("proName").toString().split(" | ");
                String proRealName = "";
                for (int i = 0; i < strPro.length - 2; i++) {
                    proRealName += " " + strPro[i];
                }
                viewHolder.proName.setText(proRealName);
                viewHolder.proNameDetails.setText(strPro[strPro.length - 1]);
            } else {
                viewHolder.proName.setText(data.get(position).get("proName").toString());
                viewHolder.proNameDetails.setVisibility(TextView.GONE);
            }

            viewHolder.proName.setText(data.get(position).get("proName").toString());
            viewHolder.proPrice.setText(data.get(position).get("price").toString());
            String strMarketPrice = "市场价:￥ " + data.get(position).get("marketPrice").toString();

            SpannableString sp = new SpannableString(strMarketPrice);
            sp.setSpan(new StrikethroughSpan(), 0, strMarketPrice.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.proMarketPrice.setText(sp);
            ImageLoader.getInstance().displayImage(ss, viewHolder.imgView);

            return convertView;
        }

        class ViewHolder {
            ImageView imgView;
            TextView proName;
            TextView proPrice;
            TextView proMarketPrice;
            TextView proNameDetails;

            public ViewHolder(View view) {
                imgView = (ImageView) view.findViewById(R.id.productIcon);
                proName = (TextView) view.findViewById(R.id.proName);
                proPrice = (TextView) view.findViewById(R.id.proPrice);
                proMarketPrice = (TextView) view.findViewById(R.id.smromarketprice);
                proNameDetails = (TextView) view.findViewById(R.id.vproNameDetails);
            }
        }

    }

    private class searchProductTasks extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = AppManager.getInstance().postGetSearchShopProduct("0071", shopId, keyword, Constants.pageSize, pageindex);
            if (!result.equals(lastResult)) {
                lastResult = result;
                dataTemp = new ArrayList<>();
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result).getJSONObject("procuctList");
                    JSONArray jsonArray = jsonObject.getJSONArray("productbean");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject oj = jsonArray.getJSONObject(i);
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("guid", oj.getString("Guid"));
                        item.put("proName", oj.getString("Name"));
                        item.put("img", oj.getString("SmallImage"));
                        item.put("detail", oj.getString("mobileProductDetail"));
                        item.put("agentID", oj.getString("SupplierLoginID").equals("") ? agentID : "");
                        item.put("supplierID", oj.getString("SupplierLoginID"));
                        item.put("price", oj.getString("ShopPrice"));
                        item.put("marketPrice", oj.getString("MarketPrice"));
                        item.put("mobile", oj.getString("ContactTel").equals("") ? "null" : oj.getString("ContactTel"));
                        dataTemp.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                dataTemp = null;
                isLastPage = true;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showLoadDialog();
            noResource.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if (dataTemp != null) {
                data.addAll(dataTemp);
                adapter.notifyDataSetChanged();
                loading.setVisibility(View.GONE);
                refreshable = true;
            } else {
                loading.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "所有数据加载完毕!", Toast.LENGTH_LONG).show();
            }
            if (adapter.getCount() == 0) {
                noResource.setVisibility(View.VISIBLE);
            } else {
                noResource.setVisibility(View.GONE);
            }
            loadingDialog.dismiss();
            super.onPostExecute(result);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
        int TouchPost = position;
        String guid = String.valueOf(item.get("guid"));
        String imgUrl = String.valueOf(item.get("img"));
        String proName = String.valueOf(item.get("proName"));
        String agentID = String.valueOf(item.get("agentID"));
        String supplierID = String.valueOf(item.get("supplierID"));
        String price = String.valueOf(item.get("price").toString());
        String detail = String.valueOf(item.get("detail"));
//        String mobile = String.valueOf(item.get("mobile"));
        // 添加最近浏览记录
        String uid = db.GetLoginUid(SearchShopProductActivity.this);
        if (uid != null && uid.trim().length() > 0) {
            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String createTime = f.format(date);
            db.InsertNearlyBroswer(SearchShopProductActivity.this, guid, proName, agentID, price, imgUrl, uid, supplierID, createTime, detail);
        }
        jsToProWebActivity(shopId, proName, price);
//		Bundle b = new Bundle();
//		b.putString("guid", guid);
//		b.putString("img", imgUrl);
//		b.putString("shopid", shopId);
//		b.putString("proName", proName);
//		b.putString("detail", detail);
//		b.putString("agentID", agentID);
//		b.putString("price", price);
//		b.putString("supplierID", supplierID);
//		b.putString("mobile", mobile);
//		Intent intent = new Intent();
//		intent.setClassName(getApplicationContext(),
//				"com.danertu.dianping.ProductDetailsActivity2");
//		// 标记
//		b.putInt("TouchPost", TouchPost);
//		intent.putExtra("arrayList", data);
//		intent.putExtras(b);
//		startActivity(intent);
    }

    public void jsToProWebActivity(String shopID, String proName, String proPrice) {
        Intent intent = new Intent(getContext(), ProductDetailWeb.class);
        intent.putExtra(ProductDetailWeb.KEY_PAGE_TYPE, 0);
        intent.putExtra(DetailActivity.KEY_SHOP_ID, shopID);
        intent.putExtra(ProductDetailWeb.KEY_PRO_NAME, proName);
        intent.putExtra(ProductDetailWeb.KEY_PRO_PRICE, proPrice);
        startActivity(intent);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.KEYCODE_SOFT_LEFT)) {
            keyword = mEditText.getText().toString().trim();
            if (!keyword.equals("")) {
                resetData();
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                updateProductList();
            }
            return true;
        }
        return false;
    }

    private void resetData() {
        mEditText.setText("");
        pageindex = 1;
        isLastPage = false;
        data = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        keyword = mEditText.getText().toString().trim();
        if (!keyword.equals("")) {
            resetData();
            updateProductList();
        }
        if (v == b_title_back) {
            finish();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if ((firstVisibleItem + visibleItemCount == totalItemCount) && (totalItemCount != 0)) {
            if (refreshable && !isLastPage) {
                refreshable = false;
                loading.setVisibility(View.VISIBLE);
                pageindex += 1;
                updateProductList();
            }
        }
    }

}
