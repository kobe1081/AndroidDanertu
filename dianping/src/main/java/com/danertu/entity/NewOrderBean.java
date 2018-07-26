package com.danertu.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/7/23 15:01
 * <p>
 * 包名：com.danertu.entity
 * 文件名：NewOrderBean
 * 描述：新的订单实体类
 */
public class NewOrderBean extends ArrayList<Parcelable> implements Parcelable {
    private String Name;
    private String Mobile;
    private String Address;
    private String AgentID;
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
    private List<OrderBody.OrderproductlistBean.OrderproductbeanBean>  productItems;

    public String getLogisticsCompanyCode() {
        return LogisticsCompanyCode;
    }

    public void setLogisticsCompanyCode(String logisticsCompanyCode) {
        LogisticsCompanyCode = logisticsCompanyCode;
    }




    public List<OrderBody.OrderproductlistBean.OrderproductbeanBean> getProductItems() {
        return productItems;
    }

    public void setProductItems(List<OrderBody.OrderproductlistBean.OrderproductbeanBean> productItems) {
        this.productItems = productItems;
    }

    public String getAgentID() {
        return AgentID;
    }

    public void setAgentID(String agentID) {
        AgentID = agentID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getClientToSellerMsg() {
        return ClientToSellerMsg;
    }

    public void setClientToSellerMsg(String clientToSellerMsg) {
        ClientToSellerMsg = clientToSellerMsg;
    }

    public String getInvoiceTitle() {
        return InvoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        InvoiceTitle = invoiceTitle;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getPaymentName() {
        return PaymentName;
    }

    public void setPaymentName(String paymentName) {
        PaymentName = paymentName;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getDispatchModeName() {
        return DispatchModeName;
    }

    public void setDispatchModeName(String dispatchModeName) {
        DispatchModeName = dispatchModeName;
    }

    public String getShipmentNumber() {
        return ShipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        ShipmentNumber = shipmentNumber;
    }

    public String getShouldPayPrice() {
        return ShouldPayPrice;
    }

    public void setShouldPayPrice(String shouldPayPrice) {
        ShouldPayPrice = shouldPayPrice;
    }

    public String getMemLoginId() {
        return MemLoginId;
    }

    public void setMemLoginId(String memLoginId) {
        MemLoginId = memLoginId;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getOderStatus() {
        return OderStatus;
    }

    public void setOderStatus(String oderStatus) {
        OderStatus = oderStatus;
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

    public void setShipmentStatus(String shipmentStatus) {
        ShipmentStatus = shipmentStatus;
    }

    public String getDispatchPrice() {
        return DispatchPrice;
    }

    public void setDispatchPrice(String dispatchPrice) {
        DispatchPrice = dispatchPrice;
    }

    public String getDispatchTime() {
        return DispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        DispatchTime = dispatchTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
