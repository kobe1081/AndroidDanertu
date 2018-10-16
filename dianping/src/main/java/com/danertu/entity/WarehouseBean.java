package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2017/12/22.
 */

public class WarehouseBean {

    /**
     * WareHouseList : [{"Guid":"91f9ebc5-19be-4275-9796-d95731d78fea","MemLoginId":"13557013342","Stock":"10","ProductName":"法国雾色红酒特惠买三送三","ProductGuid":"1490c9c5-327c-496a-9524-d0db87626bed","TotalPrice":"4140.00","SmallImage":"20170829102314617.png_300x300.jpg"},{"Guid":"ca8505e7-000f-483a-9f6a-81c1f02156f2","MemLoginId":"13557013342","Stock":"1","ProductName":"晓镇香 8陈酿  500ml   〖12支〗","ProductGuid":"af2f7274-3a55-4797-a3f5-8277fe50af1b","TotalPrice":"2376.00","SmallImage":"20160720163305485.jpg_300x300.jpg"},{"Guid":"36f1192e-26a5-44d6-be66-2b4638871024","MemLoginId":"13557013342","Stock":"1","ProductName":"拉莫 2009干红葡萄酒 750ml 〖6支〗","ProductGuid":"6b86f6ef-a0b0-4a2b-8882-8182ce368b38","TotalPrice":"2448.00","SmallImage":"20170817171422664.jpg_300x300.jpg"},{"Guid":"2331d50b-17f6-4b04-98b3-128cfc88aec1","MemLoginId":"13557013342","Stock":"10","ProductName":"测试","ProductGuid":"c57c4548-b023-47c7-a567-16c51f57e6ef","TotalPrice":"9000.00","SmallImage":"_300x300.jpg"}]
     * TotalCount_o : 4
     * TotalPageCount_o : 1
     * ProductCategory : [{"ProductCategoryId":"1","Total":"11","BrandName":"晓镇香"},{"ProductCategoryId":"62","Total":"11","BrandName":"其它"}]
     */
    private String result;
    private String info;
    private String code;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String TotalCount_o;
    private String TotalPageCount_o;
    private List<WareHouseListBean> WareHouseList;
    private List<ProductCategoryBean> ProductCategory;

    public String getTotalCount_o() {
        return TotalCount_o;
    }

    public void setTotalCount_o(String TotalCount_o) {
        this.TotalCount_o = TotalCount_o;
    }

    public String getTotalPageCount_o() {
        return TotalPageCount_o;
    }

    public void setTotalPageCount_o(String TotalPageCount_o) {
        this.TotalPageCount_o = TotalPageCount_o;
    }

    public List<WareHouseListBean> getWareHouseList() {
        return WareHouseList;
    }

    public void setWareHouseList(List<WareHouseListBean> WareHouseList) {
        this.WareHouseList = WareHouseList;
    }

    public List<ProductCategoryBean> getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(List<ProductCategoryBean> ProductCategory) {
        this.ProductCategory = ProductCategory;
    }

    public static class WareHouseListBean {
        /**
         * Guid : 91f9ebc5-19be-4275-9796-d95731d78fea
         * MemLoginId : 13557013342
         * Stock : 10
         * ProductName : 法国雾色红酒特惠买三送三
         * ProductGuid : 1490c9c5-327c-496a-9524-d0db87626bed
         * TotalPrice : 4140.00
         * SmallImage : 20170829102314617.png_300x300.jpg
         */

        private String Guid;
        private String MemLoginId;
        private String Stock;
        private String ProductName;
        private String ProductGuid;
        private String TotalPrice;
        private String SmallImage;

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

        @Override
        public String toString() {
            return "WareHouseListBean{" +
                    "Guid='" + Guid + '\'' +
                    ", MemLoginId='" + MemLoginId + '\'' +
                    ", Stock='" + Stock + '\'' +
                    ", ProductName='" + ProductName + '\'' +
                    ", ProductGuid='" + ProductGuid + '\'' +
                    ", TotalPrice='" + TotalPrice + '\'' +
                    ", SmallImage='" + SmallImage + '\'' +
                    '}';
        }
    }

    public static class ProductCategoryBean {
        /**
         * ProductCategoryId : 1
         * Total : 11
         * BrandName : 晓镇香
         */

        private String ProductCategoryId;
        private String Total;
        private String BrandName;

        public String getProductCategoryId() {
            return ProductCategoryId;
        }

        public void setProductCategoryId(String ProductCategoryId) {
            this.ProductCategoryId = ProductCategoryId;
        }

        public String getTotal() {
            return Total;
        }

        public void setTotal(String Total) {
            this.Total = Total;
        }

        public String getBrandName() {
            return BrandName;
        }

        public void setBrandName(String BrandName) {
            this.BrandName = BrandName;
        }

        @Override
        public String toString() {
            return "ProductCategoryBean{" +
                    "ProductCategoryId='" + ProductCategoryId + '\'' +
                    ", Total='" + Total + '\'' +
                    ", BrandName='" + BrandName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WarehouseBean{" +
                "TotalCount_o='" + TotalCount_o + '\'' +
                ", TotalPageCount_o='" + TotalPageCount_o + '\'' +
                ", WareHouseList=" + WareHouseList +
                ", ProductCategory=" + ProductCategory +
                '}';
    }
}
