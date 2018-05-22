package com.danertu.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.config.Constants;
import com.danertu.adapter.MyOrderAdapter;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderActivity;
import com.danertu.dianping.MyOrderListAllActivity;
import com.danertu.dianping.MyOrderNoCommentActivity;
import com.danertu.dianping.MyOrderNoPayActivity;
import com.danertu.dianping.MyOrderNoReceiveActivity;
import com.danertu.dianping.MyOrderNoSendActivity;
import com.danertu.dianping.PayBackActivity;
import com.danertu.dianping.ProductCommentActivity;
import com.danertu.tools.AESEncrypt;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.tools.PayUtils;
import com.danertu.widget.CommonTools;

/**
 * 提供订单数据及部分订单操作的类
 *
 * @author dengweilin
 */
public abstract class MyOrderData {
    public static final String ORDER_ORDERNUMBER_KEY = "order_orderNumber";
    public static final String ORDER_ORDERSTATUS_KEY = "order_OderStatus";
    public static final String ORDER_SHIPSTATUS_KEY = "order_ShipmentStatus";
    public static final String ORDER_PAYSTATUS_KEY = "order_PaymentStatus";
    public static final String ORDER_DISPATMODENAME_KEY = "order_DispatchModeName";
    public static final String ORDER_LOGISTCODE_KEY = "order_LogisticsCompanyCode";
    public static final String ORDER_AGENTID_KEY = "order_AgentId";
    public static final String ORDER_SHIPNUMBER_KEY = "order_ShipmentNumber";
    public static final String ORDER_SHOULDPAY_KEY = "order_ShouldPayPrice";
    public static final String ORDER_CREATETIME_KEY = "order_CreateTime";
    public static final String ORDER_PAYMENTNAME_KEY = "order_PaymentName";
    public static final String ORDER_YUNFEI_KEY = "order_yunfei";
    public static final String HEAD_P_NAME = "Name";
    public static final String HEAD_P_ADDRESS = "Address";
    public static final String HEAD_P_MOBILE = "Mobile";
    public static final String ORDER_TYPE_KEY = "orderType";


    public static final String ORDER_DISPATCH_TIME = "DispatchTime";//发货时间，主要用于已使用过的门票、客房在券码页面显示

    public static final String ORDER_ITEMSET_KEY = "order_itemSet";
    public static final String ORDER_ITEM_SHOPNAME_KEY = "ShopName";
    public static final String ORDER_ITEM_GUID_KEY = "Guid";
    public static final String ORDER_ITEM_JOINCOUNT_KEY = "joinCount";
    public static final String ORDER_ITEM_MARKEPRICE_KEY = "MarketPrice";
    public static final String ORDER_ITEM_NAME_KEY = "Name";
    public static final String ORDER_ITEM_CREATEUSER = "CreateUser";
    public static final String ORDER_ITEM_BUYNUMBER_KEY = "BuyNumber";
    public static final String ORDER_ITEM_AGENTID_KEY = "AgentID";
    public static final String ORDER_ITEM_SUPPLIERID_KEY = "SupplierLoginID";
    public static final String ORDER_ITEM_PRICE_KEY = "ShopPrice";
    public static final String ORDER_ITEM_IMAGE_KEY = "SmallImage";
    public static final String ORDER_ITEM_MOBILE_KEY = "mobile";
    public static final String ORDER_ITEM_ARRIVE_TIME = "other1";
    public static final String ORDER_ITEM_LEAVE_TIME = "other2";
    public static final String ORDER_ITEM_ATTRIBUTE = "attribute";
    public static final String ORDER_ITEM_DISCOUNT_BUY_NUM = "DiscountBuyNum";//后台拿货优惠条件
    public static final String ORDER_ITEM_DISCOUNT_PRICE = "DiscountPrice";//后台拿货优惠幅度


    public static ArrayList<HashMap<String, Object>> order_list_all = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_noPay = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_noSend = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_noReceive = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_noComment = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_complete = new ArrayList<>();

    /**
     * 分页数据源
     */
    public static ArrayList<HashMap<String, Object>> order_all = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_noPay = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_noSend = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_noReceive = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_noComment = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_complete = new ArrayList<>();

    public static int TAB_INDEX;
    public static boolean isFinish = false;
    public static final String tAdentID = "chunkang";
    public static final int LIST_INIT_SIZE = 30;
    public static String tOrderNumber = null;

    private Thread getOrderHeadThread;
    private Thread getOrderHeadAllThread;

    private BaseActivity base;
    private Handler handler;
    private String uid = "";
    private AppManager appManager;
    private GetOrderHead getOrderHead;
    private GetOrderHeadAll getOrderHeadAll;
    private Context context;
    /**
     * 是否只展示酒店
     */
    private boolean isOnlyHotel = false;

    /**
     * 2018年4月26日
     * 是否只展示泉眼商品
     * 用于个人中心消费码功能
     */
    private boolean isOnlyQuanYan = false;

    public static String TAG = "MyOrderData";

    public void setLoadListener(LoadDataListener loadListener) {
        this.loadListener = loadListener;
    }

    private static LoadDataListener loadListener;

    public MyOrderData(BaseActivity context) {
        this(context, null);
        this.context = context;
    }

