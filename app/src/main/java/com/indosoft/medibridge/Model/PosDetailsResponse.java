package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosDetailsResponse {

    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("add_date")
    @Expose
    private String addDate;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
