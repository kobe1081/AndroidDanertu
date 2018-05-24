package com.danertu.entity;

import java.util.List;

public class ProductCategory {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * ProductGuid : a6852148-4805-4c3c-80fb-829f69f5d23e
         * ProductCategory : 1
         * ProductProperty : 1
         */

        private String ProductGuid;
        private String ProductCategory;
        private String ProductProperty;

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
