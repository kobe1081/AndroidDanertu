package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import com.config.Constants;
import com.danertu.adapter.BaseExpandableAdapter;
import com.danertu.entity.Category;
import com.danertu.entity.SecondCategory;
import com.danertu.entity.ThreeCategory;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.Logger;
import com.danertu.widget.MWebViewClient;

public class SecondCategoryActivity extends BaseActivity {

    private Spinner spinner;
    private ArrayAdapter<Category> adapter;
    private ArrayList<HashMap<String, Object>> categoryData = new ArrayList<>();
    ArrayList<HashMap<String, Object>> threeData;
    private ArrayList<HashMap<String, Object>> data1;
    private String secondID = "";
    ExpandableListView expandTitle;
    private List<SecondCategory> parentList = new ArrayList<>();
    private Map<String, List<ThreeCategory>> childList = new HashMap<>();
    private List<ThreeCategory> childItem = new ArrayList<>();
    String id = "";
    BaseExpandableAdapter ExpandAdapter;
    int selected = 0;
    String nameFrom1 = "";
    Handler dataHandler;
    Button second_back;
    Context context = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_second_category);
        dataHandler = new Handler();
        showLoadDialog();
        try {
            findViewById();
            initFirstCateData();
            initWebContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void initFirstCateData() {
        id = getIntent().getExtras().getString("id");
        nameFrom1 = getIntent().getExtras().getString("name");
        categoryData = (ArrayList<HashMap<String, Object>>) getIntent().getExtras().get("categoryData");
        if (categoryData == null) {
            new InitFirstCateData().execute();
        } else {
            initView();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebContent() {
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.addJavascriptInterface(this, WEBINTERFACE_NAME);
        webView.loadUrl(Constants.appWebPageUrl + WEBPAGE_NAME);
        webView.setWebViewClient(new MWebViewClient(this, WEBINTERFACE_NAME) {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//				new Thread(dataRunnable).start();
                new getData().execute();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

        });
    }

    public final String WEBPAGE_NAME = "Android_classify_twoAndthree1.html";
    public final String WEBINTERFACE_NAME = "app";
    final String INTERACTIVE_NAME = "javascript:";

    @Override
    protected void findViewById() {
        spinner = (Spinner) findViewById(R.id.category_spinner);
        second_back = (Button) findViewById(R.id.second_category_btn_back);
        second_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView = (WebView) findViewById(R.id.wv_sec_content);
    }

    private class InitFirstCateData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... param) {
            try {
                if (categoryData == null) {
                    String result = AppManager.getInstance().getFirstCategory("0073");
                    categoryData = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(result).getJSONObject("firstCategoryList");
                    JSONArray jsonArray = jsonObject.getJSONArray("firstCategorybean");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject oj = jsonArray.getJSONObject(i);
                        HashMap<String, Object> item = new HashMap<>();
                        item.put("id", oj.get("ID"));
                        item.put("name", oj.get("Name"));
                        item.put("key", oj.get("Keywords"));
                        item.put("family", oj.get("Family"));
                        item.put("img", oj.getString("PhoneImage"));
                        categoryData.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            initView();
        }

    }

    @Override
    protected void initView() {
        ArrayList<Category> list = SetCategory(categoryData);
        adapter = new ArrayAdapter<>(this, R.layout.spinner_style, list);
        adapter.setDropDownViewResource(R.layout.down);
        spinner.setAdapter(adapter);
        spinner.setSelection(selected, true);
        // 添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        // 设置默认值
        spinner.setVisibility(View.VISIBLE);

    }

    // 使用数组形式操作
    private class SpinnerSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            id = ((Category) spinner.getSelectedItem()).GetID();
            parentList.clear();
            childList.clear();
            childItem.clear();
            showLoadDialog();
//			new Thread(dataRunnable).start();
            new getData().execute();

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    private ArrayList<Category> SetCategory(ArrayList<HashMap<String, Object>> categoryData2) {
        this.categoryData = categoryData2;
        ArrayList<Category> list = new ArrayList<>();
        for (int i = 0; i < categoryData.size(); i++) {
            String id = categoryData.get(i).get("id").toString();
            String name = categoryData.get(i).get("name").toString();
            if (id.equals(this.id)) {
                selected = i;
            }
            Category c = new Category(id, name);
            list.add(c);
        }
        return list;
    }

    /**
     * 实例化好二级分类数据
     *
     * @throws Exception
     */
    public void initSecData() throws Exception {

        String result = AppManager.getInstance().GetSecondCategory("0074", id);
        Logger.i("二级分类数据", id + " , " + result);

        data1 = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(result).getJSONObject("secondCategoryList");
        JSONArray jsonArray = jsonObject.getJSONArray("secondCategorybean");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject oj = jsonArray.getJSONObject(i);
            HashMap<String, Object> item = new HashMap<>();
            item.put("ID", oj.get("ID"));
            item.put("Name", oj.get("Name"));
            item.put("Keywords", oj.get("Keywords"));
            item.put("Family", oj.get("Family"));
            item.put("PhoneImage", oj.getString("PhoneImage"));
            data1.add(item);
        }
    }

    public void javaInitThreeItem(final String threeItem) {
        runOnUiThread(new Runnable() {
            public void run() {
                webView.loadUrl(INTERACTIVE_NAME + "javaInitThreeItem('" + threeItem + "')");
            }
        });
    }

    @JavascriptInterface
    public void jsLoadThreeData(final String secID) {
        runOnUiThread(new Runnable() {
            public void run() {
                String threeItem = gson.toJson(childList.get(secID));
                javaInitThreeItem(threeItem);
            }
        });
    }

    /**
     * 实例化好三级分类数据
     */
    public boolean initThreeData(String secID, String parent) {
        childItem = new ArrayList<>();

        String result = AppManager.getInstance().postGetThreeCategory(secID);
        Logger.i("三级分类数据", parent + " , " + secID + " , " + result);

        try {
            if ("".equals(result)) {
                return false;
            }
            JSONObject jsonObject = new JSONObject(result).getJSONObject("secondCategoryList");
            JSONArray jsonArray = jsonObject.getJSONArray("secondCategorybean");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject oj = jsonArray.getJSONObject(i);
                String id = oj.getString("ID");
                String name = oj.getString("Name");
                String keyWords = oj.getString("Keywords");
                String family = oj.getString("Family");
                String img = oj.getString("PhoneImage");
                String backgroundImg = oj.getString("BackgroundImage");

                ThreeCategory c3 = new ThreeCategory();
                c3.setThreeID(id);
                c3.setCategoryName(name);
                c3.setKeyWords(keyWords);
                c3.setFamily(family);
                c3.setPhoneImage(img);
                c3.setBackgroundImage(backgroundImg);
                childItem.add(c3);
            }

            if (childItem.size() > 0) {
                childList.put(secID, childItem);
                return true;
            } else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void init() {

        for (int i = 0; i < data1.size(); i++) {
            String parent = data1.get(i).get("Name").toString();
            secondID = data1.get(i).get("ID").toString();
            SecondCategory c2 = new SecondCategory(secondID, parent);
            parentList.add(c2);

            if (!initThreeData(secondID, parent)) {
                data1.remove(i);
                i--;
            }

        }

    }

    @JavascriptInterface
    public void jsClickThreeItem(String threeID, String threeName) {
        Intent intent = new Intent();
        intent.putExtra("threeID", threeID);
        intent.putExtra("name", threeName);
        intent.putExtra("shopid", getShopId());
        intent.setClass(SecondCategoryActivity.this, CategoryProductActivity.class);
        startActivity(intent);
    }

    public class getData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            try {
                initSecData();
                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String secJson = gson.toJson(data1);
            webView.loadUrl(INTERACTIVE_NAME + "initTwoData('" + secJson + "')");
            hideLoadDialog();
        }

    }

    @JavascriptInterface
    public void jsStartIndexBannerToActivity(String toActivityName, String paraStr) {
        try {

            Intent intent = new Intent(this, Class.forName("com.danertu.dianping." + toActivityName));
            Bundle b = new Bundle();
            String[] strList = paraStr.split(",;");
            for (String aStrList : strList) {
                b.putString(aStrList.substring(0, aStrList.indexOf("|")), aStrList.substring(aStrList.indexOf("|") + 1));
            }
            intent.putExtras(b);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

//	Runnable secondRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//
//			String result = AppManager.getInstance().GetSecondCategory("0074",
//					id);
//			data1 = new ArrayList<HashMap<String, Object>>();
//			try {
//				JSONObject jsonObject = new JSONObject(result)
//						.getJSONObject("secondCategoryList");
//				JSONArray jsonArray = jsonObject
//						.getJSONArray("secondCategorybean");
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject oj = jsonArray.getJSONObject(i);
//					HashMap<String, Object> item = new HashMap<String, Object>();
//					item.put("id", oj.get("ID"));
//					item.put("name", oj.get("Name"));
//					item.put("key", oj.get("Keywords"));
//					item.put("family", oj.get("Family"));
//					item.put("img", oj.getString("PhoneImage"));
//					data1.add(item);
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	};
//
//	Runnable threeRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//			threeData = new ArrayList<HashMap<String, Object>>();
//			String result = AppManager.getInstance().postGetThreeCategory(secondID);
//			Log.i("三级分类数据", result+"");
//			try {
//				JSONObject jsonObject = new JSONObject(result)
//						.getJSONObject("secondCategoryList");
//				JSONArray jsonArray = jsonObject
//						.getJSONArray("secondCategorybean");
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject oj = jsonArray.getJSONObject(i);
//					HashMap<String, Object> item = new HashMap<String, Object>();
//					item.put("id", oj.get("ID"));
//					item.put("name", oj.get("Name"));
//					item.put("key", oj.get("Keywords"));
//					item.put("family", oj.get("Family"));
//					item.put("img", oj.getString("PhoneImage"));
//					threeData.add(item);
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//	};

}
