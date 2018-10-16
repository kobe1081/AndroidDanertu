package com.danertu.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.config.Constants;
import com.danertu.widget.CommonTools;
import com.danertu.widget.MWebViewClient;
import com.umeng.analytics.MobclickAgent;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * huangyeliang
 * <p>
 * 此类中有两个post方法：dopost 和getHttpPostResult,区别仅在于参数param的类型
 *
 * @since 2017-7-17
 */
public class AppManager {
    @SuppressWarnings("unused")
    private static final char[] wJ = "0123456789abcdef".toCharArray();
    public static String imsi = "204046330839890";
    public static String p = "0";
    public static String keyword = "请不要动这些代码，用于";
    public static String translateKeyword = "%E7%94%B5%E8%AF%9D";

    private final String apiSrcUrl = Constants.apiSrcUrl;
    private String versionName = "";
    private String uid;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    private AppManager() {
    }


    //使用内部类实例以确保这个类在使用时才加载到Java虚拟机中
    private static class AppHolder {
        private static AppManager mAppManager = new AppManager();
    }

    /**
     * 获取单一实例
     */
    public static AppManager getInstance() {
        return AppHolder.mAppManager;
    }

    /**
     * 退出app，关闭进程
     */
    @SuppressWarnings("deprecation")
    public void appExit(Context context) {
        try {
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            DemoApplication da = (DemoApplication) context.getApplicationContext();
            int sysVersion = VERSION.SDK_INT;
            MobclickAgent.onKillProcess(context);
            if (sysVersion < 8) {
                activityMgr.restartPackage(context.getPackageName());
            } else {
                activityMgr.killBackgroundProcesses(context.getPackageName());
            }
            da.killAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String postGetMessage(String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0007");
        params.put("memberId", uid);
        return doPost(params);
    }


    /**
     * 不需要授权
     *
     * @param ApiId
     * @param uId
     * @param pwd
     * @return
     */
    // post取用户输入的登录信息判断登录状态   0009
    public String postLoginInfo(String ApiId, String uId, String pwd) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("pwd", pwd);
        params.put("uid", uId);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param uId
     * @param pwd
     * @param email
     * @return
     */
    // Post方式提交注册信息
    public String postInfo(String ApiId, String uId, String pwd, String email) {
        // Post 方法比Get方法????设置的参数更??
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("email", email);
        params.put("pwd", pwd);
        params.put("tid", "");
        params.put("uid", uId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param uId
     * @return
     */
    // post取用户输入的登录信息判断登录名是否存在? 0029
    public String postUserIsExist(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("tid", "");
        params.put("uId", uId);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param uId
     * @return
     */
    // post取用户收货地址  0030
    public String postGetUserAddress(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("tid", uId);
        params.put("uId", uId);
        return doPost(params);
    }


    /**
     * 需要授权
     *
     * @param ApiId
     * @param guid
     * @return
     */
    // post取用户删除收货地址  0014
    public String postDeleteUserAddress(String ApiId, String guid, String tid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("gid", guid);
        params.put("tid", tid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param uId
     * @param uName
     * @param mobile
     * @param address
     * @param isDefault
     * @param guid
     * @return
     */
    // post保存用户收货地址  0015
    public String postInsertUserAddress(String ApiId, String uId, String uName, String mobile, String address, String isDefault, String guid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("adress", address);
        param.put("aguid", guid);
        param.put("apiid", ApiId);
        param.put("isdefault", isDefault);
        param.put("mobile", mobile);
        param.put("name", uName);
        param.put("tid", uId);
        param.put("uId", uId);
        return doPost(param);
    }

    /**
     * 不需要授权
     * 注册  0011
     *
     * @param account
     * @param passwd
     * @param email
     * @return
     */
    public String postRegister(String account, String passwd, String email) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        // Post方式提交注册信息
        param.put("apiid", "0011");
        param.put("uid", account);
        param.put("pwd", passwd);
        param.put("email", "");
        return doPost(param);

    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param guid
     * @param uid
     * @return
     */
    // post用户设置收货地址为默认操作请求  0031
    public String postSetAddressIsDefault(String ApiId, String guid, String uid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("aid", guid);
        params.put("apiid", ApiId);
        params.put("_uid", uid);
        return doPost(params);
    }

    /*
     * post留言到服务器 liujun 2014-7-15
     */
    public String postInsertMessage(String ApiId, String uid, String message) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("uId", uid);
        params.put("content", message);
        return doPost(params);
    }

    // post获取订单信息请求

    /**
     * 需要授权
     *
     * @param ApiId 0033
     * @param uid
     * @param type  为1时查询当月订单，否则为全部
     * @return
     */
    public String postGetUserOrderHead(String ApiId, String uid, String type) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("tid", uid);
        param.put("type", type);
        param.put("uId", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param uid
     * @param message
     * @param guid
     * @return
     */
    /*
     * post留言到服务器 liujun 2014-7-15  0032
     */
    public String postInsertMessage(String ApiId, String uid, String message, String guid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("content", message);
        params.put("guid", guid);
        params.put("tid", uid);
        params.put("uId", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param uId
     * @return
     */
    /*
     * post获取留言列表 liujun 2014-7-16  0034
     */
    public String postGetMessage(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("tid", uId);
        params.put("uId", uId);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param guid
     * @return
     */
    /*
     * 更新消息状态 liujun 2014-7-16  0035
     */
    public String postUpdateMsgState(String ApiId, String guid, String uid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("guid", guid);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param orderNumber
     * @return
     */
    // 取订单详情  0072
    public String postGetOrderInfoShow(String ApiId, String orderNumber, String uid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("ordernumber", orderNumber);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 获取验证码
     *
     * @param mobile
     * @return
     */
    public String postGetVerityCode(String mobile) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0077");
        params.put("mobile", mobile);
        return doPost(params);
    }

    /**
     * 验证验证码
     *
     * @param mobile
     * @param code
     * @return
     */
    public String postCheckCode(String mobile, String code) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0078");
        params.put("mobile", mobile);
        params.put("vcode", code);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param cityName
     * @param pageSize
     * @param pageIndex
     * @param keyword
     * @param type
     * @return
     */
    // 根据参数取店铺列表  0037
    public String postGetIndexShopList(String ApiId, String cityName, int pageSize, int pageIndex, String keyword, String type, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keyword);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("type", type);
        param.put("tid", uid);
        if (cityName == null) {
            param.put("areaCode", "0000");
        } else {
            if (cityName.equals(Constants.getDcityName())) {
                param.put("areaCode", "");
                param.put("gps", Constants.getLa() + "," + Constants.getLt());
                param.put("less", "80000");
            } else {
                param.put("areaCode", cityName);
            }
        }
        return doPost(param);
    }

    // post 提交订单
    // liujun 2014-7-19

    /**
     * 需要授权
     *
     * @param imei
     * @param mac
     * @param deviceID
     * @param postString
     * @param remark     备注用户使用了多少积分或代金券
     * @param isBackCall 表示是否为后台拿货商品
     * @return
     */
    public String postOrder(String imei, String mac, String deviceID, String postString, String remark, boolean isBackCall, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0097");
        param.put("deviceid", deviceID + "");
        param.put("imei", imei + "");
        param.put("jsonstr", postString);
        param.put("mac", mac + "");
        param.put("remark", remark);
        param.put("tid", uid);
        if (isBackCall) {
            param.put("isbackcall", "1");
        }
        return doPost(param);
    }

    /**
     * 需要授权
     * 囤货订单
     */
    public String postStockOrder(String memLoginId, String productId, String buyNumber, String mobilePayWay, String deviceType, String remark, String appVersion, String payPrice) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0324");
        param.put("appVersion", appVersion);
        param.put("buyNumber", buyNumber);
        param.put("deviceType", deviceType);
        param.put("memLoginId", memLoginId);
        param.put("mobilePayWay", mobilePayWay);
        param.put("payPrice", payPrice);
        param.put("productId", productId);
        param.put("remark", remark);
        param.put("tid", memLoginId);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param memLoginId
     * @param guid
     * @return
     */
    public String getStockProductInfo(String memLoginId, String guid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0326");
        params.put("guid", guid);
        params.put("memLoginId", memLoginId);
        params.put("tid", memLoginId);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param memLoginId
     * @param wareHouseGuid
     * @return
     */
    public String getProductInRecord(String memLoginId, String wareHouseGuid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0330");
        params.put("memLoginId", memLoginId);
        params.put("tid", memLoginId);
        params.put("wareHouseGuid", wareHouseGuid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 提交店铺评论？
     *
     * @param shopid
     * @param uid
     * @param score
     * @param comment
     * @param imgStr
     * @param nim
     * @return
     */
    public String postShopToComment(String shopid, String uid, String score, String comment, String imgStr, String nim) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0101");
        params.put("imgstr", imgStr);
        params.put("info", comment);
        params.put("isanonymity", nim);
        params.put("memberid", uid);
        params.put("shopid", shopid);
        params.put("tid", uid);
        params.put("xinglevel", score);

        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param uid
     * @return
     */
    public String getExtensionShopId(String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0102");
        params.put("memberid", uid);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 获取后台拿货商品列表
     *
     * @param memberid
     * @return
     */
    public String getBackcallProduct(String memberid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0173");
        params.put("memberid", memberid);
        params.put("tid", memberid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @return
     */
    public String getSplashBg() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0313");
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @return
     */
    public String getUpdateTips() {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0103");
        return doPost(param);
    }


    /**
     * 不需要授权
     *
     * @param uid
     * @param name
     * @param address
     * @param mobile
     * @param remark
     * @param requestNum
     * @param shopid
     * @return
     */
    public String postTasteWine(String uid, String name, String address, String mobile, String remark, String requestNum, String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0135");
        param.put("address", address);
        param.put("memberid", uid);
        param.put("mobile", mobile);
        param.put("name", name);
        param.put("remark", remark);
        param.put("requestnum", requestNum);
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param postString
     * @param uid
     * @param pid
     * @return
     */
    // post 提交一元订单
    // liujun 2014-12-31  0096
    public String postYiYuanOrder(String ApiId, String postString, String uid, String pid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("jsonstr", postString);
        params.put("pid", pid);
        params.put("tid", uid);
        params.put("uid", uid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param shopid
     * @return
     */
    // 获取店铺详细  0041
    public String postGetShopDetails(String ApiId, String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("la", Constants.getLa());
        param.put("lt", Constants.getLt());
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param ApiId  0042
     * @param shopid
     * @return
     */
    // 获取店铺产品
    public String postGetShopProduct(String ApiId, String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param guid
     * @param imei
     * @param mac
     * @param deviceid
     * @param shopid
     * @return
     */
    // post根据商品guid获取商品  0026
    // liujun 2014-7-20
    public String postGetProductInfo(String ApiId, String guid, String imei, String mac, String deviceid, String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("deviceid", deviceid);
        param.put("imei", imei);
        param.put("mac", mac);
        param.put("proId", guid);
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param ApiId   0040
     * @param keyWord
     * @param type
     * @param shopid
     * @return
     */
    // post取商品列表
    // liujun 2014-7-20
    public String postGetProductList(String ApiId, String keyWord, String type, String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keyWord);
        param.put("memberid", shopid);
        param.put("type", type);
        return doPost(param);
    }

    /**
     * 未知是否需要授权
     *
     * @param ApiId
     * @param pageSize
     * @param pageIndex
     * @return
     */
    // post取供应商商品列表
    // liujun 2014-7-25
    public String postGetSupplierProductList(String ApiId, int pageSize, int pageIndex) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("pageSize", String.valueOf(pageSize));
        params.put("pageIndex", String.valueOf(pageIndex));
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId    0044
     * @param keywords
     * @return
     */
    // post搜索店铺列表
    // liujun 2014-7-26
    public String postGetSearchResult(String ApiId, String keywords) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keywords);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param ApiId    0045
     * @param keywords
     * @return
     */
    // post搜索平台商品
    // liujun 2014-7-29
    public String postGetSearchProductResult(String ApiId, String keywords) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keywords);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param ApiId     0037
     * @param cityName
     * @param pageSize
     * @param pageIndex
     * @param keyword
     * @param type
     * @return
     */
    // 获取附近店铺列表(默认5公里)
    public String postGetNearByShopList(String ApiId, String cityName, int pageSize, int pageIndex, String keyword, String type, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("areaCode", cityName);
        String gps = Constants.getLa() + "," + Constants.getLt();
        params.put("gps", gps);
        params.put("kword", keyword);
        params.put("less", "80000"); // 5公里范围
        params.put("pageIndex", String.valueOf(pageIndex));
        params.put("pageSize", String.valueOf(pageSize));
        params.put("tid", uid);
        params.put("type", type);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId     0047
     * @param versionNo
     * @param playDate
     * @return
     */
    // 校验服务器上今天是否玩过互动游戏
    // liujun 7-30
    public String postCheckPlayData(String ApiId, String versionNo, String playDate) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("playdate", playDate);
        params.put("versionNo", versionNo);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId     0046
     * @param versionNo
     * @param score
     * @param uid
     * @param forUid
     * @return
     */
    // post添加服务器互动表数据
    // liujun 7-29
    public String postInsertHudongScore(String ApiId, String versionNo, String score, String uid, String forUid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("foruid", forUid);
        params.put("score", score);
        params.put("uid", uid);
        params.put("versionNo", versionNo);
        return doPost(params);
    }

    /**
     * 需要授权
     * post 修改密码
     *
     * @param ApiId  0048
     * @param mid
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public String postChangePassword(String ApiId, String mid, String oldPwd, String newPwd) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("mid", mid);
        param.put("oldpwd", oldPwd);
        param.put("pwd", newPwd);
        param.put("tid", mid);
        return doPost(param);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param versionNo
     * @param playDate
     * @param foruid
     * @return
     */
    // liujun 8-6
    public String postCheckPlayDataFor(String ApiId, String versionNo, String playDate, String foruid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("versionNo", versionNo);
        params.put("playdate", playDate);
        params.put("formid", foruid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 签到逻辑判断
     *
     * @param ApiId    0050
     * @param mid
     * @param playDate
     * @return
     */
    public String judgeSignIn(String ApiId, String mid, String playDate) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("playdate", playDate);
        params.put("tid", mid);
        params.put("uid", mid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 插入签到记录
     *
     * @param ApiId 0051
     * @param mid
     * @return
     */
    public String insertSignIn(String ApiId, String mid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("uid", mid);
        params.put("tid", mid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 获取签到记录
     *
     * @param ApiId 0052
     * @param mid
     * @return
     */
    public String getSignIn(String ApiId, String mid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mid", mid);
        params.put("tid", mid);
        return doPost(params);
    }

    /**
     * 不需要授权
     * 获取企业数据
     *
     * @param ApiId 0053
     */
    public String getParams(String ApiId) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId 0055
     * @param uid
     * @param type
     * @return
     */
    // post获取代付款订单
    // liujun 8-10
    public String postGetWaitPayOrder(String ApiId, String uid, String type) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("tid", uid);
        params.put("type", type);
        params.put("uId", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 更新订单状态  0056
     *
     * @param orderNumber
     * @return
     */
    public String updateOrderStatues(String orderNumber, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0056");
        params.put("ordernumber", orderNumber);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0057
     * @return
     */
    // 获取服务器版本号
    public String getVersionNo(String ApiId) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0020
     * @param mId
     * @param gps
     * @param dm
     * @return
     */
    // 提交gps坐标到服务器
    public String addGPS(String ApiId, String mId, String gps, String dm) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("dm", dm);
        params.put("gps", gps);
        params.put("memberId", mId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0059
     * @param uid
     * @return
     */
    // 获取当前登录用户绑定的店铺
    public String getBound(String ApiId, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mobile", uid);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param shopid
     * @return
     */
    // 获取当前登录用户绑定的店铺
    public String getShareCount(String ApiId, String shopid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("shopid", shopid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0061
     * @param uid
     * @param score
     * @return
     */
    // 更新积分 apiid=0061
    public String updateUserScore(String ApiId, String uid, String score) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("memberid", uid);
        param.put("score", score);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param mcode
     * @return
     */
    // 获取该设备是否第一次安装  apiid-0062
    public String isFirstSetUp(String ApiId, String mcode) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mcode", mcode);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param mcode
     * @return
     */
    // 写入第一次安装信息
    public String insertFirstSetUp(String ApiId, String mcode) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mcode", mcode);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ErrorInfo
     * @return
     */
    // 收集异常信息 0065
    public String sendErrInfo(String ErrorInfo) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0065");
        params.put("errinfo", ErrorInfo);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param productGuid
     * @param pagesize
     * @param pageIndex
     * @return
     */
    // 获取商品评论 0066
    public String getProductComment(String ApiId, String productGuid, int pagesize, int pageIndex) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("pageindex", String.valueOf(pageIndex));
        params.put("pageSize", String.valueOf(pagesize));
        params.put("productguid", productGuid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param productGuid
     * @param loginID
     * @param content
     * @param agentID
     * @param rank
     * @return
     */
    // 添加商品评论 0067
    public String addComment(String ApiId, String productGuid, String loginID, String content, String agentID, String rank) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("agentID", agentID);
        params.put("apiid", ApiId);
        params.put("content", content);
        params.put("memloginid", loginID);
        params.put("productguid", productGuid);
        params.put("rank", rank);
        params.put("tid", loginID);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId       0068
     * @param productGuid
     * @return
     */
    // 商品评论条数 0068
    public String getCommentCount(String ApiId, String productGuid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("productguid", productGuid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param productGuid
     * @param loginID
     * @return
     */
    // 判断是否有商品评论权限 0069
    public String couldComment(String ApiId, String productGuid, String loginID) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("memberid", loginID);
        params.put("proguid", productGuid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param city
     * @return
     */
    // 取当前定位地区优惠券 0070
    public String getTicket(String ApiId, String city) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("areaname", city);
        params.put("apiid", ApiId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param shopId
     * @param keyword
     * @param pagesize
     * @param pageindex
     * @return
     */
    // 按关键字搜索店铺与入驻商品列表  0071
    // xiaohanxiang 2014-10-17
    public String postGetSearchShopProduct(String ApiId, String shopId, String keyword, int pagesize, int pageindex) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("keyword", keyword);
        params.put("pageindex", String.valueOf(pageindex));
        params.put("pageSize", String.valueOf(pagesize));
        params.put("shopid", shopId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0073
     * @return
     */
    // 获取一级分类
    public String getFirstCategory(String ApiId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        return doPost(params);
    }

    /**
     * 需要授权
     * 取消订单
     *
     * @param orderNumber 订单号
     * @return 是否取消订单成功
     * @author dengweilin
     */
    public String postCancelOrder(String orderNumber, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0075");
        params.put("ordernumber", orderNumber);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 确认收货
     *
     * @param orderNumber 订单号
     * @return 是否确认收货成功
     * @author dengweilin
     */
    public String postFinishOrder(String orderNumber, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0076");
        params.put("ordernumber", orderNumber);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param phoneNum
     * @return
     */
    // 发送手机验证码
    // xiaohanxiang 2014-10-31
    public String postSendVerityCode(String ApiId, String phoneNum) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mobile", phoneNum);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @param phoneNum
     * @param vcode
     * @return
     */
    // 验证手机验证码
    // xiaohanxiang 2014-10-31
    public String postCheckVerityCode(String ApiId, String phoneNum, String vcode) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mobile", phoneNum);
        params.put("vcode", vcode);
        return doPost(params);
    }

    /**
     * post重置密码
     *
     * @param mid
     * @param Pwd
     * @return
     */
    public String postResetPassword(String ApiId, String mid, String Pwd) {
        String result = "true";
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("mid", mid);
        params.put("pwd", Pwd);
        String str = doPost(params);
        if (!TextUtils.isEmpty(str)) {
            result = str;
        }
        return result;
    }

    /**
     * 不需要授权
     *
     * @param ApiId 0074
     * @param id
     * @return
     */
    // 获取2级分类
    public String GetSecondCategory(String ApiId, String id) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("firstid", id);
        return doPost(params);
    }

    /**
     * 不需要授权
     * 获取三级分类商品
     *
     * @param secID 二级分类id
     * @return 商品数据（没有库存不会返回来）
     */
    public String postGetThreeCategory(String secID) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0094");
        params.put("firstid", secID);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param pageSize
     * @param pageIndex
     * @param keyWord    关键字，匹配店铺名
     * @param range      查询的距离范围，单位千米
     * @param shopType   查询店铺的所属类别
     * @param searchType 查询类别
     * @return 特定美食类型的店铺列表
     * @throws Exception
     */
    public String getFoodList(int pageSize, int pageIndex, String keyWord, int range, int shopType, String searchType, String la, String lt) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        String pSize = String.valueOf(pageSize);
        String pIndex = String.valueOf(pageIndex);
        String tRange = String.valueOf(range * 1000);
        String tShopType = shopType == 0 ? "" : String.valueOf(shopType);
        String cityName = Constants.getCityName() == null ? "" : Constants.getCityName();
        params.put("areaname", cityName);
        params.put("apiid", "0098");
        params.put("gps", la + "," + lt);
        params.put("juli", tRange);
        params.put("keyword", keyWord);
        params.put("pageIndex", pIndex);
        params.put("pageSize", pSize);
        params.put("searchtype", searchType);
        params.put("shoptype", tShopType);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param firstTypeId
     * @return
     */
    public String getFoodType(int firstTypeId) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0099");
        params.put("firstTypeId", String.valueOf(firstTypeId));
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId       0079
     * @param ordernumber
     * @param memberid
     * @param reason
     * @param remark
     * @return
     */
    // 提交申请退款信息
    public String setPayBack(String ApiId, String ordernumber, String memberid, String reason, String remark, String uid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("memberid", memberid);
        params.put("ordernumber", ordernumber);
        params.put("reason", reason);
        params.put("remark", remark);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param pguid
     * @return
     */
    // 获取商品买就赠的信息  apiid=0082
    public String getPJoinedInfo(String ApiId, String pguid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("pguid", pguid);
        return doPost(params);
    }

    /**
     * 不确定是否需要授权
     *
     * @param ApiId
     * @return
     */
    // 吃住玩购对应的店铺数量
    public String getIndexTypeCount(String ApiId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId      0081
     * @param categoryID
     * @return
     */
    // 通过分类获取商品
    public String getProductByCategory(String ApiId, String categoryID) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("catoryid", categoryID);
        return doPost(params);
    }

    // 获取手机消息提示
    // xiaohanxiang 2014-10-17
    public String postGetMobileMessage(String ApiId, String memberId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("memberId", memberId);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param ApiId
     * @param memberId
     * @return
     */
    // 获取会员金萝卜
    public String postGetMemberScore(String ApiId, String memberId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("memberId", memberId);
        params.put("tid", memberId);
        return doPost(params);
    }

    // 处理抵扣萝卜
    public String postUsedMemberScore(String ApiId, String memberId, String score) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("memberId", memberId);
        params.put("score", score);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId
     * @param proguid
     * @param arriveTime
     * @return
     */
    // 获取商品库存
    public String postGetProductCount(String ApiId, String proguid, String arriveTime) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", ApiId);
        param.put("proguid", proguid);
        param.put("daoDate", arriveTime);
        String result = "";
        result = doPost(param);
        if (TextUtils.isEmpty(result))
            result = "0";
        return result;
    }

    /**
     * 不需要授权
     *
     * @param proGuid
     * @param attrNames
     * @param attrGuids
     * @return
     */
    public String postGetProAttrCount(String proGuid, String attrNames, String attrGuids) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0305");
        param.put("attr_guid", attrGuids);
        param.put("attr_name", attrNames);
        param.put("product_guid", proGuid);
        return doPost(param);
    }

    // 获取商品库存
    public String postSetProductCount(String ApiId, String proguid, String pcount) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", ApiId);
        params.put("proguid", proguid);
        params.put("pcount", pcount);
        return doPost(params);
    }

    /**
     * 不需要授权
     *
     * @param ApiId      0088
     * @param supplierID
     * @param area
     * @param proguid
     * @param count
     * @return
     */
    // 获取运费
    public String getYunFei(String ApiId, String supplierID, String area, String proguid, String count) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("area", area);
        params.put("apiid", ApiId);
        params.put("buynumberList", count);
        params.put("sproductidlist", proguid);
        params.put("supplierId", supplierID);
        return doPost(params);
    }

    /**
     * 不需要授权
     * 获取特价区商品数据
     */
    public String postGetTJQPro() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0090");
        return doPost(params);
    }

    /**
     * 设置移动网络的开关
     *
     * @param context
     * @param enabled 是否开启移动网络
     * @author dengweilin
     */
    public void setMobileDataStatus(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // ConnectivityManager类
        Class<?> conMgrClass = null;

        // ConnectivityManager类中的字段
        Field iConMgrField = null;
        // IConnectivityManager类的引用
        Object iConMgr = null;
        // IConnectivityManager类
        Class<?> iConMgrClass = null;
        // setMobileDataEnabled方法
        Method setMobileDataEnabledMethod = null;
        try {

            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象Mservice
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());

            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod(
                    "setMobileDataEnabled", Boolean.TYPE);

            // 设置setMobileDataEnabled方法是否可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);

        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要授权
     * 立即预订方法
     *
     * @param uid       登录id
     * @param agentId   代理商id
     * @param proGuid   商品id
     * @param name      预订人姓名
     * @param address   预订人地址
     * @param mobile    预订人手机号
     * @param buyCount  预订数量
     * @param shouldPay 商品总价格
     * @param realPrice 商品实际价格
     * @param proName   商品名称
     * @param shopName  店铺名称
     * @return 预订成功订单号
     * @author dengweilin
     */
    public String postReserve(String uid, String agentId, String proGuid, String name, String address, String mobile, int buyCount, double shouldPay, double realPrice, String proName, String shopName) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("Address", address);
        params.put("AgentID", agentId);
        params.put("BuyCount", buyCount + "");
        params.put("MemLoginID", uid);
        params.put("Mobile", mobile);
        params.put("Name", name);
        params.put("ProductGuid", proGuid);
        params.put("ProductPrice", realPrice + "");
        params.put("ShopName", shopName);
        params.put("ShouldPayPrice", shouldPay + "");
        params.put("apiid", "0091");
        params.put("productName", proName);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 查询预订单数据
     *
     * @param uid 登录id
     * @return 预订单数据
     * @author dengweilin
     */
    public String postGetReserveMsg(String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("AgentID", "");
        params.put("MemLoginID", uid);
        params.put("Mobile", "");
        params.put("OrderNumber", "");
        params.put("OrderStatus", "");
        params.put("ProductName", "");
        params.put("ReceiptName", "");
        params.put("apiid", "0092");
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     * 取消线下订单
     *
     * @param guid 商品id
     * @return 是否取消成功
     * @author dengweilin
     */
    public String postCancelReserveOrder(String guid, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0093");
        params.put("guid", guid);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param msgid
     * @return
     */
    public String postGetNotice(String msgid, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0095");
        params.put("msgid", msgid);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 提交参数到服务器
     *
     * @param params post参数（键值对）
     * @return 服务器返回的字符串
     * @throws Exception
     * @author dengweilin
     * @since 2015.01.15
     */
    public String doPost(LinkedHashMap<String, String> params) {
        String result = "";
        int stateCode = 0;
        OkHttpClient httpClient = null;
        Request request = null;
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        try {
            params.put("appver", versionName);
            params.put("platform", Constants.APP_PLATFORM);
            if (!params.containsKey("dateline")) {
                params.put("dateline", String.valueOf(System.currentTimeMillis()));
            }
            if (!params.containsKey("tid")) {
                params.put("tid", uid);
            }
            //先转成ArrayList集合
            ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(params.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return o1.getKey().compareTo(o2.getKey());
                }
            });
            params.clear();
            //需要授权的接口需要拼接signs以及token参数
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : list) {
                params.put(entry.getKey(), entry.getValue());
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            list.clear();
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            String signs = Base64.encode(stringBuilder.toString().getBytes("UTF-8"));
            params.put("signs", signs);
            params.put("token", Constants.USER_TOKEN);
            for (String key : params.keySet()) {
                String str = params.get(key);
                bodyBuilder.add(key, TextUtils.isEmpty(str) ? "" : str);
            }

            httpClient = new OkHttpClient.Builder()
                    .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .build();

            request = new Request.Builder()
                    .url(apiSrcUrl)
                    .post(bodyBuilder.build())
                    .build();
            Response response = httpClient.newCall(request).execute();
            stateCode = response.code();
            if (stateCode == 200) {
                result = response.body() == null ? "" : CommonTools.decodeUnicode(response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient = null;
            request = null;
            Logger.i("ApiRequest", stateCode + "\n" + params.toString() + "\n" + result);
        }
        return result;
    }
//    public String doPost(List<NameValuePair> params) {
//        HttpPost post = new HttpPost(apiSrcUrl);
//        String result = "";
//        int stateCode = 0;
//
//        try {
//            params.put("appver", versionName);
//            params.put("platform", Constants.APP_PLATFORM);
//            Collections.sort(params, new Comparator<NameValuePair>() {
//                @Override
//                public int compare(NameValuePair o1, NameValuePair o2) {
//                    return o1.getName().compareTo(o2.getName();
//                }
//            });
//            //需要授权的接口需要拼接signs以及token参数
//            StringBuilder stringBuilder = new StringBuilder();
//            for (NameValuePair param : params) {
//                stringBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");
//            }
//            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length();
//            String signs = Base64.encode(stringBuilder.toString().getBytes("UTF-8");
//            params.put("signs", signs);
//            params.put("token", Constants.USER_TOKEN);
//            if (Constants.isNeedTestFlag) {
//                params.put("testFlag", "1");
//            }
//
//
//            HttpClient client = new DefaultHttpClient();
//            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            HttpResponse response = client.execute(post);
//            stateCode = response.getStatusLine().getStatusCode();
//            if (stateCode == HttpStatus.SC_OK) {
//                result = EntityUtils.toString(response.getEntity();
//                result = CommonTools.decodeUnicode(result);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            Logger.i("ApiRequest", stateCode + "\n" + params.toString() + "\n" + result);
//            post.abort();
//        }
//        return result;
//    }


    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }


    /**
     * 参数列表排序
     *
     * @param map
     * @return
     */
    public LinkedHashMap<String, String> sortMap(Map<String, String> map) {
        LinkedHashMap<String, String> hashMap = new LinkedHashMap<>();
        //先转成ArrayList集合
        ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        for (Map.Entry<String, String> entry : list) {
            hashMap.put(entry.getKey(), entry.getValue());
        }
        return hashMap;
    }


    /**
     * 提交post参数到服务器
     *
     * @param params 参数列表
     * @return 请求结果
     */
//    private String getHttpPostResult(List<NameValuePair> params, String apiId) {
//        HttpPost httpPost = new HttpPost(apiSrcUrl);
//        HttpResponse httpResponse;
//        int stateCode = 0;
//        String result = "";
//
//        try {
//            if (Constants.list_needToken.contains(apiId)) {
//                StringBuilder stringBuilder = new StringBuilder();
//                for (NameValuePair param : params) {
//                    stringBuilder.append(param.getName()).append("=").append(param.getValue()).append("&");
//                }
//                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length();
//                String signs = Base64.encode(stringBuilder.toString().getBytes("utf-8");
//                params.put("signs", signs);
//                params.put("token", Constants.USER_TOKEN);
//            }
//            if (Constants.isNeedTestFlag) {
//                params.put("testFlag", "1");
//            }
//            // 设置httpPost请求参数
//            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8);
//            httpResponse = new DefaultHttpClient().execute(httpPost);
//            // System.out.println("POST:"+httpResponse.getStatusLine().getStatusCode();
//            stateCode = httpResponse.getStatusLine().getStatusCode();
//            if (stateCode == HttpStatus.SC_OK) {
//                // 第三步，使用getEntity方法获得返回结果
////				result = EntityUtils.toString(httpResponse.getEntity();
//                result = CommonTools.decodeUnicode(EntityUtils.toString(httpResponse.getEntity());
//                // System.out.println("result:" + result);
//                // T.displayToast(HttpURLActivity.this, "result:" + result);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            Logger.i("ApiRequest", stateCode + "\n" + params.toString() + "\n" + result);
//            httpPost.abort();
//        }
//        return result;
//    }
    public boolean isDebug() {
        return Constants.isDebug;
    }

    /**
     * 获取系统图库数据
     *
     * @author dengweilin
     * @since 2015.01.15
     */
    public Cursor getPhotoMsg(Context context) {
        String _id = MediaStore.Images.Media._ID;
        String _bucket_id = MediaStore.Images.Media.BUCKET_ID;// 直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
        String _bucket_display_name = MediaStore.Images.Media.BUCKET_DISPLAY_NAME; // 直接包含该图片文件的文件
        String _display_name = MediaStore.Images.Media.DISPLAY_NAME; // 图片文件名
        String _data = MediaStore.Images.Media.DATA; // 图片绝对路径
        String _data_modified = MediaStore.Images.Media.DATE_MODIFIED;// 图片修改时间
        String[] projection = new String[]{_id, _bucket_id, _bucket_display_name, _display_name, _data, _data_modified};

        //查找android的系统图库数据
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        //图片按时间排序
        return cr.query(uri, projection, null, null, _data_modified + " desc");
    }

    /**
     * 不需要授权
     *
     * @param shopid
     * @return
     */
    public String getCommentList(String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0100");
        param.put("shopid", shopid);
        return doPost(param).replaceAll("\n|\r| ", "");
    }

    /**
     * 需要授权
     * 提交进出帐记录
     *
     * @param encryptStr 加密文本
     * @return 是否充值或提现状态json
     * @author dengweilin
     * @since 2015.03.13
     */
    public String postMoneyInfo(String encryptStr, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
//		param.put("apiid", "0104");
        param.put("apiid", "0225");
        param.put("strDetinfo", encryptStr);
        param.put("tid", uid);
        return doPost(param).replaceAll("\n|\r| ", "");
    }

    /**
     * 需要授权
     *
     * @param guid
     * @param uid
     * @param type
     * @return
     */
    public String postEditGoods(String guid, String uid, String type) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0232");
        params.put("guid", guid);
        params.put("type", type);
        return doPost(params);
    }

    /**
     * 需要授权
     * 提交提现记录
     *
     * @param encryptStr 提现加密文本
     * @param type       0为提现到银行卡，1为提现到支付宝
     * @return
     * @throws Exception
     */
    public String postTakeMoneyInfo(String encryptStr, int type, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0105");
        param.put("dateline", String.valueOf(System.currentTimeMillis()));
        param.put("strDetinfo", encryptStr);
        //versionCode: 53
        param.put("tid", uid);
        param.put("type", String.valueOf(type));
        return doPost(param).replaceAll("\n|\r| ", "");
    }

    /**
     * 需要授权
     * 用户获取出入账记录
     */
    public String getMoneyRec(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0106");
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param).replaceAll("\n|\r", "");
    }

    /**
     * 需要授权
     * 用户获取提现记录
     */
    public String getTakeMoneyInfo(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0107");
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param).replaceAll("\n|\r", "");
    }

    /**
     * 需要授权
     * 返回保留两位小数的字符串
     *
     * @param apiid 0224 可提现余额， 0108 总额（可提现和不可提现）
     */
    @SuppressLint("DefaultLocale")
    public String getWalletMoney(String apiid, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", apiid);
        param.put("memloginid", uid);
        param.put("tid", uid);
        String post = doPost(param);
        return TextUtils.isEmpty(post.replaceAll("\n|\r| ", "")) ? "0" : post.replaceAll("\n|\r| ", "");
//        return String.format("%.2f", Double.parseDouble(TextUtils.isEmpty(post.replaceAll("\n|\r| ", "")) ? "0" : post.replaceAll("\n|\r| ", ""));
    }

    /**
     * 需要授权
     *
     * @param uid
     * @param cardNum
     * @return
     */
    public String bindBankCard(String uid, String cardNum) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0109");
        param.put("cardnumber", cardNum);
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取用户的银行卡信息
     *
     * @param uid
     * @return
     */
    public String getBindBankCardInfo(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0110");
        param.put("memloginid", uid);
        param.put("tid", uid);
        String result = doPost(param);
        result = result.replaceAll("\n|\r| ", "");
        return result;
    }

    /**
     * 需要授权
     * 获取md5加密后的支付密码
     */
    public String getPayPswMD5(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0111");
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param).replaceAll(" ", "");
    }

    /**
     * 需要授权
     * 用户设置支付密码
     */
    public String postSetPayPsw(String uid, String pswMD5) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0112");
        param.put("memloginid", uid);
        param.put("paypwd", pswMD5);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 绑定手机号
     *
     * @throws Exception
     */
    public String postBindPNumber(String uid, String pNum) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0113");
        param.put("memloginid", uid);
        param.put("mobile", pNum);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取用户绑定手机
     */
    public String getBindPNum(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0114");
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param).replaceAll(" |\n|\r", "");
    }

    /**
     * 不需要授权
     *
     * @param pNum
     * @param vCode
     * @return
     */
    public boolean checkSMSCode(String pNum, String vCode) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0078");
        param.put("mobile", pNum);
        param.put("vcode", vCode);
        return Boolean.parseBoolean(doPost(param));
    }

    /**
     * 需要授权
     *
     * @param aesStr
     * @param priceData
     * @return
     */
    public String accountToBuy(String aesStr, String priceData, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("aesstr", aesStr);
        param.put("apiid", "0116");
        param.put("pricedata", priceData);
        param.put("tid", uid);
//		param.put("json", json);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取图片服务器地址
     *
     * @param uid
     * @return
     */
    public String getImageServer(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0154");
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取订单信息.接口为0329
     *
     * @param uid
     * @param guid
     * @return
     */
    public String getStockOrderInfo(String uid, String guid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0329");
        param.put("guid", guid);
        param.put("memLoginId", uid);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取退货商品详细信息 接口-->0333
     *
     * @param uid
     * @param wareHouseOrderOrderNumber
     * @return
     */
    public String getStockReturnOrderInfo(String uid, String wareHouseOrderOrderNumber) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0333");
        param.put("memLoginId", uid);
        param.put("tid", uid);
        param.put("wareHouseOrderOrderNumber", wareHouseOrderOrderNumber);
        return doPost(param);
    }

    /**
     * 需要授权
     * 设置支付方式时通知后台
     *
     * @param uid
     * @param wareHouseOrderReturnGuid
     * @param payWay
     * @param deviceType
     * @return
     */
    public String setReturnPayWay(String uid, String wareHouseOrderReturnGuid, String payWay, String deviceType) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0334");
        param.put("deviceType", deviceType);
        param.put("mobilePayWay", payWay);
        param.put("tid", uid);
        param.put("wareHouseOrderReturnGuid", wareHouseOrderReturnGuid);
        return doPost(param);
    }


    /**
     * 需要授权
     * 2018年1月3日
     * 囤货账号支付
     *
     * @param encryptStr
     * @return
     */
    public String stockAccountPay(String encryptStr, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0331");
        param.put("encryptStr", encryptStr);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 支付退货运费
     *
     * @param encryptStr
     * @return
     */
    public String payReturnCost(String encryptStr, String uid) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("apiid", "0332");
        params.put("encryptStr", encryptStr);
        params.put("tid", uid);
        return doPost(params);
    }

    /**
     * 需要授权
     *
     * @param shopid 店铺id,作为推送标识
     * @param cType  互动类型，1、点菜，2、预订，3、店内服务
     * @param aesStr 信息加密字符串
     * @return
     * @throws Exception
     */
    public String postFoodServiceInfo(String shopid, int cType, String aesStr, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("alias", shopid);
        param.put("apiid", "0117");
        param.put("ctype", String.valueOf(cType));
        param.put("md5str", aesStr);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param shopid
     * @param uid
     * @return
     */
    public String getCashPaper(String shopid, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0202");
        param.put("memberid", uid);
        param.put("shopid", shopid);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param guid
     * @param uid
     * @param orderMoney
     * @return
     */
    public String getFavTickets(String guid, String uid, String orderMoney) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0294");
        param.put("memberid", uid);
        param.put("ordermoney", orderMoney);
        param.put("productid", guid);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 不需要授权
     * 一键开店功能，获取用户自身的店铺名
     */
    public String getShopName(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0127");
        param.put("memberid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param uid           用户登录id
     * @param ticketNumbers 代金券编号，多个用逗号分隔
     * @return 是否成功使用代金券
     * @throws Exception
     */
    public String useTickets(String uid, String ticketNumbers) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0208");
        param.put("memLoginID", uid);
        param.put("ticketNumber", ticketNumbers);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param shopid
     * @return
     */
    public String getAlcoholRecord(String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0137");
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 不需要授权
     *
     * @param shopid
     * @return
     */
    public String isQuanYan(String shopid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0126");
        param.put("shopid", shopid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param uid
     * @return
     */
    public String getAlcoholData(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0140");
        param.put("mid", uid);
        param.put("tid", uid);
        return doPost(param);
    }

    public String checkShopState(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0141");
        param.put("shopid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 商家后台添加商品
     *
     * @param shopid
     * @param name             商品名称
     * @param image            商品图片名称
     * @param shopPrice        商品价格
     * @param marketPrice      市场价
     * @param repertoryCount   库存
     * @param type             1 立即上架， 0 不立即上架
     * @param pDetail          商品详情(html字符串)
     * @param productImageList 商品详情图片（多个图片用逗号隔开）
     * @return
     * @throws Exception
     */
    public String postAddGoods(String shopid, String name, String image, double shopPrice, double marketPrice, int repertoryCount, int type, String pDetail, String productImageList, String uid) {
        name = name.replaceAll("\n|\r", "");
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0233");
        param.put("image", image);
        param.put("marketprice", String.valueOf(CommonTools.formatZero2Str(marketPrice)));
        param.put("name", name);
        param.put("repertoryCount", String.valueOf(repertoryCount));
        param.put("shopid", shopid);
        param.put("shopprice", String.valueOf(CommonTools.formatZero2Str(shopPrice)));
        param.put("tid", uid);
        param.put("type", String.valueOf(type));
        if (pDetail != null && productImageList != null) {
            param.put("pdetails", pDetail);
            param.put("productImglist", productImageList);
        }
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param guid
     * @param name
     * @param image
     * @param shopPrice
     * @param marketPrice
     * @param repertoryCount
     * @param pDetail
     * @param productImageList
     * @return
     */
    public String postEditGoods(String guid, String name, String image, double shopPrice, double marketPrice, int repertoryCount, String pDetail, String productImageList, String uid) {
        name = name.replaceAll("\n|\r", "");
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0236");
        param.put("guid", guid);
        param.put("image", image);
        param.put("marketprice", String.valueOf(CommonTools.formatZero2Str(marketPrice)));
        param.put("name", name);
        param.put("repertoryCount", String.valueOf(repertoryCount));
        param.put("shopprice", String.valueOf(CommonTools.formatZero2Str(shopPrice)));
        param.put("tid", uid);
        if (pDetail != null && productImageList != null) {
            param.put("pdetails", pDetail);
            param.put("productImglist", productImageList);
        }
        return doPost(param);
    }

    public String getLeaderInfo(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0245");
        param.put("shopid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param uid
     * @return
     */
    public String getPayAccountNum(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0150");
        param.put("memloginid", uid);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 不需要授权
     * 0264-提交红包信息
     *
     * @param uid    用户账户
     * @param leadid 用户上级id，注册、分享时传空字符串，开店时传上级id
     * @param type   1-注册账号；2-开店；3-分享
     * @return true/false
     */
    public String postRedPacket(String uid, String leadid, int type) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0264");
        param.put("leadid", leadid);
        param.put("memberid", uid);
        param.put("type", String.valueOf(type));
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param codeNumber
     * @param guids
     * @return
     */
    public String postCheckFavNum(String codeNumber, String guids, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0295");
        param.put("codenumber", codeNumber);
        param.put("productid", guids);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     *
     * @param orderNum
     * @return
     */
    public String postGetOrderHead(String orderNum, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0036");
        param.put("ordernumber", orderNum);
        param.put("tid", uid);
        return doPost(param);
    }

    public String postGetOrderBody(String orderNum, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0072");
        param.put("ordernumber", orderNum);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取订单剩余支付时间
     */
    public String postGetOrderPayTime(String orderNum, String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0309");
        param.put("ordernumber", orderNum);
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 不需要授权
     * 2018年5月22日
     * 获取温泉产品分类，订单详情页面跳转门票、客房使用
     *
     * @param productGuid 产品Guid00
     * @return ProductCategory:  1-门票，2-客房
     * <p>
     * ticket_route.html?productCategory=*&guid=*
     */
    public String postGetProductCategory(String productGuid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0338");
        param.put("productGuid", productGuid);
        return doPost(param);
    }

    /**
     * 不需要授权
     * 下单时界面是否可用金萝卜
     *
     * @param productGuid 产品guid   多个产品时，使用  ,  隔开
     * @return { "result":"false", "info": "1"}   true--可用  false--不可用
     */
    public String postCanUseJLB(String productGuid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0339");
        param.put("productGuid", productGuid);
        return doPost(param);
    }

    /**
     * 需要授权
     * 获取优惠券数量
     *
     * @param loginId 登录id
     * @return
     */
    public String postGetCouponCount(String loginId) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0346");
        param.put("memLoginId", loginId);
        param.put("tid", loginId);
        return doPost(param);
    }

    /**
     * 需要授权
     * 分类获取订单头信息
     *
     * @param loginId   登录id
     * @param type      类型  0-全部，1-待付款，2-待发货，3-待收货，4-退款
     * @param pageIndex 页码
     * @param pageSize  页容量
     * @return
     */
    public String postGetOrderByType(String loginId, String type, int pageIndex, int pageSize) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0337");
        param.put("memLoginId", loginId);
        param.put("orderType", type);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("tid", loginId);
        return doPost(param);
    }

    /**
     * 首页强制弹窗--如强制升级
     *
     * @param loginId
     * @param shopId
     * @return
     */
    public String checkAppState(String loginId, String shopId) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0349");
        param.put("memLoginId", loginId);
        param.put("shopid", shopId);
        param.put("tid", loginId);
        return doPost(param);
    }

    public String getMyCouponList(String uid, String isUsedOrIsDelete, int pageIndex, int pageSize) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0343");
        param.put("isUsedOrIsDelete", isUsedOrIsDelete);
        param.put("memLoginId", uid);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSize", String.valueOf(pageSize));
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 获取app客户端配置
     *
     * @return
     */
    public String getAppConfig(String uid) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0351");
        param.put("tid", uid);
        return doPost(param);
    }

    /**
     * 获取商品支付方式
     *
     * @param productGuids 多个商品使用英文逗号隔开
     * @return {"val":[{"PayWay":"Alipay,AccountPay,WechatPay,ArrivedPay"}]}
     */
    public String getProductPayway(String productGuids) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0352");
        param.put("productGuid", productGuids);
        return doPost(param);
    }

    /**
     * 修改订单支付方式
     *
     * @param orderNumber
     * @param uid
     * @param deviceType  android
     * @param paymentName Alipay,WechatPay,AccountApy,ArrivedPay
     * @return
     */
    public String getChangeOrderPayway(String orderNumber, String uid, String deviceType, String paymentName) {
        LinkedHashMap<String, String> param = new LinkedHashMap<>();
        param.put("apiid", "0353");
        param.put("memLoginId", uid);
        param.put("orderNumber", orderNumber);
        param.put("deviceType", deviceType);
        param.put("paymentName", paymentName);
        return doPost(param);
    }


}
