package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashMemoItemDetailsResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("unit_id")
    @Expose
    private Object unitId;
    @SerializedName("bill_no")
    @Expose
    private String billNo;
    @SerializedName("bill_date")
    @Expose
    private String billDate;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("doctor_name")
    @Expose
    private String doctorName;
    @SerializedName("bill_amount")
    @Expose
    private Object billAmount;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("batch_no")
    @Expose
    private String batchNo;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("qty")
    @Expose
    private String qty;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("amount")
    @Expose
    private String amount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public Object getUnitId() {
        return unitId;
    }

    public void setUnitId(Object unitId) {
        this.unitId = unitId;
    }
    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Object getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Object billAmount) {
        this.billAmount = billAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
