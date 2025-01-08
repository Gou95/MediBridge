package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailsResponse {
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("addtime")
    @Expose
    private String addtime;
    @SerializedName("delivery_day")
    @Expose
    private Object deliveryDay;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("order_qty")
    @Expose
    private String orderQty;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("dealer_phone")
    @Expose
    private String dealerPhone;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public CharSequence getDeliveryDay() {
        return (CharSequence) deliveryDay;
    }

    public void setDeliveryDay(Object deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }
}
