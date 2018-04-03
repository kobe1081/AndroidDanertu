package com.danertu.dianping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wl.codelibrary.widget.IOSDialog;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

import com.config.Constants;
import com.danertu.db.ChinaArea;
import com.danertu.db.DBHelper;
import com.danertu.tools.AppManager;
import com.danertu.tools.ImageLoaderConfig;
import com.danertu.tools.LocationUtil;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;
import com.joker.annotation.PermissionsGranted;
import com.joker.api.Permissions4M;
import com.joker.api.apply.PermissionsChecker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class SplashActivity extends BaseActivity {
    @SuppressWarnings("unused")
    private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "请不要动这些代码，用于";
    public static String tranlateKeyword = "%E7%94%B5%E8%AF%9D";

    private TextView tv_version;
    Handler mHandler;

    /**
     * 推广店铺id
     */
    public static String EXTENSION_SHOPID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        locationUtil=new LocationUtil(this);
        locationUtil.startLocate();
        mHandler = new Handler(getMainLooper());
        findViewById();
        initView();
        setSwipeBackEnable(false);
        setOverrideExitAniamtion(false);
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
        HashMap<String, String> params = new HashMap<>();
        params.put("apiid", "0102");
        params.put("memberid", uid);
        return AppManager.getInstance().doPost(params);
    }

    /**
     * 初始化页面
     */
    @Override
    protected void initView() {
        String tips = "";
        if (!Constants.appWebPageUrl.equals("http://115.28.55.222:8018/")) {
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
        new GetBg().execute();
        /**
         * 检查版本更新
         */
        new Thread(rdb).start();
        b_pass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialogHandler.sendMessage(getMessage(WHAT_GETEXTENS_SUCCESS, ""));
                timer.cancel();
            }
        });
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
            HashMap<String, String> param = new HashMap<>();
            param.put("apiid", "0313");
            return appManager.doPost(param);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Logger.e("splash",result);
            ImageLoader.getInstance().displayImage(result, (ImageView) findViewById(R.id.bg_splash), ImageLoaderConfig.initDisplayOptions(false));
        }
    }

    private Timer timer;
    /**
     * 获取手机imei码
     * 如果城市数据不存在，导入
     * 检查app更新
     *
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
         * 检查更新
         * @return
         */
        public boolean updateCheck() {
            // 耗时操作
            boolean result = true;
            versionNo = AppManager.getInstance().getVersionNo("0057");
            int version = 0;
            int code = getVersionCode();
            try {
                if (!TextUtils.isEmpty(versionNo))
                    version = Integer.parseInt(versionNo);
                Logger.i("当前版本号", code + "");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (code == version - 1) {
                getTips(false);
            } else if (code < version) {
                getTips(true);
            } else
                result = false;
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
            HashMap<String, String> param = new HashMap<>();
            param.put("apiid", "0103");
            try {
                String upgradeTips = AppManager.getInstance().doPost(param);
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
