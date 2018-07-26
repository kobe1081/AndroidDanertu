package com.danertu.entity;

import java.util.List;

public class CouponCountBean {

    /**
     * val : [{"UseScope":"0","Num":"1"},{"UseScope":"1","Num":"2"},{"UseScope":"2","Num":"1"},{"UseScope":"3","Num":"1"},{"UseScope":"4","Num":"5"},{"UseScope":"5","Num":"6"}]
     * total : 16
     */

    private String total;
    private List<ValBean> val;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * UseScope : 0
         * Num : 1
         */

        private String UseScope;
        private String Num;

        public String getUseScope() {
            return UseScope;
        }

        public void setUseScope(String UseScope) {
            this.UseScope = UseScope;
        }

        public String getNum() {
            return Num;
        }

        public void setNum(String Num) {
            this.Num = Num;
        }
    }
}
