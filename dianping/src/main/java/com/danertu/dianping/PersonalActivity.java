package com.danertu.dianping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.db.DBManager;
import com.danertu.dianping.sign.Seference;
import com.danertu.entity.CouponCountBean;
import com.danertu.entity.MyOrderDataQRCode;
import com.danertu.tools.AppManager;
import com.danertu.tools.AsyncTask;
import com.danertu.tools.DateTimeUtils;
import com.danertu.tools.FWorkUtil;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 2017年7月25日
 * 黄业良
 * 将修改头像的AlertDialog改为PopupWindow实现
 *
 * @Link #showPopupWindos()
 * <p>
 * 2018年4月4日
 * 未开店跳转地址修改为 activity/20171012.html
 */
public class PersonalActivity extends HomeActivity implements OnClickListener {
    private Context context;

    private String[] items = new String[]{"选择本地图片"};
    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;

    private static final int RESULT_REQUEST_CODE = 2;

    private final int REQ_LOGIN = 102;

    public static final String KEY_FROM_PUSH = "fromPush";
    public static final String KEY_TAB_INDEX = "TabIndex";
    public static final String KEY_IS_ONLY_HOTEL = "isOnlyHotel";


    private String currentDate = "";
    private String dateStr = "";// 今天的日期


//    DBManager mgr = new DBManager();
//	String filePath = android.os.Environment.getExternalStorageDirectory()
//			+ "/CACHE/1133326912";

    /**
     * 个人中心页面
     */
    final String WEBPAGE_NAME = "Android_personal2.html";
    public static final String WV_INTERFACE = "iface_personal";
    public ImageView iv_head = null;            //头像
    private LinearLayout llSignIn;              //签到item
    public TextView tv_sign_in = null;          //签到按钮
    public TextView tv_sign_in_tip = null;      //签到提示
    public TextView tv_account = null;          //账号
    public TextView tv_wallet_count = null;     //钱包余额
    public TextView tv_jlb = null;              //金萝卜
    private TextView tv_coupon;                 //优惠券
    private TextView tv_collect_count;          //收藏

    final int CHECK_IN = 101;

    private String headImgUrl = null;
    private String nickname = null;
    private String psw = null;

    private String loginId;

