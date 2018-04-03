package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.danertu.adapter.SearchProductAdapter;
import com.danertu.tools.AppManager;

public class SearchProductResultActivity extends BaseActivity {

    //	private List<Map<String, Object>> data;
    private ListView resultList;
    private View noResource;

    private void initTitle(String title) {
        Button b_title = (Button) findViewById(R.id.b_order_title_back);
        b_title.setText(title);
        b_title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product_result);
        String title = getIntent().getStringExtra("title");
        title = title == null ? "搜索结果" : title;
        initTitle(title);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        noResource = findViewById(R.id.result_noResource);
//		goback = (TextView) findViewById(R.id.goBack);
        resultList = (ListView) findViewById(R.id.resultlist);

        resultList.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                String guid = String.valueOf(item.get("guid").toString());
                jsStartActivity("ProductDetailsActivity2", "guid|" + guid + ",;shopid|" + getShopId());
                finish();
            }
        });
    }

    int GET_PRODATA = 1;
    int GET_PRODATA_FAIL = -1;

    @Override
    protected void initView() {
        noResource.setVisibility(View.GONE);
        showLoadDialog();
        String keyWord = getIntent().getExtras().getString("key");
        new SearchProduct(keyWord) {
            public void content(List<Map<String, Object>> result) {
                hideLoadDialog();
                if (result != null) {
                    SearchProductAdapter adapter = new SearchProductAdapter(getContext(), result);
                    resultList.setAdapter(adapter);
                } else {
                    noResource.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
    }

    static abstract class SearchProduct extends AsyncTask<Void, Integer, List<Map<String, Object>>> {

        public abstract void content(List<Map<String, Object>> result);

        private String keyWord;

        public SearchProduct(String keyWord) {
            this.keyWord = keyWord;
        }

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            if (TextUtils.isEmpty(keyWord))
                return null;
            String result = AppManager.getInstance().postGetSearchProductResult("0045", keyWord);
            ArrayList<Map<String, Object>> data = new ArrayList<>();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("searchprocuctList");
                JSONArray jsonArray = jsonObject.getJSONArray("searchproductbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    String name = oj.getString("Name");
                    int index = name.indexOf("|");
                    String proName = "";
                    String proFormat = "";
                    if (index != 0) {
                        proName = name.substring(0, index);
                        proFormat = name.substring(index + 1, name.length());
                    } else {
                        proName = name;
                    }
                    item.put("guid", oj.getString("Guid"));
                    item.put("proName", proName);
                    item.put("proFormat", proFormat);
                    item.put("img", oj.getString("SmallImage"));
                    item.put("detail", oj.getString("mobileProductDetail"));
                    item.put("agentID", oj.getString("AgentID"));
                    item.put("supplierID", oj.getString("SupplierLoginID"));
                    item.put("price", oj.getString("ShopPrice"));
                    item.put("marketPrice", oj.getString("MarketPrice"));

                    data.add(item);
                }
                if (data.size() > 0)
                    return data;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            super.onPostExecute(result);
            content(result);
        }
    }

}