    private final int GET_DATA_SUCCESS = 666;
    private final int ADD_DATA_ALL = 667;
    private final int ADD_DATA_NO_PAY = 668;
    private final int ADD_DATA_NO_SEND = 669;
    private final int ADD_DATA_NO_RECEIVE = 670;
    private final int ADD_DATA_NO_COMMENT = 671;
    private final int LOAD_DATA = 672;
    private final int LOAD_DATA_FINISH = 673;
    private Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            HashMap<String, Object> item = (HashMap<String, Object>) msg.obj;
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    getDataSuccess();
                    break;
                case ADD_DATA_ALL:
                    if (TAB_INDEX == 0 && order_list_all.size() < LIST_INIT_SIZE) {
                        MyOrderListAllActivity.adapter.addDateItem(item);
                    } else {
                        order_all.add(item);
                    }
                    break;
                case ADD_DATA_NO_PAY:
                    if (TAB_INDEX == 1 && order_list_noPay.size() < LIST_INIT_SIZE) {
                        MyOrderNoPayActivity.adapter.addDateItem(item);
                    } else {
                        order_noPay.add(item);
                    }
                    break;
                case ADD_DATA_NO_SEND:
                    if (TAB_INDEX == 2 && order_list_noSend.size() < LIST_INIT_SIZE) {
                        MyOrderNoSendActivity.adapter.addDateItem(item);
                    } else {
                        order_noSend.add(item);
                    }
                    break;
                case ADD_DATA_NO_RECEIVE:
                    if (TAB_INDEX == 3 && order_list_noReceive.size() < LIST_INIT_SIZE) {
                        MyOrderNoReceiveActivity.adapter.addDateItem(item);
                    } else {
                        order_noReceive.add(item);
                    }
                    break;
                case ADD_DATA_NO_COMMENT:
                    if (TAB_INDEX == 4 && order_list_noComment.size() < LIST_INIT_SIZE) {
                        MyOrderNoCommentActivity.adapter.addDateItem(item);
                    } else {
                        order_noComment.add(item);
                    }
                    break;
                case LOAD_DATA:
                    if (loadListener != null) {
                        loadListener.hideLoad();
                    }
                    break;
                case LOAD_DATA_FINISH:
                    if (loadListener != null) {
                        loadListener.loadFinish();
                    }
                    break;
            }
            return true;
        }
    };


    public MyOrderData(BaseActivity context, String orderNum) {
        this(context, orderNum, false);
    }

    /**
     * 2018年3月23日
     * 增加noClearAddData参数，用于标识是否清空所有列表数据
     * 为false时表明保留列表数据，用于在进入券码页面后返回订单详情时更新数据不引起app闪退
     *
     * @param context
     * @param noClearAllData
     * @param orderNum
     */
    public MyOrderData(BaseActivity context, boolean noClearAllData, String orderNum) {
        this(context, orderNum, false, noClearAllData, false);
    }


    public MyOrderData(BaseActivity context, boolean isOnlyHotel) {
        this(context, null, isOnlyHotel);
    }

    public MyOrderData(BaseActivity context, boolean isOnlyHotel, boolean isOnlyQuanYan) {
        this(context, null, isOnlyHotel, isOnlyQuanYan);
    }


    public MyOrderData(BaseActivity context, String orderNum, boolean isOnlyHotel) {
        this(context, orderNum, isOnlyHotel, false, false);
    }

    public MyOrderData(BaseActivity context, String orderNum, boolean isOnlyHotel, boolean isOnlyQuanYan) {
        this(context, orderNum, isOnlyHotel, false, isOnlyQuanYan);
    }

    public MyOrderData(BaseActivity context, String orderNum, boolean isOnlyHotel, boolean noClearAllData, boolean isOnlyQuanYan) {
        this.isOnlyHotel = isOnlyHotel;
        this.isOnlyQuanYan = isOnlyQuanYan;
        uid = context.getUid();
        isFinish = false;
        appManager = AppManager.getInstance();
        this.base = context;
        if (handler == null)
            handler = new Handler(callback);
        if (!noClearAllData)
            clearData();
        if (TextUtils.isEmpty(orderNum)) {
            getOrderHeadAllThread = new Thread(new GetOrderHeadAll());
            getOrderHeadAllThread.start();
        } else {
            getOrderHeadThread = new Thread(new GetOrderHead(orderNum));
            getOrderHeadThread.start();
        }
        //一个值代表一个分类得数据是否发生变化   0--全部  1--未付款  2--已付款.......
        MyOrderActivity.dataChanges = new Boolean[5];
        for (int i = 0; i < MyOrderActivity.dataChanges.length; i++) {
            MyOrderActivity.dataChanges[i] = false;
        }
    }


    public static void toPayBackActivity(Context context, String orderNumber, String uid, String price) {
        Intent intent = new Intent(context, PayBackActivity.class);
        intent.putExtra("ordernumber", orderNumber);
        intent.putExtra("memberid", uid);
        intent.putExtra("price", price);
        context.startActivity(intent);
    }

    public static void toPayBackActivityForResult(Activity context, String orderNumber, String uid, String price, int position, int requestCode) {
        Intent intent = new Intent(context, PayBackActivity.class);
        intent.putExtra("ordernumber", orderNumber);
        intent.putExtra("memberid", uid);
        intent.putExtra("price", price);
        intent.putExtra("position", position);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startToProDetail(Context context, String guid, String proName, String img, String detail, String agentID, String supplierID, String price, String mobile) {
        try {
            double tprice = Double.parseDouble(price);
            if (tprice == 1 || tprice == 0.1)
                ActivityUtils.toProDetail2(context, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 0);
            else
                ActivityUtils.toProDetail2(context, guid, proName, img, detail, agentID, supplierID, price, "0", mobile, "0", 2);
        } catch (Exception e) {
            Log.e("订单中心数据出错", price + "");
        }
    }

    private HashMap<String, Object> itemOrder;


    /**
     * 取单个订单详情
     * 取订单头信息
     */
    private class GetOrderHead implements Runnable {
        private String orderNum;

        public GetOrderHead(String orderNum) {
            this.orderNum = orderNum;
        }

        public void run() {
            String result = appManager.postGetOrderHead(orderNum);
            try {
                JSONObject oj = null;
                if (!TextUtils.isEmpty(result))
                    oj = new JSONObject(result).getJSONObject("orderinfolist").getJSONArray("orderinfobean").getJSONObject(0);
                initOrderBean(oj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(GET_DATA_SUCCESS);
        }

        private void initOrderBean(JSONObject oj) throws JSONException {
            String orderNumber = oj.getString("OrderNumber");
            String orderStatus = oj.getString("OderStatus");
            String ShipmentStatus = oj.getString("ShipmentStatus");
            String PaymentStatus = oj.getString("PaymentStatus");
            String DispatchModeName = oj.getString("DispatchModeName");
            //
            String DispatchTime = oj.get(ORDER_DISPATCH_TIME) == null ? "" : oj.get(ORDER_DISPATCH_TIME).toString();

            String LogisticsCompanyCode = "";
            String AgentId = "";
            String ShipmentNumber = oj.getString("ShipmentNumber");
            String ShouldPayPrice = oj.getString("ShouldPayPrice");
            String CreateTime = oj.getString("CreateTime");
            String PaymentName = oj.getString("PaymentName");
            String yunfei = "0.00";
            String name = oj.getString(HEAD_P_NAME);
            String address = oj.getString(HEAD_P_ADDRESS);
            String mobile = oj.getString(HEAD_P_MOBILE);
            String orderType = oj.getString(ORDER_TYPE_KEY);

            ArrayList<HashMap<String, String>> orderItemSet = analyzeOrderInfo(orderNumber);
            itemOrder = getOrder(
                    orderItemSet,
                    orderNumber,
                    orderStatus,
                    ShipmentStatus,
                    PaymentStatus,
                    DispatchModeName,
                    DispatchTime,
                    LogisticsCompanyCode,
                    AgentId,
                    ShipmentNumber,
                    ShouldPayPrice,
                    CreateTime,
                    PaymentName,
                    yunfei,
                    name,
                    address,
                    mobile,
                    orderType
            );
        }
    }

    /**
     * 注意：这是全部订单头信息，通过实例此构造方法-MyOrderData(Context context, String orderNum)
     */
    public HashMap<String, Object> getItemOrder() {
        return itemOrder;
    }

    private class GetOrderHeadAll implements Runnable {
        public GetOrderHeadAll() {
//            initList();
        }

        public void run() {
            // 耗时操作
            long ts = System.currentTimeMillis();
            String result = appManager.postGetUserOrderHead("0033", uid, "2");

            handler.sendEmptyMessage(GET_DATA_SUCCESS);
            long te = System.currentTimeMillis();
            Logger.i("获取头信息用时：", (te - ts) / 1000 + "");
            try {
                //开始解析数据
                analyzeJsonString(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            handler.sendEmptyMessage(GET_DATA_SUCCESS);
        }
    }


    /**
     * 数据加载完成后调用的方法
     */
    public abstract void getDataSuccess();

    /**
     * 打开商品评论界面
     *
     * @param context
     * @param orderNumber
     * @param guid
     * @param shopid
     */
    public static void startProductCommentAct(Context context, String orderNumber, String guid, String shopid) {
        // productGuid = getIntent().getExtras().getString("proGuid");
        // shopID = getIntent().getExtras().getString("shopid");
        MyOrderData.tOrderNumber = orderNumber;
        Intent intent = new Intent(context, ProductCommentActivity.class);
        intent.putExtra("proGuid", guid);
        intent.putExtra("shopid", shopid);
        context.startActivity(intent);
    }

    private JSONArray orderbeans;

    /**
     * 清除列表数据
     */
    public void clearData() {
        order_list_all.clear();
        order_list_noPay.clear();
        order_list_noSend.clear();
        order_list_noReceive.clear();
        order_list_noComment.clear();
        order_list_complete.clear();

        order_all.clear();
        order_noPay.clear();
        order_noSend.clear();
        order_noReceive.clear();
        order_noComment.clear();
        order_complete.clear();
    }

    public void stopThread() {
        if (getOrderHeadAllThread != null && getOrderHeadAllThread.getState() == Thread.State.RUNNABLE) {
            getOrderHeadAllThread.interrupt();
        }
        if (getOrderHeadThread != null && getOrderHeadThread.getState() == Thread.State.RUNNABLE) {
            getOrderHeadThread.interrupt();
        }
    }

    /**
     * 解析jsonStr数据
     *
     * @param jsonStr json字符串
     * @throws JSONException
     * @author dengweilin
     */
    public void analyzeJsonString(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr).getJSONObject("orderlist");
        orderbeans = jsonObject.getJSONArray("orderbean");
        initOrderBean(orderbeans, 0, orderbeans.length());
        isFinish = true;
        sendMSG(LOAD_DATA_FINISH, null);
    }

    SharedPreferences sp;
    AESEncrypt aes;

    public void initOrderBean(JSONArray jsonArray, int start, int end) throws JSONException {
        if (start >= end || jsonArray == null) {
            sendMSG(LOAD_DATA, null);
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(LOAD_DATA);
            }
        }, 1500);

        for (int i = start; i < end; i++) {
            //HEAD
            JSONObject oj = jsonArray.getJSONObject(i);
            String orderNumber = oj.getString("OrderNumber");
            String orderStatus = oj.getString("OderStatus");
            String ShipmentStatus = oj.getString("ShipmentStatus");
            String PaymentStatus = oj.getString("PaymentStatus");
            String DispatchModeName = oj.getString("DispatchModeName");

            String DispatchTime = oj.get(ORDER_DISPATCH_TIME) == null ? "" : oj.get(ORDER_DISPATCH_TIME).toString();

            String LogisticsCompanyCode = oj.getString("LogisticsCompanyCode");
            String AgentId = oj.getString("AgentId");
            String ShipmentNumber = oj.getString("ShipmentNumber");
            String ShouldPayPrice = oj.getString("ShouldPayPrice");
            String CreateTime = oj.getString("CreateTime");
            String PaymentName = oj.getString("PaymentName");
            String yunfei = oj.getString("DispatchPrice");
            String name = oj.getString(HEAD_P_NAME);
            String address = oj.getString(HEAD_P_ADDRESS);
            String mobile = oj.getString(HEAD_P_MOBILE);
            String orderType = oj.getString(ORDER_TYPE_KEY);
            if (isOnlyHotel && (TextUtils.isEmpty(orderType) || !orderType.equals("1"))) {
                continue;
            }

            ArrayList<HashMap<String, String>> orderItemSet = analyzeOrderInfo(orderNumber);

            if (isOnlyQuanYan && !isQuanYan) {
                continue;
            }

            HashMap<String, Object> item = getOrder(
                    orderItemSet,
                    orderNumber,
                    orderStatus,
                    ShipmentStatus,
                    PaymentStatus,
                    DispatchModeName,
                    DispatchTime,
                    LogisticsCompanyCode,
                    AgentId,
                    ShipmentNumber,
                    ShouldPayPrice,
                    CreateTime,
                    PaymentName,
                    yunfei,
                    name,
                    address,
                    mobile,
                    orderType
            );
            sendMSG(ADD_DATA_ALL, item);
            // orderStatus+" , "+ShipmentStatus+" , "+PaymentStatus+" , "+ShouldPayPrice+" , "+PaymentName);
            if (orderStatus.equals("1")) {
                if (PaymentStatus.equals("0")) {// 付款状态为 未付款
                    sendMSG(ADD_DATA_NO_PAY, item);
                } else if (PaymentStatus.equals("2") && ShipmentStatus.equals("0")) {// 已付款 ，未发货
                    sendMSG(ADD_DATA_NO_SEND, item);
                } else if (ShipmentStatus.equals("1") && PaymentStatus.equals("2")) {// 已发货,买家待收货
                    sendMSG(ADD_DATA_NO_RECEIVE, item);
                }
            } else if (PaymentStatus.equals("3")) {// 已退款
                sendMSG(ADD_DATA_NO_COMMENT, item);
            }
        }
    }

    private void sendMSG(int what, HashMap<String, Object> obj) {
        Message message = handler.obtainMessage();
        message.what = what;
        if (obj != null) {
            message.obj = obj;
        }
        handler.sendMessage(message);
    }

    private HashMap<String, Object> getOrder(
            ArrayList<HashMap<String, String>> orderItemSet,
            String orderNumber,
            String orderStatus,
            String ShipmentStatus,
            String PaymentStatus,
            String DispatchModeName,
            String DispatchTime,
            String LogisticsCompanyCode,
            String AgentId,
            String ShipmentNumber,
            String ShouldPayPrice,
            String CreateTime,
            String PaymentName,
            String yunfei,
            String name,
            String address,
            String mobile,
            String orderType) {
        HashMap<String, Object> item = new HashMap<>();
        item.put(ORDER_ITEMSET_KEY, orderItemSet);
        item.put("order_orderNumber", orderNumber);
        item.put("order_OderStatus", orderStatus);
        item.put("order_ShipmentStatus", ShipmentStatus);
        item.put("order_PaymentStatus", PaymentStatus);
        item.put("order_DispatchModeName", DispatchModeName);
        item.put(ORDER_DISPATCH_TIME, DispatchTime);
        item.put("order_LogisticsCompanyCode", LogisticsCompanyCode);
        item.put("order_AgentId", AgentId);
        item.put("order_ShipmentNumber", ShipmentNumber);
        item.put("order_ShouldPayPrice", ShouldPayPrice);
        item.put("order_CreateTime", CreateTime);
        item.put("order_PaymentName", PaymentName);
        item.put("order_yunfei", yunfei);
        item.put(HEAD_P_NAME, name);
        item.put(HEAD_P_ADDRESS, address);
        item.put(HEAD_P_MOBILE, mobile);
        item.put(ORDER_TYPE_KEY, orderType);

        return item;
    }

    /**
     * 获取除去赠送数量的真实数量
     *
     * @param count      单项商品总数
     * @param joinString 买几赠一 的几
     * @return 除去赠送数量的真实数量
     */
    public static int getRealCount(int count, String joinString) {
        int joinCount = 0;
        int songCount = 0;
        if (!joinString.equals(""))
            joinCount = Integer.parseInt(joinString);
        if (joinCount > 0) {// 表示买 joinCount 赠 1
            songCount = count / (joinCount + 1);
        }
        count -= songCount;
        return count;
    }

    private boolean isQuanYan = false;

    /**
     * 解析订单商品的数据
     * 检查本地缓存，有当前用户保存的订单部分信息则读取缓存，不联网获取
     * <p>
     * 2018年3月9日
     * 黄业良
     * app做了部分本地缓存，把0072接口返回的信息保存在本地，造成了泉眼核销修改了订单数量后数据显示不正常问题
     * 所以注释了从本地缓存读取的代码
     *
     * @return 商品集
     */
    // joininfo =
    // AppManager.getInstance().getPJoinedInfo("0082",list.get(index).get("productID").toString());
    @SuppressLint("CommitPrefEdits")
    private ArrayList<HashMap<String, String>> analyzeOrderInfo(String orderNumber) {
        isQuanYan = false;
//        if (sp == null)
//            sp = base.getSharedPreferences(uid + ".danertu", Context.MODE_PRIVATE);
//        Editor editor = sp.edit();
//        String jsonOrderInfo = getString(sp, orderNumber);
//        boolean isEmpty = TextUtils.isEmpty(jsonOrderInfo);
//        if (!isEmpty) {
//            try {// 判断是否为json格式
//                new JSONObject(jsonOrderInfo);
//            } catch (JSONException e) {
//                e.printStackTrace();
//                isEmpty = true;
//            }
//        }
//        /**
//         *
//         * 查询所有订单详情
//         */
//        if (isEmpty) {
//            jsonOrderInfo = AppManager.getInstance().postGetOrderInfoShow("0072", orderNumber);
//        }
        String jsonOrderInfo = AppManager.getInstance().postGetOrderInfoShow("0072", orderNumber);
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(jsonOrderInfo).getJSONObject("orderproductlist");
            JSONArray orderproductbean = jsonObj.getJSONArray("orderproductbean");
            for (int i = 0; i < orderproductbean.length(); i++) {
                HashMap<String, String> item = new HashMap<>();
                JSONObject oj = orderproductbean.getJSONObject(i);
                String supplierLoginID = oj.getString("SupplierLoginID");
                isQuanYan = supplierLoginID.equals(Constants.QY_SUPPLIERID);

                if (isOnlyQuanYan && !isQuanYan) {
                    continue;
                }

                String shopName = oj.getString(ORDER_ITEM_SHOPNAME_KEY);
                String guid = oj.getString("Guid");
                String joinCount = oj.getString("iSGive");// 返回为几就表示买几赠 1
                String marketPrice = oj.getString("MarketPrice");//市场价/原价
                String Name = oj.getString("Name");
                String CreateUser = oj.getString("CreateUser");
                String BuyNumber = oj.getString("BuyNumber");
                String agentID = oj.getString("AgentID");
                String ShopPrice = oj.getString("ShopPrice");//售价/拿货价
                String SmallImage = oj.getString("SmallImage");
                String mobile = oj.getString("tel");
                String arriveTime = oj.getString(ORDER_ITEM_ARRIVE_TIME);
                String leaveTime = oj.getString(ORDER_ITEM_LEAVE_TIME);
                String attrs = oj.getString(ORDER_ITEM_ATTRIBUTE);


                /**
                 * 2017年7月18日
                 * huangyeliang
                 * 此段数据是后台拿货才有的值
                 * 是后台拿货时
                 *  "ProductRank":[
                 *                  {
                 *                      "DiscountButNum":"10",
                 *                      "DiscountPrice":"100.00"
                 *                  }
                 *                ]
                 *   非后台拿货时
                 *  "ProductRank":[
                 *                  {
                 *                  }
                 *                  ]
                 *
                 *
                 */
                String productRank = TextUtils.isEmpty(oj.getString("ProductRank")) ? "" : oj.getString("ProductRank");
                String discountBuyNum = "";
                String discountPrice = "";
                JSONArray jsonArray = new JSONArray(productRank);
                if (!jsonArray.isNull(0)) {
                    JSONObject array = jsonArray.getJSONObject(0);
                    discountBuyNum = array.getString(ORDER_ITEM_DISCOUNT_BUY_NUM);
                    discountPrice = array.getString(ORDER_ITEM_DISCOUNT_PRICE);
                }
                // Log.i("商品内容"+i, shopName+" , "+Name+" , "+SmallImage);

                item.put("ShopName", shopName);
                item.put("Guid", guid);
                item.put("joinCount", joinCount);
                item.put("MarketPrice", marketPrice);
                item.put("Name", Name);
                item.put("CreateUser", CreateUser);
                item.put("BuyNumber", BuyNumber);
                item.put("AgentID", agentID);
                item.put("SupplierLoginID", supplierLoginID);
                item.put("ShopPrice", ShopPrice);
                item.put("SmallImage", SmallImage);
                item.put("mobile", mobile);
                item.put(ORDER_ITEM_ARRIVE_TIME, arriveTime);
                item.put(ORDER_ITEM_LEAVE_TIME, leaveTime);
                item.put(ORDER_ITEM_ATTRIBUTE, attrs);

                item.put(ORDER_ITEM_DISCOUNT_BUY_NUM, discountBuyNum);
                item.put(ORDER_ITEM_DISCOUNT_PRICE, discountPrice);

                data.add(item);
            }
            //写入加密数据到缓存
//            commitMsg(editor, orderNumber, jsonOrderInfo);
        } catch (Exception e) {
            Logger.e("test", "MyOrderData analyzeOrderInfo 错误信息》" + e.toString());
//            jsonOrderInfo = AppManager.getInstance().postGetOrderInfoShow("0072", orderNumber);
//            commitMsg(editor, orderNumber, jsonOrderInfo);
            analyzeOrderInfo(orderNumber);
        }
        return data;
    }

    /**
     * 写入加密数据到缓存
     */
    public void commitMsg(Editor editor, String key, String values) {
        try {
            if (aes == null) {
                aes = new AESEncrypt();
                aes.setAlgorithKey();
            }
            String enText = aes.encrypt(values);
            editor.putString(key, enText);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(SharedPreferences sp, String key) {
        try {
            if (aes == null) {
                aes = new AESEncrypt();
                aes.setAlgorithKey();
            }
            String values = sp.getString(key, "");
            if (TextUtils.isEmpty(values))
                return "";
            return aes.decrypt(values);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 2018年3月29日
     * 因为做了伪分页加载之后，数据的位置变得无法预测，
     * 所以只能采用暴力循环的方式操作
     */

    /**
     * 订单付款后，设置数据改变标志
     * <p>
     * 2018年3月29日
     * 付款成功后会修改所有订单列表的相应数据，同时移除未付款订单列表的数据、添加至未发货订单列表
     *
     * @param orderNumber 订单号
     */
    public static void payForOrder(String orderNumber) {
//        if (order_list_all == null || order_list_all.size() == 0)
//            return;
        Logger.e(TAG, "payForOrder");
        boolean isAllChange = false;
        boolean isNoPayChange = false;
//-------------------------------------全部列表----------------------------------
        //该订单在order_list_all
        for (int i = 0; i < order_list_all.size(); i++) {
            HashMap<String, Object> orderItem = order_list_all.get(i);
            String oNum = orderItem.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {// 改变订单状态
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                orderItem.remove("order_PaymentStatus");
                orderItem.put("order_PaymentStatus", "2");// 已付款状态
                orderItem.remove("order_ShipmentStatus");
                orderItem.put("order_ShipmentStatus", "0");// 未发货
                isAllChange = true;
                MyOrderActivity.dataChanges[0] = true;

                if (MyOrderNoSendActivity.isInit) {
                    MyOrderNoSendActivity.adapter.addDateItem(0, orderItem);//添加到未发货列表
                } else {
                    order_noSend.add(0, orderItem);
                }
                MyOrderActivity.dataChanges[2] = true;
                break;
            }
        }
        //如果数据是在order_all中
        if (!isAllChange)
            for (HashMap<String, Object> item : MyOrderData.order_all) {
                String order_orderNumber = item.get("order_orderNumber").toString();
                //更新所有订单列表
                if (orderNumber.equals(order_orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_all");
                    item.remove("order_PaymentStatus");
                    item.put("order_PaymentStatus", "2");
                    item.remove("order_ShipmentStatus");
                    item.put("order_ShipmentStatus", "0");
//                    MyOrderNoSendActivity.adapter.addDateItem(0, item);
                    order_list_noSend.add(0, item);
                    MyOrderActivity.dataChanges[2] = true;
                    break;
                }
            }

//-------------------------------------未付款列表----------------------------------
        //如果在order_list_noPay中
        /**
         * 不要在foreach循环里进行元素的remove/add操作，remove元素请使用Iterator方式，如果并发操作，需要对Iterator对象加锁
         */
        Iterator<HashMap<String, Object>> iterator = order_list_noPay.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> item = iterator.next();
            String oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noPay");
                iterator.remove();
                MyOrderActivity.dataChanges[1] = true;
                isNoPayChange = true;
                break;
            }
        }

        //如果在order_noPay中
        if (!isNoPayChange) {
            iterator = order_noPay.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> item = iterator.next();
                String oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noPay");
                    iterator.remove();
                    break;
                }
            }

        }


        if (loadListener != null) {
            loadListener.dataChanged();
        }
        MyOrderActivity.isCurrentPage = false;
        MyOrderData.tOrderNumber = null;
    }

    /**
     * 取消订单，删除特定数据，设置数据改变标志
     * <p>
     * 2018年3月29日
     * 取消订单时会修改所有订单列表中的数据，同时其他列表的会删除
     * 只有未付款的订单可以取消
     *
     * @param orderNumber 订单号
     *                    数据改变的页面底标; 0表示 全部，1表示 待付款，2表示 待发货，3表示 待收货，4表示 待评价
     *                    <p>
     *                    2018年3月28日
     *                    添加遍历order_noPay列表修改数据
     */
    public static void cancelOrder(String orderNumber) {
//        if (order_list_all == null || order_list_all.size() == 0)
//            return;
        String oNum;
        boolean isRemove = false;
        boolean isChange = false;
//--------------------------全部订单列表--------------------------------
        for (int i = 0; i < order_list_all.size(); i++) {
            oNum = order_list_all.get(i).get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                HashMap<String, Object> item = order_list_all.get(i);
                item.remove(ORDER_ORDERSTATUS_KEY);
                item.put(ORDER_ORDERSTATUS_KEY, "2");//表示订单取消
                isChange = true;
                MyOrderActivity.dataChanges[0] = true;
                break;
            }
            //2018年3月28日 注释
//            if (i < order_list_noPay.size()) {
//                oNum = order_list_noPay.get(i).get("order_orderNumber").toString();
//                if (oNum.equals(orderNumber) && !isDelete2) {
//                    Logger.i("找到相应数据", orderNumber);
//                    order_list_noPay.remove(i);
//                    isDelete2 = true;
//                }
//            }
        }
        if (!isChange)
            for (int i = 0; i < order_all.size(); i++) {
                oNum = order_all.get(i).get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_all");
                    HashMap<String, Object> item = order_all.get(i);
                    item.remove(ORDER_ORDERSTATUS_KEY);
                    item.put(ORDER_ORDERSTATUS_KEY, "2");//表示订单取消
                    break;
                }
            }
//--------------------------未付款订单列表--------------------------------

        Iterator<HashMap<String, Object>> iterator = order_list_noPay.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> item = iterator.next();
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noPay");
                iterator.remove();
                isRemove = true;
                MyOrderActivity.dataChanges[1] = true;
                break;
            }
        }


