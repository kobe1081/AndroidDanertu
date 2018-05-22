package com.danertu.tools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

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
import com.umeng.analytics.MobclickAgent;

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

    // post取用户输入的登录信息判断登录状态
    public String postLoginInfo(String ApiId, String uId, String pwd) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uid", uId));
        params.add(new BasicNameValuePair("pwd", pwd));
        return getHttpPostResult(params);
    }

    // Post方式提交注册信息
    public String postInfo(String ApiId, String uId, String pwd, String email) {
        // Post 方法比Get方法????设置的参数更??
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uid", uId));
        params.add(new BasicNameValuePair("pwd", pwd));
        params.add(new BasicNameValuePair("email", email));
        return getHttpPostResult(params);
        // HttpURLConnection httpconn = (HttpURLConnection) new URL(srcUrl)
        // .openConnection();
        // // post 方式，输入输出需要设置为true
        // httpconn.setDoInput(true);
        // httpconn.setDoOutput(true);
        // httpconn.setRequestMethod("POST"); // 设置为Post方式，默认为get方式
        // httpconn.setUseCaches(false); // 不使用缓??
        // httpconn.setInstanceFollowRedirects(true); // 重定??
        // httpconn.setRequestProperty("Content-type",
        // "Application/x-www-form-urlencoded"); // 设置连接 的Content-type类型为：
        // // application/x-www-form-urlencoded
        // httpconn.connect(); //连接
        //
        // DataOutputStream out = new
        // DataOutputStream(httpconn.getOutputStream()); //声明数据写入??
        //
        // String content = "NAME="+URLEncoder.encode("fly_binbin", "gb2312");
        //
        // out.writeBytes(content);
        //
        // out.flush();
        // out.close();
        //
        // BufferedReader reader = new BufferedReader(new
        // InputStreamReader(httpconn.getInputStream()));
        //
        // String line = "";
        // String resultDate = "";
        // while((line=reader.readLine())!=null)
        // {
        // resultDate += line;
        // }
        // return resultDate;

    }

    // post取用户输入的登录信息判断登录名是否存在?
    public String postUserIsExist(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uId));
        return getHttpPostResult(params);
    }

    // post取用户收货地址
    public String postGetUserAddress(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uId));
        return getHttpPostResult(params);
    }

    // post取用户删除收货地址
    public String postDeleteUserAddress(String ApiId, String guid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("gid", guid));
        return getHttpPostResult(params);
    }

    // post保存用户收货地址
    public String postInsertUserAddress(String ApiId, String uId, String uName, String mobile, String address, String isDefault, String guid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("uId", uId);
        param.put("name", uName);
        param.put("adress", address);
        param.put("mobile", mobile);
        param.put("isdefault", isDefault);
        param.put("aguid", guid);
        return doPost(param);
    }

    // post用户设置收货地址为默认操作请求
    public String postSetAddressIsDefault(String ApiId, String guid, String uid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("aid", guid));
        params.add(new BasicNameValuePair("_uid", uid));
        return getHttpPostResult(params);
    }

    /*
     * post留言到服务器 liujun 2014-7-15
     */
    public String postInsertMessage(String ApiId, String uid, String message) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uid));
        params.add(new BasicNameValuePair("content", message));
        return getHttpPostResult(params);
    }

    // post获取订单信息请求

    /**
     * @param ApiId
     * @param uid
     * @param type  为1时查询当月订单，否则为全部
     * @return
     */
    public String postGetUserOrderHead(String ApiId, String uid, String type) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("uId", uid);
        param.put("type", type);
        return doPost(param);
    }

    /**
     * 2018年4月24日
     * 添加分页查询
     * @param ApiId
     * @param uid
     * @param type
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public String postGetUserOrderHead(String ApiId, String uid, String type, int pageIndex, int pageSize) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("uId", uid);
        param.put("type", type);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSize", String.valueOf(pageSize));
        return doPost(param);
    }

    /*
     * post留言到服务器 liujun 2014-7-15
     */
    public String postInsertMessage(String ApiId, String uid, String message, String guid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uid));
        params.add(new BasicNameValuePair("content", message));
        params.add(new BasicNameValuePair("guid", guid));
        return getHttpPostResult(params);
    }

    /*
     * post获取留言列表 liujun 2014-7-16
     */
    public String postGetMessage(String ApiId, String uId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uId));
        return getHttpPostResult(params);
    }

    /*
     * 更新消息状态 liujun 2014-7-16
     */
    public String postUpdateMsgState(String ApiId, String guid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("guid", guid));
        return getHttpPostResult(params);
    }

    // 取订单详情
    public String postGetOrderInfoShow(String ApiId, String orderNumber) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("ordernumber", orderNumber));
        return getHttpPostResult(params);
    }

    // 根据参数取店铺列表
    public String postGetIndexShopList(String ApiId, String cityName, int pageSize, int pageIndex, String keyword, String type) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("pagesize", String.valueOf(pageSize));
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("kword", keyword);
        param.put("type", type);
        if (cityName == null) {
            param.put("areaCode", "0000");
        } else {
            if (cityName.equals(Constants.getDcityName())) {
                param.put("gps", Constants.getLa() + "," + Constants.getLt());
                param.put("less", "80000");
                param.put("areaCode", "");
            } else {
                param.put("areaCode", cityName);
            }
        }
        return doPost(param);
    }

    // post 提交订单
    // liujun 2014-7-19

    /**
     * @param imei
     * @param mac
     * @param deviceID
     * @param postString
     * @param remark     备注用户使用了多少积分或代金券
     * @param isBackCall 表示是否为后台拿货商品
     * @return
     */
    public String postOrder(String imei, String mac, String deviceID, String postString, String remark, boolean isBackCall) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0097");
        param.put("jsonstr", postString);
        param.put("imei", imei + "");
        param.put("mac", mac + "");
        param.put("deviceid", deviceID + "");
        param.put("remark", remark);
        if (isBackCall) {
            param.put("isbackcall", "1");
        }
        return doPost(param);
    }

    /**
     * 囤货订单
     */
    public String postStockOrder(String memLoginId, String productId, String buyNumber, String mobilePayWay, String deviceType, String remark, String appVersion, String payPrice) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0324");
        param.put("memLoginId", memLoginId);
        param.put("productId", productId);
        param.put("buyNumber", buyNumber);
        param.put("mobilePayWay", mobilePayWay);
        param.put("deviceType", deviceType);
        param.put("remark", remark);
        param.put("appVersion", appVersion);
        param.put("payPrice", payPrice);
        return doPost(param);
    }

    public String postTasteWine(String uid, String name, String address, String mobile, String remark, String requestNum, String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0135");
        param.put("memberid", uid);
        param.put("name", name);
        param.put("address", address);
        param.put("mobile", mobile);
        param.put("remark", remark);
        param.put("requestnum", requestNum);
        param.put("shopid", shopid);
        return doPost(param);
    }

    // post 提交一元订单
    // liujun 2014-12-31
    public String postYiYuanOrder(String ApiId, String postString, String uid, String pid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("jsonstr", postString));
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("pid", pid));
        return getHttpPostResult(params);
    }


    // 获取店铺详细
    public String postGetShopDetails(String ApiId, String shopid) {
        String result = "";
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("shopid", shopid);
        param.put("la", Constants.getLa());
        param.put("lt", Constants.getLt());
        result = doPost(param);
        return result;
    }

    // 获取店铺产品
    public String postGetShopProduct(String ApiId, String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("shopid", shopid);
        return doPost(param);
    }

    // post根据商品guid获取商品
    // liujun 2014-7-20
    public String postGetProductInfo(String ApiId, String guid, String imei, String mac, String deviceid, String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("proId", guid);
        param.put("imei", imei);
        param.put("mac", mac);
        param.put("deviceid", deviceid);
        param.put("shopid", shopid);
        return doPost(param);
    }

    // post取商品列表
    // liujun 2014-7-20
    public String postGetProductList(String ApiId, String keyWord, String type, String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("type", type);
        param.put("kword", keyWord);
        param.put("memberid", shopid);
        return doPost(param);
    }

    // post取供应商商品列表
    // liujun 2014-7-25
    public String postGetSupplierProductList(String ApiId, int pageSize, int pageIndex) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
        return getHttpPostResult(params);
    }

    // post搜索店铺列表
    // liujun 2014-7-26
    public String postGetSearchResult(String ApiId, String keywords) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keywords);
        return doPost(param);
    }

    // post搜索平台商品
    // liujun 2014-7-29
    public String postGetSearchProductResult(String ApiId, String keywords) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("kword", keywords);
        return doPost(param);
    }

    // 获取附近店铺列表(默认5公里)
    public String postGetNearByShopList(String ApiId, String cityName, int pageSize, int pageIndex, String keyword, String type) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("areaCode", cityName));
        params.add(new BasicNameValuePair("pagesize", String.valueOf(pageSize)));
        params.add(new BasicNameValuePair("pageIndex", String.valueOf(pageIndex)));
        params.add(new BasicNameValuePair("kword", keyword));
        params.add(new BasicNameValuePair("type", type));
