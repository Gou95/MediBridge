package com.indosoft.medibridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UrgentCartCountResponse {
    @SerializedName("medicounter")
    @Expose
    private String medicounter;

    public String getMedicounter() {
        return medicounter;
    }

    public void setMedicounter(String medicounter) {
        this.medicounter = medicounter;
    }

}
