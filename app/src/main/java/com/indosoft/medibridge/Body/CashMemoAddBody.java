package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashMemoAddBody {
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("unit_id")
    @Expose
    private String unitId;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("batch_no")
    @Expose
    private String batchNo;
    @SerializedName("rate")
    @Expose
    private String rate;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