    public final int getCheckInKey() {
        return CHECK_IN;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_personal);
        setSystemBarWhite();
        if (!isLogin()) {
            jsStartActivityForResult("LoginActivity", "", REQ_LOGIN);
            return;
        }
        Intent intent = getIntent();
        init();
        /**
         * 从推送调起的话，起中转作用，先打开此页面然后打开订单页面
         */
        if (intent.getBooleanExtra(KEY_FROM_PUSH, false)) {
            jsToOrderActivity(Integer.parseInt(intent.getStringExtra(KEY_TAB_INDEX)), intent.getBooleanExtra(KEY_IS_ONLY_HOTEL, false));
        }
    }

    public void onResume() {
        super.onResume();
        if (isLogin()) {
            new Thread(rInitMsg).start();
            new GetSignInToday().execute();
            new GetWallet().execute();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * 从推送调起的话，起中转作用，先打开此页面然后打开订单页面
         */
        new Thread(rInitMsg).start();
        new GetSignInToday().execute();
        if (intent.getBooleanExtra(KEY_FROM_PUSH, false)) {
            jsToOrderActivity(Integer.parseInt(intent.getStringExtra(KEY_TAB_INDEX)), intent.getBooleanExtra(KEY_IS_ONLY_HOTEL, false));
        }
    }

    /**
     * 废弃，使用新接口获取
     */
    private Runnable rInitFav = new Runnable() {
        public void run() {
            HashMap<String, String> param = new HashMap<>();
            param.put("apiid", "0291");
            param.put("memberid", getUid());
            int count = 0;
            try {
                JSONArray items = new JSONObject(appManager.doPost(param)).getJSONArray("val");
                count = items.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message msg = Message.obtain();
            msg.what = 3;
            msg.arg1 = count;
            handlerOrderData.sendMessage(msg);
        }
    };


    /**
     * 检查当前登录用户是否为店主，是则显示我的店铺、开店必看、车友会等入口
     * <p>
     * 否则隐藏
     */
    private class CheckShopState extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int type = 0;
            HashMap<String, String> param = new HashMap<>();
            param.put("apiid", "0141");
            param.put("shopid", getUid());
            String result = appManager.doPost(param);
            if (!TextUtils.isEmpty(result)) {
                try {
                    String id = new JSONObject(result).getJSONArray("val").getJSONObject(0).getString("ID");
                    if (!TextUtils.isEmpty(id)) {
                        type = 1;
                        param.put("apiid", "0252");
                        param.put("memberid", getUid());
                        result = appManager.doPost(param);
                        String isCrown = new JSONObject(result).getString("result");
                        if (isCrown != null && isCrown.equalsIgnoreCase("true")) {
                            type = 2;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return type;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            /**
             * result>0:为店主/商家
             * result=1:
             * result=2:精英联盟
             *否则为普通用户
             */
            if (result > 0) {
                isOpen = true;

                findViewById(R.id.id_level).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_shop_manager).setVisibility(View.VISIBLE);
//                if (result == 2)
//                    findViewById(R.id.p_crown).setVisibility(View.VISIBLE);
            } else {

                //显示我要开店
                findViewById(R.id.ll_open_shop).setVisibility(View.VISIBLE);
            }
        }

    }

    public void init() {
        loginId = getUid();
        fwUtil = new FWorkUtil(context);
        handlerOrderData = new Handler(new HCallBack());
        findViewById();
        setMessage();
        new CheckShopState().execute();
        new ShowCouponCount().execute(loginId);
        new ShowCollectCount().execute(loginId);
    }

    public void setMessage() {
        Cursor cursor = db.GetLoginInfo(context);
        if (!cursor.isClosed() && cursor.moveToFirst()) {
            psw = cursor.getString(cursor.getColumnIndex("pwd"));
            nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            if (TextUtils.isEmpty(headImgUrl)) {
                headImgUrl = cursor.getString(cursor.getColumnIndex("headimgurl"));
            }
        }
        cursor.close();

        if (!TextUtils.isEmpty(headImgUrl)) {
            ImageLoader.getInstance().displayImage(headImgUrl, iv_head);
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = getUid();
        }
        tv_account.setText(nickname);
        tv_account.setVisibility(View.VISIBLE);
    }

    /**
     * 更新头像图片路径（本地）
     *
     * @param headImgUrl 头像地址
     */
    private void updateHeadImg(String headImgUrl) {
        DBHelper dbHelper3 = DBHelper.getInstance(getContext());

        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(headImgUrl)) {
            values.put("headimgurl", headImgUrl);
            try {
                if (db.IsUidExists(getContext(), getUid()))
                    dbr.update("userLoginInfo", values, "uId = ?", new String[]{getUid()});
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    protected void findViewById() {
        tv_jlb = (TextView) findViewById(R.id.tv_jlb_count);
        tv_coupon = (TextView) findViewById(R.id.tv_coupon);
        tv_account = (TextView) findViewById(R.id.tv_personal_account);
        tv_sign_in = (TextView) findViewById(R.id.tv_sign_in);
        tv_sign_in_tip = (TextView) findViewById(R.id.tv_sign_in_tip);
        tv_wallet_count = (TextView) findViewById(R.id.tv_wallet_count);
        llSignIn = (LinearLayout) findViewById(R.id.ll_sign_in);
        iv_head = (ImageView) findViewById(R.id.iv_personal_head);
        tv_collect_count = $(R.id.tv_collect_count);

        tv_sign_in_tip.setOnClickListener(this);
        tv_wallet_count.setOnClickListener(this);

        findViewById(R.id.b_order_set).setOnClickListener(this);
        findViewById(R.id.iv_personal_head).setOnClickListener(this);
        findViewById(R.id.tv_sign_in).setOnClickListener(this);

        findViewById(R.id.ll_collect).setOnClickListener(this);
        findViewById(R.id.ll_wallet).setOnClickListener(this);
        findViewById(R.id.ll_coupon).setOnClickListener(this);
        findViewById(R.id.ll_jlb).setOnClickListener(this);

        findViewById(R.id.ll_qr_code).setOnClickListener(this);
        findViewById(R.id.ll_my_order).setOnClickListener(this);
        findViewById(R.id.ll_open_shop).setOnClickListener(this);



        /**
         * 店铺功能
         */
        findViewById(R.id.ll_manual).setOnClickListener(this);
        findViewById(R.id.ll_car).setOnClickListener(this);
        findViewById(R.id.ll_back_buy).setOnClickListener(this);
        findViewById(R.id.ll_warehouse).setOnClickListener(this);
        findViewById(R.id.ll_shop_setting).setOnClickListener(this);
        findViewById(R.id.ll_order_manager).setOnClickListener(this);
        findViewById(R.id.ll_product_manager).setOnClickListener(this);
        findViewById(R.id.ll_my_shop).setOnClickListener(this);
        findViewById(R.id.ll_shop_manager).setOnClickListener(this);


//        findViewById(R.id.test).setOnClickListener(this);


    }

    boolean isOpen = false;

    public void click(View v) {
        if (isClickMoreTimesShortTime(System.currentTimeMillis())) {
            switch (v.getId()) {
                case R.id.b_order_set:
                    jsStartActivity("HtmlActivityNew", "url|" + Constants.appWebPageUrl + "Android/set_center.html");
                    // 实例化SelectPicPopupWindow
                    break;
                case R.id.iv_personal_head://头像
//                showDialog();
                    showPopupWindow();
                    break;
                case R.id.tv_sign_in://签到
                    if (isLogined()) {
                        new SignIn().execute();
                    } else {
                        jsShowMsg("请先登录");
                        jsStartActivityForResult("LoginActivity", "", REQ_LOGIN);
                    }
                    break;
                case R.id.tv_sign_in_tip:
                    jsStartActivityForResult("CalendarActivity", "", CHECK_IN);
                    break;
//                case R.id.p_crown:
                //精英联盟
//                    jsStartActivity("HtmlActivityNew", "url|" + Constants.appWebPageUrl + "elite_alliance.html");
//                    break;

                case R.id.ll_collect:
                    jsToActivity("com.danertu.dianping.MyCollectActivity");
                    break;
                case R.id.ll_my_order:
                    jsToOrderActivity(0);
                    break;
                case R.id.ll_wallet:
                    jsToActivity("com.danertu.dianping.MyWalletActivity");
                    break;
                case R.id.ll_coupon:
                    /**
                     * 优惠券
                     */
                    if (isLogin()) {
                        jsStartActivity("com.danertu.dianping.MyCouponActivity", "shopid|" + getShopId());
                    } else {
                        jsShowMsg("请先登录");
                        jsStartActivityForResult("LoginActivity", "", REQ_LOGIN);
                    }
//                    jsStartActivity("HtmlActivity", "pageName|person_coupon_list2.html");
                    break;
                case R.id.ll_jlb:
                    jsToActivity("com.danertu.dianping.ScoreCenterActivity");
                    break;
                case R.id.ll_open_shop:
                    /**
                     * 2018年4月4日
                     * 修改指向地址
                     */
//                    jsStartActivityForResult("HtmlActivity", "pageName|set_shop_base.html", 3);
                    jsStartActivityForResult("HtmlActivity", "pageName|activity/20171012.html?agentid=chunkang&platform=android", 3);
                    break;
                case R.id.ll_qr_code:
                    /**
                     * 消费码
                     */
//                    jsToOrderActivity(0, false, true);

                    Intent intent = new Intent(getContext(), MyOrderQRCodeActivity.class);
                    intent.putExtra("TabIndex", 0);
                    intent.putExtra("isOnlyHotel", false);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    myOrderDataQRCode = new MyOrderDataQRCode(this, false, true) {
                        @Override
                        public void getDataSuccess() {
//
//                    if (handler != null) {
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                hideLoadDialog();
//
//                            }
//                        }, 1000);
//                    } else {
//                        hideLoadDialog();
//                    }
                        }
                    };

                    break;
                case R.id.ll_my_shop://进入店铺
                    if (!isOpen) {
                        jsStartActivityForResult("HtmlActivity", "pageName|set_shop_base.html", 3);
                    } else {
                        jsStartActivity("HtmlActivityNew", "url|" + Constants.appWebPageUrl + "Android/seller_center.html,;showDialog|true");
//                        jsStartActivity("DetailActivity", "shopid|" + getUid() + ",;shoptype|2");
                    }
                    break;
                case R.id.ll_product_manager://商品管理
                    jsStartActivity("HtmlActivity", "pageName|seller_product.html");
                    break;
                case R.id.ll_order_manager://订单管理
                    jsStartActivity("HtmlActivity", "pageName|deal_manage.html");
                    break;
                case R.id.ll_shop_setting://店铺设置
                    jsStartActivity("HtmlActivity", "pageName|set_shop_base.html");
                    break;
                case R.id.ll_warehouse://仓库
                    jsToActivity("com.danertu.dianping.StockpileActivity");
                    break;
                case R.id.ll_back_buy://后台拿货
                    jsStartActivity("HtmlActivityNew", "url|" + Constants.appWebPageUrl + "Android/seller_take.html");
                    break;
                case R.id.ll_car:
                    jsStartActivity("HtmlActivity", "pageName|car_insurance.html");
                    break;
                case R.id.ll_manual://卖家手册
                    jsStartActivity("HtmlActivityNew", "url|" + Constants.appWebPageUrl + "seller_learn.html");
                    break;
//                case R.id.test:
//                    shareImgWithQRCode("http://img.danertu.com/member/announcement/20180529163710102.jpg","1234565768666",0.2f,0.2f,200,null);
//                    break;

                default:
                    break;
            }
        } else {
            Logger.i(TAG, "频繁点击");
        }
    }

    /**
     * 获取钱包余额
     */
    private class GetWallet extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return AppManager.getInstance().getWalletMoney("0108", loginId);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //通过try-catch方式判断获取回来的结果是不是数字
            //如果是就显示
            try {
                double money = Double.parseDouble(s);
                tv_wallet_count.setText(CommonTools.formatZero2Str(money));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 从服务器中取出客户签到信息
     */
    private class GetSignInToday extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            currentDate = sDateFormat.format(date);
            return AppManager.getInstance().judgeSignIn("0050", loginId, currentDate);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                //已经签到
                tv_sign_in_tip.setVisibility(View.VISIBLE);
                llSignIn.setVisibility(View.GONE);
            } else {
                //未签到
                tv_sign_in_tip.setVisibility(View.GONE);
                llSignIn.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 将签到添加到服务器
     * <p>
     * 0051接口 签到成功返回：true
     */
    private class SignIn extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return AppManager.getInstance().insertSignIn("0051", loginId);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                jsShowMsg("签到成功");
                new Thread(rInitMsg).start();
                llSignIn.setVisibility(View.GONE);
                tv_sign_in_tip.setVisibility(View.VISIBLE);

                db.InsertRegistrationForm(context, loginId, DateTimeUtils.getDateToyyyyMMdd());

                //保存签到记录
                Seference seference = new Seference(context);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-M-d");
                Date date = new Date();
                dateStr = sDateFormat.format(date);
                seference.savePreferenceData(seference.signFileName, dateStr, true + "");
            } else {
                llSignIn.setVisibility(View.VISIBLE);
                tv_sign_in_tip.setVisibility(View.GONE);
                jsShowMsg("签到失败，请重试");
            }
        }
    }


    final String appWebPageUrl = "file:///android_asset/";

    public Handler handlerOrderData = null;

    private class HCallBack implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 10) {
                String score = msg.obj.toString();
                tv_jlb.setText(score);
            } else if (msg.what == 3) {
                tv_coupon.setText(msg.arg1 + "");
            }
            return true;
        }
    }

    /**
     * 跳转到特定名称的activity
     *
     * @param activityName AddressActivity,CalendarActivity,LiuyanActivity,MsgListActivity
     *                     MyCollectActivity,ZhanghuActivity,ScoreCenterActivity,TicketActivity
     */
    public void jsToActivity(String activityName) {
        Class<?> act;
        try {
            act = Class.forName(activityName);
            Intent intent = new Intent(PersonalActivity.this, act);
            if (act.getSimpleName().equals("MessageCenterActivity")) {
                intent.putExtra("memberid", getUid());
            }
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            if (Constants.isDebug)
                CommonTools.showShortToast(context, "异常: " + e);
            else
                CommonTools.showShortToast(context, "出错了");
        }
    }

    /**
     * 使用popupWindow
     */
    private void showPopupWindow() {
        //设置contentView
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_personal_select_photo, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);
//        TextView tvSelectCamera = (TextView) contentView.findViewById(R.id.tv_select_camera);
        TextView tvSelectGallery = (TextView) contentView.findViewById(R.id.tv_select_gallery);
        TextView tvSelectCancel = (TextView) contentView.findViewById(R.id.tv_select_cancel);
