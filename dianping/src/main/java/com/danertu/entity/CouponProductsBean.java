package com.danertu.entity;

import java.util.List;

/**
 * Guid : 8cd6ff8d-fe66-496d-89f9-2696ee82f113
 * Name : 泉眼温泉微信活动赠送 平日票1张
 * SmallImage : 20170810114640344.jpg_300x300.jpg
 * ShopPrice : 0.10
 * MarketPrice : 0.10
 * SupplierLoginID : shopnum1
 * ShopId : 13557013342
 * AppointProductUrl : ticket_route.html?  产品地址
 * AppointProductType : 1  1-成人票、儿童票  2-团体票  3-客房
 * ProductJumpType : 1      --0:一般商品，1-温泉产品
 * AgentID :
 */
public class CouponProductsBean {

    private List<ProductListBean> ProductList;

    public List<ProductListBean> getProductList() {
        return ProductList;
    }

    public void setProductList(List<ProductListBean> ProductList) {
        this.ProductList = ProductList;
    }

    public static class ProductListBean {
        /**
         * Guid : 8cd6ff8d-fe66-496d-89f9-2696ee82f113
         * Name : 泉眼温泉微信活动赠送 平日票1张
         * SmallImage : 20170810114640344.jpg_300x300.jpg
         * ShopPrice : 0.10
         * MarketPrice : 0.10
         * SupplierLoginID : shopnum1
         * ShopId : 13557013342
         * AppointProductUrl : ticket_route.html?
         * AppointProductType : 1
         * ProductJumpType : 1
         * AgentID :
         */

        private String Guid;
        private String Name;
        private String SmallImage;
        private String ShopPrice;
        private String MarketPrice;
        private String SupplierLoginID;
        private String ShopId;
        private String AppointProductUrl;
        private String AppointProductType;
        private String ProductJumpType;
        private String AgentID;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String Guid) {
            this.Guid = Guid;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getSmallImage() {
            return SmallImage;
        }

        public void setSmallImage(String SmallImage) {
            this.SmallImage = SmallImage;
        }

        public String getShopPrice() {
            return ShopPrice;
        }

        public void setShopPrice(String ShopPrice) {
            this.ShopPrice = ShopPrice;
        }

        public String getMarketPrice() {
            return MarketPrice;
        }

        public void setMarketPrice(String MarketPrice) {
            this.MarketPrice = MarketPrice;
        }

        public String getSupplierLoginID() {
            return SupplierLoginID;
        }

        public void setSupplierLoginID(String SupplierLoginID) {
            this.SupplierLoginID = SupplierLoginID;
        }

        public String getShopId() {
            return ShopId;
        }

        public void setShopId(String ShopId) {
            this.ShopId = ShopId;
        }

        public String getAppointProductUrl() {
            return AppointProductUrl;
        }

        public void setAppointProductUrl(String AppointProductUrl) {
            this.AppointProductUrl = AppointProductUrl;
        }

        public String getAppointProductType() {
            return AppointProductType;
        }

        public void setAppointProductType(String AppointProductType) {
            this.AppointProductType = AppointProductType;
        }

        public String getProductJumpType() {
            return ProductJumpType;
        }

        public void setProductJumpType(String ProductJumpType) {
            this.ProductJumpType = ProductJumpType;
        }

        public String getAgentID() {
            return AgentID;
        }

        public void setAgentID(String AgentID) {
            this.AgentID = AgentID;
        }
    }
}
