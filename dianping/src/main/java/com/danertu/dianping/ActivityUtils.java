package com.danertu.dianping;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.config.Constants;
import com.danertu.db.DBHelper;
import com.danertu.tools.AppManager;
import com.danertu.tools.Logger;

public class ActivityUtils {
    private static ActivityUtils obj;

    private ActivityUtils() {

    }

    public static ActivityUtils getInstance() {
        if (obj == null)
            synchronized (ActivityUtils.class) {
                if (obj == null)
                    obj = new ActivityUtils();
            }
        return obj;
    }

    /**
     * 跳转到商品详细2（新版）页面
     *
     * @param canBuy 表示能买多少个  0 表示不能买，1表示只能买1件，否则可任意购买
     */
    public static void toProDetail2(Context context, String guid, String proName, String img,
                                    String detail, String agentID, String supplierID, String price, String marketprice, String mobile, String VirtualBuyCount, int canBuy) {
        Intent intent = new Intent();
        intent.setClassName(context, "com.danertu.dianping.ProductDetailsActivity2");
        // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
        Bundle b = new Bundle();
        b.putString("guid", guid);
        b.putString("proName", proName);
        b.putString("img", img);
        b.putString("detail", detail);
        b.putString("agentID", agentID);
        b.putString("supplierID", supplierID);
        b.putString("price", price);
        b.putString("marketprice", marketprice);
        b.putString("shopid", agentID);
        b.putString("act", "ProductListActivity");
        b.putInt("TouchPost", 0);
        b.putString("mobile", mobile);
        b.putString("VirtualBuyCount", VirtualBuyCount);
        b.putString(ProductDetailsActivity2.KEY_CAN_BUY, String.valueOf(canBuy));

        Logger.e("test", "ActivityUtils toProDetail2 bundle=" + b);
        intent.putExtras(b);
        context.startActivity(intent);
    }

    //<---店铺json数据项key--->
    public static final String SHOP_NAME = "ShopName";
    public static final String SHOP_ADDRESS = "TYAddress";
    public static final String SHOP_MOBILE = "Mobile";
    public static final String SHOP_DETAILS = "shopdetails";
    public static final String SHOP_IMG = "shopimg";
    public static final String SHOP_TJPRODUCT = "shopshowproduct";
    public static final String SHOP_ENTITYIMAGE = "EntityImage";
    public static final String SHOP_JYFW = "ShopJYFW";
    public static final String SHOP_M = "m";
    public static final String SHOP_AVGSCORE = "avgscore";
    public static final String SHOP_PLSCORE = "plscore";
    public static final String SHOP_BANNER = "Mobilebanner";

    public HashMap<String, String> analyzeShopJson(String shopJson) throws JSONException {
        HashMap<String, String> item = new HashMap<>();
        JSONObject oj = new JSONObject(shopJson).getJSONObject("shopdetails").getJSONArray("shopbean").getJSONObject(0);
        item.put(SHOP_NAME, oj.getString(SHOP_NAME));
        item.put(SHOP_ADDRESS, oj.getString(SHOP_ADDRESS));
        item.put(SHOP_MOBILE, oj.getString(SHOP_MOBILE));
        item.put(SHOP_DETAILS, oj.getString(SHOP_DETAILS));
        item.put(SHOP_IMG, oj.getString(SHOP_IMG));
        item.put(SHOP_TJPRODUCT, oj.getString(SHOP_TJPRODUCT));
        item.put(SHOP_ENTITYIMAGE, oj.getString(SHOP_ENTITYIMAGE));
        item.put(SHOP_JYFW, oj.getString(SHOP_JYFW));
        item.put(SHOP_M, oj.getString(SHOP_M));
        item.put(SHOP_AVGSCORE, oj.getString(SHOP_AVGSCORE));
        item.put(SHOP_PLSCORE, oj.getString(SHOP_PLSCORE));
        item.put(SHOP_BANNER, oj.getString(SHOP_BANNER));
        return item;
    }