//        for (HashMap<String, Object> item : order_list_noPay) {
//            oNum = item.get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber)) {
//                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noPay");
//                isRemove = removeData(order_list_noPay, orderNumber);
//                MyOrderActivity.dataChanges[1] = true;
//                break;
//            }
//        }
        if (!isRemove) {
            iterator = order_noPay.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> item = iterator.next();
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noPay");
                    iterator.remove();
                    MyOrderActivity.dataChanges[1] = true;
                    break;
                }
            }

//            for (HashMap<String, Object> item : order_noPay) {
//                oNum = item.get("order_orderNumber").toString();
//                if (oNum.equals(orderNumber)) {
//                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noPay");
//                    removeData(order_noPay, orderNumber);
//                    break;
//                }
//            }
        }

        if (loadListener != null) {
            loadListener.dataChanged();
        }

    }

    /**
     * 确认收货
     * <p>
     * 2018年3月29日
     * 确认收货需要修改全部订单列表里的该订单数据，从待收货列表中删除该数据，添加至已完成列表
     *
     * @param orderNumber 订单号
     *                    数据改变的页面底标; 0表示 全部，1表示 待付款，2表示 待发货，3表示 待收货，4表示 待评价
     */
    public static void sureTakeGoods(String orderNumber) {
//        if (order_list_all == null || order_list_all.size() == 0)
//            return;
        String oNum;
        boolean isAllChange = false;
        boolean isRemove = false;

//--------------------------全部订单列表-----------------------------
        for (HashMap<String, Object> item : order_list_all) {
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                item.remove("order_OderStatus");
                item.remove("order_ShipmentStatus");
                item.remove("order_PaymentStatus");
                item.put("order_OderStatus", "5");// 订单完成
                item.put("order_ShipmentStatus", "2");// 已收货
                item.put("order_PaymentStatus", "2");// 已付款
                isAllChange = true;
                MyOrderActivity.dataChanges[0] = true;
                break;
            }
        }
        if (!isAllChange)
            for (HashMap<String, Object> item : order_all) {
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_all");
                    item.remove("order_OderStatus");
                    item.remove("order_ShipmentStatus");
                    item.remove("order_PaymentStatus");
                    item.put("order_OderStatus", "5");// 订单完成
                    item.put("order_ShipmentStatus", "2");// 已收货
                    item.put("order_PaymentStatus", "2");// 已付款
                    break;
                }
            }
