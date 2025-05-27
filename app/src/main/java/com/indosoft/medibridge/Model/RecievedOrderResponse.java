package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecievedOrderResponse {
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("addtime")
    @Expose
    private String addtime;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("dealer_id")
    @Expose
    private Object dealerId;
    @SerializedName("product_id")
    @Expose
    private Object productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("unit_id")
    @Expose
    private Object unitId;
    @SerializedName("unit_name")
    @Expose
    private Object unitName;
    @SerializedName("order_qty")
    @Expose
    private Object orderQty;
    @SerializedName("delivery_day")
    @Expose
    private Object deliveryDay;
    @SerializedName("dealer_name")
    @Expose
    private Object dealerName;
    @SerializedName("unlisted_medicines")
    @Expose
    private Object unlistedMedicines;
    @SerializedName("order_status")
    @Expose
    private Object orderStatus;

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

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public Object getDealerId() {
        return dealerId;
    }

    public void setDealerId(Object dealerId) {
        this.dealerId = dealerId;
    }

    public Object getProductId() {
        return productId;
    }

    public void setProductId(Object productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Object getUnitId() {
        return unitId;
    }

    public void setUnitId(Object unitId) {
        this.unitId = unitId;
    }

    public CharSequence getUnitName() {
        return (CharSequence) unitName;
    }

    public void setUnitName(Object unitName) {
        this.unitName = unitName;
    }

    public CharSequence getOrderQty() {
        return (CharSequence) orderQty;
    }

    public void setOrderQty(Object orderQty) {
        this.orderQty = orderQty;
    }

    public CharSequence getDeliveryDay() {
        return (CharSequence) deliveryDay;
    }

    public void setDeliveryDay(Object deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public CharSequence getDealerName() {
        return (CharSequence) dealerName;
    }

    public void setDealerName(Object dealerName) {
        this.dealerName = dealerName;
    }

    public CharSequence getUnlistedMedicines() {
        return (CharSequence) unlistedMedicines;
    }

    public void setUnlistedMedicines(Object unlistedMedicines) {
        this.unlistedMedicines = unlistedMedicines;
    }

    public CharSequence getOrderStatus() {
        return (CharSequence) orderStatus;
    }

    public void setOrderStatus(Object orderStatus) {
        this.orderStatus = orderStatus;
    }
}
