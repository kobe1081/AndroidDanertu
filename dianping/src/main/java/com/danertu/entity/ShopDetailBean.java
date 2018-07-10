package com.danertu.entity;

import java.util.List;

public class ShopDetailBean {

    /**
     * shopdetails : {"shopbean":[{"realname":"测试42","ShopName":"测试42","TYAddress":"广西南宁市法国g","Mobile":"13557013342","shopdetails":"","shopimg":"","shopshowproduct":"","EntityImage":"2017727143638160.png","ShopJYFW":"炖汤","m":"13557013342","avgscore":"0","plscore":"0","Mobilebanner":"20171019032131644.png","isfood":"no","la":"22286132.000000","leveltype":"0","lt":"113564624.000000","juli":"-1"}]}
     */

    private ShopdetailsBean shopdetails;

    public ShopdetailsBean getShopdetails() {
        return shopdetails;
    }

    public void setShopdetails(ShopdetailsBean shopdetails) {
        this.shopdetails = shopdetails;
    }

    public static class ShopdetailsBean {
        private List<ShopbeanBean> shopbean;

        public List<ShopbeanBean> getShopbean() {
            return shopbean;
        }

        public void setShopbean(List<ShopbeanBean> shopbean) {
            this.shopbean = shopbean;
        }

        public static class ShopbeanBean {
            /**
             * realname : 测试42
             * ShopName : 测试42
             * TYAddress : 广西南宁市法国g
             * Mobile : 13557013342
             * shopdetails :
             * shopimg :
             * shopshowproduct :
             * EntityImage : 2017727143638160.png
             * ShopJYFW : 炖汤
             * m : 13557013342
             * avgscore : 0
             * plscore : 0
             * Mobilebanner : 20171019032131644.png
             * isfood : no
             * la : 22286132.000000
             * leveltype : 0
             * lt : 113564624.000000
             * juli : -1
             */

            private String realname;
            private String ShopName;
            private String TYAddress;
            private String Mobile;
            private String shopdetails;
            private String shopimg;
            private String shopshowproduct;
            private String EntityImage;
            private String ShopJYFW;
            private String m;
            private String avgscore;
            private String plscore;
            private String Mobilebanner;
            private String isfood;
            private String la;
            private String leveltype;
            private String lt;
            private String juli;

            public String getRealname() {
                return realname;
            }

            public void setRealname(String realname) {
                this.realname = realname;
            }

            public String getShopName() {
                return ShopName;
            }

            public void setShopName(String ShopName) {
                this.ShopName = ShopName;
            }

            public String getTYAddress() {
                return TYAddress;
            }

            public void setTYAddress(String TYAddress) {
                this.TYAddress = TYAddress;
            }

            public String getMobile() {
                return Mobile;
            }

            public void setMobile(String Mobile) {
                this.Mobile = Mobile;
            }

            public String getShopdetails() {
                return shopdetails;
            }

            public void setShopdetails(String shopdetails) {
                this.shopdetails = shopdetails;
            }

            public String getShopimg() {
                return shopimg;
            }

            public void setShopimg(String shopimg) {
                this.shopimg = shopimg;
            }

            public String getShopshowproduct() {
                return shopshowproduct;
            }

            public void setShopshowproduct(String shopshowproduct) {
                this.shopshowproduct = shopshowproduct;
            }

            public String getEntityImage() {
                return EntityImage;
            }

            public void setEntityImage(String EntityImage) {
                this.EntityImage = EntityImage;
            }

            public String getShopJYFW() {
                return ShopJYFW;
            }

            public void setShopJYFW(String ShopJYFW) {
                this.ShopJYFW = ShopJYFW;
            }

            public String getM() {
                return m;
            }

            public void setM(String m) {
                this.m = m;
            }

            public String getAvgscore() {
                return avgscore;
            }

            public void setAvgscore(String avgscore) {
                this.avgscore = avgscore;
            }

            public String getPlscore() {
                return plscore;
            }

            public void setPlscore(String plscore) {
                this.plscore = plscore;
            }

            public String getMobilebanner() {
                return Mobilebanner;
            }

            public void setMobilebanner(String Mobilebanner) {
                this.Mobilebanner = Mobilebanner;
            }

            public String getIsfood() {
                return isfood;
            }

            public void setIsfood(String isfood) {
                this.isfood = isfood;
            }

            public String getLa() {
                return la;
            }

            public void setLa(String la) {
                this.la = la;
            }

            public String getLeveltype() {
                return leveltype;
            }

            public void setLeveltype(String leveltype) {
                this.leveltype = leveltype;
            }

            public String getLt() {
                return lt;
            }

            public void setLt(String lt) {
                this.lt = lt;
            }

            public String getJuli() {
                return juli;
            }

            public void setJuli(String juli) {
                this.juli = juli;
            }
        }
    }
}
