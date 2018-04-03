package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2018/1/26.
 */

public class HistoryRecordBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid : 2c0b65f5-7128-4aff-ba10-aacfedf1bef9
         * Ordernumber : 201801263736470
         * SupplierLoginId : hd888
         * Worker :
         * CreateTime : 2018/1/26 15:57:24
         */

        private String Guid;
        private String Ordernumber;
        private String SupplierLoginId;
        private String Worker;
        private String CreateTime;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String Guid) {
            this.Guid = Guid;
        }

        public String getOrdernumber() {
            return Ordernumber;
        }

        public void setOrdernumber(String Ordernumber) {
            this.Ordernumber = Ordernumber;
        }

        public String getSupplierLoginId() {
            return SupplierLoginId;
        }

        public void setSupplierLoginId(String SupplierLoginId) {
            this.SupplierLoginId = SupplierLoginId;
        }

        public String getWorker() {
            return Worker;
        }

        public void setWorker(String Worker) {
            this.Worker = Worker;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }
    }
}
