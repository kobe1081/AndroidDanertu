package com.danertu.base;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.config.Constants;
import com.danertu.db.DBManager;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.R;
import com.danertu.tools.DeviceTag;
import com.danertu.tools.ShareUtil;
import com.danertu.tools.StatusBarUtil;
import com.danertu.tools.SystemBarTintManager;
import com.danertu.widget.CommonTools;


/**
 * Fragment基类
 * Created by Viz on 2017/10/16.
 */

public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment implements BaseView {
    public final String TAG = getClass().getSimpleName();
    public Context context;
    private SystemBarTintManager manager;
    public T presenter;
    public ShareUtil shareUtil;
    private ProgressBar progressBar;
    private long firstClick;
    private boolean isLoading = false;
    private DBManager db;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        firstClick = System.currentTimeMillis();
        presenter = initPresenter();
        db = DBManager.getInstance();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.attach((V) this);
        progressBar = new ProgressBar(context);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onPause();
        } else {
            onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        Logger.e(this.getClass().getSimpleName(), "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detach();
    }

    /**
     * 定义一个Presenter 用于解绑持有的View
     * 在onCreate进行初始化Presenter的操作
     * 在onResume中进行绑定
     * 在onDestroy 中进行解绑
     */

    /**
     * 这里是适配为不同的View 装载不同Presenter
     */
    public abstract T initPresenter();


    /**
     * 获取状态高度
     *
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 获取导航栏高度
     *
     * @return 导航栏高度
     */
    public int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? getResources().getDimensionPixelSize(resourceId) : 0;
    }


    /**
     * 设置状态栏样式
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void initSystemBar() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        Window window = getActivity().getWindow();
        // Translucent status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Translucent navigation bar
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        if (manager == null) {
            manager = new SystemBarTintManager(getActivity());
        }
        manager.setStatusBarTintEnabled(true);
        manager.setNavigationBarTintEnabled(true);
        manager.setNavigationBarTintResource(R.color.black);
        setSystemBar(R.color.tab_black);
    }

    /**
     * 设置状态栏底色为白色
     */
    @JavascriptInterface
    public void setSystemBarWhite(final boolean isDark) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (StatusBarUtil.StatusBarLightMode(getActivity(), isDark) == 0)
                    setSystemBar(R.color.white);
                else
                    setSystemBar(R.color.white);
            }
        });
    }

    /**
     * 设置状态栏底色为白色
     */
    @JavascriptInterface
    public void setSystemBarWhite() {
        setSystemBarWhite(true);
    }

    public void setSystemBar(int res) {
        if (manager != null) {
            manager.setStatusBarTintResource(res);
        }
    }

    public void setSystemBar(Drawable drawable) {
        if (manager != null) {
            manager.setStatusBarTintDrawable(drawable);
        }
    }

    /**
     * 设置状态栏底色
     *
     * @param color 颜色值
     * @since versionCode 74
     */
    @JavascriptInterface
    public void setSystemBarColor(final String color) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (manager != null && !TextUtils.isEmpty(color)) {
                    manager.setStatusBarTintColor(Color.parseColor(color));
                }
            }
        });
    }

    /**
     * 获取uid
     *
     * @return uid
     */
    @JavascriptInterface
    public String getUid() {
        return db.GetLoginUid(getContext());
    }

    @Override
    public void jsShowLoading() {
//        if (isLoading)
//            return;
//        isLoading = true;
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                pb_loading.setVisibility(View.VISIBLE);
//                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
//                pb_loading.startAnimation(anim);
//            }
//        });
    }

    @Override
    public void jsHideLoading() {
//        if (!isLoading)
//            return;
//        isLoading = false;
//        getActivity().runOnUiThread(new Runnable() {
//            public void run() {
//                if (handler == null) {
//                    pb_loading.setVisibility(View.GONE);
//                    return;
//                }
//                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out);
//                pb_loading.startAnimation(anim);
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        pb_loading.setVisibility(View.GONE);
//                    }
//                }, anim.getDuration());
//            }
//        });
    }

    /**
     * 隐藏系统标题栏
     */
    @Override
    public void jsHideActionBar() {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null)
            actionBar.hide();
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    @Override
    public int jsGetVersionCode() {
        return CommonTools.getVersionCode(context);
    }


    /**
     * 获取版本名
     *
     * @return 版本名
     */
    @Override
    public String jsGetVersionName() {
        return "v" + CommonTools.getVersionName(context);
    }

    @Override
    public void jsFinish() {
        getActivity().finish();
    }

    @Override
    public String jsGetCurrentProvince() {
        return Constants.getCurrentProvince();
    }

    @Override
    public String jsGetCurrentCity() {
        return Constants.getCityName();
    }

    @Override
    public String jsGetCurrentLa() {
        return Constants.getLa();
    }

    @Override
    public String jsGetCurrentLt() {
        return Constants.getLt();
    }

    @Override
    public void jsStartActivity(String targetActivity, String param) {
        presenter.startActivity(targetActivity, param);
    }

    @Override
    public void jsStartActivity(String targetActivity) {
        jsStartActivity(targetActivity, null);
    }

    @Override
    public void jsStartActivityForResult(String targetActivity, String param, int requestCode) {
        startActivityForResult(presenter.parseToIntent(targetActivity, param), requestCode);
    }

    @Override
    public void jsSetResult(int resultCode) {
        jsSetResult(resultCode, null);
    }

    @Override
    public void jsSetResult(int resultCode, String data) {
        Bundle bundle = presenter.parseToBundle(data);
        Intent intent = null;
        if (bundle != null) {
            intent = getActivity().getIntent().putExtras(bundle);
        }
        getActivity().setResult(resultCode, intent);
    }

    @Override
    public String jsGetIMEI() {
        return new DeviceTag(context).getImei();
    }

    @Override
    public String jsGetMac() {
        return new DeviceTag(context).getMac();
    }

    @Override
    public String jsGetDeviceID() {
        return new DeviceTag(context).getDeviceID();
    }

    @Override
    public void jsShowToast(String message) {
        CommonTools.showShortToast(context, message);
    }

    @Override
    public boolean jsIsLogin() {
        DBManager utils = DBManager.getInstance();
        String uid = utils.GetLoginUid(context);
        return !TextUtils.isEmpty(uid);
    }

    @Override
    public void jsOpenSystemBrowser(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public int getScreenWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public int getSreenHeight() {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getHeight();
    }


    @Override
    public void initShareUtils() {
        if (shareUtil == null) {
            shareUtil = new ShareUtil(context);
        }
    }

    @Override
    public void jsShare(final String title, final String imgPath, final String carId, final String url, final String description, final String platformList) {
        initShareUtils();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shareUtil.share("android", title, imgPath, carId, url, description, platformList);
            }
        });
    }

    /**
     * 获取图片地址
     *
     * @param imgName    图片名称
     * @param agentID    代理商id
     * @param supplierID 供应商id
     * @return 图片地址
     */
    @JavascriptInterface
    public String getImgUrl(String imgName, String agentID, String supplierID) {
        return ActivityUtils.getImgUrl(imgName, agentID, supplierID);
    }

    @Override
    public void setMargins(ViewGroup viewGroup, int left, int top, int right, int bottom) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
        params.setMargins(left, top, right, bottom);
    }


    @Override
    public void setTopMargins(ViewGroup viewGroup, int top) {
        setMargins(viewGroup, 0, top, 0, 0);
    }


    @Override
    public void setPadding(View view, int left, int top, int right, int bottom) {
        view.setPadding(left, top, right, bottom);
    }


    @Override
    public void setTopPadding(View view, int top) {
        view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), view.getPaddingBottom());
    }


    @Override
    public boolean isClickMoreTimesShortTime(long secondClick) {
        if (secondClick - firstClick > 1500) {
            firstClick = secondClick;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getStockSmallImgPath(String imgName) {
        return Constants.imgServer + "sysProduct/" + imgName;
    }
}
