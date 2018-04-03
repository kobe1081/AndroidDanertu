package com.danertu.entity;

import java.util.List;

/**
 * 入货记录实体类
 * Created by Viz on 2017/12/27.
 */

public class ProductInRecordBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * ProductGuid : c57c4548-b023-47c7-a567-16c51f57e6ef
         * CreateTime : 2017/12/27 14:16:56
         * BuyNumber : 10
         * TotalPrice : 7900.00
         * orderType:
         */

        private String ProductGuid;
        private String CreateTime;
        private String BuyNumber;
        private String TotalPrice;
        private String OrderType;

        public String getProductGuid() {
            return ProductGuid;
        }

        public void setProductGuid(String ProductGuid) {
            this.ProductGuid = ProductGuid;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getBuyNumber() {
            return BuyNumber;
        }

        public void setBuyNumber(String BuyNumber) {
            this.BuyNumber = BuyNumber;
        }

        public String getTotalPrice() {
            return TotalPrice;
        }

        public void setTotalPrice(String TotalPrice) {
            this.TotalPrice = TotalPrice;
        }

        public String getOrderType() {
            return OrderType;
        }

        public void setOrderType(String orderType) {
            OrderType = orderType;
        }

        @Override
        public String toString() {
            return "ValBean{" +
                    "ProductGuid='" + ProductGuid + '\'' +
                    ", CreateTime='" + CreateTime + '\'' +
                    ", BuyNumber='" + BuyNumber + '\'' +
                    ", TotalPrice='" + TotalPrice + '\'' +
                    ", OrderType='" + OrderType + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ProductInRecordBean{" +
                "val=" + val +
                '}';
    }
}
