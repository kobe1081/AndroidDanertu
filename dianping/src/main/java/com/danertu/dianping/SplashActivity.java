package com.danertu.dianping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import wl.codelibrary.widget.IOSDialog;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.alibaba.fastjson.JSONObject;
import com.config.Constants;
import com.danertu.db.ChinaArea;
import com.danertu.db.DBHelper;
import com.danertu.entity.AppConfigBean;
import com.danertu.entity.UpdateBean;
import com.danertu.tools.AppManager;
import com.danertu.tools.ImageLoaderConfig;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.tools.MIUIUtils;
import com.danertu.tools.SPTool;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;


import static com.danertu.tools.MIUIUtils.isMIUI;

public class SplashActivity extends BaseActivity {
    @SuppressWarnings("unused")
    private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "请不要动这些代码，用于";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";

    public static final int REQUEST_LOCATION = 290;

    private TextView tv_version;
    private Context context;
    Handler mHandler;

    /**
     * 推广店铺id
     */
    public static String EXTENSION_SHOPID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        locationUtil = new LocationUtil(context);
        locate();
        DBHelper.getInstance(getContext()).getWritableDatabase().close();//创建数据库
        new ChinaArea(getContext()).getWritableDatabase().close();
        MobclickAgent.setDebugMode(false);
//      SDK在统计Fragment时，需要关闭Activity自带的页面统计，
//		然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);

