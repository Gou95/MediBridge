package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalSalesResponse {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("bill_amount")
    @Expose
    private Object billAmount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Object getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Object billAmount) {
        this.billAmount = billAmount;
    }
}
