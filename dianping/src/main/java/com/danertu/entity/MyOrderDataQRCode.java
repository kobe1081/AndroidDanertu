package com.danertu.entity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.config.Constants;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.MyOrderCompleteQRCodeActivity;
import com.danertu.dianping.MyOrderListAllActivity;
import com.danertu.dianping.MyOrderNoCommentActivity;
import com.danertu.dianping.MyOrderNoPayActivity;
import com.danertu.dianping.MyOrderNoReceiveActivity;
import com.danertu.dianping.MyOrderNoSendActivity;
import com.danertu.dianping.MyOrderNoUseActivity;
import com.danertu.dianping.MyOrderQRCodeActivity;
import com.danertu.dianping.PayBackActivity;
import com.danertu.dianping.ProductCommentActivity;
import com.danertu.tools.AESEncrypt;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 提供订单数据及部分订单操作的类
 *
 * @author dengweilin
 */
public abstract class MyOrderDataQRCode {
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


    public static ArrayList<HashMap<String, Object>> order_list_no_use = new ArrayList<>();
    public static ArrayList<HashMap<String, Object>> order_list_complete = new ArrayList<>();

    /**
     * 分页数据源
     */
    public static ArrayList<HashMap<String, Object>> order_no_use = new ArrayList<>();
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

    public static String TAG = "MyOrderDataQRCode";

    public void setLoadListener(LoadDataListener loadListener) {
        this.loadListener = loadListener;
    }

    private static LoadDataListener loadListener;

    public MyOrderDataQRCode(BaseActivity context) {
        this(context, null);
        this.context = context;
    }

    private final int GET_DATA_SUCCESS = 666;
    private final int ADD_DATA_NO_USE = 668;
    private final int ADD_DATA_COMPLETE = 671;
    private final int LOAD_DATA = 672;
    private final int LOAD_DATA_FINISH = 673;
    private Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            HashMap<String, Object> item = (HashMap<String, Object>) msg.obj;
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    getDataSuccess();
                    break;

