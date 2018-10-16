package com.danertu.entity;

import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/7/25 17:11
 * <p>
 * 包名：com.danertu.entity
 * 文件名：QuanYanProductCategory
 * 描述：温泉产品分类实体类
 */
public class QuanYanProductCategory {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * ProductGuid : A6852148-4805-4C3C-80FB-829F69F5D23E
         * ProductCategory : 1
         * ProductProperty : 1
         */

        private String ProductGuid;
        private String ProductCategory;
        private String ProductProperty;
        private String SupplierLoginId;

        public String getSupplierLoginId() {
            return SupplierLoginId;
        }

        public void setSupplierLoginId(String supplierLoginId) {
            SupplierLoginId = supplierLoginId;
        }

        public String getProductGuid() {
            return ProductGuid;
        }

        public void setProductGuid(String ProductGuid) {
            this.ProductGuid = ProductGuid;
        }

        public String getProductCategory() {
            return ProductCategory;
        }

        public void setProductCategory(String ProductCategory) {
            this.ProductCategory = ProductCategory;
        }

        public String getProductProperty() {
            return ProductProperty;
        }

        public void setProductProperty(String ProductProperty) {
            this.ProductProperty = ProductProperty;
        }
    }
}
