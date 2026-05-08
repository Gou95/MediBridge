package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShowCartResponse {
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
    @SerializedName("unlisted_id")
    @Expose
    private String unlistedId;
    @SerializedName("unlisted_medicines")
    @Expose
    private String unlistedMedicines;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("delivery_day")
    @Expose
    private String deliveryDay;
    @SerializedName("urgent_cart")
    @Expose
    private String urgentCart;
    @SerializedName("source")
    @Expose
    private String source;

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

    public String getUnlistedId() {
        return unlistedId;
    }

    public void setUnlistedId(String unlistedId) {
        this.unlistedId = unlistedId;
    }

    public String getUnlistedMedicines() {
        return unlistedMedicines;
    }

    public void setUnlistedMedicines(String unlistedMedicines) {
        this.unlistedMedicines = unlistedMedicines;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDeliveryDay() {
        return deliveryDay;
    }

    public void setDeliveryDay(String deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public String getUrgentCart() {
        return urgentCart;
    }

    public void setUrgentCart(String urgentCart) {
        this.urgentCart = urgentCart;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

//    @SerializedName("cart_id")
//    @Expose
//    private String cartId;
//    @SerializedName("retailer_id")
//    @Expose
//    private String retailerId;
//    @SerializedName("dealer_id")
//    @Expose
//    private String dealerId;
//    @SerializedName("dealer_name")
//    @Expose
//    private String dealerName;
//    @SerializedName("product_id")
//    @Expose
//    private String productId;
//    @SerializedName("product_name")
//    @Expose
//    private String productName;
//    @SerializedName("qty")
//    @Expose
//    private Object qty;
//    @SerializedName("unit_name")
//    @Expose
//    private Object unitName;
//    @SerializedName("delivery_day")
//    @Expose
//    private String deliveryDay;
//    @SerializedName("unlisted_medicines")
//    @Expose
//    private String unlistedMedicines;
//
//    public String getCartId() {
//        return cartId;
//    }
//
//    public void setCartId(String cartId) {
//        this.cartId = cartId;
//    }
//
//    public String getRetailerId() {
//        return retailerId;
//    }
//
//    public void setRetailerId(String retailerId) {
//        this.retailerId = retailerId;
//    }
//
//    public String getDealerId() {
//        return dealerId;
//    }
//
//    public void setDealerId(String dealerId) {
//        this.dealerId = dealerId;
//    }
//
//    public String getDealerName() {
//        return dealerName;
//    }
//
//    public void setDealerName(String dealerName) {
//        this.dealerName = dealerName;
//    }
//
//    public String getProductId() {
//        return productId;
//    }
//
//    public void setProductId(String productId) {
//        this.productId = productId;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getQty() {
//        return (String) qty;
//    }
//
//    public void setQty(Object qty) {
//        this.qty = qty;
//    }
//
//    public CharSequence getUnitName() {
//         return (CharSequence) unitName;
//    }
//
//    public void setUnitName(Object unitName) {
//        this.unitName = unitName;
//    }
//
//    public String getDeliveryDay() {
//        return deliveryDay;
//    }
//
//    public void setDeliveryDay(String deliveryDay) {
//        this.deliveryDay = deliveryDay;
//    }
//
//    public String getUnlistedMedicines() {
//        return unlistedMedicines;
//    }
//
//    public void setUnlistedMedicines(String unlistedMedicines) {
//        this.unlistedMedicines = unlistedMedicines;
//    }

}
