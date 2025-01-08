package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastOrderResponse {
    @SerializedName("order_no")
    @Expose
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
