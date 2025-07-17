package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpiryRegisterBody {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("expiry_month")
    @Expose
    private String expiryMonth;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }
}
