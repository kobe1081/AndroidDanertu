package com.danertu.entity;

import java.util.List;

public class Messagebean {

    // model columns
    public final static String COL_ID = "id";
    public final static String COL_MESSAGETITLE = "messageTitle";
    public final static String COL_MODIFLYTIME = "ModiflyTime";
    public final static String COL_SUBTITLE = "Subtitle";
    public final static String COL_IMAGE = "Image";

    public static final int NOTICE_TYPE_SYSTEM = 2;
    public static final int NOTICE_TYPE_ORDER = 4;

    /**
     * messageList : {"messagebean":[{"id":"f25323c0-6dbf-4508-bae6-850763383774","messageTitle":"1","ModiflyTime":"2018/4/23 15:42:23","Subtitle":"33","Image":"http://img.danertu.com/member/announcement/20180423154223368.jpg"},{"id":"57a2be69-8bc5-423d-9924-1a145b1ba655","messageTitle":"【致单耳兔网店主】单耳兔APP后台全新升级，新增后台拿货功能，更简单、更方便","ModiflyTime":"2017/11/1 17:48:08","Subtitle":"","Image":""},{"id":"bd289af3-93d0-413d-b525-885d7265295a","messageTitle":"单耳兔0.1元购活动今日17时截止","ModiflyTime":"2015/6/17 11:43:58","Subtitle":"","Image":""}]}
     */

    private String id;
    private String messageTitle;
    private String ModiflyTime;
    private String Subtitle;
    private String Image;
    private int messageType;        //消息类型  4--推送过来的订单，2--图送过来的系统公告.其他值--接口返回的新闻

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getModiflyTime() {
        return ModiflyTime;
    }

    public void setModiflyTime(String modiflyTime) {
        ModiflyTime = modiflyTime;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Messagebean() {
    }

    public Messagebean(String id, String messageTitle, String modiflyTime, String subtitle, String image, int messageType) {
        this.id = id;
        this.messageTitle = messageTitle;
        ModiflyTime = modiflyTime;
        Subtitle = subtitle;
        Image = image;
        this.messageType = messageType;
    }

    @Override
    public String toString() {
        return "Messagebean{" +
                "id='" + id + '\'' +
                ", messageTitle='" + messageTitle + '\'' +
                ", ModiflyTime='" + ModiflyTime + '\'' +
                ", Subtitle='" + Subtitle + '\'' +
                ", Image='" + Image + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
