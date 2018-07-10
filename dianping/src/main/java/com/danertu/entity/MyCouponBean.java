package com.danertu.entity;

import java.util.List;

/**
 * CouponName : 晓镇香优惠券1
 * UseCondition : 0     --使用条件：0-无限制，1-消费一定金额
 * UseConditionLimitPrice : 0.00
 * DiscountType : 1     --优惠形式：0-优惠金额，1-优惠折扣
 * DiscountPrice : 0.00     --优惠金额
 * DiscountPercent : 8.80   --优惠折扣
 * UseValidityType : 0      --使用有效期类型  0--自定义，1--领取后次日N天内可用，2--领取后当日N天内可用
 * UseStartTime : 2018/7/8 0:00:00  --开始使用时间
 * UseEndTime : 2018/7/20 0:00:00   --结束使用时间
 * UseFromTomorrow : 0  --领取后次日N天内可用
 * UseFromToday : 0     --领取后当日N天内可用
 * UseScope : 3     --适用范围：0-全平台，1-温泉，2-酒水，3-指定代理商，4-指定商品，5-除指定商品
 * UseAgentAppoint : 13557013342    --指定代理商
 * UseProductAppointGuid :      --指定商品
 * UseProductExceptGuid :       --除指定商品外
 * MemLoginId : 13557013342
 * Description : 1.还是减      --使用说明
 * CouponGuid : e3217ed3-45f4-4ef2-a9b2-d880c7ac181a
 * CouponRecordGuid : b72ea58f-1e02-4ae2-9cac-a53e2a7e6b54
 */
public class MyCouponBean {

    /**
     * CouponRecordList : [{"CouponName":"尼科尔优惠券1","UseCondition":"1","UseConditionLimitPrice":"500.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/7 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-10 14:41:09","UseFromTomorrowEnd":"2018-07-10 14:41:09","UseFromTodayStart":"2018-07-09 14:41:09","UseFromTodayEnd":"2018-07-09 14:41:09","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"2","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明","CouponGuid":"5559f40d-c362-4f0d-b1e7-4dd1e0dc4ac7","CouponRecordGuid":"bfb4e228-e92c-40b8-8837-006bfd29f138"},{"CouponName":"拉莫优惠券1","UseCondition":"0","UseConditionLimitPrice":"0.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/5 0:00:00","UseEndTime":"2018/7/8 0:00:00","UseFromTomorrowStart":"2018-07-10 14:40:59","UseFromTomorrowEnd":"2018-07-10 14:40:59","UseFromTodayStart":"2018-07-09 14:40:59","UseFromTodayEnd":"2018-07-09 14:40:59","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明","CouponGuid":"971a21a8-4da0-4728-b630-ef0031790ac2","CouponRecordGuid":"73be0e6c-26d1-4a96-921f-26ffee381bdc"}]
     * TotalCount_o : 2
     * TotalPageCount_o : 1
     */

    private String TotalCount_o;
    private String TotalPageCount_o;
    private List<CouponRecordListBean> CouponRecordList;

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

    public List<CouponRecordListBean> getCouponRecordList() {
        return CouponRecordList;
    }

    public void setCouponRecordList(List<CouponRecordListBean> CouponRecordList) {
        this.CouponRecordList = CouponRecordList;
    }

    public static class CouponRecordListBean {
        /**
         * CouponName : 尼科尔优惠券1
         * UseCondition : 1
         * UseConditionLimitPrice : 500.00
         * DiscountType : 0
         * DiscountPrice : 10.00
         * DiscountPercent : 0.00
         * UseValidityType : 0
         * UseStartTime : 2018/7/7 0:00:00
         * UseEndTime : 2018/7/20 0:00:00
         * UseFromTomorrowStart : 2018-07-10 14:41:09
         * UseFromTomorrowEnd : 2018-07-10 14:41:09
         * UseFromTodayStart : 2018-07-09 14:41:09
         * UseFromTodayEnd : 2018-07-09 14:41:09
         * UseFromTomorrow : 0
         * UseFromToday : 0
         * UseScope : 2
         * UseAgentAppoint :
         * UseProductAppointGuid :
         * UseProductExceptGuid :
         * MemLoginId : 13557013342
         * Description : 1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明
         * CouponGuid : 5559f40d-c362-4f0d-b1e7-4dd1e0dc4ac7
         * CouponRecordGuid : bfb4e228-e92c-40b8-8837-006bfd29f138
         */

