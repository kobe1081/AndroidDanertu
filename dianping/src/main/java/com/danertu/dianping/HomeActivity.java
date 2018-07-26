package com.danertu.dianping;

import wl.codelibrary.widget.IOSDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends BaseActivity {

    private TabHost mTabHost;
    private String tabWidgetNames[] = {"首页", "分类",/*"我要发布",*/"购物车", "个人中心", "分享赚钱"};
    private int tabWidPicIds[] = {
            R.drawable.home_tab_main_selector,
//			R.drawable.home_tab_search_selector,
//			R.drawable.tab_camera,
            R.drawable.home_tab_cart_selector,
            R.drawable.home_tab_personal_selector,
            R.drawable.home_tab_share_selector
    };
    protected TextView tv_home_tag = null;
//	protected final String PRE_INDEX_PAGE = "IndexPage.danertu";
//	protected final String PRE_INDEX_PAGE_NAME = "indexPageName";
//	protected final String PRE_INDEX_PAGE_TAG = "indexPageTag";
//	SharedPreferences sp_index = null;

    protected static String index_wap_name = null;
//	private List<View> list_tabWidgets = null;

    public static final String TAB_MAIN = "INDEX_ACTIVITY";
    public static final String TAB_SEARCH = "SEARCH_ACTIVITY";
    public static final String TAB_CATEGORY = "CATEGORY_ACTIVITY";
    public static final String TAB_CART = "CART_ACTIVITY";
    public static final String TAB_PERSONAL = "PERSONAL_ACTIVITY";
    public static final String TAB_MORE = "QRCodeActivity";
    public static final String TAB_ADD_FOOD = "ADD_FOOD";
    public String type = "";
    //    DBManager db = new DBManager();
    public static HomeHandler mHandle;
    public static final int WHAT_INIT_PERSONER = 11;

    public static boolean isForeground = false;

    public void menuClick(View v) {
//		v.setSelected(true);
        switch (v.getId()) {
            case R.id.index:
                backToHome();
                break;
//		case R.id.classify:
//			toCategory();
//			break;
            case R.id.btn_search:
                toSearch();
                break;
            case R.id.fl_car:
                toCar();
                break;
            case R.id.shoppingcar:
                toCar();
                break;
            case R.id.btn_message:
                toMessage();
                break;
            case R.id.personal:
                if (!TAG.equals("PersonalActivity"))
                    jsStartActivityClearTop("PersonalActivity", "shopid|" + getShopId());
                break;
            case R.id.share:
                toShare();
                break;
        }
    }

    /**
     * 返回首页
     *
     * @return
     */
    public boolean backToHome() {
        String index = "IndexActivity";
        if (!TAG.equals(index)) {
            return super.backToHome();
        }
        return false;
    }

    /**
     * 购物车
     */
    protected void toCar() {
        if (!TAG.equals("CartActivity"))
            jsStartActivityClearTop("CartActivity", "shopid|" + getShopId());
    }

    protected void toSearch() {
        if (!TAG.equals("SearchActivityV2")) {
            jsStartActivity("SearchActivityV2", "shopid|" + getShopId());
        }
    }

    protected void toMessage() {
        if (!TAG.equals("MessageCenterActivity"))
            jsStartActivityClearTop("MessageCenterActivity", "");
    }

    /**
     * 分类
     */
    protected void toCategory() {
        if (!TAG.equals("CategoryActivity"))
            jsStartActivityClearTop("CategoryActivity", "");
    }

    /**
     * 分享
     */
    protected void toShare() {
        if (!TAG.equals("QRCodeActivity"))
            jsStartActivityClearTop("QRCodeActivity", "");
    }

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        mHandle = new HomeHandler(this);
//		sp_index = getSharedPreferences(PRE_INDEX_PAGE, Context.MODE_PRIVATE);
//		initView();
//		registerMessageReceiver();  // used for receive msg

    }

    public void onResume() {
        super.onResume();
        initShopCarCount();
    }

    private View tabView;

    @Override
    public void setContentView(int layoutid) {
        LayoutInflater lif = LayoutInflater.from(getContext());
        View v = lif.inflate(R.layout.activity_home, null);
        tv_home_tag = (TextView) v.findViewById(R.id.index);
        tabView = v.findViewById(R.id.ll_menu);
        tv_carCount = (TextView) v.findViewById(R.id.tv_carCount);
        tv_carCount.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int size = Math.max(tv_carCount.getMeasuredWidth(), tv_carCount.getMeasuredHeight());
        size = (int) Math.max(size, tv_carCount.getTextSize());
        tv_carCount.setHeight(size);
        tv_carCount.setWidth(size);
        FrameLayout fl = (FrameLayout) v.findViewById(R.id.tabcontent);
        lif.inflate(layoutid, fl, true);
        judgeActivity(TAG, (LinearLayout) v.findViewById(R.id.ll_menu));

        setContentView(v);
    }

    protected void setTabVisibility(int visibility) {
        tabView.setVisibility(visibility);
    }

    /**
     * 初始化购物车商品数量
     * 先检查用户是否登录，有则获取购物车商品数量，并在底部导航显示，无则隐藏控件
     */
    private TextView tv_carCount;

    public void initShopCarCount() {
        if (tv_carCount == null)
            return;
        if (isLogined()) {
            Cursor cursor = db.GetShopCar(getContext(), getUid());
            if (cursor == null || cursor.isClosed()) {
                return;
            }
            int count = cursor.getCount();
            cursor.close();
            if (count > 0) {
                tv_carCount.setVisibility(View.VISIBLE);
                tv_carCount.setText(String.valueOf(count));
            } else {
                tv_carCount.setVisibility(View.GONE);
            }

        } else {
            tv_carCount.setVisibility(View.GONE);
        }
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.danertu.dianping.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "MESSAGE";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mMessageReceiver != null)
            unregisterReceiver(mMessageReceiver);
    }

    /**
     * 消息广播接收器
     */
    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : ").append(messge).append("\n");
                if (!CommonTools.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : ").append(extras).append("\n");
                }
            }
        }
    }

    private static final int MSG_EXIT = 1;
    private static final int MSG_EXIT_WAIT = 2;
    private static final long EXIT_DELAY_TIME = 2000;
    public static final int WHAT_CHANGETAB = 3;

    public static class HomeHandler extends Handler {
        HomeActivity act = null;

        public HomeHandler(HomeActivity act) {
            this.act = act;
        }

        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_EXIT:
                    if (act.tabIndex != 0) {
                        Message msg1 = new Message();
                        msg1.what = WHAT_CHANGETAB;
                        msg1.arg1 = 0;
                        sendMessage(msg1);

                    } else if (mHandle.hasMessages(MSG_EXIT_WAIT)) {
                        AppManager.getInstance().appExit(act);
                        ImageLoader.getInstance().clearMemoryCache();
//					act.finish();
//					System.exit(0);
                    } else {
                        CommonTools.showShortToast(act, "再按一次返回键退出");
                        mHandle.sendEmptyMessageDelayed(MSG_EXIT_WAIT, EXIT_DELAY_TIME);

                    }
                    break;
                case MSG_EXIT_WAIT:
                    break;
                case WHAT_CHANGETAB:

                    break;
            }
        }
    }

    boolean isChanging = false;

    protected void findViewById() {
    }

    private void judgeActivity(String className, LinearLayout ll_bottomMenu) {
        int count = ll_bottomMenu.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = ll_bottomMenu.getChildAt(i);
            String tag = v.getTag().toString();
            if (className.equals(tag)) {
                v.setSelected(true);
            } else {
                v.setSelected(false);
            }
        }
    }

    /**
     * 初始化view
     */
    private int tabIndex = 0;

    protected void initView() {
        String rString = "";

//		mTabHost = getTabHost();
        /* 去除标签下方的白线 */
        mTabHost.setPadding(mTabHost.getPaddingLeft(), mTabHost.getPaddingTop(), mTabHost.getPaddingRight(), mTabHost.getPaddingBottom() - 4);

        Intent i_main = new Intent(this, IndexActivity.class);
        Intent i_search = new Intent(this, CategoryActivity.class);
        Intent i_cart = new Intent(this, CartActivity.class);
        Intent i_personal = new Intent(this, PersonalActivity.class);
        Intent i_indexmore = new Intent(this, Tab_EmptyActivity.class);

        for (int i = 0; i < tabWidgetNames.length; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.tab_widget, null);
            ImageView iv = (ImageView) v.findViewById(R.id.iv_homeTabwidget);
            TextView tv = (TextView) v.findViewById(R.id.tv_homeTabwidget);
            iv.setImageResource(tabWidPicIds[i]);
            tv.setText(tabWidgetNames[i]);
            if (i == 0) {
                mTabHost.addTab(mTabHost.newTabSpec(TAB_MAIN).setIndicator(v).setContent(i_main));
            } else if (i == 1)
                mTabHost.addTab(mTabHost.newTabSpec(TAB_SEARCH).setIndicator(v).setContent(i_search));
//			else if(i == 2){
//				tv.setVisibility(View.GONE);
//				mTabHost.addTab(mTabHost.newTabSpec(TAB_ADD_FOOD).setIndicator(v).setContent(new Intent(this, Tab_EmptyActivity.class)));
//			}
            else if (i == 2)
                mTabHost.addTab(mTabHost.newTabSpec(TAB_CART).setIndicator(v).setContent(i_cart));
            else if (i == 3)
                mTabHost.addTab(mTabHost.newTabSpec(TAB_PERSONAL).setIndicator(v).setContent(i_personal));
            else if (i == 4)
                mTabHost.addTab(mTabHost.newTabSpec(TAB_MORE).setIndicator(v).setContent(i_indexmore));
        }

        mTabHost.setCurrentTabByTag(TAB_MAIN);
        try {
            rString = getIntent().getExtras().getString("formType");
        } catch (Exception ex) {
            rString = "0";
        }

        setCurrentPage(rString);

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
            public void onTabChanged(String tag) {
                switch (tag) {
                    case TAB_ADD_FOOD:
                        if (isLogin())
                            startActivity(new Intent(context, PShopAdd.class));
                        else
                            startActivity(new Intent(context, LoginActivity.class));
                        mTabHost.setCurrentTab(tabIndex);

                        break;
                    case TAB_MAIN:
                        setCurrentPage("1");

                        break;
                    case TAB_SEARCH:
                        setCurrentPage("2");

                        break;
                    case TAB_CART:
                        setCurrentPage("3");

                        break;
                    case TAB_PERSONAL:
                        setCurrentPage("4");

                        break;
                    case TAB_MORE:
                        setCurrentPage("5");

                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == LoginActivity.LOGIN_SUCCESS) {
            switch (requestCode) {
                case 2:
                    mTabHost.setCurrentTabByTag(TAB_CART);
                    break;
                case 3:
                    mTabHost.setCurrentTabByTag(TAB_PERSONAL);
                    break;
                case 4:
                    mTabHost.setCurrentTabByTag(TAB_MORE);
                    break;

                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 判断用户是否登录
     *
     * @return 结果
     */
    public boolean isLogin() {
        String uid = db.GetLoginUid(context);
        return !(uid == null || uid.equals(""));
    }

    /**
     * 跳转至登录
     *
     * @param reqCode 把当前页作为请求码，用户登录后即可返回当前页
     */
    public void toLogin(int reqCode) {
        CommonTools.showShortToast(HomeActivity.this, "请先登录");
        Intent toLogin = new Intent(context, LoginActivity.class);
        startActivityForResult(toLogin, reqCode);
        mTabHost.setCurrentTabByTag(TAB_MAIN);
    }

    /**
     * 设置当前页
     *
     * @param rString 当前页码
     */
    private void setCurrentPage(String rString) {
        switch (rString) {
            case "2":
                mTabHost.setCurrentTabByTag(TAB_SEARCH);

                break;
            case "4":
                if (isLogin()) {
                    mTabHost.setCurrentTabByTag(TAB_PERSONAL);
                } else
                    toLogin(3);

                break;
            case "back":
            case "1":
                mTabHost.setCurrentTabByTag(TAB_MAIN);

                break;
            case "shopcar":
            case "3":
                if (isLogin()) {
                    mTabHost.setCurrentTabByTag(TAB_CART);
                } else
                    toLogin(2);

                break;
            case "5":
//			if(isLogin())
//				mTabHost.setCurrentTabByTag(TAB_MORE);
//			else
//				toLogin(4);
                if (isLogin()) {
                    Intent i = new Intent(context, QRCodeActivity.class);
                    i.putExtra("shopid", db.GetLoginUid(context));
                    startActivity(i);
                } else
                    startActivity(new Intent(context, LoginActivity.class));
                mTabHost.setCurrentTab(tabIndex);
                break;
        }
        tabIndex = mTabHost.getCurrentTab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//		case R.id.menu_about:
//
//			break;
//
//		case R.id.menu_setting:
//
//			break;
//
//		case R.id.menu_history:
//
//			break;
//
//		case R.id.menu_feedback:
//
//			break;
//
//		case R.id.menu_help:
//
//			break;

            case R.id.menu_exit:
                final IOSDialog dialog = new IOSDialog(this);
                dialog.setTitle("退出程序");
                dialog.setMessage("确定退出商城？");
                dialog.setPositiveButton("确定", new View.OnClickListener() {
                    public void onClick(View v) {
                        AppManager.getInstance().appExit(getApplicationContext());
                        ImageLoader.getInstance().clearMemoryCache();
                    }
                });
                dialog.setNegativeButton("取消", new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;

            default:
                break;
        }
        return true;
    }

}