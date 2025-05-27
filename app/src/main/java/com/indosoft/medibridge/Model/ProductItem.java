package com.indosoft.medibridge.Model;

public class ProductItem {
    String productName;
    String days;
    String expiryDate;
    boolean isPed;
    boolean isNotPed;

    public ProductItem(String productName, String days, String expiryDate) {
        this.productName = productName;
        this.days = days;
        this.expiryDate = expiryDate;
        this.isPed = false;
        this.isNotPed = false;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isPed() {
        return isPed;
    }

    public void setPed(boolean ped) {
        isPed = ped;
    }

    public boolean isNotPed() {
        return isNotPed;
    }

    public void setNotPed(boolean notPed) {
        isNotPed = notPed;
    }

}