//--------------------待收货订单、已完成订单-----------------------------


        Iterator<HashMap<String, Object>> iterator = order_list_noReceive.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> item = iterator.next();
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noReceive");
                item.remove("order_OderStatus");
                item.remove("order_ShipmentStatus");
                item.remove("order_PaymentStatus");
                item.put("order_OderStatus", "5");// 已确认订单
                item.put("order_ShipmentStatus", "2");// 已收货
                item.put("order_PaymentStatus", "2");// 已付款
                order_list_noComment.add(0, item);
                iterator.remove();
                isRemove = true;
                MyOrderActivity.dataChanges[3] = true;
                MyOrderActivity.dataChanges[4] = true;
                break;
            }
        }


//        for (HashMap<String, Object> item : order_list_noReceive) {
//            oNum = item.get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber)) {
//                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noReceive");
//                item.remove("order_OderStatus");
//                item.remove("order_ShipmentStatus");
//                item.remove("order_PaymentStatus");
//                item.put("order_OderStatus", "5");// 已确认订单
//                item.put("order_ShipmentStatus", "2");// 已收货
//                item.put("order_PaymentStatus", "2");// 已付款
//                order_list_noComment.add(0, item);
//                order_list_noReceive.remove(item);
//                isRemove = true;
//                MyOrderActivity.dataChanges[3] = true;
//                MyOrderActivity.dataChanges[4] = true;
//                break;
//            }
//        }

        if (!isRemove) {
            iterator = order_noReceive.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> item = iterator.next();
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noReceive");
                    item.remove("order_OderStatus");
                    item.remove("order_ShipmentStatus");
                    item.remove("order_PaymentStatus");
                    item.put("order_OderStatus", "5");// 已确认订单
                    item.put("order_ShipmentStatus", "2");// 已收货
                    item.put("order_PaymentStatus", "2");// 已付款
                    order_list_noComment.add(0, item);
                    iterator.remove();
                    MyOrderActivity.dataChanges[4] = true;
                    break;
                }
            }

