package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterExpiryBody {

    @SerializedName("expiry_month")
    @Expose
    private String expiryMonth;
    @SerializedName("retailer_id")
    @Expose
    private String retailerId;
    @SerializedName("order_items_id")
    @Expose
    private String orderItemsId;
    @SerializedName("product_id")
    @Expose
    private String productId;

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getRetailerId() {
        return retailerId;
    }

    public void setRetailerId(String retailerId) {
        this.retailerId = retailerId;
    }

    public String getOrderItemsId() {
        return orderItemsId;
    }

    public void setOrderItemsId(String orderItemsId) {
        this.orderItemsId = orderItemsId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
