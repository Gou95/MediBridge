package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnlistedBody {
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("unlisted_medicines")
    @Expose
    private String unlistedMedicines;
    @SerializedName("product_id")
    @Expose
    private String productId;

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getUnlistedMedicines() {
        return unlistedMedicines;
    }

    public void setUnlistedMedicines(String unlistedMedicines) {
        this.unlistedMedicines = unlistedMedicines;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
