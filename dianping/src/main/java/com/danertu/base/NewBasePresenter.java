package com.danertu.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.tools.Logger;
import com.danertu.tools.ThreadUtil;
import com.danertu.widget.CommonTools;

import java.util.LinkedHashMap;


/**
 * presenter基类
 */

public abstract class NewBasePresenter<T, V extends BaseModel> {
    /*这个基类的Presenter 主要的作用就是将当前Presenter持有的view 在合适的时候给清除掉*/
    public T view;
    public Context context;
    public final String TAG = this.getClass().getSimpleName();
    public Handler handler;
    protected V model;

    public NewBasePresenter(Context context) {
        this.context = context;
        initHandler();
        model = initModel();
    }

    public void attach(T mView) {
        this.view = mView;
    }

    public void detach() {
        view = null;
    }


    public abstract void initHandler();

    public abstract V initModel();

    public String getUid() {
        return model.getUid(context);
    }

    public String getImei() {
        return model.getImei(context);
    }

    public String getMac() {
        return model.getMac(context);
    }

    public String getDeviceID() {
        return model.getDeviceID(context);
    }


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
        if (!targetActivity.contains(".")) {
            targetActivity = Constants.BASE_PACKAGE + targetActivity;
        }
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
        String[] strings = param.split(",;");
        for (String string : strings) {
            bundle.putString(string.substring(0, string.indexOf("|")), string.substring(string.indexOf("|") + 1));
        }
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

    public boolean isViewAttached() {
        return view != null;
    }

    public void runThread(Runnable runnable) {
        ThreadUtil.execute(runnable);
    }

    public void sendMessage(Handler handler, int what, int arg1, Object object) {
        if (handler == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        message.obj = object;
        message.arg1 = arg1;
        handler.sendMessage(message);
    }

    public void sendMessage(Handler handler, int what, int arg1) {
        sendMessage(handler, what, arg1, null);
    }

    public void sendMessage(Handler handler, int what, String info) {
        if (handler == null) {
            return;
        }
        Message message = handler.obtainMessage();
        message.what = what;
        if (!TextUtils.isEmpty(info)) {
            message.obj = info;
        }
        handler.sendMessage(message);
    }

    public void sendMessage(Handler handler, int what, Object obj) {
        sendMessage(handler, what, -1, obj);
    }

    public void sendMessage(Handler handler, int what) {
        sendMessage(handler, what, null);
    }
}