        private String CouponName;
        private String UseCondition;
        private String UseConditionLimitPrice;
        private String DiscountType;
        private String DiscountPrice;
        private String DiscountPercent;
        private String UseValidityType;
        private String UseStartTime;
        private String UseEndTime;
        private String UseFromTomorrowStart;
        private String UseFromTomorrowEnd;
        private String UseFromTodayStart;
        private String UseFromTodayEnd;
        private String UseFromTomorrow;
        private String UseFromToday;
        private String UseScope;
        private String UseAgentAppoint;
        private String UseProductAppointGuid;
        private String UseProductExceptGuid;
        private String MemLoginId;
        private String Description;
        private String CouponGuid;
        private String CouponRecordGuid;

        public String getCouponName() {
            return CouponName;
        }

        public void setCouponName(String CouponName) {
            this.CouponName = CouponName;
        }

        public String getUseCondition() {
            return UseCondition;
        }

        public void setUseCondition(String UseCondition) {
            this.UseCondition = UseCondition;
        }

        public String getUseConditionLimitPrice() {
            return UseConditionLimitPrice;
        }

        public void setUseConditionLimitPrice(String UseConditionLimitPrice) {
            this.UseConditionLimitPrice = UseConditionLimitPrice;
        }

        public String getDiscountType() {
            return DiscountType;
        }

        public void setDiscountType(String DiscountType) {
            this.DiscountType = DiscountType;
        }

        public String getDiscountPrice() {
            return DiscountPrice;
        }

        public void setDiscountPrice(String DiscountPrice) {
            this.DiscountPrice = DiscountPrice;
        }

        public String getDiscountPercent() {
            return DiscountPercent;
        }

        public void setDiscountPercent(String DiscountPercent) {
            this.DiscountPercent = DiscountPercent;
        }

        public String getUseValidityType() {
            return UseValidityType;
        }

        public void setUseValidityType(String UseValidityType) {
            this.UseValidityType = UseValidityType;
        }

        public String getUseStartTime() {
            return UseStartTime;
        }

        public void setUseStartTime(String UseStartTime) {
            this.UseStartTime = UseStartTime;
        }

        public String getUseEndTime() {
            return UseEndTime;
        }

        public void setUseEndTime(String UseEndTime) {
            this.UseEndTime = UseEndTime;
        }

        public String getUseFromTomorrowStart() {
            return UseFromTomorrowStart;
        }

        public void setUseFromTomorrowStart(String UseFromTomorrowStart) {
            this.UseFromTomorrowStart = UseFromTomorrowStart;
        }

        public String getUseFromTomorrowEnd() {
            return UseFromTomorrowEnd;
        }

        public void setUseFromTomorrowEnd(String UseFromTomorrowEnd) {
            this.UseFromTomorrowEnd = UseFromTomorrowEnd;
        }

        public String getUseFromTodayStart() {
            return UseFromTodayStart;
        }

        public void setUseFromTodayStart(String UseFromTodayStart) {
            this.UseFromTodayStart = UseFromTodayStart;
        }

        public String getUseFromTodayEnd() {
            return UseFromTodayEnd;
        }

        public void setUseFromTodayEnd(String UseFromTodayEnd) {
            this.UseFromTodayEnd = UseFromTodayEnd;
        }

        public String getUseFromTomorrow() {
            return UseFromTomorrow;
        }

        public void setUseFromTomorrow(String UseFromTomorrow) {
            this.UseFromTomorrow = UseFromTomorrow;
        }

        public String getUseFromToday() {
            return UseFromToday;
        }

        public void setUseFromToday(String UseFromToday) {
            this.UseFromToday = UseFromToday;
        }

        public String getUseScope() {
            return UseScope;
        }

        public void setUseScope(String UseScope) {
            this.UseScope = UseScope;
        }

        public String getUseAgentAppoint() {
            return UseAgentAppoint;
        }

        public void setUseAgentAppoint(String UseAgentAppoint) {
            this.UseAgentAppoint = UseAgentAppoint;
        }

        public String getUseProductAppointGuid() {
            return UseProductAppointGuid;
        }

        public void setUseProductAppointGuid(String UseProductAppointGuid) {
            this.UseProductAppointGuid = UseProductAppointGuid;
        }

        public String getUseProductExceptGuid() {
            return UseProductExceptGuid;
        }

        public void setUseProductExceptGuid(String UseProductExceptGuid) {
            this.UseProductExceptGuid = UseProductExceptGuid;
        }

        public String getMemLoginId() {
            return MemLoginId;
        }

        public void setMemLoginId(String MemLoginId) {
            this.MemLoginId = MemLoginId;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getCouponGuid() {
            return CouponGuid;
        }

        public void setCouponGuid(String CouponGuid) {
            this.CouponGuid = CouponGuid;
        }

        public String getCouponRecordGuid() {
            return CouponRecordGuid;
        }

        public void setCouponRecordGuid(String CouponRecordGuid) {
            this.CouponRecordGuid = CouponRecordGuid;
        }
    }
}
