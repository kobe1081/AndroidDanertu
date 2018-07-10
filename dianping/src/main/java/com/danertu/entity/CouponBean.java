package com.danertu.entity;

import java.util.List;


/**
 * Guid : c709901f-1e49-44fd-b7ea-2fff097f65e3
 * CouponName : 商城券12   --名称
 * RemainCount : 10 --剩余数量
 * DiscountType : 0 --优惠形式：0-优惠金额，1-优惠折扣
 * DiscountPrice : 10.00    --优惠金额
 * DiscountPercent : 0.00   --优惠折扣
 * GetStartTime : 2018/7/1 0:00:00  --领取开始时间
 * GetEndTime : 2018/7/13 0:00:00   --领取结束时间
 * UseCondition : 0 --使用条件：0-无限制，1-消费一定金额
 * UseConditionLimitPrice : 0.00    --满减价格
 * IsUsed : 0   --领取的优惠券是否适用：0-已领取未使用，1-已领取已使用，null-未领取
 * UseScope : 0     --适用范围：0-全平台，1-温泉，2-酒水，3-指定代理商，4-指定商品，5-除指定商品
 * UseAgentAppoint :        --指定代理商
 * UseProductAppointGuid :      --指定商品
 * UseProductExceptGuid :   --除指定商品外
 * UseValidityType : 2  --使用有效期类型  0--自定义，1--领取后次日N天内可用，2--领取后当日N天内可用
 * UseStartTime : 1900/1/1 0:00:00  --自定义开始使用时间
 * UseEndTime : 1900/1/1 0:00:00  --自定义结束使用时间
 * UseFromTomorrow  : 0    --领取后次日N天内可用
 * UseFromToday : 2     --领取后当日N天内可用
 * Description : 1.详细信息@@2.详细信息@@3.详细信息 --使用说明
 */
public class CouponBean {

    /**
     * CouponList : [{"Guid":"f1fd9c9e-fe07-4bbb-93dd-3371ae716081","CouponName":"温泉儿童优惠券3","RemainCount":"8","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/1 0:00:00","GetEndTime":"2018/7/10 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"0","UseScope":"1","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"2","UseStartTime":"1900/1/1 0:00:00","UseEndTime":"1900/1/1 0:00:00","UseFromTomorrow":"0","UseFromToday":"2","Description":"1.说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明@@3.说明说明说明说明说明说明说明"},{"Guid":"d2af1874-9c43-4db7-8e3e-1215a3723460","CouponName":"温泉儿童优惠券2","RemainCount":"8","DiscountType":"1","DiscountPrice":"0.00","DiscountPercent":"8.80","GetStartTime":"2018/7/1 0:00:00","GetEndTime":"2018/7/10 0:00:00","UseCondition":"1","UseConditionLimitPrice":"300.00","IsUsed":"0","UseScope":"1","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrow ":"0","UseFromToday":"0","Description":"1.说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明说明"},{"Guid":"e02ab45b-830a-4f9f-82c3-03c3fe8c0e85","CouponName":"温泉儿童优惠券1","RemainCount":"8","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/1 0:00:00","GetEndTime":"2018/7/10 0:00:00","UseCondition":"1","UseConditionLimitPrice":"200.00","IsUsed":"0","UseScope":"1","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"1","UseStartTime":"1900/1/1 0:00:00","UseEndTime":"1900/1/1 0:00:00","UseFromTomorrow ":"3","UseFromToday":"0","Description":"1.说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明说明说明@@3.说明说明说明说明说明说明说明说明"}]
     * TotalCount_o : 3
     * TotalPageCount_o : 1
     */

    private String TotalCount_o;
    private String TotalPageCount_o;
    private List<CouponListBean> CouponList;

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

    public List<CouponListBean> getCouponList() {
        return CouponList;
    }

    public void setCouponList(List<CouponListBean> CouponList) {
        this.CouponList = CouponList;
    }

    public static class CouponListBean {
        /**
         * Guid : f1fd9c9e-fe07-4bbb-93dd-3371ae716081
         * CouponName : 温泉儿童优惠券3
         * RemainCount : 8
         * DiscountType : 0
         * DiscountPrice : 10.00
         * DiscountPercent : 0.00
         * GetStartTime : 2018/7/1 0:00:00
         * GetEndTime : 2018/7/10 0:00:00
         * UseCondition : 0
         * UseConditionLimitPrice : 0.00
         * IsUsed : 0
         * UseScope : 1
         * UseAgentAppoint :
         * UseProductAppointGuid :
         * UseProductExceptGuid :
         * UseValidityType : 2
         * UseStartTime : 1900/1/1 0:00:00
         * UseEndTime : 1900/1/1 0:00:00
         * UseFromTomorrow : 0
         * UseFromToday : 2
         * Description : 1.说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明@@3.说明说明说明说明说明说明说明
         */

        private String Guid;
        private String CouponName;
        private String RemainCount;
        private String DiscountType;
        private String DiscountPrice;
        private String DiscountPercent;
        private String GetStartTime;
        private String GetEndTime;
        private String UseCondition;
        private String UseConditionLimitPrice;
        private String IsUsed;
        private String UseScope;
        private String UseAgentAppoint;
        private String UseProductAppointGuid;
        private String UseProductExceptGuid;
        private String UseValidityType;
        private String UseStartTime;
        private String UseEndTime;
        private String UseFromTomorrow;
        private String UseFromToday;
        private String Description;

        public String getGuid() {
            return Guid;
        }

        public void setGuid(String Guid) {
            this.Guid = Guid;
        }

        public String getCouponName() {
            return CouponName;
        }

        public void setCouponName(String CouponName) {
            this.CouponName = CouponName;
        }

        public String getRemainCount() {
            return RemainCount;
        }

        public void setRemainCount(String RemainCount) {
            this.RemainCount = RemainCount;
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

        public String getGetStartTime() {
            return GetStartTime;
        }

        public void setGetStartTime(String GetStartTime) {
            this.GetStartTime = GetStartTime;
        }

        public String getGetEndTime() {
            return GetEndTime;
        }

        public void setGetEndTime(String GetEndTime) {
            this.GetEndTime = GetEndTime;
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

        public String getIsUsed() {
            return IsUsed;
        }

        public void setIsUsed(String IsUsed) {
            this.IsUsed = IsUsed;
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

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }
    }
}
