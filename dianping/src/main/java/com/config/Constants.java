package com.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.danertu.dianping.WapActivity;

@SuppressWarnings("serial")
public class Constants {
    /**
     * 2017年10月12日添加说明
     * huangyeliang
     * 百度地图sdk更换，应用编号：10096377 访问AK：LTPGyqxo7G0kH1xRi7ro3xYOsXMFbDzw
     */
    /**
     * 2017年9月11日
     */
    //-------程序更新是需要更改的--------------------------------------
    /**
     * 是否开启日志输出，调试模式时异常捕捉交回给系统处理的
     */
    public final static boolean isDebug = true;//发版本请设置为 ---false---

    public final static boolean isLocal = true;
    /**
     * 是否为业务员版本
     */
    public final static boolean isSalesman = false;

    /**
     * -------------------------正式地址------------------------------------
     */
//	public static final String appWebPageUrl = "http://115.28.55.222:8018/";

//常用
//    public static final String apiSrcUrl = "http://115.28.55.222:8085/RequestApi.aspx";

//	public static final String apiSrcUrl = "http://115.28.55.222:88/RequestApi.aspx";//山西API地址

    //温泉首页地址
//    public static final String SPRING_URL ="http://www.danertu.com/mobile/qyyd/quanyan_index.htm";

    //------------------------------------------------------------------------------------------

    /**
     * -------------------------测试地址------------------------------------
     */
//    public static final String appWebPageUrl = "http://115.28.55.222:8019/";//外网测试地址
//常用
    public static final String appWebPageUrl = "http://192.168.1.137:778/";

//	public static final String appWebPageUrl = "http://192.168.1.253:8018/";
    /**
     * @see 2017/7/7
     */
    public static final String apiSrcUrl = "http://192.168.1.137:511/RequestApi.aspx";
    /**
     * https测试
     */
//    public static final String apiSrcUrl = "https://api.danertu.com/requestapi.aspx";

//	public static final String apiSrcUrl = "http://192.168.1.253:8085/RequestApi.aspx";

    //温泉首页地址
    public static final String SPRING_URL = "http://192.168.1.137:411/qyyd/quanyan_index.htm";
    //------------------------------------------------------------

    /**
     * @see
     * @notice: 现在的retrofit地址是截取apiSrcUrl使用
     * retrofit参数，发布版本时要注意修改为正式地址
     */
    /**
     * -------------------------正式地址------------------------------------
     */
//    public static final String BASE_API_URL="http://115.28.55.222:8085"+apiSrcUrl.substring(0,apiSrcUrl.lastIndexOf("/"));
    public static final String BASE_API_URL = apiSrcUrl.substring(0, apiSrcUrl.lastIndexOf("/"));

    /**
     * -------------------------测试地址------------------------------------
     */
//    public static final String BASE_API_URL="http://192.168.1.137:511";


    public static final String API_ADDRESS = "/RequestApi.aspx";


    /**
     * 分享大厅页面地址修改
     * 2017年11月28日
     */
    public static final String NEW_SHARE_HALL_ADDRESS = "http://www.danertu.com/mobile/sharehall/list.aspx?platform=android&issharehall=1";
    /**
     * 2018年2月2日
     * 单耳兔智慧仓库协议地址
     */
    public static final String DANERTU_STOCK_PROTOCOL = appWebPageUrl + "articlescrap/articlescrap_tunhuo_protocol.html?from=order";

    // appi
    // 请同时修改 androidmanifest.xml里面，.PaymentCenterActivity里的属性<data
    // android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
    public static final String APP_ID = "wx58f1866c456080f3";

    // 商户号
    public static final String MCH_ID = "1274110001";

    // API密钥，在商户平台设置
    public static final String API_KEY = "YTA3YWMzNWFkM2UwMjI5OTAyYzFiZmU4";

    public static final String QY_SUPPLIERID = "shopnum1";
    public final static String deviceType = "android";
    public final static String downloadDir = "danertu/";// 安装目录
    /**
     * 记录错误密码信息的文本
     */
    public final static String SP_PSW_WRONG_RECORD = "psw_wrong_record";

    public final static String SP_SHOPCAR = "shopCar";
    public static final String BASE_PACKAGE = "com.danertu.dianping.";

    /**
     * 经验证手机号，用于活动，优先级最高
     */
    public static String testedMobile = null;
    /**
     * 记录是否参加了0.1元购参数
     */
    public static boolean isRecorded01 = false;
    public final static String price01 = "0.1";
    public final static String proName01 = "晓镇香 5陈酿125ml";
    public final static String agentID01 = "";
    /**
     * 0.1元活动商品guid
     */
    public final static String guid01 = "6ccaa7c9-d363-45bc-bcac-4f06debb5426";

    public final static String apkDownloadAddress = "http://www.danertu.com/download/danertu.apk";

    public final static String CK_SHOPID = "15017339307";
    public final static String CK_PHONE = "4009952220";

