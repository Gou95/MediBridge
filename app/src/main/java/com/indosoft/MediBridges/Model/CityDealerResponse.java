package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CityDealerResponse {
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("dealer_contact_name")
    @Expose
    private String dealerContactName;
    @SerializedName("dealer_email")
    @Expose
    private String dealerEmail;
    @SerializedName("dealer_phone")
    @Expose
    private String dealerPhone;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("dealer_address")
    @Expose
    private String dealerAddress;
    @SerializedName("dealer_gst")
    @Expose
    private String dealerGst;
    @SerializedName("dealer_dl_no")
    @Expose
    private String dealerDlNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state_name")
    @Expose
    private String stateName;

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

    public String getDealerContactName() {
        return dealerContactName;
    }

    public void setDealerContactName(String dealerContactName) {
        this.dealerContactName = dealerContactName;
    }

    public String getDealerEmail() {
        return dealerEmail;
    }

    public void setDealerEmail(String dealerEmail) {
        this.dealerEmail = dealerEmail;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
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

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerGst() {
        return dealerGst;
    }

    public void setDealerGst(String dealerGst) {
        this.dealerGst = dealerGst;
    }

    public String getDealerDlNo() {
        return dealerDlNo;
    }

    public void setDealerDlNo(String dealerDlNo) {
        this.dealerDlNo = dealerDlNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
