package com.indosoft.mediBridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserUpdateBody {

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
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("retailer_address")
    @Expose
    private Object retailerAddress;
    @SerializedName("retailer_gst")
    @Expose
    private Object retailerGst;
    @SerializedName("retailer_dl_no")
    @Expose
    private Object retailerDlNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("firstorderplaced")
    @Expose
    private String firstorderplaced;
    @SerializedName("addtime")
    @Expose
    private String addtime;
    @SerializedName("edittime")
    @Expose
    private Object edittime;

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

    public Object getRetailerContactName() {
        return retailerContactName;
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

    public Object getRetailerEmail() {
        return retailerEmail;
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

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public Object getRetailerAddress() {
        return retailerAddress;
    }

    public void setRetailerAddress(Object retailerAddress) {
        this.retailerAddress = retailerAddress;
    }

    public Object getRetailerGst() {
        return retailerGst;
    }

    public void setRetailerGst(Object retailerGst) {
        this.retailerGst = retailerGst;
    }

    public Object getRetailerDlNo() {
        return retailerDlNo;
    }

    public void setRetailerDlNo(Object retailerDlNo) {
        this.retailerDlNo = retailerDlNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstorderplaced() {
        return firstorderplaced;
    }

    public void setFirstorderplaced(String firstorderplaced) {
        this.firstorderplaced = firstorderplaced;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public Object getEdittime() {
        return edittime;
    }

    public void setEdittime(Object edittime) {
        this.edittime = edittime;
    }
}
