package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockistListResponse {
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("dealer_contact_name")
    @Expose
    private Object dealerContactName;
    @SerializedName("dealer_email")
    @Expose
    private Object dealerEmail;
    @SerializedName("dealer_phone")
    @Expose
    private String dealerPhone;
    @SerializedName("dealer_password")
    @Expose
    private String dealerPassword;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("dealer_address")
    @Expose
    private Object dealerAddress;
    @SerializedName("dealer_gst")
    @Expose
    private String dealerGst;
    @SerializedName("dealer_dl_no")
    @Expose
    private Object dealerDlNo;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("fcm_id")
    @Expose
    private String fcmId;
    @SerializedName("subscription_plan")
    @Expose
    private Object subscriptionPlan;
    @SerializedName("subs_expiry_date")
    @Expose
    private Object subsExpiryDate;
    @SerializedName("photo_path")
    @Expose
    private Object photoPath;

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

    public Object getDealerContactName() {
        return dealerContactName;
    }

    public void setDealerContactName(Object dealerContactName) {
        this.dealerContactName = dealerContactName;
    }

    public Object getDealerEmail() {
        return dealerEmail;
    }

    public void setDealerEmail(Object dealerEmail) {
        this.dealerEmail = dealerEmail;
    }

    public String getDealerPhone() {
        return dealerPhone;
    }

    public void setDealerPhone(String dealerPhone) {
        this.dealerPhone = dealerPhone;
    }

    public String getDealerPassword() {
        return dealerPassword;
    }

    public void setDealerPassword(String dealerPassword) {
        this.dealerPassword = dealerPassword;
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

    public CharSequence getDealerAddress() {
        return (CharSequence) dealerAddress;
    }

    public void setDealerAddress(Object dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerGst() {
        return dealerGst;
    }

    public void setDealerGst(String dealerGst) {
        this.dealerGst = dealerGst;
    }

    public Object getDealerDlNo() {
        return dealerDlNo;
    }

    public void setDealerDlNo(Object dealerDlNo) {
        this.dealerDlNo = dealerDlNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public Object getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(Object subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public Object getSubsExpiryDate() {
        return subsExpiryDate;
    }

    public void setSubsExpiryDate(Object subsExpiryDate) {
        this.subsExpiryDate = subsExpiryDate;
    }

    public Object getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(Object photoPath) {
        this.photoPath = photoPath;
    }

}