    public static final int READ_PHONE_CODE = 11;
    public static final int RECORD_CODE = 12;
    public static final int READ_STORAGE_CODE = 13;
    public static final int WRITE_STORAGE_CODE = 14;

    /**
     * 清除活动的临时变量数据
     */
    public static void clearActData() {
        isRecorded01 = false;
        testedMobile = null;
        WapActivity.is1YFQ = false;
    }

    public static String getLa() {
        return la;
    }

    public static void setLa(String la) {
        Constants.la = la;
    }

    public static String getLt() {
        return lt;
    }

    public static void setLt(String lt) {
        Constants.lt = lt;
    }

    //当前纬度
    private static String la;
    //当前经度
    private static String lt;

    private static String cityName;
    //当前城市名
    private static String dcityName;
    /**
     * 2017年10月16日
     *
     * @since 80版本添加
     * 当前所在位置的省
     */
    private static String currentProvince;

    public static String getDcityName() {
        return dcityName;
    }

    public static void setDcityName(String dcityName) {
        Constants.dcityName = dcityName;
    }

    public static String getCityName() {
        return cityName;
    }

    public static void setCityName(String cityName) {
        Constants.cityName = cityName;
    }

    public static String getCurrentProvince() {
        return currentProvince;
    }

    public static void setCurrentProvince(String currentProvince) {
        Constants.currentProvince = currentProvince;
    }

    public static int MaxDateNum = 100000; // 设置最大数据条数
    public static int pageSize = 10; // 分页每页显示数

    //	public static String imgServer = "http://115.28.77.246/"; // 图片服务器
    public final static String imgServer = "http://img.danertu.com/"; // 图片服务器

    public static String cityText = "";
    // 店铺推广地址
    public final static String shareUmlDo = "http://115.28.55.222:8085/doShare.aspx?type=s&shopid=";
    public final static String IOSshareUmlDo = "http://115.28.55.222:8085/indexios.aspx?type=s&shopid=";
    // 个人分享地址
    public static String sharepUmlDo = "http://115.28.55.222:8085/doShare.aspx?type=p&shopid=";
    public static String IOSsharepUmlDo = "http://115.28.55.222:8085/indexios.aspx?type=p&shopid=";

    /**
     * webview与js交互的接口名
     */
    public final static String IFACE = "javascript:";

    public static BDLocation location;

    public static ArrayList<HashMap<String, Object>> data = null;
    public static ArrayList<HashMap<String, Object>> supperproduct = null;

    public static String pId = "";
    public static String seller = "";
    public static String privateCode = "";
    public static String publicCode = "";

    public static String APP_NAME = "";

    public static final String IMAGE_URL = "http://58.211.5.34:8080/studioms/staticmedia/IMAGES/#";

    public static final String VIDEO_URL = "http://58.211.5.34:8080/studioms/staticmedia/video/#";

    public static final String SHARED_PREFERENCE_NAME = "itau_jingdong_prefs";

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String BASE_PATH = SD_PATH + "/iTau/jingdong/";

    public static final String BASE_IMAGE_CACHE = BASE_PATH + "CACHE/IMAGES/";

    public static String IMEI = "";

    public static String TEL = "";

    public static final int SHARE_SUCCESS = 0X1000;

    public static final int SHARE_CANCEL = 0X2000;

    public static final int SHARE_ERROR = 0X3000;

    public static final int EXECUTE_LOADING = 0X4000;

    public static final int EXECUTE_SUCCESS = 0X5000;

    public static final int EXECUTE_FAILED = 0X6000;

    public static final int LOAD_DATA_SUCCESS = 0X7000;

    public static final int LOAD_DATA_ERROR = 0X8000;

    public static final int SET_DATA = 0X9000;


    @SuppressLint("SdCardPath")
    public static final class dir {
        public static final String BASE = "/sdcard/danertu";
        public static final String CACHE = BASE + "/CACHE";
        public static final String IMAGES = BASE + "/images";
    }

    public static final class api {
        //        public static final String BASE = "http://115.28.55.222:8085/RequestApi.aspx";
        public static final String BASE = apiSrcUrl;
        public static final int GET_MOBILE_MESSAGE = 7;    //获取公告消息
        public static final int POST_LOGIN_INFO = 9;    //post取用户输入的登录信息判断登录
        public static final int POST_INFO = 11;   // Post方式提交注册信息
        public static final int POST_USER_ISEXIST = 29;  // post取用户输入的登录信息判断登录名是否存在
        public static final int GET_USER_ADRESS = 30;   // post取用户收货地址
        public static final int POST_GET_PRODUCTLIST = 40;   // post取商品列表
        public static final int POST_RESET_PASSWORD = 48;   //post重置密码
        public static final int GET_FIRST_CATEGORY = 73;   // 获取一级分类
        public static final int POST_SEND_VERITY_CODE = 77;   //发送手机验证码
        public static final int POST_CHECK_VERITY_CODE = 78;    //验证手机验证码
        public static final int GET_UNION_TN = 89;    //获取银联支付TN
    }

