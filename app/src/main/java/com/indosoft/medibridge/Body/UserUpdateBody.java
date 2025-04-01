package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserUpdateBody {
    @SerializedName("retailer_name")
    @Expose
    private String retailerName;
    @SerializedName("retailer_contact_name")
    @Expose
    private String retailerContactName;
    @SerializedName("retailer_password")
    @Expose
    private String retailerPassword;
    @SerializedName("retailer_email")
    @Expose
    private String retailerEmail;
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
    @SerializedName("retailer_gst")
    @Expose
    private String retailerGst;
    @SerializedName("retailer_dl_no")
    @Expose
    private String retailerDlNo;

    @SerializedName("photo_path")
    @Expose
    private Object photoPath;

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getRetailerContactName() {
        return retailerContactName;
    }

    public void setRetailerContactName(String retailerContactName) {
        this.retailerContactName = retailerContactName;
    }

    public String getRetailerPassword() {
        return retailerPassword;
    }

    public void setRetailerPassword(String retailerPassword) {
        this.retailerPassword = retailerPassword;
    }

    public String getRetailerEmail() {
        return retailerEmail;
    }

    public void setRetailerEmail(String retailerEmail) {
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

    public String getRetailerGst() {
        return retailerGst;
    }

    public void setRetailerGst(String retailerGst) {
        this.retailerGst = retailerGst;
    }

    public String getRetailerDlNo() {
        return retailerDlNo;
    }

    public void setRetailerDlNo(String retailerDlNo) {
        this.retailerDlNo = retailerDlNo;
    }
    public Object getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(Object photoPath) {
        this.photoPath = photoPath;
    }
}
