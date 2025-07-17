package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosUpdateBody {
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("unit_id")
    @Expose
    private String unitId;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
