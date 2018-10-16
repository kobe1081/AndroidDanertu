package com.danertu.entity;

import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/8/17 10:13
 * <p>
 * 包名：com.danertu.entity
 * 文件名：AppConfigBean
 * 描述：TODO
 */
public class AppConfigBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * UrlWechatReturn : http://www.danertu.com/PayReturn/WeiPay/App_Notify.aspx
         * UrlWechatReturnWareHouse : http://www.danertu.com/PayReturn/WeiPay/App_Notify_WareHouse.aspx
         * UrlAlipayReturn : https://api.danertu.com:446/AppPayReturn.aspx
         * UrlAlipayReturnWareHouse : https://api.danertu.com:446/AppPayReturnWareHouse.aspx
         * UrlShareHall : http://www.danertu.com/mobile/sharehall/list.aspx?issharehall=1&platform=android
         * UrlWareHouseRules : https://appweb.danertu.com:8444/articlescrap/articlescrap_tunhuo_protocol.html?from=order
         * UrlImgDomain : http://img.danertu.com/
         * UrlShopShare : http://115.28.55.222:8085/doShare.aspx?type=s&shopid=
         * UrlAndroidUpdateFull : http://www.danertu.com/download/danertu.apk
         * UrlAndroidUpdateDifference : http://www.danertu.com/download/danertu-patch.apk
         */

        private String UrlWechatReturn;
        private String UrlWechatReturnWareHouse;
        private String UrlAlipayReturn;
        private String UrlAlipayReturnWareHouse;
        private String UrlShareHall;
        private String UrlWareHouseRules;
        private String UrlImgDomain;
        private String UrlShopShare;
        private String UrlAndroidUpdateFull;
        private String UrlAndroidUpdateDifference;
        private String UrlAnnouncementDetail;
        private String UrlExpressQuery;
        private String UrlTicketProductDetail;


        public String getUrlTicketProductDetail() {
            return UrlTicketProductDetail;
        }

        public void setUrlTicketProductDetail(String urlTicketProductDetail) {
            UrlTicketProductDetail = urlTicketProductDetail;
        }

        public String getUrlExpressQuery() {
            return UrlExpressQuery;
        }

        public void setUrlExpressQuery(String urlExpressQuery) {
            UrlExpressQuery = urlExpressQuery;
        }

        public String getUrlAnnouncementDetail() {
            return UrlAnnouncementDetail;
        }

        public void setUrlAnnouncementDetail(String urlAnnouncementDetail) {
            UrlAnnouncementDetail = urlAnnouncementDetail;
        }

        public String getUrlWechatReturn() {
            return UrlWechatReturn;
        }

        public void setUrlWechatReturn(String UrlWechatReturn) {
            this.UrlWechatReturn = UrlWechatReturn;
        }

        public String getUrlWechatReturnWareHouse() {
            return UrlWechatReturnWareHouse;
        }

        public void setUrlWechatReturnWareHouse(String UrlWechatReturnWareHouse) {
            this.UrlWechatReturnWareHouse = UrlWechatReturnWareHouse;
        }

        public String getUrlAlipayReturn() {
            return UrlAlipayReturn;
        }

        public void setUrlAlipayReturn(String UrlAlipayReturn) {
            this.UrlAlipayReturn = UrlAlipayReturn;
        }

        public String getUrlAlipayReturnWareHouse() {
            return UrlAlipayReturnWareHouse;
        }

        public void setUrlAlipayReturnWareHouse(String UrlAlipayReturnWareHouse) {
            this.UrlAlipayReturnWareHouse = UrlAlipayReturnWareHouse;
        }

        public String getUrlShareHall() {
            return UrlShareHall;
        }

        public void setUrlShareHall(String UrlShareHall) {
            this.UrlShareHall = UrlShareHall;
        }

        public String getUrlWareHouseRules() {
            return UrlWareHouseRules;
        }

        public void setUrlWareHouseRules(String UrlWareHouseRules) {
            this.UrlWareHouseRules = UrlWareHouseRules;
        }

        public String getUrlImgDomain() {
            return UrlImgDomain;
        }

        public void setUrlImgDomain(String UrlImgDomain) {
            this.UrlImgDomain = UrlImgDomain;
        }

        public String getUrlShopShare() {
            return UrlShopShare;
        }

        public void setUrlShopShare(String UrlShopShare) {
            this.UrlShopShare = UrlShopShare;
        }

        public String getUrlAndroidUpdateFull() {
            return UrlAndroidUpdateFull;
        }

        public void setUrlAndroidUpdateFull(String UrlAnroidUpdateFull) {
            this.UrlAndroidUpdateFull = UrlAnroidUpdateFull;
        }

        public String getUrlAndroidUpdateDifference() {
            return UrlAndroidUpdateDifference;
        }

        public void setUrlAndroidUpdateDifference(String urlAndroidUpdateDifference) {
            this.UrlAndroidUpdateDifference = urlAndroidUpdateDifference;
        }
    }
}