//        TextView tvSelectShow = (TextView) contentView.findViewById(R.id.tv_select_show);
        View view = contentView.findViewById(R.id.view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        // 在相册中选取
        tvSelectGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentFromGallery = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                intentFromGallery.setType("image/*"); // 设置文件类型
                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });
//        //调用照相机
//        tvSelectCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT,
//                        Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "head.jpg")));
//                startActivityForResult(intent2, 2);// 采用ForResult打开
//            }
//        });

        tvSelectCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        //显示PopupWindow
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.AnimationBottomFade);
        //得到当前activity的rootView
        View rootView = ((ViewGroup) PersonalActivity.this.findViewById(android.R.id.content)).getChildAt(0);
        //底部弹出
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intentFromGallery = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
                                intentFromGallery.setType("image/*"); // 设置文件类型
                                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                                break;
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    FWorkUtil fwUtil;

    /**
     * 裁剪图片方法实现
     *
     * @param uri 图片Uri
     */
    public void startPhotoZoom(Uri uri) {
        if (fwUtil == null) {
            fwUtil = new FWorkUtil(PersonalActivity.this);
        }
        headImgUrl = "file://" + fwUtil.startPhotoZoom(uri, 1, 1, 320, 320, 2, true);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data intent data
     */
    private void getImageToView(Intent data) {
        ImageLoader.getInstance().displayImage(headImgUrl, iv_head);
        updateHeadImg(headImgUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                case REQ_LOGIN:
                    if (resultCode == LoginActivity.LOGIN_SUCCESS) {
                        init();
                    } else {
                        finish();
                    }
                    break;
                case 3:
                    new CheckShopState().execute();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * liujun2014-7-17
     */
    @SuppressWarnings("unused")
    private Thread tGetGetUserMsg = new Thread(new Runnable() {

        @Override
        public void run() {
            // 耗时操作
            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            String result = AppManager.getInstance().postGetMessage("0034", db.GetLoginUid(PersonalActivity.this));
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result).getJSONObject("msglist");
                JSONArray jsonArray = jsonObject.getJSONArray("msgbean");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject oj = jsonArray.getJSONObject(i);
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("content", oj.getString("content"));
                    item.put("uid", oj.getString("uid"));
                    item.put("state", oj.getString("state"));
                    item.put("Answer", oj.getString("Answer"));
                    item.put("upTime", oj.getString("upTime"));
                    item.put("guid", oj.getString("guid"));
                    list.add(item);
                }
                db.TogetherPcMessage(PersonalActivity.this, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            SystemClock.sleep(1000);
        }
    });

    /**
     * 实例化订单信息及金萝卜信息
     */
    Runnable rInitMsg = new Runnable() {
        public void run() {
            String serverScore = "0";
            try {
                String loginResult = AppManager.getInstance().postLoginInfo("0009", getUid(), psw);
                JSONObject obj = new JSONObject(loginResult).getJSONArray("val").getJSONObject(0);
                serverScore = obj.getString("Score");
                db.updateScore(getApplicationContext(), getUid(), serverScore);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handlerOrderData.sendMessage(getMessage(10, serverScore));
        }
    };

    @Override
    public void onClick(View v) {
        click(v);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取优惠券数量并显示
     */
    class ShowCouponCount extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String param = params[0];
            if (TextUtils.isEmpty(param)) {
                return null;
            }
            return appManager.postGetCouponCount(param);
        }

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                tv_coupon.setText("0");
                return;
            }
            CouponCountBean countBean = gson.fromJson(s, CouponCountBean.class);
            if (countBean == null || countBean.getVal() == null || countBean.getVal().size() == 0) {
                tv_coupon.setText("0");
                return;
            }
            tv_coupon.setText(countBean.getTotal() == null ? "0" : countBean.getTotal());
            super.onPostExecute(s);
        }
    }

    class ShowCollectCount extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String uid = params[0];
            if (TextUtils.isEmpty(uid)) {
                return null;
            }
            if (db == null) {
                db = DBManager.getInstance();
            }
            Cursor productCursor = db.GetCollectProduct(context, uid);
            int productCount = 0;
            if (productCursor != null) {
                productCount = productCursor.getCount();
                productCursor.close();
            }
            Cursor shopCursor = db.GetCollectShop(context, uid);
            int shopCount = 0;
            if (shopCursor != null) {
                shopCount = shopCursor.getCount();
                shopCursor.close();
            }
            return "" + (productCount + shopCount);
        }

        @Override
        protected void onPostExecute(String s) {
            if (TextUtils.isEmpty(s)) {
                tv_collect_count.setText("0");
                return;
            }
            tv_collect_count.setText(s);
            super.onPostExecute(s);
        }
    }


}