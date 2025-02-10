package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockistBody {
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("dealer_phone")
    @Expose
    private String dealerPhone;
    @SerializedName("dealer_gst")
    @Expose
    private String dealerGst;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("city_id")
    @Expose
    private String cityId;

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
    }

    public String getDealerGst() {
        return dealerGst;
    }

    public void setDealerGst(String dealerGst) {
        this.dealerGst = dealerGst;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

}
