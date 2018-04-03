package com.danertu.tools;

import android.content.Context;

import cn.xiaoneng.coreapi.ChatParamsBody;
import cn.xiaoneng.coreapi.TrailActionBody;
import cn.xiaoneng.uiapi.Ntalker;
import cn.xiaoneng.utils.CoreData;
import cn.xiaoneng.utils.MyUtil;

import com.danertu.widget.CommonTools;

public class XNUtil {
    private Context context;
    /*******************************************集成SDK相关参数如下***********************************************/
    public static final  String appkey = "2E15B6D9-FEC6-4B06-8B19-8E80D7CD7AF7";
    // 用户
    String userid = android.os.Build.BRAND;// 顾客id，Mi3FG，Mi3ZHC，HTC，HUAWEI，SumSung,MEIZU
    String username = "测试账号";// 顾客名字
    // 客服
    public static final String siteid = "dr_1000";
    String settingid = "dr_1000_1444459107870";// 接待组id(设置 ——> 配置管理)
    String group = "普通客服组";// 客服组名称
    String erpparam = "我是erp";// 商品ERP信息,需提供API支持,URL
    // 交互（客服发送，顾客点击）
    String customerConent = "";// 客服发送链接打开特定应用位置的条件
    String targetname = "";// 客服发送链接打开特定应用位置的Activity框架
    // 轨迹（专有参数）
    String title = "女装/女士精品 - 服饰 - 搜索店铺 - ECMall演示站";// 当前页标题
    int userlevel = 0;// 用户级别
    String orderid = "";// 订单ID
    String orderprice = "";// 订单价格
    String ts = "";// 当前时间戳
    String otherparam = "";// 页面特定参数（购物车（商品列表）订单页（ID）商品终端页 搜索页（搜索关键词））
    // 商品
    ChatParamsBody itemparam = null;

    /*******************************************************************************************************/

    public XNUtil(Context context) {
        this.context = context;
    }

    /**
     * @param userid    顾客id
     * @param username  顾客名字
     * @param sellerid  商户id
     * @param settingid 接待组id
     * @param groupName 客服组名称
     * @param proParam  商品 json
     */
    public void setCommunicateParam(String userid, String username,
                                    String sellerid, String settingid, String groupName, ChatParamsBody proParam) {
        this.userid = userid;
        this.username = username;
        this.settingid = settingid;
        this.group = groupName;
        this.itemparam = proParam;
    }

    public String getTestProParam() {
        // 商品
        return "{"
                + "\"appgoodsinfo_type\"" + ":" + "\"3\"" + ","       //0:null(不展示)  1:id  2:url  3:json
                + "\"clientgoodsinfo_type\"" + ":" + "\"2\"" + ","    //0:null(不展示)  1:id  2:url
                + "\"goods_id\"" + ":" + "\"50094233\"" + ","             //商品ID  50023069,50094233
                + "\"goods_name\"" + ":" + "\"2015年最新潮流时尚T恤偶像同款一二线城市包邮 速度抢购有超级大礼包等你来拿\"" + ","           //商品名称
                + "\"goods_price\"" + ":" + "\"￥：188\"" + ","          //商品价格
                + "\"goods_image\"" + ":" + "\"http://img.meicicdn.com/p/51/050010/h-050010-1.jpg\"" + ","    //商品图片地址
                + "\"goods_url\"" + ":" + "\"http://xxx.com/goods?id=1\"" + ","    //该商品在网站上的实际页面地址。
                + "\"goods_showurl\"" + ":" + "\"http://pic.shopex.cn/pictures/goodsdetail/29b.jpg?rnd=111111\""   //客服端显示的商品页面地址
                + "}";
    }

