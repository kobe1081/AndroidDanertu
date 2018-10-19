package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import wl.codelibrary.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.dianping.R.color;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 1吃 2住 3玩 4购
 *
 * @author Administrator
 */
public class IndexTypeActivity extends BaseActivity {

    List<String> lis = null;
    private DropAdapter dropAdapter;
    private ArrayList<HashMap<String, Object>> data1;
    private String type = "";
    private ArrayList<HashMap<String, Object>> tmpdata;
    SwipeRefreshLayout pullList;
    ListView index_type_list;
    MyAdapter1 adapter1;
    int count = 0;
    // 设置一个最大的数据条数，超过即不再加载
    private int pageindex; // 当前页数
    public TextView footerload;
    String shopCount = "";
    String fromIndex = "";
    TextView index_type_select;
    Button index_type_title;
    private boolean isShow = false;
    private PopupWindow pop;
    private ListView dropListView;

    ArrayList<String> list_titles;
    Button b_title;

    private int pSize, pIndex, shopType;
    private double range;
    private String keyWord, searchType;
    public static final int WHAT_GETFOODLIST_SUCCESS = 3;
    public static final int WHAT_GETDATA_FAIL = -3;

    public static final int WHAT_GETTYPE = 1110;

    public static final int WHAT_GETFOODTYPE_SUCCESS = 5;
    public static final String KEY_SHOPLA = "shopLa";
    public static final String KEY_SHOPLT = "shopLt";
    private String la, lt;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int tempType = 1;
        uid=getUid();
        Bundle bundle = getIntent().getExtras();
        try {
            type = bundle.getString("type");

            //1吃 2住 3玩 4购
            tempType = Integer.parseInt(type);
        } catch (Exception e) {
            e.printStackTrace();
            type = "1";
        }

