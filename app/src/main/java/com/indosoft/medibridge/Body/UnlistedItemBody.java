package com.indosoft.medibridge.Body;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnlistedItemBody {
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_model")
    @Expose
    private String productModel;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

}
