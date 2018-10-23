package com.danertu.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.danertu.db.DBManager;
import com.danertu.dianping.ActivityUtils;
import com.danertu.dianping.BaseActivity;
import com.danertu.dianping.CartActivity;
import com.danertu.dianping.LoginActivity;
import com.danertu.dianping.PaymentCenterActivity;
import com.danertu.widget.CommonTools;


public class ProductDetailUtil {
    private BaseActivity base = null;
    private int canBuyCount = 2;
    private DBManager db = null;

//    public ProductDetailUtil(BaseActivity base, int canBuyCount, DBManager db) {
//        this(base, canBuyCount, db);
//    }

    public ProductDetailUtil(BaseActivity base, int canBuyCount, DBManager dbl) {
        this.base = base;
        this.canBuyCount = canBuyCount;
        this.db = dbl;
    }

    /**
     * @param tag 1、加入购物车，2、立即购买
     * @return
     */
    public boolean isCheckOutError(int tag, String sCount, int canBuyCount) {
        int count = 1;
        if (!base.isLogined()) {
            CommonTools.showShortToast(base, "请先登录！");
            base.openActivity(LoginActivity.class);
            return true;
        }
        try {
            count = Integer.parseInt(sCount);
        } catch (NumberFormatException e) {
            CommonTools.showShortToast(base, "不能输入非数字字符!");
            return true;
        }
        if (canBuyCount == 0) {
            CommonTools.showShortToast(base, "活动已停止或未开始");
            return true;
        } else if (canBuyCount == 1) {
            if (tag == 1) {
                CommonTools.showShortToast(base, "活动商品不能加入购物车");
                return true;
            } else {
                if (count > 1) {
                    CommonTools.showShortToast(base, "活动商品只能买一件哦");
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    private boolean checkErrorMsg(ArrayList<HashMap<String, Object>> list) {
        for (Map<String, Object> item : list) {
            if ((Boolean) item.get("selected") && (Boolean) item.get("isQuanYanProduct")) {
                if (TextUtils.isEmpty(item.get("arriveTime").toString())) {
                    CommonTools.showShortToast(base, "泉眼商品请先加入购物车，以便选择抵达和离开时间");
                    return true;
                }
                if ((Boolean) item.get("isQuanYanHotel")) {
                    if (TextUtils.isEmpty(item.get("leaveTime").toString())) {
                        CommonTools.showShortToast(base, "泉眼商品请先加入购物车，以便选择抵达和离开时间");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 跳转至结算中心
     *
     * @param guid
     * @param sCount
     * @param imgName
     * @param supId
     * @param shopid
     * @param agentId
     * @param proName
     * @param sPrice
     * @param createUser
     * @param isbackcall 是否为后台拿货商品
     * @param attrParam  商品属性
     * @param arriveTime
     * @param leaveTime
     * @param shopName
     * @return
     */
    public boolean toPayCenter(String guid, String sCount, String imgName,
                               String supId, String shopid, String agentId, String proName,
                               String sPrice, String marketPrice, String createUser, boolean isbackcall, String attrParam,
                               String arriveTime, String leaveTime, String shopName, String discountNum, String discountPrice,String uid) {
        if (isCheckOutError(2, sCount, canBuyCount)) {
            return false;
        }
        ArrayList<HashMap<String, Object>> list = ActivityUtils.getShopCarList(guid, sCount,
                imgName, supId, shopid, agentId, proName, sPrice, marketPrice, createUser, attrParam, arriveTime, leaveTime, shopName, discountNum, discountPrice,uid);
//		if(!checkErrorMsg(list)) {
        int allCount = 0;
        double totalMoney = 0;

        Iterator<HashMap<String, Object>> iter = list.iterator();
        while (iter.hasNext()) {
            Map<String, Object> item = iter.next();
            if ((Boolean) item.get("selected")) {
                try {
                    double price = Double.parseDouble(item.get("price").toString());
                    String agentid = item.get("agentID").toString();
                    int count = Integer.parseInt(item.get("count").toString());
                    allCount += count;
                    if (agentid != null && agentid.equals("")) {
                        totalMoney += (price * count);
                    }
                } catch (Exception e) {
                    return false;
                }
            } else {
                iter.remove();
            }
        }

        Intent mIntent = new Intent();
        mIntent.setClassName(base, "com.danertu.dianping.PaymentCenterActivity");
        // Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle b = new Bundle();
        b.putString(PaymentCenterActivity.KEY_ALLCOUNT, String.valueOf(allCount));
        b.putString(PaymentCenterActivity.KEY_ALLMONEY, String.valueOf(totalMoney));
        b.putBoolean(PaymentCenterActivity.KEY_IS_BACK_CALL, isbackcall);
        // 此处使用putExtras，接受方就响应的使用getExtra
        mIntent.putExtra(CartActivity.KEY_SHOPCAR_LIST, list);
        mIntent.putExtras(b);
        base.startActivity(mIntent);
        return true;
//		}
//		return false;
    }

    /**
     * 加入购物车
     */
    public boolean putInShopCar(String productID, String prouctName, String aID, String buyPrice, String proImage, String buyCount, String uid, String supId, String shopID, String attrParam, String shopName, String createUser) {
        if (isCheckOutError(1, buyCount, canBuyCount)) {
            return false;
        }
        // 确定按钮事件
        try {
            return db.InsertShopCar(base, productID, prouctName, aID, buyPrice, proImage, buyCount, uid, supId, shopID, attrParam, shopName, createUser);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
//    public boolean putInShopCar(String productID, String prouctName, String aID, String buyPrice,String marketPrice, String proImage, String buyCount, String uid, String supId, String shopID, String attrParam, String shopName, String createUser) {
//        if (isCheckOutError(1, buyCount, canBuyCount)) {
//            return false;
//        }
//        // 确定按钮事件
//        try {
//            return db.InsertShopCar(base, productID, prouctName, aID, buyPrice,marketPrice, proImage, buyCount, uid, supId, shopID, attrParam, shopName, createUser);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

}