    /**
     * @param proId
     * @param proName
     * @param price    商品价格
     * @param imgUrl   商品图片地址
     * @param goodsUrl 该商品在网站上的实际页面地址。
     * @param showUrl  客服端显示的商品页面地址
     * @return 商品信息体
     */
    public ChatParamsBody genProParam(String proId, String proName, double price, String imgUrl, String goodsUrl, String showUrl) {
        ChatParamsBody chatparams = new ChatParamsBody();
        // 咨询发起页（专有参数）
        chatparams.startPageTitle = proName;
        chatparams.startPageUrl = imgUrl;
        // 文本匹配参数
        chatparams.matchstr = "";
        // erp参数
        chatparams.erpParam = "";
        // 商品展示（专有参数）
        chatparams.itemparams.appgoodsinfo_type = CoreData.SHOW_GOODS_BY_ID;
        chatparams.itemparams.clientgoodsinfo_type = CoreData.SHOW_GOODS_BY_ID;
        chatparams.itemparams.clicktoshow_type = CoreData.CLICK_TO_APP_COMPONENT;
        chatparams.itemparams.itemparam = "";//
        chatparams.itemparams.goods_id = proId;// ntalker_test
        chatparams.itemparams.goods_name = proName;
        chatparams.itemparams.goods_price = "￥：" + String.format("%.2f", price);
        chatparams.itemparams.goods_image = imgUrl;
        chatparams.itemparams.goods_url = goodsUrl;
        chatparams.itemparams.goods_showurl = showUrl;
        return chatparams;
    }

    private final String tag = "test_xn";

    public void communicte() {
        Logger.i(tag, "appkey:" + appkey + ", " + "userid:" + userid + ", " + "username:" + username + ", " + "settingid:" + settingid + "," + "group:" + group + ", " + "itemparam:" + itemparam + "," + "erpparam:" + erpparam + ", " + "customerConent:" + customerConent + "," + "targetname:" + targetname);
        int login = Ntalker.getInstance().login(userid, username, userlevel);
        if (login != 0) {
            CommonTools.showShortToast(context, "客服登录系统故障，请稍后再试");
            return;
        }
        postCustomerTrack();

        Ntalker.getInstance().startChat(context, settingid, group, null, null, itemparam);
    }

    public void setCustomerTrackParam(String title, String userName) {
        this.title = title;
        this.username = userName;
    }

    /**
     * 发送顾客浏览轨迹
     */
    public void postCustomerTrack() {
        clearXNCache();
        Logger.i(tag, "title:" + title + ", " + "username:" + username + ", " + "userlevel:" + userlevel + ", " + "orderid:" + orderid + "," + "orderprice:" + orderprice + ", " + "ts:" + ts + "," + "otherparam:" + otherparam);
        ChatParamsBody cpb = this.itemparam;
        if (cpb == null)
            return;
        TrailActionBody trailParams = new TrailActionBody();

        trailParams.ttl = cpb.itemparams.goods_name;
        trailParams.url = cpb.itemparams.goods_url;
        trailParams.ref = "";
        trailParams.orderid = orderid;
        trailParams.orderprice = orderprice;
        trailParams.isvip = 0;
        trailParams.userlevel = userlevel;


        int action = Ntalker.getInstance().startAction(trailParams);
        if (0 == action) {
            Logger.e(tag, "上传轨迹成功");
        } else {
            Logger.e(tag, "上传轨迹失败，错误码:" + action);
        }

//		XNMethod.getCustomerTrack(context, title, sellerid, username,
//				userlevel, orderid, orderprice, ts, otherparam);
    }

    /**
     * 清空本地缓存
     */
    public void clearXNCache() {
//		XNMethod.clearXNCache(context);
        Ntalker.getInstance().clearCache();
        Ntalker.getInstance().logout();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSettingid() {
        return settingid;
    }

    public void setSettingid(String settingid) {
        this.settingid = settingid;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getErpparam() {
        return erpparam;
    }

    public void setErpparam(String erpparam) {
        this.erpparam = erpparam;
    }

    public String getCustomerConent() {
        return customerConent;
    }

    public void setCustomerConent(String customerConent) {
        this.customerConent = customerConent;
    }

    public String getTargetname() {
        return targetname;
    }

    public void setTargetname(String targetname) {
        this.targetname = targetname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserlevel() {
        return userlevel;
    }

    public void setUserlevel(int userlevel) {
        this.userlevel = userlevel;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(String orderprice) {
        this.orderprice = orderprice;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getOtherparam() {
        return otherparam;
    }

    public void setOtherparam(String otherparam) {
        this.otherparam = otherparam;
    }

    public ChatParamsBody getItemparam() {
        return itemparam;
    }

    public void setItemparam(ChatParamsBody itemparam) {
        this.itemparam = itemparam;
    }

}