                case ADD_DATA_NO_USE:
                    if (TAB_INDEX == 0 && order_list_no_use.size() < LIST_INIT_SIZE) {
                        MyOrderNoUseActivity.adapter.addDateItem(item);
                    } else {
                        order_no_use.add(item);
                    }
                    break;
                case ADD_DATA_COMPLETE:
                    if (TAB_INDEX == 1 && order_list_complete.size() < LIST_INIT_SIZE) {
                        MyOrderCompleteQRCodeActivity.adapter.addDateItem(item);
                    } else {
                        order_complete.add(item);
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


    public MyOrderDataQRCode(BaseActivity context, String orderNum) {
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
    public MyOrderDataQRCode(BaseActivity context, boolean noClearAllData, String orderNum) {
        this(context, orderNum, false, noClearAllData, false);
    }


    public MyOrderDataQRCode(BaseActivity context, boolean isOnlyHotel) {
        this(context, null, isOnlyHotel);
    }

    public MyOrderDataQRCode(BaseActivity context, boolean isOnlyHotel, boolean isOnlyQuanYan) {
        this(context, null, isOnlyHotel, isOnlyQuanYan);
    }


    public MyOrderDataQRCode(BaseActivity context, String orderNum, boolean isOnlyHotel) {
        this(context, orderNum, isOnlyHotel, false, false);
    }

    public MyOrderDataQRCode(BaseActivity context, String orderNum, boolean isOnlyHotel, boolean isOnlyQuanYan) {
        this(context, orderNum, isOnlyHotel, false, isOnlyQuanYan);
    }

    public MyOrderDataQRCode(BaseActivity context, String orderNum, boolean isOnlyHotel, boolean noClearAllData, boolean isOnlyQuanYan) {
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
        MyOrderQRCodeActivity.dataChanges = new Boolean[2];
        for (int i = 0; i < MyOrderQRCodeActivity.dataChanges.length; i++) {
            MyOrderQRCodeActivity.dataChanges[i] = false;
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
     * 取单个订单详情 通过实例此构造方法-MyOrderData(Context context, String orderNum)
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


    public HashMap<String, Object> getItemOrder() {
        return itemOrder;
    }

    /**
     * 注意：这是全部订单头信息
     */
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
                sendMSG(LOAD_DATA_FINISH, null);
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
        MyOrderDataQRCode.tOrderNumber = orderNumber;
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
        order_list_no_use.clear();
        order_list_complete.clear();

        order_no_use.clear();
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
            Logger.e(TAG, oj.toString());
            String orderNumber = oj.getString("OrderNumber");
            String orderStatus = oj.getString("OderStatus");
            String ShipmentStatus = oj.getString("ShipmentStatus");
            String PaymentStatus = oj.getString("PaymentStatus");
            String DispatchModeName = oj.getString("DispatchModeName");
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
            // orderStatus+" , "+ShipmentStatus+" , "+PaymentStatus+" , "+ShouldPayPrice+" , "+PaymentName);
            if (orderStatus.equals("1")) {
                if (PaymentStatus.equals("0")) {// 付款状态为 未付款
                    sendMSG(ADD_DATA_NO_USE, item);
                } else if (PaymentStatus.equals("2") && ShipmentStatus.equals("0")) {// 已付款 ，未发货
                    sendMSG(ADD_DATA_NO_USE, item);
                }
            } else if (orderStatus.equals("4")) {
                sendMSG(ADD_DATA_COMPLETE, item);
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
        for (int i = 0; i < order_list_no_use.size(); i++) {
            HashMap<String, Object> orderItem = order_list_no_use.get(i);
            String oNum = orderItem.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {// 改变订单状态
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_no_use");
                orderItem.remove("order_PaymentStatus");
                orderItem.put("order_PaymentStatus", "2");// 已付款状态
                orderItem.remove("order_ShipmentStatus");
                orderItem.put("order_ShipmentStatus", "0");// 未发货
                isAllChange = true;
                MyOrderQRCodeActivity.dataChanges[0] = true;
                break;
            }
        }
        //如果数据是在order_all中
        if (!isAllChange)
            for (HashMap<String, Object> item : MyOrderDataQRCode.order_no_use) {
                String order_orderNumber = item.get("order_orderNumber").toString();
                //更新所有订单列表
                if (orderNumber.equals(order_orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_no_use");
                    item.remove("order_PaymentStatus");
                    item.put("order_PaymentStatus", "2");
                    item.remove("order_ShipmentStatus");
                    item.put("order_ShipmentStatus", "0");
                    MyOrderQRCodeActivity.dataChanges[0] = true;
                    break;
                }
            }

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
        boolean isChange = false;
//--------------------------全部订单列表--------------------------------
        for (int i = 0; i < order_list_no_use.size(); i++) {
            oNum = order_list_no_use.get(i).get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_all");
                HashMap<String, Object> item = order_list_no_use.get(i);
                item.remove(ORDER_ORDERSTATUS_KEY);
                item.put(ORDER_ORDERSTATUS_KEY, "2");//表示订单取消
                isChange = true;
                MyOrderQRCodeActivity.dataChanges[0] = true;
                break;
            }
        }
        if (!isChange)
            for (int i = 0; i < order_no_use.size(); i++) {
                oNum = order_no_use.get(i).get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_no_use");
                    HashMap<String, Object> item = order_no_use.get(i);
                    item.remove(ORDER_ORDERSTATUS_KEY);
                    item.put(ORDER_ORDERSTATUS_KEY, "2");//表示订单取消
                    break;
                }
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

//--------------------------全部订单列表-----------------------------
        for (HashMap<String, Object> item : order_list_no_use) {
            oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_no_use");
                item.remove("order_OderStatus");
                item.remove("order_ShipmentStatus");
                item.remove("order_PaymentStatus");
                item.put("order_OderStatus", "5");// 订单完成
                item.put("order_ShipmentStatus", "2");// 已收货
                item.put("order_PaymentStatus", "2");// 已付款
                isAllChange = true;
                MyOrderQRCodeActivity.dataChanges[0] = true;
                break;
            }
        }
        if (!isAllChange)
            for (HashMap<String, Object> item : order_no_use) {
                oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_no_use");
                    item.remove("order_OderStatus");
                    item.remove("order_ShipmentStatus");
                    item.remove("order_PaymentStatus");
                    item.put("order_OderStatus", "5");// 订单完成
                    item.put("order_ShipmentStatus", "2");// 已收货
                    item.put("order_PaymentStatus", "2");// 已付款
                    break;
                }
            }

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

        for (HashMap<String, Object> item : order_list_complete) {
            String oNum = item.get("order_orderNumber").toString();
            if (oNum.equals(orderNumber)) {
                Logger.e(TAG, orderNumber + "->找到相应数据-order_list_complete");
                item.remove("order_OderStatus");
                item.remove("order_ShipmentStatus");
                item.remove("order_PaymentStatus");
                item.put("order_OderStatus", "5");// 已完成订单
                item.put("order_ShipmentStatus", "2");// 已收货
                item.put("order_PaymentStatus", "2");// 已付款
                isAllChange = true;
                MyOrderQRCodeActivity.dataChanges[0] = true;
                break;
            }
        }
        if (!isAllChange)
            for (HashMap<String, Object> item : order_complete) {
                String oNum = item.get("order_orderNumber").toString();
                if (oNum.equals(orderNumber)) {
                    Logger.e(TAG, orderNumber + "->找到相应数据-order_complete");
                    item.remove("order_OderStatus");
                    item.remove("order_ShipmentStatus");
                    item.remove("order_PaymentStatus");
                    item.put("order_OderStatus", "5");// 已完成订单
                    item.put("order_ShipmentStatus", "2");// 已收货
                    item.put("order_PaymentStatus", "2");// 已付款
                    break;
                }
            }

        if (loadListener != null) {
            loadListener.dataChanged();
        }
        MyOrderQRCodeActivity.isCurrentPage = false;
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