    public static ArrayList<HashMap<String, Object>> getShopCarList(String guid, String buycount, String proImg, String supID,
                                                                    String shopid, String agentID, String proName, String price, String marketPrice, String createUser, String attrJson, String arriveTime, String leaveTime, String shopName, String discountNum, String discountPrice) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        list.add(getShopCarItem(true, guid, buycount, proImg, supID, shopid, agentID, proName, price, marketPrice, createUser, attrJson, arriveTime, leaveTime, shopName, discountNum, discountPrice));
        return list;
    }

    /**
     * @param isSelect
     * @param productID
     * @param buycount
     * @param imgName
     * @param supplierID
     * @param shopID
     * @param agentID
     * @param proName
     * @param price
     * @param createUser
     * @param attrJson
     * @param arriveTime
     * @param leaveTime
     * @param shopName   默认值： 醇康
     * @return
     */
    public static HashMap<String, Object> getShopCarItem(boolean isSelect, String productID, String buycount, String imgName, String supplierID,
                                                         String shopID, String agentID, String proName, String price, String marketPrice, String createUser, String attrJson, String arriveTime, String leaveTime, String shopName, String discountNum, String discountPrice) {
        HashMap<String, Object> dataMap = new HashMap<>();
        if (supplierID != null && supplierID.equals("shopnum1")) {
            dataMap.put("isQuanYanProduct", true);
            if (Constants.list_guestroom.contains(productID)) {
                dataMap.put("isQuanYanHotel", true);
            } else {
                dataMap.put("isQuanYanHotel", false);
            }
        } else {
            dataMap.put("isQuanYanProduct", false);
            dataMap.put("isQuanYanHotel", false);
        }
        createUser = createUser == null ? "" : createUser;
        attrJson = attrJson == null ? "" : attrJson;
        String imgURL = getImgUrl(imgName, agentID, supplierID);
        shopName = TextUtils.isEmpty(shopName) ? "醇康" : shopName;

        dataMap.put("productID", productID);
        dataMap.put("proName", proName);
        dataMap.put("price", price);
        dataMap.put("marketPrice", marketPrice);
        dataMap.put("count", buycount);
        dataMap.put("agentID", agentID);
        dataMap.put("imgURL", imgURL);
        dataMap.put("supplierID", supplierID);
        dataMap.put("selected", isSelect);
        dataMap.put("arriveTime", arriveTime);
        dataMap.put("leaveTime", leaveTime);
        dataMap.put(DBHelper.SHOPCAR_SHOPID, shopID);
        dataMap.put(DBHelper.SHOPCAR_CREATEUSER, createUser);
        dataMap.put(DBHelper.SHOPCAR_ATTRJSON, attrJson);
        dataMap.put(DBHelper.SHOPCAR_SHOPNAME, shopName);
        dataMap.put("discountNum", discountNum);
        dataMap.put("discountPrice", discountPrice);
        return dataMap;
    }

    private static String imgServer = "";

    public static String getImgUrl(String imgName, String agentID, String supplierID) {
        String imgURL = "";
        if (TextUtils.isEmpty(imgServer)) {//since version 5.4
            Thread t = new Thread() {
                public void run() {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("apiid", "0154");
                    imgServer = AppManager.getInstance().doPost(param);
                    if (!TextUtils.isEmpty(imgServer) && !imgServer.contains("http")) {
                        imgServer = "http://" + imgServer + "/";
                    }
                }
            };
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(imgServer)) {
                imgServer = com.config.Constants.imgServer;
            }
        }
        if (imgName != null && imgName.contains("/")) {
            imgName = imgName.substring(imgName.lastIndexOf("/") + 1, imgName.length());

        }
        if (agentID != null && agentID.trim().length() > 0) {
            imgURL = imgServer + "Member/" + agentID + "/" + imgName;

        } else if (supplierID != null && supplierID.trim().length() > 0) {
            imgURL = imgServer + "SupplierProduct/" + supplierID + "/" + imgName;

        } else {
            imgURL = imgServer + "sysProduct/" + imgName;
        }
        return imgURL;
    }

    public static void backToIndex() {
        if (HomeActivity.mHandle != null) {
            Message msg = Message.obtain();
            msg.what = HomeActivity.WHAT_CHANGETAB;
            msg.arg1 = 0;
            HomeActivity.mHandle.sendMessage(msg);
        } else {
            Log.e("error", "HomeActivity mHandler is null");
        }
    }

    /**
     * @param type 1 自驾， 2  公交， 3  步行
     */
    public void toRouteGoPlanActivity(Context context, String type, String shopName, String la, String lt) {
//        Intent intent = new Intent();
//        intent.setClassName(context, "com.danertu.dianping.RouteGoPlanActivity");
        Intent intent = new Intent(context, RouteGoPlanActivity.class);
        // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
        Bundle b = new Bundle();
        b.putString("endName", shopName);
        b.putString("la", la);
        b.putString("lt", lt);
        b.putString("type", type);
        b.putString("cityName", Constants.getCityName());
        // 此处使用putExtras，接受方就响应的使用getExtra
        intent.putExtras(b);
        context.startActivity(intent);
    }
}
