package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2017/12/26.
 */

public class WareHouseOrderBean {
    private String TotalCount_o;
    private String TotalPageCount_o;
    private List<WareHouseOrderListBean> WareHouseOrderList;

    public static class WareHouseOrderListBean {
        String Guid;
        String MemLoginId;
        String BuyNumber;
        String ProductName;
        String SmallImage;
        String ProductGuid;
        String TotalPrice;
        String MarketPrice;
        String ShopPrice;
        String Mobile;
        String Address;
        String Name;
        String OrderStatus;
        String ShipmentStatus;
        String OrderNumber;
        String CreateTime;
        String Remark;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String guid) {
            Guid = guid;
        }

        public String getMemLoginId() {
            return MemLoginId;
        }

        public void setMemLoginId(String memLoginId) {
            MemLoginId = memLoginId;
        }

        public String getBuyNumber() {
            return BuyNumber;
        }

        public void setBuyNumber(String buyNumber) {
            BuyNumber = buyNumber;
        }

        public String getProductName() {
            return ProductName;
        }

        public void setProductName(String productName) {
            ProductName = productName;
        }

        public String getSmallImage() {
            return SmallImage;
        }

        public void setSmallImage(String smallImage) {
            SmallImage = smallImage;
        }

        public String getProductGuid() {
            return ProductGuid;
        }

        public void setProductGuid(String productGuid) {
            ProductGuid = productGuid;
        }

        public String getTotalPrice() {
            return TotalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            TotalPrice = totalPrice;
        }

        public String getMarketPrice() {
            return MarketPrice;
        }

        public void setMarketPrice(String marketPrice) {
            MarketPrice = marketPrice;
        }

        public String getShopPrice() {
            return ShopPrice;
        }

        public void setShopPrice(String shopPrice) {
            ShopPrice = shopPrice;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String mobile) {
            Mobile = mobile;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String address) {
            Address = address;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getOrderStatus() {
            return OrderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            OrderStatus = orderStatus;
        }

        public String getShipmentStatus() {
            return ShipmentStatus;
        }

        public void setShipmentStatus(String shipmentStatus) {
            ShipmentStatus = shipmentStatus;
        }

        public String getOrderNumber() {
            return OrderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            OrderNumber = orderNumber;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String createTime) {
            CreateTime = createTime;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String remark) {
            Remark = remark;
        }

        @Override
        public String toString() {
            return "WareHouseOrderListBean{" +
                    "Guid='" + Guid + '\'' +
                    ", MemLoginId='" + MemLoginId + '\'' +
                    ", BuyNumber='" + BuyNumber + '\'' +
                    ", ProductName='" + ProductName + '\'' +
                    ", SmallImage='" + SmallImage + '\'' +
                    ", ProductGuid='" + ProductGuid + '\'' +
                    ", TotalPrice='" + TotalPrice + '\'' +
                    ", MarketPrice='" + MarketPrice + '\'' +
                    ", ShopPrice='" + ShopPrice + '\'' +
                    ", Mobile='" + Mobile + '\'' +
                    ", Address='" + Address + '\'' +
                    ", Name='" + Name + '\'' +
                    ", OrderStatus='" + OrderStatus + '\'' +
                    ", ShipmentStatus='" + ShipmentStatus + '\'' +
                    ", OrderNumber='" + OrderNumber + '\'' +
                    ", CreateTime='" + CreateTime + '\'' +
                    ", Remark='" + Remark + '\'' +
                    '}';
        }
    }

    public String getTotalCount_o() {
        return TotalCount_o;
    }

    public void setTotalCount_o(String totalCount_o) {
        TotalCount_o = totalCount_o;
    }

    public String getTotalPageCount_o() {
        return TotalPageCount_o;
    }

    public void setTotalPageCount_o(String totalPageCount_o) {
        TotalPageCount_o = totalPageCount_o;
    }

    public List<WareHouseOrderListBean> getWareHouseOrderList() {
        return WareHouseOrderList;
    }

    public void setWareHouseOrderList(List<WareHouseOrderListBean> wareHouseOrderList) {
        WareHouseOrderList = wareHouseOrderList;
    }

    @Override
    public String toString() {
        return "WareHouseOrderBean{" +
                "TotalCount_o='" + TotalCount_o + '\'' +
                ", TotalPageCount_o='" + TotalPageCount_o + '\'' +
                ", WareHouseOrderListBean=" + WareHouseOrderList +
                '}';
    }
}
