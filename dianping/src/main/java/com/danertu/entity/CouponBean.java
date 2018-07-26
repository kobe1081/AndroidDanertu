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
 * ShopId :
 * ProductCategoryID : 779
 * WenQuanUrl :
 * AppUrl :
 * JumpType : 3
 * Description : 1.详细信息@@2.详细信息@@3.详细信息 --使用说明
 * <p>
 * GetTime :2018/7/1 0:00:00 --领取时间
 * EndTime:                 --截止有效期
 * AppointProductUrl: 产品地址
 * AppointProductType  1-成人票、儿童票  2-团体票  3-客房
 */
public class CouponBean {

    /**
     * CouponList : [{"Guid":"d6d7d5eb-eaae-404c-b4f4-20c630f89c49","CouponName":"全平台优惠券-新用户专享","RemainCount":"47","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/1 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"","GetTime":"","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"68886d70-7b43-44c6-93ef-4afac2a4a96c","CouponName":"全平台优惠券-限领1张","RemainCount":"47","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"15017339307","GetTime":"2018/7/20 10:25:04","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"f4c5e8ea-3de2-4837-aa88-697e9f7e6495","CouponName":"全平台优惠券-普通用户可领","RemainCount":"50","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"","GetTime":"","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"f7e67739-6f86-4e67-b174-77c90d472a32","CouponName":"全平台优惠券-无限制","RemainCount":"49","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"15017339307","GetTime":"2018/7/20 10:25:29","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"239fb240-1cf0-49e9-ae7a-7b7fddc43575","CouponName":"全平台优惠券-满100元可使用","RemainCount":"48","DiscountType":"1","DiscountPrice":"1.00","DiscountPercent":"9.80","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"1","UseConditionLimitPrice":"100.00","IsUsed":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"15017339307","GetTime":"2018/7/20 10:25:31","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"69aa1363-ac51-47db-bb4e-8eb5c1eacc5e","CouponName":"全平台优惠券-区代理、代理商可领","RemainCount":"50","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/9 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"","GetTime":"","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""},{"Guid":"feb5b1db-fbda-4c63-818f-bf86e67a3b44","CouponName":"全平台优惠券-领取后当日5天内可用","RemainCount":"44","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"2","UseStartTime":"1900/1/1 0:00:00","UseEndTime":"1900/1/1 0:00:00","UseFromTomorrow":"0","UseFromToday":"5","ShopId":"13557013342","GetTime":"2018/7/20 11:07:21","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":"1.教育的最首要功能是促进个体发展，包括个体的社会化和个性化；@@2.教育的最基础功能是影响社会人才体系的变化以及经济发展。现代社会重教育的经济功能主要包括：为经济的持续稳定发展提供良好的背景；提高受教育者的潜在劳动能力；形成适应现代经济生活的观念态度和行为方式；@@3.教育的社会功能是为国家的发展培养人才，服务于国家的政治、经济发展。@@4.教育的最深远功能是影响文化发展，教育不仅要传递文化，还"},{"Guid":"f51ba858-03af-472f-9dae-d27503f341fd","CouponName":"全平台优惠券-四级代理都可领","RemainCount":"50","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/1 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"0","UseStartTime":"2018/7/1 0:00:00","UseEndTime":"2018/7/28 0:00:00","UseFromTomorrow":"0","UseFromToday":"0","ShopId":"","GetTime":"","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":"1.教育的最首要功能是促进个体发展，包括个体的社会化和个性化；@@2.教育的最基础功能是影响社会人才体系的变化以及经济发展。现代社会重教育的经济功能主要包括：为经济的持续稳定发展提供良好的背景；提高受教育者的潜在劳动能力；形成适应现代经济生活的观念态度和行为方式；@@3.教育的社会功能是为国家的发展培养人才，服务于国家的政治、经济发展。@@4.教育的最深远功能是影响文化发展，教育不仅要传递文化，还"},{"Guid":"c80777c4-e7cc-44b1-bf8b-e4d7aa1d969b","CouponName":"领取后次日2天可用","RemainCount":"49","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","GetStartTime":"2018/7/8 0:00:00","GetEndTime":"2018/7/28 0:00:00","UseCondition":"0","UseConditionLimitPrice":"0.00","IsUsed":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","UseValidityType":"1","UseStartTime":"1900/1/1 0:00:00","UseEndTime":"1900/1/1 0:00:00","UseFromTomorrow":"2","UseFromToday":"0","ShopId":"15017339307","GetTime":"2018/7/20 11:28:35","ProductCategoryID":"","WenQuanUrl":"","AppUrl":"","JumpType":"5","Description":""}]
     * TotalCount_o : 9
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
         * Guid : d6d7d5eb-eaae-404c-b4f4-20c630f89c49
         * CouponName : 全平台优惠券-新用户专享
         * RemainCount : 47
         * DiscountType : 0
         * DiscountPrice : 10.00
         * DiscountPercent : 0.00
         * GetStartTime : 2018/7/1 0:00:00
         * GetEndTime : 2018/7/28 0:00:00
         * UseCondition : 0
         * UseConditionLimitPrice : 0.00
         * IsUsed :
         * UseScope : 0
         * UseAgentAppoint :
         * UseProductAppointGuid :
         * UseProductExceptGuid :
         * UseValidityType : 0
         * UseStartTime : 2018/7/8 0:00:00
         * UseEndTime : 2018/7/28 0:00:00
         * UseFromTomorrow : 0
         * UseFromToday : 0
         * ShopId :
         * GetTime :
         * ProductCategoryID :
         * WenQuanUrl :
         * AppUrl :
         * JumpType : 5
         * Description :
         * AppointProductUrl
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
        private String ShopId;
        private String GetTime;
        private String ProductCategoryID;
        private String WenQuanUrl;
        private String AppUrl;
        private String JumpType;
        private String Description;
        private String EndTime;
        private String AppointProductUrl;
        private String AppointProductType;

        public String getAppointProductType() {
            return AppointProductType;
        }

        public void setAppointProductType(String appointProductType) {
            AppointProductType = appointProductType;
        }

        public String getAppointProductUrl() {
            return AppointProductUrl;
        }

        public void setAppointProductUrl(String appointProductUrl) {
            AppointProductUrl = appointProductUrl;
        }

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

        public String getShopId() {
            return ShopId;
        }

        public void setShopId(String ShopId) {
            this.ShopId = ShopId;
        }

        public String getGetTime() {
            return GetTime;
        }

        public void setGetTime(String GetTime) {
            this.GetTime = GetTime;
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

        public String getAppUrl() {
            return AppUrl;
        }

        public void setAppUrl(String AppUrl) {
            this.AppUrl = AppUrl;
        }

        public String getJumpType() {
            return JumpType;
        }

        public void setJumpType(String JumpType) {
            this.JumpType = JumpType;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public String getEndTime() {
            return EndTime;
        }

        public void setEndTime(String endTime) {
            EndTime = endTime;
        }
    }
}
