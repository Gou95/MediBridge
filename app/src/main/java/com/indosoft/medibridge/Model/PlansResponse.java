package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlansResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("subscription_name")
    @Expose
    private String subscriptionName;
    @SerializedName("subscription_charges")
    @Expose
    private String subscriptionCharges;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public String getSubscriptionCharges() {
        return subscriptionCharges;
    }

    public void setSubscriptionCharges(String subscriptionCharges) {
        this.subscriptionCharges = subscriptionCharges;
    }

}
