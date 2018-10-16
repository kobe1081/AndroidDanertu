package com.danertu.entity;

import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/8/2 09:23
 * <p>
 * 包名：com.danertu.entity
 * 文件名：CouponDetailBean
 * 描述：优惠券详情实体类
 */
public class CouponDetailBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Guid : 11406e2b-e1e1-4e52-b4d0-ee8c4ad791ac
         * CouponName : 新增测试
         * UseCondition : 0
         * UseConditionLimitPrice : 0.00
         * DiscountType : 0
         * DiscountPrice : 10.00
         * DiscountPercent : 0.00
         * UseValidityType : 0
         * UseStartTime : 2018/7/30 0:00:00
         * UseEndTime : 2018/8/29 0:00:00
         * AgencyLevel :
         * UseFromTomorrowStart : 2018-08-02 14:35:57
         * UseFromTomorrowEnd : 2018-08-02 14:35:57
         * UseFromTodayStart : 2018-08-01 14:35:57
         * UseFromTodayEnd : 2018-08-01 14:35:57
         * Description :
         * UseProductExceptGuid :
         * UseProductAppointGuid :
         * UseAgentAppoint :
         * UseScope : 0
         * UseFromToday : 0
         * UseFromTomorrow : 0
         * LimitType : 0
         * MemberLimit : 0
         * MemLoginId : 13557013342
         * CouponRecordGuid : d29c6e2a-3a21-4767-9c41-cdd5656538f8
         * CouponGuid : 11406e2b-e1e1-4e52-b4d0-ee8c4ad791ac
         * ReceiveCount : 1
         * GetTime : 2018/8/1 14:35:57
         * IsDelete : 0
         * IsUsed : 0
         * ShopId : 13557013342
         * JumpType : 5
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
        private String AgencyLevel;
        private String UseFromTomorrowStart;
        private String UseFromTomorrowEnd;
        private String UseFromTodayStart;
        private String UseFromTodayEnd;
        private String Description;
        private String UseProductExceptGuid;
        private String UseProductAppointGuid;
        private String UseAgentAppoint;
        private String UseScope;
        private String UseFromToday;
        private String UseFromTomorrow;
        private String LimitType;
        private String MemberLimit;
        private String MemLoginId;
        private String CouponRecordGuid;
        private String CouponGuid;
        private String ReceiveCount;
        private String GetTime;
        private String IsDelete;
        private String IsUsed;
        private String ShopId;
        private String JumpType;

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

        public String getAgencyLevel() {
            return AgencyLevel;
        }

        public void setAgencyLevel(String AgencyLevel) {
            this.AgencyLevel = AgencyLevel;
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

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
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

        public String getMemberLimit() {
            return MemberLimit;
        }

        public void setMemberLimit(String MemberLimit) {
            this.MemberLimit = MemberLimit;
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

        public String getShopId() {
            return ShopId;
        }

        public void setShopId(String ShopId) {
            this.ShopId = ShopId;
        }

        public String getJumpType() {
            return JumpType;
        }

        public void setJumpType(String JumpType) {
            this.JumpType = JumpType;
        }
    }
}
