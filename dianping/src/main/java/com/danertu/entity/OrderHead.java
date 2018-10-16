package com.danertu.entity;

import java.util.List;

/**
 * Created by Viz on 2018/1/25.
 */

public class OrderHead {

    /**
     * orderinfolist : {"orderinfobean":[{"Name":"测试","Mobile":"18677757641","Address":"","ClientToSellerMsg":"","InvoiceTitle":"null","CreateTime":"2018/3/26 16:14:39","PaymentName":"泉眼核销-到付","PaymentStatus":"2","DispatchModeName":"EMS","ShipmentNumber":"000","ShouldPayPrice":"120.00","MemLoginId":"13557013342","OrderNumber":"201803261439066","OderStatus":"5","orderType":"0","ShipmentStatus":"2","DispatchPrice":"0.00","DispatchTime":"2018/3/26 16:16:04"}]}
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

    private OrderinfolistBean orderinfolist;

    public OrderinfolistBean getOrderinfolist() {
        return orderinfolist;
    }

    public void setOrderinfolist(OrderinfolistBean orderinfolist) {
        this.orderinfolist = orderinfolist;
    }

    public static class OrderinfolistBean {
        private List<OrderinfobeanBean> orderinfobean;

        public List<OrderinfobeanBean> getOrderinfobean() {
            return orderinfobean;
        }

        public void setOrderinfobean(List<OrderinfobeanBean> orderinfobean) {
            this.orderinfobean = orderinfobean;
        }

        public static class OrderinfobeanBean {
            /**
             * Name : 测试
             * Mobile : 18677757641
             * Address :
             * ClientToSellerMsg :
             * InvoiceTitle : null
             * CreateTime : 2018/3/26 16:14:39
             * PaymentName : 泉眼核销-到付
             * PaymentStatus : 2
             * DispatchModeName : EMS
             * ShipmentNumber : 000
             * ShouldPayPrice : 120.00
             * MemLoginId : 13557013342
             * OrderNumber : 201803261439066
             * OderStatus : 5
             * orderType : 0
             * ShipmentStatus : 2
             * DispatchPrice : 0.00
             * DispatchTime : 2018/3/26 16:16:04
             */

            private String Name;
            private String Mobile;
            private String Address;
            private String ClientToSellerMsg;
            private String InvoiceTitle;
            private String CreateTime;
            private String PaymentName;
            private String PaymentStatus;
            private String DispatchModeName;
            private String ShipmentNumber;
            private String ShouldPayPrice;
            private String MemLoginId;
            private String OrderNumber;
            private String OderStatus;
            private String orderType;
            private String ShipmentStatus;
            private String DispatchPrice;
            private String DispatchTime;
            private String LogisticsCompanyCode;
            private String TicketMoney;

            public String getTicketMoney() {
                return TicketMoney;
            }

            public void setTicketMoney(String ticketMoney) {
                TicketMoney = ticketMoney;
            }

            public String getLogisticsCompanyCode() {
                return LogisticsCompanyCode;
            }

            public void setLogisticsCompanyCode(String logisticsCompanyCode) {
                LogisticsCompanyCode = logisticsCompanyCode;
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

            public String getClientToSellerMsg() {
                return ClientToSellerMsg;
            }

            public void setClientToSellerMsg(String ClientToSellerMsg) {
                this.ClientToSellerMsg = ClientToSellerMsg;
            }

            public String getInvoiceTitle() {
                return InvoiceTitle;
            }

            public void setInvoiceTitle(String InvoiceTitle) {
                this.InvoiceTitle = InvoiceTitle;
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

            public String getPaymentStatus() {
                return PaymentStatus;
            }

            public void setPaymentStatus(String PaymentStatus) {
                this.PaymentStatus = PaymentStatus;
            }

            public String getDispatchModeName() {
                return DispatchModeName;
            }

            public void setDispatchModeName(String DispatchModeName) {
                this.DispatchModeName = DispatchModeName;
            }

            public String getShipmentNumber() {
                return ShipmentNumber;
            }

            public void setShipmentNumber(String ShipmentNumber) {
                this.ShipmentNumber = ShipmentNumber;
            }

            public String getShouldPayPrice() {
                return ShouldPayPrice;
            }

            public void setShouldPayPrice(String ShouldPayPrice) {
                this.ShouldPayPrice = ShouldPayPrice;
            }

            public String getMemLoginId() {
                return MemLoginId;
            }

            public void setMemLoginId(String MemLoginId) {
                this.MemLoginId = MemLoginId;
            }

            public String getOrderNumber() {
                return OrderNumber;
            }

            public void setOrderNumber(String OrderNumber) {
                this.OrderNumber = OrderNumber;
            }

            public String getOderStatus() {
                return OderStatus;
            }

            public void setOderStatus(String OderStatus) {
                this.OderStatus = OderStatus;
            }

            public String getOrderType() {
                return orderType;
            }

            public void setOrderType(String orderType) {
                this.orderType = orderType;
            }

            public String getShipmentStatus() {
                return ShipmentStatus;
            }

            public void setShipmentStatus(String ShipmentStatus) {
                this.ShipmentStatus = ShipmentStatus;
            }

            public String getDispatchPrice() {
                return DispatchPrice;
            }

            public void setDispatchPrice(String DispatchPrice) {
                this.DispatchPrice = DispatchPrice;
            }

            public String getDispatchTime() {
                return DispatchTime;
            }

            public void setDispatchTime(String DispatchTime) {
                this.DispatchTime = DispatchTime;
            }

            @Override
            public String toString() {
                return "OrderinfobeanBean{" +
                        "Name='" + Name + '\'' +
                        ", Mobile='" + Mobile + '\'' +
                        ", Address='" + Address + '\'' +
                        ", ClientToSellerMsg='" + ClientToSellerMsg + '\'' +
                        ", InvoiceTitle='" + InvoiceTitle + '\'' +
                        ", CreateTime='" + CreateTime + '\'' +
                        ", PaymentName='" + PaymentName + '\'' +
                        ", PaymentStatus='" + PaymentStatus + '\'' +
                        ", DispatchModeName='" + DispatchModeName + '\'' +
                        ", ShipmentNumber='" + ShipmentNumber + '\'' +
                        ", ShouldPayPrice='" + ShouldPayPrice + '\'' +
                        ", MemLoginId='" + MemLoginId + '\'' +
                        ", OrderNumber='" + OrderNumber + '\'' +
                        ", OderStatus='" + OderStatus + '\'' +
                        ", orderType='" + orderType + '\'' +
                        ", ShipmentStatus='" + ShipmentStatus + '\'' +
                        ", DispatchPrice='" + DispatchPrice + '\'' +
                        ", DispatchTime='" + DispatchTime + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "OrderinfolistBean{" +
                    "orderinfobean=" + orderinfobean +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OrderHead{" +
                "orderinfolist=" + orderinfolist +
                '}';
    }
}
