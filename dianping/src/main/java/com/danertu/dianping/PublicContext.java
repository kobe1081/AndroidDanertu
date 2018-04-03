package com.danertu.dianping;


public class PublicContext {

    public static String city;  //记录当前城市

    public static Double lat;
    public static Double lng;

    public static String getLoginUserId(String msg) {
        return msg.substring(msg.indexOf("'MemLoginID':") + 13, msg.indexOf(",", 1));
    }

}
