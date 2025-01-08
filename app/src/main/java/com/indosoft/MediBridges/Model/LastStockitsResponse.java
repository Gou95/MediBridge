package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastStockitsResponse {
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("product_id")
    @Expose
    private String productId;

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
