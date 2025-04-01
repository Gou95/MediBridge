package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetUrgentCartResponse {
    @SerializedName("cart_id")
    @Expose
    private String cartId;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("dealer_id")
    @Expose
    private String dealerId;
    @SerializedName("dealer_name")
    @Expose
    private String dealerName;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("qty")
    @Expose
    private Object qty;
    @SerializedName("unit_name")
    @Expose
    private Object unitName;
    @SerializedName("unlisted_medicines")
    @Expose
    private String unlistedMedicines;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

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

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQty() {
        return (String) qty;
    }

    public void setQty(Object qty) {
        this.qty = qty;
    }

    public CharSequence getUnitName() {
        return (CharSequence) unitName;
    }

    public void setUnitName(Object unitName) {
        this.unitName = unitName;
    }

    public String getUnlistedMedicines() {
        return unlistedMedicines;
    }

    public void setUnlistedMedicines(String unlistedMedicines) {
        this.unlistedMedicines = unlistedMedicines;
    }
}
