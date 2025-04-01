package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderListResponse {

    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("addtime")
    @Expose
    private String addtime;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("totalmeds")
    @Expose
    private String totalmeds;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("unlisted_medicines")
    @Expose
    private String unlistedMedicines;

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

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

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getTotalmeds() {
        return totalmeds;
    }

    public void setTotalmeds(String totalmeds) {
        this.totalmeds = totalmeds;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getUnlistedMedicines() {
        return unlistedMedicines;
    }

    public void setUnlistedMedicines(String unlistedMedicines) {
        this.unlistedMedicines = unlistedMedicines;
    }
}
