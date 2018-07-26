package com.danertu.entity;

import java.util.List;

public class NewOrderHeadBean {

    /**
     * val : [{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807235426293","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"336.00","CreateTime":"2018/7/23 10:54:26","PaymentName":"安卓端移动设备WAP支付-账号支付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807235725584","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"158.00","CreateTime":"2018/7/23 9:57:25","PaymentName":"安卓端移动设备WAP支付-账号支付"},{"MemLoginID":"13557013342","Name":"阿木测试","Mobile":"15768580734","Address":"","OrderNumber":"201807205018465","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"120.00","CreateTime":"2018/7/20 16:50:18","PaymentName":"苹果端移动设备WAP支付-到付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807173407920","AgentID":"18682686867","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"6036.27","CreateTime":"2018/7/17 17:34:07","PaymentName":"苹果端移动设备WAP支付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807172845308","AgentID":"18682686867","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"6036.27","CreateTime":"2018/7/17 17:28:45","PaymentName":"苹果端移动设备WAP支付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807162156761","AgentID":"18682686867","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"6106.80","CreateTime":"2018/7/16 17:21:56","PaymentName":"苹果端移动设备WAP支付"},{"MemLoginID":"13557013342","Name":"测试","Mobile":"15015007777","Address":"","OrderNumber":"201807164557801","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"353.55","CreateTime":"2018/7/16 16:45:57","PaymentName":"安卓端移动设备WAP支付-到付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807164007906","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"258.00","CreateTime":"2018/7/16 16:40:07","PaymentName":"苹果端移动设备WAP支付"},{"MemLoginID":"13557013342","Name":"小四月测试","Mobile":"13790733668","Address":"湖南省岳阳市岳阳楼区升华路了新村测试","OrderNumber":"201807162054797","AgentID":"(null)","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"268.00","CreateTime":"2018/7/16 15:20:54","PaymentName":"苹果端移动设备WAP支付"},{"MemLoginID":"13557013342","Name":"测试","Mobile":"15015007777","Address":"","OrderNumber":"201807165207808","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"176.00","CreateTime":"2018/7/16 14:52:07","PaymentName":"安卓端移动设备WAP支付-到付"},{"MemLoginID":"13557013342","Name":"测试","Mobile":"15015007777","Address":"","OrderNumber":"201807164652919","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"352.00","CreateTime":"2018/7/16 14:46:52","PaymentName":"安卓端移动设备WAP支付-到付"},{"MemLoginID":"13557013342","Name":"测试","Mobile":"15015007777","Address":"","OrderNumber":"201807163911195","AgentID":"13557013342","DispatchModeName":"","DispatchPrice":"0.00","ShipmentNumber":"","OderStatus":"1","LogisticsCompanyCode":"","ShipmentStatus":"0","PaymentStatus":"0","ShouldPayPrice":"400.00","CreateTime":"2018/7/16 14:39:11","PaymentName":"安卓端移动设备WAP支付-到付"}]
     * count : {"TotalCount_o":"245","TotalPageCount_o":"21"}
     */

    private CountBean count;
    private List<ValBean> val;

    public CountBean getCount() {
        return count;
    }

    public void setCount(CountBean count) {
        this.count = count;
    }

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class CountBean {
        /**
         * TotalCount_o : 245
         * TotalPageCount_o : 21
         */

        private String TotalCount_o;
        private String TotalPageCount_o;

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
    }

    public static class ValBean {
        /**
         * MemLoginID : 13557013342
         * Name : 小四月测试
         * Mobile : 13790733668
         * Address : 湖南省岳阳市岳阳楼区升华路了新村测试
         * OrderNumber : 201807235426293
         * AgentID : 13557013342
         * DispatchModeName :
         * DispatchPrice : 0.00
         * ShipmentNumber :
         * OderStatus : 1
         * LogisticsCompanyCode :
         * ShipmentStatus : 0
         * PaymentStatus : 0
         * ShouldPayPrice : 336.00
         * CreateTime : 2018/7/23 10:54:26
         * PaymentName : 安卓端移动设备WAP支付-账号支付
         */

        private String MemLoginID;
        private String Name;
        private String Mobile;
        private String Address;
        private String OrderNumber;
        private String AgentID;
        private String DispatchModeName;
        private String DispatchPrice;
        private String ShipmentNumber;
        private String OderStatus;
        private String LogisticsCompanyCode;
        private String ShipmentStatus;
        private String PaymentStatus;
        private String ShouldPayPrice;
        private String CreateTime;
        private String PaymentName;

        public String getMemLoginID() {
            return MemLoginID;
        }

        public void setMemLoginID(String MemLoginID) {
            this.MemLoginID = MemLoginID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getMobile() {
            return Mobile;
        }

        public void setMobile(String Mobile) {
            this.Mobile = Mobile;
        }

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getOrderNumber() {
            return OrderNumber;
        }

        public void setOrderNumber(String OrderNumber) {
            this.OrderNumber = OrderNumber;
        }

        public String getAgentID() {
            return AgentID;
        }

        public void setAgentID(String AgentID) {
            this.AgentID = AgentID;
        }

        public String getDispatchModeName() {
            return DispatchModeName;
        }

        public void setDispatchModeName(String DispatchModeName) {
            this.DispatchModeName = DispatchModeName;
        }

        public String getDispatchPrice() {
            return DispatchPrice;
        }

        public void setDispatchPrice(String DispatchPrice) {
            this.DispatchPrice = DispatchPrice;
        }

        public String getShipmentNumber() {
            return ShipmentNumber;
        }

        public void setShipmentNumber(String ShipmentNumber) {
            this.ShipmentNumber = ShipmentNumber;
        }

        public String getOderStatus() {
            return OderStatus;
        }

        public void setOderStatus(String OderStatus) {
            this.OderStatus = OderStatus;
        }

        public String getLogisticsCompanyCode() {
            return LogisticsCompanyCode;
        }

        public void setLogisticsCompanyCode(String LogisticsCompanyCode) {
            this.LogisticsCompanyCode = LogisticsCompanyCode;
        }

        public String getShipmentStatus() {
            return ShipmentStatus;
        }

        public void setShipmentStatus(String ShipmentStatus) {
            this.ShipmentStatus = ShipmentStatus;
        }

        public String getPaymentStatus() {
            return PaymentStatus;
        }

        public void setPaymentStatus(String PaymentStatus) {
            this.PaymentStatus = PaymentStatus;
        }

        public String getShouldPayPrice() {
            return ShouldPayPrice;
        }

        public void setShouldPayPrice(String ShouldPayPrice) {
            this.ShouldPayPrice = ShouldPayPrice;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getPaymentName() {
            return PaymentName;
        }

        public void setPaymentName(String PaymentName) {
            this.PaymentName = PaymentName;
        }
    }
}