//            for (HashMap<String, Object> item : order_noReceive) {
//                oNum = item.get("order_orderNumber").toString();
//                if (oNum.equals(orderNumber)) {
//                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noReceive");
//                    item.remove("order_OderStatus");
//                    item.remove("order_ShipmentStatus");
//                    item.remove("order_PaymentStatus");
//                    item.put("order_OderStatus", "5");// 已确认订单
//                    item.put("order_ShipmentStatus", "2");// 已收货
//                    item.put("order_PaymentStatus", "2");// 已付款
//                    order_list_noComment.add(0, item);
//                    MyOrderActivity.dataChanges[4] = true;
//                    order_noReceive.remove(item);
//                    break;
//                }
//            }
        }
        if (loadListener != null) {
            loadListener.dataChanged();
        }

    }

    /**
     * 申请退款数据变动处理
     * <p>
     * 2018年3月29日
     * 申请退款需要修改全部订单列表中的数据，同时移除待发货列表中的数据
     *
     * @param orderNumber 订单号
     */
    public static void payBackHandle(String orderNumber) {
//        if (order_list_all == null || order_list_all.size() == 0)
//            return;
//        boolean isDelete = false, isDelete2 = false, isDelete3 = false, isDelete4 = false;
        boolean isAllChange = false;
        boolean isRemove = false;
        String oNum;
//        for (int i = 0; i < order_list_all.size(); i++) {
//             oNum = order_list_all.get(i).get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber) && !isDelete) {
//                Logger.i("找到相应数据", orderNumber);
//                HashMap<String, Object> hashMap = order_list_all.get(i);
//                hashMap.remove(ORDER_ORDERSTATUS_KEY);
//                hashMap.put(ORDER_ORDERSTATUS_KEY, "4");// 表示 请退货
//                isDelete = true;
//            }
//            if (i < order_list_noSend.size()) {
//                removeItem(i, orderNumber, order_list_noSend, isDelete2);
//            }
//            if (i < order_list_noReceive.size()) {
//                removeItem(i, orderNumber, order_list_noReceive, isDelete3);
//            }
//            if (i < order_list_noComment.size()) {
//                removeItem(i, orderNumber, order_list_noComment, isDelete4);
//            }
//            if (isDelete && isDelete2 && isDelete3 && isDelete4) {
//                MyOrderActivity.dataChanges[0] = true;
//                MyOrderActivity.dataChanges[2] = true;
//                MyOrderActivity.dataChanges[3] = true;
//                MyOrderActivity.dataChanges[4] = true;
//                break;
//            }
//        }


//-----------------------全部订单列表----------------------------------------


        for (HashMap<String, Object> item : order_list_all) {
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                item.remove(ORDER_ORDERSTATUS_KEY);
                item.put(ORDER_ORDERSTATUS_KEY, "4");// 表示 请退货
                MyOrderActivity.dataChanges[0] = true;
                isAllChange = true;
                break;
            }
        }
        if (!isAllChange)
            for (HashMap<String, Object> item : order_all) {
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_all");
                    item.remove(ORDER_ORDERSTATUS_KEY);
                    item.put(ORDER_ORDERSTATUS_KEY, "4");// 表示 请退货
                    break;
                }
            }

