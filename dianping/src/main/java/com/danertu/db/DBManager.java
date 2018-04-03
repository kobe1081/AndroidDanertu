package com.danertu.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import cn.jpush.android.api.JPushInterface;

import com.danertu.dianping.LoginActivity;
import com.danertu.entity.Address;
import com.danertu.entity.JPushBean;
import com.danertu.tools.Logger;
import com.danertu.widget.CommonTools;

public class DBManager {

    //	private Integer version = 1;
    private boolean jpInitFirst = true;

    private static DBManager dbManager;
    public static final int PAGE_SIZE=30;
    private DBManager() {
    }

    public static DBManager getInstance(){
        if(dbManager==null){
            synchronized (DBManager.class){
                if(dbManager==null) {
                    dbManager=new DBManager();
                }
            }
        }
        return dbManager;
    }

    // 取当前系统记录有效登录信息
    public Cursor GetLoginInfo(Context context) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        String[] columns = {"uId", "pwd", "email", "score", "loginTime", "nickname", "headimgurl"};
        return dbr.query(DBHelper.TABLE_USER_INFO, columns, "isLogin='1'", null, null, null, null);
    }

    // 退出登录后，清除用户登录数据,将该用户置为不是默认登录
    public void DeleteLoginInfo(Context context, String uid) {
        uId = "";
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        // dbr.execSQL("delete from userLoginInfo");
        dbr.execSQL("update userLoginInfo set isLogin='0' where uId='" + uid + "'");

        if (!jpInitFirst) {
            JPushInterface.setAlias(context, uId, null);
            jpInitFirst = true;
        }
        dbr.close();
    }

    private String uId = "";

    public String GetLoginUid(Context context) {
        if (!TextUtils.isEmpty(uId)) {
            if (jpInitFirst) {
                JPushInterface.init(context);
                JPushInterface.setAlias(context, uId, null);
                jpInitFirst = false;
            }
            return uId;
        }

        SQLiteDatabase dbr = null;
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("userLoginInfo", new String[]{"uId"}, " isLogin='1'", null, null, null, null);

            if (!cursor.isClosed() && cursor.moveToFirst()) {
                uId = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        if (TextUtils.isEmpty(uId)) {
            JPushInterface.init(context);
            JPushInterface.setAlias(context, "", null);
            jpInitFirst = true;
        }
        return uId;
    }

    // 取到收货地址后，存入本地
    public void InsertAddress(Context context, Address address) {
        // String uuid =UUID.randomUUID().toString();
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            dbr.execSQL("insert into userAdress (_id,name,tel,mobile,adress,IsDefault,ModifyTime,_Guid,_uid) values(NULL,?,?,?,?,?,?,?,?)",
                    new Object[]{address.getName(), address.getMobile(),
                            address.getMobile(), address.getAddress(),
                            address.getIsDefault(), address.getModifyTime(),
                            address.getGuid(), GetLoginUid(context)});
            if (address.getIsDefault().equals("1")) {
                // dbr.execSQL("update userAdress set IsDefault=1 where _uid ='"+uuid+"'");
                dbr.execSQL("update userAdress set IsDefault=0 where _uid='" + GetLoginUid(context) + "' and  _Guid !='" + address.getGuid() + "'");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 将登陆用户置为默认登录用户
    public void setLoginUserIsDefault(Context context, String uid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        dbr.execSQL("update userLoginInfo set isLogin=1 where uId='" + uid + "'");
        dbr.execSQL("update userLoginInfo set isLogin=0 where uId!='" + uid + "'");
    }

    // 取用户收货地址
    public Cursor GetUserAddress(Context context, String uId) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("userAdress", new String[]{"name", "tel",
                            "mobile", "IsDefault", "adress", "_Guid", "ModifyTime"},
                    " _uid='" + uId + "'", null, null, null, "ModifyTime desc");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 用户设置默认收货地址
    public void SetDefaultAddress(Context context, String _guid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        String sql = "update userAdress set IsDefault=1 where _uid='" + GetLoginUid(context) + "' and  _Guid='" + _guid + "'";
        dbr.execSQL(sql);
        sql = "update userAdress set IsDefault=0 where _uid='" + GetLoginUid(context) + "' and  _Guid !='" + _guid + "'";
        dbr.execSQL(sql);
    }

    /**
     * 用户设置收货地址
     *
     * @author dengweilin
     */
    public void SetAddress(Context context, String name, String mobile, String address, String _guid, boolean isDefault) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        ContentValues values = new ContentValues();
        String isdefau = isDefault ? "1" : "0";
        values.put("name", name);
        values.put("mobile", mobile);
        values.put("adress", address);
        values.put("IsDefault", isdefau);
        dbr.update("userAdress", values, "_Guid=?", new String[]{_guid});
        if (isDefault) {
            String sql = "update userAdress set IsDefault=0 where _uid='" + GetLoginUid(context) + "' and  _Guid !='" + _guid + "'";
            dbr.execSQL(sql);
        }
    }

    // 删除某收货地址
    public void deleteAddress(Context context, String _guid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        dbr.execSQL("delete from userAdress where _Guid = '" + _guid + "'");
    }

    // 同步PC端用户收货地址
    @SuppressLint("SimpleDateFormat")
    public void TogetherPcUserAddress(Context context, String uid, ArrayList<HashMap<String, Object>> arrayList) {
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
            if (arrayList.size() > 0) {
                dbr.execSQL("delete from userAdress where _uid='" + uid + "'");
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<String, Object> m = arrayList.get(i);
                    SimpleDateFormat df = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");// 设置日期格式
                    // df.format(new Date());// new Date()为获取当前系统时间

                    dbr.execSQL("insert into userAdress (_id,name,tel,mobile,adress,IsDefault,ModifyTime,_Guid,_uid) values(NULL,?,?,?,?,?,?,?,?)",
                            new Object[]{m.get("adress_name").toString(),
                                    m.get("adress_mobile").toString(),
                                    m.get("adress_mobile").toString(),
                                    m.get("adress_Adress").toString(),
                                    m.get("adress_isdefault").toString(),
                                    df.format(new Date()),
                                    m.get("adress_guid").toString(),
                                    uid});

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 添加留言 liujun 2014-7-15
     */
    @SuppressLint("SimpleDateFormat")
    public void InsertMessage(Context context, String message, String uid, String guid) {

        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
            dbr.execSQL("insert into LeaveMessage(guid,content,uid,upTime,state) values('"
                    + guid
                    + "','"
                    + message
                    + "','"
                    + GetLoginUid(context)
                    + "','" + df.format(new Date()) + "',0)");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 取留言列表 liujun 2014-7-15
     */
    public Cursor GetMessage(Context context, String uid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("LeaveMessage", new String[]{"content", "uid",
                            "state", "Answer", "upTime", "guid"}, " (uid='" + uid
                            + "' or uid='all') and state=0 ", null, null, null,
                    "upTime desc");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 同步PC端留言列表
    public void TogetherPcMessage(Context context,
                                  ArrayList<HashMap<String, Object>> arrayList) {
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
            if (arrayList.size() > 0) {
                dbr.execSQL("delete from LeaveMessage where uid='"
                        + GetLoginUid(context) + "'");
                for (int i = 0; i < arrayList.size(); i++) {
                    HashMap<String, Object> m = arrayList.get(i);

                    dbr.execSQL(
                            "insert into LeaveMessage (content,uid,state,Answer,upTime,guid) values(?,?,?,?,?,?)",
                            new Object[]{m.get("content").toString(),
                                    m.get("uid").toString(), m.get("state"),
                                    m.get("Answer"),
                                    m.get("upTime").toString(), m.get("guid")});

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * 获取消息详细信息 liujun 2014-7-16
     */
    public Cursor GetMsgDetail(Context context, String guid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("LeaveMessage", new String[]{"content", "uid",
                    "state", "Answer", "upTime", "guid"}, " guid='" + guid
                    + "'", null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    /*
     * 修改消息状态 liujun 2014-7-16
     */
    public void updateMsgState(Context context, String guid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        dbr.execSQL("update LeaveMessage set state=1 where guid= '" + guid
                + "'");
    }

    /*
     * 根据guid取留言 判断留言在本地是否存在 liujun 2014-7-17
     */
    public Cursor GetMessageByGuid(Context context, String guid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("LeaveMessage", new String[]{"content", "uid",
                            "state", "Answer", "upTime", "guid"}, " (guid='" + guid
                            + "' or uid='all') and state=0 ", null, null, null,
                    "upTime desc");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 查询购物车
    // liujun 2014-7-18
    public Cursor GetShopCar(Context context, String uid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("ShopCar", new String[]{"productID",
                            "proName", "agentID", "buyPrice", "proImage", "buyCount",
                            "uid", "SupplierLoginID", "shopID",
                            DBHelper.SHOPCAR_ATTRJSON, DBHelper.SHOPCAR_SHOPNAME,
                            DBHelper.SHOPCAR_CREATEUSER}, null,
                    null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 查询用户默认收货地址
    // liujun 2014-7-19
    public Cursor GetDefaultAddress(Context context, String uid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("userAdress", new String[]{"name", "tel",
                            "mobile", "IsDefault", "adress", "_Guid", "ModifyTime"},
                    " _uid='" + uid + "'", null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    private Cursor ProductInShopCar(Context context, String guid, String attrJson, String shopID) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            String[] columns = {"productID",
                    "proName", "agentID", "buyPrice", "proImage", "buyCount",
                    "uid", "SupplierLoginID", "shopID"};
            String selection = null;
            String[] selectionArgs = null;
            boolean isEmpAttr = TextUtils.isEmpty(attrJson);
            selection = "productID='" + guid + "' and " + DBHelper.SHOPCAR_SHOPID + "='" + shopID + "'";
            if (isEmpAttr) {
                selection += " and (" + DBHelper.SHOPCAR_ATTRJSON + " is null or " + DBHelper.SHOPCAR_ATTRJSON + "='')";
            } else {
                selection += " and " + DBHelper.SHOPCAR_ATTRJSON + "='" + attrJson + "'";
            }
            cursor = dbr.query("ShopCar", columns, selection, selectionArgs, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 添加购物车
    // liujun 2014-7-20
    public boolean InsertShopCar(Context context, String productID,
                                 String proName, String agentID, String buyPrice, String image,
                                 String buyCount, String uid, String supplierID, String shopID,
                                 String attrParam, String shopName, String createUser) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            Cursor cursor = ProductInShopCar(context, productID, attrParam, shopID);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                int oldCount = Integer.parseInt(cursor.getString(5));
                int newCount = oldCount + Integer.parseInt(buyCount);
                ContentValues values = new ContentValues();
                values.put("buyCount", newCount);
                values.put("shopID", shopID);
                values.put(DBHelper.SHOPCAR_CREATEUSER, createUser);
                values.put(DBHelper.SHOPCAR_SHOPNAME, shopName);
                String whereClause = "productID='" + productID + "' and uid='" + uid + "' and " + DBHelper.SHOPCAR_SHOPID + "='" + shopID + "'";
                String[] whereArgs = null;
                if (TextUtils.isEmpty(attrParam)) {
                    whereClause += " and (" + DBHelper.SHOPCAR_ATTRJSON + " is null or " + DBHelper.SHOPCAR_ATTRJSON + "='')";
                } else {
                    whereClause += " and " + DBHelper.SHOPCAR_ATTRJSON + "='" + attrParam + "'";
                }
                dbr.update("ShopCar", values, whereClause, whereArgs);
            } else {
                dbr.execSQL(
                        "insert into ShopCar (productID,proName,agentID,buyPrice,proImage,buyCount,uid,SupplierLoginID,shopID,"
                                + DBHelper.SHOPCAR_ATTRJSON + ","
                                + DBHelper.SHOPCAR_SHOPNAME + ","
                                + DBHelper.SHOPCAR_CREATEUSER + ") values(?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{productID, proName, agentID, buyPrice,
                                image, buyCount, uid, supplierID, shopID,
                                attrParam, shopName, createUser});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    // 点击去结算时查出当前购物车中的商品信息 计算数量和总支付金额
    // liujun 2014-7-22
    public Cursor GetPayInfo(Context context, String uid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("ShopCar", new String[]{"productID",
                            "proName", "agentID", "buyPrice", "proImage", "buyCount",
                            "uid", "SupplierLoginID", "shopID"}, " uid='" + uid + "'",
                    null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 删除购物车中的商品
    // liujun 2014-7-22
    public void delProductInCar(Context context, String guid, String uid, String attrParam, String shopID) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        String sql = "delete from ShopCar where productID = '" + guid + "' and " + DBHelper.SHOPCAR_SHOPID + "='" + shopID + "'";
        if (TextUtils.isEmpty(attrParam)) {
            sql += " and (" + DBHelper.SHOPCAR_ATTRJSON + " is null or " + DBHelper.SHOPCAR_ATTRJSON + "='')";
        } else {
            sql += " and " + DBHelper.SHOPCAR_ATTRJSON + "='" + attrParam + "'";
        }
        dbr.execSQL(sql);
        dbr.close();
    }

    // 订单提交成功后， 删除购物车数据
    // liujun 2014-7-22
    public void delShopCar(Context context, String uid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        dbr.execSQL("delete from ShopCar where uid='" + uid + "'");
        dbr.close();
    }

    // 修改购物车中的商品数量
    public void updateProductCountInCar(Context context, String guid, String uid, int count, String attrParam, String shopID) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        String sql = "update ShopCar set buyCount=" + count + " where productID = '" + guid + "' and uid='" + uid + "' and " + DBHelper.SHOPCAR_SHOPID + "='" + shopID + "'";
        if (TextUtils.isEmpty(attrParam)) {
            sql += " and (" + DBHelper.SHOPCAR_ATTRJSON + " is null or " + DBHelper.SHOPCAR_ATTRJSON + "='')";
        } else {
            sql += " and " + DBHelper.SHOPCAR_ATTRJSON + "='" + attrParam + "'";
        }
        dbr.execSQL(sql);
        dbr.close();
    }

    public void updateProductPriceInCar(Context context, String guid, String uid, double price, String attrParam, String shopID) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        String sql = "update ShopCar set buyPrice=" + price + " where productID='" + guid + "' and uid='" + uid + "' and " + DBHelper.SHOPCAR_SHOPID + "='" + shopID + "'";
        if (TextUtils.isEmpty(attrParam)) {
            sql += " and (" + DBHelper.SHOPCAR_ATTRJSON + " is null or " + DBHelper.SHOPCAR_ATTRJSON + "='')";
        } else {
            sql += " and " + DBHelper.SHOPCAR_ATTRJSON + "='" + attrParam + "'";
        }
        dbr.execSQL(sql);
        dbr.close();
    }

    // 根据商品guid判断商品是否已存在于浏览记录表中
    // liujun 2014-7-22
    public Cursor IsExists(Context context, String guid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("NearlyBroswer", new String[]{"productID"},
                    " productID='" + guid + "'", null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 添加最近浏览记录
    // liujun 7-22
    public void InsertNearlyBroswer(Context context, String productID,
                                    String proName, String agentID, String buyPrice, String image,
                                    String uid, String supplierID, String createTime, String detail) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            Cursor cursor = IsExists(context, productID);
            if (cursor.getCount() > 0) {
                cursor.moveToNext();
                dbr.execSQL("update NearlyBroswer set createTime='"
                        + createTime + "'  where productID= '" + productID
                        + "' and uid='" + uid + "'");
            } else {
                dbr.execSQL(
                        "insert into NearlyBroswer (productID,proName,agentID,buyPrice,proImage,uid,SupplierLoginID,createTime,detail) values(?,?,?,?,?,?,?,?,?)",
                        new Object[]{productID, proName, agentID, buyPrice,
                                image, uid, supplierID, createTime, detail});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 查询最近浏览记录
    // liujun 2014-7-22
    public Cursor GetNearlyBroswer(Context context, String uid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            String sql = "select  productID,proName,agentID,buyPrice,proImage,uid,SupplierLoginID,createTime,detail from NearlyBroswer where uid=? order by createTime desc limit 0,5";
            cursor = dbr.rawQuery(sql, new String[]{uid});
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 判断商品是否已经被收藏过了
    // liujun 7-22
    public Cursor allreadyCollectProduct(Context context, String guid,
                                         String uid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("CollectProduct", new String[]{"productID"},
                    " productID='" + guid + "' and uid='" + uid + "'", null,
                    null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 添加收藏商品
    // liujun 7-22
    public void InsertCollectProduct(Context context, String productID,
                                     String proName, String agentID, String buyPrice, String image,
                                     String uid, String supplierID, String detail) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {

            dbr.execSQL(
                    "insert into CollectProduct (productID,proName,agentID,buyPrice,proImage,uid,SupplierLoginID,detail) values(?,?,?,?,?,?,?,?)",
                    new Object[]{productID, proName, agentID, buyPrice,
                            image, uid, supplierID, detail});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 查询收藏的商品
    // liujun 7-22
    public Cursor GetCollectProduct(Context context, String uid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("CollectProduct", new String[]{"productID", "proName", "agentID", "buyPrice", "proImage", "uid", "SupplierLoginID", "detail"}, " uid='" + uid + "'", null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    public Cursor GetCollectProductGuid(Context context, String uid) {
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("CollectProduct", new String[]{"productID"}, " uid='" + uid + "'", null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    public boolean isCollectedPro(Context context, String uid, String guid) {
        try {
            SQLiteDatabase dbr = DBHelper.getInstance(context).getReadableDatabase();
            Cursor cursor = dbr.query("CollectProduct", null, " uid='" + uid + "' and productID='" + guid + "'", null, null, null, null);
            return cursor.getCount() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteCollectProItem(Context context, String guid) {
        return deleteCollectItem(context, "CollectProduct", "productID", guid);
    }

    public boolean deleteCollectItem(Context context, String table, String key, String value) {
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
            dbr.delete(table, key + "=?", new String[]{value});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 判断店铺是否已经被收藏过了
    // liujun 7-23
    public Cursor allreadyCollectShop(Context context, String shopID, String uid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("CollectShop", new String[]{"ShopID", "uid"},
                    " ShopID='" + shopID + "' and uid='" + uid + "'", null,
                    null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 添加收藏店铺
    // liujun 7-22
    public void InsertCollectShop(Context context, String shopID,
                                  String shopName, String address, String mobile, String JYscope,
                                  String baner, String uid) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        // ShopID ,ShopName ,Address ,Mobile ,JYscope ,imgFile,uid
        try {

            dbr.execSQL(
                    "insert into CollectShop (ShopID ,ShopName ,Address ,Mobile ,JYscope ,baner,uid) values(?,?,?,?,?,?,?)",
                    new Object[]{shopID, shopName, address, mobile, JYscope,
                            baner, uid});

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 查询收藏的店铺
    // liujun 7-23
    public Cursor GetCollectShop(Context context, String uid) {
        Cursor cursor = null;

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("CollectShop",
                    new String[]{"ShopID", "ShopName", "Address", "Mobile",
                            "JYscope", "baner", "uid"}, " uid='" + uid + "'",
                    null, null, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cursor;
    }

    // 获取所有省份
    // liujun 2014-7-28
    public String[] getAllProvinces(Context context) {
        String[] columns = {"name"};

        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();

        Cursor cursor = dbr.query("provinces", columns, null, null, null, null,
                null);
        columns = null;
        int count = cursor.getCount();
        String[] provinces = new String[count];
        count = 0;
        while (!cursor.isLast()) {
            cursor.moveToNext();
            provinces[count] = cursor.getString(0);
            count = count + 1;
        }
        cursor.close();
        dbr.close();
        return provinces;
    }

    // 获取城市以及城市的code
    // liujun 2014-7-28
    public List<String[][]> getAllCityAndCode(Context context,
                                              String[] provinces) {
        int length = provinces.length;
        String[][] city = new String[length][];
        String[][] code = new String[length][];
        int count = 0;
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        for (int i = 0; i < length; i++) {
            Cursor cursor = dbr.query("citys", new String[]{"name",
                            "city_num"}, "province_id = ? ",
                    new String[]{String.valueOf(i)}, null, null, null);
            count = cursor.getCount();
            city[i] = new String[count];
            code[i] = new String[count];
            count = 0;
            while (!cursor.isLast()) {
                cursor.moveToNext();
//				String aa = cursor.getString(0);
                city[i][count] = cursor.getString(0);
                code[i][count] = cursor.getString(1);
                count = count + 1;
            }
            cursor.close();
        }
        dbr.close();
        List<String[][]> result = new ArrayList<>();
        result.add(city);
        result.add(code);
        return result;
    }

    // 根据城市名称取code
    // liujun 2014-7-28
    public String getCityCodeByName(Context context, String cityName) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        Cursor cursor = dbr.query("citys", new String[]{"city_num"},
                "name = ? ", new String[]{cityName}, null, null, null);
        String cityCode = null;
        if (!cursor.isLast()) {
            cursor.moveToNext();
            cityCode = cursor.getString(0);
        }
        cursor.close();
        dbr.close();
        return cityCode;
    }

    // 获取一级市
    // liujun 2014-7-28
    public List<String[][]> getCity(Context context, String[] provinces) {
        int length = provinces.length;
        String[][] city = new String[length][];
        String[][] code = new String[length][];
        int count = 0;
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        for (int i = 0; i < length; i++) {
            Cursor cursor = dbr.query("citys", new String[]{"name",
                            "city_num"}, "province_id = ? and name not like '%.%'",
                    new String[]{String.valueOf(i)}, null, null, null);
            count = cursor.getCount();
            city[i] = new String[count];
            code[i] = new String[count];
            count = 0;
            while (!cursor.isLast()) {
                cursor.moveToNext();
//				String aa = cursor.getString(0);
                city[i][count] = cursor.getString(0);
                code[i][count] = cursor.getString(1);
                count = count + 1;
            }
            cursor.close();
        }
        dbr.close();
        List<String[][]> result = new ArrayList<>();
        result.add(city);
        result.add(code);
        return result;
    }

    // 判断数据表是否存在 （导入省份和城市数据时用到）
    // liujun 2014-7-29
    public boolean tabIsExist(Context context, String tabName) {
        boolean result = false;
        if (tabName == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
                    + tabName.trim() + "' ";
            cursor = dbr.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    // 获取互动积分
    // liujun 7-29
    @SuppressLint("SimpleDateFormat")
    public void SethudongScore(Context context, String versionNo, int score, String forUid, String uid) {
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();

            Date date = new Date();
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            String playdate = f.format(date);

            if (!Objects.equals(forUid, "")) {
                Cursor cursor1 = dbr.query("Play_Data", new String[]{"versionNo", "score", "playdate", "ForUid", "uid"}, " versionNo='" + versionNo + "' and playdate='" + playdate + "' and foruid='" + forUid + "'", null, null, null, null);
                if (cursor1.getCount() == 0) { // 帮别人吼
                    // 今天没有帮吼 添加积分
                    dbr.execSQL("insert into Play_Data (versionNo ,score ,playdate ,ForUid ,uid) values(?,?,?,?,?)", new Object[]{versionNo, score, playdate, forUid, uid});
                }

            } else {
                Cursor cursor2 = dbr.query("Play_Data", new String[]{"versionNo", "score", "playdate", "ForUid", "uid"}, " versionNo='" + versionNo + "' and playdate='" + playdate + "' and foruid='' ", null, null, null, null);
                if (cursor2.getCount() == 0) { // 自己吼
                    // 今天没有自己吼 添加积分
                    dbr.execSQL("insert into Play_Data (versionNo ,score ,playdate ,ForUid ,uid) values(?,?,?,?,?)", new Object[]{versionNo, score, playdate, forUid, uid});
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // 将签到存入本地
    public void InsertRegistrationForm(Context context, String id, String time) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            dbr.execSQL("insert into RegistrationForm (uid,time) values ('"
                    + id + "','" + time + "')");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // 查询签到数据库
    public Cursor selectRegistrationForm(Context context, String id) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            String sql = "select * from RegistrationForm where uid = '" + id
                    + "'";
            return dbr.rawQuery(sql, null);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    // 同步积分
    public void updateScore(Context context, String uid, String score) {
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            dbr.execSQL("update userLoginInfo set score='" + score
                    + "' where uid='" + uid + "'");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // 获取所有城市 根据名称首字母排序
    public Cursor getCitys(Context context) {
        ChinaArea dbHelper3 = new ChinaArea(context);
        SQLiteDatabase dbr = dbHelper3.getWritableDatabase();
        try {
            String sql = "select * from chinacity1 ORDER BY sortname";
            return dbr.rawQuery(sql, null);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    // 根据uid判断账号是否已存在于用户登录表中
    // xiaohanxiang 2014-10-24
    public boolean IsUidExists(Context context, String uid) {
        Cursor cursor = null;
        String str = "";

        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
            cursor = dbr.query("userLoginInfo", new String[]{"uId"}, " uId='" + uid + "'", null, null, null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                str = cursor.getString(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return !(str == null || str.equals(""));
    }

    public boolean isLogin(Context context) {
        String uid = GetLoginUid(context);
        Intent intent = new Intent();
        if (uid == null || uid.trim().equals("")) {
            CommonTools.showShortToast(context, "请先登录");
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
            return false;
        }
        return true;
    }

    /**
     *
     * @param context
     * @param pageSize
     * @param startIndex
     * @return
     */
//    sqlitecmd.CommandText = string.Format("select * from GuestInfo order by GuestId limit {0} offset {0}*{1}", size, index-1);//size:每页显示条数，index页码
    //获取指定记录
    public List<JPushBean> getJPushMessage(Context context, String pageSize, String startIndex){
        if(!isLogin(context)){

        }
        Cursor cursor = null;
        List<JPushBean> list=new ArrayList<>();
        try {
            DBHelper dbHelper3 = DBHelper.getInstance(context);
            SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
//
            cursor = dbr.rawQuery("select * from JPushMessage  order by _id desc limit ? offset ?",new String[]{pageSize,startIndex});
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                list.add(new JPushBean(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return list;
    }
    //插入推送记录表
    public void insertJPushMessage(Context context,String title,String message,String pushTime){
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        String sql="insert into JPushMessage(title,message,pushTime) values(?,?,?)";
        dbr.execSQL(sql,new Object[]{title,message,pushTime});
    }

    /**
     * 删除指定推送记录
     * @param context
     * @param _id 推送记录id
     */
    public void deleteJPushMessage(Context context,int _id){
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        String sql="delete from JPushMessage where _id=?";
        dbr.execSQL(sql,new Object[]{_id});
    }

    /**
     * 清空推送记录
     * @param context
     */
    public void deleteAllJPushMessage(Context context){
        DBHelper dbHelper3 = DBHelper.getInstance(context);
        SQLiteDatabase dbr = dbHelper3.getReadableDatabase();
        String sql="delete from JPushMessage";
        dbr.execSQL(sql);
    }


}
