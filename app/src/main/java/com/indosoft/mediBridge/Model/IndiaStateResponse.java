package com.indosoft.mediBridge.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IndiaStateResponse {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("state")
    @Expose
    private String state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