//-----------------------待发货订单列表----------------------------------------

        Iterator<HashMap<String, Object>> iterator = order_list_noSend.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> item = iterator.next();
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noSend");
                iterator.remove();
                isRemove = true;
                MyOrderActivity.dataChanges[2] = true;
                break;
            }
        }

        if (!isRemove) {
            iterator = order_noSend.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> item = iterator.next();
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noSend");
                    iterator.remove();
                    break;
                }
            }
        }
        //已完成订单列表没有申请退款按钮
//-----------------------已完成订单列表----------------------------------------
//        for (HashMap<String, Object> item : order_list_noComment) {
//            oNum = item.get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber)) {
//                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noComment");
//                isRemove = removeData(order_list_noComment, orderNumber);
//                MyOrderActivity.dataChanges[4] = true;
//                break;
//            }
//        }
//        if (!isRemove)
//            for (HashMap<String, Object> item : order_noComment) {
//                oNum = item.get("order_orderNumber").toString();
//                if (oNum.equals(orderNumber)) {
//                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noComment");
//                    removeData(order_noComment, orderNumber);
//                    break;
//                }
//            }
        if (loadListener != null) {
            loadListener.dataChanged();
        }
        MyOrderActivity.isCurrentPage = false;
    }

    /**
     * 评价; 调用此方法时，先实例化MyOrderData.tOrderNumber
     *
     * @param context 数据改变的页面底标; 0表示 全部，1表示 待付款，2表示 待发货，3表示 待收货，4表示 待评价
     */
    public static void commentOrder(Context context) {
        Logger.e(TAG, "评价订单");
//        if (order_list_all == null || order_list_all.size() == 0)
//            return;
        if (tOrderNumber == null) {
            CommonTools.showShortToast(context, "更新数据出错，请重新开启程序");
            return;
        }
        String orderNumber = tOrderNumber;
        boolean isAllChange = false;
        boolean isRemove = false;

//        boolean isAllChanged = false, isNoCommentChanged = false;
//        for (int i = 0; i < order_list_all.size(); i++) {
//            String oNum = order_list_all.get(i).get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber) && !isAllChanged) {
//                Logger.i("找到相应数据", orderNumber);
//                HashMap<String, Object> hashMap = order_list_all.get(i);
//                hashMap.remove("order_OderStatus");
//                hashMap.remove("order_ShipmentStatus");
//                hashMap.remove("order_PaymentStatus");
//                hashMap.put("order_OderStatus", "5");// 已完成订单
//                hashMap.put("order_ShipmentStatus", "2");// 已收货
//                hashMap.put("order_PaymentStatus", "2");// 已付款
//                isAllChanged = true;
//            }
//            if (i < order_list_noComment.size()) {
//                oNum = order_list_noComment.get(i).get("order_orderNumber").toString();
//                if (oNum.equals(orderNumber) && !isNoCommentChanged) {
//                    order_list_noComment.remove(i);
//                    isNoCommentChanged = true;
//                }
//            }
//            if (isAllChanged && isNoCommentChanged) {
//                MyOrderActivity.dataChanges[0] = true;
//                MyOrderActivity.dataChanges[4] = true;
//                break;
//            }
//        }

/*
-------------------------全部订单列表----------------------------------------
*/
        for (HashMap<String, Object> item : order_list_all) {
            String oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                item.remove("order_OderStatus");
                item.remove("order_ShipmentStatus");
                item.remove("order_PaymentStatus");
                item.put("order_OderStatus", "5");// 已完成订单
                item.put("order_ShipmentStatus", "2");// 已收货
                item.put("order_PaymentStatus", "2");// 已付款
                isAllChange = true;
                MyOrderActivity.dataChanges[0] = true;
                break;
            }
        }
        if (!isAllChange)
            for (HashMap<String, Object> item : order_all) {
                String oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_all");
                    item.remove("order_OderStatus");
                    item.remove("order_ShipmentStatus");
                    item.remove("order_PaymentStatus");
                    item.put("order_OderStatus", "5");// 已完成订单
                    item.put("order_ShipmentStatus", "2");// 已收货
                    item.put("order_PaymentStatus", "2");// 已付款
                    break;
                }
            }
