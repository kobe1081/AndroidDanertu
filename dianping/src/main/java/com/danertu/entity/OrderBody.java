package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2018/1/25.
 */

public class OrderBody {

    /**
     * orderproductlist : {"orderproductbean":[{"ShopName":"测试42","Guid":"20d7d9ad-4913-4d34-a61e-f757220ad1d8","MarketPrice":"980.00","Name":"豪华室内温泉双人房","CreateUser":"shopnum1","BuyNumber":"1","AgentID":"","Detail":"","SupplierLoginID":"shopnum1","iSGive":"0","tel":"076085883668","ShopPrice":"686.00","SmallImage":"20170424112806442.jpg_300x300.jpg","other1":"2018-02-01","other2":"2018-02-02","attribute":"","ProductRank":[]}]}
     */

    private OrderproductlistBean orderproductlist;

    public OrderproductlistBean getOrderproductlist() {
        return orderproductlist;
    }

    public void setOrderproductlist(OrderproductlistBean orderproductlist) {
        this.orderproductlist = orderproductlist;
    }

    public static class OrderproductlistBean {
        private List<OrderproductbeanBean> orderproductbean;

        public List<OrderproductbeanBean> getOrderproductbean() {
            return orderproductbean;
        }

        public void setOrderproductbean(List<OrderproductbeanBean> orderproductbean) {
            this.orderproductbean = orderproductbean;
        }

        public static class OrderproductbeanBean {
            /**
             * ShopName : 测试42
             * Guid : 20d7d9ad-4913-4d34-a61e-f757220ad1d8
             * MarketPrice : 980.00
             * Name : 豪华室内温泉双人房
             * CreateUser : shopnum1
             * BuyNumber : 1
             * AgentID :
             * Detail :
             * SupplierLoginID : shopnum1
             * iSGive : 0
             * tel : 076085883668
             * ShopPrice : 686.00
             * SmallImage : 20170424112806442.jpg_300x300.jpg
             * other1 : 2018-02-01
             * other2 : 2018-02-02
             * attribute :
             * ProductRank : []
             */

            private String ShopName;
            private String Guid;
            private String MarketPrice;
            private String Name;
            private String CreateUser;
            private String BuyNumber;
            private String AgentID;
            private String Detail;
            private String SupplierLoginID;
            private String iSGive;
            private String tel;
            private String ShopPrice;
            private String SmallImage;
            private String other1;
            private String other2;
            private String attribute;
            private List<?> ProductRank;

            public String getShopName() {
                return ShopName;
            }

            public void setShopName(String ShopName) {
                this.ShopName = ShopName;
            }

            public String getGuid() {
                return Guid;
            }

            public void setGuid(String Guid) {
                this.Guid = Guid;
            }

            public String getMarketPrice() {
                return MarketPrice;
            }

            public void setMarketPrice(String MarketPrice) {
                this.MarketPrice = MarketPrice;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getCreateUser() {
                return CreateUser;
            }

            public void setCreateUser(String CreateUser) {
                this.CreateUser = CreateUser;
            }

            public String getBuyNumber() {
                return BuyNumber;
            }

            public void setBuyNumber(String BuyNumber) {
                this.BuyNumber = BuyNumber;
            }

            public String getAgentID() {
                return AgentID;
            }

            public void setAgentID(String AgentID) {
                this.AgentID = AgentID;
            }

            public String getDetail() {
                return Detail;
            }

            public void setDetail(String Detail) {
                this.Detail = Detail;
            }

            public String getSupplierLoginID() {
                return SupplierLoginID;
            }

            public void setSupplierLoginID(String SupplierLoginID) {
                this.SupplierLoginID = SupplierLoginID;
            }

            public String getISGive() {
                return iSGive;
            }

            public void setISGive(String iSGive) {
                this.iSGive = iSGive;
            }

            public String getTel() {
                return tel;
            }

            public void setTel(String tel) {
                this.tel = tel;
            }

            public String getShopPrice() {
                return ShopPrice;
            }

            public void setShopPrice(String ShopPrice) {
                this.ShopPrice = ShopPrice;
            }

            public String getSmallImage() {
                return SmallImage;
            }

            public void setSmallImage(String SmallImage) {
                this.SmallImage = SmallImage;
            }

            public String getOther1() {
                return other1;
            }

            public void setOther1(String other1) {
                this.other1 = other1;
            }

            public String getOther2() {
                return other2;
            }

            public void setOther2(String other2) {
                this.other2 = other2;
            }

            public String getAttribute() {
                return attribute;
            }

            public void setAttribute(String attribute) {
                this.attribute = attribute;
            }

            public List<?> getProductRank() {
                return ProductRank;
            }

            public void setProductRank(List<?> ProductRank) {
                this.ProductRank = ProductRank;
            }

            @Override
            public String toString() {
                return "OrderproductbeanBean{" +
                        "ShopName='" + ShopName + '\'' +
                        ", Guid='" + Guid + '\'' +
                        ", MarketPrice='" + MarketPrice + '\'' +
                        ", Name='" + Name + '\'' +
                        ", CreateUser='" + CreateUser + '\'' +
                        ", BuyNumber='" + BuyNumber + '\'' +
                        ", AgentID='" + AgentID + '\'' +
                        ", Detail='" + Detail + '\'' +
                        ", SupplierLoginID='" + SupplierLoginID + '\'' +
                        ", iSGive='" + iSGive + '\'' +
                        ", tel='" + tel + '\'' +
                        ", ShopPrice='" + ShopPrice + '\'' +
                        ", SmallImage='" + SmallImage + '\'' +
                        ", other1='" + other1 + '\'' +
                        ", other2='" + other2 + '\'' +
                        ", attribute='" + attribute + '\'' +
                        ", ProductRank=" + ProductRank +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "OrderBody{" +
                "orderproductlist=" + orderproductlist +
                '}';
    }
}
