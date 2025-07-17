package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSignUpUserResponse {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("retailer_contact_name")
    @Expose
    private Object retailerContactName;
    @SerializedName("retailer_password")
    @Expose
    private String retailerPassword;
    @SerializedName("retailer_email")
    @Expose
    private Object retailerEmail;
    @SerializedName("retailer_phone")
    @Expose
    private String retailerPhone;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("retailer_address")
    @Expose
    private Object retailerAddress;
    @SerializedName("retailer_gst")
    @Expose
    private Object retailerGst;
    @SerializedName("retailer_dl_no")
    @Expose
    private Object retailerDlNo;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("photo_path")
    @Expose
    private Object photoPath;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("subs_expiry_date")
    @Expose
    private String subsExpiryDate;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public CharSequence getRetailerContactName() {
        return (CharSequence) retailerContactName;
    }

    public void setRetailerContactName(Object retailerContactName) {
        this.retailerContactName = retailerContactName;
    }

    public String getRetailerPassword() {
        return retailerPassword;
    }

    public void setRetailerPassword(String retailerPassword) {
        this.retailerPassword = retailerPassword;
    }

    public String getRetailerEmail() {
        return (String) retailerEmail;
    }

    public void setRetailerEmail(Object retailerEmail) {
        this.retailerEmail = retailerEmail;
    }

    public String getRetailerPhone() {
        return retailerPhone;
    }

    public void setRetailerPhone(String retailerPhone) {
        this.retailerPhone = retailerPhone;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Object getRetailerAddress() {
        return retailerAddress;
    }

    public void setRetailerAddress(Object retailerAddress) {
        this.retailerAddress = retailerAddress;
    }

    public CharSequence getRetailerGst() {
        return (CharSequence) retailerGst;
    }

    public void setRetailerGst(Object retailerGst) {
        this.retailerGst = retailerGst;
    }

    public CharSequence getRetailerDlNo() {
        return (CharSequence) retailerDlNo;
    }

    public void setRetailerDlNo(Object retailerDlNo) {
        this.retailerDlNo = retailerDlNo;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public Object getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(Object photoPath) {
        this.photoPath = photoPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubsExpiryDate() {
        return subsExpiryDate;
    }

    public void setSubsExpiryDate(String subsExpiryDate) {
        this.subsExpiryDate = subsExpiryDate;
    }

}
