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
 * WenQuanUrl:    --温泉地址
 * JumpType：0  --跳转类型，
 * ShopId:13557013342  --领取优惠券时的店铺id
 * ProductCategoryID : 779  --跳转分类列表的id
 * AppUrl:
 * AppointProductUrl:   产品地址
 * AppointProductType  1-成人票、儿童票  2-团体票  3-客房
 * ImageUrl:  要分享的图片地址（需经过添加二维码）
 * ImageWidth  二维码宽度
 * ImageX  二维码起始点x坐标
 * ImageY 二维码起始点Y坐标
 */
public class MyCouponBean {

    /**
     * CouponRecordList : [{"CouponName":"尼科尔优惠券1","UseCondition":"1","UseConditionLimitPrice":"500.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/7 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-12 17:22:04","UseFromTomorrowEnd":"2018-07-12 17:22:04","UseFromTodayStart":"2018-07-11 17:22:04","UseFromTodayEnd":"2018-07-11 17:22:04","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"2","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明","CouponGuid":"5559f40d-c362-4f0d-b1e7-4dd1e0dc4ac7","ShopId":"13925340017","ProductCategoryID":"779","WenQuanUrl":"","JumpType":"3","CouponRecordGuid":"1dd6958e-adfb-48e4-b7aa-ea746f3f5b50"},{"CouponName":"会员限制-代理级别","UseCondition":"1","UseConditionLimitPrice":"300.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/10 0:00:00","UseEndTime":"2018/7/27 0:00:00","UseFromTomorrowStart":"2018-07-12 16:08:49","UseFromTomorrowEnd":"2018-07-12 16:08:49","UseFromTodayStart":"2018-07-11 16:08:49","UseFromTodayEnd":"2018-07-11 16:08:49","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"0","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"","CouponGuid":"c920c6ec-4710-45c4-a74e-e3e41d1b0fad","ShopId":"13557013342","ProductCategoryID":"","WenQuanUrl":"","JumpType":"5","CouponRecordGuid":"82591261-4439-4038-b7b8-e19489778334"},{"CouponName":"会员限制-代理级别1","UseCondition":"1","UseConditionLimitPrice":"300.00","DiscountType":"1","DiscountPrice":"8.80","DiscountPercent":"8.80","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-12 16:06:28","UseFromTomorrowEnd":"2018-07-12 16:06:28","UseFromTodayStart":"2018-07-11 16:06:28","UseFromTodayEnd":"2018-07-11 16:06:28","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"2","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"","CouponGuid":"071b3dc2-935a-4b38-ac86-f8deb4c597df","ShopId":"13557013342","ProductCategoryID":"779","WenQuanUrl":"","JumpType":"3","CouponRecordGuid":"84a7a00e-16b3-449e-a745-a3077f10cefa"},{"CouponName":"除指定商品之外","UseCondition":"1","UseConditionLimitPrice":"100.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/9 0:00:00","UseEndTime":"2018/7/27 0:00:00","UseFromTomorrowStart":"2018-07-12 10:07:33","UseFromTomorrowEnd":"2018-07-12 10:07:33","UseFromTodayStart":"2018-07-11 10:07:33","UseFromTodayEnd":"2018-07-11 10:07:33","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"5","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"a502b088-50ed-4d58-a04c-1ce294a535f0,a502b088-50ed-4d58-a04c-1ce294a535f0,a502b088-50ed-4d58-a04c-1ce294a535f0","MemLoginId":"13557013342","Description":"1、人工智能尖端人才远远不能满足需求。行业风口的人工智能，在中国人才缺口将超过500万人，而中国人工智能人才数量目前只有5万（数据来自工信部教育考试中心）。@@2、并且目前岗位溢价相当严重，2017年人工智能在互联网岗位薪酬中位列第三，月薪20.1k，如果按照普遍的16月薪酬计算，那么人工智能在2017年一年的薪酬就是2.01*16=32.16万。那么再来看一组2018的薪酬数据。@@3、2018","CouponGuid":"47a9a8f9-e6b9-4036-b0d7-1ab9bc0c7ed3","ShopId":"13557013342","ProductCategoryID":"","WenQuanUrl":"","JumpType":"5","CouponRecordGuid":"519e738b-1ecb-4033-8a2c-ad875a8b27f1"},{"CouponName":"指定商品","UseCondition":"1","UseConditionLimitPrice":"300.00","DiscountType":"0","DiscountPrice":"10.00","DiscountPercent":"0.00","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-12 10:03:43","UseFromTomorrowEnd":"2018-07-12 10:03:43","UseFromTodayStart":"2018-07-11 10:03:43","UseFromTodayEnd":"2018-07-11 10:03:43","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"4","UseAgentAppoint":"","UseProductAppointGuid":"dd70d3e2-580b-4531-90d1-07e04ef8cced,de18ba4f-5e02-428b-a47e-0ec9d1fccd35,e4358ec8-1265-4182-b7ed-119ae4c8265a,c57c4548-b023-47c7-a567-16c51f57e6ef","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1、人工智能尖端人才远远不能满足需求。行业风口的人工智能，在中国人才缺口将超过500万人，而中国人工智能人才数量目前只有5万（数据来自工信部教育考试中心）。@@2、并且目前岗位溢价相当严重，2017年人工智能在互联网岗位薪酬中位列第三，月薪20.1k，如果按照普遍的16月薪酬计算，那么人工智能在2017年一年的薪酬就是2.01*16=32.16万。那么再来看一组2018的薪酬数据。@@3、2018","CouponGuid":"1bf1c124-63ac-489d-8b41-ccee0b1e481a","ShopId":"13557013342","ProductCategoryID":"","WenQuanUrl":"","JumpType":"1","CouponRecordGuid":"8890a112-fab5-44cc-8e96-a0bf1757900e"},{"CouponName":"晓镇香优惠券1","UseCondition":"0","UseConditionLimitPrice":"0.00","DiscountType":"1","DiscountPrice":"0.00","DiscountPercent":"8.80","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-10 15:05:08","UseFromTomorrowEnd":"2018-07-10 15:05:08","UseFromTodayStart":"2018-07-09 15:05:08","UseFromTodayEnd":"2018-07-09 15:05:08","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"3","UseAgentAppoint":"13557013342","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.还是减肥建设的更加激发告诉大家国防建设生不逢时给大家发个少见多怪封建时代广泛接受的高房价还是个的说法技术的广泛接受的鬼斧神工的积分告诉大家国防建设@@2.还是减肥建设的更加激发告诉大家国防建设生不逢时给大家发个少见多怪封建时代广泛接受的高房价还是个的说法技术的广泛接受的鬼斧神工的积分告诉大家国防建设","CouponGuid":"e3217ed3-45f4-4ef2-a9b2-d880c7ac181a","ShopId":"13557013342","ProductCategoryID":"","WenQuanUrl":"","JumpType":"4","CouponRecordGuid":"b72ea58f-1e02-4ae2-9cac-a53e2a7e6b54"},{"CouponName":"拉莫优惠券","UseCondition":"0","UseConditionLimitPrice":"0.00","DiscountType":"1","DiscountPrice":"0.00","DiscountPercent":"2.00","UseValidityType":"1","UseStartTime":"1900/1/1 0:00:00","UseEndTime":"1900/1/1 0:00:00","UseFromTomorrowStart":"2018-07-10 14:41:08","UseFromTomorrowEnd":"2018-07-13 14:41:08","UseFromTodayStart":"2018-07-09 14:41:08","UseFromTodayEnd":"2018-07-09 14:41:08","UseFromTomorrow":"3","UseFromToday":"0","UseScope":"2","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明","CouponGuid":"29494043-8758-4f26-af35-4929b81ca195","ShopId":"13557013342","ProductCategoryID":"779","WenQuanUrl":"","JumpType":"3","CouponRecordGuid":"d1049bdb-39c0-41b6-b96b-66b44a4c0807"},{"CouponName":"温泉儿童优惠券2","UseCondition":"1","UseConditionLimitPrice":"300.00","DiscountType":"1","DiscountPrice":"0.00","DiscountPercent":"8.80","UseValidityType":"0","UseStartTime":"2018/7/8 0:00:00","UseEndTime":"2018/7/20 0:00:00","UseFromTomorrowStart":"2018-07-10 14:41:02","UseFromTomorrowEnd":"2018-07-10 14:41:02","UseFromTodayStart":"2018-07-09 14:41:02","UseFromTodayEnd":"2018-07-09 14:41:02","UseFromTomorrow":"0","UseFromToday":"0","UseScope":"1","UseAgentAppoint":"","UseProductAppointGuid":"","UseProductExceptGuid":"","MemLoginId":"13557013342","Description":"1.说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明说明说明说明","CouponGuid":"d2af1874-9c43-4db7-8e3e-1215a3723460","ShopId":"13557013342","ProductCategoryID":"","WenQuanUrl":"http://192.168.1.137:411/qyyd/quanyan_index.htm?agentid=13557013342","JumpType":"0","CouponRecordGuid":"128ce9f4-1c30-4229-b287-077e2bcd1f10"}]
     * TotalCount_o : 8
     * TotalPageCount_o : 1
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
         * UseFromTomorrowStart : 2018-07-12 17:22:04
         * UseFromTomorrowEnd : 2018-07-12 17:22:04
         * UseFromTodayStart : 2018-07-11 17:22:04
         * UseFromTodayEnd : 2018-07-11 17:22:04
         * UseFromTomorrow : 0
         * UseFromToday : 0
         * UseScope : 2
         * UseAgentAppoint :
         * UseProductAppointGuid :
         * UseProductExceptGuid :
         * MemLoginId : 13557013342
         * Description : 1.说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明说明@@2.说明说明说明说明说明
         * CouponGuid : 5559f40d-c362-4f0d-b1e7-4dd1e0dc4ac7
         * ShopId : 13925340017
         * ProductCategoryID : 779
         * WenQuanUrl :
         * JumpType : 3
         * CouponRecordGuid : 1dd6958e-adfb-48e4-b7aa-ea746f3f5b50
         * AppUrl:
         * AppointProductUrl
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
        private String ShopId;
        private String ProductCategoryID;
        private String WenQuanUrl;
        private String JumpType;
        private String CouponRecordGuid;
        private String AppUrl;
        private String AppointProductUrl;
        private String AppointProductType;
        private String ImageUrl;
        private String ImageWidth;
        private String ImageX;
        private String ImageY;
        private String CouponShareUrl;

        public String getCouponShareUrl() {
            return CouponShareUrl;
        }

        public void setCouponShareUrl(String couponShareUrl) {
            CouponShareUrl = couponShareUrl;
        }

        public String getImageUrl() {
            return ImageUrl;
        }

        public void setImageUrl(String imageUrl) {
            ImageUrl = imageUrl;
        }

        public String getImageWidth() {
            return ImageWidth;
        }

        public void setImageWidth(String imageWidth) {
            ImageWidth = imageWidth;
        }

        public String getImageX() {
            return ImageX;
        }

        public void setImageX(String imageX) {
            ImageX = imageX;
        }

        public String getImageY() {
            return ImageY;
        }

        public void setImageY(String imageY) {
            ImageY = imageY;
        }

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

        public String getAppUrl() {
            return AppUrl;
        }

        public void setAppUrl(String appUrl) {
            AppUrl = appUrl;
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

        public String getShopId() {
            return ShopId;
        }

        public void setShopId(String ShopId) {
            this.ShopId = ShopId;
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

        public String getJumpType() {
            return JumpType;
        }

        public void setJumpType(String JumpType) {
            this.JumpType = JumpType;
        }

        public String getCouponRecordGuid() {
            return CouponRecordGuid;
        }

        public void setCouponRecordGuid(String CouponRecordGuid) {
            this.CouponRecordGuid = CouponRecordGuid;
        }
    }
}
