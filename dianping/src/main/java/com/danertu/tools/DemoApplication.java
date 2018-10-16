package com.danertu.tools;

import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.multidex.MultiDex;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;
import okio.Buffer;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.ModuleName;
import com.baidu.mapapi.OpenLogUtil;
import com.baidu.mapapi.SDKInitializer;
import com.mob.MobApplication;

public class DemoApplication extends MobApplication {

    //	public static RequestQueue mQueue;
    /**
     * activity列表，所有打开的activity都会被add，退出时remove
     */
    private Stack<Activity> activities;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        //异常捕捉
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
//        百度地图
//      在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //开启Log调试
        OpenLogUtil.setModuleLogEnable(ModuleName.TILE_OVERLAY_MODULE, true);

//        极光推送相关
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        JPushInterface.resumePush(this);
//        mQueue = Volley.newRequestQueue(this);
//        mQueue.start();
        activities = new Stack<>();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void addActivity(Activity act) {
        activities.add(act);
    }

    public void log() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < activities.size(); i++) {
            sb.append(i).append(" : ").append(activities.get(i).toString()).append("\n");
        }
        Logger.e("activities", sb.toString());
    }

    public void removeActivity(Activity act) {
        Logger.e("removeActivity", act.toString());
        activities.remove(act);
    }

    public void killAllActivity() {
        for (int i = activities.size() - 1; i >= 0; i--) {
            activities.get(i).finish();
        }
        System.exit(0);
    }

    public int getActivitySize() {
        return activities.size();
    }

    public void backToActivity(String actName) {
        for (int i = activities.size() - 1; i >= 0; i--) {
            Activity a = activities.get(i);
            String name = a.getClass().getSimpleName();
            if (!name.equals(actName)) {
                a.finish();
            }
        }
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等

    private Handler main;

    public void setMainHandler(Handler handler) {
        main = handler;

    }

    public void flushMain() {
        main.sendEmptyMessage(0);
    }


}