        View v = LayoutInflater.from(this).inflate(R.layout.splash_screen_view, null);
        setContentView(v);
        TestinAgent.init(this, "5556ffbe482f07894420f903d174758d", "");
        setSystemBar(R.color.splash_gbColor);
        mHandler = new Handler(getMainLooper());
        getStoragePermission();
        getPhoneStatePermission();
        findViewById();
        new GetBg().execute();
        new GetAppConfig().execute();
        setSwipeBackEnable(false);
        setOverrideExitAniamtion(false);
    }


    private boolean isRequestPermission = false;

    public void locate() {
        //如果当前系统为MIUI
        if (isMIUI() && isRequestPermission) {
            isRequestPermission = true;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (!checkOpsPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    jsShowMsg("请授予单耳兔权限");
                    MIUIUtils.gotoMiuiPermission(this);
                    return;
                }

            }
        }
        /**权限检查*/
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && !isRequestPermission) {
            isRequestPermission = true;
            jsShowMsg("请授予单耳兔权限");
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_LOCATION);
            return;
        }
        locationUtil.startLocate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                locate();
                break;
            case REQUEST_PHONE_STATE:
                break;
        }
    }

    private final String k_upgradeTips = "upgradeTips";
    private final String k_isComplete = "isComplete";
    private String versionNo;
    public static final int WHAT_CHECKVERSION_SUCCESS = 10;

    /**
     * @param upgradeTips 升级提示
     * @param isComplete  false 下载差分包增量升级，true 下载完整安装包升级
     */
    public void showVersionUpdate(String upgradeTips, final boolean isComplete) {
        Logger.e("test", "isComplete=" + isComplete);
        if (versionNo == null || versionNo.equals("")) {
            Logger.w("检测更新出错", versionNo);
        } else {
            // 发现新版本，提示用户更新
            final IOSDialog alert = new IOSDialog(this);
            alert.setTitle("软件升级");
            alert.setMessage(upgradeTips);
            alert.setPositiveButton("更新",
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            alert.dismiss();
                            try {
                                APKDownload.isComplete = isComplete;
                                Intent intent = new Intent(SplashActivity.this, APKDownload.class);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                judgeFirstEnter();
                                e.printStackTrace();
                            }
                        }
                    });
            alert.setNegativeButton("取消", new View.OnClickListener() {
                public void onClick(View v) {
                    alert.cancel();
                }
            });
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    judgeFirstEnter();
                }
            });
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }
    }

    public void onResume() {
        super.onResume();
        JPushInterface.onResume(getContext());
    }

    public void onPause() {
        super.onPause();
        JPushInterface.onPause(getContext());
    }

    public static final int WHAT_GETEXTENS_SUCCESS = 6;
    public static final int WHAT_COUNT = 7;

    public String getExtensionShopId(String uid) {
        return appManager.getExtensionShopId(uid);
    }

    /**
     * 初始化页面
     */
    @Override
    protected void initView() {
        String tips = "";
        if (!Constants.appWebPageUrl.equals("https://appweb.danertu.com:8444/")) {
            tips = "_" + Constants.appWebPageUrl;
        }
        if (Constants.isDebug) {
            tips += "_调试模式";
        }
        if (Constants.isSalesman) {
            tips += "_业务员版";
        }
        tv_version.setText(getVersionName() + tips);
        /**
         * 获取启动页背景
         */

        /**
         * 检查版本更新
         */
        new Thread(rdb).start();
        b_pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogHandler.sendMessage(getMessage(WHAT_GETEXTENS_SUCCESS, ""));
                if (timer != null)
                    timer.cancel();
            }
        });
    }

    class GetAppConfig extends AsyncTask<Void, Integer, AppConfigBean> {

        @Override
        protected AppConfigBean doInBackground(Void... voids) {
            AppConfigBean bean = null;
            try {
                String appConfig = appManager.getAppConfig(getUid());
                bean = JSONObject.parseObject(appConfig, AppConfigBean.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bean;
        }

        @Override
        protected void onPostExecute(AppConfigBean appConfigBean) {
            super.onPostExecute(appConfigBean);
            if (appConfigBean == null || appConfigBean.getVal() == null || appConfigBean.getVal().size() == 0) {

                String urlAlipayReturn = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlAlipayReturn");
                if (!TextUtils.isEmpty(urlAlipayReturn))
                    Constants.APP_URL.ALI_PAY_CALLBACK_URL_SIMPLE = urlAlipayReturn;

                String urlAlipayReturnWareHouse = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlAlipayReturnWareHouse");
                if (!TextUtils.isEmpty(urlAlipayReturnWareHouse))
                    Constants.APP_URL.ALI_PAY_CALLBACK_URL_STOCK = urlAlipayReturnWareHouse;

                String urlWechatReturn = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlWechatReturn");
                if (!TextUtils.isEmpty(urlWechatReturn))
                    Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_SIMPLE = urlWechatReturn;

                String urlWechatReturnWareHouse = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlWechatReturnWareHouse");
                if (!TextUtils.isEmpty(urlWechatReturnWareHouse))
                    Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_STOCK = urlWechatReturnWareHouse;

                String urlWareHouseRules = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlWareHouseRules");
                if (!TextUtils.isEmpty(urlWareHouseRules))
                    Constants.APP_URL.DANERTU_STOCK_PROTOCOL = urlWareHouseRules;

                String urlShopShare = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlShopShare");
                if (TextUtils.isEmpty(urlShopShare))
                    Constants.APP_URL.SHOP_SHARE_URL = urlShopShare;

                String urlShareHall = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlShareHall");
                if (!TextUtils.isEmpty(urlShareHall))
                    Constants.APP_URL.NEW_SHARE_HALL_ADDRESS = urlShareHall;

                String urlImgDomain = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlImgDomain");
                if (!TextUtils.isEmpty(urlImgDomain))
                    Constants.APP_URL.imgServer = urlImgDomain;

                String urlAndroidUpdateFull = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlAndroidUpdateFull");
                if (TextUtils.isEmpty(urlAndroidUpdateFull))
                    Constants.APP_URL.APK_DOWNLOAD_URL = urlAndroidUpdateFull;

                String urlAndroidUpdateDifference = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlAndroidUpdateDifference");
                if (!TextUtils.isEmpty(urlAndroidUpdateDifference))
                    Constants.APP_URL.APK_PATCH_DOWNLOAD_URL = urlAndroidUpdateDifference;

                String urlAnnouncementDetail = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlAnnouncementDetail");
                if (!TextUtils.isEmpty(urlAnnouncementDetail))
                    Constants.APP_URL.ANNOUNCEMENT_DETAIL_URL = urlAnnouncementDetail;

                String urlExpressQuery = SPTool.getString(context, SPTool.SP_APP_CONFIG, "UrlExpressQuery");
                if (!TextUtils.isEmpty(urlExpressQuery))
                    Constants.APP_URL.KUAIDI100_ADDRESS = urlExpressQuery;
            } else {
                AppConfigBean.ValBean valBean = appConfigBean.getVal().get(0);

                String urlAlipayReturn = valBean.getUrlAlipayReturn();
                if (!TextUtils.isEmpty(urlAlipayReturn)) {
                    Constants.APP_URL.ALI_PAY_CALLBACK_URL_SIMPLE = urlAlipayReturn;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlAlipayReturn", urlAlipayReturn);
                }

                String urlAlipayReturnWareHouse = valBean.getUrlAlipayReturnWareHouse();
                if (!TextUtils.isEmpty(urlAlipayReturnWareHouse)) {
                    Constants.APP_URL.ALI_PAY_CALLBACK_URL_STOCK = urlAlipayReturnWareHouse;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlAlipayReturnWareHouse", urlAlipayReturnWareHouse);
                }

                String urlWechatReturn = valBean.getUrlWechatReturn();
                if (!TextUtils.isEmpty(urlWechatReturn)) {
                    Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_SIMPLE = urlWechatReturn;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlWechatReturn", urlWechatReturn);
                }

                String urlWechatReturnWareHouse = valBean.getUrlWechatReturnWareHouse();
                if (!TextUtils.isEmpty(urlWechatReturnWareHouse)) {
                    Constants.APP_URL.WECHAT_PAY_CALLBACK_URL_STOCK = urlWechatReturnWareHouse;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlWechatReturnWareHouse", urlWechatReturnWareHouse);
                }

                String urlWareHouseRules = valBean.getUrlWareHouseRules();
                if (!TextUtils.isEmpty(urlWareHouseRules)) {
                    Constants.APP_URL.DANERTU_STOCK_PROTOCOL = urlWareHouseRules;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlWareHouseRules", urlWareHouseRules);
                }

                String urlShopShare = valBean.getUrlShopShare();
                if (!TextUtils.isEmpty(urlShopShare)) {
                    Constants.APP_URL.SHOP_SHARE_URL = urlShopShare;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlShopShare", urlShopShare);
                }

                String urlShareHall = valBean.getUrlShareHall();
                if (!TextUtils.isEmpty(urlShareHall)) {
                    Constants.APP_URL.NEW_SHARE_HALL_ADDRESS = urlShareHall;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlShareHall", urlShareHall);
                }

                String urlImgDomain = valBean.getUrlImgDomain();
                if (!TextUtils.isEmpty(urlImgDomain)) {
                    Constants.APP_URL.imgServer = urlImgDomain;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlImgDomain", urlImgDomain);
                }

                String urlAndroidUpdateFull = valBean.getUrlAndroidUpdateFull();
                if (!TextUtils.isEmpty(urlAndroidUpdateFull)) {
                    Constants.APP_URL.APK_DOWNLOAD_URL = urlAndroidUpdateFull;
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlAndroidUpdateFull", urlAndroidUpdateFull);
                }

                String urlAndroidUpdateDifference = valBean.getUrlAndroidUpdateDifference();
                if (!TextUtils.isEmpty(urlAndroidUpdateDifference)) {
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlAndroidUpdateDifference", urlAndroidUpdateDifference);
                    Constants.APP_URL.APK_PATCH_DOWNLOAD_URL = urlAndroidUpdateDifference;
                }

                String UrlAnnouncementDetail = valBean.getUrlAnnouncementDetail();
                if (!TextUtils.isEmpty(UrlAnnouncementDetail)) {
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlAnnouncementDetail", UrlAnnouncementDetail);
                    Constants.APP_URL.ANNOUNCEMENT_DETAIL_URL = UrlAnnouncementDetail;
                }

                String UrlExpressQuery = valBean.getUrlExpressQuery();
                if (!TextUtils.isEmpty(UrlExpressQuery)) {
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlExpressQuery", UrlExpressQuery);
                    Constants.APP_URL.KUAIDI100_ADDRESS = UrlExpressQuery;
                }

                String urlTicketProductDetail = valBean.getUrlTicketProductDetail();
                if (!TextUtils.isEmpty(urlTicketProductDetail)) {
                    SPTool.updateString(context, SPTool.SP_APP_CONFIG, "UrlTicketProductDetail", urlTicketProductDetail);
                    Constants.APP_URL.TICKET_DETAIL_URL = urlTicketProductDetail;
                }
            }
            initView();
        }

    }


    /**
     * 判断是否是首次进入app
     * 如果是，进入app引导页
     * 否则直接进入app首页
     */
    public void judgeFirstEnter() {
        // 启动homeactivty，相当于Intent
        SharedPreferences sharedPreferences = getSharedPreferences("myActivityName", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstIn", true);
        String keyVCode = "versionCode";
        int spVCode = sharedPreferences.getInt(keyVCode, 0);
        int vCode = getVersionCode();
        isFirstRun = (vCode > spVCode);
        if (isFirstRun) {
            openActivity(AppShowActivity.class);
        } else {
            openActivity(IndexActivity.class);
        }

        finish();
    }

    /**
     * 获取app启动页背景
     */
    private class GetBg extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            return appManager.getSplashBg();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logger.e("splash", result);
            ImageLoader.getInstance().displayImage(result, (ImageView) findViewById(R.id.bg_splash), ImageLoaderConfig.initDisplayOptions(false));
        }
    }

    private Timer timer;
    /**
     * 获取手机imei码
     * 如果城市数据不存在，导入
     * 检查app更新
     */
    public Runnable rdb = new Runnable() {
        public void run() {
            getIMEI();

            boolean b = db.tabIsExist(SplashActivity.this, "citys");
            boolean c = db.tabIsExist(SplashActivity.this, "provinces");
            boolean d = db.tabIsExist(SplashActivity.this, "ChinaCity1");
            if (!b || !c || !d) {
                //导入城市数据库
                importInitDatabase();
            }
            if (updateCheck()) {
                return;
            }
            dialogHandler.sendEmptyMessage(9);
        }

        /**
         *
         * 检查更新
         * @return
         *
         * 2018年10月22日
         * @since version 92
         *  添加接口可控制是否增量更新
         */
        public boolean updateCheck() {
            // 耗时操作
            boolean result = true;
            versionNo = AppManager.getInstance().getVersionNo("0057");
//            versionNo = "{\"newVersion\":\"91\",\"isComplete\":\"false\"}";
            if (versionNo.contains("{")) {
                result = newUpdate(result, versionNo);
            } else {
                result = oldUpdate(result);
            }
            return result;
        }

        public boolean newUpdate(boolean result, String json) {
            try {
                UpdateBean updateBean = JSONObject.parseObject(json, UpdateBean.class);
                int code = getVersionCode();
                int version = updateBean.getNewVersion();
                if ("true".equals(updateBean.getIsComplete())) {
                    getTips(true);
                } else {
                    if (code == version - 1) {
                        getTips(false);
                    } else if (code < version) {
                        getTips(true);
                    } else {
                        result = false;
                    }
                }

            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }

            return result;
        }

        private boolean oldUpdate(boolean result) {
            int version = 0;
            int code = getVersionCode();
            try {
                if (!TextUtils.isEmpty(versionNo))
                    version = Integer.parseInt(versionNo);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (code == version - 1) {
                getTips(false);
            } else if (code < version) {
                getTips(true);
            } else {
                result = false;
            }
            return result;
        }
        // IndexActivity.this.showhandler.sendMessage(msg);

        /**
         * 获取更新提示
         *
         * @param isComplete 是否下载app整包
         *                   true：下载完整包
         *                   false：下载差分包
         */
        public void getTips(boolean isComplete) {

            try {
                String upgradeTips = appManager.getUpdateTips();
                Message msg = Message.obtain();
                Bundle data = new Bundle();
                data.putString(k_upgradeTips, upgradeTips);
                data.putBoolean(k_isComplete, isComplete);
                msg.setData(data);
                msg.what = WHAT_CHECKVERSION_SUCCESS;
                dialogHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * 弹出更新提醒对话框
     */
    Handler dialogHandler = new Handler() {
        private boolean canUpdate = false;

        public void handleMessage(Message msg) {
            if (msg.what == WHAT_GETEXTENS_SUCCESS) {
                if (!canUpdate) {
                    judgeFirstEnter();
                }
            } else if (msg.what == WHAT_CHECKVERSION_SUCCESS) {

                canUpdate = true;
                Bundle b = msg.getData();
                String tips = b.getString(k_upgradeTips);
                boolean isComplete = b.getBoolean(k_isComplete);
                showVersionUpdate(tips, isComplete);

            } else if (msg.what == WHAT_COUNT) {
                b_pass.setText("跳过 " + msg.arg1 + "s");

            } else if (msg.what == 9) {
                b_pass.setVisibility(View.VISIBLE);
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    int count = 0;

                    public void run() {
                        int n = 3 - count++;
                        if (n <= 0) {
                            dialogHandler.sendMessage(getMessage(WHAT_GETEXTENS_SUCCESS, ""));
                            timer.cancel();
                        } else {
                            Message msg = Message.obtain();
                            msg.arg1 = n;
                            msg.what = WHAT_COUNT;
                            dialogHandler.sendMessage(msg);
                        }
                    }
                };
                timer.schedule(task, 0, 1000);

            }
        }
    };
    private Button b_pass;

    /**
     * 将res/raw中的城市数据库导入到安装的程序中的database目录下
     */
    public void importInitDatabase() {
        // 数据库的目录
        String dirPath = "/data/data/com.danertu.dianping/databases";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
        }
        // 数据库文件
        File dbfile = new File(dir, ChinaArea.DB_NAME);
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            if (!dbfile.exists()) {
                boolean newFile = dbfile.createNewFile();
            }
            // 加载欲导入的数据库
            is = this.getApplicationContext().getResources().openRawResource(R.raw.danertu_db);
            fos = new FileOutputStream(dbfile);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化页面控件
     */
    @Override
    protected void findViewById() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        b_pass = (Button) findViewById(R.id.b_pass);
    }

    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            /*String s = */
            intent.getAction();
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static boolean isOPen(final Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gps || network) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    @SuppressWarnings("deprecation")
    public final void openGPS(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);

        String provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            context.sendBroadcast(poke);
        }
    }

}