//-----------------------已完成订单列表----------------------------------------

        Iterator<HashMap<String, Object>> iterator = order_list_noComment.iterator();
        while (iterator.hasNext()) {
            HashMap<String, Object> item = iterator.next();
            String oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noComment");
                iterator.remove();
                isRemove = true;
                MyOrderActivity.dataChanges[4] = true;
                break;
            }
        }


//        for (HashMap<String, Object> item : order_list_noComment) {
//            String oNum = item.get("order_orderNumber").toString();
//            if (oNum.equals(orderNumber)) {
//                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_noComment");
//                isRemove = removeData(order_list_noComment, orderNumber);
//                MyOrderActivity.dataChanges[4] = true;
//                break;
//            }
//        }

        if (!isRemove) {
            iterator = order_noComment.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> item = iterator.next();
                String oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_noComment");
                    iterator.remove();
                    break;
                }
            }
        }

        if (loadListener != null) {
            loadListener.dataChanged();
        }
        MyOrderActivity.isCurrentPage = false;
        tOrderNumber = null;
    }

    private static void removeItem(int index, String orderNumber, ArrayList<HashMap<String, Object>> order_list, boolean isDelete) {
        String oNum = order_list.get(index).get("order_orderNumber").toString();
        if (oNum.equals(orderNumber) && !isDelete) {
            Logger.i(orderNumber + "->找到相应数据", orderNumber);
            order_list.remove(index);
            isDelete = true;
        }
    }

    /**
     * 获取订单状态
     */
    public String getorderStatus(String oresult) {
        String oderStatus = null;
        switch (oresult) {
            case ("0"):
                oderStatus = "未确认";
                break;
            case ("1"):
                oderStatus = "已确认";
                break;
            case ("2"):
                oderStatus = "已取消";
                break;
            case ("3"):
                oderStatus = "已作废";
                break;
            case ("4"):
                oderStatus = "退货";
                break;
            case ("5"):
                oderStatus = "已完成";
                break;
        }
        return oderStatus;
    }

    /**
     * 获取订单发货状态
     */
    public String getordersresult(String sresult) {
        String shipmentStatus = "";
        switch (sresult) {
            case ("0"):
                shipmentStatus = "未发货";
                break;
            case ("1"):
                shipmentStatus = "已发货";
                break;
            case ("2"):
                shipmentStatus = "已收货";
                break;
            case ("3"):
                shipmentStatus = "配货中";
                break;
            case ("4"):
                shipmentStatus = "已退货";
                break;
        }
        return shipmentStatus;
    }

    /**
     * 获取订单支付状态
     */
    public String getorderpaymentStatus(String presult) {
        String paymentStatus = "";
        switch (presult) {
            case ("0"):
                paymentStatus = "未付款";
                break;
            case ("1"):
                paymentStatus = "付款中";
                break;
            case ("2"):
                paymentStatus = "已付款";
                break;
            case ("3"):
                paymentStatus = "已退款";
                break;
        }
        return paymentStatus;
    }

    /**
     * @param list
     * @param orderNumber
     */
    public static boolean removeData(ArrayList<HashMap<String, Object>> list, String orderNumber) {
        boolean result = false;
        for (HashMap<String, Object> item : list) {
            String number = item.get("order_orderNumber").toString();
            if (number.equals(orderNumber)) {
                list.remove(item);
                result = true;
                break;
            }
        }
        return result;
    }


    public interface LoadDataListener {
        void hideLoad();

        void loadFinish();

        void dataChanged();
    }
}
