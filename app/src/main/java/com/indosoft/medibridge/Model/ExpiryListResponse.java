package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpiryListResponse {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("batch_no")
    @Expose
    private String batchNo;
    @SerializedName("expiry_month")
    @Expose
    private String expiryMonth;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }
}
