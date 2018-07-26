package com.danertu.entity;

import java.util.List;
/**
 * CouponName : 拉莫优惠券1
 * UseCondition : 0
 * UseConditionLimitPrice : 0.00
 * DiscountType : 0
 * DiscountPrice : 10.00
 * DiscountPercent : 0.00
 * UseValidityType : 0
 * UseStartTime : 2018/7/5 0:00:00
 * UseEndTime : 2018/7/8 0:00:00
 * UseFromTomorrowStart : 2018-07-12 17:20:59
 * UseFromTomorrowEnd : 2018-07-12 17:20:59
 * UseFromTodayStart : 2018-07-11 17:20:59
 * UseFromTodayEnd : 2018-07-11 17:20:59
 * UseFromTomorrow : 0
 * UseFromToday : 0
 * Description : 1.说明说明说明说明说明说明说明说明说明说明说明说明
 * CouponGuid : 971a21a8-4da0-4728-b630-ef0031790ac2
 * UseScope : 0
 * UseAgentAppoint :
 * UseProductAppointGuid :
 * UseProductExceptGuid :
 * MemLoginId : 13557013342
 * CouponRecordGuid : d1f4f359-dc30-4ea9-97f3-d5b9e2c140bc
 * LimitType 0  --是否可与金萝卜一起使用  0-可用,1-不可用
 */
public class ChooseCouponBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid : 4d325c2b-b1fd-43d2-b5ee-31eb204885c9
         * CouponName : 指定多个商品-酒水
         * UseCondition : 0
         * UseConditionLimitPrice : 0.00
         * DiscountType : 0
         * DiscountPrice : 10.00
         * DiscountPercent : 0.00
         * UseValidityType : 0
         * UseStartTime : 2018/7/8 0:00:00
         * UseEndTime : 2018/7/28 0:00:00
         * UseFromTomorrowStart : 2018-07-18 14:47:34
         * UseFromTomorrowEnd : 2018-07-18 14:47:34
         * UseFromTodayStart : 2018-07-17 14:47:34
         * UseFromTodayEnd : 2018-07-17 14:47:34
         * MemLoginId : 13557013342
         * CouponRecordGuid : 45dbf57f-7ef5-4f5d-aff1-8540c20f108e
         * CouponGuid : 4d325c2b-b1fd-43d2-b5ee-31eb204885c9
         * ReceiveCount : 1
         * GetTime : 2018/7/17 14:47:34
         * Description :
         * IsDelete : 0
         * IsUsed : 0
         * UseProductExceptGuid : dd70d3e2-580b-4531-90d1-07e04ef8cced,de18ba4f-5e02-428b-a47e-0ec9d1fccd35,e4358ec8-1265-4182-b7ed-119ae4c8265a,c57c4548-b023-47c7-a567-16c51f57e6ef,035ccb2a-eb0a-4062-9d54-1a986b82f3e7
         * UseProductAppointGuid :
         * UseAgentAppoint :
         * UseScope : 5
         * UseFromToday : 0
         * UseFromTomorrow : 0
         * LimitType : 0
         * ShopId : 13557013342
         * MemberLimit : 0
         * JumpType : 5
         * AppUrl :
         * ProductCategoryID :
         * WenQuanUrl :
         */

        private String Guid;
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
        private String MemLoginId;
        private String CouponRecordGuid;
        private String CouponGuid;
        private String ReceiveCount;
        private String GetTime;
        private String Description;
        private String IsDelete;
        private String IsUsed;
        private String UseProductExceptGuid;
        private String UseProductAppointGuid;
        private String UseAgentAppoint;
        private String UseScope;
        private String UseFromToday;
        private String UseFromTomorrow;
        private String LimitType;
        private String ShopId;
        private String MemberLimit;
        private String JumpType;
        private String AppUrl;
        private String ProductCategoryID;
        private String WenQuanUrl;

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

        public String getMemLoginId() {
            return MemLoginId;
        }

        public void setMemLoginId(String MemLoginId) {
            this.MemLoginId = MemLoginId;
        }

        public String getCouponRecordGuid() {
            return CouponRecordGuid;
        }

        public void setCouponRecordGuid(String CouponRecordGuid) {
            this.CouponRecordGuid = CouponRecordGuid;
        }

        public String getCouponGuid() {
            return CouponGuid;
        }

        public void setCouponGuid(String CouponGuid) {
            this.CouponGuid = CouponGuid;
        }

        public String getReceiveCount() {
            return ReceiveCount;
        }

        public void setReceiveCount(String ReceiveCount) {
            this.ReceiveCount = ReceiveCount;
        }

        public String getGetTime() {
            return GetTime;
        }

        public void setGetTime(String GetTime) {
            this.GetTime = GetTime;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getIsDelete() {
            return IsDelete;
        }

        public void setIsDelete(String IsDelete) {
            this.IsDelete = IsDelete;
        }

        public String getIsUsed() {
            return IsUsed;
        }

        public void setIsUsed(String IsUsed) {
            this.IsUsed = IsUsed;
        }

        public String getUseProductExceptGuid() {
            return UseProductExceptGuid;
        }

        public void setUseProductExceptGuid(String UseProductExceptGuid) {
            this.UseProductExceptGuid = UseProductExceptGuid;
        }

        public String getUseProductAppointGuid() {
            return UseProductAppointGuid;
        }

        public void setUseProductAppointGuid(String UseProductAppointGuid) {
            this.UseProductAppointGuid = UseProductAppointGuid;
        }

        public String getUseAgentAppoint() {
            return UseAgentAppoint;
        }

        public void setUseAgentAppoint(String UseAgentAppoint) {
            this.UseAgentAppoint = UseAgentAppoint;
        }

        public String getUseScope() {
            return UseScope;
        }

        public void setUseScope(String UseScope) {
            this.UseScope = UseScope;
        }

        public String getUseFromToday() {
            return UseFromToday;
        }

        public void setUseFromToday(String UseFromToday) {
            this.UseFromToday = UseFromToday;
        }

        public String getUseFromTomorrow() {
            return UseFromTomorrow;
        }

        public void setUseFromTomorrow(String UseFromTomorrow) {
            this.UseFromTomorrow = UseFromTomorrow;
        }

        public String getLimitType() {
            return LimitType;
        }

        public void setLimitType(String LimitType) {
            this.LimitType = LimitType;
        }

        public String getShopId() {
            return ShopId;
        }

        public void setShopId(String ShopId) {
            this.ShopId = ShopId;
        }

        public String getMemberLimit() {
            return MemberLimit;
        }

        public void setMemberLimit(String MemberLimit) {
            this.MemberLimit = MemberLimit;
        }

        public String getJumpType() {
            return JumpType;
        }

        public void setJumpType(String JumpType) {
            this.JumpType = JumpType;
        }

        public String getAppUrl() {
            return AppUrl;
        }

        public void setAppUrl(String AppUrl) {
            this.AppUrl = AppUrl;
        }

        public String getProductCategoryID() {
            return ProductCategoryID;
        }

        public void setProductCategoryID(String ProductCategoryID) {
            this.ProductCategoryID = ProductCategoryID;
        }

        public String getWenQuanUrl() {
            return WenQuanUrl;
        }

        public void setWenQuanUrl(String WenQuanUrl) {
            this.WenQuanUrl = WenQuanUrl;
        }
    }
}