    public static final class err {
        public static final String NETWORK = "网络错误";
        public static final String MESSAGE = "消息错误";
        public static final String JSON_FORMAT = "消息格式错误";
    }

    /**
     * 泉眼客房guid
     */
    public final static List<String> list_guestroom = new ArrayList<String>() {
        {
            add("1d22fec3-c2c7-467b-abbe-0432b252e5a3");
            add("b33144b9-e809-4894-9d31-081f00f43a00");
            add("84d4c18b-79b9-41ed-9424-0fca6839beeb");
            add("67269b0a-5265-4ee5-b991-317414191c28");
            add("04173c8e-b7bd-413c-a963-41cce7e7faee");
            add("73345f23-d35f-4f9f-a9c7-427500657957");
            add("233c3bf7-c42d-47c5-a7b3-57b9139cbe7b");
            add("452961bd-fa6e-47c6-b77c-588af83a9c73");
            add("3b8d53c9-ec37-4411-ab5e-63ba5be29c4c");
            add("6bdee8c4-2b2d-4052-9c18-91918b279a06");
            add("3a8071e8-c710-4199-99b1-9a0fa5894154");
            add("fe9288cd-0582-4901-b59f-be44a9a83edc");
            add("98fd0179-647d-4419-ba2c-c47b488b484a");
            add("815d0755-9a51-450d-a8f3-c551ae7c18dd");
            add("d8162986-e0b6-4a12-ba08-c8e657c2ac9a");
            add("b8cdd0f8-f26a-4219-9997-cc3fa397d76b");
            add("eb04ed47-3083-4eee-aa9c-cdfc6e78098e");
            add("495c5375-0e98-4537-928e-e18ac3e9b38d");
            add("20d7d9ad-4913-4d34-a61e-f757220ad1d8");
        }
    };
    /**
     * 泉眼门票guid
     */
    public final static List<String> list_ticket = new ArrayList<String>() {
        {
            add("4713442b-505a-47a0-b490-09a6b2c6c662");
            add("2ad4ee2c-bb98-44ba-b5a5-0dc152d7a0e8");
            add("9724ab16-2a50-452d-9c0e-423f0aeaaf26");
            add("a6852148-4805-4c3c-80fb-829f69f5d23e");
            add("21ddf0e1-3b22-45b8-9c71-92084b523750");
            add("21ddf0e1-3b22-45b8-9c71-92084b523750");
            add("0d488462-12ce-4009-a62f-431b472e4897");
            add("b6bf8372-d9b4-4c5c-9357-ffd5f88f10e7");
            add("14753f29-5cb7-4908-a6ab-6635b17a0d3b");
            add("ead1ed14-418e-4211-9469-6179d4e41730");
            add("46e36622-5da2-4ad7-b80f-05f48706c52d");
            add("2552f365-3fcc-4f47-95ec-72ab4f03789b");
            add("57914ef9-7be3-4ef1-a7b0-7cfb709baa63");
            add("b113c289-3c31-48d3-8a08-711d67d4c1fb");
            add("945e0e25-1bdb-4451-b733-70efba101fd4");

            //2018年3月20日添加
            //中式按摩温泉套餐
            //门票处理，需要核销
            add("c2701786-d83e-4237-a677-96e47277fc7c");
            add("5c42aca4-5a19-41da-9c39-867c22df13f6");
            add("b20d81c3-50ee-422e-b95b-1a0b25a3cf36");
        }
    };


    public final static ArrayList<String> ACT_FILL_STATUSBAR = new ArrayList<String>() {
        {
            add("IndexActivity");
            add("ProductDetailsActivity2");
            add("SplashActivity");
            add("AppShowActivity");
            add("MyOrderDetail");
//            add("HtmlActivity");
//            add("HtmlActivityNew");
        }
    };
    public final static String LOGIN_SUCCESS_BROADCAST = "com.danertu.dianping.LOGIN";
    public final static String LOGOUT_SUCCESS_BROADCAST = "com.danertu.dianping.LOGOUT";
    public final static String REFRESH_INDEX = "com.danertu.dianping.REFRESH";
    public final static String ORDER_FINISH = "com.danertu.dianping.ORDER_FINISH";
    public final static String ORDER_DATA_CHANGE = "com.danertu.dianping.ORDER_DATA_CHANGE";
    public final static String ORDER_DATA_ON_ACTIVITY_FOR_RESULT = "com.danertu.dianping.ORDER_DATA_ON_ACTIVITY_FOR_RESULT";
    public final static String ORDER_DATA_ON_ACTIVITY_FOR_RESULT_QRCODE = "com.danertu.dianping.ORDER_DATA_ON_ACTIVITY_FOR_RESULT_QRCODE";
    public final static String GET_LOCATION_FINISH = "com.danertu.dianping.GET_LOCATION_FINISH";

    public final static String PAY_UTILS_RESULT = "com.danertu.dianping.PAY_UTILS_RESULT";
}
