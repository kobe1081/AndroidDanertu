package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2017/12/25.
 */

public class StockDetailBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid : 91f9ebc5-19be-4275-9796-d95731d78fea
         * MemLoginId : 13557013342
         * Stock : 10
         * ProductName : 法国雾色红酒特惠买三送三
         * ProductGuid : 1490c9c5-327c-496a-9524-d0db87626bed
         * ShopPrice : 414.00
         * MarketPrice : 4308.00
         * SmallImage : 20170829102314617.png_300x300.jpg
         * ProductCategoryId : 62
         */

        private String Guid;
        private String MemLoginId;
        private String Stock;
        private String ProductName;
        private String ProductGuid;
        private String ShopPrice;
        private String MarketPrice;
        private String SmallImage;
        private String ProductCategoryId;

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

        public String getStock() {
            return Stock;
        }

        public void setStock(String Stock) {
            this.Stock = Stock;
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

        public String getSmallImage() {
            return SmallImage;
        }

        public void setSmallImage(String SmallImage) {
            this.SmallImage = SmallImage;
        }

        public String getProductCategoryId() {
            return ProductCategoryId;
        }

        public void setProductCategoryId(String ProductCategoryId) {
            this.ProductCategoryId = ProductCategoryId;
        }

        @Override
        public String toString() {
            return "ValBean{" +
                    "Guid='" + Guid + '\'' +
                    ", MemLoginId='" + MemLoginId + '\'' +
                    ", Stock='" + Stock + '\'' +
                    ", ProductName='" + ProductName + '\'' +
                    ", ProductGuid='" + ProductGuid + '\'' +
                    ", ShopPrice='" + ShopPrice + '\'' +
                    ", MarketPrice='" + MarketPrice + '\'' +
                    ", SmallImage='" + SmallImage + '\'' +
                    ", ProductCategoryId='" + ProductCategoryId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "StockDetailBean{" +
                "val=" + val +
                '}';
    }
}
