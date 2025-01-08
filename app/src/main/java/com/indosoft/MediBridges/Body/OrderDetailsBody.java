package com.indosoft.MediBridges.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailsBody {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("order_qty")
    @Expose
    private String orderQty;
    @SerializedName("unit_id")
    @Expose
    private String unitId;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(String orderQty) {
        this.orderQty = orderQty;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
