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
    @SerializedName("trial_period")
    @Expose
    private String trialPeriod;

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

    public String getTrialPeriod() {
        return trialPeriod;
    }

    public void setTrialPeriod(String trialPeriod) {
        this.trialPeriod = trialPeriod;
    }
    public int getMonths() {
        // Example: "3 Months Subscription" → 3
        try {
            return Integer.parseInt(subscriptionName.split(" ")[0]);
        } catch (Exception e) {
            return 1; // fallback
        }
    }

    public int getMonthlyCharge() {
        try {
            return Integer.parseInt(subscriptionCharges);
        } catch (Exception e) {
            return 0;
        }
    }

    public int getTotalAmount() {
        return getMonths() * getMonthlyCharge();
    }


}
