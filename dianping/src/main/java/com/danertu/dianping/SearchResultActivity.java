package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.danertu.adapter.SearchShopAdapter;
import com.danertu.tools.AppManager;

public class SearchResultActivity extends BaseActivity {

    private ListView lvResult;
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
        setContentView(R.layout.activity_search_result);
        String title = getIntent().getStringExtra("title");
        title = title == null ? "搜索结果" : title;
        initTitle(title);
        findViewById();
        initView();
    }

    @Override
    protected void findViewById() {
        lvResult = (ListView) findViewById(R.id.searchresultlist);
        noResource = findViewById(R.id.result_noResource);
    }

    @Override
    protected void initView() {
        bindData();
        lvResult.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> item = (Map<String, Object>) parent.getItemAtPosition(position);
                String shopid = String.valueOf(item.get("shopid"));
                String levelType = item.get("leveltype").toString();
                if (shopid.equals("dlts")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("shopid", shopid);
                    bundle.putString("shopName", String.valueOf(item.get("shopName")));
                    bundle.putString("mobile", String.valueOf(item.get("mobile")));
                    Intent intent = new Intent(SearchResultActivity.this, DetailActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    jsStartActivity("IndexActivity", "shopid|" + shopid + ",;shoptype|" + levelType);
                }

            }
        });

    }

    /**
     * 数据绑定
     */
    public void bindData() {
        noResource.setVisibility(View.GONE);
        showLoadDialog();
        String keyWord = getIntent().getExtras().getString("key");
        new SearchShop(keyWord) {
            public void content(List<Map<String, Object>> result) {
                hideLoadDialog();
                if (result != null && result.size() > 0) {
                    SearchShopAdapter adapter = new SearchShopAdapter(getContext(), result);
                    lvResult.setAdapter(adapter);
                } else {
                    if (lvResult != null && noResource != null) {
                        lvResult.setVisibility(View.GONE);
                        noResource.setVisibility(View.VISIBLE);
                    }
                }
            }
        }.execute();
    }

    /**
     * 商店搜索异步任务
     */
    static abstract class SearchShop extends AsyncTask<Void, Integer, List<Map<String, Object>>> {

        public abstract void content(List<Map<String, Object>> result);

        private String keyWord;

        public SearchShop(String keyWord) {
            this.keyWord = keyWord;
        }

        @Override
        protected List<Map<String, Object>> doInBackground(Void... params) {
            if (keyWord != null && keyWord.trim().length() > 0) {
                // 耗时操作
                String searchList = AppManager.getInstance().postGetSearchResult("0044", keyWord);
                JSONObject jsonObject;
                ArrayList<Map<String, Object>> data = new ArrayList<>();
                try {
                    jsonObject = new JSONObject(searchList).getJSONObject("supplierprocuctList");
                    JSONArray jsonArray = jsonObject.getJSONArray("supplierproductbean");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Map<String, Object> map = new HashMap<>();
                        JSONObject oj = jsonArray.getJSONObject(i);
                        String shopName = oj.getString("s");
                        String mobile = oj.getString("om");
                        mobile = TextUtils.isEmpty(mobile.trim()) ? oj.getString("m") : mobile;
                        String address = oj.getString("w");
                        String titleImg = oj.getString("e");
                        String jyfw = oj.getString("jyfw");
                        String shopid = oj.getString("id");
                        String la = oj.getString("la");
                        String lt = oj.getString("lt");
                        String levelType = oj.getString("leveltype");
                        map.put("shopName", shopName);
                        map.put("mobile", mobile);
                        map.put("address", address);
                        map.put("titleImg", titleImg);
                        map.put("jyfw", jyfw);
                        map.put("shopid", shopid);
                        map.put("la", la);
                        map.put("lt", lt);
                        map.put("leveltype", levelType);
                        data.add(map);
                    }
                    if (data.size() > 0)
                        return data;
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
