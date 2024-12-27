package com.indosoft.mediBridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddtoCartBody {

    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("unit")
    @Expose
    private String unit;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
