package com.indosoft.mediBridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignUpBody {
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("retailer_phone")
    @Expose
    private String retailerPhone;
    @SerializedName("retailer_password")
    @Expose
    private String retailerPassword;
    @SerializedName("state_id")
    @Expose
    private Integer stateId;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerPhone() {
        return retailerPhone;
    }

    public void setRetailerPhone(String retailerPhone) {
        this.retailerPhone = retailerPhone;
    }

    public String getRetailerPassword() {
        return retailerPassword;
    }

    public void setRetailerPassword(String retailerPassword) {
        this.retailerPassword = retailerPassword;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
