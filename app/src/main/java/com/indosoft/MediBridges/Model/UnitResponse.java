package com.indosoft.MediBridges.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnitResponse {
    @SerializedName("unit_id")
    @Expose
    private String unitId;
    @SerializedName("unit_name")
    @Expose
    private String unitName;

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
