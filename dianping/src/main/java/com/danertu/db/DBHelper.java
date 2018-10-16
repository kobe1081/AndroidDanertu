package com.danertu.db;

import com.danertu.tools.Logger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 13;
    public static final String DB_NAME = "danertu_db";
    private static DBHelper dbHelper = null;
    public static final String TABLE_USER_INFO = "userLoginInfo";
    public static final String TABLE_SHOPCAR = "ShopCar";

    //三个不同参数的构造函数
    //带全部参数的构造函数，此构造函数必不可少
    private DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null)
            dbHelper = new DBHelper(context);
        return dbHelper;
    }

    public void createUserLoginInfo(SQLiteDatabase db) {
        String sql = "create table userLoginInfo(_id integer primary key autoincrement,"
                + "uId text,"
                + "pwd text,"
                + "email text,"
                + "score text,"
                + "loginTime DATETIME,"
                + "isLogin int,"
                + "nickname char(20),"
                + "token text,"
                + "headimgurl text)";
        db.execSQL(sql);
    }

    public void createUserAdress(SQLiteDatabase db) {
        String sql = "create table userAdress(_id integer primary key autoincrement," +
                "name text," +
                "tel text," +
                "mobile text," +
                "adress text," +
                "IsDefault text," +
                "ModifyTime text," +
                "_Guid text," +
                "_uid text)";
        db.execSQL(sql);
    }

    /**
     * 创建留言表
     * liujun
     * 2014-7-15
     */
    public void createLeaveMessage(SQLiteDatabase db) {
        String sql = "create table LeaveMessage(guid text,content text,uid text,state int,Answer text,upTime text)";
        db.execSQL(sql);
    }

    public static final String SHOPCAR_SHOPID = "shopID";
    /**
     * 新增字段2016-10-26
     */
    public static final String SHOPCAR_ATTRJSON = "attrJson";
    public static final String SHOPCAR_SHOPNAME = "shopName";
    public static final String SHOPCAR_CREATEUSER = "CreateUser";

    public void createShopCar(SQLiteDatabase db) {
        //创建购物车表
        //liujun  2014-7-18
        String sql = "create table ShopCar(productID text,"
                + "proName text,"
                + "agentID text,"
                + "buyPrice text,"
                + "proImage text,"
                + "buyCount int,"
                + "uid text,"
                + "SupplierLoginID text,"
                + SHOPCAR_SHOPID + " text,"
                + SHOPCAR_ATTRJSON + " text,"
                + SHOPCAR_SHOPNAME + " text,"
                + SHOPCAR_CREATEUSER + " text)";
        db.execSQL(sql);
    }

    public void createNearlyBroswer(SQLiteDatabase db) {
        //创建最近浏览器记录表
        //liujun  2014-7-22
        String sql = "create table NearlyBroswer(" +
                "productID text," +
                "proName text," +
                "agentID text," +
                "buyPrice text," +
                "proImage text," +
                "uid text," +
                "SupplierLoginID text," +
                "createTime text," +
                "detail text)";
        db.execSQL(sql);
    }

    public void createCollectProduct(SQLiteDatabase db) {
        //创建收藏商品表
        //liujun  7-22
        String sql = "create table CollectProduct(" +
                "productID text,proName text," +
                "agentID text," +
                "buyPrice text," +
                "proImage text," +
                "uid text," +
                "SupplierLoginID text," +
                "detail text)";
        db.execSQL(sql);
    }

    public void createCollectShop(SQLiteDatabase db) {
        //创建收藏店铺表
        //liujun 7-23
        String sql = "create table CollectShop(" +
                "ShopID text," +
                "ShopName text," +
                "Address text," +
                "Mobile text," +
                "JYscope text," +
                "baner text," +
                "uid text)";
        db.execSQL(sql);
    }

    public void createPlay_Data(SQLiteDatabase db) {
        //创建互动积分表
        //liujun 2014-7-29
        String sql = "create table Play_Data(" +
                "versionNo text," +
                "score int," +
                "playdate text," +
                "ForUid text," +
                "uid text)";
        db.execSQL(sql);
    }

    public void createRegistrationForm(SQLiteDatabase db) {
        //创建签到记录表
        String sql = "create table RegistrationForm(uid text,time text)";
        db.execSQL(sql);
    }

    /**
     * 记录消息中心的推送记录，
     * 跟账号有关
     *
     * @param db
     */
    public void createJPushMessageRecord(SQLiteDatabase db) {
        //创建极光推送记录表
        String sql = "create table JPushMessage(" +
                "_id integer primary key autoincrement," +
                "uid text," +
                "Title text," +
                "Content text," +
                "ToRank text," +
                "Link text," +
                "Method text," +
                "pushTime long)";
        db.execSQL(sql);
    }

    public void createNoticeRecord(SQLiteDatabase db) {
        //创建推送记录表
        String sql = "create table tb_notice(" +
                "_id integer primary key autoincrement," +
                "uid text," +
                "image text," +
                "title text," +
                "subtitle text," +
                "option text," +
                "pushTime text)";
        db.execSQL(sql);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        //	Log.i(SWORD,"onCreate a Database");
        //创建数据库sql语句
        try {
            createUserLoginInfo(db);
            createUserAdress(db);
            createLeaveMessage(db);
            createShopCar(db);
            createNearlyBroswer(db);
            createCollectProduct(db);
            createCollectShop(db);
            createPlay_Data(db);
            createRegistrationForm(db);
            createJPushMessageRecord(db);
            createNoticeRecord(db);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //创建成功，日志输出提示
        int curVersion = 9;
        if (oldVersion < curVersion || newVersion == curVersion) {
            String[] columns = {"nickname char(20)", "headimgurl text"};
            addColumns(db, TABLE_USER_INFO, columns);
        }

        curVersion = 10;
        if (oldVersion < curVersion || newVersion == curVersion) {
            String[] columns = {SHOPCAR_ATTRJSON + " text", SHOPCAR_SHOPNAME + " text", SHOPCAR_CREATEUSER + " text"};
            addColumns(db, TABLE_SHOPCAR, columns);
        }
        curVersion = 11;
        if (oldVersion < curVersion || newVersion == curVersion) {
            //创建推送的通知表
            createJPushMessageRecord(db);
        }
        curVersion = 12;
        if (oldVersion < curVersion || newVersion == curVersion) {
            //创建推送的通知表
            createNoticeRecord(db);
        }

        /**
         * 2018年7月31日
         * 给用户表添加token字段
         */
        curVersion = 13;
        if (oldVersion < curVersion || newVersion == curVersion) {
            addColumns(db, "userLoginInfo", new String[]{"token text"});
        }

        Logger.e("onUpgrade", "old:" + oldVersion + ", new:" + newVersion);
    }

    private void addColumns(SQLiteDatabase db, String table, String[] columns) {
        for (String item : columns) {
            String addColumn = "alter table " + table + " add " + item + ";";
            db.execSQL(addColumn);
        }
    }

    private Cursor ExecSQLForCursor(String sql, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        return db.rawQuery(sql, selectionArgs);
    }

    // 查询照片信息
    public Cursor searchPhoto(int row, String sort) {
        String sql = "select * from userLoginInfo";
        return ExecSQLForCursor(sql, null);
    }

}