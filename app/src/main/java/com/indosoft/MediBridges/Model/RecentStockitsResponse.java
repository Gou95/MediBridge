package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentStockitsResponse {

    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;

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
}
