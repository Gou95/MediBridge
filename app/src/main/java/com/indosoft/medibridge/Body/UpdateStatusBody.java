package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateStatusBody {
    @SerializedName("subs_expiry_date")
    @Expose
    private String subsExpiryDate;
    @SerializedName("subscription_plan")
    @Expose
    private String subscriptionPlan;
    @SerializedName("subscription_id")
    @Expose
    private String subscriptionId;

    public String getSubsExpiryDate() {
        return subsExpiryDate;
    }

    public void setSubsExpiryDate(String subsExpiryDate) {
        this.subsExpiryDate = subsExpiryDate;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }
}
