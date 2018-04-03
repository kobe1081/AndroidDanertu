package com.danertu.dianping;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;
import com.danertu.widget.QuickActionWidget;
import com.danertu.widget.QuickActionWidget.OnQuickActionClickListener;

public class DetailActivity extends BaseWebActivity {
    private String la, lt;
    private String banner;
    ListView listView;
    LinearLayout loadingLayout;
    LinearLayout toolbar;
    private String agentID = "";

    private Context context = null;
    private EditText mEditText;
    private DWebClient dWebClient;
    private boolean isFirst = true;
    private String phoneNumber = "";
    private String jyfw = "";
    private String shopName = "";
    private String address = "";
    private String shopId = null;
    final int TYPE_SIMPLE = 1, TYPE_COMMON = 0, TYPE_FOOD = 2;

    //---变量-----------------------
    /**
     * 0为旧版店铺页面，1为简版店铺页面，2美食店铺页面
     */
    private int shopType = 0;
    private String proListJson = null;
    private String shopJson = null;
    private String shopCommentJson = null;

    @JavascriptInterface
    public String getLa() {
        return la;
    }

    @JavascriptInterface
    public String getLt() {
        return lt;
    }

    @JavascriptInterface
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JavascriptInterface
    public String getShopId() {
        return shopId;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.poidetail2);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        shopId = bundle.getString(KEY_SHOP_ID);
        showLoadDialog();
        //启动获取店铺详情异步任务
        new GetShopDetail().execute(shopId);
    }

    final static int WHAT_GETPRO_SUCCESS = 12;
    final static int WHAT_SHOPDETAIL_SUCCESS = 11;
    private long time_shop, time_pro;
    private final MyHandler initViewHandler = new MyHandler(this);

    public static class MyHandler extends Handler {
        WeakReference<Activity> wAct;

        public MyHandler(Activity act) {
            wAct = new WeakReference<>(act);
        }

        public void handleMessage(Message msg) {
            DetailActivity da = (DetailActivity) wAct.get();
            if (msg.obj == null) {
                da.jsShowMsg("店铺数据出现异常！");
                da.finish();
                return;
            }
            if (msg.what == WHAT_SHOPDETAIL_SUCCESS) {
                //获取店铺详情成功，开始加载html内容
                da.shopJson = msg.obj.toString();
                String shopDetail = da.shopJson;
                da.javaLoadContent(shopDetail, 2);
                if (da.time_shop >= da.time_pro)
                    da.hideLoadDialog();
            } else if (msg.what == WHAT_GETPRO_SUCCESS) {
                da.proListJson = msg.obj.toString();
                String proStr = da.proListJson;
                da.javaLoadContent(proStr, 1);
                if (da.time_pro >= da.time_shop)
                    da.hideLoadDialog();
            } else if (msg.what == ShopCommentActivity.WHAT_GET_COMMENT_SUCCESS) {
                da.shopCommentJson = msg.obj.toString();
                da.javaLoadContent(da.shopCommentJson, 3);
                if (da.time_shop >= da.time_pro)
                    da.hideLoadDialog();
            }
        }
    }

    final String WEBPAGE_NAME = "Android_shop2.html";
    final String WEBPAGE_NAME1 = "Android_shop_new2.html";
    final String WEBPAGE_FOOD = "Android/food_shop2.html";

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView() {
        dWebClient = new DWebClient(this, IndexActivity.WV_INTERFACE);
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, IndexActivity.WV_INTERFACE);
        webView.setWebViewClient(dWebClient);

        String pageName = null;
        int index = 0;
        if (shopId.equals("dlts")) {
            pageName = "Android/sepcial_shop.html";
        } else if (shopType == TYPE_SIMPLE) {
            pageName = WEBPAGE_NAME1;
        } else if (shopType == TYPE_FOOD) {
            pageName = WEBPAGE_FOOD;
        } else if (shopType == TYPE_COMMON) {
            pageName = WEBPAGE_NAME;
        } else if ((index = shopType - 10) > 0) {
            pageName = "Android/shop_template_" + index + ".html";
        } else {
            pageName = "alcohol_shop.html";
        }
        webView.loadUrl(Constants.appWebPageUrl + pageName);
    }

    @JavascriptInterface
    public void jsToShopComment() {
        Bundle pBundle = new Bundle();
        pBundle.putString(KEY_SHOP_ID, shopId);
        pBundle.putString(ShopCommentActivity.KEY_SHOP_JSON, shopJson);
        pBundle.putString(ShopCommentActivity.KEY_SHOP_COMMENT_JSON, shopCommentJson);
        openActivity(ShopCommentActivity.class, pBundle);
    }

    public class DWebClient extends MWebViewClient {
        public DWebClient(Context act, String ifaceName) {
            super(act, ifaceName);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Logger.v("页面加载完毕", "开启线程获取数据");
            if (isFirst) {
                if (shopType != 1)
                    new Thread(tGetprocList).start();
                new Thread(tGetShopDetails).start();
                isFirst = false;
            }
        }
    }

    /**
     * 加载html内容
     *
     * @param jsonStr json数据
     * @param type    0表示代理商商品数据， 1表示线下产品数据， 2表示店铺信息数据
     * @author dengweilin
     */
    private void javaLoadContent(String jsonStr, int type) {
        if (jsonStr == null) {
            CommonTools.showShortToast(context, "数据异常：" + type);
            return;
        }
        Logger.i("数据---", jsonStr + "");
        String realJson = jsonStr.replaceAll("\n", "");
        realJson = realJson.replaceAll("\r", "");
//        if (type == 0) {
//            // tShoplistjson = jsonStr;
//            webView.loadUrl("javascript:javaLoadPro('" + realJson + "')");
//        } else if (type == 1) {
//            Logger.i("线下产品数据", realJson + "");
//            webView.loadUrl("javascript:javaLoadOffline('" + realJson + "')");
//        } else if (type == 2) {
//            Logger.i("店铺信息数据", realJson + "");
//            webView.loadUrl("javascript:javaLoadShopDetail('" + realJson + "')");
//        } else if (type == 3) {//店铺评论
//            Logger.i("店铺评论数据", realJson + "");
//            webView.loadUrl(Constants.IFACE + "javaLoadShopComment('" + realJson + "')");
//        }
        switch (type) {
            case 0:
                webView.loadUrl("javascript:javaLoadPro('" + realJson + "')");
                break;
            case 1:
                Logger.i("线下产品数据", realJson + "");
                webView.loadUrl("javascript:javaLoadOffline('" + realJson + "')");
                break;
            case 2:
                Logger.i("店铺信息数据", realJson + "");
                webView.loadUrl("javascript:javaLoadShopDetail('" + realJson + "')");
                break;
            case 3:
                Logger.i("店铺评论数据", realJson + "");
                webView.loadUrl(Constants.IFACE + "javaLoadShopComment('" + realJson + "')");
                break;
        }
    }

    /**
     * 由web端调用的加载更多“泉眼商品”的方法
     */
    boolean isLoading = false;

    @JavascriptInterface
    public void jsLoadMorePro() {
        if (!isLoading) {
//			 threadPool.submit(suppeRunnable);
//			 isLoading = true;
        }
    }

    @JavascriptInterface
    public void jsPhoneCall() {
        if (TextUtils.isEmpty(phoneNumber)) {
            jsShowMsg("暂无商家号码！");
            return;
        }
        Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent1);
    }

    /**
     * web端实例化好Java端店铺信息
     *
     * @param shopName  店铺名
     * @param jyfw      经营范围
     * @param address   店铺地址
     * @param phoneNum  店铺联系号码
     * @param bannerUrl 店铺图标url
     */
    @JavascriptInterface
    public void jsInitShopData(String shopName, String jyfw, String address, String phoneNum, String bannerUrl, String la, String lt) {
        this.jyfw = jyfw;
        this.phoneNumber = phoneNum;
        this.address = address;
        this.shopName = shopName;
        this.banner = bannerUrl;
        this.la = la;
        this.lt = lt;
        Logger.i("shopData---", shopName + " , " + jyfw + " , " + address + " , " + phoneNum + " , " + bannerUrl);
    }

    @JavascriptInterface
    public void jsInitAgentID(String agentID) {
        this.agentID = agentID;
    }

    /**
     * 线下产品的html点击事件, 把线下产品的json传到商品详细页
     *
     * @param index 控件底标(json选中项的底标)
     */
    @JavascriptInterface
    public void jsClickItem(int index) {
        Logger.e("Json_item", index + "");
        String guid = "";
        String agentid = "";
        try {
            JSONObject item = new JSONObject(proListJson).getJSONObject("shopprocuctList").getJSONArray("shopproductbean").getJSONObject(index);
            guid = item.getString("Guid");
            agentid = item.getString("AgentId");

            JSONObject shopObj = new JSONObject(shopJson).getJSONObject("shopdetails").getJSONArray("shopbean").getJSONObject(0);
            String mobileBanner = shopObj.getString("Mobilebanner");
            String imgPath = TextUtils.isEmpty(mobileBanner) ? shopObj.getString("EntityImage") : mobileBanner;
            imgPath = "http://img.danertu.com/Member/" + shopObj.getString("m") + "/" + imgPath;
            String param = "shopName|" + shopObj.getString("ShopName")
                    + ",;proName|" + item.getString("Name")
                    + ",;imgPath|" + imgPath + ",;guid|" + guid + ",;"
                    + WapActivity.KEY_WEB_TITLE + "|折扣卡,;"
                    + WapActivity.KEY_WEB_URL + "|" + Constants.appWebPageUrl
                    + "Android/discount_take.html";

            String zkJson = getData("apiid|0128,;productGuid|" + guid);
            JSONObject obj = new JSONObject(zkJson);
            String isDaZheKa = obj.getString("result");
            if (Boolean.parseBoolean(isDaZheKa)) {
                jsStartActivity("WapActivity", param);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (shopType == TYPE_FOOD) {
                String paraStr = "guid|" + guid + ",;shopid|" + shopId + ",;agentID|" + shopId + ",;mobile|" + phoneNumber
                        + ",;endName|" + shopName + ",;cityName|" + Constants.getCityName() + ",;la|" + la + ",;lt|" + lt;
                jsStartActivity("ProductDetailsActivity2", paraStr);
            } else {
                if (TextUtils.isEmpty(agentid)) {
                    ActivityUtils.toProDetail2(context, guid, null, null, null, shopId, null, null, null, phoneNumber, null, 2);
                    return;
                }
                Intent intent = new Intent(this, ProductDetailWeb.class);
                intent.putExtra(ProductDetailWeb.KEY_PRO_INDEX, index);
                intent.putExtra(ProductDetailWeb.KEY_PRO_LIST_JSON, proListJson);
                intent.putExtra(ProductDetailWeb.KEY_SHOP_JSON, shopJson);
                intent.putExtra(ProductDetailWeb.KEY_PAGE_TYPE, 0);
                intent.putExtra(KEY_SHOP_ID, shopId);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void collect() {
        String uid = db.GetLoginUid(DetailActivity.this);
        Intent intent = new Intent();
        if (uid == null || uid.trim().equals("")) {
            CommonTools.showShortToast(DetailActivity.this, "请先登录");
            intent.setClass(DetailActivity.this, LoginActivity.class);
            startActivity(intent);

        } else {
            Cursor cursor = db.allreadyCollectShop(DetailActivity.this, shopId, uid);
            if (cursor.getCount() > 0) {
                CommonTools.showShortToast(DetailActivity.this, "该店铺您已经收藏过了！");
            } else {
                db.InsertCollectShop(context, shopId, shopName, address, phoneNumber, jyfw, banner, uid);
                CommonTools.showShortToast(DetailActivity.this, "收藏店铺成功！");
            }
        }
    }

    @JavascriptInterface
    public void toCalendar() {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.danertu.dianping.CalendarActivity");
        startActivity(intent);
    }

    @JavascriptInterface
    public void huDong() {
        String uid = db.GetLoginUid(DetailActivity.this);
        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        if (uid != null && uid.trim().length() > 0) {
            bundle.putString("foruid", shopId);
            intent.putExtras(bundle);
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.HuDongActivity");

        } else {
            CommonTools.showShortToast(DetailActivity.this, "请先登录！");
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.LoginActivity");
        }
        startActivity(intent);
    }

    @Override
    protected void findViewById() {
        webView = (WebView) findViewById(R.id.wv_detail_content);
        mEditText = (EditText) findViewById(R.id.search_edit);
    }

    public static final String KEY_SHOP_ID = "shopid";
    /**
     * 1为简版店铺页面，否则正常店铺页面
     */
    public static final String KEY_SHOP_TYPE = "shoptype";

    protected void initView() {
        findViewById(R.id.search_btn).setOnClickListener(this);//设置点击监听事件
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView arg0, int actionId, KeyEvent arg2) {
                if (actionId == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_DONE) {
                    search();
                }
                return false;
            }
        });
    }

    @JavascriptInterface
    public void toShareActivity() {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), "com.danertu.dianping.QRCodeActivity");
        Bundle b = new Bundle();
        b.putString("shopid", shopId);

        // 此处使用putExtras，接受方就响应的使用getExtra
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * 获取店铺线下产品
     */
    private Runnable tGetprocList = new Runnable() {
        @Override
        public void run() {
            // 耗时操作
            String shopProductResult = AppManager.getInstance().postGetShopProduct("0042", shopId);
            time_pro = System.currentTimeMillis();
            Message msg = new Message();
            msg.obj = shopProductResult;
            msg.what = WHAT_GETPRO_SUCCESS;
            initViewHandler.sendMessage(msg);
        }
    };

    String shopDetailJson = "";

    /**
     * 获取店铺详情异步任务
     */
    public class GetShopDetail extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... arg0) {
            try {
                String shopId = arg0[0];
                String result = appManager.postGetShopDetails("0041", shopId);
                shopDetailJson = result;
                JSONObject obj = new JSONObject(result).getJSONObject("shopdetails").getJSONArray("shopbean").getJSONObject(0);
                String sType = obj.getString("leveltype");
                return TextUtils.isEmpty(sType) ? 0 : Integer.parseInt(sType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer levelType) {
            super.onPostExecute(levelType);
            shopType = levelType;
            findViewById();
            initView();
            initWebView();
        }


    }

    /**
     * 获取店铺详细信息
     */
    private Runnable tGetShopDetails = new Runnable() {
        @Override
        public void run() {
            // 耗时操作
            AppManager am = AppManager.getInstance();
            String result = null;
            Message msg = null;
            try {
                result = shopDetailJson;
                // Log.e("店铺详细", result);
                time_shop = System.currentTimeMillis();
                msg = getMessage(WHAT_SHOPDETAIL_SUCCESS, result);
                initViewHandler.sendMessage(msg);

                if (shopType == TYPE_SIMPLE) {
                    result = am.getCommentList(shopId);
                    time_shop = System.currentTimeMillis();
                    msg = getMessage(ShopCommentActivity.WHAT_GET_COMMENT_SUCCESS, result);
                    initViewHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    @JavascriptInterface
    public void jsToShopProductActivity() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClassName(getApplicationContext(), "com.danertu.dianping.ShopProductActivity");
                intent.putExtra(ProductDetailWeb.KEY_SHOP_JSON, shopJson);
                startActivity(intent);
            }
        });
    }

    @JavascriptInterface
    public void search() {
        String keyword = mEditText.getText().toString().trim();
        if (!keyword.equals("")) {
            Intent intent = new Intent();
            intent.setClassName(getApplicationContext(), "com.danertu.dianping.SearchShopProductActivity");
            Bundle b = new Bundle();
            b.putString("shopid", shopId);
            b.putString("agentID", agentID);
            b.putString("keyword", keyword);
            // 此处使用putExtras，接受方就响应的使用getExtra
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    @JavascriptInterface
    public void jsToProductList() {
        Bundle bundle = new Bundle();
        bundle.putString("shopid", shopId);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(getApplicationContext(), ProductListActivity.class);
        startActivity(intent);
    }

    Dialog dialog = null;

    @JavascriptInterface
    public void jsShowSelectDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                dialog = new Dialog(context, R.style.Dialog);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.dialog_route_select);
                dialog.show();
                dialog.findViewById(R.id.b_route_car).setOnClickListener(dialogClick);
                dialog.findViewById(R.id.b_route_bus).setOnClickListener(dialogClick);
                dialog.findViewById(R.id.b_route_walk).setOnClickListener(dialogClick);
                dialog.findViewById(R.id.b_route_bike).setOnClickListener(dialogClick);
            }
        });
    }

    public View.OnClickListener dialogClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.b_route_car:
                    toRouteGoPlanActivity("1");
                    break;
                case R.id.b_route_bus:
                    toRouteGoPlanActivity("2");
                    break;
                case R.id.b_route_walk:
                    toRouteGoPlanActivity("3");
                    break;
                case R.id.b_route_bike:
                    toRouteGoPlanActivity("4");
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * @param type 1 自驾， 2  公交， 3  步行 4 骑行
     */
    @JavascriptInterface
    public void toRouteGoPlanActivity(String type) {
        ActivityUtils.getInstance().toRouteGoPlanActivity(context, type, shopName, la, lt);
    }

    public void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    OnQuickActionClickListener n = new OnQuickActionClickListener() {
        @Override
        public void onQuickActionClicked(
                QuickActionWidget paramQuickActionWidget, int paramInt) {
            Toast.makeText(getApplicationContext(), paramInt + " selected", Toast.LENGTH_SHORT).show();
        }
    };

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        View listItem = null;
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            try {
                listItem = listAdapter.getView(i, null, listView);
            } catch (Exception e) {
                Logger.e("error", e + "");
            }
            // 计算子项View 的宽高
            assert listItem != null;
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    protected void onDestroy() {
        initViewHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

}