package com.danertu.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.widget.CommonTools;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * presenter基类
 * Created by Viz on 2017/10/16.
 */

public abstract class BasePresenter<T> {
    /*这个基类的Presenter 主要的作用就是将当前Presenter持有的view 在合适的时候给清除掉*/
    public T view;
    public Context context;
    public final String TAG = this.getClass().getSimpleName();
    public Handler handler;

    public BasePresenter(Context context) {
        this.context = context;

    }

    public void attach(T mView) {
        this.view = mView;
    }

    public void detach() {
        view = null;
    }

    public abstract void initHandler();

    public void startActivity(String targetActivity, String param) {
        try {
            context.startActivity(parseToIntent(targetActivity, param));
        } catch (Exception e) {
            e.printStackTrace();
            if (Constants.isDebug) {
                CommonTools.showShortToast(context, "找不到此页面:" + targetActivity);
            } else {
                CommonTools.showShortToast(context, "找不到此页面");
            }
        }
    }

    /**
     * 将string转换为intent
     *
     * @param targetActivity
     * @param param
     * @return
     */
    public Intent parseToIntent(String targetActivity, String param) {
//        targetActivity = Constants.BASE_PACKAGE + targetActivity;
        Intent intent = null;
        try {
            intent = new Intent(context, Class.forName(targetActivity));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(param)) {
            return intent;
        }
        intent.putExtras(parseToBundle(param));
        return intent;
    }

    /**
     * 将string转换未bundle
     *
     * @param param
     * @return
     */
    public Bundle parseToBundle(String param) {
        if (TextUtils.isEmpty(param)) {
            return null;
        }
        Bundle bundle = new Bundle();
//        String[] strings = param.split(Constants.PARAM_SEPARATOR);
//        for (String string : strings) {
//            bundle.putString(string.substring(0, string.indexOf(Constants.KEY_VALUE_SEPARATOR)), string.substring(string.indexOf(Constants.KEY_VALUE_SEPARATOR) + 1));
//        }
        return bundle;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 获取导航栏高度
     *
     * @return 导航栏高度
     */
    public int getNavigationBarHeight() {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    public String parseToHttpParam(LinkedHashMap<String, Object> map) {
        StringBuilder params = new StringBuilder();
        params.append("{");
        if (map.size() > 0) {
            for (String s : map.keySet()) {
                params.append("\"").append(s).append("\"").append(":\"").append(map.get(s)).append("\"").append(",");
            }
            params.deleteCharAt(params.lastIndexOf(","));
        }
        params.append("}");
        return params.toString();
    }

}