//		String la = Constants.getLa();
//		String lt = Constants.getLt();
        String gps = Constants.getLa() + "," + Constants.getLt();
        params.add(new BasicNameValuePair("gps", gps));
        params.add(new BasicNameValuePair("less", "80000")); // 5公里范围
        return getHttpPostResult(params);
    }

    // 校验服务器上今天是否玩过互动游戏
    // liujun 7-30
    public String postCheckPlayData(String ApiId, String versionNo, String playDate) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("versionNo", versionNo));
        params.add(new BasicNameValuePair("playdate", playDate));
        return getHttpPostResult(params);
    }

    // post添加服务器互动表数据
    // liujun 7-29
    public String postInsertHudongScore(String ApiId, String versionNo, String score, String uid, String forUid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uid", uid));
        params.add(new BasicNameValuePair("versionNo", versionNo));
        params.add(new BasicNameValuePair("score", score));
        params.add(new BasicNameValuePair("foruid", forUid));
        return getHttpPostResult(params);
    }

    /**
     * post 修改密码
     *
     * @param ApiId
     * @param mid
     * @param oldPwd
     * @param newPwd
     * @return
     */
    public String postChangePassword(String ApiId, String mid, String oldPwd, String newPwd) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("mid", mid);
        param.put("pwd", newPwd);
        param.put("oldpwd", oldPwd);
        return doPost(param);
    }

    // liujun 8-6
    public String postCheckPlayDataFor(String ApiId, String versionNo, String playDate, String foruid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("versionNo", versionNo));
        params.add(new BasicNameValuePair("playdate", playDate));
        params.add(new BasicNameValuePair("formid", foruid));
        return getHttpPostResult(params);
    }

    /**
     * 签到逻辑判断
     */
    public String judgeSignIn(String ApiId, String mid, String playDate) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uid", mid));
        params.add(new BasicNameValuePair("playdate", playDate));
        return getHttpPostResult(params);
    }

    /**
     * 插入签到记录
     */
    public String insertSignIn(String ApiId, String mid) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uid", mid));
        return getHttpPostResult(params);
    }

    /**
     * 获取签到记录
     */
    public String getSignIn(String ApiId, String mid) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mid", mid));
        return getHttpPostResult(params);
    }

    /**
     * 获取企业数据
     */
    public String getParams(String ApiId) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        return getHttpPostResult(params);
    }

    // post获取代付款订单
    // liujun 8-10
    public String postGetWaitPayOrder(String ApiId, String uid, String type) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("uId", uid));
        params.add(new BasicNameValuePair("type", type));
        return getHttpPostResult(params);
    }

    /**
     * 更新订单状态
     *
     * @param orderNumber
     * @return
     */
    public String updateOrderStatues(String orderNumber) {
        String ApiId = "0056";
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("ordernumber", orderNumber));
        return getHttpPostResult(params);
    }

    // 获取服务器版本号
    public String getVersionNo(String ApiId) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        return getHttpPostResult(params);
    }

    // 提交gps坐标到服务器
    public String addGPS(String ApiId, String mId, String gps, String dm) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("memberId", mId));
        params.add(new BasicNameValuePair("gps", gps));
        params.add(new BasicNameValuePair("dm", dm));
        return getHttpPostResult(params);
    }

    // 获取当前登录用户绑定的店铺
    public String getBound(String ApiId, String uid) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mobile", uid));
        return getHttpPostResult(params);
    }

    // 获取当前登录用户绑定的店铺
    public String getShareCount(String ApiId, String shopid) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("shopid", shopid));
        return getHttpPostResult(params);
    }

    // 更新积分
    public String updateUserScore(String ApiId, String uid, String score) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("memberid", uid);
        param.put("score", score);
        return doPost(param);
    }

    // 获取该设备是否第一次安装
    public String isFirstSetUp(String ApiId, String mcode) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mcode", mcode));
        return getHttpPostResult(params);
    }

    // 写入第一次安装信息
    public String insertFirstSetUp(String ApiId, String mcode) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mcode", mcode));
        return getHttpPostResult(params);
    }

    // 收集异常信息
    public String sendErrInfo(String ApiId, String ErrorInfo) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("errinfo", ErrorInfo));
        return getHttpPostResult(params);
    }

    public String sendErrInfo(String ErrorInfo) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", "0065"));
        params.add(new BasicNameValuePair("errinfo", ErrorInfo));
        return getHttpPostResult(params);
    }

    // 获取商品评论
    public String getProductComment(String ApiId, String productGuid, int pagesize, int pageIndex) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("productguid", productGuid));
        params.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        params.add(new BasicNameValuePair("pageindex", String.valueOf(pageIndex)));
        return getHttpPostResult(params);
    }

    // 添加商品评论
    public String addComment(String ApiId, String productGuid, String loginID, String content, String agentID, String rank) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("productguid", productGuid));
        params.add(new BasicNameValuePair("memloginid", loginID));
        params.add(new BasicNameValuePair("content", content));
        params.add(new BasicNameValuePair("agentID", agentID));
        params.add(new BasicNameValuePair("rank", rank));
        return getHttpPostResult(params);
    }


    // 商品评论条数
    public String getCommentCount(String ApiId, String productGuid) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("productguid", productGuid));
        return getHttpPostResult(params);
    }

    // 判断是否有商品评论权限
    public String couldComment(String ApiId, String productGuid, String loginID) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("proguid", productGuid));
        params.add(new BasicNameValuePair("memberid", loginID));
        return getHttpPostResult(params);
    }

    // 取当前定位地区优惠券
    public String getTicket(String ApiId, String city) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("areaname", city));
        return getHttpPostResult(params);
    }

    // 按关键字搜索店铺与入驻商品列表
    // xiaohanxiang 2014-10-17
    public String postGetSearchShopProduct(String ApiId, String shopId, String keyword, int pagesize, int pageindex) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("shopid", shopId));
        params.add(new BasicNameValuePair("keyword", keyword));
        params.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        params.add(new BasicNameValuePair("pageindex", String.valueOf(pageindex)));
        return getHttpPostResult(params);
    }

    // 获取一级分类
    public String getFirstCategory(String ApiId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        return getHttpPostResult(params);
    }

    /**
     * 取消订单
     *
     * @param orderNumber 订单号
     * @return 是否取消订单成功
     * @author dengweilin
     */
    public boolean postCancelOrder(String orderNumber) {
        String ApiId = "0075";
        HttpResponse response = null;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(apiSrcUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("ordernumber", orderNumber));
        try {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 确认收货
     *
     * @param orderNumber 订单号
     * @return 是否确认收货成功
     * @author dengweilin
     */
    public boolean postFinishOrder(String orderNumber) {
        String ApiId = "0076";
        HttpResponse response = null;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(apiSrcUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("ordernumber", orderNumber));
        try {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 发送手机验证码
    // xiaohanxiang 2014-10-31
    public String postSendVerityCode(String ApiId, String phoneNum) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mobile", phoneNum));
        return getHttpPostResult(params);
    }

    // 验证手机验证码
    // xiaohanxiang 2014-10-31
    public String postCheckVerityCode(String ApiId, String phoneNum, String vcode) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mobile", phoneNum));
        params.add(new BasicNameValuePair("vcode", vcode));
        return getHttpPostResult(params);
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
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("mid", mid));
        params.add(new BasicNameValuePair("pwd", Pwd));
        if (!TextUtils.isEmpty(getHttpPostResult(params))) {
            result = getHttpPostResult(params);
        }
        return result;
    }

    // 获取2级分类
    public String GetSecondCategory(String ApiId, String id) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("firstid", id));
        return getHttpPostResult(params);
    }

    /**
     * 获取三级分类商品
     *
     * @param secID 二级分类id
     * @return 商品数据（没有库存不会返回来）
     */
    public String postGetThreeCategory(String secID) {
        String apiID = "0094";
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiID));
        params.add(new BasicNameValuePair("firstid", secID));
        return getHttpPostResult(params);
    }

    /**
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
        HashMap<String, String> params = new HashMap<>();
        String pSize = String.valueOf(pageSize);
        String pIndex = String.valueOf(pageIndex);
        String tRange = String.valueOf(range * 1000);

        String tShopType = shopType == 0 ? "" : String.valueOf(shopType);
        String cityName = Constants.getCityName() == null ? "" : Constants.getCityName();
        params.put("apiid", "0098");
        params.put("pageSize", pSize);
        params.put("pageIndex", pIndex);
        params.put("gps", la + "," + lt);
        params.put("keyword", keyWord);
        params.put("juli", tRange);
        params.put("shoptype", tShopType);
        params.put("searchtype", searchType);
        params.put("areaname", cityName);
        return doPost(params);
    }

    public String getFoodType(int firstTypeId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("apiid", "0099");
        params.put("firstTypeId", String.valueOf(firstTypeId));
        return doPost(params);
    }

    // 提交申请退款信息
    public String setPayBack(String ApiId, String ordernumber, String memberid, String reason, String remark) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("ordernumber", ordernumber));
        params.add(new BasicNameValuePair("memberid", memberid));
        params.add(new BasicNameValuePair("reason", reason));
        params.add(new BasicNameValuePair("remark", remark));
        return getHttpPostResult(params);
    }

    // 获取商品买就赠的信息
    public String getPJoinedInfo(String ApiId, String pguid) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("pguid", pguid));
        return getHttpPostResult(params);
    }

    // 吃住玩购对应的店铺数量
    public String getIndexTypeCount(String ApiId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        return getHttpPostResult(params);
    }

    // 通过分类获取商品
    public String getProductByCategory(String ApiId, String categoryID) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("catoryid", categoryID));
        return getHttpPostResult(params);
    }

    // 获取手机消息提示
    // xiaohanxiang 2014-10-17
    public String postGetMobileMessage(String ApiId, String memberId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("memberId", memberId));
        return getHttpPostResult(params);
    }

    // 获取会员金萝卜
    public String postGetMemberScore(String ApiId, String memberId) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("memberId", memberId));
        return getHttpPostResult(params);
    }

    // 处理抵扣萝卜
    public String postUsedMemberScore(String ApiId, String memberId, String score) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("memberId", memberId));
        params.add(new BasicNameValuePair("score", score));
        return getHttpPostResult(params);
    }

    // 获取商品库存
    public String postGetProductCount(String ApiId, String proguid, String arriveTime) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", ApiId);
        param.put("proguid", proguid);
        param.put("daoDate", arriveTime);
        String result = "";
        result = doPost(param);
        if (TextUtils.isEmpty(result))
            result = "0";
        return result;
    }

    public String postGetProAttrCount(String proGuid, String attrNames, String attrGuids) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0305");
        param.put("attr_guid", attrGuids);
        param.put("attr_name", attrNames);
        param.put("product_guid", proGuid);
        return doPost(param);
    }

    // 获取商品库存
    public String postSetProductCount(String ApiId, String proguid, String pcount) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("proguid", proguid));
        params.add(new BasicNameValuePair("pcount", pcount));
        return getHttpPostResult(params);
    }

    // 获取运费
    public String getYunFei(String ApiId, String supplierID, String area, String proguid, String count) {
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", ApiId));
        params.add(new BasicNameValuePair("supplierId", supplierID));
        params.add(new BasicNameValuePair("area", area));
        params.add(new BasicNameValuePair("sproductidlist", proguid));
        params.add(new BasicNameValuePair("buynumberList", count));
        return getHttpPostResult(params);
    }

    /**
     * 获取特价区商品数据
     */
    public String postGetTJQPro() {
        String apiID = "0090";
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiID));
        return getHttpPostResult(params);
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
        String apiID = "0091";
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiID));
        params.add(new BasicNameValuePair("MemLoginID", uid));
        params.add(new BasicNameValuePair("AgentID", agentId));
        params.add(new BasicNameValuePair("ProductGuid", proGuid));
        params.add(new BasicNameValuePair("Name", name));
        params.add(new BasicNameValuePair("Address", address));
        params.add(new BasicNameValuePair("Mobile", mobile));
        params.add(new BasicNameValuePair("BuyCount", buyCount + ""));
        params.add(new BasicNameValuePair("ShouldPayPrice", shouldPay + ""));
        params.add(new BasicNameValuePair("ProductPrice", realPrice + ""));
        params.add(new BasicNameValuePair("productName", proName));
        params.add(new BasicNameValuePair("ShopName", shopName));
        return getHttpPostResult(params);
    }

    /**
     * 查询预订单数据
     *
     * @param uid 登录id
     * @return 预订单数据
     * @author dengweilin
     */
    public String postGetReserveMsg(String uid) {
        String apiID = "0092";
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiID));
        params.add(new BasicNameValuePair("OrderNumber", ""));
        params.add(new BasicNameValuePair("MemLoginID", uid));
        params.add(new BasicNameValuePair("AgentID", ""));
        params.add(new BasicNameValuePair("ProductName", ""));
        params.add(new BasicNameValuePair("OrderStatus", ""));
        params.add(new BasicNameValuePair("Mobile", ""));
        params.add(new BasicNameValuePair("ReceiptName", ""));
        return getHttpPostResult(params);
    }

    /**
     * 取消线下订单
     *
     * @param guid 商品id
     * @return 是否取消成功
     * @author dengweilin
     */
    public boolean postCancelReserveOrder(String guid) {
        String apiID = "0093";
        HttpPost post = new HttpPost(apiSrcUrl);
        HttpClient client = new DefaultHttpClient();
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiID));
        params.add(new BasicNameValuePair("guid", guid));
        try {
            post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String postGetNotice(String msgid) {
        String apiid = "0095";
        ArrayList<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("apiid", apiid));
        params.add(new BasicNameValuePair("msgid", msgid));
        return getHttpPostResult(params);
    }

    /**
     * 提交参数到服务器
     *
     * @param param post参数（键值对）
     * @return 服务器返回的字符串
     * @throws Exception
     * @author dengweilin
     * @since 2015.01.15
     */
    public String doPost(Map<String, String> param) {
        HttpPost post = new HttpPost(apiSrcUrl);
        String result = "";
        int stateCode = 0;
        try {
            HttpClient client = new DefaultHttpClient();
            List<NameValuePair> list = new ArrayList<>();
            if (param != null && !param.isEmpty()) {
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
            HttpResponse response = client.execute(post);
            stateCode = response.getStatusLine().getStatusCode();
            if (stateCode == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity());
                result = CommonTools.decodeUnicode(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Logger.i("ApiRequest", stateCode + "\n" + param.toString() + "\n" + result);
            post.abort();
        }
        return result;
    }

    /**
     * 提交post参数到服务器
     *
     * @param params 参数列表
     * @return 请求结果
     */
    private String getHttpPostResult(List<NameValuePair> params) {
        HttpPost httpPost = new HttpPost(apiSrcUrl);
        HttpResponse httpResponse;
        int stateCode = 0;
        String result = "";
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpPost);
            // System.out.println("POST:"+httpResponse.getStatusLine().getStatusCode());
            stateCode = httpResponse.getStatusLine().getStatusCode();
            if (stateCode == HttpStatus.SC_OK) {
                // 第三步，使用getEntity方法获得返回结果
//				result = EntityUtils.toString(httpResponse.getEntity());
                result = CommonTools.decodeUnicode(EntityUtils.toString(httpResponse.getEntity()));
                // System.out.println("result:" + result);
                // T.displayToast(HttpURLActivity.this, "result:" + result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Logger.i("ApiRequest", stateCode + "\n" + params.toString() + "\n" + result);
            httpPost.abort();
        }
        return result;
    }

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

    public String getCommentList(String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0100");
        param.put("shopid", shopid);
        return doPost(param).replaceAll("\n|\r| ", "");
    }

    /**
     * 提交进出帐记录
     *
     * @param encryptStr 加密文本
     * @return 是否充值或提现状态json
     * @author dengweilin
     * @since 2015.03.13
     */
    public String postMoneyInfo(String encryptStr) {
        HashMap<String, String> param = new HashMap<>();
//		param.put("apiid", "0104");
        param.put("apiid", "0225");
        param.put("strDetinfo", encryptStr);
        String result = doPost(param);
        return result.replaceAll("\n|\r| ", "");
    }

    /**
     * 提交提现记录
     *
     * @param encryptStr 提现加密文本
     * @param type       0为提现到银行卡，1为提现到支付宝
     * @return
     * @throws Exception
     */
    public String postTakeMoneyInfo(String encryptStr, int type) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0105");
        param.put("strDetinfo", encryptStr);
        //versionCode: 53
        param.put("type", String.valueOf(type));
        return doPost(param).replaceAll("\n|\r| ", "");
    }

    /**
     * 用户获取出入账记录
     */
    public String getMoneyRec(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0106");
        param.put("memloginid", uid);
        return doPost(param).replaceAll("\n|\r", "");
    }

    /**
     * 用户获取提现记录
     */
    public String getTakeMoneyInfo(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0107");
        param.put("memloginid", uid);
        return doPost(param).replaceAll("\n|\r", "");
    }

    /**
     * 返回保留两位小数的字符串
     *
     * @param apiid 0224 可提现余额， 0108 总额（可提现和不可提现）
     */
    @SuppressLint("DefaultLocale")
    public String getWalletMoney(String apiid, String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", apiid);
        param.put("memloginid", uid);
        String all = TextUtils.isEmpty(doPost(param).replaceAll("\n|\r| ", ""))?"0":doPost(param).replaceAll("\n|\r| ", "");

        return String.format("%.2f", Double.parseDouble(all));
    }

    public String bindBankCard(String uid, String cardNum) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0109");
        param.put("memloginid", uid);
        param.put("cardnumber", cardNum);
        return doPost(param);
    }

    /**
     * 获取用户的银行卡信息
     *
     * @param uid
     * @return
     */
    public String getBindBankCardInfo(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0110");
        param.put("memloginid", uid);
        String result = doPost(param);
        result = result.replaceAll("\n|\r| ", "");
        return result;
    }

    /**
     * 获取md5加密后的支付密码
     */
    public String getPayPswMD5(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0111");
        param.put("memloginid", uid);
        return doPost(param).replaceAll(" ", "");
    }

    /**
     * 用户设置支付密码
     */
    public String postSetPayPsw(String uid, String pswMD5) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0112");
        param.put("memloginid", uid);
        param.put("paypwd", pswMD5);
        return doPost(param);
    }

    /**
     * 绑定手机号
     *
     * @throws Exception
     */
    public String postBindPNumber(String uid, String pNum) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0113");
        param.put("memloginid", uid);
        param.put("mobile", pNum);
        return doPost(param);
    }

    /**
     * 获取用户绑定手机
     */
    public String getBindPNum(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0114");
        param.put("memloginid", uid);
        return doPost(param).replaceAll(" |\n|\r", "");
    }

    public boolean checkSMSCode(String pNum, String vCode) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0078");
        param.put("mobile", pNum);
        param.put("vcode", vCode);
        return Boolean.parseBoolean(doPost(param));
    }

    public String accountToBuy(String aesStr, String priceData) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0116");
        param.put("aesstr", aesStr);
        param.put("pricedata", priceData);
