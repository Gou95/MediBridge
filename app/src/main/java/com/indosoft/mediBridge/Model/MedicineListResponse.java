package com.indosoft.mediBridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MedicineListResponse {
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("company_id")
    @Expose
    private Object companyId;
    @SerializedName("supplier_name")
    @Expose
    private Object supplierName;
    @SerializedName("unit_id")
    @Expose
    private Object unitId;
    @SerializedName("unit_name")
    @Expose
    private Object unitName;

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

    public Object getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Object companyId) {
        this.companyId = companyId;
    }

    public Object getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(Object supplierName) {
        this.supplierName = supplierName;
    }

    public Object getUnitId() {
        return unitId;
    }

    public void setUnitId(Object unitId) {
        this.unitId = unitId;
    }

    public Object getUnitName() {
        return unitName;
    }

    public void setUnitName(Object unitName) {
        this.unitName = unitName;
    }

}
