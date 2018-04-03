package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2017/12/27.
 */

public class StockOrderDetailBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid : 06f6e5f0-a040-4d40-8169-b2ba63afb009
         * MemLoginId : 13557013342
         * BuyNumber : 1
         * ProductName : 法国雾色红酒特惠买三送三
         * ProductGuid : 1490c9c5-327c-496a-9524-d0db87626bed
         * TotalPrice : 414.00
         * SmallImage : 20170829102314617.png_300x300.jpg
         * MarketPrice : 4308.00
         * ShipmentNumber:
         * ShopPrice : 414.00
         * Remark :
         * Name : 测试
         * Mobile : 13557013342
         * Address : 广西省南宁市兴宁区测试测试测试
         * CreateTime : 2017/12/26 14:47:38
         * Freight :
         */

        private String Guid;
        private String MemLoginId;
        private String BuyNumber;
        private String ProductName;
        private String ProductGuid;
        private String TotalPrice;
        private String SmallImage;
        private String MarketPrice;
        private String ShopPrice;
        private String ShipmentNumber;
        private String Remark;
        private String Name;
        private String Mobile;
        private String Address;
        private String CreateTime;
        private String Freight;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String Guid) {
            this.Guid = Guid;
        }

        public String getMemLoginId() {
            return MemLoginId;
        }

        public void setMemLoginId(String MemLoginId) {
            this.MemLoginId = MemLoginId;
        }

        public String getBuyNumber() {
            return BuyNumber;
        }

        public void setBuyNumber(String BuyNumber) {
            this.BuyNumber = BuyNumber;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String ProductName) {
            this.ProductName = ProductName;
        }

        public String getProductGuid() {
            return ProductGuid;
        }

        public void setProductGuid(String ProductGuid) {
            this.ProductGuid = ProductGuid;
        }

        public String getTotalPrice() {
            return TotalPrice;
        }

        public void setTotalPrice(String TotalPrice) {
            this.TotalPrice = TotalPrice;
        }

        public String getSmallImage() {
            return SmallImage;
        }

        public void setSmallImage(String SmallImage) {
            this.SmallImage = SmallImage;
        }

        public String getMarketPrice() {
            return MarketPrice;
        }

        public void setMarketPrice(String MarketPrice) {
            this.MarketPrice = MarketPrice;
        }

        public String getShopPrice() {
            return ShopPrice;
        }

        public void setShopPrice(String ShopPrice) {
            this.ShopPrice = ShopPrice;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String Mobile) {
            this.Mobile = Mobile;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getFreight() {
            return Freight;
        }

        public void setFreight(String Freight) {
            this.Freight = Freight;
        }

        public String getShipmentNumber() {
            return ShipmentNumber;
        }

        public void setShipmentNumber(String shipmentNumber) {
            ShipmentNumber = shipmentNumber;
        }

        @Override
        public String toString() {
            return "ValBean{" +
                    "Guid='" + Guid + '\'' +
                    ", MemLoginId='" + MemLoginId + '\'' +
                    ", BuyNumber='" + BuyNumber + '\'' +
                    ", ProductName='" + ProductName + '\'' +
                    ", ProductGuid='" + ProductGuid + '\'' +
                    ", TotalPrice='" + TotalPrice + '\'' +
                    ", SmallImage='" + SmallImage + '\'' +
                    ", MarketPrice='" + MarketPrice + '\'' +
                    ", ShopPrice='" + ShopPrice + '\'' +
                    ", ShipmentNumber='" + ShipmentNumber + '\'' +
                    ", Remark='" + Remark + '\'' +
                    ", Name='" + Name + '\'' +
                    ", Mobile='" + Mobile + '\'' +
                    ", Address='" + Address + '\'' +
                    ", CreateTime='" + CreateTime + '\'' +
                    ", Freight='" + Freight + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StockOrderDetailBean{" +
                "val=" + val +
                '}';
    }
}