//		param.put("json", json);
        return doPost(param);
    }

    /**
     * 2018年1月3日
     * 囤货账号支付
     *
     * @param encryptStr
     * @return
     */
    public String stockAccountPay(String encryptStr) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0331");
        param.put("encryptStr", encryptStr);
        return doPost(param);
    }

    /**
     * 支付退货运费
     *
     * @param encryptStr
     * @return
     */
    public String payReturnCost(String encryptStr) {
        HashMap<String, String> map = new HashMap<>();
        map.put("apiid", "0332");
        map.put("encryptStr", encryptStr);
        return doPost(map);
    }

    /**
     * @param shopid 店铺id,作为推送标识
     * @param cType  互动类型，1、点菜，2、预订，3、店内服务
     * @param aesStr 信息加密字符串
     * @return
     * @throws Exception
     */
    public String postFoodServiceInfo(String shopid, int cType, String aesStr) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0117");
        param.put("alias", shopid);
        param.put("ctype", String.valueOf(cType));
        param.put("md5str", aesStr);
        return doPost(param);
    }

    public String getCashPaper(String shopid, String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0202");
        param.put("shopid", shopid);
        param.put("memberid", uid);
        return doPost(param);
    }

    public String getFavTickets(String guid, String uid, String orderMoney) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0294");
        param.put("productid", guid);
        param.put("memberid", uid);
        param.put("ordermoney", orderMoney);
        return doPost(param);
    }

    /**
     * 一键开店功能，获取用户自身的店铺名
     */
    public String getShopName(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0127");
        param.put("memberid", uid);
        return doPost(param);
    }

    /**
     * @param uid           用户登录id
     * @param ticketNumbers 代金券编号，多个用逗号分隔
     * @return 是否成功使用代金券
     * @throws Exception
     */
    public String useTickets(String uid, String ticketNumbers) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0208");
        param.put("memLoginID", uid);
        param.put("ticketNumber", ticketNumbers);
        return doPost(param);
    }

    public String getAlcoholRecord(String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0137");
        param.put("shopid", shopid);
        return doPost(param);
    }

    public String isQuanYan(String shopid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0126");
        param.put("shopid", shopid);
        return doPost(param);
    }

    public String getAlcoholData(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0140");
        param.put("mid", uid);
        return doPost(param);
    }

    /**
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
    public String postAddGoods(String shopid, String name, String image, double shopPrice, double marketPrice, int repertoryCount, int type, String pDetail, String productImageList) {
        name = name.replaceAll("\n|\r", "");
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0233");
        param.put("shopid", shopid);
        param.put("name", name);
        param.put("image", image);
        param.put("shopprice", String.valueOf(CommonTools.formatZero2Str(shopPrice)));
        param.put("marketprice", String.valueOf(CommonTools.formatZero2Str(marketPrice)));
        param.put("repertoryCount", String.valueOf(repertoryCount));
        param.put("type", String.valueOf(type));
        if (pDetail != null && productImageList != null) {
            param.put("pdetails", pDetail);
            param.put("productImglist", productImageList);
        }
        return doPost(param);
    }

    public String postEditGoods(String guid, String name, String image, double shopPrice, double marketPrice, int repertoryCount, String pDetail, String productImageList) {
        name = name.replaceAll("\n|\r", "");
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0236");
        param.put("guid", guid);
        param.put("name", name);
        param.put("image", image);
        param.put("shopprice", String.valueOf(CommonTools.formatZero2Str(shopPrice)));
        param.put("marketprice", String.valueOf(CommonTools.formatZero2Str(marketPrice)));
        param.put("repertoryCount", String.valueOf(repertoryCount));
        if (pDetail != null && productImageList != null) {
            param.put("pdetails", pDetail);
            param.put("productImglist", productImageList);
        }
        return doPost(param);
    }

    public String getPayAccountNum(String uid) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0150");
        param.put("memloginid", uid);
        return doPost(param);
    }

    /**
     * 0264-提交红包信息
     *
     * @param uid    用户账户
     * @param leadid 用户上级id，注册、分享时传空字符串，开店时传上级id
     * @param type   1-注册账号；2-开店；3-分享
     * @return true/false
     */
    public String postRedPacket(String uid, String leadid, int type) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0264");
        param.put("memberid", uid);
        param.put("leadid", leadid);
        param.put("type", String.valueOf(type));
        return doPost(param);
    }

    public String postCheckFavNum(String codeNumber, String guids) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0295");
        param.put("codenumber", codeNumber);
        param.put("productid", guids);
        return doPost(param);
    }

    public String postGetOrderHead(String orderNum) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0036");
        param.put("ordernumber", orderNum);
        return doPost(param);
    }
    public String postGetOrderBody(String orderNum) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0072");
        param.put("ordernumber", orderNum);
        return doPost(param);
    }

    /**
     * 获取订单剩余支付时间
     */
    public String postGetOrderPayTime(String orderNum) {
        HashMap<String, String> param = new HashMap<>();
        param.put("apiid", "0309");
        param.put("ordernumber", orderNum);
        return doPost(param);
    }

}
