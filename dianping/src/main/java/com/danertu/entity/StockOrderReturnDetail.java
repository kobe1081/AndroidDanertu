package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2018/1/3.
 */

public class StockOrderReturnDetail {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid:8ff0f033-15a2-4d5a-a039-b967897917c1
         * MemLoginId : 15015007777
         * Address : 哪都行
         * BuyNumber : 1
         * CreateTime : 2017/12/19 14:23:58
         * MarketPrice : 4308.00
         * ProductGuid : 1490c9c5-327c-496a-9524-d0db87626bed
         * ProductName : 法国雾色红酒特惠买三送三
         * Remark : hehe
         * Mobile : 15015007778
         * Name : 小明傻逼
         * ShopPrice : 414.00
         * SmallImage : 20170829102314617.png_300x300.jpg
         * WareHouseOrderFreight :
         * ShouldPayPrice : 414.00
         * TotalPrice : 4140.00
         * WareHouseGuid : 91F9EBC5-19BE-4275-9796-D95731D78FEA
         * WareHouseOrderReturnFreight : 30.00
         * OrderNumber : 201801034248611
         * ShipmentStatus : 4
         * OrderStatus : 5
         * PayStatus:2
         */
        private String Guid;
        private String MemLoginId;
        private String Address;
        private String BuyNumber;
        private String CreateTime;
        private String MarketPrice;
        private String ProductGuid;
        private String ProductName;
        private String Remark;
        private String Mobile;
        private String Name;
        private String ShopPrice;
        private String SmallImage;
        private String WareHouseOrderFreight;
        private String ShouldPayPrice;
        private String TotalPrice;
        private String WareHouseGuid;
        private String WareHouseOrderReturnFreight;
        private String OrderNumber;
        private String ShipmentStatus;
        private String OrderStatus;
        private String PayStatus;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String guid) {
            Guid = guid;
        }

        public String getMemLoginId() {
            return MemLoginId;
        }

        public void setMemLoginId(String MemLoginId) {
            this.MemLoginId = MemLoginId;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getBuyNumber() {
            return BuyNumber;
        }

        public void setBuyNumber(String BuyNumber) {
            this.BuyNumber = BuyNumber;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getMarketPrice() {
            return MarketPrice;
        }

        public void setMarketPrice(String MarketPrice) {
            this.MarketPrice = MarketPrice;
        }

        public String getProductGuid() {
            return ProductGuid;
        }

        public void setProductGuid(String ProductGuid) {
            this.ProductGuid = ProductGuid;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String ProductName) {
            this.ProductName = ProductName;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String Mobile) {
            this.Mobile = Mobile;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
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

        public String getWareHouseOrderFreight() {
            return WareHouseOrderFreight;
        }

        public void setWareHouseOrderFreight(String WareHouseOrderFreight) {
            this.WareHouseOrderFreight = WareHouseOrderFreight;
        }

        public String getShouldPayPrice() {
            return ShouldPayPrice;
        }

        public void setShouldPayPrice(String ShouldPayPrice) {
            this.ShouldPayPrice = ShouldPayPrice;
        }

        public String getTotalPrice() {
            return TotalPrice;
        }

        public void setTotalPrice(String TotalPrice) {
            this.TotalPrice = TotalPrice;
        }

        public String getWareHouseGuid() {
            return WareHouseGuid;
        }

        public void setWareHouseGuid(String WareHouseGuid) {
            this.WareHouseGuid = WareHouseGuid;
        }

        public String getWareHouseOrderReturnFreight() {
            return WareHouseOrderReturnFreight;
        }

        public void setWareHouseOrderReturnFreight(String WareHouseOrderReturnFreight) {
            this.WareHouseOrderReturnFreight = WareHouseOrderReturnFreight;
        }

        public String getOrderNumber() {
            return OrderNumber;
        }

        public void setOrderNumber(String OrderNumber) {
            this.OrderNumber = OrderNumber;
        }

        public String getShipmentStatus() {
            return ShipmentStatus;
        }

        public void setShipmentStatus(String ShipmentStatus) {
            this.ShipmentStatus = ShipmentStatus;
        }

        public String getOrderStatus() {
            return OrderStatus;
        }

        public void setOrderStatus(String OrderStatus) {
            this.OrderStatus = OrderStatus;
        }

        public String getPayStatus() {
            return PayStatus;
        }

        public void setPayStatus(String payStatus) {
            PayStatus = payStatus;
        }

        @Override
        public String toString() {
            return "ValBean{" +
                    "Guid='" + Guid + '\'' +
                    ", MemLoginId='" + MemLoginId + '\'' +
                    ", Address='" + Address + '\'' +
                    ", BuyNumber='" + BuyNumber + '\'' +
                    ", CreateTime='" + CreateTime + '\'' +
                    ", MarketPrice='" + MarketPrice + '\'' +
                    ", ProductGuid='" + ProductGuid + '\'' +
                    ", ProductName='" + ProductName + '\'' +
                    ", Remark='" + Remark + '\'' +
                    ", Mobile='" + Mobile + '\'' +
                    ", Name='" + Name + '\'' +
                    ", ShopPrice='" + ShopPrice + '\'' +
                    ", SmallImage='" + SmallImage + '\'' +
                    ", WareHouseOrderFreight='" + WareHouseOrderFreight + '\'' +
                    ", ShouldPayPrice='" + ShouldPayPrice + '\'' +
                    ", TotalPrice='" + TotalPrice + '\'' +
                    ", WareHouseGuid='" + WareHouseGuid + '\'' +
                    ", WareHouseOrderReturnFreight='" + WareHouseOrderReturnFreight + '\'' +
                    ", OrderNumber='" + OrderNumber + '\'' +
                    ", ShipmentStatus='" + ShipmentStatus + '\'' +
                    ", OrderStatus='" + OrderStatus + '\'' +
                    ", PayStatus='" + PayStatus + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StockOrderReturnDetail{" +
                "val=" + val +
                '}';
    }
}
