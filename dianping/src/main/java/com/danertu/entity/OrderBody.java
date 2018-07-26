package com.danertu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Viz on 2018/1/25.
 */

/**
 * ShopName : 测试42
 * Guid : dd70d3e2-580b-4531-90d1-07e04ef8cced
 * MarketPrice : 336.00
 * Name : 土豪醇 窖藏 500ml〖单支〗
 * CreateUser : chunkang
 * BuyNumber : 1
 * AgentID :
 * Detail :
 * SupplierLoginID :
 * iSGive : 0
 * tel :
 * ShopPrice : 208.00
 * SmallImage : 20170817170738450.jpg_300x300.jpg
 * other1 :
 * other2 :
 * attribute :
 * ProductRank : [{"DiscountBuyNum":"10","DiscountPrice":"100.00"}]  //后台拿货才有的值
 */
public class OrderBody implements Parcelable {

    /**
     * orderproductlist : {"orderproductbean":[{"ShopName":"测试42","Guid":"dd70d3e2-580b-4531-90d1-07e04ef8cced","MarketPrice":"336.00","Name":"土豪醇 窖藏 500ml〖单支〗","CreateUser":"chunkang","BuyNumber":"1","AgentID":"","Detail":"","SupplierLoginID":"","iSGive":"0","tel":"","ShopPrice":"208.00","SmallImage":"20170817170738450.jpg_300x300.jpg","other1":"","other2":"","attribute":"","ProductRank":[{"DiscountBuyNum":"10","DiscountPrice":"100.00"}]}]}
     */

    private OrderproductlistBean orderproductlist;

    public OrderproductlistBean getOrderproductlist() {
        return orderproductlist;
    }

    public void setOrderproductlist(OrderproductlistBean orderproductlist) {
        this.orderproductlist = orderproductlist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static class OrderproductlistBean implements Parcelable {
        private List<OrderproductbeanBean> orderproductbean;

        public List<OrderproductbeanBean> getOrderproductbean() {
            return orderproductbean;
        }

        public void setOrderproductbean(List<OrderproductbeanBean> orderproductbean) {
            this.orderproductbean = orderproductbean;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

        }

        public static class OrderproductbeanBean implements Parcelable {
            /**
             * ShopName : 测试42
             * Guid : dd70d3e2-580b-4531-90d1-07e04ef8cced
             * MarketPrice : 336.00
             * Name : 土豪醇 窖藏 500ml〖单支〗
             * CreateUser : chunkang
             * BuyNumber : 1
             * AgentID :
             * Detail :
             * SupplierLoginID :
             * iSGive : 0
             * tel :
             * ShopPrice : 208.00
             * SmallImage : 20170817170738450.jpg_300x300.jpg
             * other1 :
             * other2 :
             * attribute :
             * ProductRank : [{"DiscountBuyNum":"10","DiscountPrice":"100.00"}]  //后台拿货才有的值
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
            private List<ProductRankBean> ProductRank;

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

            public List<ProductRankBean> getProductRank() {
                return ProductRank;
            }

            public void setProductRank(List<ProductRankBean> ProductRank) {
                this.ProductRank = ProductRank;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }

            public static class ProductRankBean implements Parcelable {
                /**
                 * DiscountBuyNum : 10
                 * DiscountPrice : 100.00
                 */

                private String DiscountBuyNum;
                private String DiscountPrice;

                public String getDiscountBuyNum() {
                    return DiscountBuyNum;
                }

                public void setDiscountBuyNum(String DiscountButNum) {
                    this.DiscountBuyNum = DiscountButNum;
                }

                public String getDiscountPrice() {
                    return DiscountPrice;
                }

                public void setDiscountPrice(String DiscountPrice) {
                    this.DiscountPrice = DiscountPrice;
                }

                @Override
                public int describeContents() {
                    return 0;
                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {

                }
            }
        }
    }
}