        initContainer();
        handler = new Handler(new HCallback());
        if (tempType == 1) {
            la = bundle.getString(KEY_SHOPLA);
            lt = bundle.getString(KEY_SHOPLT);
            setContentView(R.layout.activity_web);
            initWebContent();

        } else {

            setContentView(R.layout.activity_index_type);
            showLoadDialog();
            findViewById();

            initView(tempType);
            getFirstPage();
        }
    }

    public void finish() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            pIndex = 0;
            secID = 0;
            if (list_titles.size() > 1) {
                int index = list_titles.size() - 1;
                list_titles.remove(index);
                String title = list_titles.get(index - 1);
                b_title.setText(title);
            }
            return;
        }
        super.finish();
    }

    private void initListData(int type) {
        lis.clear();
        if (type > 1 && type < 5) {
            lis.add("全部");
            lis.add("住");
            lis.add("玩");
            lis.add("购");
        }
    }

    /**
     * html端调用此方法实例数据
     *
     * @param pageSize
     * @param keyWord    关键字，匹配店铺名
     * @param range      查询的距离范围，单位千米
     * @param shopType   查询店铺的所属类别
     * @param searchType 查询类别
     * @return 特定美食类型的店铺列表
     * @throws Exception
     */
    @JavascriptInterface
    public void jsInitFoodList(int pageSize, String keyWord, double range, int shopType, String searchType) {
        setpSize(pageSize);
        setpIndex(++pIndex);
        setKeyWord(keyWord);
        setRange(range);
        setShopType(shopType);
        setSearchType(searchType);
        runOnUiThread(new Runnable() {
            public void run() {
                new Thread(rGetFoodList).start();
            }
        });
    }

    @JavascriptInterface
    public void jsChangType(int pageSize, String keyWord, double range, int shopType, String searchType) {
        showLoadDialog();
        pIndex = 0;
        isChange = 1;
        jsInitFoodList(pageSize, keyWord, range, shopType, searchType);
    }

    @JavascriptInterface
    public void jsGetSecTypeJson() {//美食店铺列表获取数据的方法
        showLoadDialog();
        runOnUiThread(new Runnable() {
            public void run() {
                Runnable r = new RGetFoodType(firstType);
                new Thread(r).start();
            }
        });
    }

    public void javaLoadSecCate(int secID, String secTypeJson) {
        webView.loadUrl(Constants.IFACE + "javaLoadSecCate(" + secID + ",'" + secTypeJson + "')");
    }

    private int firstType;
    private int secID;

    @JavascriptInterface
    public void jsInitTitleId(final String title, int id, int tag) {//分类实例的数据
        if (tag == 1)
            this.firstType = id;
        else if (tag == 2) {
            this.secID = id;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                list_titles.add(title);
                b_title.setText(title);
            }
        });
    }

    int isChange;

    public void javaLoadFoodShop(String json) {
        webView.loadUrl(Constants.IFACE + "javaLoadData(" + isChange + ",'" + json + "')");
        isChange = 0;
    }

    private class HCallback implements Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_GETTYPE:
                    onRefresh();
                    break;

                case WHAT_GETFOODLIST_SUCCESS:
                    javaLoadFoodShop(msg.obj.toString());
                    break;

                case WHAT_GETFOODTYPE_SUCCESS:
                    String secTypeJson = msg.obj.toString();
                    javaLoadSecCate(secID, secTypeJson);
                    break;

                case WHAT_GETDATA_FAIL:
                    jsShowMsg(msg.obj.toString());
                    break;

            }
            hideLoadDialog();
            return false;
        }
    }

    public Runnable rGetFoodList = new Runnable() {
        public void run() {
            try {
                String msg = appManager.getFoodList(getpSize(), getpIndex(), getKeyWord(), (int) getRange(), getShopType(), getSearchType(), la, lt);
                msg = msg.replaceAll("\n|\r", "");
                sendMessage(WHAT_GETFOODLIST_SUCCESS, msg, null);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(WHAT_GETDATA_FAIL, e, null);
            }
        }
    };

    private class RGetFoodType implements Runnable {
        int type = 0;

        public RGetFoodType(int firstType) {
            this.type = firstType;
        }

        public void run() {
            try {
                String json = appManager.getFoodType(type);
                json = json.replaceAll("\n|\r", "");
                sendMessage(WHAT_GETFOODTYPE_SUCCESS, json, null);
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(WHAT_GETDATA_FAIL, e, null);
            }
        }
    }

    private void initContainer() {
        list_titles = new ArrayList<>();
        lis = new ArrayList<>();
    }

    final String ifaceName = "app";
    final String WEBPAGE_NAME = "Android_foodMenu.html";

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebContent() {
        b_title = (Button) findViewById(R.id.b_title_back2);
        Button b_opera = (Button) findViewById(R.id.b_title_operation2);
        b_opera.setVisibility(View.GONE);
        list_titles.add("单耳兔美食");
        b_title.setText(list_titles.get(0));

        b_title.setOnClickListener(webClick);

        webView = (WebView) findViewById(R.id.wv_container);
        WebSettings setting = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存
        webView.addJavascriptInterface(this, ifaceName);
        webView.setWebViewClient(new MWebClient(this, ifaceName));

        String url = null;
        if (la == null && lt == null) {
            la = Constants.getLa();
            lt = Constants.getLt();
            url = Constants.appWebPageUrl + WEBPAGE_NAME;
        } else {
            if (!la.contains(".") || !lt.contains(".")) {
                try {
                    double tLa = Double.parseDouble(la) / 1000000;
                    double tLt = Double.parseDouble(lt) / 1000000;
                    la = String.valueOf(tLa);
                    lt = String.valueOf(tLt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            url = Constants.appWebPageUrl + "Android_food_type.html";
        }
        webView.loadUrl(url);
    }

    private class MWebClient extends MWebViewClient {

        MWebClient(Activity act, String ifaceName) {
            super(act, ifaceName);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoadDialog();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showLoadDialog();
        }
    }

    public View.OnClickListener webClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_title_back2:
                    finish();
                    break;
                case R.id.b_indexType_back:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void findViewById() {
        index_type_select = (TextView) findViewById(R.id.index_type_select);
        index_type_title = (Button) findViewById(R.id.b_indexType_back);
        pullList = (SwipeRefreshLayout) findViewById(R.id.srl_lv);
        pullList.setColor(color.red, color.palegreen, color.yellow, color.green);
        pullList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                pullList.setRefreshing(false);
            }
        });
        pullList.setOnLoadListener(new SwipeRefreshLayout.OnLoadListener() {
            public void onLoad() {
                loadMoreDate();
            }
        });
        index_type_list = (ListView) findViewById(R.id.index_type_list);

        index_type_title.setOnClickListener(webClick);
    }

    protected void initView(int type) {
        initListData(type);
        switch (type) {
            case 1:
                index_type_select.setText("吃");
                break;
            case 2:
                index_type_select.setText("住");
                break;
            case 3:
                index_type_select.setText("玩");
                break;
            case 4:
                index_type_select.setText("购");
                break;
            case 5:
                index_type_select.setText("烟酒茶");
                index_type_select.setEnabled(false);
                index_type_select.setCompoundDrawables(null, null, null, null);
                break;
        }
        index_type_select.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (pop == null) {
                    if (dropAdapter == null) {
                        float width = CommonTools.dip2px(getContext(), 130);
                        dropAdapter = new DropAdapter();
                        dropListView = new ListView(IndexTypeActivity.this);
                        pop = new PopupWindow(dropListView, (int) width, LayoutParams.WRAP_CONTENT);
                        dropListView.setAdapter(dropAdapter);
                        pop.showAsDropDown(index_type_select);
                        isShow = true;
                    }
                } else if (isShow) {
                    pop.dismiss();
                    isShow = false;
                } else if (!isShow) {
                    pop.showAsDropDown(index_type_select);
                    isShow = true;
                }
            }
        });

    }

    private class DropAdapter extends BaseAdapter {
        LayoutInflater mInflater;

        public DropAdapter() {
            mInflater = LayoutInflater.from(IndexTypeActivity.this);
        }

        public int getCount() {
            return lis.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            final String name = lis.get(position);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.index_type_down, null);
                holder = new Holder();
                holder.typeName = (TextView) convertView.findViewById(R.id.index_type_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (holder != null) {
                convertView.setId(position);
                holder.setId(position);
                holder.typeName.setText(name);
                holder.typeName.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectType(position, name);
                    }
                });
            }
            return convertView;
        }

        void selectType(int position, String name) {
            // 1为吃，吃已改为新版样式，所以position大于0时要加1
            if (position == 0) {
                type = "";
            } else {
                type = String.valueOf(position + 1);
            }
//			index_type_title.setText(name);
            index_type_select.setText(name);
            pop.dismiss();
            isShow = false;
            showLoadDialog();
            getFirstPage();
        }

        class Holder {
            TextView typeName;

            void setId(int position) {
                typeName.setId(position);
            }
        }

    }

    public void getFirstPage() {
        pageindex = 1;
        new Thread(sendAble).start();
        if (data1 == null) {
            data1 = new ArrayList<>();
        } else {
            data1.clear();
        }
        index_type_list.setSelection(0);
    }

    private class MyAdapter1 extends BaseAdapter {

        private class ViewHolder {
            ImageView imgView;
            TextView shopName;
            TextView address;
            TextView jy;
            TextView juli;
        }

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
        public View getView(int position, View view, ViewGroup arg2) {
            ViewHolder vh;
            if (view == null) {
                LayoutInflater inflater = IndexTypeActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.expandnearby_item, null);
                ImageView imgView = (ImageView) view.findViewById(R.id.ivIcon);
                TextView shopName = (TextView) view.findViewById(R.id.index_type_shopName);
                TextView address = (TextView) view.findViewById(R.id.index_type_address);
                TextView jy = (TextView) view.findViewById(R.id.index_type_jyfw);
                TextView juli = (TextView) view.findViewById(R.id.index_type_juli);
                vh = new ViewHolder();
                vh.imgView = imgView;
                vh.shopName = shopName;
                vh.address = address;
                vh.jy = jy;
                vh.juli = juli;
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }
            String imgName = data1.get(position).get("img").toString();
            String shopid = data1.get(position).get("shopid").toString();
            String imgUrl = ActivityUtils.getImgUrl(imgName, shopid, null,uid);
            vh.shopName.setText(data1.get(position).get("shopname").toString().trim());
            vh.address.setText(data1.get(position).get("adress").toString().trim());
            vh.jy.setText(data1.get(position).get("jyfw").toString().trim());
            vh.juli.setText(data1.get(position).get("juli").toString());
            ImageLoader.getInstance().displayImage(imgUrl, vh.imgView);
            return view;

        }

    }

    private void loadMoreDate() {
        if (!isLoading()) {
            showLoadDialog();
            new Thread(sendAble).start();
        }

    }

    private Runnable sendAble = new Runnable() {
        @Override
        public void run() {
            String result="";
            try {
                 result = appManager.postGetIndexShopList("0037", Constants.getCityName(), Constants.pageSize, pageindex, "", type,uid);
                JSONObject jsonObject;
                jsonObject = new JSONObject(result).getJSONObject("shoplist");
                JSONArray jsonArray = jsonObject.getJSONArray("shopbean");
                tmpdata = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("shopname", oj.getString("s"));
                    item.put("img", oj.getString("e"));
                    item.put("mobile", oj.getString("m"));
                    item.put("adress", oj.getString("w"));
                    item.put("shopid", oj.getString("id"));
                    item.put("jyfw", oj.getString("jyfw"));
                    item.put("juli", oj.getString("juli") + "km");

                    tmpdata.add(item);
                }
                pageindex++;
                sendMessage(WHAT_GETTYPE, null, null);
            } catch (Exception e) {
                judgeIsTokenException(result,"您的登录信息已过期，请重新登录",-1);
                if (pullList != null) {
                    pullList.setLoading(false);
                }
                sendMessage(WHAT_GETDATA_FAIL, "所有店铺已加载完成", null);
            }
        }
    };

    public void onRefresh() {
        if (tmpdata != null) {
            data1.addAll(tmpdata);
            tmpdata = null;
        }
        if (adapter1 == null) {
            adapter1 = new MyAdapter1();
            index_type_list.setAdapter(adapter1);
            index_type_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
//					position = position - 1;
//					position = position < 0 ? 0 : position;
                    String shopid = data1.get(position).get("shopid").toString();
                    Intent intent = new Intent();
                    intent.setClass(getContext(), DetailActivity.class);
                    // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
                    Bundle b = new Bundle();
                    b.putString("shopid", shopid);
                    // 此处使用putExtras，接受方就响应的使用getExtra
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

        } else {
            adapter1.notifyDataSetChanged();
            int last = index_type_list.getLastVisiblePosition();
            if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                int offset = 0;
                int duration = 330;
                index_type_list.smoothScrollToPositionFromTop(last, offset, duration);
            } else {
                int first = index_type_list.getFirstVisiblePosition();
                int rang = last - first;
                index_type_list.smoothScrollToPosition(last + rang);
            }
            pullList.setLoading(false);
        }
    }

    @JavascriptInterface
    public static Integer GetJuli(double lat1, double lon1, double lat2, double lon2) {
        int wuce = 1000000;
        Double poA = Math.abs(lat1 * wuce - lat2 * wuce);
        Double poB = Math.abs(lon1 * wuce - lon2 * wuce);
        Double wwc = 0.9296;// 换算百度精准倍率
//        return Integer.valueOf((int) (Math.sqrt((poA * poA) + (poB * poB)) / 10 / wwc));
        return (int) (Math.sqrt((poA * poA) + (poB * poB)) / 10 / wwc);
    }

    @JavascriptInterface
    public int getpSize() {
        return pSize;
    }

    @JavascriptInterface
    public void setpSize(int pSize) {
        this.pSize = pSize;
    }

    @JavascriptInterface
    public int getpIndex() {
        return pIndex;
    }

    @JavascriptInterface
    public void setpIndex(int pIndex) {
        this.pIndex = pIndex;
    }

    @JavascriptInterface
    public double getRange() {
        return range;
    }

    @JavascriptInterface
    public void setRange(double range) {
        this.range = range;
    }

    @JavascriptInterface
    public String getKeyWord() {
        return keyWord;
    }

    @JavascriptInterface
    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @JavascriptInterface
    public int getShopType() {
        return shopType;
    }

    @JavascriptInterface
    public void setShopType(int shopType) {
        this.shopType = shopType;
    }

    @JavascriptInterface
    public String getSearchType() {
        return searchType;
    }

    @JavascriptInterface
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    @Override
    protected void initView() {
    }

}
